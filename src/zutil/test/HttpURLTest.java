package zutil.test;

import static org.junit.Assert.*;

import org.junit.Test;

import zutil.net.http.HttpURL;

public class HttpURLTest {

	@Test
	public void fullURLTest() {
		HttpURL url = new HttpURL();
		url.setProtocol("http");
		assertEquals( "http://127.0.0.1/", url.getURL() );
		
		url.setHost("koc.se");
		assertEquals( "http://koc.se/", url.getURL() );
		
		url.setPort( 80 );
		assertEquals( "http://koc.se:80/", url.getURL() );
		
		url.setPath("test/index.html");
		assertEquals( "http://koc.se:80/test/index.html", url.getURL() );
		
		url.setParameter("key", "value");
		assertEquals( "http://koc.se:80/test/index.html?key=value", url.getURL() );
		
		url.setAnchor( "anch" );
		assertEquals( "http://koc.se:80/test/index.html?key=value#anch", url.getURL() );
	}
	
	@Test
	public void urlParameterTest() {
		HttpURL url = new HttpURL();
		url.setParameter("key1", "value1");
		assertEquals( "key1=value1", url.getParameterString() );
		
		url.setParameter("key1", "value1");
		assertEquals( "key1=value1", url.getParameterString() );
		
		url.setParameter("key2", "value2");
		assertEquals( "key2=value2&key1=value1", url.getParameterString() );

	}


}
