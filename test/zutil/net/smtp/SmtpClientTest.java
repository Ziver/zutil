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
