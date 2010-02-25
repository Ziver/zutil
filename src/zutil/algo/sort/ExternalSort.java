package zutil.algo.sort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Sort very big files that doesn't fit in ram
 * Inspiration:
 * http://www.codeodor.com/index.cfm/2007/5/14/Re-Sorting-really-BIG-files---the-Java-source-code/1208
 * 
 * @author Ziver
 */
public class ExternalSort {
	public static int CHUNK_SIZE = 100000;

	private BufferedReader in;
	private File sortedFile;

	/**
	 * Creates a ExternalSort object that sort a big file
	 * with minimal use of ram
	 * 
	 * @param orgFile File to sort
	 * @param sortedFile The sorted file
	 * @throws FileNotFoundException
	 */
	public ExternalSort(File orgFile, File sortedFile) throws FileNotFoundException{
		in = new BufferedReader(new FileReader(orgFile));
		this.sortedFile = sortedFile;
	}
	
	/**
	 * Creates a ExternalSort object that sort a big file
	 * with minimal use of ram
	 * 
	 * @param orgFile File to sort
	 * @param sortedFile The sorted file
	 * @param chunk The chunk size
	 * @throws FileNotFoundException
	 */
	public ExternalSort(File orgFile, File sortedFile, int chunk) throws FileNotFoundException{
		in = new BufferedReader(new FileReader(orgFile));
		this.sortedFile = sortedFile;
		CHUNK_SIZE = chunk;
	}

	/**
	 * Sorts the given file
	 * 
	 * @throws IOException Some kind of error
	 */
	public void sort() throws IOException{
		// sorting the chunks
		LinkedList<File> chunkFiles = sortChunks();

		//merging the chunks
		mergeFiles(chunkFiles);

		//removing the chunks
		removeFiles(chunkFiles);
	}

	/**
	 * Merges all the files to one
	 * @param files
	 */
	private void mergeFiles(LinkedList<File> files){
		try	{
			BufferedReader[] chunkReader = new BufferedReader[files.size()];
			String[] rows = new String[files.size()];
			BufferedWriter out = new BufferedWriter(new FileWriter(sortedFile));

			boolean someFileStillHasRows = false;

			for (int i=0; i<files.size(); i++){
				chunkReader[i] = new BufferedReader(new FileReader(files.get(i)));

				// get the first row
				String line = chunkReader[i].readLine();
				if (line != null){
					rows[i] = line;
					someFileStillHasRows = true;
				}
				else{
					rows[i] = null;
				}

			}

			String row;
			while (someFileStillHasRows){
				String min;
				int minIndex = 0;

				row = rows[0];
				if (row!=null) {
					min = row;
					minIndex = 0;
				}
				else {
					min = null;
					minIndex = -1;
				}

				// check which one is minimum
				for(int i=1; i<rows.length ;i++){
					row = rows[i];
					if (min!=null) {
						if(row!=null && row.compareTo(min) < 0){
							minIndex = i;
							min = row;
						}
					}
					else{
						if(row!=null){
							min = row;
							minIndex = i;
						}
					}
				}

				if (minIndex < 0) {
					someFileStillHasRows = false;
				}
				else{
					// write to the sorted file
					out.append(rows[minIndex]);
					out.newLine();

					// get another row from the file that had the min
					String line = chunkReader[minIndex].readLine();
					if (line != null){
						rows[minIndex] = line;
					}
					else{
						rows[minIndex] = null;
					}
				}
				
				// check if one still has rows
				someFileStillHasRows = false;
				for(int i=0; i<rows.length ; i++){
					if(rows[i] != null){
						if (minIndex < 0){
							throw(new IOException("Error sorting!!!"));
						}
						someFileStillHasRows = true;
						break;
					}
				}

				// check the actual files one more time
				if (!someFileStillHasRows){
					//write the last one not covered above
					for(int i=0; i<rows.length ; i++){
						if (rows[i] == null){
							String line = chunkReader[i].readLine();
							if (line != null){
								someFileStillHasRows=true;
								rows[i] = line;
							}
						}
					}
				}
			}
			// close all the files
			out.close();
			for(int i=0; i<chunkReader.length ; i++){
				chunkReader[i].close();
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Sorts the chunk files and returns a LinkedList 
	 * with all the files
	 * @return A linkedList with the files
	 * @throws IOException Some kind of error
	 */
	private LinkedList<File> sortChunks() throws IOException{
		LinkedList<File> chunkFiles = new LinkedList<File>();
		LinkedList<String> chunk = new LinkedList<String>();
		do{
			chunk = readChunk(in);

			//QuickSort.sort(new SortableLinkedList(chunk));
			Collections.sort(chunk);

			File file = new File("extsort"+chunkFiles.size()+".txt");
			chunkFiles.add(file);
			writeChunk(chunk,file);
		}while(!chunk.isEmpty());

		return chunkFiles;
	}

	/**
	 * Reads in a chunk of rows into a LinkedList
	 * 
	 * @param list The list to populate
	 * @param in The BufferedReader to read from
	 * @return The LinkeList with the chunk
	 * @throws IOException Some kind of error
	 */
	private LinkedList<String> readChunk(BufferedReader in) throws IOException{
		LinkedList<String> list = new LinkedList<String>();
		String tmp;
		for(int i=0; i<CHUNK_SIZE ;i++){
			tmp = in.readLine();
			if(tmp == null) break;
			list.add(tmp);
		}		
		return list;
	}

	/**
	 * Writs a chunk of strings to a file
	 * 
	 * @param list The list to write down
	 * @param file The file to write to
	 * @throws IOException Some kind of error
	 */
	private void writeChunk(LinkedList<String> list, File file) throws IOException{
		BufferedWriter out = new BufferedWriter(new FileWriter(file));
		Iterator<String> it = list.iterator();
		while(it.hasNext()){
			out.write(it.next());
			out.newLine();
		}
		out.close();
	}

	private void removeFiles(LinkedList<File> list){
		Iterator<File> it = list.iterator();
		while(it.hasNext()){
			it.next().delete();
		}
	}
}
