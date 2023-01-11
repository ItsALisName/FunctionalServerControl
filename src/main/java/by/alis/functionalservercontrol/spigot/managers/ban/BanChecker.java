package by.alis.functionalservercontrol.spigot.managers.ban;

import by.alis.functionalservercontrol.spigot.additional.misc.AdventureApiUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.MD5TextUtils;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
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
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    return getSQLiteManager().getBannedPlayersNames().contains(nullPlayerName);
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
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: return getBannedPlayersContainer().getIpContainer().contains(getSQLiteManager().getIpByUUID(player.getUniqueId()));
                case H2: return false;
            }
        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: return getSQLiteManager().getBannedIps().contains(getSQLiteManager().getIpByUUID(player.getUniqueId())) && getSQLiteManager().getBannedUUIDs().contains(String.valueOf(player.getUniqueId()));
                case H2: return false;
            }
        }
        return false;
    }

    public static void bannedIpNotify(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
            if(isIpBanned(player)) {
                String playerIp = player.getAddress().getAddress().getHostAddress();
                List<String> bannedAccounts = new ArrayList<>();
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    for(String ip : getBannedPlayersContainer().getIpContainer()) {
                        if(ip.equalsIgnoreCase(playerIp)) bannedAccounts.add(getBannedPlayersContainer().getNameContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(ip)));
                    }
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            for(String ip : getSQLiteManager().getBannedIps()) {
                                if(ip.equalsIgnoreCase(playerIp)) bannedAccounts.add(getSQLiteManager().getBannedPlayersNames().get(getSQLiteManager().getBannedIps().indexOf(ip)));
                            }
                        }
                        case H2: {}
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
