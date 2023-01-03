package by.alis.functionalservercontrol.spigot.Additional.SomeUtils;

import by.alis.functionalservercontrol.API.Enums.ProtocolVersions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

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



    public static boolean isNotNullPlayer(String ProtocolVersions) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getNamesFromAllPlayers().contains(ProtocolVersions);
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

    public static boolean isNotNullIp(String ip) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getIpsFromAllPlayers().contains(ip);
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

    public static boolean verifyNickNameFormat(String nickProtocolVersions) {
        if(!getConfigSettings().isNickFormatControlEnabled()) return false;
        boolean a = false;
        for(String blockedFormat : getConfigSettings().getBlockedNickFormats()) {
            String pattern = "(" + blockedFormat.replace("<num>", "[0-9]").replace("<let>", "[a-zA-Z]") + ")";
            if(Pattern.compile(pattern).matcher(nickProtocolVersions).find()) {
                a = true;
                break;
            }
        }
        return a;
    }

    @Nullable
    public static OfflinePlayer getPlayerByIP(String ip) {
        for(Map.Entry<Player, String> e : TemporaryCache.getOnlineIps().entrySet()) {
            if(e.getValue().equalsIgnoreCase(ip)) {
                return (OfflinePlayer)e.getKey();
            }
        }
        return null;
    }



    public static boolean isSuppotedVersion(Server server) {
        String version = getServerPackageName(server);
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
        if(version.startsWith("v1_16"))
            return true;
        if(version.startsWith("v1_17"))
            return true;
        if(version.startsWith("v1_18"))
            return true;
        if(version.startsWith("v1_19"))
            return true;
        return false;
    }

    public static boolean isServerSupportMDHoverText() {
        return isClassExists("net.md_5.bungee.api.chat.hover.content.Text");
    }

    public static boolean isServerSupportAdventureApi() {
        return isClassExists("net.kyori.adventure.text.Component");
    }

    public static ProtocolVersions convertProtocolVersion(int protocolVersion) {
        if(protocolVersion < 0) return ProtocolVersions.UNKNOWN;
        if(protocolVersion <= 4) return ProtocolVersions.V7_1_5;
        if(protocolVersion == 5) return ProtocolVersions.V7_6_10;
        if(protocolVersion <= 106) return ProtocolVersions.V8_9;
        if(protocolVersion == 107) return ProtocolVersions.V9;
        if(protocolVersion == 108) return ProtocolVersions.V9_1;
        if(protocolVersion == 109) return ProtocolVersions.V9_2;
        if(protocolVersion <= 203) return ProtocolVersions.V9_3_4;
        if(protocolVersion <= 313) return ProtocolVersions.V10_1_2;
        if(protocolVersion <= 327) return ProtocolVersions.V11_1_2;
        if(protocolVersion <= 336) return ProtocolVersions.V12;
        if(protocolVersion <= 338) return ProtocolVersions.V12_1;
        if(protocolVersion <= 382) return ProtocolVersions.V12_2;
        if(protocolVersion <= 398) return ProtocolVersions.V13;
        if(protocolVersion <= 401) return ProtocolVersions.V13_1;
        if(protocolVersion <= 471) return ProtocolVersions.V13_2;
        if(protocolVersion <= 477) return ProtocolVersions.V14;
        if(protocolVersion <= 480) return ProtocolVersions.V14_1;
        if(protocolVersion <= 485) return ProtocolVersions.V14_2;
        if(protocolVersion <= 490) return ProtocolVersions.V14_3;
        if(protocolVersion <= 564) return ProtocolVersions.V14_4;
        if(protocolVersion <= 573) return ProtocolVersions.V15;
        if(protocolVersion <= 575) return ProtocolVersions.V15_1;
        if(protocolVersion <= 719) return ProtocolVersions.V15_2;
        if(protocolVersion <= 735) return ProtocolVersions.V16;
        if(protocolVersion <= 743) return ProtocolVersions.V16_1;
        if(protocolVersion <= 751) return ProtocolVersions.V16_2;
        if(protocolVersion <= 753) return ProtocolVersions.V16_3;
        if(protocolVersion == 754) return ProtocolVersions.V16_4_5;
        if(protocolVersion == 755) return ProtocolVersions.V17;
        if(protocolVersion == 756) return ProtocolVersions.V17_1;
        if(protocolVersion == 757) return ProtocolVersions.V18_0_1;
        if(protocolVersion == 758) return ProtocolVersions.V18_2;
        if(protocolVersion == 759) return ProtocolVersions.V19;
        if(protocolVersion == 760) return ProtocolVersions.V19_1_2;
        if(protocolVersion == 761) return ProtocolVersions.V19_3;
        return ProtocolVersions.V19_3;
    }

    public static ProtocolVersions getServerVersion(Server server) {
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String[] parts = packageName.split("\\.");
        String versionSuffix = parts[parts.length - 1];
        ProtocolVersions serverVersion = null;
        if (!versionSuffix.startsWith("v")) {
            serverVersion = ProtocolVersions.V17_1;
        } else {
            serverVersion = ProtocolVersions.V8.valueOf("V" + versionSuffix.replace("v1_", "").replace("R", ""));
        }
        return serverVersion;
    }

}
