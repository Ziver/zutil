/*
 * Copyright (c) 2015 ezivkoc
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
 */

package zutil.test;

import java.io.IOException;
import java.util.Map;

import zutil.net.http.HttpHeaderParser;
import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;
import zutil.net.http.HttpServer;


public class HTTPGuessTheNumber implements HttpPage{

	public static void main(String[] args) throws IOException{
		//HttpServer server = new HttpServer("localhost", 443, FileFinder.find("keySSL"), "rootroot");//SSL
		HttpServer server = new HttpServer(8080);
		server.setDefaultPage(new HTTPGuessTheNumber());
		server.run();
	}

	public void respond(HttpPrintStream out,
			HttpHeaderParser client_info,
			Map<String, Object> session, 
			Map<String, String> cookie,
			Map<String, String> request) throws IOException {

		out.enableBuffering(true);
		out.println("<html>");
		out.println("<H2>Welcome To The Number Guess Game!</H2>");

		if(session.containsKey("random_nummber") && request.containsKey("guess") && !request.get("guess").isEmpty()){
			int guess = Integer.parseInt(request.get("guess"));
			int nummber = (Integer)session.get("random_nummber");
			try {
				if(guess == nummber){
					session.remove("random_nummber");
					out.println("You Guessed Right! Congrats!");
					out.println("</html>");
					return;
				}
				else if(guess > nummber){
					out.println("<b>To High</b><br>");
					if(Integer.parseInt(cookie.get("high")) > guess){
						out.setCookie("high", ""+guess);
						cookie.put("high", ""+guess);
					}
				}
				else{
					out.println("<b>To Low</b><br>");
					if(Integer.parseInt(cookie.get("low")) < guess){
						out.setCookie("low", ""+guess);
						cookie.put("low", ""+guess);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else{
			session.put("random_nummber", (int)(Math.random()*99+1));
			try {
				out.setCookie("low", "0");
				out.setCookie("high", "100");
				cookie.put("low", "0");
				cookie.put("high", "100");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		out.println("<form method='post'>");
		out.println(cookie.get("low")+" < X < "+cookie.get("high")+"<br>");
		out.println("Guess a number between 0 and 100:<br>");
		out.println("<input type='text' name='guess'>");
		out.println("<input type='hidden' name='test' value='test'>");
		out.println("<input type='submit' value='Guess'>");
		out.println("</form>");
		out.println("<script>document.all.guess.focus();</script>");
		out.println("<b>DEBUG: nummber="+session.get("random_nummber")+"</b><br>");
		out.println("</html>");
	}

}
