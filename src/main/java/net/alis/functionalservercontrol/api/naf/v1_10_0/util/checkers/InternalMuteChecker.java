package net.alis.functionalservercontrol.api.naf.v1_10_0.util.checkers;

import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class InternalMuteChecker {

    public static boolean isPlayerMuted(FID fid) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getMutedPlayersContainer().getFids().contains(fid);
        } else {
            return BaseManager.getBaseManager().getMutedFids().contains(fid);
        }
    }

    public static boolean isIpMuted(FID fid) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getMutedPlayersContainer().getIpContainer().contains(BaseManager.getBaseManager().getIpByFunctionalId(fid));
        } else {
            return BaseManager.getBaseManager().getMutedIps().contains(BaseManager.getBaseManager().getIpByFunctionalId(fid));
        }
    }

}
