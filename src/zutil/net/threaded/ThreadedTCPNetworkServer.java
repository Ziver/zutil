package zutil.net.threaded;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;

import javax.net.ssl.SSLServerSocketFactory;

import zutil.io.MultiPrintStream;


/**
 * A simple web server that handles both cookies and
 * sessions for all the clients
 * 
 * @author Ziver
 */
public abstract class ThreadedTCPNetworkServer extends Thread{
	public final int port;
	private File keyStore;
	private String keyStorePass;

	/**
	 * Creates a new instance of the sever
	 * 
	 * @param port The port that the server should listen to
	 */
	public ThreadedTCPNetworkServer(int port){
		this(port, null, null);
	}
	/**
	 * Creates a new instance of the sever
	 * 
	 * @param port The port that the server should listen to
	 * @param sslCert If this is not null then the server will use SSL connection with this keyStore file path
	 * @param sslCert If this is not null then the server will use a SSL connection with the given certificate
	 */
	public ThreadedTCPNetworkServer(int port, File keyStore, String keyStorePass){	
		this.port = port;
		this.keyStorePass = keyStorePass;
		this.keyStore = keyStore;
	}


	public void run(){
		ServerSocket ss = null;
		try{
			if(keyStorePass != null && keyStore != null){
				registerCertificate(keyStore, keyStorePass);
				ss = initSSL( port );
			}
			else{
				ss = new ServerSocket( port );
			}

			while(true){
				Socket s = ss.accept();
				ThreadedTCPNetworkServerThread t = getThreadInstance( s );
				if( t!=null )
					new Thread( t ).start();
				else{
					MultiPrintStream.out.println("Unable to instantiate ThreadedTCPNetworkServerThread, closing connection!");
					s.close();
				}
			}
		} catch(Exception e) {
			e.printStackTrace( MultiPrintStream.out );
		}
		
		if( ss!=null ){
			try{
				ss.close();
			}catch(IOException e){ e.printStackTrace( MultiPrintStream.out ); }
		}
	}

	/**
	 * This method returns an new instance of the ThreadedTCPNetworkServerThread
	 * that will handle the newly made connection, if an null value is returned
	 * then the ThreadedTCPNetworkServer will close the new connection.
	 * 
	 * @param s is an new connection to an host
	 * @return a new instance of an thread or null
	 */
	protected abstract ThreadedTCPNetworkServerThread getThreadInstance( Socket s );

	/**
	 * Initiates a SSLServerSocket
	 * 
	 * @param port The port to listen to
	 * @return The SSLServerSocket
	 * @throws IOException
	 */
	private ServerSocket initSSL(int port) throws IOException{
		SSLServerSocketFactory sslserversocketfactory =
			(SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		return sslserversocketfactory.createServerSocket(port);

	}

	/**
	 * Registers the given cert file to the KeyStore
	 * 
	 * @param certFile The cert file
	 */
	protected void registerCertificate(File keyStore, String keyStorePass) throws CertificateException, IOException, KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException{
		System.setProperty("javax.net.ssl.keyStore", keyStore.getAbsolutePath());
		System.setProperty("javax.net.ssl.keyStorePassword", keyStorePass);
	}
}