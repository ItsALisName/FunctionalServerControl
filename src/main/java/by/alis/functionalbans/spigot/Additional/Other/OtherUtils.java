package by.alis.functionalbans.spigot.Additional.Other;

import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.util.UUID;
import java.util.regex.Pattern;

import static by.alis.functionalbans.databases.DataBases.getSQLiteManager;
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
            case SQLITE: {
                return getSQLiteManager().getNamesFromAllPlayers().contains(name);
            }
            case MYSQL: {
                return false;
            }
            case H2: {
                return false;
            }
        }
        return false;
    }

    public static boolean isNotNullPlayer(UUID uuid) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getUUIDsFromAllPlayers().contains(String.valueOf(uuid));
            }
            case MYSQL: {
                return true;
            }
            case H2: {
                return true;
            }
        }
        return false;
    }

    public static boolean isNumber(String arg) {
        try {
            Integer.parseInt(arg);
            return true;
        } catch (NumberFormatException ignored) {
            return false;
        }
    }

    public static boolean isOldServerVersion() {
        Server server = Bukkit.getServer();
        String version = getServerPackageName(server);
        if(version.startsWith("v1_7"))
            return true;
        if (version.startsWith("v1_8"))
            return true;
        if (version.startsWith("v1_9"))
            return true;
        if (version.startsWith("v1_10"))
            return true;
        if (version.startsWith("v1_11"))
            return true;
        if (version.startsWith("v1_12"))
            return true;
        if(version.startsWith("v1_13"))
            return true;
        if(version.startsWith("v1_14"))
            return true;
        if(version.startsWith("v1_15"))
            return true;
        return false;
    }

    public static String getServerPackageName(Server server) {
        String packageName = server.getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1);
    }

    public static int generateRandomNumber() {
        return (int) (Math.random() * (599 + 1));
    }

    public static boolean verifyNickNameFormat(String nickname) {
        if(!getConfigSettings().isNickFormatControlEnabled()) return false;
        boolean a = false;
        for(String blockedFormat : getConfigSettings().getBlockedNickFormats()) {
            String pattern = "(" + blockedFormat.replace("<num>", "[0-9]").replace("<let>", "[a-zA-Z]") + ")";
            if(Pattern.compile(pattern).matcher(nickname).find()) {
                a = true;
                break;
            }
        }
        return a;
    }

}
