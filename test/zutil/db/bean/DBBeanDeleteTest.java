package zutil.db.bean;

import org.junit.BeforeClass;
import org.junit.Test;
import zutil.db.DBConnection;
import zutil.log.CompactLogFormatter;
import zutil.log.LogUtil;

import java.sql.SQLException;
import java.util.logging.Level;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static zutil.db.bean.DBBeanTestBase.*;

/**
 *
 */
public class DBBeanDeleteTest {

    private DBConnection db = new DBConnection(DBConnection.DBMS.SQLite, ":memory:");
    public DBBeanDeleteTest() throws Exception {}

    @BeforeClass
    public static void init(){
        LogUtil.setGlobalFormatter(new CompactLogFormatter());
        LogUtil.setGlobalLevel(Level.ALL);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void simpleClassDelete() throws SQLException {
        SimpleTestClass obj = simpleClassInit(db);
        obj.save(db);
        obj.delete(db);

        assertEquals(0, DBBeanTestBase.getRowCount(db, "SimpleTestClass"));
        assertNull(obj.getId());
        assertNull(DBBeanCache.get(SimpleTestClass.class, 1L));
    }

    @Test(expected=NullPointerException.class)
    public void notSavedSimpleClassDelete() throws SQLException {
        simpleClassInit(db).save(db);
        SimpleTestClass obj = new SimpleTestClass();
        obj.delete(db); // Exception
    }

    @Test
    public void multiSimpleClassDelete() throws SQLException {
        SimpleTestClass obj = simpleClassInit(db);
        obj.save(db);
        for(int i=0; i<5; ++i)
            new SimpleTestClass().save(db);
        obj.delete(db);

        assertEquals(5, DBBeanTestBase.getRowCount(db, "SimpleTestClass"));
        assertNull(obj.getId());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void emptySubObjectDelete() throws SQLException {
        subObjectInit(db).save(db);
        ParentTestClass obj = new ParentTestClass();
        obj.save(db);
        obj.delete(db);

        assertEquals(1, DBBeanTestBase.getRowCount(db, "parent"));
        assertEquals(1, DBBeanTestBase.getRowCount(db, "subobject"));
    }

    @Test
    public void subObjectDelete() throws SQLException {
        ParentTestClass obj = subObjectInit(db);
        obj.save(db);
        obj.delete(db);

        assertEquals("Parent table size", 0, DBBeanTestBase.getRowCount(db, "parent"));
        assertEquals("SubObject table size", 0, DBBeanTestBase.getRowCount(db, "subobject"));
        assertNull(DBBeanCache.get(ParentTestClass.class, 1L));
        assertNull(DBBeanCache.get(SubObjectTestClass.class, 1L));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void emptySubLinkObjectDelete() throws SQLException {
        subLinkObjectInit(db).save(db);
        ParentLinkTestClass obj = new ParentLinkTestClass();
        obj.save(db);
        obj.delete(db);

        assertEquals("Parent table size", 1, DBBeanTestBase.getRowCount(db, "parent"));
        assertEquals("Link table size", 1, DBBeanTestBase.getRowCount(db, "link"));
        assertEquals("SubObject table size", 1, DBBeanTestBase.getRowCount(db, "subobject"));
    }

    @Test
    public void subLinkObjectDelete() throws SQLException {
        ParentLinkTestClass obj = subLinkObjectInit(db);
        obj.save(db);
        obj.delete(db);

        assertEquals("Parent table size", 0, DBBeanTestBase.getRowCount(db, "parent"));
        assertEquals("Link table size", 0, DBBeanTestBase.getRowCount(db, "link"));
        assertEquals("SubObject table size", 0, DBBeanTestBase.getRowCount(db, "subobject"));
        assertNull(DBBeanCache.get(ParentTestClass.class, 1L));
        assertNull(DBBeanCache.get(SubObjectTestClass.class, 1L));
    }
}