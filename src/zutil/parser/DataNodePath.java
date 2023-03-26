package zutil.parser;

import zutil.ObjectUtil;
import zutil.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a class that implements the JSONPath syntax to lookup eöements of a DataNode structure.
 * <br>
 * Operators (From JSONPath spec):
 * <table>
 *     <tr><th>Operator</th><th>Description</th></tr>
 *     <tr><td><code>$</code></td><td>The root element to query. This starts all path expressions.</td></tr>
 *     <tr><td><code>*</code></td><td>Wildcard. Available anywhere a name or numeric are required.</td></tr>
 *     <tr><td><code>.&lt;name&gt;</code></td><td>Dot-notated child</td></tr>
 *     <tr><td><code>['&lt;name&gt;']</code></td><td>Bracket-notated child</td></tr>
 *     <tr><td><code>[&lt;number&gt;]</code></td><td>Array index</td></tr>
 * </table>
 *
 * Examples:
 * <table>
 *     <tr><td> <strong>JSONPath</strong> </td><td> <strong>Result</strong> </td></tr>
 *     <tr><td class="lft"><code>$.store.book[*].author</code> </td><td class="lft">a list of all authors for the books in the store </td></tr>
 *     <tr><td class="lft"><code>$.store.*</code> </td><td class="lft">all things in store, which are some books and a red bicycle. </td></tr>
 * </table>
 *
 * @see <a href="https://github.com/json-path/JsonPath">JSONPath Reference</a>
 */
// TODO:  *     <tr><td><code>['&lt;name&gt;' , '&lt;name&gt;']</code></td><td>Bracket-notated children</td></tr>
// TODO:  *     <tr><td><code>[&lt;number&gt; , &lt;number&gt;]</code></td><td>Array indexes</td></tr>
// TODO:  *     <tr><td><code>@</code></td><td>The current node being processed by a filter predicate.</td></tr>
// TODO:  *     <tr><td><code>..</code></td><td>Deep scan. Available anywhere a name is required.</td></tr>
// TODO:  *     <tr><td><code>[start:end]</code></td><td>Array slice operator</td></tr>
// TODO:  *     <tr><td><code>[?(&lt;expression&gt;)]</code></td><td>Filter expression. Expression must evaluate to a boolean value.</td></tr>
public class DataNodePath {
    private final String path;
    private List<PathEntity> pathEntityList;


    public DataNodePath(String path) {
        this.path = path;
        pathEntityList = parsePath(path);
    }

    private static List<PathEntity> parsePath(String path) {
        ArrayList<PathEntity> pathList = new ArrayList<>();

        if (ObjectUtil.isEmpty(path) || path.charAt(0) != '$')
            throw new IllegalArgumentException("Path string must start with $");

        char operator = 0;
        StringBuilder buffer = new StringBuilder();

        for (int i = 1; i <= path.length(); i++) {
            char c = (i < path.length() ? path.charAt(i) : 0);
            switch (c) {
                case 0:   // End of String
                case '.':
                case '[':
                case ']':
                case '*':
                    // Finalize previous operand
                    if (c == '*') {
                        pathList.add(new AllChildPathEntity());
                    } else if (operator == '.') {
                        pathList.add(new NamedChildPathEntity(buffer.toString()));
                    } else if (operator == '[') {
                        if (buffer.charAt(0) == '\'' && buffer.charAt(buffer.length()-1) == '\'')
                            pathList.add(new NamedChildPathEntity(StringUtil.trim(buffer.toString(), '\'')));
                        else
                            pathList.add(new IndexChildPathEntity(Integer.parseInt(buffer.toString())));
                    }

                    buffer.delete(0, buffer.length());

                    // Start next operand
                    operator = c;
                    break;

                case '\'': // Read in everything until next quote
                    buffer.append(c);
                    for (; i < path.length(); i++) {
                        char c2 = path.charAt(i);

                        buffer.append(c2);
                        if (c2 == c)
                            break;
                    }
                    break;

                default:
                    buffer.append(c);
            }
        }

        return pathList;
    }


    /**
     * @return the String path this object was initialized with.
     */
    public String getPath() {
        return path;
    }


    /**
     * This method will search the path and return the node that is found.
     *
     * @param path     the path to search for.
     * @param rootNode the root node where the path search should start from.
     * @return a DataNode corresponding to the configured path.
     */
    public static DataNode search(String path, DataNode rootNode) {
        DataNodePath pather = new DataNodePath(path);
        return pather.search(rootNode);
    }

    /**
     * This method will execute the path and return the node that is found.
     *
     * @param rootNode the root node where the path search should start from.
     * @return a DataNode corresponding to the configured path.
     */
    public DataNode search(DataNode rootNode) {
        DataNode node = rootNode;

        for (PathEntity entity : pathEntityList) {
            node = entity.getNextNode(node);

            if (node == null) break;
        }
        return node;
    }

    // ******************************************
    // Path Entities
    // ******************************************

    private interface PathEntity {
        /**
         * @param rootNode the parent node the next node should be fetched from.
         * @return the next node based on the entity logic. Null if the next node is unavailable.
         */
        DataNode getNextNode(DataNode rootNode);
    }

    /**
     * This entity returns the child by key name.
     */
    private static class NamedChildPathEntity implements PathEntity {
        private String childName;

        public NamedChildPathEntity(String childName) {
            this.childName = childName;
        }

        @Override
        public DataNode getNextNode(DataNode rootNode) {
            if (rootNode.isMap())
                return rootNode.get(childName);
            return null;
        }
    }

    /**
     * This entity returns the child by key name.
     */
    private static class IndexChildPathEntity implements PathEntity {
        private int index;

        public IndexChildPathEntity(int index) {
            this.index = index;
        }

        @Override
        public DataNode getNextNode(DataNode rootNode) {
            if (rootNode.isList())
                return rootNode.get(index);
            return null;
        }
    }

    /**
     * This entity returns the child by key name.
     */
    private static class AllChildPathEntity implements PathEntity {
        @Override
        public DataNode getNextNode(DataNode rootNode) {
            if (rootNode.isList() || rootNode.isMap())
                return rootNode;
            return null;
        }
    }
}
