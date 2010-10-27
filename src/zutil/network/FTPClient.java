package zutil.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Pattern;

import javax.security.auth.login.AccountException;

import zutil.io.MultiPrintStream;

/**
 * A simple FTP client class
 * 
 * @author Ziver
 *
 * http://en.wikipedia.org/wiki/List_of_FTP_commands
 * http://en.wikipedia.org/wiki/List_of_FTP_server_return_codes
 * http://www.ietf.org/rfc/rfc959.txt
 *
 * TODO: file info, rename, Active mode
 */
public class FTPClient extends Thread{
	public static boolean DEBUG = true;
	
	public static final int FTP_ACTIVE = 0;
	public static final int FTP_PASSIVE = 1;
	public static final int FTP_PORT = 21;
	public static final int FTP_DATA_PORT = 20;
	public static final int FTP_NOOP_INT = 120;

	//**************  FTP Return Codes ******************
	public static final int FTPC_USER_OK = 331;
	public static final int FTPC_NEED_PASS = 331;
	public static final int FTPC_LOGIN_NO = 530;
	public static final int FTPC_LOGIN_OK = 230;
	
	public static final int FTPC_ENTERING_PASSIVE = 227;
	public static final int FTPC_FILE_ACTION_OK = 250;
	public static final int FTPC_PATH_CREATED = 257;
	//***************************************************

	private BufferedReader in;
	private PrintStream out;
	private Socket socket;
	private long last_sent;

	public static void main(String[] args){
		try {
			FTPClient client = new FTPClient("213.180.86.135", 21, "administrator", "geineZ2K", FTP_PASSIVE);
			/*
			client.createDir("./ziver/lol");
			client.removeDir("./ziver/lol");
			
			MultiPrintStream.out.dump(client.getFileList("./ziver"));
			client.sendFile("./ziver/test.txt", "lol");
			MultiPrintStream.out.dump(client.getFileList("./ziver"));
			
			MultiPrintStream.out.dump(client.getFile("./ziver/test.txt"));
			client.readCommand(DEBUG);
			
			MultiPrintStream.out.println(client.getFileInfo("./ziver/test.txt"));
			
			MultiPrintStream.out.dump(client.getFileList("./ziver"));
			client.removeFile("./ziver/test.txt");
			MultiPrintStream.out.dump(client.getFileList("./ziver"));
			*/
			ArrayList<String[]> tmp = client.getFileInfo("");
			MultiPrintStream.out.println("****************");
			MultiPrintStream.out.dump(tmp);
			MultiPrintStream.out.println(tmp.size());
			MultiPrintStream.out.println("****************");
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}		
	}

	/**
	 * Creates a FTP connection and logs in
	 * 
	 * @param url The address to server
	 * @param port Port number
	 * @param user User name
	 * @param pass Password
	 * @param connection_type Pasive or Active
	 */
	public FTPClient(String url, int port, String user, String pass, int connection_type) throws UnknownHostException, IOException, AccountException{
		socket = new Socket(url, port);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintStream(socket.getOutputStream());
		
		readCommand(DEBUG);
		sendCommand("USER "+user);
		sendNoReplyCommand("PASS "+pass, DEBUG);
		if(DEBUG)System.out.println("PASS ***");
		String tmp = readMultipleCommands(DEBUG);
		if(parseReturnCode(tmp) == FTPC_LOGIN_NO){
			close();
			throw new AccountException(tmp);
		}
		
		start();
	}

//**************************************************************************************
//**************************************************************************************
//********************************* Command channel ************************************
	
	/**
	 * Sends the given line to the server and returns a status integer
	 * 
	 * @param cmd The command to send
	 * @return The return code from the server
	 * @throws IOException 
	 */
	public synchronized int sendCommand(String cmd) throws IOException{
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
	private synchronized String sendCommand(String cmd, boolean print) throws IOException{
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
	private synchronized void sendNoReplyCommand(String cmd, boolean print) throws IOException{
		out.println(cmd);
		last_sent = System.currentTimeMillis();
		if(print)System.out.println(cmd);
	}
	
	/**
	 * Reads on line from the command channel
	 * 
	 * @param print If the method should print the input line
	 * @return The input line
	 * @throws IOException
	 */
	public synchronized String readCommand(boolean print) throws IOException{
		String tmp = in.readLine();
		if(print)System.out.println(tmp);
		if(parseReturnCode(tmp) >= 400 ) throw new IOException(tmp);
		
		return tmp;
	}
	
	/**
	 * Reads from the command channel until there are nothing
	 * left to read and returns the last line
	 * 
	 * @param print To print out the received lines
	 * @return The last received line
	 * @throws IOException
	 */
	private synchronized String readMultipleCommands(boolean print) throws IOException{
		String tmp = readCommand(print);
		while(!tmp.substring(3, 4).equalsIgnoreCase(" ")){
			tmp = readCommand(print);
		}
		
		/*
		String tmp = in.readLine();
		if(print)System.out.println(tmp);
		try{ Thread.sleep(500); }catch(Exception e){}
		while(in.ready()){
			tmp = in.readLine();
			if(print)System.out.println(tmp);
			try{ Thread.sleep(500); }catch(Exception e){}
		}
		*/
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

//**************************************************************************************
//**************************************************************************************
//****************************** File system actions ************************************
	
	/**
	 * Returns a LinkedList with the names of all the files in the directory
	 * 
	 * @param path Path to the files to be listed
	 * @return LinkedList whit filenames
	 * @throws IOException 
	 */
	public LinkedList<String> getFileList(String path) throws IOException{
		LinkedList<String> list = new LinkedList<String>();
		
		BufferedReader data_in = getDataInputStream();
		sendCommand("NLST "+path, DEBUG);		
		
		String tmp = "";
		while((tmp = data_in.readLine()) != null){
			list.add(tmp);
		}
		
		data_in.close();
		readCommand(DEBUG);
		return list;
	}
	
	/**
	 * Returns information about the file or directory
	 * 
	 * @deprecated
	 * @param path The path and filename of a file or a directory
	 * @return A List of Strings with information
	 * @throws IOException 
	 */
	public ArrayList<String[]> getFileInfo(String path) throws IOException{
		Pattern regex = Pattern.compile("\\s{1,}");
		ArrayList<String[]> info = new ArrayList<String[]>();
		
		BufferedReader data_in = getDataInputStream();
		sendCommand("LIST "+path, DEBUG);		
		
		String tmp = "";
		while((tmp = data_in.readLine()) != null){			
			System.err.println(tmp);
			info.add(regex.split(tmp));
		}
		
		data_in.close();
		readCommand(DEBUG);
		return info;
	}
	
	/**
	 * Creates a file at the server with the given data
	 * 
	 * @param path The path and filename
	 * @param data The data to put in the file
	 * @throws IOException 
	 */
	public void sendFile(String path, String data) throws IOException{
		PrintStream data_out = getDataOutputStream();
		sendCommand("STOR "+path, DEBUG);
		data_out.println(data);
		data_out.close();
		readCommand(DEBUG);
	}
	
	/**
	 * Creates a directory at the server
	 * 
	 * @param path The path to the directory
	 * @throws IOException 
	 */
	public boolean createDir(String path) throws IOException{
		if(sendCommand("MKD "+path) == FTPC_PATH_CREATED)
			return true;
		return false;
	}
	
	/**
	 * Returns a BufferedReader with the file data
	 * WARNING: you must run readCommand(true); after you close the stream
	 * 
	 * @param path The path and filename
	 * @return Stream with the file
	 * @throws IOException 
	 */
	private BufferedReader getFile(String path) throws IOException{
		BufferedReader ret = getDataInputStream();
		sendCommand("RETR "+path, DEBUG);
		return ret;
	}
	
	/**
	 * Downloads a file from the FTP server to a local file
	 * 
	 * @param source The source file on the server
	 * @param destination The local file to save to
	 * @throws IOException 
	 */
	public void getFile(String source, String destination) throws IOException{
		BufferedReader file_in = getFile(source);
		PrintStream file_out = new PrintStream(new File(destination));
		
		String tmp = "";
		while((tmp = file_in.readLine()) != null){
			file_out.println(tmp);
		}
		readCommand(DEBUG);
	}
	
	/**
	 * Removes a file from the FTP server
	 * 
	 * @param path The path and filename of the file to be deleted
	 * @return True if the command was successful or false otherwise
	 * @throws IOException 
	 */
	public boolean removeFile(String path) throws IOException{
		if(sendCommand("DELE "+path) == FTPC_FILE_ACTION_OK)
			return true;
		return false;
	}
	
	/**
	 * Removes a directory from the FTP server
	 * 
	 * @param path The path of the directory to be deleted
	 * @return True if the command was successful or false otherwise
	 * @throws IOException 
	 */
	public boolean removeDir(String path) throws IOException{
		if(sendCommand("RMD "+path) == FTPC_FILE_ACTION_OK)
			return true;
		return false;
	}

//**************************************************************************************
//**************************************************************************************
//******************************** Data Connection *************************************
	
	/**
	 * Starts a connection to the server. It automatically handles
	 * passive or active mode
	 * 
	 * @return The PrintStream for the channel
	 * @throws IOException 
	 */
	public synchronized PrintStream getDataOutputStream() throws IOException{
		int port = getDataConnectionPortType();
		if(port < 0){ // Active Mode
			port *= -1;
			return getActiveDataOutputStream(port);
		}
		else{
			System.out.println("port: "+port);
			return getPassiveDataOutputStream(port);
		}
	}
	
	/**
	 * Starts a connection to the server. It automatically handles
	 * passive or active mode
	 * 
	 * @return The BufferedReader for the channel
	 * @throws IOException 
	 */
	public synchronized BufferedReader getDataInputStream() throws IOException{
		int port = getDataConnectionPortType();
		if(port < 0){ // Active Mode
			port *= -1;
			return getActiveDataInputStream(port);
		}
		else{
			return getPassiveDataInputStream(port);
		}
	}
	
	/**
	 * This method chooses the appropriate data connection type
	 * to the server (Passive or Active) and returns the port number
	 * 
	 * @return A port number. If port > 0 = Passive AND port < 0 Active
	 * @throws IOException 
	 */
	private int getDataConnectionPortType() throws IOException{
		return setPassiveMode();
	}
	
	/**
	 * Connects to the data port on the server and returns the InputStream
	 * 
	 * @param port The port to connect to
	 * @return The InputStream for the data channel
	 * @throws IOException 
	 */
	private BufferedReader getPassiveDataInputStream(int port) throws IOException{
		Socket data_socket = new Socket(socket.getInetAddress().getHostAddress(), port);
		BufferedReader data_in = new BufferedReader(new InputStreamReader(data_socket.getInputStream()));
		
		return data_in;
	}
	
	/**
	 * Connects to the data port on the server and returns the OutputStream
	 * 
	 * @param port The port to connect to
	 * @return The OutputStream for the data channel
	 * @throws IOException 
	 */
	private PrintStream getPassiveDataOutputStream(int port) throws IOException{
		Socket data_socket = new Socket(socket.getInetAddress().getHostAddress(), port);
		PrintStream data_out = new PrintStream(data_socket.getOutputStream());
		
		return data_out;
	}
	
	/**
	 * Listens on a local port for a connection from the server
	 * and returns with the InputStream of the connection from the server
	 * 
	 * @param port The port to listen to
	 * @return The InputStream for the data channel
	 * @throws IOException 
	 */
	private BufferedReader getActiveDataInputStream(int port) throws IOException{
		// TODO: 
		return null;
	}
	
	/**
	 * Listens on a local port for a connection from the server
	 * and returns with the OutputStream of the connection from the server
	 * 
	 * @param port The port to listen to
	 * @return The OutputStream for the data channel
	 * @throws IOException 
	 */
	private PrintStream getActiveDataOutputStream(int port) throws IOException{
		// TODO: 
		return null;
	}
	
	/**
	 * Sets Passive mode at the server and returns the port number 
	 * for the data channel
	 * 
	 * @return Port number for data channel
	 * @throws IOException
	 */
	private int setPassiveMode() throws IOException{
		String tmp = sendCommand("PASV", DEBUG);
		if(parseReturnCode(tmp) != FTPC_ENTERING_PASSIVE){
			throw new IOException(tmp);
		}
		tmp = tmp.substring(tmp.indexOf('(')+1, tmp.indexOf(')'));
		String[] tmpArray = tmp.split("[,]");
		
		if(tmpArray.length <= 1)
			return Integer.parseInt(tmpArray[0]);
		else
			return Integer.parseInt(tmpArray[4])*256 + Integer.parseInt(tmpArray[5]);
	}
	
//**************************************************************************************
//**************************************************************************************
	
	/**
	 * To keep the connection alive
	 */
	public void run(){
		try {
			while(true){
				if(last_sent > System.currentTimeMillis() + FTP_NOOP_INT*1000){
					sendCommand("NOOP");
				}
				try{ Thread.sleep(5000); }catch(Exception e){}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Close the FTP connection
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("deprecation")
	public void close() throws IOException{
		sendCommand("QUIT", DEBUG);
		in.close();
		out.close();
		socket.close();
		this.stop();
	}
}
