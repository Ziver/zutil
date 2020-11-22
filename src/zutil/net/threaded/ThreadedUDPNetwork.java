/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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

package zutil.net.threaded;

import java.io.IOException;
import java.net.*;



/**
 *  * A simple network server that handles UDP communication
 *
 * @author Ziver
 */
public class ThreadedUDPNetwork extends Thread{
    public static final int BUFFER_SIZE = 512;

    // Type of UDP socket
    enum UDPType{
        MULTICAST,
        UNICAST
    }
    protected final UDPType type;
    protected final int port;
    protected DatagramSocket socket;
    protected ThreadedUDPNetworkThread thread = null;

    /**
     * Creates a new unicast Client instance of the class
     *
     * @throws SocketException if there is any issue creating the new socket
     */
    public ThreadedUDPNetwork() throws SocketException{
        this.type = UDPType.UNICAST;
        this.port = -1;

        socket = new DatagramSocket();
    }

    /**
     * Creates a new unicast Server instance of the class
     *
     * @param	port	is the port that the server should listen to
     * @throws SocketException if there is any issue creating the new socket
     */
    public ThreadedUDPNetwork(int port) throws SocketException{
        this.type = UDPType.UNICAST;
        this.port = port;

        socket = new DatagramSocket( port );
    }

    /**
     * Creates a new multicast Server instance of the class
     *
     * @param	port			is the port that the server should listen to
     * @param	multicastAddr	is the multicast address that the server will listen on
     * @throws  IOException     if there is any issue opening the connection
     */
    public ThreadedUDPNetwork(String multicastAddr, int port ) throws IOException{
        this.type = UDPType.MULTICAST;
        this.port = port;

        // init udp socket
        MulticastSocket msocket = new MulticastSocket( port );
        InetAddress group = InetAddress.getByName( multicastAddr );
        msocket.joinGroup( group );

        socket = msocket;
    }


    public void run(){
        try{
            while(true){
                byte[] buf = new byte[BUFFER_SIZE];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive( packet );
                if( thread!=null )
                    thread.receivedPacket( packet, this );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends the given packet
     *
     * @param	packet	is the packet to send
     * @throws IOException if there is any issue with sending the packet
     */
    public synchronized void send(DatagramPacket packet) throws IOException {
        socket.send(packet);
    }

    /**
     * Sets the thread that will handle the incoming packets
     *
     * @param	thread	is the thread
     */
    public void setThread(ThreadedUDPNetworkThread thread) {
        this.thread = thread;
    }

    /**
     * Stops the server and interrupts its internal thread.
     * This is a permanent action that will not be able to recover from
     */
    public void close(){
        this.interrupt();
        socket.close();
    }
}
