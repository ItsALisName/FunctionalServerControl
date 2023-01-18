package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.api.enums.BanType;
import net.alis.functionalservercontrol.spigot.managers.ban.UnbanManager;
import net.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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
        Player player = event.getPlayer();
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            if(getBannedPlayersContainer().getNameContainer().contains(player.getName())) {
                int indexOf = getBannedPlayersContainer().getNameContainer().indexOf(player.getName());
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
                getBanContainerManager().removeFromBanContainer("-n", player.getName());
                BaseManager.getBaseManager().deleteFromNullBannedPlayers("-n", player.getName());
                BaseManager.getBaseManager().insertIntoBannedPlayers(id, player.getAddress().getAddress().getHostAddress(), player.getName(), initiatorName, reason, banType, realDate, realTime, player.getUniqueId(), time);
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
                    player.kickPlayer(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                            }
                        }
                    }
                }
                if(banType == BanType.PERMANENT_IP) {
                    player.kickPlayer(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                            }
                        }
                    }
                }
                if(banType == BanType.TIMED_NOT_IP) {
                    player.kickPlayer(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                            }
                        }
                    }
                }
                if(banType == BanType.TIMED_IP) {
                    player.kickPlayer(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
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
                        TaskManager.preformAsync(() -> this.unbanManager.preformUnban(player, "The Ban time has expired"));
                        return;
                    }
                }
                String reason = getBannedPlayersContainer().getReasonContainer().get(indexOf);
                String realDate = getBannedPlayersContainer().getRealBanDateContainer().get(indexOf);
                String realTime = getBannedPlayersContainer().getRealBanTimeContainer().get(indexOf);
                String id = getBannedPlayersContainer().getIdsContainer().get(indexOf);
                getBanContainerManager().removeFromBanContainer("-n", player.getName());
                BaseManager.getBaseManager().deleteFromNullBannedPlayers("-n", player.getName());
                BaseManager.getBaseManager().insertIntoBannedPlayers(id, player.getAddress().getAddress().getHostAddress(), player.getName(), initiatorName, reason, banType, realDate, realTime, player.getUniqueId(), time);
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
                    player.kickPlayer(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                            }
                        }
                    }
                }
                if(banType == BanType.PERMANENT_IP) {
                    player.kickPlayer(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                            }
                        }
                    }
                }
                if(banType == BanType.TIMED_NOT_IP) {
                    player.kickPlayer(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                            }
                        }
                    }
                }
                if(banType == BanType.TIMED_IP) {
                    player.kickPlayer(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                            }
                        }
                    }
                }
                return;
            }
        } else {
            TaskManager.preformAsync(() -> {
                if(BaseManager.getBaseManager().getBannedPlayersNames().contains(player.getName())) {
                    int indexOf = BaseManager.getBaseManager().getBannedPlayersNames().indexOf(player.getName());
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
                    getBanContainerManager().removeFromBanContainer("-n", player.getName());
                    BaseManager.getBaseManager().deleteFromNullBannedPlayers("-n", player.getName());
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, player.getAddress().getAddress().getHostAddress(), player.getName(), initiatorName, reason, banType, realDate, realTime, player.getUniqueId(), time);
                    if(banType == BanType.PERMANENT_NOT_IP) {
                        TaskManager.preformSync(() -> CoreAdapter.getAdapter().kick(player, TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever()))));
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(Player admin : Bukkit.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                                }
                            }
                        }
                    }
                    if(banType == BanType.PERMANENT_IP) {
                        TaskManager.preformSync(() -> CoreAdapter.getAdapter().kick(player, TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever()))));
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(Player admin : Bukkit.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                                }
                            }
                        }
                    }
                    if(banType == BanType.TIMED_NOT_IP) {
                        TaskManager.preformSync(() -> CoreAdapter.getAdapter().kick(player, TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))))));
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(Player admin : Bukkit.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                                }
                            }
                        }
                    }
                    if(banType == BanType.TIMED_IP) {
                        TaskManager.preformSync(() -> CoreAdapter.getAdapter().kick(player, TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))))));
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(Player admin : Bukkit.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                                }
                            }
                        }
                    }
                    return;
                }
                if(BaseManager.getBaseManager().getBannedIps().contains(player.getAddress().getAddress().getHostAddress()) && BaseManager.getBaseManager().getBannedPlayersNames().get(BaseManager.getBaseManager().getBannedIps().indexOf(player.getAddress().getAddress().getHostAddress())).equalsIgnoreCase("NULL_PLAYER")) {
                    int indexOf = BaseManager.getBaseManager().getBannedIps().indexOf(player.getName());
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
                    getBanContainerManager().removeFromBanContainer("-n", player.getName());
                    BaseManager.getBaseManager().deleteFromNullBannedPlayers("-n", player.getName());
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, player.getAddress().getAddress().getHostAddress(), player.getName(), initiatorName, reason, banType, realDate, realTime, player.getUniqueId(), time);
                    if(banType == BanType.PERMANENT_NOT_IP) {
                        TaskManager.preformSync(() -> CoreAdapter.getAdapter().kick(player, TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever()))));
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(Player admin : Bukkit.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                                }
                            }
                        }
                    }
                    if(banType == BanType.PERMANENT_IP) {
                        TaskManager.preformSync(() -> CoreAdapter.getAdapter().kick(player, TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever()))));
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(Player admin : Bukkit.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                                }
                            }
                        }
                    }
                    if(banType == BanType.TIMED_NOT_IP) {
                        TaskManager.preformSync(() -> CoreAdapter.getAdapter().kick(player, TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))))));
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(Player admin : Bukkit.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time)))));
                                }
                            }
                        }
                    }
                    if(banType == BanType.TIMED_IP) {
                        TaskManager.preformSync(() -> CoreAdapter.getAdapter().kick(player, TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))))));
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(Player admin : Bukkit.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
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
