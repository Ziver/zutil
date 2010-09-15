package zutil.jee.upload;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemHeadersSupport;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import zutil.FileUtil;
import zutil.StringUtil;
import zutil.jee.upload.FileUploadListener.Status;
import zutil.log.LogUtil;
import zutil.parser.json.JSONNode;
import zutil.parser.json.JSONWriter;
import zutil.parser.json.JSONNode.JSONType;

/**
 * <XMP>
 * Example web.xml: 
 * <servlet>
 * 	<servlet-name>Upload</servlet-name>
 * 	<servlet-class>zall.util.AjaxFileUpload</servlet-class>
 * 	<init-param>
 * 		<param-name>JAVASCRIPT</param-name>
 * 		<param-value>{FILE_PATH}</param-value>
 * 	</init-param>
 * 	<init-param>
 * 		<param-name>TEMP_PATH</param-name>
 * 		<param-value>SYSTEM|SERVLET|{PATH}</param-value>
 * 	</init-param>
 * </servlet>
 * 
 * 
 * HTML Header: 
 * <script type='text/javascript' src='{PATH_TO_SERVLET}?js'></script>
 *
 *
 * HTML Body: 
 * <FORM id="AjaxFileUpload">
 * 	<input type="file" multiple name="file" />
 * </FORM>
 * <UL id="UploadQueue"></UL>
 * 
 * 
 * </XMP>
 * @author Ziver
 *
 */
public abstract class AjaxFileUpload extends HttpServlet {
	public static final Logger logger = LogUtil.getLogger();
	private static final long serialVersionUID = 1L;

	public static final String SESSION_FILEUPLOAD_LISTENER = "FILEUPLOAD_LISTENER";
	public static final String JAVASCRIPT_FILE = "zutil/jee/upload/AjaxFileUpload.js";

	public static File TEMPFILE_PATH = null;
	public static String JAVASCRIPT = "";

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			// Read the javascript file to memory
			String path = JAVASCRIPT_FILE;
			if(config.getInitParameter("JAVASCRIPT") != null)
				path = config.getInitParameter("JAVASCRIPT");
			JAVASCRIPT = FileUtil.getContent( FileUtil.findURL(path) );

			// Read temp dir
			if(config.getInitParameter("TEMP_PATH") != null){
				if( config.getInitParameter("TEMP_PATH").equalsIgnoreCase("SYSTEM") )
					TEMPFILE_PATH = new File( System.getProperty("java.io.tmpdir") );
				else if( config.getInitParameter("TEMP_PATH").equalsIgnoreCase("SERVLET") )
					TEMPFILE_PATH = (File) config.getServletContext().getAttribute("javax.servlet.context.tempdir");
				else
					TEMPFILE_PATH = new File( config.getInitParameter("TEMP_PATH") );
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		if(request.getParameter("js") != null){
			response.setContentType("application/x-javascript");
			String tmp = JAVASCRIPT;
			tmp = JAVASCRIPT.replaceAll("\\{SERVLET_URL\\}", request.getRequestURI());
			tmp = tmp.replaceAll("\\{BGUPLOAD\\}", "false");
			tmp = tmp.replaceAll("\\{PROGHTML\\}", getProgressHTML());
			out.print(tmp);
			return;
		}

		response.setContentType("application/json");
		HttpSession session = request.getSession();			
		LinkedList<FileUploadListener> list = 
			(LinkedList<FileUploadListener>)session.getAttribute(SESSION_FILEUPLOAD_LISTENER);
		if (list == null) {
			out.println("[]");
			return;
		}

		// Generate JSON
		JSONNode root = new JSONNode( JSONType.List );
		Iterator<FileUploadListener> it = list.iterator();
		while( it.hasNext() ) {
			FileUploadListener listener = it.next();
			if( listener.getStatus() == Status.Done || listener.getStatus() == Status.Error ){
				if( listener.getTime() + 5000 < System.currentTimeMillis() ){
					it.remove();
				}
			}

			JSONNode node = new JSONNode( JSONType.Map );
			node.add("id", listener.getID());
			node.add("filename", listener.getFilename());
			node.add("percent", listener.getPercentComplete());
			node.add("uploaded", StringUtil.formatBytesToString( listener.getBytesRead() ));
			node.add("total", StringUtil.formatBytesToString( listener.getContentLength() ));
			node.add("speed", StringUtil.formatBytesToString( listener.getSpeed() )+"/s");
			node.add("status", listener.getStatus().toString());
			root.add(node);
		}

		// Write to the user
		JSONWriter json_out = new JSONWriter( out );
		json_out.write(root);
	}



	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		FileUploadListener listener = new FileUploadListener();
		try {
			// Initiate list and HashMap that will contain the data
			HashMap<String,String> fields = new HashMap<String,String>();
			ArrayList<FileItem> files = new ArrayList<FileItem>();
			
			// Add the listener to the session
			HttpSession session = request.getSession();
			LinkedList<FileUploadListener> list = 
				(LinkedList<FileUploadListener>)session.getAttribute(SESSION_FILEUPLOAD_LISTENER);
			if(list == null){
				list = new LinkedList<FileUploadListener>();
				session.setAttribute(SESSION_FILEUPLOAD_LISTENER, list);
			}
			list.add(listener);

			// Create a factory for disk-based file items
			DiskFileItemFactory factory = new DiskFileItemFactory();
			if(TEMPFILE_PATH != null) 
				factory.setRepository( TEMPFILE_PATH );
			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setProgressListener( listener );
			// Set overall request size constraint
			//upload.setSizeMax(yourMaxRequestSize);

			// Parse the request
			FileItemIterator it = upload.getItemIterator( request );
			while( it.hasNext() ) {
				FileItemStream item = it.next();
				listener.setFileName( item.getName() );
				FileItem fileItem = factory.createItem(item.getFieldName(),
						item.getContentType(), item.isFormField(), item.getName());
				// Read the file data
				Streams.copy(item.openStream(), fileItem.getOutputStream(),	true);
				if (fileItem instanceof FileItemHeadersSupport) {
					final FileItemHeaders fih = item.getHeaders();
					((FileItemHeadersSupport) fileItem).setHeaders(fih);
				}
				
				//Handle the item
				if(fileItem.isFormField()){
					fields.put( fileItem.getFieldName(), fileItem.getString());
				}
				else{
					files.add( fileItem );
					logger.info("Recieved file: "+fileItem.getName()+" ("+StringUtil.formatBytesToString( fileItem.getSize() )+")");
				}
			}
			// Process the upload
			listener.setStatus( Status.Processing );
			doUpload( request, response, fields, files );
			// Done
			listener.setStatus( Status.Done );
		} catch (Exception e) {
			e.printStackTrace();
			listener.setStatus(Status.Error);
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
	 * Handle the uppload
	 */
	public abstract void doUpload(HttpServletRequest request, HttpServletResponse response, 
										Map<String,String> fields, List<FileItem> files);
}
