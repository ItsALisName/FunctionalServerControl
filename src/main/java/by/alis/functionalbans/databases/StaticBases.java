package by.alis.functionalbans.databases;

import by.alis.functionalbans.databases.SQLite.SQLManager;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;

public class StaticBases {

    private static final SQLManager sqlManager = new SQLManager(FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class));

    public static SQLManager getSQLiteManager() {
        return sqlManager;
    }


}
