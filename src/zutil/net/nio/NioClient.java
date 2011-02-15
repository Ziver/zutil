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
