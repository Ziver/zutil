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

package zutil.net.smtp;

import zutil.log.LogUtil;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple class that connects, authenticates and sends emails through a SMTP server
 *
 * @see <a href="http://cr.yp.to/smtp/client.html">SMTP Summary</a>
 * @see <a href="https://www.ietf.org/rfc/rfc2821.txt">RFC2821</a>
 * @author Ziver
 *
 */
public class SMTPClient {
	private static final Logger logger = LogUtil.getLogger();

	protected static final String NEWLINE   = "\r\n";
	private static final String CMD_HELO  = "HELO";
	private static final String CMD_FROM  = "MAIL FROM";
	private static final String CMD_TO    = "RCPT TO";
	private static final String CMD_DATA  = "DATA";
	private static final String CMD_DATA_END  = ".";
	private static final String CMD_RESET = "RSET";
	private static final String CMD_NOOP  = "NOOP";
	private static final String CMD_QUIT  = "QUIT";


    private Socket socket;
	private BufferedReader in;
	private Writer out;


    /**
     * Will look for a SMTP server on localhost on port 25
     */
    public SMTPClient() throws IOException {
        this("localhost", 25);
    }
    /**
     * Will look for a SMTP server on specified host on port 25
     */
	public SMTPClient(String host) throws IOException {
		this(host, 25);
	}
	public SMTPClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new OutputStreamWriter(socket.getOutputStream());

        readCommand();
        sendCommand(CMD_HELO + " " + InetAddress.getLocalHost().getHostName());
	}

	/**
	 * Sends a basic email to the smtp server
	 * 
	 * @param   from    the senders email address
	 * @param   to      the recipients email address
	 * @param   subj    the email subject line
	 * @param   msg     the email body message
	 */
	public synchronized void send(String from, String to, String subj, String msg) throws IOException{
        Email email = new Email();
        email.setFrom(from);
        email.setTo(to);
        email.setSubject(subj);
        email.setMessage(msg);
        send(email);
	}

    /**
     * Sends a email to the connected SMTP server
     *
     * @param   email   a email object containing message specific data
     */
    public synchronized void send(Email email) throws IOException{
        if(email.getFromAddress() == null)
            throw new IllegalArgumentException("From value cannot be null!");
        if(email.getToAddress() == null)
            throw new IllegalArgumentException("To value cannot be null!");
        try{
            // Pre metadata
            sendCommand(CMD_FROM + ":" + email.getFromAddress());
            sendCommand(CMD_TO + ":" + email.getToAddress());
            sendCommand(CMD_DATA);
            // Message headers and body
            email.write(out);
            out.write(NEWLINE);
            sendCommand(CMD_DATA_END);
            reset();
        }catch(IOException e){
            logger.log(Level.SEVERE, null,e );
        }
    }



	/**
	 * Sends the given line to the server and return the last line of the response
	 *
     * @param   cmd     a String command that will be sent to the server
	 * @return the server response code
	 */
	public synchronized int sendCommand(String cmd) throws IOException{
		logger.finest(">> "+cmd);
	    out.write(cmd + NEWLINE);
		String reply = readCommand();
		return parseReturnCode(reply);
	}

	/**
	 * Reads on line from the command channel
     *
	 * @throws IOException if the server returns a error code
	 */
	public synchronized String readCommand() throws IOException{
		String tmp = in.readLine();
		logger.finest(">> "+tmp);
		if(parseReturnCode(tmp) >= 400 )
			throw new IOException(tmp);

		return tmp;
	}

    private static int parseReturnCode(String msg){
        return Integer.parseInt(msg.substring(0, 3));
    }

    /**
     * Reset the server context, this needs to be done
     * between emails id multiple messages are sent
     */
    public synchronized void reset() throws IOException {
        sendCommand(CMD_RESET);
    }

	public synchronized void close() throws IOException{
        if (in != null) {
            sendCommand(CMD_QUIT);
            socket.close();
            socket = null;
            in = null;
            out = null;
        }
	}
}
