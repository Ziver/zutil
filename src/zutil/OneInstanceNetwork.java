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

package zutil;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import zutil.io.MultiPrintStream;

/**
 * This class checks if the app is alredy running
 * by Locking a port
 * 
 * @author Ziver Koc
 */
public class OneInstanceNetwork extends Thread implements OneInstance{
	private int port;
	
	/**
	 * Creates a One App objekt
	 * 
	 * @param port The port to lock
	 */
	public OneInstanceNetwork(int port){
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
