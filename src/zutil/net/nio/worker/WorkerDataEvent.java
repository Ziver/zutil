package zutil.net.nio.worker;

import java.nio.channels.SocketChannel;

import zutil.net.nio.NioNetwork;


public class WorkerDataEvent {
	public NioNetwork network;
	public SocketChannel socket;
	public Object data;
	
	public WorkerDataEvent(NioNetwork server, SocketChannel socket, Object data) {
		this.network = server;
		this.socket = socket;
		this.data = data;
	}
}