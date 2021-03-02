package zutil.net.ws.openapi;

import zutil.ClassUtil;
import zutil.log.LogUtil;
import zutil.net.ws.*;
import zutil.parser.DataNode;
import zutil.parser.json.JSONWriter;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

/**
 * A OpenAPI specification generator class.
 *
 * @see <a href="https://swagger.io/specification/">OpenAPI Specification</a>
 */
@SuppressWarnings("rawtypes")
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
            List<Class> objSchemas = new ArrayList<>();

            DataNode root = new DataNode(DataNode.DataType.Map);
            root.set("openapi", OPENAPI_VERSION);
            root.set("info", generateInfo());
            root.set("servers", generateServers());
            root.set("paths", generatePaths(objSchemas));
            root.set("components", generateComponents(objSchemas));

            this.cache = JSONWriter.toString(root);
        }
        return cache;
    }

    private DataNode generateInfo() {
        DataNode infoRoot = new DataNode(DataNode.DataType.Map);
        infoRoot.set("title", ws.getName());
        infoRoot.set("version", "");

        if (ws.getDocumentation() != null)
            infoRoot.set("description", ws.getDocumentation());

        // Not implemented properties
        // "termsOfService": xxx,
        // "contact": {"name": xxx,"url": xxx,"email": xxx},
        // "license": {"name": xxx, "url": xxx},
        return infoRoot;
    }

    private DataNode generateServers() {
        DataNode serversRoot = new DataNode(DataNode.DataType.List);

        for (ServerData data : servers) {
            DataNode serverNode = serversRoot.add(DataNode.DataType.Map);
            serverNode.set("url", data.url);
            serverNode.set("description", data.description);
        }

        return serversRoot;
    }

    private DataNode generatePaths(List<Class> objSchemas) {
        DataNode pathsRoot = new DataNode(DataNode.DataType.Map);

        for (WSMethodDef methodDef : ws.getMethods()) {
            DataNode pathNode = pathsRoot.set(methodDef.getPath(), DataNode.DataType.Map);

            DataNode typeNode = pathNode.set(methodDef.getRequestType().toString().toLowerCase(), DataNode.DataType.Map);

            if (methodDef.getDocumentation() != null)
                typeNode.set("description", methodDef.getDocumentation());

            // --------------------------------------------
            // Inputs
            // --------------------------------------------

            DataNode parametersNode = typeNode.set("parameters", DataNode.DataType.List);
            for (WSParameterDef parameterDef : methodDef.getInputs()) {
                DataNode parameterNode = parametersNode.add(DataNode.DataType.Map);
                parameterNode.set("name", parameterDef.getName());
                parameterNode.set("in", "query");
                parameterNode.set("required", !parameterDef.isOptional());

                if (parameterDef.getDocumentation() != null)
                    parameterNode.set("description", parameterDef.getDocumentation());

                DataNode schemaNode = parameterNode.set("schema", DataNode.DataType.Map);
                generateSchema(schemaNode, parameterDef.getParamClass(), true, objSchemas);
            }

            // --------------------------------------------
            // Outputs
            // --------------------------------------------

            DataNode responseNode = typeNode.set("responses", DataNode.DataType.Map);
            DataNode successNode = responseNode.set("200", DataNode.DataType.Map);
            successNode.set("description", "A successful response.");

            if (methodDef.getOutputClass() != void.class) {
                DataNode schemaNode = successNode.set("content", DataNode.DataType.Map)
                        .set("application/json", DataNode.DataType.Map)
                        .set("schema", DataNode.DataType.Map);
                generateSchema(schemaNode, methodDef.getOutputClass(), true, objSchemas);
            }
        }

        return pathsRoot;
    }

    private DataNode generateComponents(List<Class> objSchemas) {
        DataNode componentsRoot = new DataNode(DataNode.DataType.Map);
        DataNode schemasNode = componentsRoot.set("schemas", DataNode.DataType.Map);

        // Generate schemas

        for (int i=0; i<objSchemas.size(); i++) {
            Class clazz = objSchemas.get(i);

            DataNode objectNode = schemasNode.set(clazz.getSimpleName(), DataNode.DataType.Map);
            generateSchema(objectNode, clazz, false, objSchemas);
        }

        return componentsRoot;
    }

    private void generateSchema(DataNode parent, Class<?> clazz, boolean reference, List<Class> objSchemas) {
        if (clazz == void.class)
            return;

        if (ClassUtil.isPrimitive(clazz) || ClassUtil.isWrapper(clazz)) {
            parent.set("type", getOpenAPIType(clazz));

            if (clazz == byte.class || clazz == Byte.class)
                parent.set("format", "byte");
        } else if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
            parent.set("type", "array");

            DataNode itemsNode = parent.set("items", DataNode.DataType.Map);
            generateSchema(itemsNode, ClassUtil.getArrayClass(clazz, 1), reference, objSchemas);
        } else {
            parent.set("type", "object");

            if (reference) {
                if (!objSchemas.contains(clazz))
                    objSchemas.add(clazz);
                parent.set("$ref", "#/components/schemas/" + clazz.getSimpleName());
            } else {
                if (WSReturnObject.class.isAssignableFrom(clazz)) {
                    DataNode propertiesNode = parent.set("properties", DataNode.DataType.Map);
                    for (Field field : clazz.getFields()) {
                        String fieldName = field.getName();

                        WSInterface.WSParamName paramNameAnnotation = field.getAnnotation(WSInterface.WSParamName.class);
                        if (paramNameAnnotation != null)
                            fieldName = paramNameAnnotation.value();

                        DataNode parameterNode = propertiesNode.set(fieldName, DataNode.DataType.Map);
                        generateSchema(parameterNode, field.getType(), false, objSchemas);
                    }
                }
            }
        }
    }


    private String getOpenAPIType(Class<?> clazz) {
        switch (clazz.getName()) {
            case "int":
            case "java.lang.Integer":
                return "integer";

            case "float":
            case "java.lang.Float":
            case "double":
            case "java.lang.Double":
                return "number";

            case "byte":
            case "java.lang.Byte":
            case "java.lang.String":
                return "string";

            case "boolean":
            case "java.lang.Boolean":
                return "boolean";
        }

        return null;
    }

    /**
     * Class containing Target API server information.
     */
    protected static class ServerData {
        String url;
        String description;

        protected ServerData(String url, String description) {
            this.url = url;
            this.description = description;
        }
    }
}
