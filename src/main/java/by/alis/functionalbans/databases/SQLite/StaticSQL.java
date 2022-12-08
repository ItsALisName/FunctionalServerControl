package by.alis.functionalbans.databases.SQLite;

import by.alis.functionalbans.spigot.FunctionalBansSpigot;

public class StaticSQL {

    private static final SQLManager sqlManager = new SQLManager(FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class));

    public static SQLManager getSQLManager() {
        return sqlManager;
    }


}
