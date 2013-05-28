/*******************************************************************************
 * Copyright (c) 2013 Ziver
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import zutil.log.LogUtil;


public class NetLogClient extends Thread{
	private static final Logger logger = LogUtil.getLogger();
	
	private ConcurrentLinkedQueue<NetLogListener> listeners;
	private Socket s;
	private ObjectOutputStream out;

	public NetLogClient(String host, int port) throws UnknownHostException, IOException{
		s = new Socket(host, port);
		out = new ObjectOutputStream(s.getOutputStream());
		listeners = new ConcurrentLinkedQueue<NetLogListener>();
		this.start();
	}

	public void addListener(NetLogListener listener){
		logger.info("Registring new NetLogListener: "+listener.getClass().getName());
		listeners.add( listener );
	}

	public void run(){
		try{
			ObjectInputStream in = new ObjectInputStream(s.getInputStream());
			while( true ){
				Object o = in.readObject();

				for( NetLogListener listener : listeners ){
					if( o instanceof NetLogMessage )
						listener.handleLogMessage((NetLogMessage)o);
					else if( o instanceof NetLogExceptionMessage )
						listener.handleExceptionMessage((NetLogExceptionMessage)o);
					else if( o instanceof NetLogStatusMessage )
						listener.handleStatusMessage((NetLogStatusMessage)o);
					else
						logger.warning("Received unknown message: "+o.getClass().getName());
				}
			}
		} catch( Exception e ){
			logger.log(Level.SEVERE, null, e);
			close();
		}
	}



	public void close(){
		try{
			this.interrupt();
			s.close();
		} catch (Exception e){
			logger.log(Level.SEVERE, "Unable to close Client Socket.", e);
		}
	}
}
