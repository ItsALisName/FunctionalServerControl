package by.alis.functionalservercontrol.databases;

import by.alis.functionalservercontrol.databases.sql.MySQLManager;
import by.alis.functionalservercontrol.databases.sqlite.SQLiteManager;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;

public class DataBases {

    private static final SQLiteManager sqliteManager = new SQLiteManager(FunctionalServerControl.getPlugin(FunctionalServerControl.class));
    private static final MySQLManager mySqlManager = new MySQLManager(FunctionalServerControl.getPlugin(FunctionalServerControl.class));

    public static SQLiteManager getSQLiteManager() {
        return sqliteManager;
    }
    public static MySQLManager getMySQLManager() {
        return mySqlManager;
    }
}
