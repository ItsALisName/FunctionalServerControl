package net.alis.functionalservercontrol.spigot.managers.ban;

import net.alis.functionalservercontrol.api.enums.StatsType;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;

import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.api.events.AsyncBanPreprocessEvent;
import net.alis.functionalservercontrol.api.enums.BanType;
import net.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass;
import net.alis.functionalservercontrol.spigot.managers.IdsManager;
import net.alis.functionalservercontrol.spigot.managers.time.TimeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.UUID;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBanContainerManager;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static net.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getDate;
import static net.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getTime;
import static net.alis.functionalservercontrol.spigot.managers.ban.BanChecker.isIpBanned;
import static net.alis.functionalservercontrol.spigot.managers.ban.BanChecker.isPlayerBanned;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

/**
 * The class responsible for the new account ban method
 */
public class BanManager {

    /**
     * Blocks a player who has ever played on the server (i.e. not a null player)
     * @param player player
     * @param type Ban type
     * @param reason Ban reason
     * @param initiator Who blocked
     * @param time Ban time
     * @param announceBan If true, then the blocking will be notified in the chat
     */
    public void preformBan(OfflineFunctionalPlayer player, BanType type, String reason, CommandSender initiator, long time, boolean announceBan) {

        IdsManager idsManager = new IdsManager();
        TimeManager timeManager = new TimeManager();
        
        String realTime = WorldTimeAndDateClass.getTime();
        String realDate = WorldTimeAndDateClass.getDate();
        if(type == BanType.PERMANENT_IP || type == BanType.PERMANENT_NOT_IP) time = -1;
        if(reason == null || reason.equalsIgnoreCase("")) {
            reason = getGlobalVariables().getDefaultReason();
        }

        String convertedTime;
        if(time < 0) {
            convertedTime = getGlobalVariables().getVarUnknownTime();
        } else {
            convertedTime = timeManager.convertFromMillis(timeManager.getPunishTime(time));
        }
        String id = idsManager.getId();
        AsyncBanPreprocessEvent banPlayerEvent = new AsyncBanPreprocessEvent(id, player, initiator, type, time, reason, realTime, realDate, convertedTime);
        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(banPlayerEvent);
        }
        if(banPlayerEvent.isCancelled()) return;

        if(!announceBan) {
            if(!initiator.hasPermission("functionalservercontrol.use.silently")) {
                initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                banPlayerEvent.setCancelled(true);
                return;
            }
        }

        if(reason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
            if(!getConfigSettings().isBanAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-reason")));
                banPlayerEvent.setCancelled(true);
                return;
            }
        }

        if(initiator instanceof FunctionalPlayer) {
            if (!initiator.hasPermission("functionalservercontrol.time-bypass")) {
                if (time > timeManager.getMaxPlayerBanPunishTime((FunctionalPlayer) initiator)) {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.ban-over-time").replace("%1$f", timeManager.convertFromMillis(timeManager.getMaxPlayerBanPunishTime((FunctionalPlayer) initiator)))));
                    banPlayerEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(getConfigSettings().isProhibitYourselfInteraction()) {
            if(initiator.getName().equalsIgnoreCase(player.nickname())) {
                initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-yourself-actions")));
                banPlayerEvent.setCancelled(true);
                return;
            }
        }

        reason = banPlayerEvent.getReason().replace("'", "\"");
        String initiatorName = null;
        if(initiator instanceof FunctionalPlayer) {
            initiatorName = initiator.getName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }


        if(type == BanType.PERMANENT_NOT_IP) {
            if (isPlayerBanned(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromBannedPlayers("-fid", player.getFunctionalId().toString());
                        BaseManager.getBaseManager().insertIntoBannedPlayers(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1, player.getFunctionalId());
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.ban").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof  FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_BANS);
                        getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId()));
                        getBanContainerManager().removeFromBanContainer("-ip", BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()));
                        getBanContainerManager().addToBanContainer(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L, player.getFunctionalId());
                        if(player.isOnline()) {
                            player.getPlayer().kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));                            }
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player.nickname())));
                        if(announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        BaseManager.getBaseManager().deleteFromBannedPlayers("-fid", player.getFunctionalId().toString());
                        
                        BaseManager.getBaseManager().insertIntoBannedPlayers(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1, player.getFunctionalId());
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.ban").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_BANS);
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player.nickname())));
                        if(player.isOnline()) {
                            player.getPlayer().kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                        }
                        if(announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1, player.getFunctionalId());
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.ban").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_BANS);
                    getBanContainerManager().addToBanContainer(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L, player.getFunctionalId());
                    if(announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        player.getPlayer().kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    }
                } else {
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1, player.getFunctionalId());
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.ban").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_BANS);
                    getBanContainerManager().addToBanContainer(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L, player.getFunctionalId());
                    if(announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        player.getPlayer().kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    }
                }
            }
        }

        if(type == BanType.PERMANENT_IP) {
            if(isIpBanned(player)) {
                if(initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromBannedPlayers("-fid", player.getFunctionalId().toString());
                        BaseManager.getBaseManager().insertIntoBannedPlayers(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1, player.getFunctionalId());
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_BANS);
                        getBanContainerManager().removeFromBanContainer("-fid", player.getFunctionalId().toString());
                        getBanContainerManager().addToBanContainer(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L, player.getFunctionalId());
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player.nickname())));
                        if(announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason)));
                        }
                        if(player.isOnline()) {
                            player.getPlayer().kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                        }
                    } else {
                        BaseManager.getBaseManager().deleteFromBannedPlayers("-fid", player.getFunctionalId().toString());
                        
                        BaseManager.getBaseManager().insertIntoBannedPlayers(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1, player.getFunctionalId());
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_BANS);
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player.nickname())));
                        if(announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason)));
                        }
                        if(player.isOnline()) {
                            player.getPlayer().kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1, player.getFunctionalId());
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_BANS);
                    getBanContainerManager().addToBanContainer(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L, player.getFunctionalId());
                    if(announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        player.getPlayer().kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    }
                } else {
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1, player.getFunctionalId());
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_BANS);
                    if(announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        player.getPlayer().kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    }
                    return;
                }
            }
        }

        if(type == BanType.TIMED_NOT_IP) {
            if (isPlayerBanned(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromBannedPlayers("-fid", player.getFunctionalId().toString());
                        
                        BaseManager.getBaseManager().insertIntoBannedPlayers(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime(), player.getFunctionalId());
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempban").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_BANS);
                        getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId()));
                        getBanContainerManager().removeFromBanContainer("-ip", BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()));
                        getBanContainerManager().addToBanContainer(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), banPlayerEvent.getBanTime(), player.getFunctionalId());
                        String fcTime = timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()));
                        if(player.isOnline()) {
                            player.getPlayer().kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", fcTime)));
                        }
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player.nickname())));
                        if(announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", fcTime).replace("%1$f", initiatorName).replace("%4$f", reason)));
                        }
                        return;
                    } else {
                        BaseManager.getBaseManager().deleteFromBannedPlayers("-fid", player.getFunctionalId().toString());
                        
                        BaseManager.getBaseManager().insertIntoBannedPlayers(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime(), player.getFunctionalId());
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempban").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_BANS);
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player.nickname())));
                        String fcTime = timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()));
                        if(player.isOnline()) {
                            player.getPlayer().kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", fcTime)));
                        }
                        if(announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", fcTime).replace("%1$f", initiatorName).replace("%4$f", reason)));
                        }
                    }
                    return;
                } else {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {

                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime(), player.getFunctionalId());
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempban").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_BANS);
                    getBanContainerManager().addToBanContainer(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), banPlayerEvent.getBanTime(), player.getFunctionalId());
                    String fcTime = timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()));
                    if(announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", fcTime).replace("%1$f", initiatorName).replace("%4$f", reason)));
                    }
                    if(player.isOnline()) {
                        player.getPlayer().kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", fcTime)));
                    }
                } else {
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime(), player.getFunctionalId());
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempban").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_BANS);
                    String fcTime = timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()));
                    if(player.isOnline()) {
                        player.getPlayer().kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", fcTime)));
                    }
                    if(announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", fcTime).replace("%1$f", initiatorName).replace("%4$f", reason)));
                    }
                    return;
                }
            }
        }

        if(type == BanType.TIMED_IP) {
            if(isIpBanned(player)) {
                if(initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromBannedPlayers("-fid", player.getFunctionalId().toString());
                        
                        BaseManager.getBaseManager().insertIntoBannedPlayers(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime(), player.getFunctionalId());
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_BANS);
                        getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId()));
                        getBanContainerManager().removeFromBanContainer("-ip", BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()));
                        getBanContainerManager().addToBanContainer(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), banPlayerEvent.getBanTime(), player.getFunctionalId());
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player.nickname())));
                        String fcTime = timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()));
                        if(player.isOnline()) {
                            player.getPlayer().kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName)).replace("%4$f", realDate + ", " + realTime).replace("%5$f", fcTime));
                        }
                        if(announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", fcTime).replace("%4$f", reason)));
                        }
                    } else {
                        BaseManager.getBaseManager().deleteFromBannedPlayers("-fid", player.getFunctionalId().toString());
                        
                        BaseManager.getBaseManager().insertIntoBannedPlayers(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime(), player.getFunctionalId());
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_BANS);
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player.nickname())));
                        String fcTime = timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()));
                        if(player.isOnline()) {
                            player.getPlayer().kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName)).replace("%4$f", realDate + ", " + realTime).replace("%5$f", fcTime));
                        }
                        if(announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", fcTime).replace("%4$f", reason)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime(), player.getFunctionalId());
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_BANS);
                    getBanContainerManager().addToBanContainer(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), banPlayerEvent.getBanTime(), player.getFunctionalId());
                    String fcTime = timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()));
                    if(player.isOnline()) {
                        player.getPlayer().kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName)).replace("%4$f", realDate + ", " + realTime).replace("%5$f", fcTime));
                    }
                    if(announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", fcTime).replace("%4$f", reason)));
                    }
                } else {
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, BaseManager.getBaseManager().getIpByFunctionalId(player.getFunctionalId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime(), player.getFunctionalId());
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_BANS);
                    String fcTime = timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()));
                    if(player.isOnline()) {
                        player.getPlayer().kick(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName)).replace("%4$f", realDate + ", " + realTime).replace("%5$f", fcTime));
                    }
                    if(announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", fcTime).replace("%4$f", reason)));
                    }
                }
            }
        }

    }

    public void preformBan(String player, BanType type, String reason, CommandSender initiator, long time, boolean announceBan) {
        FID fid = new FID(player);
        IdsManager idsManager = new IdsManager();
        TimeManager timeManager = new TimeManager();
        BanContainerManager banContainerManager = new BanContainerManager();

        String realTime = WorldTimeAndDateClass.getTime();
        String realDate = WorldTimeAndDateClass.getDate();
        String convertedTime;
        if (time < 0) {
            convertedTime = getGlobalVariables().getVariableNever();
        } else {
            convertedTime = timeManager.convertFromMillis(time - System.currentTimeMillis());
        }
        String id = idsManager.getId();
        if (reason == null || reason.equalsIgnoreCase("")) {
            reason = getGlobalVariables().getDefaultReason();
        }
        AsyncBanPreprocessEvent banPlayerEvent = new AsyncBanPreprocessEvent(id, player, initiator, type, time, reason, realTime, realDate, convertedTime);
        reason = banPlayerEvent.getReason().replace("'", "\"");
        time = banPlayerEvent.getBanTime();
        if (type == BanType.PERMANENT_IP || type == BanType.PERMANENT_NOT_IP) time = -1;
        if (getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(banPlayerEvent);
        }
        if (banPlayerEvent.isCancelled()) return;

        if(!announceBan) {
            if(!initiator.hasPermission("functionalservercontrol.use.silently")) {
                initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                banPlayerEvent.setCancelled(true);
                return;
            }
        }

        if(reason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
            if(!getConfigSettings().isBanAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-reason")));
                banPlayerEvent.setCancelled(true);
                return;
            }
        }

        if(initiator instanceof FunctionalPlayer) {
            if (!initiator.hasPermission("functionalservercontrol.time-bypass")) {
                if (time > timeManager.getMaxPlayerBanPunishTime((FunctionalPlayer) initiator)) {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.ban-over-time").replace("%1$f", timeManager.convertFromMillis(timeManager.getMaxPlayerBanPunishTime((FunctionalPlayer) initiator)))));
                    banPlayerEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(getConfigSettings().isProhibitYourselfInteraction()) {
            if(initiator.getName().equalsIgnoreCase(player)) {
                initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-yourself-actions")));
                banPlayerEvent.setCancelled(true);
                return;
            }
        }

        String initiatorName = "ERROR";
        if (initiator instanceof FunctionalPlayer) {
            initiatorName = initiator.getName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }

        if (type == BanType.PERMANENT_NOT_IP) {
            if (isPlayerBanned(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromNullBannedPlayers("-fid", fid.toString());
                        BaseManager.getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.ban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_BANS);
                        getBanContainerManager().removeFromBanContainer("-n", player);
                        getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(Bukkit.getOfflinePlayer(player).getUniqueId()), time, fid);
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if (announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        BaseManager.getBaseManager().deleteFromNullBannedPlayers("-fid", fid.toString());
                        BaseManager.getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.ban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_BANS);
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if (announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.ban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_BANS);
                    getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(Bukkit.getOfflinePlayer(player).getUniqueId()), time, fid);
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    BaseManager.getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.ban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_BANS);
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if (type == BanType.PERMANENT_IP) {
            if (isPlayerBanned(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromNullBannedPlayers("-fid", fid.toString());
                        BaseManager.getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_BANS);
                        getBanContainerManager().removeFromBanContainer("-fid", fid.toString());
                        getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(Bukkit.getOfflinePlayer(player).getUniqueId()), banPlayerEvent.getBanTime(), fid);
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if (announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        BaseManager.getBaseManager().deleteFromNullBannedPlayers("-fid", fid.toString());
                        BaseManager.getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_BANS);
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if (announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_BANS);
                    getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(Bukkit.getOfflinePlayer(player).getUniqueId()), banPlayerEvent.getBanTime(), fid);
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    BaseManager.getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_BANS);
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if (type == BanType.TIMED_IP) {
            if (isPlayerBanned(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromBannedPlayers("-n", player);
                        BaseManager.getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_BANS);
                        getBanContainerManager().removeFromBanContainer("-fid", fid.toString());
                        getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(Bukkit.getOfflinePlayer(player).getUniqueId()), time, fid);
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if (announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        BaseManager.getBaseManager().deleteFromBannedPlayers("-fid", fid.toString());
                        BaseManager.getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_BANS);
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if (announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_BANS);
                    getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(Bukkit.getOfflinePlayer(player).getUniqueId()), time, fid);
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                } else {
                    BaseManager.getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_BANS);
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                }
            }
        }

        if (type == BanType.TIMED_NOT_IP) {
            if (isPlayerBanned(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromBannedPlayers("-fid", fid.toString());
                        BaseManager.getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_BANS);
                        getBanContainerManager().removeFromBanContainer("-n", player);
                        getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(Bukkit.getOfflinePlayer(player).getUniqueId()), time, fid);
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if (announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        BaseManager.getBaseManager().deleteFromBannedPlayers("-n", player);
                        BaseManager.getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_BANS);
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if (announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_BANS);
                    banContainerManager.addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(Bukkit.getOfflinePlayer(player).getUniqueId()), time, fid);
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                } else {
                    BaseManager.getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_BANS);
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                }
            }
        }
    }

    public void preformBanByIp(String ip, BanType type, String reason, CommandSender initiator, long time, boolean announceBan, boolean isNull) {

        IdsManager idsManager = new IdsManager();
        TimeManager timeManager = new TimeManager();
        BanContainerManager banContainerManager = new BanContainerManager();

        String realTime = WorldTimeAndDateClass.getTime();
        String realDate = WorldTimeAndDateClass.getDate();
        String convertedTime;
        if (time < 0) {
            convertedTime = getGlobalVariables().getVariableNever();
        } else {
            convertedTime = timeManager.convertFromMillis(time - System.currentTimeMillis());
        }
        String id = idsManager.getId();
        if (reason == null || reason.equalsIgnoreCase("")) {
            reason = getGlobalVariables().getDefaultReason();
        }
        AsyncBanPreprocessEvent banPlayerEvent = new AsyncBanPreprocessEvent(id, ip, initiator, type, time, reason, realTime, realDate, convertedTime);
        reason = banPlayerEvent.getReason().replace("'", "\"");
        time = banPlayerEvent.getBanTime();
        if (type == BanType.PERMANENT_IP || type == BanType.PERMANENT_NOT_IP) time = -1;
        if (getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(banPlayerEvent);
        }
        if (banPlayerEvent.isCancelled()) return;

        if(!announceBan) {
            if(!initiator.hasPermission("functionalservercontrol.use.silently")) {
                initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                banPlayerEvent.setCancelled(true);
                return;
            }
        }

        if(reason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
            if(!getConfigSettings().isBanAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-reason")));
                banPlayerEvent.setCancelled(true);
                return;
            }
        }

        if(initiator instanceof FunctionalPlayer) {
            if (!initiator.hasPermission("functionalservercontrol.time-bypass")) {
                if (time > timeManager.getMaxPlayerBanPunishTime((FunctionalPlayer) initiator)) {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.ban-over-time").replace("%1$f", timeManager.convertFromMillis(timeManager.getMaxPlayerBanPunishTime((FunctionalPlayer) initiator)))));
                    banPlayerEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(initiator instanceof FunctionalPlayer) {
            if(getConfigSettings().isProhibitYourselfInteraction()) {
                if(((FunctionalPlayer) initiator).address().equalsIgnoreCase(ip)) {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-yourself-actions")));
                    banPlayerEvent.setCancelled(true);
                    return;
                }
            }
        }


        String initiatorName = "ERROR";
        if (initiator instanceof FunctionalPlayer) {
            initiatorName = initiator.getName();
        } else if(initiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = initiator.getName();
        }

        FID fid = FID.random();
        UUID uuid = UUID.randomUUID();

        if (type == BanType.PERMANENT_NOT_IP) {
            if (isIpBanned(ip)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromNullBannedPlayers("-ip", ip);
                        BaseManager.getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName, reason, type, realDate, realTime, time, uuid, fid);
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        if(!isNull) {
                            OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                                BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_BANS);
                            }
                        }
                        getBanContainerManager().removeFromBanContainer("-ip", ip);
                        getBanContainerManager().addToBanContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, uuid.toString(), time, fid);
                        if(isNull) {
                            initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ip-ban-removed").replace("%1$f", ip)));
                        if (announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban.ip-broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        BaseManager.getBaseManager().deleteFromNullBannedPlayers("-ip", ip);
                        BaseManager.getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        if(!isNull) {
                            OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                                BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_BANS);
                            }
                        }
                        if(isNull) {
                            initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ip-ban-removed").replace("%1$f", ip)));
                        if (announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban.ip-broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoNullBannedPlayersIP(id, ip,  initiatorName, reason, type, realDate, realTime, time, uuid, fid);
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    if(!isNull) {
                        OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                            BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_BANS);
                        }
                    }
                    getBanContainerManager().addToBanContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, uuid.toString(), time, fid);
                    if(isNull) {
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban.ip-broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    BaseManager.getBaseManager().insertIntoNullBannedPlayersIP(id, ip,  initiatorName, reason, type, realDate, realTime, time, uuid, fid);
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    if(!isNull) {
                        OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                            BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_BANS);
                        }
                    }
                    if(isNull) {
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban.ip-broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if (type == BanType.PERMANENT_IP) {
            if (isIpBanned(ip)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromNullBannedPlayers("-ip", ip);
                        BaseManager.getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        if(!isNull) {
                            OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                                BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_BANS);
                            }
                        }
                        getBanContainerManager().removeFromBanContainer("-ip", ip);
                        getBanContainerManager().addToBanContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, uuid.toString(), banPlayerEvent.getBanTime(), fid);
                        if(isNull) {
                            initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ip-ban-removed").replace("%1$f", ip)));
                        if (announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        BaseManager.getBaseManager().deleteFromNullBannedPlayers("-ip", ip);
                        BaseManager.getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        if(!isNull) {
                            OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                                BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_BANS);
                            }
                        }
                        if(isNull) {
                            initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ip-ban-removed").replace("%1$f", ip)));
                        if (announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    if(!isNull) {
                        OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                            BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_BANS);
                        }
                    }
                    getBanContainerManager().addToBanContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, uuid.toString(), banPlayerEvent.getBanTime(), fid);
                    if(isNull) {
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    BaseManager.getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    if(!isNull) {
                        OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                            BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_BANS);
                        }
                    }
                    if(isNull) {
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if (type == BanType.TIMED_IP) {
            if (isIpBanned(ip)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromBannedPlayers("-ip", ip);
                        BaseManager.getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        if(!isNull) {
                            OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                                BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_BANS);
                            }
                        }
                        getBanContainerManager().removeFromBanContainer("-ip", ip);
                        getBanContainerManager().addToBanContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, uuid.toString(), time, fid);
                        if(isNull) {
                            initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ip-ban-removed").replace("%1$f", ip)));
                        if (announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        BaseManager.getBaseManager().deleteFromBannedPlayers("-ip", ip);
                        BaseManager.getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        if(!isNull) {
                            OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                                BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_BANS);
                            }
                        }
                        if(isNull) {
                            initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ip-ban-removed").replace("%1$f", ip)));
                        if (announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    if(!isNull) {
                        OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                            BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_BANS);
                        }
                    }
                    getBanContainerManager().addToBanContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, uuid.toString(), time, fid);
                    if(isNull) {
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                } else {
                    BaseManager.getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    if(!isNull) {
                        OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                            BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_BANS);
                        }
                    }
                    if(isNull) {
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                }
            }
        }

        if (type == BanType.TIMED_NOT_IP) {
            if (isIpBanned(ip)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromBannedPlayers("-ip", ip);
                        BaseManager.getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        if(!isNull) {
                            OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                                BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_BANS);
                            }
                        }
                        getBanContainerManager().removeFromBanContainer("-ip", ip);
                        getBanContainerManager().addToBanContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, uuid.toString(), time, fid);
                        if(isNull) {
                            initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ip-ban-removed").replace("%1$f", ip)));
                        if (announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban.ip-broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        BaseManager.getBaseManager().deleteFromBannedPlayers("-ip", ip);
                        BaseManager.getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                        if(!isNull) {
                            OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                                BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_BANS);
                            }
                        }
                        if(isNull) {
                            initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.last-ip-ban-removed").replace("%1$f", ip)));
                        if (announceBan) {
                            Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban.ip-broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    if(!isNull) {
                        OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                            BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_BANS);
                        }
                    }
                    banContainerManager.addToBanContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, uuid.toString(), time, fid);
                    if(isNull) {
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban.ip-broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                } else {
                    BaseManager.getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                    BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_BANS);
                    if(!isNull) {
                        OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                            BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_BANS);
                        }
                    }
                    if(isNull) {
                        initiator.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceBan) {
                        Bukkit.broadcastMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.tempban.ip-broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                }
            }
        }
    }

}
