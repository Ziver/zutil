package zutil.net.ws.rest;

import org.junit.Test;
import zutil.net.ws.WSInterface;

import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * Created by Ziver on 2016-09-27.
 */
public class RestHttpPageTest {

    public static class TestClass implements WSInterface{
        public String hello(){
            return "hello world";
        }
    }

    @Test
    public void noInput() throws Throwable {
        RestHttpPage rest = new RestHttpPage(new TestClass());

        HashMap<String,String> input = new HashMap<>();
        String output = rest.execute("hello", input);
        assertEquals("\"hello world\"", output);
    }




    public static class TestEchoClass implements WSInterface{
        public String echo(@WSParamName("input") String input){
            return "echo: "+input;
        }
    }

    @Test
    public void oneInput() throws Throwable {
        RestHttpPage rest = new RestHttpPage(new TestEchoClass());

        HashMap<String,String> input = new HashMap<>();
        input.put("input", "test input");
        String output = rest.execute("echo", input);
        assertEquals("\"echo: test input\"", output);
    }

}