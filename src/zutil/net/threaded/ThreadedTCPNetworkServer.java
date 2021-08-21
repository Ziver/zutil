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

import zutil.log.LogUtil;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.cert.Certificate;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A simple network server that handles TCP communication
 *
 * @author Ziver
 */
public abstract class ThreadedTCPNetworkServer extends Thread {
    private static final Logger logger = LogUtil.getLogger();

    private Executor executor = Executors.newCachedThreadPool();
    private final int port;
    private ServerSocket serverSocket;

    /**
     * Creates a new instance of the sever.
     *
     * @param 	port 		the port that the server should listen to
     */
    public ThreadedTCPNetworkServer(int port) throws IOException {
        this.port = port;
        this.serverSocket = ServerSocketFactory.getDefault().createServerSocket(port);
    }
    /**
     * Creates a new SSL instance of the sever.
     *
     * @param   port            the port that the server should listen to.
     * @param   certificate     the certificate for the server domain.
     */
    public ThreadedTCPNetworkServer(int port, Certificate certificate) throws IOException, GeneralSecurityException {
        this(port, getKeyStore(certificate), null);
    }
    /**
     * Creates a new SSL instance of the sever.
     *
     * @param   port            the port that the server should listen to.
     * @param   keyStoreFile    the path to the key store file containing the server certificates
     * @param   keyStorePass    the password to decrypt the key store file.
     */
    public ThreadedTCPNetworkServer(int port, File keyStoreFile, char[] keyStorePass) throws IOException, GeneralSecurityException {
        this(port, getKeyStore(keyStoreFile, keyStorePass), keyStorePass);
    }
    /**
     * Creates a new SSL instance of the sever.
     *
     * @param   port            the port that the server should listen to.
     * @param   keyStore        the KeyStore that contains the certificate to be used by the server
     * @param   keyStorePass    the password to decrypt the key store file, null if there is no password set
     */
    public ThreadedTCPNetworkServer(int port, KeyStore keyStore, char[] keyStorePass) throws IOException, GeneralSecurityException {
        this.port = port;
        this.serverSocket = getSSLServerSocketFactory(keyStore, keyStorePass).createServerSocket(port);
    }

    /**
     * Initiates a SSLServerSocket
     *
     * @param   certificate     the certificate for the server domain.
     * @return a SSLServerSocket object
     */
    private static KeyStore getKeyStore(Certificate certificate) throws IOException, GeneralSecurityException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null); // Create empty keystore
        keyStore.setCertificateEntry("ssl_server_cert", certificate);

        return keyStore;
    }
    /**
     * Initiates a SSLServerSocket
     *
     * @param   keyStoreFile    the cert file location
     * @param   keyStorePass    the password for the cert file, null if there is no password set
     * @return a SSLServerSocket object
     */
    private static KeyStore getKeyStore(File keyStoreFile, char[] keyStorePass) throws IOException, GeneralSecurityException {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(new FileInputStream(keyStoreFile), keyStorePass);

        return keyStore;
    }

    /**
     * Creates a new SSLServerSocketFactory
     *
     * @param   keyStore        the key store containing the domain certificates
     * @param   keyStorePass    the password for the cert file, null if there is no password set
     * @return a SSLServerSocketFactory object
     */
    private static SSLServerSocketFactory getSSLServerSocketFactory(KeyStore keyStore, char[] keyStorePass)
            throws NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, KeyManagementException {
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, keyStorePass);

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(keyManagerFactory.getKeyManagers(), null, SecureRandom.getInstanceStrong());
        return ctx.getServerSocketFactory();
    }


    /**
     * @return the port that this TCP server is listening to.
     */
    public int getPort() {
        return port;
    }

    public void run() {
        try {
            logger.info("Accepting TCP Connections on port: " + port);

            while (true) {
                Socket connectionSocket = serverSocket.accept();
                ThreadedTCPNetworkServerThread thread = getThreadInstance(connectionSocket);

                if (thread != null) {
                    executor.execute(thread);
                } else {
                    logger.severe("Unable to instantiate ThreadedTCPNetworkServerThread, closing connection!");
                    connectionSocket.close();
                }
            }
        } catch(Exception e) {
            logger.log(Level.SEVERE, null, e);
        } finally {
            if (serverSocket != null) {
                try {
                    logger.info("Closing TCP socket listener (Port: " + port + ").");
                    serverSocket.close();
                    serverSocket = null;
                } catch(IOException e) { logger.log(Level.SEVERE, null, e); }
            }
        }
    }

    /**
     * This method returns an new instance of the ThreadedTCPNetworkServerThread
     * that will handle the newly made connection, if an null value is returned
     * then the ThreadedTCPNetworkServer will close the new connection.
     *
     * @param   socket   is an new connection to an host
     * @return a new instance of an thread or null
     */
    protected abstract ThreadedTCPNetworkServerThread getThreadInstance(Socket socket) throws IOException;

    /**
     * Stops the server and interrupts its internal thread.
     * This is a permanent action that will not be able to recover from
     */
    public void close() {
        this.interrupt();
    }
}
