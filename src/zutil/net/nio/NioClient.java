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

package zutil.net.nio;

import zutil.net.nio.message.Message;
import zutil.net.nio.message.RequestResponseMessage;
import zutil.net.nio.response.ResponseEvent;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;


public class NioClient extends NioNetwork{
	private InetSocketAddress remoteAddress;
	
	/**
	 * Creates a NioClient that connects to a server
	 * 
	 * @param	remoteAddress   the server address
	 * @param	remotePort      the port to listen on
	 */
	public NioClient(InetAddress remoteAddress, int remotePort) throws IOException {
		this.remoteAddress = new InetSocketAddress(remoteAddress, remotePort);
		connect(this.remoteAddress);
	}

	protected Selector initSelector() throws IOException {
		// Create a new selector
		return SelectorProvider.provider().openSelector();
	}
	
	/**
	 * Sends a Message to the default server
	 * 
	 * @param   data    the data to be sent
	 */
	public void send(Message data) throws IOException {
		send(remoteAddress, data);
	}
	
	/**
	 * This method is for the Client to send a message to the server
	 * 
	 * @param   handler the response handler
	 * @param   data    the data to send
	 */
	public void send(ResponseEvent handler, RequestResponseMessage data) throws IOException {
		send(remoteAddress, handler, data);
	}
}
