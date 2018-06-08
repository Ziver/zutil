package zutil.net.ws.rest;

import org.junit.Test;
import zutil.net.ws.WSInterface;
import zutil.net.ws.WebServiceDef;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Ziver on 2016-09-27.
 */
public class RESTClientTest {

    public interface OpenWeartherMap extends WSInterface {

        int weather(@WSParamName("q") String city);
    }


    //@Test
    public void testREST() throws MalformedURLException {
        OpenWeartherMap restObj = RESTClientFactory.createClient(
                new URL("http://samples.openweathermap.org/data/2.5/"),
                OpenWeartherMap.class);

        assertNotNull(restObj.weather("London,uk"));
    }

}