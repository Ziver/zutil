package zutil.test;

import java.io.IOException;
import java.util.HashMap;

import zutil.network.http.HttpPage;
import zutil.network.http.HttpPrintStream;
import zutil.network.http.HttpServer;



public class HTTPUploaderTest implements HttpPage{

	public static void main(String[] args) throws IOException{
		HttpServer server = new HttpServer("localhost", 80);
		server.setDefaultPage(new HTTPUploaderTest());
		server.run();
	}

	public void respond(HttpPrintStream out,
			HashMap<String, String> client_info,
			HashMap<String, Object> session, HashMap<String, String> cookie,
			HashMap<String, String> request) {

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
