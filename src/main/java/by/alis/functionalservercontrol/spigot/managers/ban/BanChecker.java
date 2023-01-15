package by.alis.functionalservercontrol.spigot.managers.ban;

import by.alis.functionalservercontrol.spigot.additional.misc.AdventureApiUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.MD5TextUtils;
import by.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

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
            return getBaseManager().getBannedPlayersNames().contains(nullPlayerName);
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
            return getBaseManager().getBannedUUIDs().contains(String.valueOf(player.getUniqueId()));
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
            return getBaseManager().getBannedIps().contains(ipAddress);
        }
    }

    /**
     * Checks if the IP of the specified player is banned
     * @param player - player whose ip will be verified
     * @return true if player ip is banned
     */
    public static boolean isIpBanned(OfflinePlayer player) {

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getIpContainer().contains(getBaseManager().getIpByUUID(player.getUniqueId()));
        } else {
            return getBaseManager().getBannedIps().contains(getBaseManager().getIpByUUID(player.getUniqueId()));
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
                    for(String ip : getBaseManager().getBannedIps()) {
                        if(ip.equalsIgnoreCase(playerIp)) bannedAccounts.add(getBaseManager().getBannedPlayersNames().get(getBaseManager().getBannedIps().indexOf(ip)));
                    }
                }
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.same-ip")
                        .replace("%1$f", player.getName())
                        .replace("%2$f", playerIp)
                        .replace("%3$f", String.join(", ", bannedAccounts))));

                for(Player admin : Bukkit.getOnlinePlayers()) {
                    if(admin.hasPermission("functionalservercontrol.notification.same-ip")) {
                        if(admin.hasPermission("functionalservercontrol.ban") && getConfigSettings().isServerSupportsHoverEvents()) {
                            if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                admin.spigot().sendMessage(
                                        MD5TextUtils.appendTwo(
                                                MD5TextUtils.stringToTextComponent(setColors(getFileAccessor().getLang().getString("other.notifications.same-ip")
                                                        .replace("%1$f", player.getName())
                                                        .replace("%2$f", playerIp)
                                                        .replace("%3$f", String.join(", ", bannedAccounts)))),
                                                MD5TextUtils.createClickableSuggestCommandText(
                                                        setColors(" " + getGlobalVariables().getButtonBan()),
                                                        "/ban " + player.getName()
                                                )
                                        )
                                );
                                continue;
                            }
                            if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                admin.sendMessage(
                                        AdventureApiUtils.stringToComponent(setColors(getFileAccessor().getLang().getString("other.notifications.same-ip")
                                                        .replace("%1$f", player.getName())
                                                        .replace("%2$f", playerIp)
                                                        .replace("%3$f", String.join(", ", bannedAccounts))))
                                                .append(AdventureApiUtils.createClickableSuggestCommandText(
                                                        setColors(" " + getGlobalVariables().getButtonBan()),
                                                        "/ban " + player.getName()
                                                ))
                                );
                                continue;
                            }
                        } else {
                            admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.same-ip")
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
