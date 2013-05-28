/*******************************************************************************
 * Copyright (c) 2013 Ziver
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
 ******************************************************************************/

package zutil.net.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

import zutil.net.nio.message.Message;
import zutil.net.nio.message.type.ResponseRequestMessage;
import zutil.net.nio.response.ResponseEvent;


public class NioClient extends NioNetwork{
	private SocketChannel serverSocket;
	
	/**
	 * Creates a NioClient that connects to a server
	 * 
	 * @param hostAddress The server address
	 * @param port The port to listen on
	 */
	public NioClient(InetAddress serverAddress, int port) throws IOException {
		super(InetAddress.getLocalHost(), port, NetworkType.CLIENT);
		serverSocket = initiateConnection(new InetSocketAddress(serverAddress, port));
		Thread thread = new Thread(this);
		thread.setDaemon(false);
		thread.start();
	}

	protected Selector initSelector() throws IOException {
		// Create a new selector
		return SelectorProvider.provider().openSelector();
	}
	
	/**
	 * Sends a Message to the default server
	 * 
	 * @param data The data to be sent
	 * @throws IOException Something got wrong
	 */
	public void send(Message data) throws IOException {
		send(serverSocket, data);
	}
	
	/**
	 * This method is for the Client to send a message to the server
	 * 
	 * @param handler The response handler
	 * @param data The data to send
	 * @throws IOException
	 */
	public void send(ResponseEvent handler, ResponseRequestMessage data) throws IOException {
		send(serverSocket, handler, data);
	}
}
