/*
 * Copyright (c) 2015 ezivkoc
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

package zutil.net.upnp.services;

import org.dom4j.DocumentException;
import zutil.io.file.FileUtil;
import zutil.net.http.HttpHeaderParser;
import zutil.net.http.HttpPage;
import zutil.net.http.HttpPrintStream;
import zutil.net.upnp.UPnPService;
import zutil.net.ws.WSInterface;
import zutil.net.ws.WSReturnObject;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Information about a UPNP Service
 * 
 * @author Ziver
 */
public class UPnPContentDirectory implements UPnPService, HttpPage, WSInterface {
	public static final int VERSION = 1;

	private static List<File> file_list;

	public UPnPContentDirectory(){}
	
	public UPnPContentDirectory(File dir){
		file_list = FileUtil.search(dir, new LinkedList<File>(), true, Integer.MAX_VALUE);
	}

	/**
	 * This action returns the searching capabilities 
	 * that are supported by the device.
	 * 
	 */
	@WSReturnName("SortCaps")
	public String GetSearchCapabilities(){
		// "dc:title,res@size"
		return "";
	}

	/**
	 * Returns the CSV list of meta-data tags that can 
	 * be used in sortCriteria
	 * 
	 */
	@WSReturnName("SortCaps")
	public String GetSortCapabilities(){
		return "dc:title";
	}

	/**
	 * This action returns the current value of state variable 
	 * SystemUpdateID. It can be used by clients that want to 
	 * 'poll' for any changes in the Content Directory 
	 * (as opposed to subscribing to events).
	 * 
	 */
	@WSReturnName("Id")
	public int GetSystemUpdateID(){
		return 0;
	}

	/**
	 * This action allows the caller to incrementally browse 
	 * the native hierarchy of the Content Directory objects 
	 * exposed by the Content Directory Service, including 
	 * information listing the classes of objects available 
	 * in any particular object container.
	 * @throws DocumentException 
	 * 
	 */
	//@WSNameSpace("urn:schemas-upnp-org:service:ContentDirectory:1")
	public BrowseRetObj Browse(
			@WSParamName("ObjectID") String ObjectID,
			@WSParamName("BrowseFlag") String BrowseFlag,
			@WSParamName("Filter") String Filter,
			@WSParamName("StartingIndex") int StartingIndex,
			@WSParamName("RequestedCount") int RequestedCount,
			@WSParamName("SortCriteria") String SortCriteria) throws DocumentException{

		BrowseRetObj ret = new BrowseRetObj();
		if( BrowseFlag.equals("BrowseMetadata") ){

		}
		else if( BrowseFlag.equals("BrowseDirectChildren") ){
			StringBuffer xml = new StringBuffer();
			xml.append( "<DIDL-Lite xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " +
									"xmlns:upnp=\"urn:schemas-upnp-org:metadata-1-0/upnp/\" " +
									"xmlns=\"urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/\">" );
			List<File> tmp = FileUtil.search( file_list.get(Integer.parseInt(ObjectID)), new LinkedList<File>(), false );
			for(File file : tmp){
				xml.append("	<container id=\""+file_list.indexOf(file)+"\" ");
				if(tmp.get(0) != file) xml.append("parentID=\""+file_list.indexOf(file.getParent())+"\" ");
				if(file.isDirectory()) xml.append("childCount=\""+file.list().length+"\" ");
				xml.append("restricted=\"1\" searchable=\"0\" >");
				
				xml.append("		<dc:title>"+file.getName()+"</dc:title> ");
				if( file.isDirectory() ) 
					xml.append("		<upnp:class>object.container.storageFolder</upnp:class> ");
				else
					xml.append("		<upnp:class>object.container</upnp:class> ");
				xml.append("		<upnp:storageUsed>"+(int)(file.length()/1000)+"</upnp:storageUsed> ");
				xml.append("	</container> ");
				
				ret.NumberReturned++;
				ret.TotalMatches++;
			}
			xml.append( "</DIDL-Lite>" );
			
			ret.Result = xml.toString();
			//Document document = DocumentHelper.parseText( xml.toString() );
			//ret.Result =  document.getRootElement();
		}
		return ret;
	}
	public class BrowseRetObj extends WSReturnObject{
		@WSValueName("Result")
		public String Result;
		@WSValueName("NumberReturned")
		public int NumberReturned;
		@WSValueName("TotalMatches")
		public int TotalMatches;
		@WSValueName("UpdateID")
		public int UpdateID;
	}
	

	@WSDisabled
	public void respond(HttpPrintStream out, HttpHeaderParser clientInfo,
			Map<String, Object> session, Map<String, String> cookie,
			Map<String, String> request) throws IOException {

		out.enableBuffering(true);
		out.setHeader("Content-Type", "text/xml");

		out.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");		
		out.println("<scpd xmlns=\"urn:schemas-upnp-org:service-1-0\">");
		out.println("	<serviceStateTable>");
		out.println("		<stateVariable>");
		out.println("			<Optional/>");
		out.println("			<name>TransferIDs</name>");
		out.println("			<sendEventsAttribute>yes</sendEventsAttribute>");
		out.println("			<dataType>string</dataType>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<name>A_ARG_TYPE_ObjectID</name>");
		out.println("			<sendEventsAttribute>no</sendEventsAttribute>");
		out.println("			<dataType>string</dataType>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<name>A_ARG_TYPE_Result</name>");
		out.println("			<sendEventsAttribute>no</sendEventsAttribute>");
		out.println("			<dataType>string</dataType>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<Optional/>");
		out.println("			<name>A_ARG_TYPE_SearchCriteria</name>");
		out.println("			<sendEventsAttribute>no</sendEventsAttribute>");
		out.println("			<dataType>string</dataType>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<name>A_ARG_TYPE_BrowseFlag</name>");
		out.println("			<sendEventsAttribute>no</sendEventsAttribute>");
		out.println("			<dataType>string</dataType>");
		out.println("			<allowedValueList>");
		out.println("				<allowedValue>BrowseMetadata</allowedValue>");
		out.println("				<allowedValue>BrowseDirectChildren</allowedValue>");
		out.println("			</allowedValueList>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<name>A_ARG_TYPE_Filter</name>");
		out.println("			<sendEventsAttribute>no</sendEventsAttribute>");
		out.println("			<dataType>string</dataType>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<name>A_ARG_TYPE_SortCriteria</name>");
		out.println("			<sendEventsAttribute>no</sendEventsAttribute>");
		out.println("			<dataType>string</dataType>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<name>A_ARG_TYPE_Index</name>");
		out.println("			<sendEventsAttribute>no</sendEventsAttribute>");
		out.println("			<dataType>ui4</dataType>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<name>A_ARG_TYPE_Count</name>");
		out.println("			<sendEventsAttribute>no</sendEventsAttribute>");
		out.println("			<dataType>ui4</dataType>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<name>A_ARG_TYPE_UpdateID</name>");
		out.println("			<sendEventsAttribute>no</sendEventsAttribute>");
		out.println("			<dataType>ui4</dataType>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<Optional/>");
		out.println("			<name>A_ARG_TYPE_TransferID</name>");
		out.println("			<sendEventsAttribute>no</sendEventsAttribute>");
		out.println("			<dataType>ui4</dataType>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<Optional/>");
		out.println("			<name>A_ARG_TYPE_TransferStatus</name>");
		out.println("			<sendEventsAttribute>no</sendEventsAttribute>");
		out.println("			<dataType>string</dataType>");
		out.println("			<allowedValueList>");
		out.println("				<allowedValue>COMPLETED</allowedValue>");
		out.println("				<allowedValue>ERROR</allowedValue>");
		out.println("				<allowedValue>IN_PROGRESS</allowedValue>");
		out.println("				<allowedValue>STOPPED</allowedValue>");
		out.println("			</allowedValueList>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<Optional/>");
		out.println("			<name>A_ARG_TYPE_TransferLength</name>");
		out.println("			<sendEventsAttribute>no</sendEventsAttribute>");
		out.println("			<dataType>string</dataType>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<Optional/>");
		out.println("			<name>A_ARG_TYPE_TransferTotal</name>");
		out.println("			<sendEventsAttribute>no</sendEventsAttribute>");
		out.println("			<dataType>string</dataType>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<Optional/>");
		out.println("			<name>A_ARG_TYPE_TagValueList</name>");
		out.println("			<sendEventsAttribute>no</sendEventsAttribute>");
		out.println("			<dataType>string</dataType>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<Optional/>");
		out.println("			<name>A_ARG_TYPE_URI</name>");
		out.println("			<sendEventsAttribute>no</sendEventsAttribute>");
		out.println("			<dataType>uri</dataType>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<name>SearchCapabilities</name>");
		out.println("			<sendEventsAttribute>no</sendEventsAttribute>");
		out.println("			<dataType>string</dataType>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<name>SortCapabilities</name>");
		out.println("			<sendEventsAttribute>no</sendEventsAttribute>");
		out.println("			<dataType>string</dataType>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<name>SystemUpdateID</name>");
		out.println("			<sendEventsAttribute>yes</sendEventsAttribute>");
		out.println("			<dataType>ui4</dataType>");
		out.println("		</stateVariable>");
		out.println("		<stateVariable>");
		out.println("			<Optional/>");
		out.println("			<name>ContainerUpdateIDs</name>");
		out.println("			<sendEventsAttribute>yes</sendEventsAttribute>");
		out.println("			<dataType>string</dataType>");
		out.println("		</stateVariable>");
		out.println("	</serviceStateTable>");


		out.println("	<actionList>");
		out.println("		<action>");
		out.println("			<name>GetSearchCapabilities</name>");
		out.println("			<argumentList>");
		out.println("				<argument>");
		out.println("					<name>SearchCaps</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>SearchCapabilities</relatedStateVariable>");
		out.println("				</argument>");
		out.println("			</argumentList>");
		out.println("		</action>");
		out.println("		<action>");
		out.println("			<name>GetSortCapabilities</name>");
		out.println("			<argumentList>");
		out.println("				<argument>");
		out.println("					<name>SortCaps</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>SortCapabilities</relatedStateVariable>");
		out.println("				</argument>");
		out.println("			</argumentList>");
		out.println("		</action>");
		out.println("		<action>");
		out.println("			<name>GetSystemUpdateID</name>");
		out.println("			<argumentList>");
		out.println("				<argument>");
		out.println("					<name>Id</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>SystemUpdateID</relatedStateVariable>");
		out.println("				</argument>");
		out.println("			</argumentList>");
		out.println("		</action>");

		out.println("		<action>");
		out.println("			<name>Browse</name>");
		out.println("			<argumentList>");
		out.println("				<argument>");
		out.println("					<name>ObjectID</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_ObjectID</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>BrowseFlag</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_BrowseFlag</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>Filter</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_Filter</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>StartingIndex</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_Index</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>RequestedCount</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_Count</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>SortCriteria</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_SortCriteria</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>Result</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_Result</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>NumberReturned</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_Count</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>TotalMatches</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_Count</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>UpdateID</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_UpdateID</relatedStateVariable>");
		out.println("				</argument>");
		out.println("			</argumentList>");
		out.println("		</action>");

		/*out.println("		<action>");
		out.println("			<Optional/>");
		out.println("			<name>Search</name>");
		out.println("			<argumentList>");
		out.println("				<argument>");
		out.println("					<name>ContainerID</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_ObjectID</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>SearchCriteria</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_SearchCriteria </relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>Filter</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_Filter</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>StartingIndex</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_Index</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>RequestedCount</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_Count</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>SortCriteria</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_SortCriteria</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>Result</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_Result</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>NumberReturned</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_Count</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>TotalMatches</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_Count</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>UpdateID</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_UpdateID</relatedStateVariable>");
		out.println("				</argument>");
		out.println("			</argumentList>");
		out.println("		</action>");*/

		/*out.println("		<action>");
		out.println("			<Optional/>");
		out.println("			<name>CreateObject</name>");
		out.println("			<argumentList>");
		out.println(" 				<argument>");
		out.println("					<name>ContainerID</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_ObjectID</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>Elements</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_Result</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>ObjectID</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_ObjectID</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>Result</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_Result</relatedStateVariable>");
		out.println("				</argument>");
		out.println("			</argumentList>");
		out.println("		</action>");*/

		/*out.println("		<action>");
		out.println("			<Optional/>");
		out.println("			<name>DestroyObject</name>");
		out.println("			<argumentList>");
		out.println("				<argument>");
		out.println("					<name>ObjectID</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_ObjectID</relatedStateVariable>");
		out.println("				</argument>");
		out.println("			</argumentList>");
		out.println("		</action>");*/

		/*out.println("		<action>");
		out.println("			<Optional/>");
		out.println("			<name>UpdateObject</name>");
		out.println("			<argumentList>");
		out.println("				<argument>");
		out.println("					<name>ObjectID</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_ObjectID</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>CurrentTagValue</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_TagValueList </relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>NewTagValue</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_TagValueList </relatedStateVariable>");
		out.println("				</argument>");
		out.println("			</argumentList>");
		out.println("		</action>");*/

		/*out.println("		<action>");
		out.println("			<Optional/>");
		out.println("			<name>ImportResource</name>");
		out.println("			<argumentList>");
		out.println("				<argument>");
		out.println("					<name>SourceURI</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_URI</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>DestinationURI</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_URI</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>TransferID</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_TransferID </relatedStateVariable>");
		out.println("				</argument>");
		out.println("			</argumentList>");
		out.println("		</action>");*/

		/*out.println("		<action>");
		out.println("			<Optional/>");
		out.println("			<name>ExportResource</name>");
		out.println("			<argumentList>");
		out.println("				<argument>");
		out.println("					<name>SourceURI</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_URI</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>DestinationURI</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_URI</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>TransferID</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_TransferID </relatedStateVariable>");
		out.println("				</argument>");
		out.println("			</argumentList>");
		out.println("		</action>");*/

		/*out.println("		<action>");
		out.println("			<Optional/>");
		out.println("			<name>StopTransferResource</name>");
		out.println("			<argumentList>");
		out.println("				<argument>");
		out.println("					<name>TransferID</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_TransferID </relatedStateVariable>");
		out.println("				</argument>");
		out.println("			</argumentList>");
		out.println("		</action>");*/

		/*out.println("		<action>");
		out.println("			<Optional/>");
		out.println("			<name>GetTransferProgress</name>");
		out.println("			<argumentList>");
		out.println("				<argument>");
		out.println("					<name>TransferID</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_TransferID </relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>TransferStatus</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_TransferStatus </relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>TransferLength</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_TransferLength </relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>TransferTotal</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_TransferTotal</relatedStateVariable>");
		out.println("				</argument>");
		out.println("			</argumentList>");
		out.println("		</action>");*/

		/*out.println("		<action>");
		out.println("			<Optional/>");
		out.println("			<name>DeleteResource</name>");
		out.println("			<argumentList>");
		out.println("				<argument>");
		out.println("					<name>ResourceURI</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_URI</relatedStateVariable>");
		out.println("				</argument>");
		out.println("			</argumentList>");
		out.println("		</action>");*/

		/*out.println("		<action>");
		out.println("			<Optional/>");
		out.println("			<name>CreateReference</name>");
		out.println("			<argumentList>");
		out.println("				<argument>");
		out.println("					<name>ContainerID</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_ObjectID</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>ObjectID</name>");
		out.println("					<direction>in</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_ObjectID</relatedStateVariable>");
		out.println("				</argument>");
		out.println("				<argument>");
		out.println("					<name>NewID</name>");
		out.println("					<direction>out</direction>");
		out.println("					<relatedStateVariable>A_ARG_TYPE_ObjectID</relatedStateVariable>");
		out.println("				</argument>");
		out.println("			</argumentList>");
		out.println("		</action>");*/

		out.println("	</actionList>");
		out.println("</scpd>");
	}
}	
