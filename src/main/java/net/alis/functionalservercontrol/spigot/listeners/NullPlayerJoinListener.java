package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.api.enums.BanType;
import net.alis.functionalservercontrol.spigot.managers.ban.UnbanManager;
import net.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBanContainerManager;
import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class NullPlayerJoinListener implements Listener {
    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
    private final UnbanManager unbanManager = new UnbanManager();

    @EventHandler
    public void onNullPlayerJoin(PlayerJoinEvent event) {
        FunctionalPlayer player = FunctionalPlayer.get(event.getPlayer().getName());
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            if(getBannedPlayersContainer().getFidsContainer().contains(player.getFunctionalId())) {
                Bukkit.getConsoleSender().sendMessage("TRUE: " + player.getFunctionalId() + " === " + player.nickname());
                int indexOf = getBannedPlayersContainer().getFidsContainer().indexOf(player.getFunctionalId());
                String initiatorName = getBannedPlayersContainer().getInitiatorNameContainer().get(indexOf);
                long time = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(indexOf);
                if(banType != BanType.PERMANENT_IP && banType != BanType.PERMANENT_NOT_IP) {
                    if (System.currentTimeMillis() >= time) {
                        TaskManager.preformAsync(() -> this.unbanManager.preformUnban(player, "The Ban time has expired"));
                        return;
                    }
                }
                String reason = getBannedPlayersContainer().getReasonContainer().get(indexOf);
                String realDate = getBannedPlayersContainer().getRealBanDateContainer().get(indexOf);
                String realTime = getBannedPlayersContainer().getRealBanTimeContainer().get(indexOf);
                String id = getBannedPlayersContainer().getIdsContainer().get(indexOf);
                getBanContainerManager().removeFromBanContainer("-id", id);
                BaseManager.getBaseManager().deleteFromNullBannedPlayers("-id", id);
                BaseManager.getBaseManager().insertIntoBannedPlayers(id, player.address(), player.nickname(), initiatorName, reason, banType, realDate, realTime, player.getUniqueId(), time, player.getFunctionalId());
                getBannedPlayersContainer().addToBansContainer(
                        id,
                        player.address(),
                        player.nickname(),
                        initiatorName,
                        reason,
                        banType,
                        realDate,
                        realTime,
                        String.valueOf(player.getUniqueId()),
                        time,
                        player.getFunctionalId()
                );
                if(banType == BanType.PERMANENT_NOT_IP) {
                    player.kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", getGlobalVariables().getVariableNever())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", getGlobalVariables().getVariableNever())));
                            }
                        }
                    }
                }
                if(banType == BanType.PERMANENT_IP) {
                    player.kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.nickname()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.address())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.nickname()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.address())));
                            }
                        }
                    }
                }
                if(banType == BanType.TIMED_NOT_IP) {
                    player.kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                            }
                        }
                    }
                }
                if(banType == BanType.TIMED_IP) {
                    player.kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.nickname()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.address())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.nickname()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.address())));
                            }
                        }
                    }
                }
                return;
            }

            if(getBannedPlayersContainer().getIpContainer().contains(player.address()) && getBannedPlayersContainer().getNameContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(player.address())).equalsIgnoreCase("NULL_PLAYER")) {
                int indexOf = getBannedPlayersContainer().getIpContainer().indexOf(player.nickname());
                String initiatorName = getBannedPlayersContainer().getInitiatorNameContainer().get(indexOf);
                long time = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(indexOf);
                if(banType != BanType.PERMANENT_IP && banType != BanType.PERMANENT_NOT_IP) {
                    if (System.currentTimeMillis() >= time) {
                        TaskManager.preformAsync(() -> this.unbanManager.preformUnban(player, "The Ban time has expired"));
                        return;
                    }
                }
                String reason = getBannedPlayersContainer().getReasonContainer().get(indexOf);
                String realDate = getBannedPlayersContainer().getRealBanDateContainer().get(indexOf);
                String realTime = getBannedPlayersContainer().getRealBanTimeContainer().get(indexOf);
                String id = getBannedPlayersContainer().getIdsContainer().get(indexOf);
                getBanContainerManager().removeFromBanContainer("-id", id);
                BaseManager.getBaseManager().deleteFromNullBannedPlayers("-id", id);
                BaseManager.getBaseManager().insertIntoBannedPlayers(id, player.address(), player.nickname(), initiatorName, reason, banType, realDate, realTime, player.getUniqueId(), time, player.getFunctionalId());
                getBannedPlayersContainer().addToBansContainer(
                        id,
                        player.address(),
                        player.nickname(),
                        initiatorName,
                        reason,
                        banType,
                        realDate,
                        realTime,
                        String.valueOf(player.getUniqueId()),
                        time,
                        player.getFunctionalId()
                );
                if(banType == BanType.PERMANENT_NOT_IP) {
                    player.kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", getGlobalVariables().getVariableNever())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", getGlobalVariables().getVariableNever())));
                            }
                        }
                    }
                }
                if(banType == BanType.PERMANENT_IP) {
                    player.kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.nickname()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.address())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.nickname()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.address())));
                            }
                        }
                    }
                }
                if(banType == BanType.TIMED_NOT_IP) {
                    player.kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                            }
                        }
                    }
                }
                if(banType == BanType.TIMED_IP) {
                    player.kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.nickname()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.address())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.nickname()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.address())));
                            }
                        }
                    }
                }
                return;
            }
        } else {
            TaskManager.preformAsync(() -> {
                if(BaseManager.getBaseManager().getBannedPlayersNames().contains(player.nickname())) {
                    int indexOf = BaseManager.getBaseManager().getBannedPlayersNames().indexOf(player.nickname());
                    String initiatorName = BaseManager.getBaseManager().getBanInitiators().get(indexOf);
                    long time = BaseManager.getBaseManager().getUnbanTimes().get(indexOf);
                    if (System.currentTimeMillis() >= time) {
                        this.unbanManager.preformUnban(player, "The Ban time has expired");
                        return;
                    }
                    String reason = BaseManager.getBaseManager().getBanReasons().get(indexOf);
                    String realDate = BaseManager.getBaseManager().getBansDates().get(indexOf);
                    String realTime = BaseManager.getBaseManager().getBansTimes().get(indexOf);
                    String id = BaseManager.getBaseManager().getBannedIds().get(indexOf);
                    BanType banType = BaseManager.getBaseManager().getBanTypes().get(indexOf);
                    getBanContainerManager().removeFromBanContainer("-id", id);
                    BaseManager.getBaseManager().deleteFromNullBannedPlayers("-id", id);
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, player.address(), player.nickname(), initiatorName, reason, banType, realDate, realTime, player.getUniqueId(), time, player.getFunctionalId());
                    if(banType == BanType.PERMANENT_NOT_IP) {
                        player.kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", getGlobalVariables().getVariableNever())));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", getGlobalVariables().getVariableNever())));
                                }
                            }
                        }
                    }
                    if(banType == BanType.PERMANENT_IP) {
                        player.kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.nickname()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.address())));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.nickname()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.address())));
                                }
                            }
                        }
                    }
                    if(banType == BanType.TIMED_NOT_IP) {
                        player.kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                                }
                            }
                        }
                    }
                    if(banType == BanType.TIMED_IP) {
                        player.kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.nickname()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.address())));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.nickname()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.address())));
                                }
                            }
                        }
                    }
                    return;
                }
                if(BaseManager.getBaseManager().getBannedIps().contains(player.address()) && BaseManager.getBaseManager().getBannedPlayersNames().get(BaseManager.getBaseManager().getBannedIps().indexOf(player.address())).equalsIgnoreCase("NULL_PLAYER")) {
                    int indexOf = BaseManager.getBaseManager().getBannedIps().indexOf(player.nickname());
                    String initiatorName = BaseManager.getBaseManager().getBanInitiators().get(indexOf);
                    long time = BaseManager.getBaseManager().getUnbanTimes().get(indexOf);
                    if (System.currentTimeMillis() >= time) {
                        this.unbanManager.preformUnban(player, "The Ban time has expired");
                        return;
                    }
                    String reason = BaseManager.getBaseManager().getBanReasons().get(indexOf);
                    String realDate = BaseManager.getBaseManager().getBansDates().get(indexOf);
                    String realTime = BaseManager.getBaseManager().getBansTimes().get(indexOf);
                    String id = BaseManager.getBaseManager().getBannedIds().get(indexOf);
                    BanType banType = BaseManager.getBaseManager().getBanTypes().get(indexOf);
                    getBanContainerManager().removeFromBanContainer("-id", id);
                    BaseManager.getBaseManager().deleteFromNullBannedPlayers("-id", id);
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, player.address(), player.nickname(), initiatorName, reason, banType, realDate, realTime, player.getUniqueId(), time, player.getFunctionalId());
                    if(banType == BanType.PERMANENT_NOT_IP) {
                        player.kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", getGlobalVariables().getVariableNever())));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", getGlobalVariables().getVariableNever())));
                                }
                            }
                        }
                    }
                    if(banType == BanType.PERMANENT_IP) {
                        player.kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.nickname()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.address())));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.nickname()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.address())));
                                }
                            }
                        }
                    }
                    if(banType == BanType.TIMED_NOT_IP) {
                        player.kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                                }
                            }
                        }
                    }
                    if(banType == BanType.TIMED_IP) {
                        player.kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.nickname()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.address())));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.nickname()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.address())));
                                }
                            }
                        }
                    }
                    return;
                }
            });
        }
    }

}
