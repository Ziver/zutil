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

package zutil.net.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import zutil.ProgressListener;
import zutil.io.file.FileUtil;
import zutil.log.LogUtil;

/**
 * This class connects to a update server and updates a path
 * with the servers
 * 
 * @author Ziver
 *
 */
public class UpdateClient{
	private static final Logger logger = LogUtil.getLogger();

	private String path;
	private Socket socket;
	private FileListMessage fileList;

	private long speed;
	private long totalReceived;
	private long expectedSize;
	private ProgressListener<UpdateClient,FileInfo> progress;

	/**
	 * Creates a UpdateClient
	 * 
	 * @param address Address to the UpdateServer
	 * @param port The port on the server
	 * @param path Path to the files to update
	 * @throws Exception
	 */
	public UpdateClient(String address, int port, String path) throws Exception{
		fileList = new FileListMessage(path);
		socket = new Socket(address, port);
		this.path = path;
	}

	public void setProgressListener(ProgressListener<UpdateClient,FileInfo> p){
		progress = p;
	}

	/**
	 * Updates the files
	 */
	public void update() throws IOException{
		try{
			ObjectOutputStream out = new ObjectOutputStream( socket.getOutputStream());
			ObjectInputStream  in  = new ObjectInputStream ( socket.getInputStream() );

			// send client file list
			out.writeObject( fileList );
			out.flush();
			// get update list
			FileListMessage updateList = (FileListMessage) in.readObject();
			expectedSize = updateList.getTotalSize();

			// receive file updates
			File tmpPath = FileUtil.find(path);
			totalReceived = 0;
			for(FileInfo info : updateList.getFileList() ){
				// reading new file data
				File file = new File( tmpPath, info.getPath() );
				logger.fine("Updating file: "+file);
				if( !file.getParentFile().exists() && !file.getParentFile().mkdirs() ){
					throw new IOException("Unable to create folder: "+file.getParentFile());
				}
				File tmpFile = File.createTempFile(file.getName(), ".tmp", tmpPath);				
				tmpFile.deleteOnExit();

				FileOutputStream fileOut = new FileOutputStream(tmpFile);
				byte[] buffer = new byte[socket.getReceiveBufferSize()];

				long bytesReceived = 0;
				int byteRead = 0;
				long time = System.currentTimeMillis();
				long timeTotalRecived = 0;

				while( bytesReceived < info.getSize() ) {
					byteRead = in.read(buffer);
					fileOut.write(buffer, 0, byteRead);
					bytesReceived += byteRead;

					if(time+1000 < System.currentTimeMillis()){
						time = System.currentTimeMillis();
						speed = (int)(totalReceived - timeTotalRecived);
						timeTotalRecived = totalReceived;
					}

					totalReceived += byteRead;
					if(progress != null) progress.progressUpdate(this, info, ((double)totalReceived/updateList.getTotalSize())*100);
				}
				fileOut.close();
				speed = 0;

				// delete old file and replace whit new
				file.delete();
				if( !tmpFile.renameTo(file) ){
					throw new IOException("Can not move downloaded file: "+tmpFile.getAbsolutePath()+" to: "+file);
				}
			}
		}catch(ClassNotFoundException e){
			logger.log(Level.SEVERE, null, e);
		}

		logger.info("Update done.");
	}

	/**
	 * Returns the speed of the transfer
	 * 
	 * @return The speed in bytes/s
	 */
	public long getSpeed(){
		return speed;
	}

	/**
	 * Returns the total amount of data received
	 * 
	 * @return a long that represents bytes
	 */
	public long getTotalReceived(){
		return totalReceived;
	}
	
	/**
	 * Returns the expected total amount of data that will be received
	 * 
	 * @return a long that represents bytes
	 */
	public long getTotalSize(){
		return expectedSize;
	}

	/**
	 * Closes the connection
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException{
		socket.close();
	}
}
