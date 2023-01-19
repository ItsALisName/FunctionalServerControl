package net.alis.functionalservercontrol.spigot.managers.ban;

import net.alis.functionalservercontrol.spigot.additional.textcomponents.MD5TextUtils;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.managers.file.SFAccessor;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.AdventureApiUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;

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
            return BaseManager.getBaseManager().getBannedPlayersNames().contains(nullPlayerName);
        }
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
            return BaseManager.getBaseManager().getBannedUUIDs().contains(String.valueOf(player.getUniqueId()));
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
    public static boolean isIpBanned(OfflinePlayer player) {

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getIpContainer().contains(BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()));
        } else {
            return BaseManager.getBaseManager().getBannedIps().contains(BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()));
        }
    }

    public static void bannedIpNotify(Player player) {
        TaskManager.preformAsync(() -> {
            if(isIpBanned(player)) {
                String playerIp = player.getAddress().getAddress().getHostAddress();
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
                        .replace("%1$f", player.getName())
                        .replace("%2$f", playerIp)
                        .replace("%3$f", String.join(", ", bannedAccounts))));

                for(Player admin : Bukkit.getOnlinePlayers()) {
                    if(admin.hasPermission("functionalservercontrol.notification.same-ip")) {
                        if(admin.hasPermission("functionalservercontrol.ban") && getConfigSettings().isServerSupportsHoverEvents()) {
                            if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                admin.spigot().sendMessage(
                                        MD5TextUtils.appendTwo(
                                                MD5TextUtils.stringToTextComponent(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.notifications.same-ip")
                                                        .replace("%1$f", player.getName())
                                                        .replace("%2$f", playerIp)
                                                        .replace("%3$f", String.join(", ", bannedAccounts)))),
                                                MD5TextUtils.createClickableSuggestCommandText(
                                                        TextUtils.setColors(" " + getGlobalVariables().getButtonBan()),
                                                        "/ban " + player.getName()
                                                )
                                        )
                                );
                                continue;
                            }
                            if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                admin.sendMessage(
                                        AdventureApiUtils.stringToComponent(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.notifications.same-ip")
                                                        .replace("%1$f", player.getName())
                                                        .replace("%2$f", playerIp)
                                                        .replace("%3$f", String.join(", ", bannedAccounts))))
                                                .append(AdventureApiUtils.createClickableSuggestCommandText(
                                                        TextUtils.setColors(" " + getGlobalVariables().getButtonBan()),
                                                        "/ban " + player.getName()
                                                ))
                                );
                                continue;
                            }
                        } else {
                            admin.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.notifications.same-ip")
                                    .replace("%1$f", player.getName())
                                    .replace("%2$f", playerIp)
                                    .replace("%3$f", String.join(", ", bannedAccounts))
                            ));
                        }
                    }
                }
            }
        });
    }

}
