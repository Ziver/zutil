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

package zutil.net.http.page;

import zutil.Hasher;
import zutil.MimeTypeUtil;
import zutil.StringUtil;
import zutil.io.IOUtil;
import zutil.io.file.FileUtil;
import zutil.log.LogUtil;
import zutil.net.http.HttpHeader;
import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
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
    private static final int MAX_CACHE_AGE_SECONDS = 120;

    private final File resource_root;
    private boolean showFolders;
    private boolean redirectToIndex;

    private final HashMap<File,FileCache> cache;

    private static class FileCache{
        public long lastModified;
        public String hash;
    }

    /**
     * @param    file       a reference to a root directory or a file.
     */
    public HttpFilePage(File file){
        if (file == null)
            throw new IllegalArgumentException("Root path cannot be null.");;

        this.resource_root = file;
        this.showFolders = true;
        this.redirectToIndex = true;
        this.cache = new HashMap<>();
    }


    @Override
    public void respond(HttpPrintStream out,
                        HttpHeader headers,
                        Map<String, Object> session,
                        Map<String, String> cookie,
                        Map<String, String> request) {

        try {
            // Is the root only one file or a folder
            if (resource_root.isFile()) {
                deliverFileWithCache(headers, resource_root, out);
            } else { // Resource root is a folder
                File file = new File(resource_root,
                        headers.getRequestURL());
                if (file.getCanonicalPath().startsWith(resource_root.getCanonicalPath())) {
                    // Web Gui
                    if (file.isDirectory() && showFolders) {
                        File indexFile = new File(file, "index.html");
                        // Redirect to index.html
                        if (redirectToIndex && indexFile.isFile()) {
                            deliverFile(indexFile, out);
                        }
                        // Show folder contents
                        else if (showFolders) {
                            out.println("<html>");
                            out.println("<body>");
                            out.println("    <h1>Directory: " + headers.getRequestURL() + "</h1>");
                            out.println("    <hr>");
                            out.println("    <ul>");
                            for (String containingFile : file.list()) {
                                String url = headers.getRequestURL();
                                out.println("        <li><a href='" +
                                        url + (url.endsWith("/") ? "" : "/") + containingFile
                                        +"'>" + containingFile + "</a></li>");
                            }
                            out.println("    </ul>");
                            out.println("    <hr>");
                            out.println("</body>");
                            out.println("</html>");
                        }
                        else {
                            throw new SecurityException("User not allowed to view folder: root=" + resource_root.getAbsolutePath());
                        }
                    }
                    // Deliver the requested file
                    else {
                        deliverFileWithCache(headers, file, out);
                    }
                } else {
                    throw new SecurityException("File is outside of root directory: root=" + resource_root.getAbsolutePath() + " file=" + file.getAbsolutePath());
                }
            }

        } catch (FileNotFoundException | SecurityException e) {
            if (!out.isHeaderSent())
                out.setResponseStatusCode(404);
            log.log(Level.WARNING, e.getMessage());
            out.println("404 Page Not Found: " + headers.getRequestURL());
        } catch (IOException e) {
            if (!out.isHeaderSent())
                out.setResponseStatusCode(500);
            log.log(Level.WARNING, null, e);
            out.println("500 Internal Server Error: "+e.getMessage() );
        }
    }

    private void deliverFileWithCache(HttpHeader headers, File file, HttpPrintStream out) throws IOException {
        String eTag = getFileHash(file);
        out.setHeader(HttpHeader.HEADER_CACHE_CONTROL, "max-age=" + MAX_CACHE_AGE_SECONDS);

        if (eTag != null) {
            out.setHeader("ETag", "\"" + eTag + "\"");

            if (headers.getHeader(HttpHeader.HEADER_IF_NONE_MATCH) != null &&
                    eTag.equals(StringUtil.trimQuotes(headers.getHeader(HttpHeader.HEADER_IF_NONE_MATCH)))) { // File has not changed
                out.setResponseStatusCode(304);
            } else {
                deliverFile(file, out);
            }
        }
    }
    private void deliverFile(File file, HttpPrintStream out) throws IOException {
        String fileExt = FileUtil.getFileExtension(file);

        if (MimeTypeUtil.getMimeByExtension(fileExt) != null)
            out.setHeader(HttpHeader.HEADER_CONTENT_TYPE, MimeTypeUtil.getMimeByExtension(fileExt).toString());
        out.setHeader(HttpHeader.HEADER_CONTENT_LENGTH, "" + file.length());
        out.flush();

        InputStream in = new FileInputStream(file);
        IOUtil.copyStream(in, out);
        in.close();
    }


    private String getFileHash(File file) throws IOException {
        try {
            FileCache fileCache = cache.get(file);
            if (fileCache == null)
                cache.put(file, fileCache = new FileCache());
            if (fileCache.lastModified != file.lastModified()) {
                fileCache.hash = Hasher.hash(file, "SHA-1");
                fileCache.lastModified = file.lastModified();
            }
            return fileCache.hash;
        } catch (NoSuchAlgorithmException e) {
            log.log(Level.WARNING, "Unable to generate hash", e);
        }
        return "";
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
