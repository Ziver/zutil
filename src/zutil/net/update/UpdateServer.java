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
package zutil.net.update;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import zutil.io.MultiPrintStream;
import zutil.log.LogUtil;
import zutil.net.threaded.ThreadedTCPNetworkServer;
import zutil.net.threaded.ThreadedTCPNetworkServerThread;

public class UpdateServer extends ThreadedTCPNetworkServer{
	public static final Logger logger = LogUtil.getLogger();

	private FileListMessage fileList;

	/**
	 * Creates a UpdateServer Thread
	 * 
	 * @param path The path to sync the clients with
	 */
	public UpdateServer(int port, String path) throws Exception{
		super(port);
		fileList = new FileListMessage(path);
		MultiPrintStream.out.dump(fileList);
		
		this.start();
		logger.info("Update Server Ready.");
	}

	@Override
	protected ThreadedTCPNetworkServerThread getThreadInstance(Socket s) {
		return new UpdateServerThread(s);
	}

	/**
	 * Handles all the connecting clients
	 * 
	 * @author Ziver
	 */
	class UpdateServerThread implements ThreadedTCPNetworkServerThread{
		private ObjectOutputStream out;
		private ObjectInputStream in;
		private Socket socket;

		/**
		 * Creates a UpdateServerThread
		 * 
		 * @param 		client 		is the socket to the client
		 */
		public UpdateServerThread(Socket c){
			socket = c;
			try {
				out = new ObjectOutputStream( socket.getOutputStream());
				in  = new ObjectInputStream ( socket.getInputStream() );
			} catch (IOException e) {
				logger.log(Level.SEVERE, null, e);
			}

		}

		public void run(){
			try {
				logger.info("Client["+socket.getInetAddress()+"] connectiong...");
				// receive the clients file list
				FileListMessage clientFileList = (FileListMessage)in.readObject();
				MultiPrintStream.out.dump(clientFileList);
				FileListMessage diff = fileList.getDiff( clientFileList );
				MultiPrintStream.out.dump(diff);
				out.writeObject( diff );

				logger.info("Updating client["+socket.getInetAddress()+"]...");
				for(FileInfo info : diff.getFileList()){
					// send file data
					FileInputStream input = new FileInputStream( info.getFile() );
					byte[] nextBytes = new byte[ socket.getSendBufferSize() ];
					int bytesRead = 0;
					while((bytesRead = input.read(nextBytes)) > 0){
						out.write(nextBytes,0,bytesRead);
					}
					out.flush();
					input.close();
				}
				
				out.flush();
				socket.close();
				logger.info("Client["+socket.getInetAddress()+"] update done.");
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Update error Client["+socket.getInetAddress()+"].", e);
			} finally {
				logger.info("Client["+socket.getInetAddress()+"] disconnected.");
			}
		}
	}
}


