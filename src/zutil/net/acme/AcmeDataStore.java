package zutil.net.acme;

import java.net.URL;
import java.security.KeyPair;
import java.security.cert.X509Certificate;

public interface AcmeDataStore {

    /**
     * Read in an account location URL.
     *
     * @return a URL to the account, this is used as an identifier.
     */
    URL getAccountLocation();

    /**
     * Retrieve an account key pair.
     *
     * @return a KeyPair object for the account, null if no KeyPair is found.
     */
    KeyPair getAccountKeyPair();

    /**
     * Store an accounts key pair for later usage.
     *
     * @param accountLocation   the URL to the account profile, this is used as an identifier to the ACME service.
     * @param accountKeyPair    the keys for the user account
     */
    void storeAccountKeyPair(URL accountLocation, KeyPair accountKeyPair);


    /**
     * Read in a domain key pair.
     *
     * @return a KeyPair object for the domains, null if no KeyPar was found.
     */
    KeyPair getDomainKeyPair();

    /**
     * Store a domain key pair for later usage.
     *
     * @param keyPair   the keys for the domain
     */
    void storeDomainKeyPair(KeyPair keyPair);


    /**
     * Loads a certificate
     *
     * @return a certificate that has previously been generated, null if no certificate is available.
     */
    X509Certificate getCertificate();

    /**
     * Store a domain key pair for later usage.
     *
     * @param certificate   the certificate to be stored
     */
    void storeCertificate(X509Certificate certificate);
}