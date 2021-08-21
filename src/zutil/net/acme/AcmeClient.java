package zutil.net.acme;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.shredzone.acme4j.*;
import org.shredzone.acme4j.challenge.Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.KeyPairUtils;
import zutil.StringUtil;
import zutil.log.LogUtil;
import zutil.net.http.HttpServer;

/**
 * A class implementing the ACME protocol (Automatic Certificate Management Environment) used by the LetsEncrypt service.
 *
 * Code based on the example from the acme4j project:
 * https://github.com/shred/acme4j/blob/master/acme4j-example/src/main/java/org/shredzone/acme4j/example/ClientTest.java
 */
public class AcmeClient {
    private static final Logger logger = LogUtil.getLogger();

    public static final String ACME_SERVER_LETSENCRYPT_PRODUCTION = "acme://letsencrypt.org";
    public static final String ACME_SERVER_LETSENCRYPT_STAGING    = "acme://letsencrypt.org/staging";

    private static final int KEY_SIZE = 2048; // RSA key size of generated key pairs


    private String acmeServerUrl;
    private AcmeDataStore dataStore;
    private AcmeChallengeFactory challengeFactory;

    private Account acmeAccount;
    private ArrayList<String> domains = new ArrayList<>();
    private Order order;
    private ArrayList<Challenge> challenges = new ArrayList<>();


    public AcmeClient(AcmeDataStore dataStore, HttpServer httpServer) throws AcmeException {
        this(dataStore, new AcmeHttpChallengeFactory(httpServer), ACME_SERVER_LETSENCRYPT_PRODUCTION);
    }
    public AcmeClient(AcmeDataStore dataStore, AcmeChallengeFactory challengeFactory) throws AcmeException {
        this(dataStore, challengeFactory, ACME_SERVER_LETSENCRYPT_PRODUCTION);
    }
    /**
     * Create a new instance of the ACME Client and authenticates the user account towards
     * the AMCE service, if no account exists then a new one will be created.
     */
    public AcmeClient(AcmeDataStore dataStore, AcmeChallengeFactory challengeFactory, String acmeServerUrl) throws AcmeException {
        Security.addProvider(new BouncyCastleProvider());

        this.dataStore = dataStore;
        this.challengeFactory = challengeFactory;
        this.acmeServerUrl = acmeServerUrl;

        // ------------------------------------------------
        // Start user authentication process
        // ------------------------------------------------

        Session session = new Session(acmeServerUrl);
        acmeAccount = getAccount(session); // Get the Account. If there is no account yet, create a new one.
    }


    /**
     * Add a domain that the certificate should be valid for. This method can only be called before
     * the {@link #prepareRequest()} method has been called.
     *
     * @param domains   the domains to add to the certificate
     */
    public void addDomain(String... domains) {
        Collections.addAll(this.domains, domains);
    }


    /**
     * This method will prepare the request for the ACME service. Any manual action must be taken after this
     * method has been called and before the {@link #requestCertificate()} method is called.
     *
     * @throws AcmeException
     */
    public void prepareRequest() throws AcmeException {
        order = acmeAccount.newOrder().domains(domains).create(); // Order the certificate

        // Perform all required authorizations
        for (Authorization auth : order.getAuthorizations()) {
            if (auth.getStatus() == Status.VALID)
                continue; // The authorization is already valid. No need to process a challenge.

            challenges.add(challengeFactory.createChallenge(auth));
        }
    }

    /**
     * Connects to the ACME service and requests a certificate to be generated.
     * <p>
     * Note that before this method is called the {@link #prepareRequest()} method must be called to prepare
     * the challenge requests. The reason for this is as some challenges require manual intervention between
     * the preparation and the actual request.
     *
     * @return a certificate for the given domains.
     */
    public X509Certificate requestCertificate() throws IOException, AcmeException {
        if (order == null)
            throw new IllegalStateException("prepareRequest() method has not been called before the request of certificate.");

        // Perform all required domain authorizations
        for (Challenge challenge : challenges) {
            execDomainChallenge(challenge);
        }

        // Load or create a key pair for the domains. This should not be same as the userKeyPair!
        KeyPair domainKeyPair = dataStore.getDomainKeyPair();

        if (domainKeyPair == null) {
            logger.fine("Creating new domain keys.");
            domainKeyPair = KeyPairUtils.createKeyPair(KEY_SIZE);
            dataStore.storeDomainKeyPair(domainKeyPair);
        }

        // Generate one "Certificate Signing Request" for all the domains, and sign it with the domain key pair.
        CSRBuilder csrBuilder = new CSRBuilder();
        csrBuilder.addDomains(domains);
        csrBuilder.sign(domainKeyPair);

        order.execute(csrBuilder.getEncoded()); // Order the certificate

        // Wait for the order to complete
        try {
            for (int attempts = 0; attempts < 10; attempts++) {
                // Did the order pass or fail?
                if (order.getStatus() == Status.VALID) {
                    break;
                } else if (order.getStatus() == Status.INVALID) {
                    throw new AcmeException("Certificate order has failed, reason: " + order.getError());
                }

                // Wait for a few seconds
                long sleep = 100L + 1000L * attempts;
                logger.fine("Challenge not yet completed, sleeping for: " + StringUtil.formatTimeToString(sleep));
                Thread.sleep(sleep);

                // Then update the status
                order.update();
            }
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Interrupted", ex);
        }

        // Get the certificate
        Certificate certificate = order.getCertificate();

        logger.info("The certificate for domains '" + StringUtil.join(",", domains) + "' has been successfully generated.");

        // Cleanup

        order = null;
        challenges.clear();

        return certificate.getCertificate();
    }


    /**
     * Finds your {@link Account} at the ACME server. It will be found by your user's
     * public key. If your key is not known to the server yet, a new account will be
     * created.
     *
     * @param session   {@link Session} to bind with
     * @return {@link Account}
     */
    private Account getAccount(Session session) throws AcmeException {
        // Load the account key pair

        URL accountLocation = dataStore.getAccountLocation();
        KeyPair accountKeyPair = dataStore.getAccountKeyPair();

        // Ask the user to accept the TOS, if server provides us with a link.

        URI tos = session.getMetadata().getTermsOfService();
        if (tos != null) {
            logger.info("By using this service you accept the Terms of Service: " + tos);
        }

        // Create a new account or login existing user

        if (accountLocation == null || accountKeyPair == null) {
            logger.fine("Creating new account keys.");
            accountKeyPair = KeyPairUtils.createKeyPair(KEY_SIZE);

            Account account = new AccountBuilder()
                    .agreeToTermsOfService()
                    .useKeyPair(accountKeyPair)
                    .create(session);

            logger.info("Successfully registered new account, URL: " + account.getLocation());
            dataStore.storeAccountKeyPair(account.getLocation(), accountKeyPair);
            return account;
        } else {
            logger.info("Logging in existing account: " + accountLocation);
            Login login = session.login(accountLocation, accountKeyPair);
            return login.getAccount();
        }
    }


    /**
     * Authorize a domain. It will be associated with your account, so you will be able to
     * retrieve a signed certificate for the domain later.
     *
     * @param   challenge    {@link Challenge} to be performed
     */
    private void execDomainChallenge(Challenge challenge) throws AcmeException {
        logger.info("Executing challenge: " + challenge);

        try {
            // If the challenge is already verified, there's no need to execute it again.
            if (challenge.getStatus() == Status.VALID)
                return;

            // Now trigger the challenge.
            challenge.trigger();

            // Poll for the challenge to complete.

            for (int attempts = 0; attempts < 30; attempts++) {
                // Did the authorization fail?
                if (challenge.getStatus() == Status.VALID) {
                    break;
                } else if (challenge.getStatus() == Status.INVALID) {
                    throw new AcmeException("Certificate challenge failed: " + challenge.getError());
                }

                // Wait for a few seconds
                long sleep = 100L + 1000L * attempts;
                logger.fine("Challenge not yet completed, sleeping for: " + StringUtil.formatTimeToString(sleep));
                Thread.sleep(sleep);

                // Then update the status
                challenge.update();
            }

            // All reattempts are used up and there is still no valid authorization?
            if (challenge.getStatus() != Status.VALID)
                throw new AcmeException("Failed to pass the challenge: " + challenge.getError());
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Interrupted", ex);
        } finally {
            // Cleanup
            challengeFactory.postChallengeAction(challenge);
        }

        logger.fine("Domain challenge executed successfully.");
    }
}
