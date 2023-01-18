package net.alis.functionalservercontrol.databases;

import net.alis.functionalservercontrol.databases.mysql.MySQLManager;
import net.alis.functionalservercontrol.spigot.FunctionalServerControl;
import net.alis.functionalservercontrol.databases.sqlite.SQLiteManager;

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
