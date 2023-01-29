package net.alis.functionalservercontrol.spigot.managers.mute;

import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class MuteChecker {

    /**
     * Checks if null player is muted
     * @param nullPlayerName - player name who never player on the server
     * @return true if nickname muted
     */
    public static boolean isPlayerMuted(String nullPlayerName) {
        FID fid = new FID(nullPlayerName);
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getMutedPlayersContainer().getFids().contains(fid);
        } else {
            return BaseManager.getBaseManager().getMutedFids().contains(fid);
        }
    }

    /**
     * Checks if a player is muted
     * @param player - player to be tested
     * @return true if player muted
     */
    public static boolean isPlayerMuted(OfflineFunctionalPlayer player) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getMutedPlayersContainer().getFids().contains(player.getFunctionalId());
        } else {
            return BaseManager.getBaseManager().getMutedFids().contains(player.getFunctionalId());
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
    public static boolean isIpMuted(OfflineFunctionalPlayer player) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getMutedPlayersContainer().getIpContainer().contains(BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()));
        } else {
            return BaseManager.getBaseManager().getMutedIps().contains(BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()));
        }
    }

}
