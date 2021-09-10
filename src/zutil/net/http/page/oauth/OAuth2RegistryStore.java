package zutil.net.http.page.oauth;

import zutil.Timer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


/**
 * A storage interface for the {@link OAuth2Registry} class. This interface
 * is mainly used to save any changed client states and to load client data.
 */
public interface OAuth2RegistryStore {

    /**
     * @return all previously stored client registries.
     */
    List<OAuth2ClientRegister> getClientRegistries();

    /**
     * A client registry has been updated and needs to be stored for later retrieval.
     *
     * @param register the register object that has changed and needs to be stored.
     */
    void storeClientRegister(OAuth2ClientRegister register);


    /**
     * This data class contains OAuth data related to a single client.
     */
    class OAuth2ClientRegister implements Serializable {
        public String clientId;
        public HashMap<String, Timer> authCodes = new HashMap<>();
        public HashMap<String, Timer> accessTokens = new HashMap<>();


        public OAuth2ClientRegister() {}
        public OAuth2ClientRegister(String clientId) {
            this.clientId = clientId;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            OAuth2ClientRegister that = (OAuth2ClientRegister) o;
            return Objects.equals(clientId, that.clientId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(clientId);
        }
    }
}
