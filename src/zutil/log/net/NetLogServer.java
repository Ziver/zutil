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

package zutil.log.net;

import zutil.log.LogUtil;
import zutil.net.nio.message.Message;
import zutil.net.threaded.ThreadedTCPNetworkServer;
import zutil.net.threaded.ThreadedTCPNetworkServerThread;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


public class NetLogServer extends Handler {
	private static final Logger logger = LogUtil.getLogger();

	private NetLogNetwork net;
	private ConcurrentHashMap<NetLogExceptionMessage,NetLogExceptionMessage> exceptions;

	/**
	 * @param 		port		the port the server will listen on
	 */
	public NetLogServer(int port) {
		super();
		exceptions = new ConcurrentHashMap<NetLogExceptionMessage,NetLogExceptionMessage>();
		net = new NetLogNetwork(port);
		net.start();
	}


	public void publish(LogRecord record) {
		// ensure that this log record should be logged by this Handler
		if (!isLoggable(record))
			return;

		// Output the formatted data to the file
		if(record.getThrown() != null){
			NetLogExceptionMessage exception = new NetLogExceptionMessage(record);
			if(!exceptions.containsKey(exception)){
				logger.finest("Received new exception: "+exception);
				exceptions.put(exception, exception);
				net.sendMessage( exception );
			}
			else{
				exception = exceptions.get(exception);
				exception.addCount(1);
				logger.finest("Received known exception(Count: "+exception.getCount()+"): "+exception);
				net.sendMessage( exception );
			}
		}
		else{
			NetLogMessage log = new NetLogMessage(record);
			net.sendMessage( log );
		}
	}

	public void flush() {}

	public void close() {
		net.close();
	}


	class NetLogNetwork extends ThreadedTCPNetworkServer{
		private ConcurrentLinkedQueue<NetLogServerThread> threads;

		public NetLogNetwork(int port) {
			super(port);
			threads = new ConcurrentLinkedQueue<NetLogServerThread>();
		}

		public void sendMessage(Message log){
			for( NetLogServerThread thread : threads ){
				thread.sendMessage( log );
			}
		}

		@Override
		protected ThreadedTCPNetworkServerThread getThreadInstance(Socket s) {
			try {
				NetLogServerThread thread = new NetLogServerThread(s);
				return thread;
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Unable to start Client thread", e);
			}
			return null;
		}


		class NetLogServerThread implements ThreadedTCPNetworkServerThread{
			private ObjectOutputStream out;
			private ObjectInputStream in;
			private Socket s;

			public NetLogServerThread(Socket s) throws IOException{
				this.s = s;
				logger.info("Client connected: "+s.getInetAddress());
				out = new ObjectOutputStream( s.getOutputStream() );
				in = new ObjectInputStream( s.getInputStream() );
				
				sendAllExceptions();
				threads.add( this );
			}

			public void sendMessage(Message msg){
				try {
					out.writeObject( msg );
					out.reset();
				} catch (Exception e) {
					this.close();
					logger.log(Level.SEVERE, "Unable to send message to client: "+s.getInetAddress(), e);
				}
			}

			public void sendAllExceptions(){
				logger.fine("Sending all exceptions to client: "+s.getInetAddress());
				for(NetLogExceptionMessage e : exceptions.values())
					sendMessage(e);
			}

			public void run() {
				try {
					while( true ){
						in.readObject();
					}
				} catch (Exception e) {
					logger.log(Level.SEVERE, null, e);
				} finally {
					this.close();
				}
			}


			public void close(){
				try {
					threads.remove(this);
					logger.info("Client disconnected: "+s.getInetAddress());					
					out.close();
					s.close();
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Unable to close Client Socket", e);
				}
			}
		}
	}
}
