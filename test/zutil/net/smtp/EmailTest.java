package zutil.net.smtp;

import org.junit.Test;
import zutil.io.StringOutputStream;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;
import static zutil.net.smtp.SMTPClient.NEWLINE;

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
                        "Date: Wed, 22 Nov 2000 15:20:55 +0100" + NEWLINE +
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
                        "Date: Wed, 22 Nov 2000 15:20:55 +0100" + NEWLINE +
                        "Content-Type: text/html;" + NEWLINE +
                        "Subject: Title" + NEWLINE +
                        NEWLINE +
                        "<html>"+NEWLINE+"<body>"+NEWLINE+"<b>message</b>"+NEWLINE+"</body>"+NEWLINE+"</html>",
                getEmailString(email));
    }




    private String getEmailString(Email email) throws IOException {
        StringOutputStream buff = new StringOutputStream();
        OutputStreamWriter out = new OutputStreamWriter(buff);
        email.write(out);
        out.flush();
        return buff.toString();
    }

    private Date getDate(){
        GregorianCalendar date = new GregorianCalendar(2000,10,22, 15,20,55);
        return date.getTime();
    }
}