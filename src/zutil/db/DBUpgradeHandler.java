/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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

package zutil.db;

import zutil.StringUtil;
import zutil.db.handler.SimpleSQLResult;
import zutil.log.LogUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * This class will take a reference DB and alter
 * the source DB to have the same structure.
 * NOTE: only works with SQLite
 *
 * Created by Ziver
 */
public class DBUpgradeHandler {
    private static final Logger logger = LogUtil.getLogger();

    private DBConnection reference;
    private DBConnection target;
    private boolean forceUpgradeEnabled = false;
    private HashMap<String,String> tableRenameMap;
    private HashSet<String> ignoredTablesSet;


    public DBUpgradeHandler(DBConnection reference){
        this.tableRenameMap = new HashMap<>();
        this.ignoredTablesSet = new HashSet<>();
        this.reference = reference;
    }

    public void setTargetDB(DBConnection db){
        this.target = db;
    }

    /**
     * Will create a rename mapping where an existing table will be renamed.
     *
     * @param   oldTableName    current name of the table
     * @param   newTableName    new name that ol table will be renamed to.
     */
    public void setTableRenameMap(String oldTableName, String newTableName){
        this.tableRenameMap.put(oldTableName, newTableName);
    }

    /**
     * @param   table           a table name that will be ignored from the upgrade procedure
     */
    public void addIgnoredTable(String table){
        ignoredTablesSet.add(table);
    }

    /**
     * With the default behaviour unnecessary columns will not be removed.
     * But if forced upgrade is set to true then the upgrade handler will
     * create a new table and migrate the data from the old one to the new table.
     *
     * @param   enable
     */
    public void setForcedDBUpgrade(boolean enable){
        this.forceUpgradeEnabled = enable;
    }


    public void upgrade() throws SQLException {
        try {
            logger.fine("Starting upgrade transaction...");
            target.getConnection().setAutoCommit(false);

            upgradeRenameTables();
            upgradeCreateTables();
            upgradeDropTables();
            upgradeAlterTables();

            logger.fine("Committing upgrade transaction...");
            target.getConnection().commit();
        } catch(SQLException e){
            target.getConnection().rollback();
            throw e;
        } finally {
            target.getConnection().setAutoCommit(true);
        }
    }

    private void upgradeRenameTables() throws SQLException {
        if(tableRenameMap.size() > 0) {
            List<String> targetTables = getTableList(target);

            for (String oldTableName : tableRenameMap.keySet()) {
                if (targetTables.contains(oldTableName)) {
                    String newTableName = tableRenameMap.get(oldTableName);
                    logger.fine(String.format("Renaming table from: '%s' to: '%s'", oldTableName, newTableName));
                    target.exec(String.format(
                            "ALTER TABLE %s RENAME TO %s", oldTableName, newTableName));
                }
            }
        }
    }

    private void upgradeCreateTables() throws SQLException {
        List<String> refTables = getTableList(reference);
        List<String> targetTables = getTableList(target);

        for(String table : refTables){
            if(!targetTables.contains(table)){
                logger.fine(String.format("Creating new table: '%s'", table));
                // Get reference create sql
                String sql = getTableSql(reference, table);
                // Execute sql on target
                target.exec(sql);
            }
        }
    }

    private void upgradeDropTables() throws SQLException {
        List<String> refTables = getTableList(reference);
        List<String> targetTables = getTableList(target);

        for(String table : targetTables){
            if(!refTables.contains(table)){
                logger.fine(String.format("Dropping table: '%s'", table));
                target.exec("DROP TABLE " + table);
            }
        }
    }

    private void upgradeAlterTables() throws SQLException {
        List<String> refTables = getTableList(reference);
        List<String> targetTables = getTableList(target);

        for(String table : targetTables){
            if(refTables.contains(table)){
                // Get reference structure
                List<DBColumn> refStruct = getColumnList(reference, table);
                // Get target structure
                List<DBColumn> targetStruct = getColumnList(target, table);

                // Check unnecessary columns
                boolean execForcedUpgrade = false;
                for(DBColumn column : targetStruct) {
                    if(refStruct.contains(column)) {
                        DBColumn refColumn = refStruct.get(refStruct.indexOf(column));
                        // Check if the columns have the same type
                        if(!column.type.equals(refColumn.type)){
                            if(forceUpgradeEnabled)
                                execForcedUpgrade = true;
                            else
                                logger.warning(String.format(
                                        "Skipping alter(%s -> %s) column: '%s.%s' (no SQLite support, forced upgrade needed)",
                                        column.type, refColumn.type, table, column.name));
                        }
                    }
                    else { // Column does not exist in reference DB, column should be removed
                        if (forceUpgradeEnabled)
                            execForcedUpgrade = true;
                        else
                            logger.warning(String.format(
                                    "Skipping drop column: '%s.%s' (no SQLite support, forced upgrade needed)",
                                    table, column.name));
                    }
                }

                // Do a forced upgrade where we create a new table and migrate the old data
                if(execForcedUpgrade){
                    // Backup table
                    String backupTable = table+"_temp";
                    logger.fine(String.format("Forced Upgrade: Backing up table: '%s' to: '%s'", table, backupTable));
                    target.exec(String.format("ALTER TABLE %s RENAME TO %s", table, backupTable));

                    // Creating new table
                    logger.fine(String.format("Forced Upgrade: Creating new table: '%s'", table));
                    String sql = getTableSql(reference, table);
                    target.exec(sql);

                    // Restoring data
                    logger.fine(String.format("Forced Upgrade: Restoring data for table: '%s'", table));
                    String cols = StringUtil.join(refStruct, ",");
                    target.exec(String.format(
                            "INSERT INTO %s (%s) SELECT %s FROM %s",
                            table, cols, cols, backupTable));

                    // Remove backup table
                    logger.fine(String.format("Forced Upgrade: Dropping backup table: '%s'", backupTable));
                    target.exec("DROP TABLE " + backupTable);
                }
                // Do a
                else{
                    // Add new columns
                    for(DBColumn column : refStruct) {
                        if(!targetStruct.contains(column)) {
                            logger.fine(String.format("Adding column '%s.%s'", table, column.name));
                            target.exec(
                                    String.format("ALTER TABLE %s ADD COLUMN %s %s %s%s%s",
                                            table,
                                            column.name,
                                            column.type,
                                            (column.defaultValue != null ? " DEFAULT '"+column.defaultValue+"'" : ""),
                                            (column.notNull ? " NOT NULL" : ""),
                                            (column.publicKey ? " PRIMARY KEY" : "")));
                        }
                    }
                }
            }
        }
    }


    private List<String> getTableList(DBConnection db) throws SQLException {
        return db.exec("SELECT name FROM sqlite_master WHERE type='table';", new SQLResultHandler<List<String>>() {
            public List<String> handleQueryResult(Statement stmt, ResultSet result) throws SQLException {
                ArrayList<String> list = new ArrayList<String>();
                while( result.next() ) {
                    String table = result.getString(1);
                    if(!ignoredTablesSet.contains(table))
                        list.add(result.getString(1));
                }
                return list;
            }
        });
    }
    private static String getTableSql(DBConnection db, String table) throws SQLException {
        PreparedStatement stmt = db.getPreparedStatement("SELECT sql FROM sqlite_master WHERE name == ?");
        stmt.setString(1, table);
        return DBConnection.exec(stmt, new SimpleSQLResult<String>());
    }
    private static List<DBColumn> getColumnList(DBConnection db, String table) throws SQLException {
        return db.exec(
                String.format("PRAGMA table_info(%s)", table),
                new TableStructureResultHandler());
    }

    private static class DBColumn{
        String name;
        String type;
        boolean notNull;
        String defaultValue;
        boolean publicKey;

        public boolean equals(Object obj){
            return obj instanceof DBColumn &&
                    name.equals(((DBColumn)obj).name);
        }

        public String toString(){
            return name;
        }
    }

    private static class TableStructureResultHandler implements SQLResultHandler<List<DBColumn>>{

        @Override
        public List<DBColumn> handleQueryResult(Statement stmt, ResultSet result) throws SQLException {
            ArrayList<DBColumn> list = new ArrayList<>();
            while (result.next()){
                DBColumn column = new DBColumn();
                column.name = result.getString("name");
                column.type = result.getString("type");
                column.notNull = result.getBoolean("notnull");
                column.defaultValue = result.getString("dflt_value");
                column.publicKey = result.getBoolean("pk");
                list.add(column);
            }
            return list;
        }
    }
}
