package zutil.db;

import com.sun.deploy.util.StringUtils;
import zutil.db.handler.ListSQLResult;
import zutil.db.handler.SimpleSQLResult;
import zutil.log.LogUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class will take a reference DB and alter
 * the source DB to have the same structure.
 * NOTE: only works with sqllite
 *
 * Created by Ziver
 */
public class DBUpgradeHandler {
    private static final Logger logger = LogUtil.getLogger();

    private DBConnection reference;
    private DBConnection target;
    private boolean forceUpgradeEnabled = false;
    private HashMap<String,String> tableRenameMap;


    public DBUpgradeHandler(DBConnection reference){
        this.tableRenameMap = new HashMap<>();
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
     * With the default behaviour unnecessary columns will not be removed.
     * But if forced upgrade is set to true then the upgrade handler will
     * create a new table and migrate the data from the old one to the new table.
     *
     * @param   enable
     */
    public void forceDBUpgrade(boolean enable){
        this.forceUpgradeEnabled = enable;
    }

    public void upgrade() throws SQLException {
        try {
            logger.fine("Starting upgrade transaction...");
            target.exec("BEGIN IMMEDIATE TRANSACTION");

            upgradeRenameTables();
            upgradeCreateTables();
            upgradeDropTables();
            upgradeAlterTables();

            logger.fine("Committing upgrade transaction...");
            target.exec("COMMIT TRANSACTION");
        } catch(SQLException e){
            try {
                target.exec("ROLLBACK TRANSACTION");
            } catch (SQLException secondary_e) {
                logger.log(Level.SEVERE, null, secondary_e);
            }
            throw e;
        }
    }

    private void upgradeRenameTables() throws SQLException {
        if(tableRenameMap.size() > 0) {
            List<String> targetTables = getTableList(target);

            for (String oldTableName : tableRenameMap.keySet()) {
                if (targetTables.contains(oldTableName)) {
                    String newTableName = tableRenameMap.get(oldTableName);
                    logger.fine("Renaming table from: " + oldTableName + ", to: " + newTableName);
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
                logger.fine("Creating new table: "+ table);
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
                logger.fine("Dropping table: " + table);
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
                boolean forcedUpgrade = false;
                for(DBColumn column : targetStruct) {
                    if(!refStruct.contains(column)) {
                        if(forceUpgradeEnabled)
                            forcedUpgrade = true;
                        else
                            logger.warning("Unable to drop column: '" + column.name + "' from table: "+ table
                                    +" (SQLite does not support dropping columns)");
                    }
                }

                // Do a forced upgrade where we create a new table and migrate the old data
                if(forcedUpgrade){
                    // Backup table
                    String backupTable = table+"_temp";
                    logger.fine("Forced Upgrade: Backing up table: "+table+", to: "+backupTable);
                    target.exec(String.format("ALTER TABLE %s RENAME TO %s", table, backupTable));

                    // Creating new table
                    logger.fine("Forced Upgrade: Creating new table: "+table);
                    String sql = getTableSql(reference, table);
                    target.exec(sql);

                    // Restoring data
                    logger.fine("Forced Upgrade: Restoring data for table: "+table);
                    String cols = StringUtils.join(refStruct, ",");
                    target.exec(String.format(
                            "INSERT INTO %s (%s) SELECT %s FROM %s",
                            table, cols, cols, backupTable));

                    // Remove backup table
                    logger.fine("Forced Upgrade: Removing backup table: "+backupTable);
                    target.exec("DROP TABLE " + backupTable);
                }
                // Do a
                else{
                    // Add new columns
                    for(DBColumn column : refStruct) {
                        if(!targetStruct.contains(column)) {
                            logger.fine("Adding column '" + column.name + "' to table: " + table);
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


    private static List<String> getTableList(DBConnection db) throws SQLException {
        return db.exec("SELECT name FROM sqlite_master WHERE type='table';", new ListSQLResult<String>());
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
