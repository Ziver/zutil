package zutil.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import zutil.io.MultiPrintStream;

/**
 * This class broadcast its address in the LAN so that 
 * the ServerFindClient can get the server IP
 * 
 * @author Ziver
 *
 */
public class ServerFind extends Thread {
	public String broadcastAddress = "230.0.0.1";
	
	private InetAddress group;
	private MulticastSocket Msocket;

	private boolean avsluta;
	private int port;

	/**
	 * Creates a ServerFind Thread an the specified port
	 * 
	 * @param port The port to run the ServerFind Server on
	 * @throws IOException
	 */
	public ServerFind (int port) throws IOException {
		this.port = port;
		avsluta = false;
		group = InetAddress.getByName(broadcastAddress);
		Msocket = new MulticastSocket(port);
		Msocket.joinGroup(group);

		start();
	}

	public void run (){
		byte[] buf = new byte[256];
		DatagramPacket packet;
		DatagramSocket lan_socket = null;
		
		while (!avsluta){
			try {
				packet = new DatagramPacket(buf, buf.length);
				Msocket.receive(packet);

				lan_socket = new DatagramSocket(port , packet.getAddress());		
				packet = new DatagramPacket(buf, buf.length, group, port);
				lan_socket.send(packet);
				lan_socket.close();
			} catch (Exception e) {
				MultiPrintStream.out.println("Error Establishing ServerFind Connection!!!\n" + e);
				e.printStackTrace();
			}
		}
		
		close();
	}

	/**
	 * Closes the broadcast socket
	 */
	public void close(){
		avsluta = true;		
		Msocket.close();
	}
}