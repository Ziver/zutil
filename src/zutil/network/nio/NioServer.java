package zutil.net.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;

public class NioServer extends NioNetwork{
	
	/**
	 * Creates a NioServer object which listens on localhost
	 * 
	 * @param port The port to listen to
	 */
	public NioServer(int port) throws IOException {
		this(null, port);
	}
	
	/**
	 * Creates a NioServer object which listens to a specific address
	 * 
	 * @param address The address to listen to
	 * @param port The port to listen to
	 */
	public NioServer(InetAddress address, int port) throws IOException {
		super(address, port, NetworkType.SERVER);
		new Thread(this).start();
	}

	protected Selector initSelector() throws IOException {
		// Create a new selector
		Selector socketSelector = SelectorProvider.provider().openSelector();

		// Create a new non-blocking server socket channel
		serverChannel = ServerSocketChannel.open();
		serverChannel.socket().setReuseAddress(true);
		serverChannel.configureBlocking(false);

		// Bind the server socket to the specified address and port
		InetSocketAddress isa = new InetSocketAddress(address, port);
		serverChannel.socket().bind(isa);

		// Register the server socket channel, indicating an interest in 
		// accepting new connections
		serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

		return socketSelector;
	}
	
	/**
	 * Broadcasts the message to all the connected clients
	 * 
	 * @param data The data to broadcast
	 */
	public void broadcast(byte[] data){
		synchronized(clients){
			Iterator<InetSocketAddress> it = clients.keySet().iterator();
			while(it.hasNext()){
				send(it.next(), data);
			}
		}
	}
	
}
