package zutil.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * This class is the client for ServerFind that receives the server IP
 * 
 * @author Ziver
 *
 */
public class ServerFindClient{
	public String broadcastAddress = "230.0.0.1";
	
	private int port;

	/**
	 * Creates a ServerFind Client
	 * 
	 * @param port The port to contact the server on
	 */
	public ServerFindClient(int port){
		this.port = port;
	}
	
	/**
	 * Requests IP from server 
	 * 
	 * @return The address of the server
	 * @throws IOException
	 */
	public InetAddress find() throws IOException{
		InetAddress	group = InetAddress.getByName(broadcastAddress);
		DatagramSocket lan_socket = new DatagramSocket();
		MulticastSocket Msocket = new MulticastSocket(port);
		Msocket.joinGroup(group);

		byte[] buf = new byte[256];
		DatagramPacket packet = new DatagramPacket(buf, buf.length, group, port);
		lan_socket.send(packet);
		
		packet = new DatagramPacket(buf, buf.length);
		Msocket.receive(packet);	

		return packet.getAddress();
	}
}