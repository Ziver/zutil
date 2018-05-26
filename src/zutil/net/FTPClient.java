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

package zutil.net;

import zutil.io.IOUtil;

import javax.security.auth.login.AccountException;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * A simple FTP client class
 * 
 * @author Ziver
 *
 * http://en.wikipedia.org/wiki/List_of_FTP_commands
 * http://en.wikipedia.org/wiki/List_of_FTP_server_return_codes
 * http://www.ietf.org/rfc/rfc959.txt
 *
 * TODO: file info, rename, Active mode
 */
public class FTPClient extends Thread{
    public static final int FTP_PORT = 21;
    public static final int FTP_DATA_PORT = 20;
    public static final int FTP_NOOP_INT = 120;

    public static enum FTPConnectionType{
        ACTIVE,
        PASSIVE
    }

    public static enum FTPReturnCode{
        UNKNOWN          (  -1 ),

        USER_OK          ( 331 ),
        NEED_PASS        ( 331 ),
        LOGIN_NO         ( 530 ),
        LOGIN_OK         ( 230 ),

        ENTERING_PASSIVE ( 227 ),
        FILE_ACTION_OK   ( 250 ),
        PATH_CREATED     ( 257 );

        private int code;
        private FTPReturnCode(int code){
            this.code = code;
        }

        public boolean isError(){
            return code >= 400;
        }

        public static FTPReturnCode fromCode(int code){
            for(FTPReturnCode type : FTPReturnCode.values()){
                if(code == type.code) return type;
            }
            return UNKNOWN;
        }
    }
    //***************************************************

    private FTPConnectionType connectionType;
    private BufferedReader in;
    private Writer out;
    private Socket socket;
    private long last_sent;

    /**
     * Creates a FTP connection and logs in
     *
     * @param   url         the address to server
     * @param   port        port number
     * @param   user        login username
     * @param   pass        password
     * @param   conn_type   connection type
     */
    public FTPClient(String url, int port, String user, String pass, FTPConnectionType conn_type) throws UnknownHostException, IOException, AccountException{
        socket = new Socket(url, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new OutputStreamWriter(socket.getOutputStream());
        connectionType = conn_type;

        readCommand();
        sendCommand("USER "+user);
        sendNoReplyCommand("PASS "+pass);
        String tmp = readCommand();
        if(parseReturnCode(tmp) == FTPReturnCode.LOGIN_NO){
            close();
            throw new AccountException(tmp);
        }

        start();
    }

//**************************************************************************************
//********************************* Command channel ************************************

    /**
     * Sends the given command to the server and returns a status integer
     *
     * @return last line received from the server
     */
    private FTPReturnCode sendCommand(String cmd) throws IOException{
        sendNoReplyCommand(cmd);
        return parseReturnCode( readCommand( ) );
    }

    /**
     * Sends a command and don't cares about the reply
     */
    private void sendNoReplyCommand(String cmd) throws IOException{
        last_sent = System.currentTimeMillis();
        out.append(cmd).append('\n');
    }

    /**
     * Reads from the command channel until there are nothing
     * left to read and returns the last line
     *
     * @return last line received by the server
     */
    private String readCommand() throws IOException{
        String tmp = in.readLine();
        while(!Character.isWhitespace(tmp.charAt(3))){
            tmp = in.readLine();
            if(parseReturnCode(tmp).isError()) throw new IOException(tmp);
        }
        return tmp;
    }

    /**
     * Parses the return line from the server and returns the status code
     *
     * @param   msg     message String from the server
     * @return a status code response
     */
    private FTPReturnCode parseReturnCode(String msg){
        return FTPReturnCode.fromCode(Integer.parseInt(msg.substring(0, 3)));
    }

//**************************************************************************************
//****************************** File system actions ************************************

    /**
     * Returns a LinkedList with names of all the files in the directory
     *
     * @deprecated
     * @return List with filenames
     */
    public String[] getFileList(String path) throws IOException{
        BufferedInputStream data_in = getDataInputStream();
        sendCommand("NLST "+path);

        String data = new String(IOUtil.readContent(data_in));

        data_in.close();
        readCommand();
        return data.split("[\n\r]");
    }

    /**
     * Returns information about a file or directory
     *
     * @deprecated
     * @return a List of Strings with information
     */
    public String getFileInfo(String path) throws IOException{
        Pattern regex = Pattern.compile("\\s{1,}");

        BufferedInputStream data_in = getDataInputStream();
        sendCommand("LIST "+path);

        String data = new String(IOUtil.readContent(data_in));

        data_in.close();
        readCommand();
        return data;
    }

    /**
     * Creates a file in the server with the given data
     *
     * @param   path    filepath
     * @param   data    data to put in the file
     */
    public void sendFile(String path, String data) throws IOException{
        BufferedOutputStream data_out = getDataOutputStream();
        sendCommand("STOR "+path);

        byte[] byte_data = data.getBytes();
        data_out.write(byte_data, 0, byte_data.length);
        data_out.close();

        readCommand();
    }

    /**
     * Creates a directory in the server
     *
     * @param path The path to the directory
     */
    public boolean createDir(String path) throws IOException{
        if(sendCommand("MKD "+path) == FTPReturnCode.PATH_CREATED)
            return true;
        return false;
    }

    /**
     * Returns a InputStream for a file on the server
     * WARNING: you must run readCommand(); after you close the stream
     *
     * @return a stream with file data
     */
    private BufferedInputStream getFileInputStream(String path) throws IOException{
        BufferedInputStream input = getDataInputStream();
        sendCommand("RETR "+path);
        return input;
    }

    /**
     * Download a file from the server to a local file
     *
     * @param   source      source file on the server
     * @param   destination local destination file
     */
    public void getFile(String source, String destination) throws IOException{
        BufferedInputStream ext_file_in = getFileInputStream(source);
        BufferedOutputStream local_file_out = new BufferedOutputStream(new FileOutputStream(new File(destination)));

        IOUtil.copyStream(ext_file_in, local_file_out);
        readCommand();
    }

    /**
     * Remove a file from the FTP server
     *
     * @return true if the command was successful, false otherwise
     */
    public boolean removeFile(String path) throws IOException{
        if(sendCommand("DELE "+path) == FTPReturnCode.FILE_ACTION_OK)
            return true;
        return false;
    }

    /**
     * Removes a directory from the FTP server
     *
     * @return True if the command was successful or false otherwise
     */
    public boolean removeDir(String path) throws IOException{
        if(sendCommand("RMD "+path) == FTPReturnCode.FILE_ACTION_OK)
            return true;
        return false;
    }

//**************************************************************************************
//******************************** Data Connection *************************************

    /**
     * Start a data connection to the server.
     *
     * @return a PrintStream for the channel
     */
    public BufferedOutputStream getDataOutputStream() throws IOException{
        if(connectionType == FTPConnectionType.PASSIVE){ // Passive Mode
            int port = setPassiveMode();
            Socket data_socket = new Socket(socket.getInetAddress().getHostAddress(), port);
            return new BufferedOutputStream(data_socket.getOutputStream());
        }
        else{ // Active Mode
            return null;
        }
    }

    /**
     * Start a data connection to the server.
     *
     * @return a BufferedReader for the data channel
     */
    public BufferedInputStream getDataInputStream() throws IOException{
        if(connectionType == FTPConnectionType.PASSIVE){ // Passive Mode
            int port = setPassiveMode();
            Socket data_socket = new Socket(socket.getInetAddress().getHostAddress(), port);
            return new BufferedInputStream(data_socket.getInputStream());
        }
        else{ // Active Mode
            return null;
        }
    }


    /**
     * Sets Passive mode to the server
     *
     * @return a port number for data channel
     */
    private int setPassiveMode() throws IOException{
        sendNoReplyCommand("PASV");
        String ret_msg = readCommand();
        if(parseReturnCode(ret_msg) != FTPReturnCode.ENTERING_PASSIVE){
            throw new IOException("Passive mode rejected by server: "+ret_msg);
        }
        ret_msg = ret_msg.substring(ret_msg.indexOf('(')+1, ret_msg.indexOf(')'));
        String[] tmpArray = ret_msg.split("[,]");

        if(tmpArray.length <= 1)
            return Integer.parseInt(tmpArray[0]);
        else
            return Integer.parseInt(tmpArray[4])*256 + Integer.parseInt(tmpArray[5]);
    }

//**************************************************************************************
//**************************************************************************************

    /**
     * Keep the connection alive
     */
    public void run(){
        try {
            while(true){
                if(last_sent > System.currentTimeMillis() + FTP_NOOP_INT*1000){
                    sendCommand("NOOP");
                }
                try{ Thread.sleep(5000); }catch(Exception e){}
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    /**
     * Close the FTP connection
     */
    public void close() throws IOException{
        sendCommand("QUIT");
        in.close();
        out.close();
        socket.close();
        this.interrupt();
    }
}
