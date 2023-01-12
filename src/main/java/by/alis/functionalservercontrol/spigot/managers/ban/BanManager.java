package by.alis.functionalservercontrol.spigot.managers.ban;

import by.alis.functionalservercontrol.api.enums.StatsType;
import by.alis.functionalservercontrol.api.events.AsyncBanPreprocessEvent;
import by.alis.functionalservercontrol.api.enums.BanType;
import by.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import by.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.managers.IdsManager;
import by.alis.functionalservercontrol.spigot.managers.time.TimeManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBanContainerManager;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getDate;
import static by.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getTime;
import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;
import static by.alis.functionalservercontrol.spigot.managers.ban.BanChecker.isIpBanned;
import static by.alis.functionalservercontrol.spigot.managers.ban.BanChecker.isPlayerBanned;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

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
    public void preformBan(OfflinePlayer player, BanType type, String reason, CommandSender initiator, long time, boolean announceBan) {

        IdsManager idsManager = new IdsManager();
        TimeManager timeManager = new TimeManager();

        BanContainerManager banContainerManager = new BanContainerManager();
        
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
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                banPlayerEvent.setCancelled(true);
                return;
            }
        }

        if(reason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
            if(!getConfigSettings().isBanAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-reason")));
                banPlayerEvent.setCancelled(true);
                return;
            }
        }

        if(initiator instanceof Player) {
            if (!initiator.hasPermission("functionalservercontrol.time-bypass")) {
                if (time > timeManager.getMaxPlayerBanPunishTime((Player) initiator)) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.ban-over-time").replace("%1$f", timeManager.convertFromMillis(timeManager.getMaxPlayerBanPunishTime((Player) initiator)))));
                    banPlayerEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(getConfigSettings().isProhibitYourselfInteraction()) {
            if(initiator.getName().equalsIgnoreCase(player.getName())) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-yourself-actions")));
                banPlayerEvent.setCancelled(true);
                return;
            }
        }

        reason = banPlayerEvent.getReason().replace("'", "\"");
        String initiatorName = null;
        if(initiator instanceof Player) {
            initiatorName = ((Player)initiator).getName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }


        if(type == BanType.PERMANENT_NOT_IP) {
            if (isPlayerBanned(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                        getBaseManager().deleteFromBannedPlayers("-ip", getBaseManager().getIpByUUID(player.getUniqueId()));
                        getBaseManager().insertIntoBannedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.ban").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof  Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                        try { getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId())); } catch (NullPointerException ingored) {}
                        try { getBanContainerManager().removeFromBanContainer("-ip", getBaseManager().getIpByUUID(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        getBanContainerManager().addToBanContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
                        if(player.isOnline()) {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));                            }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player.getName())));
                        if(announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        getBaseManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));getBaseManager().deleteFromBannedPlayers("-ip", getBaseManager().getIpByUUID(player.getUniqueId()));
                        getBaseManager().insertIntoBannedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.ban").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof  Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player.getName())));
                        if(player.isOnline()) {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                        }
                        if(announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoBannedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.ban").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof  Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                    getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                    getBanContainerManager().addToBanContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
                    if(announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        String finalReason = reason;
                        String finalInitiatorName = initiatorName;
                        Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                        });
                    }
                } else {
                    getBaseManager().insertIntoBannedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.ban").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof  Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                    getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                    getBanContainerManager().addToBanContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
                    if(announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        String finalReason = reason;
                        String finalInitiatorName = initiatorName;
                        Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                        });
                    }
                }
            }
        }

        if(type == BanType.PERMANENT_IP) {
            if(isIpBanned(player)) {
                if(initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                        getBaseManager().deleteFromBannedPlayers("-ip", getBaseManager().getIpByUUID(player.getUniqueId()));
                        getBaseManager().insertIntoBannedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason));
                        if(initiator instanceof  Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                        try { getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId())); } catch (NullPointerException ingored) {}
                        try { getBanContainerManager().removeFromBanContainer("-ip", getBaseManager().getIpByUUID(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        getBanContainerManager().addToBanContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player.getName())));
                        if(announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason)));
                        }
                        if(player.isOnline()) {
                            String finalReason = reason;
                            String finalInitiatorName = initiatorName;
                            Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                                player.getPlayer().kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                            });
                        }
                    } else {
                        getBaseManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                        getBaseManager().deleteFromBannedPlayers("-ip", getBaseManager().getIpByUUID(player.getUniqueId()));
                        getBaseManager().insertIntoBannedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof  Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player.getName())));
                        if(announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason)));
                        }
                        if(player.isOnline()) {
                            String finalReason = reason;
                            String finalInitiatorName = initiatorName;
                            Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                                player.getPlayer().kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                            });
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoBannedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof  Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                    getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                    getBanContainerManager().addToBanContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
                    if(announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        String finalReason = reason;
                        String finalInitiatorName = initiatorName;
                        Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                        });
                    }
                } else {
                    getBaseManager().insertIntoBannedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof  Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                    getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                    if(announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        String finalReason = reason;
                        String finalInitiatorName = initiatorName;
                        Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                        });
                    }
                    return;
                }
            }
        }

        if(type == BanType.TIMED_NOT_IP) {
            if (isPlayerBanned(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                        getBaseManager().deleteFromBannedPlayers("-ip", getBaseManager().getIpByUUID(player.getUniqueId()));
                        getBaseManager().insertIntoBannedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempban").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof  Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                        try { getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        try { getBanContainerManager().removeFromBanContainer("-ip", getBaseManager().getIpByUUID(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        getBanContainerManager().addToBanContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), banPlayerEvent.getBanTime());
                        if(player.isOnline()) {
                            String finalInitiatorName = initiatorName;
                            String finalReason = reason;
                            Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                                player.getPlayer().kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime())))));
                            });
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player.getName())));
                        if(announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%1$f", initiatorName).replace("%4$f", reason)));
                        }
                        return;
                    } else {
                        getBaseManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                        getBaseManager().deleteFromBannedPlayers("-ip", getBaseManager().getIpByUUID(player.getUniqueId()));
                        getBaseManager().insertIntoBannedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempban").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof  Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player.getName())));
                        if(player.isOnline()) {
                            String finalReason = reason;
                            String finalInitiatorName = initiatorName;
                            Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                                player.getPlayer().kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime())))));
                            });
                        }
                        if(announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%1$f", initiatorName).replace("%4$f", reason)));
                        }
                    }
                    return;
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {

                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoBannedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempban").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof  Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                    getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                    getBanContainerManager().addToBanContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), banPlayerEvent.getBanTime());
                    if(announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%1$f", initiatorName).replace("%4$f", reason)));
                    }
                    if(player.isOnline()) {
                        String finalReason = reason;
                        String finalInitiatorName = initiatorName;
                        Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime())))));
                        });
                    }
                } else {
                    getBaseManager().insertIntoBannedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempban").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof  Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                    getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                    if(player.isOnline()) {
                        String finalReason = reason;
                        String finalInitiatorName = initiatorName;
                        Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime())))));
                        });
                    }
                    if(announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%1$f", initiatorName).replace("%4$f", reason)));
                    }
                    return;
                }
            }
        }

        if(type == BanType.TIMED_IP) {
            if(isIpBanned(player)) {
                if(initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                        getBaseManager().deleteFromBannedPlayers("-ip", getBaseManager().getIpByUUID(player.getUniqueId()));
                        getBaseManager().insertIntoBannedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof  Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                        try { getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        try { getBanContainerManager().removeFromBanContainer("-ip", getBaseManager().getIpByUUID(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        getBanContainerManager().addToBanContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), banPlayerEvent.getBanTime());
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player.getName())));
                        if(announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                        if(player.isOnline()) {
                            String finalReason = reason;
                            String finalInitiatorName = initiatorName;
                            Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                                player.getPlayer().kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName)).replace("%4$f", realDate + ", " + realTime).replace("%5$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))));
                            });
                        }
                    } else {
                        getBaseManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                        getBaseManager().deleteFromBannedPlayers("-ip", getBaseManager().getIpByUUID(player.getUniqueId()));
                        getBaseManager().insertIntoBannedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof  Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player.getName())));
                        if(announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                        if(player.isOnline()) {
                            String finalReason = reason;
                            String finalInitiatorName = initiatorName;
                            Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                                player.getPlayer().kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName)).replace("%4$f", realDate + ", " + realTime).replace("%5$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))));
                            });
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoBannedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof  Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                    getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                    getBanContainerManager().addToBanContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), banPlayerEvent.getBanTime());
                    if(announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                    if(player.isOnline()) {
                        String finalInitiatorName = initiatorName;
                        String finalReason = reason;
                        Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName)).replace("%4$f", realDate + ", " + realTime).replace("%5$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))));
                        });
                    }
                } else {
                    getBaseManager().insertIntoBannedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof  Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                    getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                    if(announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                    if(player.isOnline()) {
                        String finalInitiatorName = initiatorName;
                        String finalReason = reason;
                        Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName)).replace("%4$f", realDate + ", " + realTime).replace("%5$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))));
                        });
                    }
                }
            }
        }

    }

    public void preformBan(String player, BanType type, String reason, CommandSender initiator, long time, boolean announceBan) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
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
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                banPlayerEvent.setCancelled(true);
                return;
            }
        }

        if(reason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
            if(!getConfigSettings().isBanAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-reason")));
                banPlayerEvent.setCancelled(true);
                return;
            }
        }

        if(initiator instanceof Player) {
            if (!initiator.hasPermission("functionalservercontrol.time-bypass")) {
                if (time > timeManager.getMaxPlayerBanPunishTime((Player) initiator)) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.ban-over-time").replace("%1$f", timeManager.convertFromMillis(timeManager.getMaxPlayerBanPunishTime((Player) initiator)))));
                    banPlayerEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(getConfigSettings().isProhibitYourselfInteraction()) {
            if(initiator.getName().equalsIgnoreCase(player)) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-yourself-actions")));
                banPlayerEvent.setCancelled(true);
                return;
            }
        }

        String initiatorName = "ERROR";
        if (initiator instanceof Player) {
            initiatorName = ((Player) initiator).getName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }

        if (type == BanType.PERMANENT_NOT_IP) {
            if (isPlayerBanned(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromNullBannedPlayers("-n", player);
                        getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.ban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_BANS);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                        }
                        getBanContainerManager().removeFromBanContainer("-n", player);
                        getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(offlinePlayer.getUniqueId()), time);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if (announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        getBaseManager().deleteFromNullBannedPlayers("-n", player);
                        getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.ban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_BANS);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if (announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.ban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_BANS);
                    if (offlinePlayer != null) {
                        getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                        getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                    }
                    getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(offlinePlayer.getUniqueId()), time);
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.ban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_BANS);
                    if (offlinePlayer != null) {
                        getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                        getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                    }
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if (type == BanType.PERMANENT_IP) {
            if (isPlayerBanned(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromNullBannedPlayers("-n", player);
                        getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_BANS);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                        }
                        getBanContainerManager().removeFromBanContainer("-n", player);
                        getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", banPlayerEvent.getBanTime());
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if (announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        getBaseManager().deleteFromNullBannedPlayers("-n", player);
                        getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_BANS);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if (announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_BANS);
                    if (offlinePlayer != null) {
                        getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                        getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                    }
                    getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", banPlayerEvent.getBanTime());
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_BANS);
                    if (offlinePlayer != null) {
                        getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                        getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                    }
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if (type == BanType.TIMED_IP) {
            if (isPlayerBanned(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromBannedPlayers("-n", player);
                        getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_BANS);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                        }
                        getBanContainerManager().removeFromBanContainer("-n", player);
                        getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(offlinePlayer.getUniqueId()), time);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if (announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        getBaseManager().deleteFromBannedPlayers("-n", player);
                        getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_BANS);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if (announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_BANS);
                    if (offlinePlayer != null) {
                        getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                        getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                    }
                    getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(offlinePlayer.getUniqueId()), time);
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                } else {
                    getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_BANS);
                    if (offlinePlayer != null) {
                        getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                        getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                    }
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                }
            }
        }

        if (type == BanType.TIMED_NOT_IP) {
            if (isPlayerBanned(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromBannedPlayers("-n", player);
                        getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_BANS);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                        }
                        getBanContainerManager().removeFromBanContainer("-n", player);
                        getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(offlinePlayer.getUniqueId()), time);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if (announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        getBaseManager().deleteFromBannedPlayers("-n", player);
                        getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_BANS);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if (announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_BANS);
                    if (offlinePlayer != null) {
                        getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                        getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                    }
                    banContainerManager.addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                } else {
                    getBaseManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_BANS);
                    if (offlinePlayer != null) {
                        getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                        getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                    }
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
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
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                banPlayerEvent.setCancelled(true);
                return;
            }
        }

        if(reason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
            if(!getConfigSettings().isBanAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-reason")));
                banPlayerEvent.setCancelled(true);
                return;
            }
        }

        if(initiator instanceof Player) {
            if (!initiator.hasPermission("functionalservercontrol.time-bypass")) {
                if (time > timeManager.getMaxPlayerBanPunishTime((Player) initiator)) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.ban-over-time").replace("%1$f", timeManager.convertFromMillis(timeManager.getMaxPlayerBanPunishTime((Player) initiator)))));
                    banPlayerEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(initiator instanceof Player) {
            if(getConfigSettings().isProhibitYourselfInteraction()) {
                if(((Player) initiator).getPlayer().getAddress().getAddress().getHostAddress().equalsIgnoreCase(ip)) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-yourself-actions")));
                    banPlayerEvent.setCancelled(true);
                    return;
                }
            }
        }


        String initiatorName = "ERROR";
        if (initiator instanceof Player) {
            initiatorName = ((Player) initiator).getName();
        } else if(initiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = initiator.getName();
        }

        if (type == BanType.PERMANENT_NOT_IP) {
            if (isIpBanned(ip)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromNullBannedPlayers("-ip", ip);
                        getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName, reason, type, realDate, realTime, time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                        if(!isNull) {
                            OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                                getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                            }
                        }
                        getBanContainerManager().removeFromBanContainer("-ip", ip);
                        getBanContainerManager().addToBanContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                        if(isNull) {
                            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ip-ban-removed").replace("%1$f", ip)));
                        if (announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban.ip-broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        getBaseManager().deleteFromNullBannedPlayers("-ip", ip);
                        getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                        if(!isNull) {
                            OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                                getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                            }
                        }
                        if(isNull) {
                            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ip-ban-removed").replace("%1$f", ip)));
                        if (announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban.ip-broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoNullBannedPlayersIP(id, ip,  initiatorName, reason, type, realDate, realTime, time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                    if(!isNull) {
                        OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                        }
                    }
                    getBanContainerManager().addToBanContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                    if(isNull) {
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban.ip-broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    getBaseManager().insertIntoNullBannedPlayersIP(id, ip,  initiatorName, reason, type, realDate, realTime, time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                    if(!isNull) {
                        OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                        }
                    }
                    if(isNull) {
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban.ip-broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if (type == BanType.PERMANENT_IP) {
            if (isIpBanned(ip)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromNullBannedPlayers("-ip", ip);
                        getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                        if(!isNull) {
                            OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                                getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                            }
                        }
                        getBanContainerManager().removeFromBanContainer("-ip", ip);
                        getBanContainerManager().addToBanContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", banPlayerEvent.getBanTime());
                        if(isNull) {
                            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ip-ban-removed").replace("%1$f", ip)));
                        if (announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        getBaseManager().deleteFromNullBannedPlayers("-ip", ip);
                        getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                        if(!isNull) {
                            OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                                getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                            }
                        }
                        if(isNull) {
                            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ip-ban-removed").replace("%1$f", ip)));
                        if (announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                    if(!isNull) {
                        OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                        }
                    }
                    getBanContainerManager().addToBanContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", banPlayerEvent.getBanTime());
                    if(isNull) {
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.banip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                    if(!isNull) {
                        OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                        }
                    }
                    if(isNull) {
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if (type == BanType.TIMED_IP) {
            if (isIpBanned(ip)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromBannedPlayers("-ip", ip);
                        getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                        if(!isNull) {
                            OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                                getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                            }
                        }
                        getBanContainerManager().removeFromBanContainer("-ip", ip);
                        getBanContainerManager().addToBanContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                        if(isNull) {
                            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ip-ban-removed").replace("%1$f", ip)));
                        if (announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        getBaseManager().deleteFromBannedPlayers("-ip", ip);
                        getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                        if(!isNull) {
                            OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                                getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                            }
                        }
                        if(isNull) {
                            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ip-ban-removed").replace("%1$f", ip)));
                        if (announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                    if(!isNull) {
                        OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                        }
                    }
                    getBanContainerManager().addToBanContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                    if(isNull) {
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                } else {
                    getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                    if(!isNull) {
                        OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                        }
                    }
                    if(isNull) {
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                }
            }
        }

        if (type == BanType.TIMED_NOT_IP) {
            if (isIpBanned(ip)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromBannedPlayers("-ip", ip);
                        getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                        if(!isNull) {
                            OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                                getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                            }
                        }
                        getBanContainerManager().removeFromBanContainer("-ip", ip);
                        getBanContainerManager().addToBanContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                        if(isNull) {
                            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ip-ban-removed").replace("%1$f", ip)));
                        if (announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban.ip-broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        getBaseManager().deleteFromBannedPlayers("-ip", ip);
                        getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                        if(!isNull) {
                            OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                                getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                            }
                        }
                        if(isNull) {
                            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ip-ban-removed").replace("%1$f", ip)));
                        if (announceBan) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban.ip-broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                    if(!isNull) {
                        OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                        }
                    }
                    banContainerManager.addToBanContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                    if(isNull) {
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban.ip-broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                } else {
                    getBaseManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempbanip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_BANS);
                    if(!isNull) {
                        OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_BANS);
                        }
                    }
                    if(isNull) {
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceBan) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempban.ip-broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                }
            }
        }
    }

}
