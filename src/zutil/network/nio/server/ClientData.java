package zutil.network.nio.server;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class ClientData {
	private SocketChannel socketChannel;
	private long lastMessageReceived;
	
	public ClientData(SocketChannel socketChannel){
		this.socketChannel = socketChannel;
	}
	
	public SocketChannel getSocketChannel(){
		return socketChannel;
	}
	
	public InetSocketAddress getAddress(){
		return (InetSocketAddress) socketChannel.socket().getRemoteSocketAddress();
	}
	
	public void setLastMessageReceived(long time){
		lastMessageReceived = time;
	}
	
	public long getLastMessageReceived(){
		return lastMessageReceived;
	}
}
