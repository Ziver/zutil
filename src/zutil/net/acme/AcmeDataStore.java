package zutil.net.acme;

import java.net.URL;
import java.security.KeyPair;

public interface AcmeDataStore {

        /**
         * Loads an accounts key pair.
         *
         * @return a KeyPair for the account, null if no KeyPair is found.
         */
        URL getAccountLocation();

        /**
         * Retrieve an account key pair.
         *
         * @return a KeyPair object for the account, null if no KeyPair is found.
         */
        KeyPair getAccountKeyPair();

        /**
         * Stores an accounts key pair for later usage.
         *
         * @param accountLocation       the URL to the account profile, this is used as an identifier to the ACME service.
         * @param accountKeyPair        the keys for the user account
         */
        void storeAccountKeyPair(URL accountLocation, KeyPair accountKeyPair);

        /**
         * Loads a domain key pair.
         *
         * @return a KeyPair object for the domains, null if no KeyPar was found.
         */
        KeyPair getDomainKeyPair();

        /**
         * Stores a domain key pair for later usage.
         *
         * @param keyPair   the keys for the domain
         */
        void storeDomainKeyPair(KeyPair keyPair);
    }