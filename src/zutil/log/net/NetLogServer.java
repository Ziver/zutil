/*******************************************************************************
 * Copyright (c) 2011 Ziver Koc
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
 ******************************************************************************/
package zutil.log.net;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import zutil.log.LogUtil;
import zutil.net.nio.message.Message;
import zutil.net.threaded.ThreadedTCPNetworkServer;
import zutil.net.threaded.ThreadedTCPNetworkServerThread;


public class NetLogServer extends Handler {
	private static final Logger logger = LogUtil.getLogger();
	
	private NetLogNetwork net;

	/**
	 * @param 		port		the port the server will listen on
	 */
	public NetLogServer(int port) {
		super();
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
			net.sendMessage( exception );
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
				thread.queueMessage( log );
			}
		}

		@Override
		protected ThreadedTCPNetworkServerThread getThreadInstance(Socket s) {
			try {
				NetLogServerThread thread = new NetLogServerThread(s);
				logger.info("Client connection from: "+s.getInetAddress());
				threads.add( thread );
				return thread;
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Unable to start Client thread", e);
			}
			return null;
		}


		class NetLogServerThread implements ThreadedTCPNetworkServerThread{
			private Queue<Message> queue;
			private ObjectOutputStream out;
			private Socket s;

			public NetLogServerThread(Socket s) throws IOException{
				queue = new LinkedList<Message>();
				this.s = s;
				out = new ObjectOutputStream( s.getOutputStream() );
			}

			public void queueMessage(Message log){
				synchronized(queue){
					queue.add( log );
					queue.notify();
				}
			}

			public void run() {
				try {
					while( true ){
						synchronized(queue){
							while( !queue.isEmpty() ){
								Message msg = queue.poll();
								out.writeObject( msg );
							}
							queue.wait();
						}
					}
				} catch (Exception e) {
					logger.log(Level.SEVERE, null, e);
				} finally {
					this.close();
				}
			}


			public void close(){
				try {
					out.close();
					s.close();
					threads.remove(this);
					queue = null;
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Unable to close Client Socket", e);
				}
			}
		}
	}
}
