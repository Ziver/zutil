package zutil.db.bean;

import org.junit.BeforeClass;
import org.junit.Test;
import zutil.db.DBConnection;
import zutil.db.bean.DBBeanTestBase.*;
import zutil.log.CompactLogFormatter;
import zutil.log.LogUtil;

import java.sql.SQLException;
import java.util.logging.Level;

import static org.junit.Assert.*;
import static zutil.db.bean.DBBeanTestBase.*;

/**
 *
 */
public class DBBeanSaveTest {

    private DBConnection db = new DBConnection(DBConnection.DBMS.SQLite, ":memory:");
    public DBBeanSaveTest() throws Exception {}

    @BeforeClass
    public static void init(){
        LogUtil.setGlobalFormatter(new CompactLogFormatter());
        LogUtil.setGlobalLevel(Level.ALL);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void simpleClassCreate() throws SQLException {
        SimpleTestClass obj = simpleClassInit(db);
        obj.save(db);

        assertEquals(1234,
                getColumnValue(db, "SimpleTestClass", "intField"));
        assertEquals("helloworld",
                getColumnValue(db, "SimpleTestClass", "strField"));
    }

    @Test
    public void simpleClassUpdate() throws SQLException {
        SimpleTestClass obj = simpleClassInit(db);
        obj.save(db);
        obj.intField = 1337;
        obj.strField = "monkey";
        obj.save(db);

        assertEquals(1337,
                getColumnValue(db, "SimpleTestClass", "intField"));
        assertEquals("monkey",
                getColumnValue(db, "SimpleTestClass", "strField"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void aliasFieldsCreate() throws SQLException {
        AliasFieldsTestClass obj = aliasFieldsInit(db);
        obj.save(db);

        assertEquals(1234,
                getColumnValue(db, "aliasTable", "aliasIntField"));
        assertEquals("helloworld",
                getColumnValue(db, "aliasTable", "aliasStrField"));
    }

    @Test
    public void aliasFieldsUpdate() throws SQLException {
        AliasFieldsTestClass obj = aliasFieldsInit(db);
        obj.save(db);
        obj.intField = 1337;
        obj.strField = "monkey";
        obj.save(db);

        assertEquals(1337,
                getColumnValue(db, "aliasTable", "aliasIntField"));
        assertEquals("monkey",
                getColumnValue(db, "aliasTable", "aliasStrField"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void subObjectCreate() throws SQLException {
        ParentTestClass obj = subObjectInit(db);
        obj.save(db);

        assertEquals(1234,
                getColumnValue(db, "subobject", "intField"));
        assertEquals(1,
                getColumnValue(db, "subobject", "parent_id"));
    }

    @Test
    public void subObjectUpdate() throws SQLException {
        ParentTestClass obj = subObjectInit(db);
        obj.save(db);
        obj.subobjs.get(0).intField = 1337;
        obj.save(db);

        assertEquals("Check for duplicates",1, getRowCount(db, "subobject"));
        assertEquals(1337,
                getColumnValue(db, "subobject", "intField"));
        assertEquals(1,
                getColumnValue(db, "subobject", "parent_id"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void subLinkObjectCreate() throws SQLException {
        ParentLinkTestClass obj = subLinkObjectInit(db);
        obj.save(db);

        assertEquals(1,
                getColumnValue(db, "link", "parent_id"));
        assertEquals(1,
                getColumnValue(db, "link", "id"));
        assertEquals(1234,
                getColumnValue(db, "subobject", "intField"));
    }

    @Test
    public void subLinkObjectUpdate() throws SQLException {
        ParentLinkTestClass obj = subLinkObjectInit(db);
        obj.save(db);
        obj.subobjs.get(0).intField = 1337;
        obj.save(db);

        assertEquals("Check for duplicates",1, getRowCount(db, "subobject"));
        assertEquals("Check for duplicate links",1, getRowCount(db, "link"));
        assertEquals(1,
                getColumnValue(db, "link", "parent_id"));
        assertEquals(1,
                getColumnValue(db, "link", "id"));
        assertEquals(1337,
                getColumnValue(db, "subobject", "intField"));
    }

}