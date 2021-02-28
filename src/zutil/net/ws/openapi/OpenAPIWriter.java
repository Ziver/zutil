package zutil.net.ws.openapi;

import zutil.log.LogUtil;
import zutil.net.ws.WSMethodDef;
import zutil.net.ws.WSParameterDef;
import zutil.net.ws.WebServiceDef;
import zutil.parser.DataNode;
import zutil.parser.json.JSONWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * A OpenAPI specification generator class.
 *
 * @see <a href="https://swagger.io/specification/">OpenAPI Specification</a>
 */
public class OpenAPIWriter {
    private static final Logger logger = LogUtil.getLogger();

    private static final String OPENAPI_VERSION = "3.0.1";

    /** Current Web service definition **/
    private WebServiceDef ws;
    /** Current Web service definition **/
    private List<ServerData> servers = new ArrayList<>();
    /** Cache of generated WSDL **/
    private String cache;


    public OpenAPIWriter(WebServiceDef ws) {
        this.ws = ws;
    }


    public void addServer(String url, String description){
        servers.add(new ServerData(url, description));
        cache = null;
    }


    public void write(Writer out) throws IOException {
        out.write(write());
    }

    public void write(PrintStream out) {
        out.print(write());
    }

    public void write(OutputStream out) throws IOException {
        out.write(write().getBytes());
    }

    public String write() {
        if (cache == null) {
            DataNode root = new DataNode(DataNode.DataType.Map);
            root.set("openapi", OPENAPI_VERSION);
            root.set("info", generateInfo());
            root.set("servers", generateServers());
            root.set("paths", generatePaths());
            root.set("components", generateComponents());

            this.cache = JSONWriter.toString(root);
        }
        return cache;
    }

    private DataNode generateInfo() {
        DataNode infoRoot = new DataNode(DataNode.DataType.Map);
        infoRoot.set("title", ws.getName());
        infoRoot.set("description", ws.getDocumentation());

        // Not implemented properties
        // "termsOfService": xxx,
        // "contact": {"name": xxx,"url": xxx,"email": xxx},
        // "license": {"name": xxx, "url": xxx},
        // "version": xxx
        return infoRoot;
    }

    private DataNode generateServers() {
        DataNode serversRoot = new DataNode(DataNode.DataType.List);

        for (ServerData data : servers) {
            DataNode serverNode = new DataNode(DataNode.DataType.Map);
            serverNode.set("url", data.url);
            serverNode.set("description", data.description);
            serversRoot.add(serverNode);
        }

        return serversRoot;
    }

    private DataNode generatePaths() {
        DataNode pathsRoot = new DataNode(DataNode.DataType.Map);

        for (WSMethodDef methodDef : ws.getMethods()) {
            DataNode pathNode = new DataNode(DataNode.DataType.Map);

            DataNode typeNode = new DataNode(DataNode.DataType.Map);
            typeNode.set("description", methodDef.getDocumentation());
            pathNode.set(methodDef.getRequestType().toString().toLowerCase(), typeNode);

            // --------------------------------------------
            // Inputs
            // --------------------------------------------

            DataNode parameterNode = new DataNode(DataNode.DataType.Map);
            for (WSParameterDef parameterDef : methodDef.getInputs()) {
                parameterNode.set("name", parameterDef.getName());
                parameterNode.set("description", parameterDef.getDocumentation());
                parameterNode.set("in", "query");
                parameterNode.set("required", parameterDef.isOptional());

                parameterNode.set("schema", "");
            }
            typeNode.set("parameters", parameterNode);

            // --------------------------------------------
            // Outputs
            // --------------------------------------------

            DataNode responseNode = new DataNode(DataNode.DataType.Map);
            for (WSParameterDef parameterDef : methodDef.getOutputs()) {
                parameterNode.set("name", parameterDef.getName());
                parameterNode.set("description", parameterDef.getDocumentation());
                parameterNode.set("in", "query");
                parameterNode.set("required", parameterDef.isOptional());

                parameterNode.set("schema", "");
            }
            typeNode.set("responses", responseNode);

        }

        return pathsRoot;
    }

    private DataNode generateComponents() {
        DataNode componentsRoot = new DataNode(DataNode.DataType.Map);
        DataNode schemasNode = new DataNode(DataNode.DataType.Map);
        componentsRoot.set("schemas", schemasNode);

        // Generate schemas

        return componentsRoot;
    }


    protected static class ServerData {
        String url;
        String description;

        protected ServerData(String url, String description) {
            this.url = url;
            this.description = description;
        }
    }
}
