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
        insert(db, "SimpleTestClass", "10", "1234", "\"helloworld\"");
        SimpleTestClass obj = DBBean.load(db, SimpleTestClass.class, 10);

        assertEquals((Long)10L, obj.getId());
        assertEquals(1234, obj.intField);
        assertEquals("helloworld", obj.strField);
    }


    @Test
    public void simpleClassCache() throws SQLException {
        simpleClassInit(db);
        insert(db, "SimpleTestClass", "11", "1234", "\"helloworld\"");
        SimpleTestClass obj1 = DBBean.load(db, SimpleTestClass.class, 11);
        SimpleTestClass obj2 = DBBean.load(db, SimpleTestClass.class, 11);

        assertSame(obj1, obj2);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void aliasFieldsLoad() throws SQLException {
        aliasFieldsInit(db);
        insert(db, "aliasTable", "20", "1234", "\"helloworld\"");
        AliasFieldsTestClass obj = DBBean.load(db, AliasFieldsTestClass.class, 20);

        assertEquals((Long)20L, obj.getId());
        assertEquals(1234, obj.intField);
        assertEquals("helloworld", obj.strField);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void emptySubObjectLoad() throws SQLException {
        subObjectInit(db);
        insert(db, "parent", "30");
        ParentTestClass obj = DBBean.load(db, ParentTestClass.class, 30);

        assertEquals((Long)30L, obj.getId());
        assertEquals(0, obj.subobjs.size());
    }

    @Test
    public void subObjectLoad() throws SQLException {
        subObjectInit(db);
        insert(db, "parent", "31");
        insert(db, "subobject", "310", "31", "1234");
        ParentTestClass obj = DBBean.load(db, ParentTestClass.class, 31);

        assertEquals(1, obj.subobjs.size());
        assertEquals((Long)310L, obj.subobjs.get(0).getId());
        assertEquals(1234, obj.subobjs.get(0).intField);
    }

    @Test
    public void multiSubObjectLoad() throws SQLException {
        subObjectInit(db);
        insert(db, "parent", "32");
        insert(db, "subobject", "320", "32", "1001");
        insert(db, "subobject", "321", "32", "1002");
        insert(db, "subobject", "322", "32", "1003");
        insert(db, "subobject", "323", "32", "1004");
        ParentTestClass obj = DBBean.load(db, ParentTestClass.class, 32);

        assertEquals(4, obj.subobjs.size());
        assertEquals((Long)320L, obj.subobjs.get(0).getId());
        assertEquals(1001, obj.subobjs.get(0).intField);
        assertEquals((Long)321L, obj.subobjs.get(1).getId());
        assertEquals(1002, obj.subobjs.get(1).intField);
        assertEquals((Long)322L, obj.subobjs.get(2).getId());
        assertEquals(1003, obj.subobjs.get(2).intField);
        assertEquals((Long)323L, obj.subobjs.get(3).getId());
        assertEquals(1004, obj.subobjs.get(3).intField);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void emptySubLinkObjectLoad() throws SQLException {
        subLinkObjectInit(db);
        insert(db, "parent", "40");
        ParentLinkTestClass obj = DBBean.load(db, ParentLinkTestClass.class, 40);

        assertEquals((Long)40L, obj.getId());
        assertEquals(0, obj.subobjs.size());
    }

    @Test
    public void subLinkObjectLoad() throws SQLException {
        subLinkObjectInit(db);
        insert(db, "parent", "41");
        insert(db, "link", "41", "410");
        insert(db, "subobject", "410", "1234");
        ParentLinkTestClass obj = DBBean.load(db, ParentLinkTestClass.class, 41);

        assertEquals(1, obj.subobjs.size());
        assertEquals((Long)410L, obj.subobjs.get(0).getId());
        assertEquals(1234, obj.subobjs.get(0).intField);
    }

    @Test
    public void multiSubLinkObjectLoad() throws SQLException {
        subLinkObjectInit(db);
        insert(db, "parent", "42");
        insert(db, "link", "42", "420");
        insert(db, "link", "42", "421");
        insert(db, "link", "42", "422");
        insert(db, "subobject", "420", "1001");
        insert(db, "subobject", "421", "1002");
        insert(db, "subobject", "422", "1003");
        ParentLinkTestClass obj = DBBean.load(db, ParentLinkTestClass.class, 42);

        assertEquals(3, obj.subobjs.size());
        assertEquals((Long)420L, obj.subobjs.get(0).getId());
        assertEquals(1001, obj.subobjs.get(0).intField);
        assertEquals((Long)421L, obj.subobjs.get(1).getId());
        assertEquals(1002, obj.subobjs.get(1).intField);
        assertEquals((Long)422L, obj.subobjs.get(2).getId());
        assertEquals(1003, obj.subobjs.get(2).intField);
    }
}