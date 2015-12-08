package zutil.db;

import zutil.db.handler.ListSQLResult;
import zutil.log.LogUtil;

import java.sql.SQLException;
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


    public DBUpgradeHandler(DBConnection reference){
        this.reference = reference;
    }

    public void setTargetDB(DBConnection db){
        this.target = db;
    }

    public void upgrade() throws SQLException {
        /*
        - beginTransaction
        - run a table creation with if not exists (we are doing an upgrade, so the table might not exists yet, it will fail alter and drop)
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
        - setTransactionSuccessful
         */
        try {
            target.exec("BEGIN IMMEDIATE TRANSACTION");

            upgradeCreateTabels();
            upgradeAlterTables();
            upgradeDeleteTables();

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



    private void upgradeCreateTabels() throws SQLException {
        List<String> refTables = reference.exec("SELECT name FROM sqlite_master WHERE type='table';", new ListSQLResult<String>());
        List<String> targetTables = reference.exec("SELECT name FROM sqlite_master WHERE type='table';", new ListSQLResult<String>());

        for(String table : refTables){
            if(!targetTables.contains(table)){
                logger.fine("Creating new table: "+ table);

            }
        }
    }

    private void upgradeAlterTables() throws SQLException {
        logger.fine("Altering table: ");
        //RAGMA table_info([tablename]);
    }

    private void upgradeDeleteTables() throws SQLException {
        logger.fine("Deleting table: ");
    }
}
