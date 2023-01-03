package by.alis.functionalservercontrol.databases;

import by.alis.functionalservercontrol.databases.SQLite.SQLManager;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;

public class DataBases {

    private static final SQLManager sqlManager = new SQLManager(FunctionalServerControl.getPlugin(FunctionalServerControl.class));
    public static SQLManager getSQLiteManager() {
        return sqlManager;
    }


}
