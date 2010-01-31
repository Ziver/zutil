package zutil.network.threaded;

import java.net.DatagramPacket;

/**
 * This interface is for processing received packets 
 * from the TNetworkUDPServer
 * 
 * @author Ziver
 *
 */
public interface ThreadedUDPNetworkThread extends Runnable{
	
	/**
	 * Packet will be processed in this method
	 * 
	 * @param packet is the received packet
	 * @param network is the network class that received the packet
	 */
	public void receivedPacket(DatagramPacket packet, ThreadedUDPNetwork network);
}
