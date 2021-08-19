package zutil.net.acme;

import org.shredzone.acme4j.util.KeyPairUtils;

import java.io.*;
import java.security.KeyPair;

public class AcmeFileDataStore implements AcmeDataStore {

    private final File userKeyFile;
    private final File domainKeyFile;


    /**
     * Create a new file based datastore for storing ACME protocol needed data.
     *
     * @param folder    is the folder there the different files should be stored in.
     */
    public AcmeFileDataStore(File folder) {
        this.userKeyFile     = new File(folder, "user.key");
        this.domainKeyFile   = new File(folder, "domain.key");
    }


    public KeyPair loadUserKeyPair(){
        if (domainKeyFile.exists()) {
            try (FileReader fr = new FileReader(domainKeyFile)) {
                return KeyPairUtils.readKeyPair(fr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public void storeUserKeyPair(KeyPair keyPair){
        try (FileWriter fw = new FileWriter(userKeyFile)) {
            KeyPairUtils.writeKeyPair(keyPair, fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public KeyPair loadDomainKeyPair() {
        if (userKeyFile.exists()) {
            try (FileReader fr = new FileReader(userKeyFile)) {
                return KeyPairUtils.readKeyPair(fr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public void storeDomainKeyPair(KeyPair keyPair){
        try (FileWriter fw = new FileWriter(userKeyFile)) {
            KeyPairUtils.writeKeyPair(keyPair, fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
