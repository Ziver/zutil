package zutil.test;

import java.io.IOException;
import java.util.Map;

import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;
import zutil.net.http.HttpServer;



public class HTTPUploaderTest implements HttpPage{

	public static void main(String[] args) throws IOException{
		HttpServer server = new HttpServer("localhost", 80);
		server.setDefaultPage(new HTTPUploaderTest());
		server.run();
	}

	public void respond(HttpPrintStream out,
			Map<String, String> client_info,
			Map<String, Object> session, 
			Map<String, String> cookie,
			Map<String, String> request) {

		if(!session.containsKey("file1")){
			out.println("</html>" +
					"	<form enctype='multipart/form-data' method='post'>" +
					"		<p>Please specify a file, or a set of files:<br>" +
					"		<input type='file' name='datafile' size='40'>" +
					"		</p>" +
					"		<input type='submit' value='Send'>" +
					"	</form>" +
					"</html>");
		}	
	}

}
