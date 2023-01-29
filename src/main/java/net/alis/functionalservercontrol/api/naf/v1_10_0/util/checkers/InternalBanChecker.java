package net.alis.functionalservercontrol.api.naf.v1_10_0.util.checkers;

import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class InternalBanChecker {

    public static boolean inIpBanned(FID fid) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getIpContainer().contains(BaseManager.getBaseManager().getIpByFunctionalId(fid));
        } else {
            return BaseManager.getBaseManager().getBannedIps().contains(BaseManager.getBaseManager().getIpByFunctionalId(fid));
        }
    }

    public static boolean isPlayerBanned(FID fid) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getFidsContainer().contains(fid);
        } else {
            return BaseManager.getBaseManager().getBannedFids().contains(fid);
        }
    }

}
