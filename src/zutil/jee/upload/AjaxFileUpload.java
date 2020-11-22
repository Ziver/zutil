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

package zutil.jee.upload;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import zutil.StringUtil;
import zutil.io.MultiPrintStream;
import zutil.io.file.FileUtil;
import zutil.jee.upload.FileUploadListener.Status;
import zutil.log.LogUtil;
import zutil.parser.DataNode;
import zutil.parser.DataNode.DataType;
import zutil.parser.json.JSONWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <pre>
 * Example web.xml:
 * &lt;servlet&gt;
 * 	&lt;servlet-name&gt;Upload&lt;/servlet-name&gt;
 * 	&lt;servlet-class&gt;zall.util.AjaxFileUpload&lt;/servlet-class&gt;
 * 	&lt;init-param&gt;
 * 		&lt;param-name&gt;JAVASCRIPT&lt;/param-name&gt;
 * 		&lt;param-value&gt;{FILE_PATH}&lt;/param-value&gt;
 * 	&lt;/init-param&gt;
 * 	&lt;init-param&gt;
 * 		&lt;param-name&gt;TEMP_PATH&lt;/param-name&gt;
 * 		&lt;param-value&gt;SYSTEM|SERVLET|{PATH}&lt;/param-value&gt;
 * 	&lt;/init-param&gt;
 * &lt;/servlet&gt;
 *
 *
 * HTML Header:
 * &lt;script type='text/javascript' src='{PATH_TO_SERVLET}?js'&gt;&lt;/script&gt;
 *
 *
 * HTML Body:
 * &lt;FORM id="AjaxFileUpload"&gt;
 * 	&lt;input type="file" multiple name="file" /&gt;
 * &lt;/FORM&gt;
 * &lt;UL id="UploadQueue"&gt;&lt;/UL&gt;
 *
 *
 * </pre>
 *
 * @author Ziver
 */
public abstract class AjaxFileUpload extends HttpServlet {
    private static final Logger logger = LogUtil.getLogger();
    private static final long serialVersionUID = 1L;

    public static final String SESSION_FILEUPLOAD_LISTENER = "FILEUPLOAD_LISTENER";
    public static final String JAVASCRIPT_FILE = "zutil/jee/upload/AjaxFileUpload.js";

    public static File TEMPFILE_PATH = null;
    public static String JAVASCRIPT = "";
    public static HashSet<String> ALLOWED_EXTENSIONS = new HashSet<>();

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        try {
            // Read the javascript file to memory
            String path = JAVASCRIPT_FILE;
            if (config.getInitParameter("JAVASCRIPT_FILE") != null)
                path = config.getInitParameter("JAVASCRIPT_FILE");
            JAVASCRIPT = FileUtil.getContent(FileUtil.findURL(path));

            // Read temp dir
            if (config.getInitParameter("TEMP_PATH") != null) {
                if (config.getInitParameter("TEMP_PATH").equalsIgnoreCase("SYSTEM"))
                    TEMPFILE_PATH = new File(System.getProperty("java.io.tmpdir"));
                else if (config.getInitParameter("TEMP_PATH").equalsIgnoreCase("SERVLET"))
                    TEMPFILE_PATH = (File) config.getServletContext().getAttribute("javax.servlet.context.tempdir");
                else
                    TEMPFILE_PATH = new File(config.getInitParameter("TEMP_PATH"));
            }

            // Read allowed file types
            if (config.getInitParameter("ALLOWED_EXTENSIONS") != null) {
                ALLOWED_EXTENSIONS.addAll(Arrays.asList(
                        config.getInitParameter("ALLOWED_EXTENSIONS").toLowerCase().split(",")));

                logger.info("Allowed extensions: " + MultiPrintStream.dumpToString(ALLOWED_EXTENSIONS));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        if (request.getParameter("js") != null) {
            response.setContentType("application/x-javascript");

            String tmp = JAVASCRIPT.replaceAll("\\{SERVLET_URL\\}", request.getRequestURI());
            tmp = tmp.replaceAll("\\{BGUPLOAD\\}", "false");
            tmp = tmp.replaceAll("\\{PROGHTML\\}", getProgressHTML());
            out.print(tmp);
            return;
        }

        response.setContentType("application/json");
        HttpSession session = request.getSession();
        LinkedList<FileUploadListener> list =
                (LinkedList<FileUploadListener>) session.getAttribute(SESSION_FILEUPLOAD_LISTENER);
        if (list == null) {
            out.println("[]");
            return;
        }

        // Generate JSON
        DataNode root = new DataNode(DataType.List);
        Iterator<FileUploadListener> it = list.iterator();
        while (it.hasNext()) {
            FileUploadListener listener = it.next();
            if (listener.getStatus() == Status.Done || listener.getStatus() == Status.Error) {
                if (listener.getTime() + 5000 < System.currentTimeMillis()) {
                    it.remove();
                }
            }

            root.add(listener.getJSON());
        }

        // Write to the user
        JSONWriter json_out = new JSONWriter(out);
        json_out.write(root);
    }


    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        FileUploadListener listener = new FileUploadListener();
        try {
            // Initiate list and HashMap that will contain the data
            HashMap<String, String> fields = new HashMap<>();
            ArrayList<FileItem> files = new ArrayList<>();

            // Add the listener to the session
            HttpSession session = request.getSession();
            LinkedList<FileUploadListener> list =
                    (LinkedList<FileUploadListener>) session.getAttribute(SESSION_FILEUPLOAD_LISTENER);
            if (list == null) {
                list = new LinkedList<>();
                session.setAttribute(SESSION_FILEUPLOAD_LISTENER, list);
            }
            list.add(listener);

            // Create a factory for disk-based file items
            DiskFileItemFactory factory = new DiskFileItemFactory();
            if (TEMPFILE_PATH != null)
                factory.setRepository(TEMPFILE_PATH);
            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setProgressListener(listener);
            // Set overall request size constraint
            //upload.setSizeMax(yourMaxRequestSize);

            // Parse the request
            FileItemIterator it = upload.getItemIterator(request);
            while (it.hasNext()) {
                FileItemStream item = it.next();

                // Is the file type allowed?
                String ext = FileUtil.getFileExtension(item.getName()).toLowerCase();
                if (!item.isFormField() &&
                        !ALLOWED_EXTENSIONS.contains(ext)) {
                    String msg = "File type '" + ext + "' is not allowed!";
                    logger.warning(msg);
                    listener.setStatus(Status.Error);
                    listener.setFileName(item.getName());
                    listener.setMessage(msg);
                    return;
                }

                listener.setFileName(item.getName());
                FileItem fileItem = factory.createItem(item.getFieldName(),
                        item.getContentType(), item.isFormField(), item.getName());

                // Read the file data
                Streams.copy(item.openStream(), fileItem.getOutputStream(), true);
                if (fileItem instanceof FileItemHeadersSupport) {
                    final FileItemHeaders fih = item.getHeaders();
                    ((FileItemHeadersSupport) fileItem).setHeaders(fih);
                }

                //Handle the item
                if (fileItem.isFormField()) {
                    fields.put(fileItem.getFieldName(), fileItem.getString());
                } else {
                    files.add(fileItem);
                    logger.info("Received file: " + fileItem.getName() + " (" + StringUtil.formatByteSizeToString(fileItem.getSize()) + ")");
                }
            }

            // Process the upload
            listener.setStatus(Status.Processing);
            doUpload(request, response, fields, files);

            // Done
            listener.setStatus(Status.Done);
        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
            listener.setStatus(Status.Error);
            listener.setFileName("");
            listener.setMessage(e.getMessage());
        }
    }

    /**
     * @return the HTML for the progress bar. Special ID's:
     * <br>-filename = String
     * <br>-progress = percent
     * <br>-total	 = String
     * <br>-uploaded = String
     * <br>-status	 = String (Uploading, Initializing etc)
     * <br>-speed	 = String
     */
    public abstract String getProgressHTML();

    /**
     * Handle the upload
     */
    public abstract void doUpload(HttpServletRequest request, HttpServletResponse response,
                                  Map<String, String> fields, List<FileItem> files) throws ServletException;
}
