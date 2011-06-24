package zutil.test;

import static org.junit.Assert.*;

import org.junit.Test;

import zutil.converters.Converter;

public class ConverterTest {

	@Test
	public void testHexToByte() {
		assertEquals( (byte)1, Converter.hexToByte('1') );
		assertEquals( (byte)5, Converter.hexToByte('5') );
		assertEquals( (byte)10, Converter.hexToByte('A') );
		assertEquals( (byte)10, Converter.hexToByte('a') );
	}
	
	@Test
	public void testHexToByte2() {
		assertEquals( 0x00, Converter.hexToByte('0','0') );
		assertEquals( 0x11, Converter.hexToByte('1','1') );
		assertEquals( 0x75, Converter.hexToByte('7','5') );
		assertEquals( 0xDA, Converter.hexToByte('D','A') );
		assertEquals( 0xFA, Converter.hexToByte('F','a') );
		assertEquals( 0xFF, Converter.hexToByte('f','f') );
	}
	
	@Test
	public void testUrlEncode() {
		assertEquals( "fas8dg7%20a0d1%2313f9g8d7%200h9a%a4%25h0", 
				Converter.urlEncode("fas8dg7 a0d1#13f9g8d7 0h9a¤%h0") );
		assertEquals( "9i34%e5%202y92%a452%25%2623%20463765%a4(%2f%26(", 
				Converter.urlEncode("9i34å 2y92¤52%&23 463765¤(/&(") );
		
	}
	
	@Test
	public void testUrlDecode() {
		assertEquals( "fas8dg7 a0d1#13f9g8d7 0h9a%h0", 
				Converter.urlDecode("fas8dg7%20a0d1%2313f9g8d7%200h9a%25h0") );
		assertEquals( "9i34å 2y9252%&23 463765(/&(", 
				Converter.urlDecode("9i34%e5%202y9252%25%2623%20463765(%2f%26(") );
	}

}
