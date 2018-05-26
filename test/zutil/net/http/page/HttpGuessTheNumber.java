/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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

package zutil.net.http.page;

import zutil.log.CompactLogFormatter;
import zutil.log.LogUtil;
import zutil.net.http.HttpHeader;
import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;
import zutil.net.http.HttpServer;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import static zutil.net.http.HttpServer.SESSION_KEY_ID;


public class HttpGuessTheNumber implements HttpPage {

    private static final String SESSION_KEY_NUMBER = "random_number";
    private static final String REQUEST_KEY_GUESS  = "guess";
    private static final String COOKIE_KEY_LOW     = "low";
    private static final String COOKIE_KEY_HIGH    = "high";


    public static void main(String[] args) {
        LogUtil.setGlobalLevel(Level.ALL);
        LogUtil.setGlobalFormatter(new CompactLogFormatter());

        //HttpServer server = new HttpServer("localhost", 443, FileFinder.find("keySSL"), "rootroot");//SSL
        HttpServer server = new HttpServer(8080);
        server.setDefaultPage(new HttpGuessTheNumber());
        server.run();
    }

    public void respond(HttpPrintStream out,
            HttpHeader client_info,
            Map<String, Object> session,
            Map<String, String> cookie,
            Map<String, String> request) throws IOException {

        out.enableBuffering(true);
        out.println("<html>");
        out.println("<H2>Welcome To The Number Guess Game!</H2>");

        String low = cookie.get(COOKIE_KEY_LOW);
        String high = cookie.get(COOKIE_KEY_HIGH);

        if(session.containsKey(SESSION_KEY_NUMBER)){
            if (request.containsKey(REQUEST_KEY_GUESS)) {
                int guess = Integer.parseInt(request.get(REQUEST_KEY_GUESS));
                int number = (Integer) session.get(SESSION_KEY_NUMBER);

                if (guess == number) {
                    session.remove(SESSION_KEY_NUMBER);
                    out.println("You Guessed Right! Congrats!");
                    out.println("</html>");
                    return;
                } else if (guess > number) {
                    out.println("<b>To High</b><br>");
                    if (Integer.parseInt(high) > guess) {
                        high = String.valueOf(guess);
                        out.setCookie(COOKIE_KEY_HIGH, high);
                    }
                } else {
                    out.println("<b>To Low</b><br>");
                    if (Integer.parseInt(low) < guess) {
                        low = String.valueOf(guess);
                        out.setCookie(COOKIE_KEY_LOW, low);
                    }
                }
            }
        }
        else{
            session.put(SESSION_KEY_NUMBER, (int)(Math.random()*99+1));
            low = "0";
            high = "100";
            out.setCookie(COOKIE_KEY_LOW, low);
            out.setCookie(COOKIE_KEY_HIGH, high);
        }

        out.println("<form method='post'>");
        out.println(low+" < X < "+high+"<br>");
        out.println("Guess a number between 0 and 100:<br>");
        out.println("<input type='text' name='guess'>");
        out.println("<input type='hidden' name='test' value='test'>");
        out.println("<input type='submit' value='Guess'>");
        out.println("</form>");
        out.println("<script>document.all.guess.focus();</script>");
        out.println("<b>DEBUG: session_id="+session.get(SESSION_KEY_ID)+"</b><br>");
        out.println("<b>DEBUG: number="+session.get(SESSION_KEY_NUMBER)+"</b><br>");
        out.println("</html>");
    }

}
