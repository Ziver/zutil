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

package zutil.net.nio;

import zutil.converter.Converter;
import zutil.log.LogUtil;
import zutil.net.nio.server.ChangeRequest;
import zutil.net.nio.server.ClientData;
import zutil.net.nio.worker.Worker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.logging.Logger;


public abstract class NioNetwork implements Runnable {
    private static Logger logger = LogUtil.getLogger();

    protected SocketAddress localAddress;
    // The channel on which we'll accept connections
    protected ServerSocketChannel serverChannel;
    // The selector we will be monitoring
    private Selector selector;
    // The buffer into which we'll read data when it's available
    private ByteBuffer readBuffer = ByteBuffer.allocate(8192);
    protected Worker worker;

    // This map contains all the clients that are connected
    protected Map<InetSocketAddress, ClientData> clients = new HashMap<>();

    // A list of PendingChange instances
    private List<ChangeRequest> pendingChanges = new LinkedList<>();
    // Maps a SocketChannel to a list of ByteBuffer instances
    private Map<SocketChannel, List<ByteBuffer>> pendingWriteData = new HashMap<>();



    /**
     * Create a client based Network object
     */
    public NioNetwork() throws IOException {
        this(null);
    }

    /**
     * Create a server based Network object
     *
     * @param   localAddress    the address the server will listen on
     */
    public NioNetwork(SocketAddress localAddress) throws IOException {
        this.localAddress = localAddress;
        // init selector
        this.selector = initSelector();
        // init traffic thread
        new Thread(this).start();
    }

    protected abstract Selector initSelector() throws IOException;



    /**
     * Sets the default worker for non System messages.
     *
     * @param   worker  the worker that should handle incoming messages
     */
    public void setDefaultWorker(Worker worker) {
        this.worker = worker;
    }


    /**
     * Connect to a remote Server.
     */
    protected void connect(SocketAddress address) throws IOException {
        logger.fine("Connecting to: " +address);
        // Create a non-blocking socket channel
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.socket().setReuseAddress(true);
        socketChannel.configureBlocking(false);
        // Establish the Connection
        socketChannel.connect(address);

        // Queue a channel registration
        synchronized(this.pendingChanges) {
            pendingChanges.add(new ChangeRequest(socketChannel, ChangeRequest.REGISTER, SelectionKey.OP_CONNECT));
        }
        selector.wakeup();
    }


    public void send(SocketAddress address, Object data) throws IOException{
        send(address, Converter.toBytes(data));
    }

    /**
     * Queues a message to be sent
     *
     * @param   address the target address where the message should be sent
     * @param   data    the data to send
     */
    public void send(SocketAddress address, byte[] data) {
        logger.finest("Sending Queue...");
        SocketChannel socket = getSocketChannel(address);

        // And queue the data we want written
        synchronized (pendingWriteData) {
            List<ByteBuffer> queue = pendingWriteData.get(socket);
            if (queue == null) {
                queue = new ArrayList<>();
                pendingWriteData.put(socket, queue);
            }
            queue.add(ByteBuffer.wrap(data));
        }
        // Changing the key state to write
        synchronized (pendingChanges) {
            // Indicate we want the interest ops set changed
            pendingChanges.add(new ChangeRequest(socket, ChangeRequest.CHANGEOPS, SelectionKey.OP_WRITE));
        }
        // Finally, wake up our selecting thread so it can make the required changes
        selector.wakeup();
    }





    public void run() {
        logger.info("NioNetwork Started.");
        while (selector.isOpen()) {
            try {
                // Handle any pending changes
                synchronized (pendingChanges) {
                    for (ChangeRequest change : pendingChanges) {
                        switch (change.type) {
                            case ChangeRequest.CHANGEOPS:
                                SelectionKey key = change.socket.keyFor(selector);
                                key.interestOps(change.ops);
                                logger.finest("change.ops " + change.ops);
                                break;
                            case ChangeRequest.REGISTER:
                                change.socket.register(selector, change.ops);
                                logger.finest("register socket ");
                                break;
                        }
                    }
                    pendingChanges.clear();
                }

                // Wait for an event from one of the channels
                selector.select();
                logger.finest("selector is awake");

                // Iterate over the set of keys for which events are available
                if (selector.isOpen()) {
                    Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
                    while (selectedKeys.hasNext()) {
                        SelectionKey key = selectedKeys.next();
                        selectedKeys.remove();
                        logger.finest("KeyOP: " + key.interestOps() + "	isAcceptable: " + SelectionKey.OP_ACCEPT + " isConnectible: " + SelectionKey.OP_CONNECT + " isWritable: " + SelectionKey.OP_WRITE + " isReadable: " + SelectionKey.OP_READ);

                        if (key.isValid()) {
                            // Check what event is available and deal with it
                            if (key.isAcceptable()) {
                                logger.finest("Accepting Connection!!");
                                accept(key);
                            } else if (key.isConnectable()) {
                                logger.finest("Establishing Connection!!");
                                establishConnection(key);
                            } else if (key.isWritable()) {
                                logger.finest("Writing");
                                write(key);
                            } else if (key.isReadable()) {
                                logger.finest("Reading");
                                read(key);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        logger.info("Shutting down NioNetwork");
    }

    /**
     * Handle an accept event from a remote host. Channel can only be a server socket.
     */
    private void accept(SelectionKey key) throws IOException {
        // For an accept to be pending the channel must be a server socket channel.
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

        // Accept the connection and make it non-blocking
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.socket().setReuseAddress(true);
        socketChannel.configureBlocking(false);

        // Register the new SocketChannel with our Selector, indicating
        // we'd like to be notified when there's data waiting to be read
        socketChannel.register(selector, SelectionKey.OP_READ);

        // adds the client to the clients list
        registerSocketChannel(socketChannel);
        logger.fine("New Connection(" + socketChannel.getRemoteAddress() + ")!!! Count: " + clients.size());
    }

    /**
     * Finnish an ongoing remote connection establishment procedure
     */
    private void establishConnection(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        try {
            // Finalize/Finish the connection.
            socketChannel.finishConnect();

            // Register an interest in writing on this channel
            key.interestOps(SelectionKey.OP_WRITE);

            registerSocketChannel(socketChannel);
            logger.fine("Connection established(" + socketChannel.getRemoteAddress() + ")");
        } catch (IOException e) {
            // Cancel the channel's registration with our selector
            e.printStackTrace();
            key.cancel();
        }
    }


    /**
     * Writes data pending message into a specific socket defined by the key
     */
    private void write(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        synchronized (pendingWriteData) {
            List<ByteBuffer> queue = pendingWriteData.get(socketChannel);
            if (queue == null) {
                queue = new ArrayList<>();
                pendingWriteData.put(socketChannel, queue);
            }

            // Write until there's no more data ...
            while (!queue.isEmpty()) {
                ByteBuffer buf = queue.get(0);
                socketChannel.write(buf);
                if (buf.remaining() > 0) {
                    logger.finest("Write Buffer Full!");
                    break;
                }
                queue.remove(0);
            }

            if (queue.isEmpty()) {
                // All data written, change selector interest
                logger.finest("No more Data to write!");
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    /**
     * Handle a read event from a socket specified by the key.
     */
    private void read(SelectionKey key) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        SocketAddress remoteAdr = socketChannel.socket().getRemoteSocketAddress();

        // Clear out our read buffer so it's ready for new data
        readBuffer.clear();

        // Attempt to read off the channel
        int numRead;
        try {
            numRead = socketChannel.read(readBuffer);
        } catch (IOException e) {
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            key.cancel();
            socketChannel.close();
            clients.remove(remoteAdr);
            pendingWriteData.remove(socketChannel);
            logger.fine("Connection forcibly closed(" + remoteAdr + ")! Remaining connections: " + clients.size());
            throw new IOException("Remote forcibly closed the connection");
        }

        if (numRead == -1) {
            // Remote entity shut the socket down cleanly. Do the
            // same from our end and cancel the channel.
            key.channel().close();
            key.cancel();
            clients.remove(remoteAdr);
            pendingWriteData.remove(socketChannel);
            logger.fine("Connection Closed(" + remoteAdr + ")! Remaining connections: " + clients.size());
            throw new IOException("Remote closed the connection");
        }

        // Make a correctly sized copy of the data before handing it to the client
        //byte[] rspByteData = new byte[numRead];
        //System.arraycopy(readBuffer.array(), 0, rspByteData, 0, numRead);

        try {
            Object rspData = Converter.toObject(readBuffer.array());

            // Hand the data off to our worker thread
            if (worker != null) {
                logger.finer("Handling incoming message...");
                worker.processData(this, socketChannel.getRemoteAddress(), rspData);
            } else {
                logger.fine("No worker set, message unhandled!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private ClientData registerSocketChannel(SocketChannel socket) {
        InetSocketAddress remoteAdr = (InetSocketAddress) socket.socket().getRemoteSocketAddress();
        if (!clients.containsKey(remoteAdr)) {
            ClientData clientData = new ClientData(socket);
            clients.put(remoteAdr, clientData);
        }
        return clients.get(remoteAdr);
    }
    private SocketChannel getSocketChannel(SocketAddress address) {
        return clients.get(address).getSocketChannel();
    }




    /**
     * Close a specific ongoing connection
     */
    protected void closeConnection(InetSocketAddress address) throws IOException{
        closeConnection(getSocketChannel(address));
    }

    private void closeConnection(SocketChannel socketChannel) throws IOException{
        socketChannel.close();
        socketChannel.keyFor(selector).cancel();
    }

    /**
     * Close all connections
     */
    public void close() throws IOException{
        if (serverChannel != null) {
            serverChannel.close();
            serverChannel.keyFor(selector).cancel();
        }
        clients.clear();
        pendingChanges.clear();
        pendingWriteData.clear();
        selector.close();
    }
}
