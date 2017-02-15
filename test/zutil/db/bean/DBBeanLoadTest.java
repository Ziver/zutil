package zutil.db.bean;

import org.junit.BeforeClass;
import org.junit.Test;
import zutil.db.DBConnection;
import zutil.log.CompactLogFormatter;
import zutil.log.LogUtil;

import java.sql.SQLException;
import java.util.logging.Level;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static zutil.db.bean.DBBeanTestBase.*;

/**
 *
 */
public class DBBeanLoadTest {

    private DBConnection db = new DBConnection(DBConnection.DBMS.SQLite, ":memory:");
    public DBBeanLoadTest() throws Exception {}

    @BeforeClass
    public static void init(){
        LogUtil.setGlobalFormatter(new CompactLogFormatter());
        LogUtil.setGlobalLevel(Level.ALL);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void simpleClassLoad() throws SQLException {
        simpleClassInit(db);
        insert(db, "SimpleTestClass", "5", "1234", "\"helloworld\"");
        SimpleTestClass obj = DBBean.load(db, SimpleTestClass.class, 5);

        assertEquals((Long)5L, obj.getId());
        assertEquals(1234, obj.intField);
        assertEquals("helloworld", obj.strField);
    }


    @Test
    public void simpleClassCache() throws SQLException {
        simpleClassInit(db);
        insert(db, "SimpleTestClass", "5", "1234", "\"helloworld\"");
        SimpleTestClass obj1 = DBBean.load(db, SimpleTestClass.class, 5);
        SimpleTestClass obj2 = DBBean.load(db, SimpleTestClass.class, 5);

        assertSame(obj1, obj2);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void aliasFieldsLoad() throws SQLException {
        aliasFieldsInit(db);
        insert(db, "aliasTable", "5", "1234", "\"helloworld\"");
        AliasFieldsTestClass obj = DBBean.load(db, AliasFieldsTestClass.class, 5);

        assertEquals((Long)5L, obj.getId());
        assertEquals(1234, obj.intField);
        assertEquals("helloworld", obj.strField);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void emptySubObjectLoad() throws SQLException {
        subObjectInit(db);
        insert(db, "parent", "5");
        ParentTestClass obj = DBBean.load(db, ParentTestClass.class, 5);

        assertEquals((Long)5L, obj.getId());
        assertEquals(0, obj.subobjs.size());
    }

    @Test
    public void subObjectLoad() throws SQLException {
        subObjectInit(db);
        insert(db, "parent", "5");
        insert(db, "subobject", "10", "5", "1234");
        ParentTestClass obj = DBBean.load(db, ParentTestClass.class, 5);

        assertEquals(1, obj.subobjs.size());
        assertEquals((Long)10L, obj.subobjs.get(0).getId());
        assertEquals(1234, obj.subobjs.get(0).intField);
    }

    @Test
    public void multiSubObjectLoad() throws SQLException {
        subObjectInit(db);
        insert(db, "parent", "5");
        insert(db, "subobject", "10", "5", "1001");
        insert(db, "subobject", "11", "5", "1002");
        insert(db, "subobject", "12", "5", "1003");
        insert(db, "subobject", "13", "5", "1004");
        ParentTestClass obj = DBBean.load(db, ParentTestClass.class, 5);

        assertEquals(4, obj.subobjs.size());
        assertEquals((Long)10L, obj.subobjs.get(0).getId());
        assertEquals(1001, obj.subobjs.get(0).intField);
        assertEquals((Long)11L, obj.subobjs.get(1).getId());
        assertEquals(1002, obj.subobjs.get(1).intField);
        assertEquals((Long)12L, obj.subobjs.get(2).getId());
        assertEquals(1003, obj.subobjs.get(2).intField);
        assertEquals((Long)13L, obj.subobjs.get(3).getId());
        assertEquals(1004, obj.subobjs.get(3).intField);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void emptySubLinkObjectLoad() throws SQLException {
        subLinkObjectInit(db);
        insert(db, "parent", "5");
        ParentLinkTestClass obj = DBBean.load(db, ParentLinkTestClass.class, 5);

        assertEquals((Long)5L, obj.getId());
        assertEquals(0, obj.subobjs.size());
    }

    @Test
    public void subLinkObjectLoad() throws SQLException {
        subLinkObjectInit(db);
        insert(db, "parent", "5");
        insert(db, "link", "5", "10");
        insert(db, "subobject", "10", "1234");
        ParentLinkTestClass obj = DBBean.load(db, ParentLinkTestClass.class, 5);

        assertEquals(1, obj.subobjs.size());
        assertEquals((Long)10L, obj.subobjs.get(0).getId());
        assertEquals(1234, obj.subobjs.get(0).intField);
    }

    @Test
    public void multiSubLinkObjectLoad() throws SQLException {
        subLinkObjectInit(db);
        insert(db, "parent", "5");
        insert(db, "link", "5", "10");
        insert(db, "link", "5", "11");
        insert(db, "link", "5", "12");
        insert(db, "subobject", "10", "1001");
        insert(db, "subobject", "11", "1002");
        insert(db, "subobject", "12", "1003");
        ParentLinkTestClass obj = DBBean.load(db, ParentLinkTestClass.class, 5);

        assertEquals(3, obj.subobjs.size());
        assertEquals((Long)10L, obj.subobjs.get(0).getId());
        assertEquals(1001, obj.subobjs.get(0).intField);
        assertEquals((Long)11L, obj.subobjs.get(1).getId());
        assertEquals(1002, obj.subobjs.get(1).intField);
        assertEquals((Long)12L, obj.subobjs.get(2).getId());
        assertEquals(1003, obj.subobjs.get(2).intField);
    }
}