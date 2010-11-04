package zutil.test;

import static org.junit.Assert.*;

import org.junit.Test;

import zutil.Hasher;


public class HasherTest {
	
	@Test
	public void MD5Test(){
		assertEquals(Hasher.MD5("AAAABBBB"), 			"9da4fc50e09e5eeb8ae8149ef4f23792");
		assertEquals(Hasher.MD5("qwerty12345"), 		"85064efb60a9601805dcea56ec5402f7");
		assertEquals(Hasher.MD5("123456789"), 			"25f9e794323b453885f5181f1b624d0b");
		//assertEquals(Hasher.MD5(".,<>|!#¤%&/()=?"), 	"20d5cda029514fa49a8bbe854a539847");
		assertEquals(Hasher.MD5("Test45"), 				"fee43a4c9d88769e14ec6a1d8b80f2e7");
	}
	
	@Test
	public void SHA1Test(){
		assertEquals(Hasher.SHA1("AAAABBBB"), 			"7cd188ef3a9ea7fa0ee9c62c168709695460f5c0");
		assertEquals(Hasher.SHA1("qwerty12345"), 		"4e17a448e043206801b95de317e07c839770c8b8");
		assertEquals(Hasher.SHA1("123456789"), 			"f7c3bc1d808e04732adf679965ccc34ca7ae3441");
		//assertEquals(Hasher.SHA1(".,<>|!#¤%&/()=?"), 	"6b3de029cdb367bb365d5154a197294ee590a77a");
		assertEquals(Hasher.SHA1("Test45"), 			"9194c6e64a6801e24e63a924d5843a46428d2b3a");
	}
}
