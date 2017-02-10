package zutil.db.bean;

import org.junit.BeforeClass;
import org.junit.Test;
import zutil.db.DBConnection;
import zutil.db.handler.SimpleSQLResult;
import zutil.log.CompactLogFormatter;
import zutil.log.LogUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static org.junit.Assert.*;

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

    private static class SimpleTestClass extends DBBean{
        int intField;
        String strField;
    }

    @Test
    public void simpleClassCreate() throws SQLException {
        db.exec("CREATE TABLE SimpleTestClass (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "intField INTEGER, " +
                "strField TEXT);");

        SimpleTestClass obj = new SimpleTestClass();
        obj.intField = 1234;
        obj.strField = "helloworld";
        obj.save(db);

        assertEquals(1234,
                getColumnValue(db, "SimpleTestClass", "intField"));
        assertEquals("helloworld",
                getColumnValue(db, "SimpleTestClass", "strField"));
    }

    @Test
    public void simpleClassUpdate() throws SQLException {
        db.exec("CREATE TABLE SimpleTestClass (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "intField INTEGER, " +
                "strField TEXT);");

        SimpleTestClass obj = new SimpleTestClass();
        obj.intField = 1234;
        obj.strField = "helloworld";
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

    @DBBean.DBTable("aliasTable")
    private static class AliasFieldsTestClass extends DBBean{
        @DBColumn("aliasIntField")
        int intField;
        @DBColumn("aliasStrField")
        String strField;
    }

    @Test
    public void aliasFieldsCreate() throws SQLException {
        db.exec("CREATE TABLE aliasTable (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "aliasIntField INTEGER, " +
                "aliasStrField TEXT);");

        AliasFieldsTestClass obj = new AliasFieldsTestClass();
        obj.intField = 1234;
        obj.strField = "helloworld";
        obj.save(db);

        assertEquals(1234,
                getColumnValue(db, "aliasTable", "aliasIntField"));
        assertEquals("helloworld",
                getColumnValue(db, "aliasTable", "aliasStrField"));
    }

    @Test
    public void aliasFieldsUpdate() throws SQLException {
        db.exec("CREATE TABLE aliasTable (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "aliasIntField INTEGER, " +
                "aliasStrField TEXT);");

        AliasFieldsTestClass obj = new AliasFieldsTestClass();
        obj.intField = 1234;
        obj.strField = "helloworld";
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

    @DBBean.DBTable("parent")
    private static class ParentTestClass extends DBBean{
        @DBLinkTable(table = "subobject", idColumn = "parent_id",beanClass = SubObjectTestClass.class)
        List<SubObjectTestClass> subobjs = new ArrayList<>();
    }
    @DBBean.DBTable("subobject")
    private static class SubObjectTestClass extends DBBean{
        int intField;
    }

    @Test
    public void subObjectCreate() throws SQLException {
        db.exec("CREATE TABLE parent (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT);");
        db.exec("CREATE TABLE subobject (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "parent_id INTEGER, " +
                "intField INTEGER);");

        ParentTestClass obj = new ParentTestClass();
        SubObjectTestClass subObj = new SubObjectTestClass();
        subObj.intField = 1337;
        obj.subobjs.add(subObj);
        obj.save(db);

        assertEquals(1337,
                getColumnValue(db, "subobject", "intField"));
        assertEquals(1,
                getColumnValue(db, "subobject", "parent_id"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @DBBean.DBTable("parent")
    private static class ParentLinkTestClass extends DBBean{
        @DBLinkTable(table = "link", idColumn = "parent_id",beanClass = SubObjectTestClass.class)
        List<SubObjectTestClass> subobjs = new ArrayList<>();
    }

    @Test
    public void subLinkObjectCreate() throws SQLException {
        db.exec("CREATE TABLE parent (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT);");
        db.exec("CREATE TABLE link (" +
                "parent_id INTEGER, " +
                "id INTEGER);");
        db.exec("CREATE TABLE subobject (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "parent_id INTEGER, " +
                "intField INTEGER);");

        ParentTestClass obj = new ParentTestClass();
        SubObjectTestClass subObj = new SubObjectTestClass();
        subObj.intField = 1337;
        obj.subobjs.add(subObj);
        obj.save(db);

        assertEquals(1,
                getColumnValue(db, "link", "parent_id"));
        assertEquals(1,
                getColumnValue(db, "link", "id"));
        assertEquals(1337,
                getColumnValue(db, "subobject", "intField"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    public static Object getColumnValue(DBConnection db, String table, String column) throws SQLException {
        return db.exec("SELECT "+column+" FROM "+table, new SimpleSQLResult<>());
    }
}