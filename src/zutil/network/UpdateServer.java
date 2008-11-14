package zutil.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import zutil.FileFinder;
import zutil.Hasher;
import zutil.MultiPrintStream;

public class UpdateServer extends Thread{
	private ArrayList<FileHash> fileList;
	private ServerSocket server;
	private boolean close;
	private String path;

	/**
	 * Creates a UpdateServer Thread
	 * 
	 * @param path The path to sync the clients with
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws NoSuchAlgorithmException 
	 */
	public UpdateServer(int port, String path) throws Exception{
		fileList = getFileList(path);
		server = new ServerSocket(port);
		close = false;
		this.path = path;

		this.start();
		MultiPrintStream.out.println("Update Server Online!!!");
	}

	public void run(){
		while (!close){
			try {
				new UpdateServerThread(server.accept()).start();
				MultiPrintStream.out.println("Update Server: Client Connected!!!");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handles all the connecting clients
	 * 
	 * @author Ziver
	 */
	class UpdateServerThread extends Thread{
		private ObjectOutputStream out;
		private ObjectInputStream in;
		private Socket client;

		/**
		 * Creates a UpdateServerThread
		 * @param client The socket to the client
		 * @throws IOException 
		 */
		public UpdateServerThread(Socket c) throws IOException {
			client = c;
			out = new ObjectOutputStream(client.getOutputStream());
			in = new ObjectInputStream(client.getInputStream());
		}

		@SuppressWarnings("unchecked")
		public void run(){
			try {
				// receive the clients filelist
				ArrayList<FileHash> clientFileList = (ArrayList<FileHash>)in.readObject();
				File tmpPath = FileFinder.find(path);

				for(FileHash file : fileList){
					if(!clientFileList.contains(file)){
						// send new file to client
						out.writeObject(file);
						out.flush();

						// send file data
						FileInputStream input = new FileInputStream(tmpPath.getAbsolutePath()+file.path);
						byte[] nextBytes = new byte[client.getSendBufferSize()];
						int bytesRead = 0;
						while((bytesRead = input.read(nextBytes)) > 0){
							out.write(nextBytes,0,bytesRead);
						}
					}
				}
				
				// send update done message
				out.writeObject(new FileHash("","",0));
				out.flush();
				
				out.close();
				in.close();
				client.close();
			} catch (Exception e) {
				MultiPrintStream.out.println("Update Server: Client Error!!! "+e.getMessage());
			} finally {
				MultiPrintStream.out.println("Update Server: Client Update Done!!!");
			}
		}
	}

	/**
	 * Returns a ArrayList with all the files in the specified folder and there 
	 * MD5 hashes
	 * 
	 * @param path The path to search
	 * @return A ArrayList with all the files in the path
	 * @throws URISyntaxException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static ArrayList<FileHash> getFileList(String path) throws Exception{
		ArrayList<FileHash> fileHash = new ArrayList<FileHash>();

		ArrayList<File> files = FileFinder.search(FileFinder.find(path));
		for(File file : files){
			fileHash.add(new FileHash(
					FileFinder.relativePath(file, path),
					Hasher.hash(file, "MD5"),
					file.length()));
		}

		return fileHash;
	}
}

/**
 * This class is used to store the files
 * and there hashes
 * 
 * @author Ziver
 */
class FileHash implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public String path;
	public String hash;
	public long size;

	public FileHash(String p, String h, long s){
		path = p;
		hash = h;
		size = s;
	}

	public boolean equals(Object comp){
		FileHash tmp = (FileHash)comp;
		return path.equals(tmp.path) && hash.equals(tmp.hash);
	}
	
	public String toString(){
		return path;
	}
}
