package zutil.net.ws.rest;

import zutil.io.IOUtil;
import zutil.log.LogUtil;
import zutil.net.http.HttpClient;
import zutil.net.http.HttpHeader;
import zutil.net.http.HttpURL;
import zutil.net.ws.WSInterface.RequestType;
import zutil.parser.DataNode;
import zutil.parser.json.JSONParser;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * A class for making basic REST request to a remote service.
 */
public class RESTClient {
    private static Logger logger = LogUtil.getLogger();

    /**
     * See {@link #request(RequestType, HttpURL)} for details.
     */
    public static DataNode get(HttpURL url) throws IOException {
        return request(RequestType.GET, url);
    }
    /**
     * See {@link #request(RequestType, HttpURL)} for details.
     */
    public static DataNode post(HttpURL url) throws IOException {
        return request(RequestType.POST, url);
    }
    /**
     * See {@link #request(RequestType, HttpURL)} for details.
     */
    public static DataNode put(HttpURL url) throws IOException {
        return request(RequestType.POST, url);
    }
    /**
     * See {@link #request(RequestType, HttpURL)} for details.
     */
    public static DataNode patch(HttpURL url) throws IOException {
        return request(RequestType.PATCH, url);
    }
    /**
     * See {@link #request(RequestType, HttpURL)} for details.
     */
    public static DataNode delete(HttpURL url) throws IOException {
        return request(RequestType.DELETE, url);
    }

    /**
     * This method will do a REST request to the specified remote endpoint.
     *
     * @param type is the HTTP type of the request.
     * @param url  is the URL to the endpoint including URL parameters.
     * @return a DataNode object parsed from the JSON response from the REST endpoint.
     */
    public static DataNode request(RequestType type, HttpURL url) throws IOException {
        HttpClient request = new HttpClient(String.valueOf(type));
        request.setURL(url);

        logger.fine("Sending request for: " + url);
        HttpHeader responseHeader = request.send();
        logger.fine("Received response code: " + responseHeader.getResponseStatusCode());

        String responseStr = IOUtil.readContentAsString(request.getResponseInputStream());
        request.close();
        logger.finest("Received response body:");
        logger.finest(responseStr);

        DataNode json = JSONParser.read(responseStr);
        return json;
    }
}
