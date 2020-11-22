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

package zutil.net;

import zutil.io.MultiPrintStream;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * This class broadcast its address in the LAN so that
 * the ServerFindClient can get the server IP
 *
 * @author Ziver
 *
 */
public class ServerFind extends Thread {
    private static final String BROADCAST_ADDRESS = "230.0.0.1";

    private InetAddress group;
    private MulticastSocket mSocket;

    private boolean shutdown;
    private int port;

    /**
     * Creates a ServerFind Thread an the specified port
     *
     * @param port The port to run the ServerFind Server on
     */
    public ServerFind (int port) throws IOException {
        this.port = port;
        shutdown = false;
        group = InetAddress.getByName(BROADCAST_ADDRESS);
        mSocket = new MulticastSocket(port);
        mSocket.joinGroup(group);

        start();
    }

    public void run (){
        byte[] buf = new byte[256];
        DatagramPacket packet;
        DatagramSocket lan_socket;

        while (!shutdown){
            try {
                packet = new DatagramPacket(buf, buf.length);
                mSocket.receive(packet);

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
        shutdown = true;
        mSocket.close();
    }
}
