package net.alis.functionalservercontrol.spigot.managers.mute;

import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import org.bukkit.OfflinePlayer;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

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
            return BaseManager.getBaseManager().getMutedPlayersNames().contains(nullPlayerName);
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
            return BaseManager.getBaseManager().getMutedUUIDs().contains(String.valueOf(player.getUniqueId()));
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
            return BaseManager.getBaseManager().getMutedIps().contains(ipAddress);
        }
    }

    /**
     * Checks if the IP of the specified player is muted
     * @param player - player whose ip will be verified
     * @return true if player ip is muted
     */
    public static boolean isIpMuted(OfflinePlayer player) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getMutedPlayersContainer().getIpContainer().contains(BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()));
        } else {
            return BaseManager.getBaseManager().getMutedIps().contains(BaseManager.getBaseManager().getIpByUUID(player.getUniqueId())) && BaseManager.getBaseManager().getMutedUUIDs().contains(String.valueOf(player.getUniqueId()));
        }
    }

}
