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

package zutil.net.http.pages;

import zutil.io.IOUtil;
import zutil.io.file.FileUtil;
import zutil.log.LogUtil;
import zutil.net.http.HttpHeaderParser;
import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;

import java.io.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This Http Page will host static content from the server.
 *
 * Created by Ziver on 2015-03-30.
 */
public class HttpFilePage implements HttpPage{
    private static final Logger log = LogUtil.getLogger();

    private File resource_root;
    private boolean showFolders;
    private boolean redirectToIndex;

    /**
     * @param    file       a reference to a root directory or a file.
     */
    public HttpFilePage(File file){
        this.resource_root = file;
        this.showFolders = true;
        this.redirectToIndex = true;
    }


    @Override
    public void respond(HttpPrintStream out,
                        HttpHeaderParser client_info,
                        Map<String, Object> session,
                        Map<String, String> cookie,
                        Map<String, String> request) throws IOException{

        try {
            // Is the root only one file or a folder
            if (resource_root.isFile()) {
                deliverFile(resource_root, out);
            }
            else { // Resource root is a folder
                File file = new File(resource_root,
                        client_info.getRequestURL());
                if(file.getCanonicalPath().startsWith(resource_root.getCanonicalPath())){
                    if(file.isDirectory() && showFolders){
                        File indexFile = new File(file, "index.html");
                        // Redirect to index.html
                        if(redirectToIndex && indexFile.isFile()) {
                            deliverFile(indexFile, out);
                        }
                        // Show folder contents
                        else if(showFolders){
                            out.println("<HTML><BODY><H1>Directory: " + client_info.getRequestURL() + "</H1>");
                            out.println("<HR><UL>");
                            for (String f : file.list()) {
                                String url = client_info.getRequestURL();
                                out.println("<LI><A href='" +
                                        url + (url.charAt(url.length()-1)=='/'?"":"/")+ f
                                        +"'>" + f + "</A></LI>");
                            }
                            out.println("</UL><HR></BODY></HTML>");
                        }
                        else {
                            throw new SecurityException("User not allowed to view folder: root=" + resource_root.getAbsolutePath());
                        }
                    }
                    else {
                        deliverFile(file, out);
                    }
                }
                else {
                    throw new SecurityException("File is outside of root directory: root=" + resource_root.getAbsolutePath() + " file=" + file.getAbsolutePath());
                }
            }

        }catch (FileNotFoundException e){
            if(!out.isHeaderSent())
                out.setStatusCode(404);
            log.log(Level.WARNING, e.getMessage());
            out.println("404 Page Not Found: " + client_info.getRequestURL());
        }catch (SecurityException e){
            if(!out.isHeaderSent())
                out.setStatusCode(404);
            log.log(Level.WARNING, e.getMessage());
            out.println("404 Page Not Found: " + client_info.getRequestURL() );
        }catch (IOException e){
            if(!out.isHeaderSent())
                out.setStatusCode(500);
            log.log(Level.WARNING, null, e);
            out.println("500 Internal Server Error: "+e.getMessage() );
        }
    }

    private void deliverFile(File file, HttpPrintStream out) throws IOException {
        out.setHeader("Content-Type", getMIMEType(file));
        out.flush();

        //InputStream in = new BufferedInputStream(new FileInputStream(file));
        InputStream in = new FileInputStream(file);
        IOUtil.copyStream(in, out);
        in.close();
    }

    private String getMIMEType(File file){
        switch(FileUtil.getFileExtension(file)){
            case "css":  return "text/css";
            case "cvs":  return "text/csv";
            case "jpg":  return "image/jpeg";
            case "js":   return "application/javascript";
            case "png":  return "image/png";
            case "htm":
            case "html": return "text/html";
            case "xml":  return "text/xml";
            default:     return "text/plain";
        }
    }


    /**
     * Enable or disable showing of folder contents
     */
    public void showFolders(boolean enabled){
        this.showFolders = enabled;
    }

    /**
     * If directory links should be redirected to index files
     */
    public void redirectToIndexFile(boolean enabled){
        this.redirectToIndex = enabled;
    }
}
