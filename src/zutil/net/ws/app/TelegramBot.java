package zutil.net.ws.app;

import zutil.io.IOUtil;
import zutil.log.LogUtil;
import zutil.net.http.HttpClient;
import zutil.net.http.HttpHeader;
import zutil.net.http.HttpURL;
import zutil.net.ws.rest.RESTClient;
import zutil.parser.DataNode;
import zutil.parser.json.JSONParser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class connects to the Telegram API endpoints to send messages.
 *
 * @see <a href="https://core.telegram.org/bots/api">Telegram API Documentation</a>
 */
public class TelegramBot {
    private static final Logger logger = LogUtil.getLogger();
    private static String TELEGRAM_API_URL = "https://api.telegram.org";

    /** Token used to authenticate to the API **/
    private String token;

    /**
     *
     * @param token token used to authenticate to the API
     */
    public TelegramBot(String token) {
        this.token = token;
    }

    private String getBaseUrl() {
        return TELEGRAM_API_URL + "/bot" + token;
    }

    /**
     * Send a message to a user or group.
     *
     * @param chatId a chat ID number, this could be the ID of a user or a group.
     * @param msg    the message to send in the chat.
     */
    public void sendMessage(long chatId, String msg) {
        try {
            HttpURL url = new HttpURL(getBaseUrl() + "/sendMessage");
            url.setParameter("chat_id", String.valueOf(chatId));
            url.setParameter("text", msg);

            DataNode reponse = RESTClient.get(url);

        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "Failed to generate API URL.", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to send API request.", e);
        }
    }

    /**
     * Use this method to receive incoming updates using long polling (wiki). Returns an Array of Update objects.
     */
    public void getUpdates() {
        try {
            HttpURL url = new HttpURL(getBaseUrl() + "/getUpdates");

            DataNode reponse = RESTClient.get(url);

        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "Failed to generate API URL.", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to send API request.", e);
        }
    }
}
