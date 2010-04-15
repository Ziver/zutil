package zutil.network.http.soap;

import javax.wsdl.WSDLException;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;


public class SOAPTest {
	//*******************************************************************************************
	//**************************** TEST *********************************************************

	public static void main(String[] args){
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
}

class SOAPTestClass3 implements SOAPObject{
	public String lol = "lol11";
	public String lol2 = "lol22";
}

class SOAPTestClass2 implements SOAPObject{
	@SOAPFieldName(value="lolz", optional=true)
	public String lol = "lol1";
	@SOAPFieldName("lolx")
	public String lol2 = "lol2";
	public byte[] b = new byte[]{0x12, 0x23};
	public SOAPTestClass3 l = new SOAPTestClass3();
}

class SOAPTestRetClass implements SOAPReturnObjectList{
	@SOAPValueName("retTest")
	public String lol = "lol1";
	public String lol2 = "lol2";
}

class SOAPTestClass implements SOAPInterface{
	public SOAPTestClass(){}

	@SOAPHeader()
	@WSDLDocumentation("hello")
	public void pubZ(
			@SOAPParamName(value="olle", optional=true) int lol,
			@SOAPParamName(value="olle2", optional=true) int lol2) throws Exception{ 
		//System.out.println("Param: "+lol);
		throw new Exception("Ziver is the fizle");
	}

	@SOAPReturnName("param")
	@WSDLParamDocumentation("null is the shizzle")
	public String[][] pubA (
			@SOAPParamName("Ztring") String lol) throws Exception{ 
		//System.out.println("ParamZ: "+lol); 
		return new String[][]{{"test","test2"},{"test3","test4"}};
	}

	@SOAPReturnName("zivarray")
	@WSDLParamDocumentation("null is the shizzle")
	public SOAPTestClass2[] pubX (
			@SOAPParamName("Ztring") String lol) throws Exception{ 
		return new SOAPTestClass2[]{new SOAPTestClass2(), new SOAPTestClass2()};
	}

	@SOAPReturnName("zivarray")
	@WSDLParamDocumentation("null is the shizzle")
	public SOAPTestRetClass pubB (
			@SOAPParamName("byte") String lol) throws Exception{ 
		SOAPTestRetClass tmp = new SOAPTestRetClass();
		tmp.lol = "test";
		tmp.lol2 = "test2";
		return tmp;
	}
	
	@SOAPDisabled()
	public void privaZ(){ }
	protected void protZ(){ }			
}
