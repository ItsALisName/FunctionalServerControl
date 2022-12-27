package by.alis.functionalservercontrol.databases;

import by.alis.functionalservercontrol.databases.SQLite.SQLManager;
import by.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;

public class DataBases {

    private static final SQLManager sqlManager = new SQLManager(FunctionalServerControlSpigot.getPlugin(FunctionalServerControlSpigot.class));
    public static SQLManager getSQLiteManager() {
        return sqlManager;
    }


}
