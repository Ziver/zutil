/*******************************************************************************
 * Copyright (c) 2011 Ziver Koc
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package zutil.test;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import zutil.net.http.soap.SOAPHttpPage;
import zutil.net.ws.WSInterface;
import zutil.net.ws.WSInterface.*;
import zutil.net.ws.WSReturnObject;
import zutil.net.ws.WebServiceDef;
import zutil.parser.wsdl.WSDLWriterOld;
import zutil.parser.wsdl.WSDLWriter;


public class SOAPTest {
	//*******************************************************************************************
	//**************************** TEST *********************************************************

	public static void main(String[] args){
		new SOAPTest();
	}
	
	public SOAPTest(){
		WebServiceDef wsDef = new WebServiceDef( SOAPTestClass.class );
		SOAPHttpPage soap = new SOAPHttpPage( wsDef );
		
		WSDLWriterOld wsdl = new WSDLWriterOld( wsDef );
		wsdl.write(System.out);
		System.out.println( "****************** new *********************" );	
		WSDLWriter wsdl2 = new WSDLWriter( wsDef );
		wsdl2.write(System.out);
		
		// Response		
		try {	
			Document document = soap.genSOAPResponse(
					"<?xml version=\"1.0\"?>" +
					"<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
					"	<soap:Body xmlns:m=\"http://www.example.org/stock\">\n" +
					//"		<m:pubA>\n" +
					//"			<m:Ztring>IBM</m:Ztring>\n" +
					//"		</m:pubA>\n" +
					//"		<m:pubZ>\n" +
					//"			<m:olle>66</m:olle>\n" +
					//"		</m:pubZ>\n" +
					"		<m:pubB>\n" +
					"			<m:byte>IBM</m:byte>\n" +
					"		</m:pubB>\n" +
					"	</soap:Body>\n" +
			"</soap:Envelope>");
			System.out.println( "****************** RESPONSE *********************" );	

			OutputFormat format = OutputFormat.createPrettyPrint();
			XMLWriter writer = new XMLWriter( System.out, format );
			writer.write( document );

			System.out.println();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static class SOAPTestClass3 extends WSReturnObject{
		public String lol = "lol11";
		public String lol2 = "lol22";
	}

	public static class SOAPTestClass2 extends WSReturnObject{
		@WSValueName(value="lolz")
		public String lol = "lol1";
		@WSValueName("lolx")
		public String lol2 = "lol2";
		public byte[] b = new byte[]{0x12, 0x23};
		public SOAPTestClass3 l = new SOAPTestClass3();
	}

	public static class SOAPTestRetClass extends WSReturnObject{
		@WSValueName("retTest")
		public String lol = "lol1";
		public String lol2 = "lol2";
	}

	@WSNamespace("http://test.se:8080/")
	public static class SOAPTestClass implements WSInterface{
		public SOAPTestClass(){}
		
		@WSHeader()
		@WSDocumentation("hello")
		public void pubZ(
				@WSParamName(value="olle", optional=true) int lol,
				@WSParamName(value="olle2", optional=true) int lol2) throws Exception{ 
			//System.out.println("Param: "+lol);
			throw new Exception("Ziver is the fizle");
		}

		@WSReturnName("param")
		@WSParamDocumentation("null is the shizzle")
		public String[][] pubA (
				@WSParamName("Ztring") String lol) throws Exception{ 
			//System.out.println("ParamZ: "+lol); 
			return new String[][]{{"test","test2"},{"test3","test4"}};
		}

		@WSReturnName("zivarray")
		@WSParamDocumentation("null is the bla")
		public SOAPTestClass2[] pubX (
				@WSParamName("Ztring") String lol) throws Exception{ 
			return new SOAPTestClass2[]{new SOAPTestClass2(), new SOAPTestClass2()};
		}

		@WSReturnName("zivarray")
		@WSParamDocumentation("null is the kala")
		public SOAPTestRetClass pubB (
				@WSParamName("byte") String lol) throws Exception{ 
			SOAPTestRetClass tmp = new SOAPTestRetClass();
			tmp.lol = "test";
			tmp.lol2 = "test2";
			return tmp;
		}
		
		@WSDisabled()
		public void privaZ(){ }
		protected void protZ(){ }			
	}
}
