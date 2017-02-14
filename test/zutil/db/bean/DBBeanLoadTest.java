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
}