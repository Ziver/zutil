package zutil.test;

import javax.wsdl.WSDLException;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import zutil.network.http.soap.SOAPHttpPage;
import zutil.network.ws.WSInterface;
import zutil.network.ws.WSObject;
import zutil.network.ws.WSReturnValueList;


public class SOAPTest {
	//*******************************************************************************************
	//**************************** TEST *********************************************************

	public static void main(String[] args){
		new SOAPTest();
	}
	
	public SOAPTest(){
		try {
			SOAPHttpPage soap = new SOAPHttpPage("http://test.se:8080/", new SOAPTestClass());
			
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
		} catch (WSDLException e) {
			e.printStackTrace();
		}
	}
	
	public class SOAPTestClass3 implements WSObject{
		public String lol = "lol11";
		public String lol2 = "lol22";
	}

	public class SOAPTestClass2 implements WSObject{
		@WSFieldName(value="lolz", optional=true)
		public String lol = "lol1";
		@WSFieldName("lolx")
		public String lol2 = "lol2";
		public byte[] b = new byte[]{0x12, 0x23};
		public SOAPTestClass3 l = new SOAPTestClass3();
	}

	public class SOAPTestRetClass extends WSReturnValueList{
		@WSValueName("retTest")
		public String lol = "lol1";
		public String lol2 = "lol2";
	}

	public class SOAPTestClass implements WSInterface{
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
