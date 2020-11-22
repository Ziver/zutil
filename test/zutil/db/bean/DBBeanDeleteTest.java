/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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