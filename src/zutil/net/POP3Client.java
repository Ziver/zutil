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

package zutil.net;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * A simple class that connects and logs in to a POP3
 * server and then can read and delete messages.
 * INFO: http://pages.prodigy.net/michael_santovec/pop3telnet.htm
 * 
 * @author Ziver
 *
 */
public class POP3Client {
	public static boolean DEBUG = false;
	public static final int POP3_PORT = 110;
	public static final int POP3_SSL_PORT = 995;

	private BufferedReader in;
	private PrintStream out;
	private Socket socket;

	/**
	 * Connect to a POP3 server without username
	 * 
	 * @param host The hostname of the server
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public POP3Client(String host) throws UnknownHostException, IOException{
		this(host, POP3_PORT, null, null, false);
	}
	
	/**
	 * Connect to a POP3 server with username and password
	 * 
	 * @param host The hostname of the server
	 * @param user The username
	 * @param password the password
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public POP3Client(String host, String user, String password) throws UnknownHostException, IOException{
		this(host, POP3_PORT, user, password, false);
	}
	
	/**
	 * Connects to a POP3 server with username and password and SSL
	 * 
	 * @param host The hostname of the server
	 * @param user The username
	 * @param password the password
	 * @param ssl If SSL should be used
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public POP3Client(String host, String user, String password, boolean ssl) throws UnknownHostException, IOException{
		this(host, (ssl ? POP3_SSL_PORT : POP3_PORT), user, password, ssl);		
	}

	/**
	 * Connects to a POP3 server with username and password and 
	 * SSL and with costume port.
	 * 
	 * @param host The hostname of the server
	 * @param port The port number to connect to on the server
	 * @param user The username
	 * @param password the password
	 * @param ssl If SSL should be used
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public POP3Client(String host, int port, String user, String password, boolean ssl) throws UnknownHostException, IOException{
		if(ssl) connectSSL(host, port);
		else connect(host, port);
		
		if(user != null){
			login(user, password);
		}
	}
	
	/**
	 * Connects to the server
	 * 
	 * @param host The hostname of the server
	 * @param port The port to connect to on the server
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private void connect(String host, int port) throws UnknownHostException, IOException{
		socket = new Socket(host, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintStream(socket.getOutputStream());

		readCommand(true);
	}
	
	/**
	 * Connects to the server with SSL.
	 * http://www.exampledepot.com/egs/javax.net.ssl/Client.html
	 * 
	 * @param host The hostname of the server
	 * @param port The port to connect to on the server
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private void connectSSL(String host, int port) throws UnknownHostException, IOException{
		SocketFactory socketFactory = SSLSocketFactory.getDefault();
		socket = socketFactory.createSocket(host, port);
         
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintStream(socket.getOutputStream());

		readCommand(DEBUG);
	}

	/**
	 * Logs in to the POP3 server with the username and password if the password is set
	 * 
	 * @param user The user name
	 * @param password The password or null if no password is required
	 * @throws IOException
	 */
	private void login(String user, String password) throws IOException{
		sendCommand("USER "+user);
		if(password != null){
			sendNoReplyCommand("PASS "+password, false);
			if(DEBUG)System.out.println("PASS ***");
			readCommand(DEBUG);
		}
	}
	
	/**
	 * Returns the number of messages that is on the server
	 * 
	 * @return Message count
	 * @throws IOException
	 */
	public int getMessageCount() throws IOException{
		String msg = sendCommand("STAT", DEBUG);
		return Integer.parseInt(
				msg.substring(
						msg.indexOf(' ')+1,
						msg.indexOf(' ', msg.indexOf(' ')+1)));
	}
	
	/**
	 * Retrieves the message with the given id.
	 * 
	 * @param id The id of the message to get
	 * @return The message
	 * @throws IOException 
	 */
	public String getMessage(int id) throws IOException{
		sendCommand("RETR "+id);
		return readMultipleLines(DEBUG);
	}
	
	/**
	 * Returns the title of the message
	 * 
	 * @param id The message id
	 * @return The title
	 * @throws IOException
	 */
	public String getMessageTitle(int id) throws IOException{
		String tmp = getMessageHeader(id);
		String tmp2 = tmp.toLowerCase();
		if(tmp2.contains("subject:")){
			return tmp.substring(
					tmp2.indexOf("subject:")+8, 
					tmp2.indexOf('\n', 
							tmp2.indexOf("subject:")));
		}
		else
			return null;
	}
	
	/**
	 * Returns the header of the given message id.
	 * 
	 * @param id The id of the message to get
	 * @return The message
	 * @throws IOException 
	 */
	public String getMessageHeader(int id) throws IOException{
		sendCommand("TOP "+id+" 0");
		return readMultipleLines(DEBUG);
	}
	
	/**
	 * Deletes the message with the given id
	 * 
	 * @param id The id of the message to be deleted
	 * @throws IOException
	 */
	public void delete(int id) throws IOException{
		sendCommand("DELE "+id);
	}


//*********************** IO Stuff *********************************************
	
	/**
	 * Sends the given line to the server and returns a status integer
	 * 
	 * @param cmd The command to send
	 * @return The return code from the server
	 * @throws IOException if the cmd fails
	 */
	private boolean sendCommand(String cmd) throws IOException{
		return parseReturnCode(sendCommand(cmd, DEBUG));
	}

	/**
	 * Sends the given line to the server and returns the last line
	 * 
	 * @param cmd The command to send
	 * @param print To print out the received lines
	 * @return Last String line from the server
	 * @throws IOException 
	 */
	private String sendCommand(String cmd, boolean print) throws IOException{
		sendNoReplyCommand(cmd, print);
		return readCommand(print);
	}

	/**
	 * Sends a given command and don't cares about the reply
	 * 
	 * @param cmd The command
	 * @param print If it should print to System.out
	 * @throws IOException 
	 */
	private void sendNoReplyCommand(String cmd, boolean print) throws IOException{
		out.println(cmd);
		if(print)System.out.println(cmd);
	}

	/**
	 * Reads on line from the command channel
	 * 
	 * @param print If the method should print the input line
	 * @return The input line
	 * @throws IOException if the server returns a error code
	 */
	private String readCommand(boolean print) throws IOException{
		String tmp = in.readLine();
		if(print)System.out.println(tmp);
		if( !parseReturnCode(tmp) ) throw new IOException(tmp);

		return tmp;
	}

	/**
	 * Reads from the server until there are a line with 
	 * only one '.'
	 * 
	 * @param print To print out the received lines
	 * @return String with the text
	 * @throws IOException
	 */
	private String readMultipleLines(boolean print) throws IOException{
		StringBuffer msg = new StringBuffer();
		String tmp = in.readLine();
		while(!tmp.equals(".")){
			msg.append(tmp);
			msg.append('\n');
			tmp = in.readLine();
			if(print)System.out.println(tmp);
		}
		
		return msg.toString();
	}	

	
	/**
	 * Parses the return line from the server and returns the status code
	 * 
	 * @param msg The message from the server
	 * @return Returns true if return code is OK false if it is ERR
	 * @throws IOException 
	 */
	private boolean parseReturnCode(String msg){
		int endpos = (msg.indexOf(' ')<0 ? msg.length() : msg.indexOf(' '));
		return msg.substring(0, endpos).equals("+OK");
	}
	
//*********************************************************************************

	/**
	 * All the delete marked messages are unmarkt
	 * @throws IOException 
	 */
	public void reset() throws IOException{
		sendCommand("RSET", DEBUG);
	}

	/**
	 * All the changes(DELETE) are performed and then the connection is closed
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException{
		sendCommand("QUIT", DEBUG);
		in.close();
		out.close();
		socket.close();
	}
}
