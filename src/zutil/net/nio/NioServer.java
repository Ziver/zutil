/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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
     * @param	port	the port to listen to
     */
    public NioServer(int port) throws IOException {
        this(null, port);
    }

    /**
     * Creates a NioServer object which listens to a specific address
     *
     * @param	address	the address to listen to
     * @param	port	the port to listen to
     */
    public NioServer(InetAddress address, int port) throws IOException {
        super(new InetSocketAddress(address, port));
    }

    protected Selector initSelector() throws IOException {
        // Create a new selector
        Selector socketSelector = SelectorProvider.provider().openSelector();

        // Create a new non-blocking server socket channel
        serverChannel = ServerSocketChannel.open();
        serverChannel.socket().setReuseAddress(true);
        serverChannel.configureBlocking(false);

        // Bind the server socket to the specified address and port
        serverChannel.socket().bind(localAddress);

        // Register the server socket channel, indicating an interest in
        // accepting new connections
        serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

        return socketSelector;
    }

    /**
     * Broadcasts the message to all the connected clients
     *
     * @param	data	the data to broadcast
     */
    public void broadcast(byte[] data){
        synchronized(clients){
            for(InetSocketAddress target : clients.keySet()){
                send(target, data);
            }
        }
    }

}
