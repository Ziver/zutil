package zutil.net.nio.service;

import java.nio.channels.SocketChannel;

import zutil.net.nio.NioNetwork;
import zutil.net.nio.message.Message;

public abstract class NetworkService {
	protected static NetworkService instance;
	protected NioNetwork nio;
	
	public NetworkService(NioNetwork nio){
		instance = this;
		this.nio = nio;
	}
	
	public abstract void handleMessage(Message message, SocketChannel socket);
	
	/**
	 * @return A instance of this class
	 */
	public static NetworkService getInstance(){
		return instance;
	}
}
