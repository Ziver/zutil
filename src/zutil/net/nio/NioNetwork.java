/*******************************************************************************
 * Copyright (c) 2011 Ziver Koc
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
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import zutil.Encrypter;
import zutil.converters.Converter;
import zutil.io.DynamicByteArrayStream;
import zutil.io.MultiPrintStream;
import zutil.log.LogUtil;
import zutil.net.nio.message.type.ResponseRequestMessage;
import zutil.net.nio.message.type.SystemMessage;
import zutil.net.nio.response.ResponseEvent;
import zutil.net.nio.server.ChangeRequest;
import zutil.net.nio.server.ClientData;
import zutil.net.nio.worker.SystemWorker;
import zutil.net.nio.worker.Worker;


public abstract class NioNetwork implements Runnable {
	private static Logger logger = LogUtil.getLogger();
	public static enum NetworkType {SERVER, CLIENT};

	private NetworkType type;

	// The host:port combination to listen on
	protected InetAddress address;
	protected int port;

	// The channel on which we'll accept connections
	protected ServerSocketChannel serverChannel;
	// The selector we'll be monitoring
	private Selector selector;
	// The buffer into which we'll read data when it's available
	private ByteBuffer readBuffer = ByteBuffer.allocate(8192);
	protected Worker worker;
	protected SystemWorker systemWorker;

	// This map contains all the clients that are conncted
	protected Map<InetSocketAddress, ClientData> clients = new HashMap<InetSocketAddress, ClientData>();

	// A list of PendingChange instances
	private List<ChangeRequest> pendingChanges = new LinkedList<ChangeRequest>();
	// Maps a SocketChannel to a list of ByteBuffer instances
	private Map<SocketChannel, List<ByteBuffer>> pendingWriteData = new HashMap<SocketChannel, List<ByteBuffer>>();
	private Map<SocketChannel, DynamicByteArrayStream> pendingReadData = new HashMap<SocketChannel, DynamicByteArrayStream>();
	// The encrypter
	private Encrypter encrypter;

	/**
	 * Create a nio network class
	 * 
	 * @param hostAddress The host address
	 * @param port The port 
	 * @param type The type of network host
	 * @throws IOException
	 */
	public NioNetwork(InetAddress address, int port, NetworkType type) throws IOException {
		this.port = port;
		this.address = address;
		this.type = type;
		this.selector = initSelector();
		this.systemWorker = new SystemWorker(this);
	}

	protected abstract Selector initSelector() throws IOException;

	/**
	 * Sets the Worker for the network messages
	 * 
	 * @param worker The worker that handles the incoming messages
	 */
	public void setDefaultWorker(Worker worker){
		this.worker = worker;
	}

	/**
	 * Sets the encrypter to use in the network
	 * 
	 * @param enc The encrypter to use or null fo no encryption
	 */
	public void setEncrypter(Encrypter enc){
		encrypter = enc;
		MultiPrintStream.out.println("Network Encryption "+
				(encrypter != null ? "Enabled("+encrypter.getAlgorithm()+")" : "Disabled")+"!!");
	}

	public void send(SocketChannel socket, Object data) throws IOException{
		send(socket, Converter.toBytes(data));
	}

	public void send(InetSocketAddress address, Object data) throws IOException{
		send(address, Converter.toBytes(data));
	}

	public void send(InetSocketAddress address, byte[] data){
		send(getSocketChannel(address), data);
	}

	public void send(SocketChannel socket, ResponseEvent handler, ResponseRequestMessage data) throws IOException {
		// Register the response handler
		systemWorker.addResponseHandler(handler, data);

		queueSend(socket,Converter.toBytes(data));
	}

	/**
	 * This method sends data true the given socket
	 * 
	 * @param socket The socket
	 * @param data The data to send
	 */
	public void send(SocketChannel socket, byte[] data) {
		queueSend(socket,data);
	}

	/**
	 * Queues the message to be sent and wakeups the selector
	 * 
	 * @param socket The socet to send the message thrue
	 * @param data The data to send
	 */
	protected void queueSend(SocketChannel socket, byte[] data){
		logger.finest("Sending Queue...");
		// And queue the data we want written
		synchronized (pendingWriteData) {
			List<ByteBuffer> queue = pendingWriteData.get(socket);
			if (queue == null) {
				queue = new ArrayList<ByteBuffer>();
				pendingWriteData.put(socket, queue);
			}
			//encrypts
			if(encrypter != null) 
				queue.add(ByteBuffer.wrap(encrypter.encrypt(data)));
			else queue.add(ByteBuffer.wrap(data));		
		}
		// Changing the key state to write 
		synchronized (pendingChanges) {
			// Indicate we want the interest ops set changed
			pendingChanges.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));
		}
		logger.finest("selector.wakeup();");
		// Finally, wake up our selecting thread so it can make the required changes
		selector.wakeup();
	}

	public void run() {
		logger.fine("NioNetwork Started!!!");
		while (true) {
			try {
				// Process any pending changes
				synchronized (pendingChanges) {
					Iterator<ChangeRequest> changes = pendingChanges.iterator();
					while (changes.hasNext()) {
						ChangeRequest change = changes.next();
						switch (change.type) {
						case ChangeRequest.CHANGEOPS:
							SelectionKey key = change.socket.keyFor(selector);
							key.interestOps(change.ops);
							logger.finest("change.ops "+change.ops);
							break;
						case ChangeRequest.REGISTER:
							change.socket.register(selector, change.ops);
							logger.finest("register socket ");
							break;
						}
					}
					pendingChanges.clear();
				}

				// Wait for an event one of the registered channels
				selector.select();
				logger.finest("selector is awake");

				// Iterate over the set of keys for which events are available
				Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
				while (selectedKeys.hasNext()) {
					SelectionKey key = (SelectionKey) selectedKeys.next();
					selectedKeys.remove();
					logger.finest("KeyOP: "+key.interestOps()+"	isAcceptable: "+SelectionKey.OP_ACCEPT+" isConnectable: "+SelectionKey.OP_CONNECT+" isWritable: "+SelectionKey.OP_WRITE+" isReadable: "+SelectionKey.OP_READ);

					if (key.isValid()) {
						// Check what event is available and deal with it
						if (key.isAcceptable()) {
							logger.finest("Accepting Connection!!");
							accept(key);
						}
						else if (key.isConnectable()) {
							logger.finest("Finnishing Connection!!");
							finishConnection(key);
						}
						else if (key.isWritable()) {
							logger.finest("Writing");
							write(key);
						}
						else if (key.isReadable()) {
							logger.finest("Reading");
							read(key);
						} 
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Server
	 */
	private void accept(SelectionKey key) throws IOException {
		// For an accept to be pending the channel must be a server socket channel.
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

		// Accept the connection and make it non-blocking
		SocketChannel socketChannel = serverSocketChannel.accept();
		socketChannel.socket().setReuseAddress(true);
		socketChannel.configureBlocking(false);

		// Register the new SocketChannel with our Selector, indicating
		// we'd like to be notified when there's data waiting to be read
		socketChannel.register(selector, SelectionKey.OP_READ);

		// adds the client to the clients list
		InetSocketAddress remoteAdr = (InetSocketAddress) socketChannel.socket().getRemoteSocketAddress();
		if(!clients.containsValue(remoteAdr)){
			clients.put(remoteAdr, new ClientData(socketChannel));
			logger.fine("New Connection("+remoteAdr+")!!! Count: "+clients.size());
		}
	}

	/**
	 * Client and Server
	 */
	private void read(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();
		InetSocketAddress remoteAdr = (InetSocketAddress) socketChannel.socket().getRemoteSocketAddress();

		// Clear out our read buffer so it's ready for new data
		readBuffer.clear();

		// Attempt to read off the channel
		int numRead;
		try {
			numRead = socketChannel.read(readBuffer);
		} catch (IOException e) {
			// The remote forcibly closed the connection, cancel
			// the selection key and close the channel.
			key.cancel();
			socketChannel.close();
			clients.remove(remoteAdr);
			pendingReadData.remove(socketChannel);
			pendingWriteData.remove(socketChannel);
			logger.fine("Connection Forced Close("+remoteAdr+")!!! Connection Count: "+clients.size());
			if(type == NetworkType.CLIENT) 
				throw new IOException("Server Closed The Connection!!!");
			return;
		}

		if (numRead == -1) {
			// Remote entity shut the socket down cleanly. Do the
			// same from our end and cancel the channel.
			key.channel().close();
			key.cancel();
			clients.remove(remoteAdr);
			pendingReadData.remove(socketChannel);
			pendingWriteData.remove(socketChannel);
			logger.fine("Connection Close("+remoteAdr+")!!! Connection Count: "+clients.size());
			if(type == NetworkType.CLIENT) 
				throw new IOException("Server Closed The Connection!!!");
			return;
		}

		// Make a correctly sized copy of the data before handing it
		// to the client
		byte[] rspByteData = new byte[numRead];
		System.arraycopy(readBuffer.array(), 0, rspByteData, 0, numRead);
		if(encrypter != null)// Encryption
			rspByteData = encrypter.decrypt(rspByteData);
		
		// Message Count 1m: 36750
		// Message Count 1s: 612
		if(!pendingReadData.containsKey(socketChannel)){
			pendingReadData.put(socketChannel, new DynamicByteArrayStream());
		}
		DynamicByteArrayStream dynBuf = pendingReadData.get(socketChannel);
		dynBuf.append(rspByteData);
		 
		
		Object rspData = null;		
		try{
			//rspData = Converter.toObject(rspByteData);
			rspData = Converter.toObject(dynBuf);
			handleRecivedMessage(socketChannel, rspData);
			dynBuf.clear();
		}catch(Exception e){
			e.printStackTrace();
			dynBuf.reset();
		}
	}

	/**
	 * Client and Server
	 */
	private void write(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		synchronized (pendingWriteData) {
			List<ByteBuffer> queue = pendingWriteData.get(socketChannel);
			if(queue == null){
				queue = new ArrayList<ByteBuffer>();
				pendingWriteData.put(socketChannel, queue);
			}

			// Write until there's not more data ...
			while (!queue.isEmpty()) {
				ByteBuffer buf = queue.get(0);
				socketChannel.write(buf);
				if (buf.remaining() > 0) {
					// ... or the socket's buffer fills up
					logger.finest("Write Buffer Full!!");
					break;
				}
				queue.remove(0);
			}

			if (queue.isEmpty()) {
				// We wrote away all data, so we're no longer interested
				// in writing on this socket. Switch back to waiting for
				// data.
				logger.finest("No more Data to write!!");
				key.interestOps(SelectionKey.OP_READ);
			}
		}
	}

	private void handleRecivedMessage(SocketChannel socketChannel, Object rspData){
		logger.finer("Handling incomming message...");
		
		if(rspData instanceof SystemMessage){
			if(systemWorker != null){
				logger.finest("System Message!!!");
				systemWorker.processData(this, socketChannel, rspData);
			}
			else{
				logger.finer("Unhandled System Message!!!");
			}
		}
		else{
			// Hand the data off to our worker thread
			if(worker != null){
				logger.finest("Worker Message!!!");
				worker.processData(this, socketChannel, rspData);
			}
			else{
				logger.fine("Unhandled Worker Message!!!");
			}
		}
	}

	/**
	 * Initializes a socket to a server
	 */
	protected SocketChannel initiateConnection(InetSocketAddress address) throws IOException {
		// Create a non-blocking socket channel
		SocketChannel socketChannel = SocketChannel.open();
		socketChannel.socket().setReuseAddress(true);
		socketChannel.configureBlocking(false);
		logger.fine("Connecting to: "+address);

		// Kick off connection establishment
		socketChannel.connect(address);

		// Queue a channel registration since the caller is not the 
		// selecting thread. As part of the registration we'll register
		// an interest in connection events. These are raised when a channel
		// is ready to complete connection establishment.
		synchronized(this.pendingChanges) {
			pendingChanges.add(new ChangeRequest(socketChannel, ChangeRequest.REGISTER, SelectionKey.OP_CONNECT));
		}

		return socketChannel;
	}

	protected SocketChannel getSocketChannel(InetSocketAddress address){
		return clients.get(address).getSocketChannel();
	}

	/**
	 * Client
	 */
	private void finishConnection(SelectionKey key){
		SocketChannel socketChannel = (SocketChannel) key.channel();

		// Finish the connection. If the connection operation failed
		// this will raise an IOException.
		try {
			socketChannel.finishConnect();
		} catch (IOException e) {
			// Cancel the channel's registration with our selector
			e.printStackTrace();
			key.cancel();
			return;
		}

		// Register an interest in writing on this channel
		key.interestOps(SelectionKey.OP_WRITE);
	}

	/**
	 * Client
	 * @throws IOException 
	 */
	protected void closeConnection(SocketChannel socketChannel) throws IOException{		
		socketChannel.close();
		socketChannel.keyFor(selector).cancel();
	}

	/**
	 * Client
	 * @throws IOException 
	 */
	protected void closeConnection(InetSocketAddress address) throws IOException{		
		closeConnection(getSocketChannel(address));
	}

	/*
	public void close() throws IOException{
		if(serverChannel != null){
			serverChannel.close();
			serverChannel.keyFor(selector).cancel();
		}
		selector.close();
	}*/

	public NetworkType getType(){
		return type;
	}
}