/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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

package zutil.net.smtp;

import org.junit.Test;
import zutil.io.StringOutputStream;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static zutil.net.smtp.SmtpClient.NEWLINE;

/**
 * Created by Ziver on 2017-01-19.
 */
public class EmailTest {

    @Test
    public void sanitizingFrom(){
        Email email = new Email();

        email.setFrom("aa\n@aa.aa"+NEWLINE);
        assertEquals("aa@aa.aa", email.getFromAddress());

        email.setFrom("aa\n@aa.aa"+NEWLINE, "aa\n bb"+NEWLINE);
        assertEquals("aa@aa.aa", email.getFromAddress());
        assertEquals("aa bb", email.getFromName());
    }

    @Test
    public void sanitizingReplyTo(){
        Email email = new Email();

        email.setReplyTo("aa\n@aa.aa"+NEWLINE);
        assertEquals("aa@aa.aa", email.getReplyToAddress());
    }

    @Test
    public void sanitizingTo(){
        Email email = new Email();

        email.setTo("aa\n@aa.aa"+NEWLINE);
        assertEquals("aa@aa.aa", email.getToAddress());

        email.setTo("aa\n@aa.aa"+NEWLINE, "aa\n bb"+NEWLINE);
        assertEquals("aa@aa.aa", email.getToAddress());
        assertEquals("aa bb", email.getToName());
    }

    @Test
    public void sanitizingSubject(){
        Email email = new Email();

        email.setSubject("aa\n aa aa"+NEWLINE);
        assertEquals("aa aa aa", email.getSubject());
    }

    @Test
    public void sanitizingMessage(){
        Email email = new Email();

        email.setMessage("aa\nbb"+ NEWLINE +"cc\n");
        assertEquals("aa"+ NEWLINE +"bb"+ NEWLINE +"cc"+ NEWLINE,
                email.getMessage());

        email.setMessage("aa"+ NEWLINE +"."+ NEWLINE +"bb");
        assertEquals("aa"+ NEWLINE +".."+ NEWLINE +"bb",
                email.getMessage());

        email.setMessage("aa\n.\nbb");
        assertEquals("aa"+ NEWLINE +".."+ NEWLINE +"bb",
                email.getMessage());
    }



    @Test
    public void simpleEmail() throws IOException {
        Email email = new Email();
        email.setFrom("test@example.com");
        email.setTo("to@example.com");
        email.setDate(getDate());
        email.setMessage("message");

        assertEquals(
                "From: test@example.com" + NEWLINE +
                        "To: to@example.com" + NEWLINE +
                        "Date: Sun, 22 Oct 2000 15:20:55 GMT" + NEWLINE +
                        "Content-Type: text/plain;" + NEWLINE +
                        "Subject: " + NEWLINE +
                        NEWLINE +
                        "message",
                getEmailString(email));
    }

    @Test
    public void fullEmail() throws IOException {
        Email email = new Email();
        email.setFrom("test@example.com", "Test Tester");
        email.setTo("to@example.com", "To Totter");
        email.setDate(getDate());
        email.setContentType(Email.ContentType.HTML);
        email.setReplyTo("mokey@example.org");
        email.setSubject("Title");
        email.setMessage("<html>\n<body>\n<b>message</b>\n</body>\n</html>");

        assertEquals(
                "From: Test Tester <test@example.com>" + NEWLINE +
                        "Reply-To: <mokey@example.org>" + NEWLINE +
                        "To: To Totter <to@example.com>" + NEWLINE +
                        "Date: Sun, 22 Oct 2000 15:20:55 GMT" + NEWLINE +
                        "Content-Type: text/html;" + NEWLINE +
                        "Subject: Title" + NEWLINE +
                        NEWLINE +
                        "<html>"+NEWLINE+"<body>"+NEWLINE+"<b>message</b>"+NEWLINE+"</body>"+NEWLINE+"</html>",
                getEmailString(email));
    }

    // ---------------------------------------------
    // Utility functions
    // ---------------------------------------------

    private String getEmailString(Email email) throws IOException {
        StringOutputStream buff = new StringOutputStream();
        OutputStreamWriter out = new OutputStreamWriter(buff);
        email.write(out);
        out.flush();
        return buff.toString();
    }

    private OffsetDateTime getDate(){
        OffsetDateTime dateTime = OffsetDateTime.of(2000,10,22, 15,20,55, 0, ZoneOffset.UTC);
        return dateTime;
    }
}