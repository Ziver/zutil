package zutil.db;

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
    private boolean forceUpgrade = false;
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
        this.forceUpgrade = enable;
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
            List<String> targetTables = target.exec("SELECT name FROM sqlite_master WHERE type='table';", new ListSQLResult<String>());

            for (String oldTableName : tableRenameMap.keySet()) {
                if (targetTables.contains(oldTableName)) {
                    String newTableName = tableRenameMap.get(oldTableName);
                    logger.fine("Renaming table from: " + oldTableName + ", to: " + newTableName);
                    target.exec("ALTER TABLE "+oldTableName+" RENAME TO "+newTableName);
                }
            }
        }
    }

    private void upgradeCreateTables() throws SQLException {
        List<String> refTables = reference.exec("SELECT name FROM sqlite_master WHERE type='table';", new ListSQLResult<String>());
        List<String> targetTables = target.exec("SELECT name FROM sqlite_master WHERE type='table';", new ListSQLResult<String>());

        for(String table : refTables){
            if(!targetTables.contains(table)){
                logger.fine("Creating new table: "+ table);
                // Get reference create sql
                PreparedStatement stmt = reference.getPreparedStatement("SELECT sql FROM sqlite_master WHERE name == ?");
                stmt.setString(1, table);
                String sql = DBConnection.exec(stmt, new SimpleSQLResult<String>());
                // Execute sql on target
                target.exec(sql);
            }
        }
    }

    private void upgradeDropTables() throws SQLException {
        List<String> refTables = reference.exec("SELECT name FROM sqlite_master WHERE type='table';", new ListSQLResult<String>());
        List<String> targetTables = target.exec("SELECT name FROM sqlite_master WHERE type='table';", new ListSQLResult<String>());

        for(String table : targetTables){
            if(!refTables.contains(table)){
                logger.fine("Dropping table: " + table);
                target.exec("DROP TABLE " + table);
            }
        }
    }

    private void upgradeAlterTables() throws SQLException {
        List<String> refTables = reference.exec("SELECT name FROM sqlite_master WHERE type='table';", new ListSQLResult<String>());
        List<String> targetTables = target.exec("SELECT name FROM sqlite_master WHERE type='table';", new ListSQLResult<String>());

        for(String table : targetTables){
            if(refTables.contains(table)){
                // Get reference structure
                List<DBColumn> refStruct = reference.exec("PRAGMA table_info("+table+")",
                        new TableStructureResultHandler());
                // Get target structure
                List<DBColumn> targetStruct = target.exec("PRAGMA table_info("+table+")",
                        new TableStructureResultHandler());

                // Check existing columns
                for(DBColumn column : refStruct) {
                    if(!targetStruct.contains(column)) {
                        logger.fine("Adding column '" + column.name + "' to table: " + table);
                        target.exec("ALTER TABLE "+table+" ADD COLUMN"
                                + " " + column.name // Column name
                                + " " + column.type // Column type
                                + (column.defaultValue != null ? " DEFAULT '"+column.defaultValue+"'" : "")
                                + (column.notNull ? " NOT NULL" : "")
                                + (column.publicKey ? " PRIMARY KEY" : ""));
                    }
                }
                // Check unnecessary columns
                for(DBColumn column : targetStruct) {
                    if(!refStruct.contains(column)) {
                        if(forceUpgrade){
                        /*
                        - put in a list the existing columns List<String> columns = DBUtils.GetColumns(db, TableName);
                        - backup table (ALTER table " + TableName + " RENAME TO 'temp_"                    + TableName)
                        - create new table (the newest table creation schema)
                        - get the intersection with the new columns, this time columns taken from the upgraded table (columns.retainAll(DBUtils.GetColumns(db, TableName));)
                        - restore data (String cols = StringUtils.join(columns, ",");
                                    db.execSQL(String.format(
                                            "INSERT INTO %s (%s) SELECT %s from temp_%s",
                                            TableName, cols, cols, TableName));
                        )
                        - remove backup table (DROP table 'temp_" + TableName)
                         */
                        }
                        else
                            logger.warning("Unable to drop column: '" + column.name + "' from table: "+ table +" (SQLite does not support dropping columns)");
                    }
                }
            }
        }
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
