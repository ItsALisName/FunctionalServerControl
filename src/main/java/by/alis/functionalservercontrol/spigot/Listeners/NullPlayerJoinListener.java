package by.alis.functionalservercontrol.spigot.Listeners;

import by.alis.functionalservercontrol.api.Enums.BanType;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.Managers.Bans.UnbanManager;
import by.alis.functionalservercontrol.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getBanContainerManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class NullPlayerJoinListener implements Listener {
    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
    private final UnbanManager unbanManager = new UnbanManager();

    @EventHandler
    public void onNullPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            if(getBannedPlayersContainer().getNameContainer().contains(player.getName())) {
                int indexOf = getBannedPlayersContainer().getNameContainer().indexOf(player.getName());
                String initiatorName = getBannedPlayersContainer().getInitiatorNameContainer().get(indexOf);
                long time = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(indexOf);
                if(banType != BanType.PERMANENT_IP && banType != BanType.PERMANENT_NOT_IP) {
                    if (System.currentTimeMillis() >= time) {
                        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                            this.unbanManager.preformUnban(player, "The Ban time has expired");
                        });
                        return;
                    }
                }
                String reason = getBannedPlayersContainer().getReasonContainer().get(indexOf);
                String realDate = getBannedPlayersContainer().getRealBanDateContainer().get(indexOf);
                String realTime = getBannedPlayersContainer().getRealBanTimeContainer().get(indexOf);
                String id = getBannedPlayersContainer().getIdsContainer().get(indexOf);
                getBanContainerManager().removeFromBanContainer("-n", player.getName());
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player.getName());
                        getSQLiteManager().insertIntoBannedPlayers(id, player.getAddress().getAddress().getHostAddress(), player.getName(), initiatorName, reason, banType, realDate, realTime, player.getUniqueId(), time);
                        break;
                    }
                    case H2: {
                        break;
                    }
                }
                getBannedPlayersContainer().addToBansContainer(
                        id,
                        player.getAddress().getAddress().getHostAddress(),
                        player.getName(),
                        initiatorName,
                        reason,
                        banType,
                        realDate,
                        realTime,
                        String.valueOf(player.getUniqueId()),
                        time
                );
                if(banType == BanType.PERMANENT_NOT_IP) {
                    player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                            }
                        }
                    }
                }
                if(banType == BanType.PERMANENT_IP) {
                    player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                            }
                        }
                    }
                }
                if(banType == BanType.TIMED_NOT_IP) {
                    player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                            }
                        }
                    }
                }
                if(banType == BanType.TIMED_IP) {
                    player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                            }
                        }
                    }
                }
                return;
            }
            if(getBannedPlayersContainer().getIpContainer().contains(player.getAddress().getAddress().getHostAddress()) && getBannedPlayersContainer().getNameContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(player.getAddress().getAddress().getHostAddress())).equalsIgnoreCase("NULL_PLAYER")) {
                int indexOf = getBannedPlayersContainer().getIpContainer().indexOf(player.getName());
                String initiatorName = getBannedPlayersContainer().getInitiatorNameContainer().get(indexOf);
                long time = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(indexOf);
                if(banType != BanType.PERMANENT_IP && banType != BanType.PERMANENT_NOT_IP) {
                    if (System.currentTimeMillis() >= time) {
                        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                            this.unbanManager.preformUnban(player, "The Ban time has expired");
                        });
                        return;
                    }
                }
                String reason = getBannedPlayersContainer().getReasonContainer().get(indexOf);
                String realDate = getBannedPlayersContainer().getRealBanDateContainer().get(indexOf);
                String realTime = getBannedPlayersContainer().getRealBanTimeContainer().get(indexOf);
                String id = getBannedPlayersContainer().getIdsContainer().get(indexOf);
                getBanContainerManager().removeFromBanContainer("-n", player.getName());
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player.getName());
                        getSQLiteManager().insertIntoBannedPlayers(id, player.getAddress().getAddress().getHostAddress(), player.getName(), initiatorName, reason, banType, realDate, realTime, player.getUniqueId(), time);
                        break;
                    }
                    
                    case H2: {
                        break;
                    }
                }
                getBannedPlayersContainer().addToBansContainer(
                        id,
                        player.getAddress().getAddress().getHostAddress(),
                        player.getName(),
                        initiatorName,
                        reason,
                        banType,
                        realDate,
                        realTime,
                        String.valueOf(player.getUniqueId()),
                        time
                );
                if(banType == BanType.PERMANENT_NOT_IP) {
                    player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                            }
                        }
                    }
                }
                if(banType == BanType.PERMANENT_IP) {
                    player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                            }
                        }
                    }
                }
                if(banType == BanType.TIMED_NOT_IP) {
                    player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                            }
                        }
                    }
                }
                if(banType == BanType.TIMED_IP) {
                    player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                            }
                        }
                    }
                }
                return;
            }
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        if(getSQLiteManager().getBannedPlayersNames().contains(player.getName())) {
                            int indexOf = getSQLiteManager().getBannedPlayersNames().indexOf(player.getName());
                            String initiatorName = getSQLiteManager().getBanInitiators().get(indexOf);
                            long time = getSQLiteManager().getUnbanTimes().get(indexOf);
                            if (System.currentTimeMillis() >= time) {
                                this.unbanManager.preformUnban(player, "The Ban time has expired");
                                return;
                            }
                            String reason = getSQLiteManager().getBanReasons().get(indexOf);
                            String realDate = getSQLiteManager().getBansDates().get(indexOf);
                            String realTime = getSQLiteManager().getBansTimes().get(indexOf);
                            String id = getSQLiteManager().getBannedIds().get(indexOf);
                            BanType banType = getSQLiteManager().getBanTypes().get(indexOf);
                            getBanContainerManager().removeFromBanContainer("-n", player.getName());
                            getSQLiteManager().deleteFromNullBannedPlayers("-n", player.getName());
                            getSQLiteManager().insertIntoBannedPlayers(id, player.getAddress().getAddress().getHostAddress(), player.getName(), initiatorName, reason, banType, realDate, realTime, player.getUniqueId(), time);
                            if(banType == BanType.PERMANENT_NOT_IP) {
                                Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                                    player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                                });
                                if (getConfigSettings().isConsoleNotification()) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                                }
                                if(getConfigSettings().isPlayersNotification()) {
                                    for(Player admin : Bukkit.getOnlinePlayers()) {
                                        if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                                        }
                                    }
                                }
                            }
                            if(banType == BanType.PERMANENT_IP) {
                                Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                                    player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                                });
                                if (getConfigSettings().isConsoleNotification()) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                                }
                                if(getConfigSettings().isPlayersNotification()) {
                                    for(Player admin : Bukkit.getOnlinePlayers()) {
                                        if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                                        }
                                    }
                                }
                            }
                            if(banType == BanType.TIMED_NOT_IP) {
                                Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                                    player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                                });
                                if (getConfigSettings().isConsoleNotification()) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                                }
                                if(getConfigSettings().isPlayersNotification()) {
                                    for(Player admin : Bukkit.getOnlinePlayers()) {
                                        if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                                        }
                                    }
                                }
                            }
                            if(banType == BanType.TIMED_IP) {
                                Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                                    player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                                });
                                if (getConfigSettings().isConsoleNotification()) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                                }
                                if(getConfigSettings().isPlayersNotification()) {
                                    for(Player admin : Bukkit.getOnlinePlayers()) {
                                        if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                                        }
                                    }
                                }
                            }
                            return;
                        }
                        if(getSQLiteManager().getBannedIps().contains(player.getAddress().getAddress().getHostAddress()) && getSQLiteManager().getBannedPlayersNames().get(getSQLiteManager().getBannedIps().indexOf(player.getAddress().getAddress().getHostAddress())).equalsIgnoreCase("NULL_PLAYER")) {
                            int indexOf = getSQLiteManager().getBannedIps().indexOf(player.getName());
                            String initiatorName = getSQLiteManager().getBanInitiators().get(indexOf);
                            long time = getSQLiteManager().getUnbanTimes().get(indexOf);
                            if (System.currentTimeMillis() >= time) {
                                this.unbanManager.preformUnban(player, "The Ban time has expired");
                                return;
                            }
                            String reason = getSQLiteManager().getBanReasons().get(indexOf);
                            String realDate = getSQLiteManager().getBansDates().get(indexOf);
                            String realTime = getSQLiteManager().getBansTimes().get(indexOf);
                            String id = getSQLiteManager().getBannedIds().get(indexOf);
                            BanType banType = getSQLiteManager().getBanTypes().get(indexOf);
                            getBanContainerManager().removeFromBanContainer("-n", player.getName());
                            getSQLiteManager().deleteFromNullBannedPlayers("-n", player.getName());
                            getSQLiteManager().insertIntoBannedPlayers(id, player.getAddress().getAddress().getHostAddress(), player.getName(), initiatorName, reason, banType, realDate, realTime, player.getUniqueId(), time);
                            if(banType == BanType.PERMANENT_NOT_IP) {
                                Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                                    player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                                });
                                if (getConfigSettings().isConsoleNotification()) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                                }
                                if(getConfigSettings().isPlayersNotification()) {
                                    for(Player admin : Bukkit.getOnlinePlayers()) {
                                        if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                                        }
                                    }
                                }
                            }
                            if(banType == BanType.PERMANENT_IP) {
                                Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                                    player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                                });
                                if (getConfigSettings().isConsoleNotification()) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                                }
                                if(getConfigSettings().isPlayersNotification()) {
                                    for(Player admin : Bukkit.getOnlinePlayers()) {
                                        if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                                        }
                                    }
                                }
                            }
                            if(banType == BanType.TIMED_NOT_IP) {
                                Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                                    player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                                });
                                if (getConfigSettings().isConsoleNotification()) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                                }
                                if(getConfigSettings().isPlayersNotification()) {
                                    for(Player admin : Bukkit.getOnlinePlayers()) {
                                        if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                                        }
                                    }
                                }
                            }
                            if(banType == BanType.TIMED_IP) {
                                Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                                    player.kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                                });
                                if (getConfigSettings().isConsoleNotification()) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                                }
                                if(getConfigSettings().isPlayersNotification()) {
                                    for(Player admin : Bukkit.getOnlinePlayers()) {
                                        if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                                        }
                                    }
                                }
                            }
                            return;
                        }
                    }

                    

                    case H2: {
                        break;
                    }
                }
            });
        }
    }

}
