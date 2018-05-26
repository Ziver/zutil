package zutil.net.smtp;

import zutil.log.CompactLogFormatter;
import zutil.log.LogUtil;

import java.io.IOException;
import java.util.logging.Level;

/**
 * Sends a test email with SmtpClient class.
 *
 * @see <a href="https://nilhcem.github.io/FakeSMTP/">Fake SMTP Server</a>
 */
public class SmtpClientTest {

    public static void main(String[] args) throws IOException {
        LogUtil.setGlobalFormatter(new CompactLogFormatter());
        LogUtil.setGlobalLevel(Level.ALL);

        SmtpClient smtp = new SmtpClient();
        smtp.send("from@example.com",
                "to@example.com",
                "Test email",
                "Disregard this email");
        smtp.send("from2@example.com",
                "to2@example.com",
                "Test 2 email",
                "Disregard this email");
        smtp.close();
    }
}
