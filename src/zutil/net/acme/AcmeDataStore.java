package zutil.net.acme;

import java.security.KeyPair;

public interface AcmeDataStore {

        /**
         * Loads a user key pair.
         *
         * @return a KeyPair for the user account, null if no KeyPar was found.
         */
        KeyPair loadUserKeyPair();

        /**
         * Stores a user key pair for later usage.
         *
         * @param keyPair   the keys for the user account
         */
        void storeUserKeyPair(KeyPair keyPair);

        /**
         * Loads a domain key pair.
         *
         * @return a KeyPair for the domains, null if no KeyPar was found.
         */
        KeyPair loadDomainKeyPair();

        /**
         * Stores a domain key pair for later usage.
         *
         * @param keyPair   the keys for the domain
         */
        void storeDomainKeyPair(KeyPair keyPair);
    }