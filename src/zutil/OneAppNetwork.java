package zutil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class checks if the app is alredy running
 * by Locking a port
 * 
 * @author Ziver Koc
 */
public class OneAppNetwork extends Thread implements OneApp{
	private int port;
	
	/**
	 * Creates a One App objekt
	 * 
	 * @param port The port to lock
	 */
	public OneAppNetwork(int port){
		this.port = port;
	}
	
	/**
	 * Starts the port lock
	 * 
	 * @return Always true
	 */
	public boolean lockApp(){
		this.start();
		return true;
	}
	
	/**
	 * he port lock thread 
	 * should not be cald outside the class
	 */
	public void run() {
		ServerSocket serverSocket = null;
		Socket clientSocket = null;
		try {
			// Create the server socket
			serverSocket = new ServerSocket(port, 1);
			while (true) {
				// Wait for a connection
				clientSocket = serverSocket.accept();
				clientSocket.close();
			}
		}
		catch (IOException ioe) {
			MultiPrintStream.out.println("Error in JustOneServer: " + ioe);
		}
	}

	/**
	 * Checks if the port is locked
	 * 
	 * @return True if port is locked else false
	 */
	public boolean check() {
		try {
			Socket clientSocket = new Socket("localhost", port);
			MultiPrintStream.out.println("Already running!!!");
			clientSocket.close();
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}
}
