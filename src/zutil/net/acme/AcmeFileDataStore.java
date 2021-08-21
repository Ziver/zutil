package zutil.net.acme;

import org.shredzone.acme4j.toolbox.AcmeUtils;
import org.shredzone.acme4j.util.KeyPairUtils;
import zutil.io.file.FileUtil;

import java.io.*;
import java.net.URL;
import java.security.KeyPair;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class AcmeFileDataStore implements AcmeDataStore {

    private final File accountLocationFile;
    private final File accountKeyFile;
    private final File domainKeyFile;
    private final File certificateFile;


    /**
     * Create a new file based datastore for storing ACME protocol needed data.
     *
     * @param folder    is the folder there the different files should be stored in.
     */
    public AcmeFileDataStore(File folder) {
        this.accountLocationFile = new File(folder, "accountLocation.cfg");
        this.accountKeyFile = new File(folder, "account.key");
        this.domainKeyFile   = new File(folder, "domain.key");
        this.certificateFile   = new File(folder, "certificate.pem");
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


    @Override
    public X509Certificate getCertificate() {
        if (certificateFile.exists()) {
            try (FileInputStream in = new FileInputStream(certificateFile)) {
                CertificateFactory factory = CertificateFactory.getInstance("X.509");
                return (X509Certificate) factory.generateCertificate(in);
            } catch (IOException | CertificateException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void storeCertificate(X509Certificate certificate) {
        try(FileWriter out = new FileWriter(certificateFile)) {
            AcmeUtils.writeToPem(certificate.getEncoded(), AcmeUtils.PemLabel.CERTIFICATE, out);
        } catch (IOException | CertificateEncodingException e) {
            e.printStackTrace();
        }
    }
}
