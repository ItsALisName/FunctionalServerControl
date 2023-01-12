package by.alis.functionalservercontrol.spigot.managers.mute;

import org.bukkit.OfflinePlayer;

import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;

public class MuteChecker {

    /**
     * Checks if null player is muted
     * @param nullPlayerName - player name who never player on the server
     * @return true if nickname muted
     */
    public static boolean isPlayerMuted(String nullPlayerName) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getMutedPlayersContainer().getNameContainer().contains(nullPlayerName);
        } else {
            return getBaseManager().getMutedPlayersNames().contains(nullPlayerName);
        }
    }

    /**
     * Checks if a player is muted
     * @param player - player to be tested
     * @return true if player muted
     */
    public static boolean isPlayerMuted(OfflinePlayer player) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getMutedPlayersContainer().getNameContainer().contains(player.getName()) && getMutedPlayersContainer().getUUIDContainer().contains(String.valueOf(player.getUniqueId()));
        } else {
            return getBaseManager().getMutedUUIDs().contains(String.valueOf(player.getUniqueId()));
        }
    }

    /**
     * Checks if ip is muted
     * @param ipAddress - ip to be tested
     * @return true if IP muted
     */
    public static boolean isIpMuted(String ipAddress) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getMutedPlayersContainer().getIpContainer().contains(ipAddress);
        } else {
            return getBaseManager().getMutedIps().contains(ipAddress);
        }
    }

    /**
     * Checks if the IP of the specified player is muted
     * @param player - player whose ip will be verified
     * @return true if player ip is muted
     */
    public static boolean isIpMuted(OfflinePlayer player) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getMutedPlayersContainer().getIpContainer().contains(getBaseManager().getIpByUUID(player.getUniqueId()));
        } else {
            return getBaseManager().getMutedIps().contains(getBaseManager().getIpByUUID(player.getUniqueId())) && getBaseManager().getMutedUUIDs().contains(String.valueOf(player.getUniqueId()));
        }
    }

}
