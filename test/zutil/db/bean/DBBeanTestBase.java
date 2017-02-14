package zutil.db.bean;

import zutil.StringUtil;
import zutil.db.DBConnection;
import zutil.db.handler.SimpleSQLResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class DBBeanTestBase {


    public static class SimpleTestClass extends DBBean{
        int intField;
        String strField;
    }

    public static SimpleTestClass simpleClassInit(DBConnection db) throws SQLException {
        db.exec("CREATE TABLE SimpleTestClass (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "intField INTEGER, " +
                "strField TEXT);");

        SimpleTestClass obj = new SimpleTestClass();
        obj.intField = 1234;
        obj.strField = "helloworld";
        return obj;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @DBBean.DBTable("aliasTable")
    public static class AliasFieldsTestClass extends DBBean{
        @DBColumn("aliasIntField")
        int intField;
        @DBColumn("aliasStrField")
        String strField;
    }

    public static AliasFieldsTestClass aliasFieldsInit(DBConnection db) throws SQLException {
        db.exec("CREATE TABLE aliasTable (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "aliasIntField INTEGER, " +
                "aliasStrField TEXT);");

        AliasFieldsTestClass obj = new AliasFieldsTestClass();
        obj.intField = 1234;
        obj.strField = "helloworld";
        return obj;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @DBBean.DBTable("parent")
    public static class ParentTestClass extends DBBean{
        @DBLinkTable(table = "subobject", idColumn = "parent_id",beanClass = SubObjectTestClass.class)
        List<SubObjectTestClass> subobjs = new ArrayList<>();
    }
    @DBBean.DBTable("subobject")
    public static class SubObjectTestClass extends DBBean{
        int intField;
    }

    public static ParentTestClass subObjectInit(DBConnection db) throws SQLException {
        db.exec("CREATE TABLE parent (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT);");
        db.exec("CREATE TABLE subobject (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "parent_id INTEGER, " +
                "intField INTEGER);");

        ParentTestClass obj = new ParentTestClass();
        SubObjectTestClass subObj = new SubObjectTestClass();
        subObj.intField = 1234;
        obj.subobjs.add(subObj);
        return obj;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @DBBean.DBTable("parent")
    public static class ParentLinkTestClass extends DBBean{
        @DBLinkTable(table = "link", idColumn = "parent_id",beanClass = SubObjectTestClass.class)
        List<SubObjectTestClass> subobjs = new ArrayList<>();
    }

    public static ParentLinkTestClass subLinkObjectInit(DBConnection db) throws SQLException {
        db.exec("CREATE TABLE parent (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT);");
        db.exec("CREATE TABLE link (" +
                "parent_id INTEGER, " +
                "id INTEGER);");
        db.exec("CREATE TABLE subobject (" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                "parent_id INTEGER, " +
                "intField INTEGER);");

        ParentLinkTestClass obj = new ParentLinkTestClass();
        SubObjectTestClass subObj = new SubObjectTestClass();
        subObj.intField = 1234;
        obj.subobjs.add(subObj);
        return obj;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    public static Object getColumnValue(DBConnection db, String table, String column) throws SQLException {
        return db.exec("SELECT "+column+" FROM "+table, new SimpleSQLResult<>());
    }

    public static Object getRowCount(DBConnection db, String table) throws SQLException {
        return db.exec("SELECT count(*) FROM "+table, new SimpleSQLResult<>());
    }

    public static void insert(DBConnection db, String table, String... values) throws SQLException {
        db.exec("INSERT INTO "+table+" VALUES("+
                StringUtil.join(",", values)+");");
    }
}