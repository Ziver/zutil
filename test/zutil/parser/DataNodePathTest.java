package zutil.parser;

import org.junit.Test;
import zutil.parser.json.JSONParser;
import zutil.parser.json.JSONWriter;

import static org.junit.Assert.*;

public class DataNodePathTest {

    @Test
    public void invalidPath() {
        assertThrows(IllegalArgumentException.class, () -> DataNodePath.search("", new DataNode(DataNode.DataType.Map)));
        assertThrows(IllegalArgumentException.class, () -> DataNodePath.search("aa", new DataNode(DataNode.DataType.Map)));
        assertThrows(IllegalArgumentException.class, () -> DataNodePath.search("11", new DataNode(DataNode.DataType.Map)));
    }

    @Test
    public void mapPath() {
        String json = "{child1: 'test', child2: {child3: 'banana'}}";
        DataNode rootNode = JSONParser.read(json);

        assertEquals("test", DataNodePath.search("$.child1", rootNode).getString());
        assertEquals("banana", DataNodePath.search("$.child2.child3", rootNode).getString());

        assertEquals("test", DataNodePath.search("$['child1']", rootNode).getString());
        assertEquals("banana", DataNodePath.search("$['child2']['child3']", rootNode).getString());
    }

    @Test
    public void arrayPath() {
        String json = "{child1: ['test', {child2: 'banana'}]}";
        DataNode rootNode = JSONParser.read(json);

        assertEquals("test", DataNodePath.search("$.child1[0]", rootNode).getString());
        assertEquals("banana", DataNodePath.search("$.child1[1].child2", rootNode).getString());
    }

    @Test
    public void starPath() {
        String json = "{}";
        DataNode rootNode = JSONParser.read(json);

        assertNull(DataNodePath.search("$.child1.*", rootNode));
        assertNull(DataNodePath.search("$.child1[*]", rootNode));

        json = "{child1: []}";
        rootNode = JSONParser.read(json);

        assertEquals("[]", JSONWriter.toString(DataNodePath.search("$.child1.*", rootNode)));
        assertEquals("[]", JSONWriter.toString(DataNodePath.search("$.child1[*]", rootNode)));

        json = "{child1: ['test1', 'test2']}";
        rootNode = JSONParser.read(json);

        assertEquals("[\"test1\", \"test2\"]", JSONWriter.toString(DataNodePath.search("$.child1.*", rootNode)));
        assertEquals("[\"test1\", \"test2\"]", JSONWriter.toString(DataNodePath.search("$.child1[*]", rootNode)));
    }
}