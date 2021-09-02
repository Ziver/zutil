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

package zutil.net.upnp.service;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import zutil.io.file.FileUtil;
import zutil.net.http.HttpHeader;
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
 * A directory browsing UPnP service.
 */
public class UPnPContentDirectory implements UPnPService, HttpPage, WSInterface {
    private static List<File> file_list;


    public UPnPContentDirectory(File dir) {
        file_list = FileUtil.search(dir, new LinkedList<>(), true, Integer.MAX_VALUE);
    }

    /**
     * This action returns the searching capabilities
     * that are supported by the device.
     *
     */
    @WSReturnName("SortCaps")
    public String GetSearchCapabilities() {
        // "dc:title,res@size"
        return "";
    }

    /**
     * Returns the CSV list of meta-data tags that can
     * be used in sortCriteria
     *
     */
    @WSReturnName("SortCaps")
    public String GetSortCapabilities() {
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
    public int GetSystemUpdateID() {
        // Todo: add caching support
        return (int) (Math.random() *  Integer.MAX_VALUE);
    }

    /**
     * This action allows the caller to incrementally browse
     * the native hierarchy of the Content Directory objects
     * exposed by the Content Directory Service, including
     * information listing the classes of objects available
     * in any particular object container.
     *
     */
    @WSPath("urn:schemas-upnp-org:service:ContentDirectory:1")
    public BrowseRetObj Browse(
            @WSParamName("ObjectID") String objectID,
            @WSParamName("BrowseFlag") String browseFlag,
            @WSParamName("filter") String filter,
            @WSParamName("StartingIndex") int startingIndex,
            @WSParamName("RequestedCount") int requestedCount,
            @WSParamName("SortCriteria") String sortCriteria) {

        BrowseRetObj ret = new BrowseRetObj();

        if (browseFlag.equals("BrowseMetadata")) {

        }
        else if (browseFlag.equals("BrowseDirectChildren")) {
            Document document = DocumentHelper.createDocument();
            Element rootElement = document.addElement("DIDL-Lite", "urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/");
            rootElement.addNamespace("dc", "http://purl.org/dc/elements/1.1/");
            rootElement.addNamespace("upnp", "urn:schemas-upnp-org:metadata-1-0/upnp/");

            List<File> fileList = FileUtil.search(file_list.get(Integer.parseInt(objectID)), new LinkedList<>(), false);

            for (File file : fileList) {
                Element containerElement = rootElement.addElement("container")
                        .addAttribute("id", "" + file_list.indexOf(file))
                        .addAttribute("restricted", "1")
                        .addAttribute("searchable", "0");

                if (fileList.get(0) != file)
                    containerElement.addAttribute("parentID", "" + file_list.indexOf(file.getParent()));

                containerElement.addElement("dc:title").setText(file.getName());
                containerElement.addElement("upnp:storageUsed").setText("" + (int) (file.length() / 1000));

                if (file.isDirectory()) {
                    containerElement.addAttribute("childCount", "" + file.list().length);
                    containerElement.addElement("upnp:class").setText("object.container.storageFolder");
                } else {
                    containerElement.addElement("upnp:class").setText("object.container");
                }
            }

            ret.NumberReturned = fileList.size();
            ret.TotalMatches = rootElement.elements().size();
            ret.Result = document.asXML();
        }
        return ret;
    }


    @WSIgnore
    public void respond(HttpPrintStream out, HttpHeader headers,
                        Map<String, Object> session,
                        Map<String, String> cookie,
                        Map<String, String> request) throws IOException {

        out.enableBuffering(true);
        out.setHeader(HttpHeader.HEADER_CONTENT_TYPE, "text/xml");

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

    public static class BrowseRetObj extends WSReturnObject {
        @WSParamName("Result")
        public String Result;
        @WSParamName("NumberReturned")
        public int NumberReturned;
        @WSParamName("TotalMatches")
        public int TotalMatches;
        @WSParamName("UpdateID")
        public int updateID;
    }
}