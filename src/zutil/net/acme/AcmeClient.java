package zutil.net.acme;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.shredzone.acme4j.Account;
import org.shredzone.acme4j.AccountBuilder;
import org.shredzone.acme4j.Authorization;
import org.shredzone.acme4j.Certificate;
import org.shredzone.acme4j.Order;
import org.shredzone.acme4j.Session;
import org.shredzone.acme4j.Status;
import org.shredzone.acme4j.challenge.Http01Challenge;
import org.shredzone.acme4j.exception.AcmeException;
import org.shredzone.acme4j.util.CSRBuilder;
import org.shredzone.acme4j.util.KeyPairUtils;
import zutil.StringUtil;
import zutil.log.LogUtil;
import zutil.net.http.HttpServer;
import zutil.net.http.page.HttpStaticContentPage;

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

    /**
     * Create a new instance of the ACME Client
     */
    public AcmeClient(AcmeDataStore dataStore, String acmeServerUrl) {
        Security.addProvider(new BouncyCastleProvider());

        this.dataStore = dataStore;
        this.acmeServerUrl = acmeServerUrl;
    }

    public AcmeClient(AcmeDataStore dataStore) {
        this(dataStore, ACME_SERVER_LETSENCRYPT_PRODUCTION);
    }


    /**
     * Generates a certificate for the given domains. Also takes care for the registration
     * process.
     *
     * @param httpServer    the web server where the challenge and response will be performed on and where the certificate will be applied to.
     * @param domains       the domains to get a certificates for
     * @return a certificate for the given domains.
     */
    public X509Certificate fetchCertificate(HttpServer httpServer, String... domains) throws IOException, AcmeException {
        // ------------------------------------------------
        // Read in keys
        // ------------------------------------------------

        KeyPair userKeyPair = dataStore.loadUserKeyPair();     // Load the user key file. If there is no key file, create a new one.
        KeyPair domainKeyPair = dataStore.loadDomainKeyPair(); // Load or create a key pair for the domains. This should not be the userKeyPair!

        if (userKeyPair == null) {
            userKeyPair = KeyPairUtils.createKeyPair(KEY_SIZE);
            dataStore.storeUserKeyPair(userKeyPair);
        }
        if (domainKeyPair == null) {
            domainKeyPair = KeyPairUtils.createKeyPair(KEY_SIZE);
            dataStore.storeDomainKeyPair(domainKeyPair);
        }

        // ------------------------------------------------
        // Start authorization process
        // ------------------------------------------------

        Session session = new Session(acmeServerUrl);
        Account acct = getAccount(session, userKeyPair); // Get the Account. If there is no account yet, create a new one.
        Order order = acct.newOrder().domains(domains).create();    // Order the certificate

        // Perform all required authorizations
        for (Authorization auth : order.getAuthorizations()) {
            execHttpChallenge(auth, httpServer);
        }

        // Generate a "Certificate Signing Request" for all of the domains, and sign it with the domain key pair.
        CSRBuilder csrBuilder = new CSRBuilder();
        csrBuilder.addDomains(domains);
        csrBuilder.sign(domainKeyPair);

        order.execute(csrBuilder.getEncoded()); // Order the certificate

        // Wait for the order to complete
        try {
            for (int attempts = 0; attempts < 10; attempts--) {
                // Did the order pass or fail?
                if (order.getStatus() == Status.VALID) {
                    break;
                } else if (order.getStatus() == Status.INVALID) {
                    throw new AcmeException("Certificate order has failed, reason: " + order.getError());
                }

                // Wait for a few seconds
                Thread.sleep(100L + 500L * attempts);

                // Then update the status
                order.update();
            }
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Interrupted", ex);
        }

        // Get the certificate
        Certificate certificate = order.getCertificate();

        logger.info("The certificate for domains '" + StringUtil.join(",", domains) + "' has been successfully generated.");

        return certificate.getCertificate();
    }


    /**
     * Finds your {@link Account} at the ACME server. It will be found by your user's
     * public key. If your key is not known to the server yet, a new account will be
     * created.
     * <p>
     * This is a simple way of finding your {@link Account}. A better way is to get the
     * URL of your new account with {@link Account#getLocation()} and store it somewhere.
     * If you need to get access to your account later, reconnect to it via {@link
     * Session#login(URL, KeyPair)} by using the stored location.
     *
     * @param session   {@link Session} to bind with
     * @return {@link Account}
     */
    private Account getAccount(Session session, KeyPair accountKey) throws AcmeException {
        // Ask the user to accept the TOS, if server provides us with a link.
        URI tos = session.getMetadata().getTermsOfService();
        if (tos != null) {
            logger.info("By using this service you accept the Terms of Service: " + tos);
        }

        Account account = new AccountBuilder()
                .agreeToTermsOfService()
                .useKeyPair(accountKey)
                .create(session);
        logger.info("Registered a new user, URL: " + account.getLocation());

        return account;
    }

    /**
     * Authorize a domain. It will be associated with your account, so you will be able to
     * retrieve a signed certificate for the domain later.
     *
     * @param   auth    {@link Authorization} to perform
     */
    private void execHttpChallenge(Authorization auth, HttpServer httpServer) throws AcmeException {
        logger.info("Authorization for domain: " + auth.getIdentifier().getDomain());

        // The authorization is already valid. No need to process a challenge.
        if (auth.getStatus() == Status.VALID) {
            return;
        }

        // Find the desired challenge and prepare it.
        Http01Challenge challenge = auth.findChallenge(Http01Challenge.class);
        if (challenge == null) {
            throw new AcmeException("Found no " + Http01Challenge.TYPE + " challenge.");
        }

        // If the challenge is already verified, there's no need to execute it again.
        if (challenge.getStatus() == Status.VALID)
            return;

        String url = "http://" + auth.getIdentifier().getDomain();
        String path = "/.well-known/acme-challenge/" + challenge.getToken();
        String content = challenge.getAuthorization();

        // Output the challenge, wait for acknowledge...
        logger.fine("Adding challenge HttpPage at: " + url + path);
        httpServer.setPage(path, new HttpStaticContentPage(content));

        // Now trigger the challenge.
        challenge.trigger();

        // Poll for the challenge to complete.
        try {
            for (int attempts = 0; attempts < 10; attempts--) {
                // Did the authorization fail?
                if (challenge.getStatus() == Status.VALID) {
                    break;
                } else if (challenge.getStatus() == Status.INVALID) {
                    throw new AcmeException("Certificate challenge failed: " + challenge.getError());
                }

                // Wait for a few seconds
                Thread.sleep(100L + 500L * attempts);

                // Then update the status
                challenge.update();
            }

            // All reattempts are used up and there is still no valid authorization?
            if (challenge.getStatus() != Status.VALID)
                throw new AcmeException("Failed to pass the challenge for domain " + auth.getIdentifier().getDomain());
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Interrupted", ex);
        } finally {
            // Cleanup
            httpServer.removePage(path);
        }
    }
}
