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

package zutil.io.file;

import zutil.StringUtil;
import zutil.io.IOUtil;
import zutil.log.LogUtil;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * File path utilities
 *
 * @author Ziver
 */
public class FileUtil {
    private static final Logger logger = LogUtil.getLogger();

    /**
     * Returns a String with a relative path from the given path
     *
     * @param 		file 		is the file to get a relative path from
     * @param 		path 		is the path
     * @return 					A String with a relative path
     */
    public static String relativePath(File file, String path){
        if(file == null || path == null)
            return null;

        String absolute = file.getAbsolutePath();
        String tmpPath = path.replaceAll(
                "[/\\\\]",
                Matcher.quoteReplacement(File.separator));

        String relative = absolute.substring(
                absolute.indexOf(tmpPath)+path.length());
        return relative;
    }

    /**
     * Returns the File object for the given file.
     * Can not point to files in JAR files.
     * This method might not be able to find files inside
     * recursive archives, in this case {@link #getContent(String)}.
     *
     * @param 		path 		is the path to the file (no / if not absolute path)
     * @return 					A File object for the file
     */
    public static File find(String path){
        try {
            File file = new File(path);
            if(file.exists())
                return file;

            URL url = findURL(path);
            if(url != null && "file".equals(url.getProtocol()))
                return new File(url.toURI());
        } catch (Exception e) {
            logger.log(Level.FINE, "Unable to find file: " + path, e);
        }
        return null;
    }

    /**
     * Copy the contents of a source file to another file.
     * NOTE: the method will replace the destination file if it exists.
     */
    public static void copy(File source, File destination) throws IOException{
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destination));){

            IOUtil.copyStream(in, out);
        }
    }

    /**
     * Returns a nonexistent file that has the same name as the
     * provided file but appended with a number sequence.
     *
     * @param   file    is the original file or subsequent files returned by this method
     * @return a new File object with four numbers.
     *          First call will return {FILE_NAME}.0001 and each
     *          subsequent call will return a incremented the number
     *          if the previous file was created.
     */
    public static File getNextFile(File file){
        for(int i = 1; i<10000; ++i){
            File next = new File(file.getParentFile(), file.getName() + "." + StringUtil.prefixInt(i, 4));
            if(!next.exists())
                return next;
        }
        return null;
    }

    /**
     * Returns the URL to the given file
     *
     * @param 		path 		is the path to the file (no / if not absolute path)
     * @return					A URL object for the file
     */
    public static URL findURL(String path){
        return Thread.currentThread().getContextClassLoader().getResource(path);
    }

    /**
     * Returns a InputStream from the path
     *
     * @param 		path 		is the path to the file (no / if not absolute path)
     * @return 					A InputStream object for the file
     */
    public static InputStream getInputStream(String path){
        try {
            File file = new File(path);
            if(file.exists())
                return new BufferedInputStream(new FileInputStream(file));

            return Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(path);
        } catch (Exception e) {
            logger.log(Level.FINE, "Unable to find file: " + path, e);
        }
        return null;
    }


    /**
     * Reads and returns the content of a file as a String.
     * This method can read files inside compressed archives also.
     */
    public static String getContent(String file) throws IOException {
        return new String(getByteContent(file));
    }
    /**
     * Reads and returns the content of a file as a byte array.
     * This method can read files inside compressed archives also.
     */
    public static byte[] getByteContent(String file) throws IOException {
        return IOUtil.readContent(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(file),
                true);
    }

    /**
     * Reads and returns the content of a file as a String.
     */
    public static String getContent(File file) throws IOException{
        return new String(getByteContent(file));
    }
    /**
     * Reads and returns the content of a file as a byte array.
     */
    public static byte[] getByteContent(File file) throws IOException {
        return IOUtil.readContent(new FileInputStream(file), true);
    }

    /**
     * Connects to a URL and returns the response of it as a String.
     */
    public static String getContent(URL url) throws IOException{
        return new String(IOUtil.readContent(url.openStream(), true));
    }

    /**
     * Replaces the contents of a file with the specified String.
     *
     * @param	file	the file to write the data to
     * @param	data	the String to write to the file
     */
    public static void setContent(File file, String data) throws IOException{
        setContent(file, data.getBytes());
    }

    /**
     * Replaces the contents of a file with the specified data.
     *
     * @param	file	the file to write the data to
     * @param	data	the data to write to the file
     */
    public static void setContent(File file, byte[] data) throws IOException{
        OutputStream out = new FileOutputStream(file);
        out.write(data);
        out.close();
    }

    /**
     * Searches the directory and all its subdirectories
     *
     * @param 		dir 		is the directory to search in
     * @return a List of files
     */
    public static List<File> search(File dir){
        return search(dir, new LinkedList<>(), true);
    }

    /**
     * Searches the directory and all its subdirectories
     *
     * @param 		dir 		is the directory to search in
     * @param 		fileList 	an existing List to add all files found
     * @param 		recursive 	if the search should go into subdirectories
     * @return A List of files
     */
    public static List<File> search(File dir, List<File> fileList, boolean recursive){
        return search(dir, new LinkedList<>(), false, (recursive ? Integer.MAX_VALUE : 0));
    }

    /**
     * Searches the directory and all its subdirectories
     *
     * @param 		dir 		is the root directory to start searching from
     * @param 		fileList 	an existing List to add all files found
     * @param 		folders 	is if the method should add folders to the List also
     * @param 		recurse 	if the search should go into subdirectories
     * @return A List with the files and/or folders
     */
    public static List<File> search(File dir, List<File> fileList, boolean folders, int recurse){
        if(recurse<0)
            return fileList;
        --recurse;
        if(folders){
            fileList.add( dir );
        }

        File file;
        String[] temp = dir.list();
        if(temp != null){
            for(int i=0; i<temp.length ;i++){
                file = new File(dir.getPath()+File.separator+temp[i]);
                if(file.isDirectory()){
                    logger.finer("Found Folder: "+file);
                    search(new File(dir.getPath()+File.separator+temp[i]+File.separator), fileList, folders, recurse);
                }
                else if(file.isFile()){
                    logger.finer("Found File: "+file);
                    fileList.add(file);
                }
            }
        }

        return fileList;
    }

    /**
     * Returns the extension(without the dot) of the file. e.g. "png" "avi"
     *
     * @param 		file 		is the file
     * @return 					The extension
     */
    public static String getFileExtension(File file){
        return getFileExtension(file.getName());
    }

    /**
     * Returns the extension(without the dot) of the file. e.g. "png" "avi"
     *
     * @param 		file 		is the file
     * @return 					The extension
     */
    public static String getFileExtension(String file){
        if( file == null || file.lastIndexOf(".") == -1 )
            return "";
        return file.substring(file.lastIndexOf(".")+1, file.length());
    }

    /**
     * Replaces the current extension on the file with the given one.
     *
     * @param 		file		is the name of the file
     * @param 		ext			is the new extension, without the dot
     */
    public static String replaceExtension(String file, String ext) {
        if( file == null )
            return null;
        if( file.lastIndexOf(".") == -1 )
            return file+"."+ext;
        return file.substring(0, file.lastIndexOf(".")+1)+ext;
    }

    /**
     * This function will replace some data between two boundaries.
     * If the boundary is not found it will be added  to the end of the file.
     *
     * @param 	file		is the file to modify
     * @param 	boundary	is the start and end boundary to put the data between, this is a full line boundary.
     * @param 	data		is the data that will be written to the file
     */
    public static void writeBetweenBoundary(File file, String boundary, String data) throws IOException{
        BufferedReader in = new BufferedReader(new FileReader(file));
        StringBuilder output = new StringBuilder();

        String line;
        while((line = in.readLine()) != null){
            // Found starting boundary
            if(line.equals(boundary)){
                while((line = in.readLine()) != null)
                    // Find ending boundary
                    if(line.equals(boundary)) break;
                // EOF and no ending boundary found
                if(line == null){
                    in.close();
                    throw new EOFException("No ending boundary found");
                }
                // Write the new data
                output.append(boundary).append('\n');
                output.append(data).append('\n');
                output.append(boundary).append('\n');
            }
            else
                output.append(line).append('\n');
        }
        in.close();

        // Save changes
        FileWriter out = new FileWriter(file);
        out.write(output.toString());
        out.close();
    }
}
