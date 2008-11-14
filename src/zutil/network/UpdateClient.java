package zutil.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import zutil.FileFinder;
import zutil.MultiPrintStream;
import zutil.ProgressListener;

/**
 * This class connects to a update server and updates a path
 * with the servers
 * 
 * @author Ziver
 *
 */
public class UpdateClient{
	private ArrayList<FileHash> clientFileList;
	private Socket socket;
	private String path;
	private ProgressListener progress;
	private int speed;
	private long totalReceived;

	/**
	 * Creates a UpdateClient
	 * 
	 * @param address Address to the UpdateServer
	 * @param port The port on the server
	 * @param path Path to the files to update
	 * @throws Exception
	 */
	public UpdateClient(String address, int port, String path) throws Exception{
		clientFileList = UpdateServer.getFileList(path);
		socket = new Socket(address, port);
		this.path = path;
	}
	
	public void setProgressListener(ProgressListener p){
		progress = p;
	}

	/**
	 * Updates the files
	 * 
	 * @throws Exception
	 */
	public void update() throws Exception{
		ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
		ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		
		// send client file list
		out.writeObject(clientFileList);
		out.flush();
		
		// receive file updates
		FileHash fileInfo = (FileHash)in.readObject();
		File tmpPath = FileFinder.find(path);
		while(!fileInfo.path.isEmpty()){
			MultiPrintStream.out.println("Updating: "+path+fileInfo.path);
			// reading new file data
			File file = new File(tmpPath.getAbsolutePath()+fileInfo.path);
			File tmpFile = File.createTempFile(file.getName(), ".tmp", tmpPath);
			tmpFile.getParentFile().mkdirs();
			tmpFile.deleteOnExit();
			
			FileOutputStream fileOut = new FileOutputStream(tmpFile);
            byte[] buffer = new byte[socket.getReceiveBufferSize()];
            
            int bytesReceived = 0;
            totalReceived = 0;            
            long time = System.currentTimeMillis();
            long timeTotalRecived = 0;
            
            while((bytesReceived = in.read(buffer)) > 0) {
            	fileOut.write(buffer, 0, bytesReceived);
            	
            	if(time+1000 < System.currentTimeMillis()){
            		time = System.currentTimeMillis();
            		speed = (int)(totalReceived - timeTotalRecived);
            		timeTotalRecived = totalReceived;
            	}
            	
            	totalReceived += bytesReceived;
            	if(progress != null) progress.progressUpdate(this, fileInfo, (double)totalReceived/fileInfo.size*100);
            }
            fileOut.close();
            speed = 0;
            
            // delete old file and replace whit new
            file.delete();
            if(!tmpFile.renameTo(file)){
            	throw new Exception("Cannot update file: "+file.getAbsolutePath());
            }
            // read new message
            fileInfo = (FileHash)in.readObject();
		}
		
		MultiPrintStream.out.println("Update Done!!");
	}
	
	/**
	 * Returns the speed of the transfer
	 * 
	 * @return The speed in bytes/s
	 */
	public int speed(){
		return speed;
	}
	
	/**
	 * Returns the total amount of data received for the
	 * current file
	 * 
	 * @return The speed in bytes/s
	 */
	public long totalReceived(){
		return totalReceived;
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
