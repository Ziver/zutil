package zutil.net.acme;

import org.shredzone.acme4j.util.KeyPairUtils;
import zutil.io.file.FileUtil;

import java.io.*;
import java.net.URL;
import java.security.KeyPair;

public class AcmeFileDataStore implements AcmeDataStore {

    private final File accountLocationFile;
    private final File accountKeyFile;
    private final File domainKeyFile;


    /**
     * Create a new file based datastore for storing ACME protocol needed data.
     *
     * @param folder    is the folder there the different files should be stored in.
     */
    public AcmeFileDataStore(File folder) {
        this.accountLocationFile = new File(folder, "accountLocation.cfg");
        this.accountKeyFile = new File(folder, "account.key");
        this.domainKeyFile   = new File(folder, "domain.key");
    }


    @Override
    public URL getAccountLocation() {
        if (accountKeyFile.exists()) {
            try {
                return new URL(FileUtil.getContent(accountKeyFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public KeyPair getAccountKeyPair(){
        if (domainKeyFile.exists()) {
            try (FileReader fr = new FileReader(domainKeyFile)) {
                return KeyPairUtils.readKeyPair(fr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void storeAccountKeyPair(URL accountLocation, KeyPair accounKeyPair){
        try (FileWriter fw = new FileWriter(accountKeyFile)) {
            FileUtil.setContent(accountLocationFile, accountLocation.toString());
            KeyPairUtils.writeKeyPair(accounKeyPair, fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public KeyPair getDomainKeyPair() {
        if (accountKeyFile.exists()) {
            try (FileReader fr = new FileReader(accountKeyFile)) {
                return KeyPairUtils.readKeyPair(fr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public void storeDomainKeyPair(KeyPair keyPair){
        try (FileWriter fw = new FileWriter(accountKeyFile)) {
            KeyPairUtils.writeKeyPair(keyPair, fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
