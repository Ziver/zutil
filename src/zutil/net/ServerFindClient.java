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
