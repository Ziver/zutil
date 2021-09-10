package zutil.net.http.page.oauth;

import org.junit.Test;
import zutil.Timer;
import zutil.io.file.FileUtil;
import zutil.net.http.page.oauth.OAuth2RegistryStore.OAuth2ClientRegister;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class OAuth2RegistryFileStoreTest {

    @Test
    public void getClientRegistries() throws IOException {
        File file = File.createTempFile("oauth2_test", ".json");
        file.deleteOnExit();
        FileUtil.setContent(file, "[{\"@class\": \"zutil.net.http.page.oauth.OAuth2RegistryStore$OAuth2ClientRegister\", \"clientId\": \"test-client-id\", \"authCodes\": {\"code-abc\": {\"period\": 100, \"@class\": \"zutil.Timer\", \"@object_id\": 2, \"timestamp\": 2000000000000}}, \"@object_id\": 1, \"accessTokens\": {\"token-abc\": {\"period\": 500, \"@class\": \"zutil.Timer\", \"@object_id\": 3, \"timestamp\": 2000000000000}}}]");

        OAuth2RegistryFileStore store = new OAuth2RegistryFileStore(file);
        List<OAuth2ClientRegister> list = store.getClientRegistries();

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("test-client-id", list.get(0).clientId);
        assertEquals(2000000000100l, list.get(0).authCodes.get("code-abc").getTimeoutTimeMillis());
        assertEquals(2000000000500l, list.get(0).accessTokens.get("token-abc").getTimeoutTimeMillis());

        file.delete();
    }

    @Test
    public void storeClientRegister() throws IOException {
        File file = File.createTempFile("oauth2_test", ".json");
        file.deleteOnExit();

        OAuth2ClientRegister obj = new OAuth2ClientRegister("test-client-id");
        obj.authCodes.put("code-abc", new Timer(100));
        obj.accessTokens.put("token-abc", new Timer(500));

        OAuth2RegistryFileStore store = new OAuth2RegistryFileStore(file);
        store.storeClientRegister(obj);

        assertEquals("[{\"@class\": \"zutil.net.http.page.oauth.OAuth2RegistryStore$OAuth2ClientRegister\", \"clientId\": \"test-client-id\", \"authCodes\": {\"code-abc\": {\"period\": 100, \"@class\": \"zutil.Timer\", \"@object_id\": 2, \"timestamp\": -1}}, \"@object_id\": 1, \"accessTokens\": {\"token-abc\": {\"period\": 500, \"@class\": \"zutil.Timer\", \"@object_id\": 3, \"timestamp\": -1}}}]",
                FileUtil.getContent(file));

        file.delete();
    }
}