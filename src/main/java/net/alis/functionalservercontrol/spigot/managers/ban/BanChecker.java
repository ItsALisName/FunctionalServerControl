package net.alis.functionalservercontrol.spigot.managers.ban;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.Component;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.managers.file.SFAccessor;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class BanChecker {

    /**
     * Checks if null player is banned
     * @param nullPlayerName - player name who never player on the server
     * @return true if nickname banned
     */
    public static boolean isPlayerBanned(String nullPlayerName) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getFidsContainer().contains(new FID(nullPlayerName));
        } else {
            return BaseManager.getBaseManager().getBannedFids().contains(new FID(nullPlayerName));
        }
    }

    /**
     * Checks if a player is banned
     * @param player - player to be tested
     * @return true if player banned
     */
    public static boolean isPlayerBanned(OfflineFunctionalPlayer player) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getFidsContainer().contains(player.getFunctionalId());
        } else {
            return BaseManager.getBaseManager().getBannedFids().contains(player.getFunctionalId());
        }
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
            return BaseManager.getBaseManager().getBannedIps().contains(ipAddress);
        }
    }

    /**
     * Checks if the IP of the specified player is banned
     * @param player - player whose ip will be verified
     * @return true if player ip is banned
     */
    public static boolean isIpBanned(OfflineFunctionalPlayer player) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getIpContainer().contains(BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()));
        } else {
            return BaseManager.getBaseManager().getBannedIps().contains(BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()));
        }
    }

    public static void bannedIpNotify(FunctionalPlayer player) {
        TaskManager.preformAsync(() -> {
            if(isIpBanned(player)) {
                String playerIp = player.address();
                List<String> bannedAccounts = new ArrayList<>();
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    for(String ip : getBannedPlayersContainer().getIpContainer()) {
                        if(ip.equalsIgnoreCase(playerIp)) bannedAccounts.add(getBannedPlayersContainer().getNameContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(ip)));
                    }
                } else {
                    for(String ip : BaseManager.getBaseManager().getBannedIps()) {
                        if(ip.equalsIgnoreCase(playerIp)) bannedAccounts.add(BaseManager.getBaseManager().getBannedPlayersNames().get(BaseManager.getBaseManager().getBannedIps().indexOf(ip)));
                    }
                }
                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.notifications.same-ip")
                        .replace("%1$f", player.nickname())
                        .replace("%2$f", playerIp)
                        .replace("%3$f", String.join(", ", bannedAccounts))));

                for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                    if(admin.hasPermission("functionalservercontrol.notification.same-ip")) {
                        admin.expansion().message(Component.stringToSimplifiedComponent(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.notifications.same-ip")
                                                .replace("%1$f", player.nickname())
                                                .replace("%2$f", playerIp)
                                                .replace("%3$f", String.join(", ", bannedAccounts)))),
                                        Component.addPunishmentButtons(admin, player.nickname())
                        );
                        continue;
                    }
                }
            }
        });
    }

}
