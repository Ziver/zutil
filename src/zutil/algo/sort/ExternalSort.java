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

package zutil.algo.sort;

import java.io.*;
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
     *
     * @param   files   a list of files to be merged
     */
    private void mergeFiles(LinkedList<File> files) {
        try	{
            BufferedReader[] chunkReader = new BufferedReader[files.size()];
            String[] rows = new String[files.size()];
            BufferedWriter out = new BufferedWriter(new FileWriter(sortedFile));

            boolean someFileStillHasRows = false;

            for (int i=0; i<files.size(); i++) {
                chunkReader[i] = new BufferedReader(new FileReader(files.get(i)));

                // get the first row
                String line = chunkReader[i].readLine();
                if (line != null) {
                    rows[i] = line;
                    someFileStillHasRows = true;
                }
                else {
                    rows[i] = null;
                }

            }

            String row;
            while (someFileStillHasRows) {
                String min;
                int minIndex;

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
                for (int i=1; i<rows.length; i++) {
                    row = rows[i];
                    if (min != null) {
                        if (row != null && row.compareTo(min) < 0) {
                            minIndex = i;
                            min = row;
                        }
                    }
                    else {
                        if (row != null) {
                            min = row;
                            minIndex = i;
                        }
                    }
                }

                if (minIndex < 0) {
                    someFileStillHasRows = false;
                }
                else {
                    // write to the sorted file
                    out.append(rows[minIndex]);
                    out.newLine();

                    // get another row from the file that had the min
                    String line = chunkReader[minIndex].readLine();
                    if (line != null) {
                        rows[minIndex] = line;
                    }
                    else {
                        rows[minIndex] = null;
                    }
                }

                // check if one still has rows
                someFileStillHasRows = false;
                for (int i=0; i<rows.length; i++) {
                    if (rows[i] != null) {
                        if (minIndex < 0) {
                            throw new IOException("Error sorting!!!");
                        }
                        someFileStillHasRows = true;
                        break;
                    }
                }

                // check the actual files one more time
                if (!someFileStillHasRows) {
                    //write the last one not covered above
                    for (int i=0; i<rows.length; i++) {
                        if (rows[i] == null) {
                            String line = chunkReader[i].readLine();
                            if (line != null) {
                                someFileStillHasRows = true;
                                rows[i] = line;
                            }
                        }
                    }
                }
            }
            // close all the files
            out.close();
            for (int i=0; i<chunkReader.length; i++) {
                chunkReader[i].close();
            }
        }
        catch (Exception ex) {
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
        LinkedList<File> chunkFiles = new LinkedList<>();
        LinkedList<String> chunk;
        do{
            chunk = readChunk(in);

            //QuickSort.sort(new SortableLinkedList(chunk));
            Collections.sort(chunk);

            File file = new File("extsort" + chunkFiles.size() + ".txt");
            chunkFiles.add(file);
            writeChunk(chunk,file);
        }while (!chunk.isEmpty());

        return chunkFiles;
    }

    /**
     * Reads in a chunk of rows into a LinkedList
     *
     * @param in The BufferedReader to read from
     * @return a LinkedList with the chunks
     */
    private LinkedList<String> readChunk(BufferedReader in) throws IOException{
        LinkedList<String> list = new LinkedList<>();
        String tmp;
        for (int i=0; i<CHUNK_SIZE; i++) {
            tmp = in.readLine();
            if (tmp == null) break;
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
        for (String str : list) {
            out.write(str);
            out.newLine();
        }
        out.close();
    }

    private void removeFiles(LinkedList<File> list) {
        for (File file : list) {
            file.delete();
        }
    }
}
