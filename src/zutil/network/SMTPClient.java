package zutil.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * A simple class that connects and logs in to a SMTP
 * server and then send emails.
 * INFO: http://cr.yp.to/smtp/client.html
 * 
 * @author Ziver
 *
 */
public class SMTPClient {

	private BufferedReader in;
	private PrintStream out;
	private Socket socket;
	private String url;
	private int port;

	public SMTPClient(String url){
		this(url, 25);
	}

	public SMTPClient(String url, int port){
		this.url = url;
		this.port = port;
	}

	/**
	 * Connects to the server. 
	 * Sends the message. 
	 * Closes the connection
	 * 
	 * @param from The destination email address
	 * @param to The recipients email address
	 * @param subj The subject of the message
	 * @param msg The message
	 * @throws IOException
	 */
	public synchronized void send(String from, String to, String subj, String msg) throws IOException{
		try{
			connect();
			// FROM and TO
			sendCommand("MAIL FROM:"+from);
			sendCommand("RCPT TO:"+to);
			sendCommand("DATA");
			// The Message
			sendNoReplyCommand("Date: "+(new Date()), true);
			sendNoReplyCommand("From: "+from, true);
			sendNoReplyCommand("To: "+to, true);
			sendNoReplyCommand("Subject: "+subj, true);
			sendNoReplyCommand(" ", true);
			sendNoReplyCommand(msg, true);
			sendCommand(".", true);
			
			close();
		}catch(IOException e){
			close();
			throw e;
		}
	}

	/**
	 * Connects to the server
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public void connect() throws UnknownHostException, IOException{
		socket = new Socket(url, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintStream(socket.getOutputStream());

		readCommand(true);
		sendCommand("HELO "+url);
	}

	/**
	 * Sends the given line to the server and returns a status integer
	 * 
	 * @param cmd The command to send
	 * @return The return code from the server
	 * @throws IOException if the cmd fails
	 */
	private int sendCommand(String cmd) throws IOException{
		return parseReturnCode(sendCommand(cmd, true));
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
		if(parseReturnCode(tmp) >= 400 ) throw new IOException(tmp);

		return tmp;
	}

	/**
	 * Parses the return line from the server and returns the status code
	 * 
	 * @param msg The message from the server
	 * @return The status code
	 * @throws IOException 
	 */
	private synchronized int parseReturnCode(String msg){
		return Integer.parseInt(msg.substring(0, 3));
	}

	public void close() throws IOException{
		sendCommand("QUIT", true);
		in.close();
		out.close();
		socket.close();
	}
}
