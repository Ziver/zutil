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

package zutil.net.threaded;

import zutil.log.LogUtil;

import javax.net.ssl.SSLServerSocketFactory;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A simple network server that handles TCP communication
 * 
 * @author Ziver
 */
public abstract class ThreadedTCPNetworkServer extends Thread{
    private static final Logger logger = LogUtil.getLogger();

    private final int port;
    private Executor executor;
    private File keyStore;
    private String keyStorePass;

    /**
     * Creates a new instance of the sever
     *
     * @param 	port 		The port that the server should listen to
     */
    public ThreadedTCPNetworkServer(int port){
        this(port, null, null);
    }
    /**
     * Creates a new instance of the sever
     *
     * @param 	port 		    The port that the server should listen to
     * @param 	keyStore 	    If this is not null then the server will use SSL connection with this keyStore file path
     * @param 	keyStorePass 	If this is not null then the server will use a SSL connection with the given certificate
     */
    public ThreadedTCPNetworkServer(int port, File keyStore, String keyStorePass){
        this.port = port;
        executor = Executors.newCachedThreadPool();
        this.keyStorePass = keyStorePass;
        this.keyStore = keyStore;
    }


    public void run(){
        ServerSocket serverSocket = null;
        try{
            if(keyStorePass != null && keyStore != null){
                registerCertificate(keyStore, keyStorePass);
                serverSocket = initSSL( port );
            }
            else{
                serverSocket = new ServerSocket( port );
            }
            logger.info("Listening for TCP Connections on port: "+port);

            while(true){
                Socket connectionSocket = serverSocket.accept();
                ThreadedTCPNetworkServerThread thread = getThreadInstance( connectionSocket );
                if( thread!=null ) {
                    executor.execute(thread);
                }
                else{
                    logger.severe("Unable to instantiate ThreadedTCPNetworkServerThread, closing connection!");
                    connectionSocket.close();
                }
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE, null, e);
        } finally {
            if( serverSocket!=null ){
                try{
                    serverSocket.close();
                }catch(IOException e){ logger.log(Level.SEVERE, null, e); }
            }
        }
    }

    /**
     * This method returns an new instance of the ThreadedTCPNetworkServerThread
     * that will handle the newly made connection, if an null value is returned
     * then the ThreadedTCPNetworkServer will close the new connection.
     *
     * @param 		s 		is an new connection to an host
     * @return 				a new instance of an thread or null
     */
    protected abstract ThreadedTCPNetworkServerThread getThreadInstance( Socket s ) throws IOException;

    /**
     * Initiates a SSLServerSocket
     *
     * @param 		port 	The port to listen to
     * @return 				The SSLServerSocket
     */
    private ServerSocket initSSL(int port) throws IOException{
        SSLServerSocketFactory sslserversocketfactory =
            (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        return sslserversocketfactory.createServerSocket(port);

    }

    /**
     * Registers the given cert file to the KeyStore
     *
     * @param 		keyStore 	The cert file
     */
    protected void registerCertificate(File keyStore, String keyStorePass) throws CertificateException, IOException, KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException{
        System.setProperty("javax.net.ssl.keyStore", keyStore.getAbsolutePath());
        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePass);
    }

    /**
     * Stops the server and interrupts its internal thread.
     * This is a permanent action that will not be able to recover from
     */
    public void close(){
        this.interrupt();
    }
}
