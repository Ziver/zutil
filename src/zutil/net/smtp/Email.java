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

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import static zutil.net.smtp.SmtpClient.NEWLINE;


/**
 * Simplifies sending of a email
 *
 * @author Ziver
 */
public class Email {

    public enum ContentType{
        PLAIN, HTML
    }
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    private static final Pattern PATTERN_NEWLINE = Pattern.compile("(\\r\\n|\\n)");

    private String fromAddress;
    private String fromName = null;
    private String toAddress;
    private String toName = null;
    private String replyToAddress = null;
    private String dateStr = dateFormatter.format(new Date(System.currentTimeMillis()));
    private ContentType type = ContentType.PLAIN;
    private String subject;
    private String message;



    public Email() {}


    public void setFrom(String address) {
        this.fromAddress = sanitizeParam(address);
    }
    public void setFrom(String address, String niceName) {
        fromAddress = sanitizeParam(address);
        fromName = sanitizeParam(niceName);
    }
    public String getFromAddress() {
        return fromAddress;
    }
    public String getFromName() {
        return fromName;
    }

    public void setReplyTo(String address) {
        this.replyToAddress = sanitizeParam(address);
    }
    public String getReplyToAddress() {
        return replyToAddress;
    }

    public void setTo(String address) {
        this.toAddress = sanitizeParam(address);
    }
    public void setTo(String address, String niceName) {
        this.toAddress = sanitizeParam(address);
        this.toName = sanitizeParam(niceName);
    }
    public String getToAddress() {
        return toAddress;
    }
    public String getToName() {
        return toName;
    }

    public void setDate(Date date) {
        this.dateStr = dateFormatter.format(date);
    }
    public void setDate(OffsetDateTime date) {
        this.dateStr = date.format(DateTimeFormatter.RFC_1123_DATE_TIME);;
    }

    public void setContentType(ContentType t) {
        type = t;
    }

    public void setSubject(String subject) {
        this.subject = sanitizeParam(subject);
    }
    public String getSubject() {
        return subject;
    }

    public void setMessage(String msg) {
        message = msg.replaceAll("(\\r\\n|\\n)", NEWLINE);
        message = message.replaceAll(NEWLINE + "\\.", NEWLINE + "..");
    }
    public String getMessage() {
        return message;
    }


    private String sanitizeParam(String param) {
        return PATTERN_NEWLINE.matcher(param).replaceAll("");
    }


    /**
     * Will write the data from this object into a PrintStream.
     *
     * @throws IllegalArgumentException if from address and to address has not been set
     */
    public void write(Writer out) throws IOException{
        if (fromAddress == null)
            throw new IllegalArgumentException("From value cannot be null!");
        if (toAddress == null)
            throw new IllegalArgumentException("To value cannot be null!");

        //************ Headers

        // From
        if (fromName !=null)
            out.write("From: " + fromName + " <" + fromAddress + ">" + NEWLINE);
        else
            out.write("From: " + fromAddress + NEWLINE);

        // Reply-To
        if (replyToAddress != null)
            out.write("Reply-To: <" + replyToAddress + ">" + NEWLINE);

        // To
        if (toName !=null)
            out.write("To: " + toName + " <" + toAddress + ">" + NEWLINE);
        else
            out.write("To: " + toAddress + NEWLINE);

        // Date
        out.write("Date: " + dateStr + NEWLINE);

        // Content type
        switch(type) {
        case HTML:
            out.write("Content-Type: text/html;" + NEWLINE); break;
        default:
            out.write("Content-Type: text/plain;" + NEWLINE); break;
        }

        // Subject
        out.write("Subject: " + (subject!=null ? subject : "") + NEWLINE);

        out.write(NEWLINE);

        //*********** Mesasge

        out.write(message);
    }
}