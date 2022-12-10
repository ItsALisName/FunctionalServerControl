package by.alis.functionalbans.spigot.Additional.Other;

import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import org.bukkit.Bukkit;

import java.util.UUID;
import java.util.regex.Pattern;

import static by.alis.functionalbans.databases.StaticBases.getSQLiteManager;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

public class OtherUtils {

    public static boolean isArgumentIP(String str) {
        String ipPattern = "([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})";
        return Pattern.compile(ipPattern).matcher(str).find();
    }

    public static boolean isClassExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }



    public static boolean isNotNullPlayer(String name) {
        switch (getConfigSettings().getStorageType()) {
            case "sqlite": {
                return getSQLiteManager().getNamesFromAllPlayers().contains(name);
            }
            case "mysql": {
                return true;
            }
            case "h2": {
                return true;
            }
            default: {
                return getSQLiteManager().getNamesFromAllPlayers().contains(name);
            }
        }
    }

    public static boolean isNotNullPlayer(UUID uuid) {
        switch (getConfigSettings().getStorageType()) {
            case "sqlite": {
                return getSQLiteManager().getUUIDsFromAllPlayers().contains(String.valueOf(uuid));
            }
            case "mysql": {
                return true;
            }
            case "h2": {
                return true;
            }
            default: {
                return getSQLiteManager().getUUIDsFromAllPlayers().contains(String.valueOf(uuid));
            }
        }
    }

}
