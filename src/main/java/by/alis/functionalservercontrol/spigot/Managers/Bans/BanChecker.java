package by.alis.functionalservercontrol.spigot.Managers.Bans;

import org.bukkit.OfflinePlayer;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

public class BanChecker {

    /**
     * Checks if null player is banned
     * @param nullPlayerName - player name who never player on the server
     * @return true if nickname banned
     */
    public static boolean isPlayerBanned(String nullPlayerName) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getNameContainer().contains(nullPlayerName);
        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    return getSQLiteManager().getBannedPlayersNames().contains(nullPlayerName);
                }
                case MYSQL: {
                    return false;
                }
                case H2: {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a player is banned
     * @param player - player to be tested
     * @return true if player banned
     */
    public static boolean isPlayerBanned(OfflinePlayer player) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getNameContainer().contains(player.getName()) && getBannedPlayersContainer().getUUIDContainer().contains(String.valueOf(player.getUniqueId()));
        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    return getSQLiteManager().getBannedUUIDs().contains(String.valueOf(player.getUniqueId())) && getSQLiteManager().getBannedPlayersNames().contains(player.getName());
                }
                case MYSQL: {
                    return false;
                }
                case H2: {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Checks if ip is banned
     * @param ipAddress - ip to be tested
     * @return true if IP banned
     */
    public static boolean isIpBanned(String ipAddress) {


        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getIpContainer().contains(ipAddress);
        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    return getSQLiteManager().getBannedIps().contains(ipAddress);
                }
                case MYSQL: {
                    break;
                }
                case H2: {
                    break;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the IP of the specified player is banned
     * @param player - player whose ip will be verified
     * @return true if player ip is banned
     */
    public static boolean isIpBanned(OfflinePlayer player) {

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getIpContainer().contains(getSQLiteManager().selectIpByUUID(player.getUniqueId()));
        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    return getSQLiteManager().getBannedIps().contains(getSQLiteManager().selectIpByUUID(player.getUniqueId())) && getSQLiteManager().getBannedUUIDs().contains(String.valueOf(player.getUniqueId()));
                }
                case MYSQL: {
                    return false;
                }
                case H2: {
                    return false;
                }
            }
        }
        return false;
    }

}
