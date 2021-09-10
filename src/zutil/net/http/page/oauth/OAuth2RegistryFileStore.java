package zutil.net.http.page.oauth;

import zutil.ObjectUtil;
import zutil.io.file.FileUtil;
import zutil.log.LogUtil;
import zutil.parser.json.JSONObjectInputStream;
import zutil.parser.json.JSONObjectOutputStream;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class OAuth2RegistryFileStore implements OAuth2RegistryStore {
    private static final Logger logger = LogUtil.getLogger();

    private final File file;
    private List<OAuth2ClientRegister> registerList = new ArrayList<>();


    public OAuth2RegistryFileStore(File file) {
        this.file = file;
    }


    @Override
    public synchronized List<OAuth2ClientRegister> getClientRegistries() {
        if (file.exists()) {
            try {
                String json = FileUtil.getContent(file);

                if (!ObjectUtil.isEmpty(json)) {
                    registerList = JSONObjectInputStream.parse(json);
                    return registerList;
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Was unable to read in OAuth2 registry from file: " + file, e);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public synchronized void storeClientRegister(OAuth2ClientRegister register) {
        registerList.remove(register);
        registerList.add(register);

        try {
            FileUtil.setContent(file, JSONObjectOutputStream.toString(registerList));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Was unable to write the OAuth2 registry into file: " + file, e);
        }
    }
}
