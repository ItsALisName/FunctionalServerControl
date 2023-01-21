package net.alis.functionalservercontrol.databases;

import net.alis.functionalservercontrol.databases.mysql.MySQLManager;
import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import net.alis.functionalservercontrol.databases.sqlite.SQLiteManager;

public class DataBases {

    private static final SQLiteManager sqliteManager = new SQLiteManager(FunctionalServerControlSpigot.getPlugin(FunctionalServerControlSpigot.class));
    private static final MySQLManager mySqlManager = new MySQLManager(FunctionalServerControlSpigot.getPlugin(FunctionalServerControlSpigot.class));

    public static SQLiteManager getSQLiteManager() {
        return sqliteManager;
    }
    public static MySQLManager getMySQLManager() {
        return mySqlManager;
    }
}
