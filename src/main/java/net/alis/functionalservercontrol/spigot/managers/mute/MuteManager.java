package net.alis.functionalservercontrol.spigot.managers.mute;

import net.alis.functionalservercontrol.api.enums.MuteType;
import net.alis.functionalservercontrol.api.enums.StatsType;
import net.alis.functionalservercontrol.api.events.AsyncMutePreprocessEvent;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.Component;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.spigot.managers.IdsManager;
import net.alis.functionalservercontrol.spigot.managers.file.SFAccessor;
import net.alis.functionalservercontrol.spigot.managers.time.TimeManager;
import net.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers;
import net.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.UUID;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static net.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getDate;
import static net.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getTime;
import static net.alis.functionalservercontrol.spigot.managers.mute.MuteChecker.isIpMuted;
import static net.alis.functionalservercontrol.spigot.managers.mute.MuteChecker.isPlayerMuted;

public class MuteManager {
    
    private static final MuteContainerManager muteContainerManager = new MuteContainerManager();

    public void preformMute(OfflineFunctionalPlayer player, MuteType type, String reason, CommandSender initiator, long time, boolean announceMute) {

        IdsManager idsManager = new IdsManager();
        TimeManager timeManager = new TimeManager();

        String realTime = WorldTimeAndDateClass.getTime();
        String realDate = WorldTimeAndDateClass.getDate();
        if(type == MuteType.PERMANENT_IP || type == MuteType.PERMANENT_NOT_IP) time = -1;
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
        AsyncMutePreprocessEvent asyncMutePreprocessEvent = new AsyncMutePreprocessEvent(id, player, initiator, type, time, reason, realTime, realDate, convertedTime);
        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(asyncMutePreprocessEvent);
        }
        if(asyncMutePreprocessEvent.isCancelled()) return;

        if(!announceMute) {
            if(!initiator.hasPermission("functionalservercontrol.use.silently")) {
                initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                asyncMutePreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(reason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
            if(!getConfigSettings().isMuteAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.no-reason")));
                asyncMutePreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(initiator instanceof FunctionalPlayer) {
            if (!initiator.hasPermission("functionalservercontrol.time-bypass")) {
                if (time > timeManager.getMaxPlayerMutePunishTime((FunctionalPlayer) initiator)) {
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.mute-over-time").replace("%1$f", timeManager.convertFromMillis(timeManager.getMaxPlayerMutePunishTime((FunctionalPlayer) initiator)))));
                    asyncMutePreprocessEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(getConfigSettings().isProhibitYourselfInteraction()) {
            if(initiator.getName().equalsIgnoreCase(player.nickname())) {
                initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.no-yourself-actions")));
                asyncMutePreprocessEvent.setCancelled(true);
                return;
            }
        }

        reason = asyncMutePreprocessEvent.getReason().replace("'", "\"");
        String initiatorName = null;
        if(initiator instanceof FunctionalPlayer) {
            initiatorName = initiator.getName();
        } else if(initiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = initiator.getName();
        }

        if(StaticContainers.getCheckingCheatsPlayers().getCheckingPlayers().contains(player)) {
            if(getConfigSettings().isCheatCheckFunctionEnabled() && getConfigSettings().isPreventMuteDuringCheatCheck()) {
                asyncMutePreprocessEvent.setCancelled(true);
                initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.mute-player-on-check")));
                return;
            }
        }

        if(type == MuteType.PERMANENT_NOT_IP) {
            if (isPlayerMuted(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromMutedPlayers("-fid", player.getFunctionalId().toString());
                        BaseManager.getBaseManager().insertIntoMutedPlayers(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1, player.getFunctionalId());
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_MUTES);
                        getMuteContainerManager().removeFromMuteContainer("-u", String.valueOf(player.getUniqueId()));
                        getMuteContainerManager().addToMuteContainer(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L, player.getFunctionalId());
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player.nickname())));
                        if(announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        if(player.isOnline()) {
                            if(getConfigSettings().isSendTitleWhenMuted()) {
                                sendTitleMessageWhenMuted(player.getPlayer(), initiatorName, getGlobalVariables().getVariableNever(), reason, id);
                            }
                        }
                        return;
                    } else {
                        BaseManager.getBaseManager().deleteFromMutedPlayers("-fid", player.getFunctionalId().toString());
                        BaseManager.getBaseManager().insertIntoMutedPlayers(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1, player.getFunctionalId());
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_MUTES);
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player.nickname())));
                        if(announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        if(player.isOnline()) {
                            if(getConfigSettings().isSendTitleWhenMuted()) {
                                FunctionalPlayer onlinePlayer = player.getPlayer();
                                sendTitleMessageWhenMuted(onlinePlayer, initiatorName, getGlobalVariables().getVariableNever(), reason, id);
                            }
                        }
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {

                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoMutedPlayers(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1, player.getFunctionalId());
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_MUTES);
                    getMuteContainerManager().addToMuteContainer(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L, player.getFunctionalId());
                    if(announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        if(getConfigSettings().isSendTitleWhenMuted()) {
                            FunctionalPlayer onlinePlayer = player.getPlayer();
                            sendTitleMessageWhenMuted(onlinePlayer, initiatorName, getGlobalVariables().getVariableNever(), reason, id);
                        }
                    }
                } else {
                    BaseManager.getBaseManager().insertIntoMutedPlayers(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1, player.getFunctionalId());
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_MUTES);
                    getMuteContainerManager().addToMuteContainer(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L, player.getFunctionalId());
                    if(announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        if(getConfigSettings().isSendTitleWhenMuted()) {
                            FunctionalPlayer onlinePlayer = player.getPlayer();
                            sendTitleMessageWhenMuted(onlinePlayer, initiatorName, getGlobalVariables().getVariableNever(), reason, id);
                        }
                    }
                }
            }
        }

        if(type == MuteType.PERMANENT_IP) {
            if(isIpMuted(player)) {
                if(initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromMutedPlayers("-fid", player.getFunctionalId().toString());
                        
                        BaseManager.getBaseManager().insertIntoMutedPlayers(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1, player.getFunctionalId());
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_MUTES);
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-u", String.valueOf(player.getUniqueId())); } catch (NullPointerException ingored) {}
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-ip", BaseManager.getBaseManager().getIpByUUID(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        getMuteContainerManager().addToMuteContainer(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L, player.getFunctionalId());
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player.nickname())));
                        if(announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason)));
                        }
                        if(player.isOnline()) {
                            if(getConfigSettings().isSendTitleWhenMuted()) {
                                FunctionalPlayer onlinePlayer = player.getPlayer();
                                sendTitleMessageWhenMuted(onlinePlayer, initiatorName, getGlobalVariables().getVariableNever(), reason, id);
                            }
                        }
                    } else {
                        BaseManager.getBaseManager().deleteFromMutedPlayers("-fid", player.getFunctionalId().toString());
                        
                        BaseManager.getBaseManager().insertIntoMutedPlayers(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1, player.getFunctionalId());
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_MUTES);
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player.nickname())));
                        if(announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason)));
                        }
                        if(player.isOnline()) {
                            if(getConfigSettings().isSendTitleWhenMuted()) {
                                FunctionalPlayer onlinePlayer = player.getPlayer();
                                sendTitleMessageWhenMuted(onlinePlayer, initiatorName, getGlobalVariables().getVariableNever(), reason, id);
                            }
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoMutedPlayers(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1, player.getFunctionalId());
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_MUTES);
                    getMuteContainerManager().addToMuteContainer(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L, player.getFunctionalId());
                    if(announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        if(getConfigSettings().isSendTitleWhenMuted()) {
                            FunctionalPlayer onlinePlayer = player.getPlayer();
                            assert onlinePlayer != null;
                            sendTitleMessageWhenMuted(onlinePlayer, initiatorName, getGlobalVariables().getVariableNever(), reason, id);
                        }
                    }
                } else {
                    BaseManager.getBaseManager().insertIntoMutedPlayers(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1, player.getFunctionalId());
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", reason));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_MUTES);
                    if(announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        if(getConfigSettings().isSendTitleWhenMuted()) {
                            FunctionalPlayer onlinePlayer = player.getPlayer();
                            assert onlinePlayer != null;
                            sendTitleMessageWhenMuted(onlinePlayer, initiatorName, getGlobalVariables().getVariableNever(), reason, id);
                        }
                    }
                    return;
                }
            }
        }

        if(type == MuteType.TIMED_NOT_IP) {
            if (isPlayerMuted(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromMutedPlayers("-fid", player.getFunctionalId().toString());
                        BaseManager.getBaseManager().insertIntoMutedPlayers(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime(), player.getFunctionalId());
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", convertedTime).replace("%4$f", reason));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_MUTES);
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-u", String.valueOf(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-ip", BaseManager.getBaseManager().getIpByUUID(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        getMuteContainerManager().addToMuteContainer(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), asyncMutePreprocessEvent.getMuteTime(), player.getFunctionalId());
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player.nickname())));
                        if(announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime()))).replace("%1$f", initiatorName).replace("%4$f", reason)));
                        }
                        if(player.isOnline()) {
                            if(getConfigSettings().isSendTitleWhenMuted()) {
                                FunctionalPlayer onlinePlayer = player.getPlayer();
                                sendTitleMessageWhenMuted(onlinePlayer, initiatorName, timeManager.convertFromMillis(asyncMutePreprocessEvent.getMuteTime() - System.currentTimeMillis()), reason, id);
                            }
                        }
                        return;
                    } else {
                        BaseManager.getBaseManager().deleteFromMutedPlayers("-fid", player.getFunctionalId().toString());
                        
                        BaseManager.getBaseManager().insertIntoMutedPlayers(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime(), player.getFunctionalId());
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", convertedTime).replace("%4$f", reason));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_MUTES);
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player.nickname())));
                        if(announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime()))).replace("%1$f", initiatorName).replace("%4$f", reason)));
                        }
                        if(player.isOnline()) {
                            if(getConfigSettings().isSendTitleWhenMuted()) {
                                FunctionalPlayer onlinePlayer = player.getPlayer();
                                sendTitleMessageWhenMuted(onlinePlayer, initiatorName, timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime())), reason, id);
                            }
                        }
                    }
                    return;
                } else {
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {

                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoMutedPlayers(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime(), player.getFunctionalId());
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", convertedTime).replace("%4$f", reason));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_MUTES);
                    getMuteContainerManager().addToMuteContainer(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), asyncMutePreprocessEvent.getMuteTime(), player.getFunctionalId());
                    if(announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime()))).replace("%1$f", initiatorName).replace("%4$f", reason)));
                    }
                    if(player.isOnline()) {
                        if(getConfigSettings().isSendTitleWhenMuted()) {
                            FunctionalPlayer onlinePlayer = player.getPlayer();
                            sendTitleMessageWhenMuted(onlinePlayer, initiatorName, timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime())), reason, id);
                        }
                    }
                } else {
                    BaseManager.getBaseManager().insertIntoMutedPlayers(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime(), player.getFunctionalId());
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", convertedTime).replace("%4$f", reason));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_MUTES);
                    if(announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%2$f", player.nickname()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime()))).replace("%1$f", initiatorName).replace("%4$f", reason)));
                    }
                    if(player.isOnline()) {
                        if(getConfigSettings().isSendTitleWhenMuted()) {
                            FunctionalPlayer onlinePlayer = player.getPlayer();
                            sendTitleMessageWhenMuted(onlinePlayer, initiatorName, timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime())), reason, id);
                        }
                    }
                    return;
                }
            }
        }

        if(type == MuteType.TIMED_IP) {
            if(isIpMuted(player)) {
                if(initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromMutedPlayers("-fid", player.getFunctionalId().toString());
                        
                        BaseManager.getBaseManager().insertIntoMutedPlayers(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime(), player.getFunctionalId());
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_MUTES);
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-u", String.valueOf(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-ip", BaseManager.getBaseManager().getIpByUUID(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        getMuteContainerManager().addToMuteContainer(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), asyncMutePreprocessEvent.getMuteTime(), player.getFunctionalId());
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player.nickname())));
                        if(announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                        if(player.isOnline()) {
                            if(getConfigSettings().isSendTitleWhenMuted()) {
                                FunctionalPlayer onlinePlayer = player.getPlayer();
                                sendTitleMessageWhenMuted(onlinePlayer, initiatorName, timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime())), reason, id);
                            }
                        }
                    } else {
                        BaseManager.getBaseManager().deleteFromMutedPlayers("-fid", player.getFunctionalId().toString());
                        
                        BaseManager.getBaseManager().insertIntoMutedPlayers(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime(), player.getFunctionalId());
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_MUTES);
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player.nickname())));
                        if(announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                        if(player.isOnline()) {
                            if(getConfigSettings().isSendTitleWhenMuted()) {
                                FunctionalPlayer onlinePlayer = player.getPlayer();
                                sendTitleMessageWhenMuted(onlinePlayer, initiatorName, timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime())), reason, id);
                            }
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoMutedPlayers(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime(), player.getFunctionalId());
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_MUTES);
                    getMuteContainerManager().addToMuteContainer(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), asyncMutePreprocessEvent.getMuteTime(), player.getFunctionalId());
                    if(announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                    if(player.isOnline()) {
                        if(getConfigSettings().isSendTitleWhenMuted()) {
                            FunctionalPlayer onlinePlayer = player.getPlayer();
                            sendTitleMessageWhenMuted(onlinePlayer, initiatorName, timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime())), reason, id);
                        }
                    }
                } else {
                    BaseManager.getBaseManager().insertIntoMutedPlayers(id, BaseManager.getBaseManager().getIpByUUID(player.getUniqueId()), player.nickname(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime(), player.getFunctionalId());
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_MUTES);
                    if(announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                    if(player.isOnline()) {
                        if(getConfigSettings().isSendTitleWhenMuted()) {
                            FunctionalPlayer onlinePlayer = player.getPlayer();
                            sendTitleMessageWhenMuted(onlinePlayer, initiatorName, timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime())), reason, id);
                        }
                    }
                }
            }
        }
    }

    public void preformMute(String player, MuteType type, String reason, CommandSender initiator, long time, boolean announceMute) {
        FID fid = new FID(player);
        UUID uuid = Bukkit.getOfflinePlayer(player).getUniqueId();
        IdsManager idsManager = new IdsManager();
        TimeManager timeManager = new TimeManager();

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
        AsyncMutePreprocessEvent mutePreprocessEvent = new AsyncMutePreprocessEvent(id, player, initiator, type, time, reason, realTime, realDate, convertedTime);
        reason = mutePreprocessEvent.getReason().replace("'", "\"");
        time = mutePreprocessEvent.getMuteTime();
        if (type == MuteType.PERMANENT_IP || type == MuteType.PERMANENT_NOT_IP) time = -1;
        if (getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(mutePreprocessEvent);
        }
        if (mutePreprocessEvent.isCancelled()) return;

        if(!announceMute) {
            if(!initiator.hasPermission("functionalservercontrol.use.silently")) {
                initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                mutePreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(reason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
            if(!getConfigSettings().isMuteAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.no-reason")));
                mutePreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(initiator instanceof FunctionalPlayer) {
            if (!initiator.hasPermission("functionalservercontrol.time-bypass")) {
                if (time > timeManager.getMaxPlayerMutePunishTime((FunctionalPlayer) initiator)) {
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.mute-over-time").replace("%1$f", timeManager.convertFromMillis(timeManager.getMaxPlayerMutePunishTime((FunctionalPlayer) initiator)))));
                    mutePreprocessEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(getConfigSettings().isProhibitYourselfInteraction()) {
            if(initiator.getName().equalsIgnoreCase(player)) {
                initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.no-yourself-actions")));
                mutePreprocessEvent.setCancelled(true);
                return;
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

        if (type == MuteType.PERMANENT_NOT_IP) {
            if (isPlayerMuted(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromNullMutedPlayers("-fid", fid.toString());
                        BaseManager.getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                        getMuteContainerManager().removeFromMuteContainer("-n", player);
                        getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(Bukkit.getOfflinePlayer(player).getUniqueId()), time, fid);
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        BaseManager.getBaseManager().deleteFromNullMutedPlayers("-fid", fid.toString());
                        BaseManager.getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, uuid, time, fid);
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                    getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, uuid.toString(), time, fid);
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    BaseManager.getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, uuid, time, fid);
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if (type == MuteType.PERMANENT_IP) {
            if (isPlayerMuted(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromNullMutedPlayers("-fid", fid.toString());
                        BaseManager.getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                        getMuteContainerManager().removeFromMuteContainer("-n", player);
                        getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, uuid.toString(), mutePreprocessEvent.getMuteTime(), fid);
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        BaseManager.getBaseManager().deleteFromNullMutedPlayers("-fid", fid.toString());
                        BaseManager.getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, uuid, time, fid);
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                    getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, uuid.toString(), mutePreprocessEvent.getMuteTime(), fid);
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    BaseManager.getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, uuid, time, fid);
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if (type == MuteType.TIMED_IP) {
            if (isPlayerMuted(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromNullMutedPlayers("-fid", fid.toString());
                        BaseManager.getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, Bukkit.getOfflinePlayer(player).getUniqueId(), time, fid);
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                        getMuteContainerManager().removeFromMuteContainer("-n", player);
                        getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, uuid.toString(), time, fid);
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        BaseManager.getBaseManager().deleteFromNullMutedPlayers("-fid", fid.toString());
                        BaseManager.getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, uuid, time, fid);
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, uuid, time, fid);
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                    getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, uuid.toString(), time, fid);
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                } else {
                    BaseManager.getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, uuid, time, fid);
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                }
            }
        }

        if (type == MuteType.TIMED_NOT_IP) {
            if (isPlayerMuted(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromNullMutedPlayers("-fid", fid.toString());
                        BaseManager.getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, uuid, time, fid);
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                        getMuteContainerManager().removeFromMuteContainer("-n", player);
                        getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(uuid), time, fid);
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        BaseManager.getBaseManager().deleteFromNullMutedPlayers("-fid", fid.toString());
                        BaseManager.getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, uuid, time, fid);
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                        BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, uuid, time, fid);
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                    getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(uuid), time, fid);
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                } else {
                    BaseManager.getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, uuid, time, fid);
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
                    BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                }
            }
        }
    }

    public void preformMuteByIp(String ip, MuteType type, String reason, CommandSender initiator, long time, boolean announceMute, boolean isNull) {

        IdsManager idsManager = new IdsManager();
        TimeManager timeManager = new TimeManager();

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
        AsyncMutePreprocessEvent mutePreprocessEvent = new AsyncMutePreprocessEvent(id, ip, initiator, type, time, reason, realTime, realDate, convertedTime);
        reason = mutePreprocessEvent.getReason().replace("'", "\"");
        time = mutePreprocessEvent.getMuteTime();
        if (type == MuteType.PERMANENT_IP || type == MuteType.PERMANENT_NOT_IP) time = -1;
        if (getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(mutePreprocessEvent);
        }
        if (mutePreprocessEvent.isCancelled()) return;

        if(!announceMute) {
            if(!initiator.hasPermission("functionalservercontrol.use.silently")) {
                initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                mutePreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(reason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
            if(!getConfigSettings().isMuteAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.no-reason")));
                mutePreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(initiator instanceof FunctionalPlayer) {
            if (!initiator.hasPermission("functionalservercontrol.time-bypass")) {
                if (time > timeManager.getMaxPlayerMutePunishTime((FunctionalPlayer) initiator)) {
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.mute-over-time").replace("%1$f", timeManager.convertFromMillis(timeManager.getMaxPlayerMutePunishTime((FunctionalPlayer) initiator)))));
                    mutePreprocessEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(initiator instanceof FunctionalPlayer) {
            if(getConfigSettings().isProhibitYourselfInteraction()) {
                if(((FunctionalPlayer)initiator).address().equalsIgnoreCase(ip)) {
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.no-yourself-actions")));
                    mutePreprocessEvent.setCancelled(true);
                    return;
                }
            }
        }
        
        FID fid = FID.random();
        UUID uuid = UUID.randomUUID();


        String initiatorName = "ERROR";
        if (initiator instanceof FunctionalPlayer) {
            initiatorName = initiator.getName();
        } else if(initiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = initiator.getName();
        }

        if (type == MuteType.PERMANENT_NOT_IP) {
            if (isIpMuted(ip)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromNullMutedPlayers("-ip", ip);
                        BaseManager.getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName, reason, type, realDate, realTime, time, uuid, fid);
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        if(!isNull) {
                            OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                                BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_MUTES);
                                
                            }
                        }
                        getMuteContainerManager().removeFromMuteContainer("-ip", ip);
                        getMuteContainerManager().addToMuteContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, uuid.toString(), time, fid);
                        if(isNull) {
                            initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-ip-mute-removed").replace("%1$f", ip)));
                        if (announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.mute.ip-broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        BaseManager.getBaseManager().deleteFromNullMutedPlayers("-ip", ip);
                        BaseManager.getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        if(!isNull) {
                            OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                                BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_MUTES);

                            }
                        }
                        if(isNull) {
                            initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-ip-mute-removed").replace("%1$f", ip)));
                        if (announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.mute.ip-broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoNullMutedPlayersIP(id, ip,  initiatorName, reason, type, realDate, realTime, time, uuid, fid);
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    if(!isNull) {
                        OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                            BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_MUTES);

                        }
                    }
                    getMuteContainerManager().addToMuteContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, uuid.toString(), time, fid);
                    if(isNull) {
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.mute.ip-broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    BaseManager.getBaseManager().insertIntoNullMutedPlayersIP(id, ip,  initiatorName, reason, type, realDate, realTime, time, uuid, fid);
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    if(!isNull) {
                        OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                            BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_MUTES);

                        }
                    }
                    if(isNull) {
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.mute.ip-broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if (type == MuteType.PERMANENT_IP) {
            if (isIpMuted(ip)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromNullMutedPlayers("-ip", ip);
                        BaseManager.getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        if(!isNull) {
                            OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                                BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_MUTES);

                            }
                        }
                        getMuteContainerManager().removeFromMuteContainer("-ip", ip);
                        getMuteContainerManager().addToMuteContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, uuid.toString(), mutePreprocessEvent.getMuteTime(), fid);
                        if(isNull) {
                            initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-ip-mute-removed").replace("%1$f", ip)));
                        if (announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        BaseManager.getBaseManager().deleteFromNullMutedPlayers("-ip", ip);
                        BaseManager.getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        if(!isNull) {
                            OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                                BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_MUTES);

                            }
                        }
                        if(isNull) {
                            initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-ip-mute-removed").replace("%1$f", ip)));
                        if (announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    if(!isNull) {
                        OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                            BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_MUTES);

                        }
                    }
                    getMuteContainerManager().addToMuteContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, uuid.toString(), mutePreprocessEvent.getMuteTime(), fid);
                    if(isNull) {
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    BaseManager.getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    if(!isNull) {
                        OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                            BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_MUTES);

                        }
                    }
                    if(isNull) {
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if (type == MuteType.TIMED_IP) {
            if (isIpMuted(ip)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromNullMutedPlayers("-ip", ip);
                        BaseManager.getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        if(!isNull) {
                            OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                                BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_MUTES);

                            }
                        }
                        getMuteContainerManager().removeFromMuteContainer("-ip", ip);
                        getMuteContainerManager().addToMuteContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, uuid.toString(), time, fid);
                        if(isNull) {
                            initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-ip-mute-removed").replace("%1$f", ip)));
                        if (announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        BaseManager.getBaseManager().deleteFromNullMutedPlayers("-ip", ip);
                        BaseManager.getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        if(!isNull) {
                            OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                                BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_MUTES);

                            }
                        }
                        if(isNull) {
                            initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-ip-mute-removed").replace("%1$f", ip)));
                        if (announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    if(!isNull) {
                        OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                            BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_MUTES);

                        }
                    }
                    getMuteContainerManager().addToMuteContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, uuid.toString(), time, fid);
                    if(isNull) {
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                } else {
                    BaseManager.getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    if(!isNull) {
                        OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                            BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_MUTES);

                        }
                    }
                    if(isNull) {
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                }
            }
        }

        if (type == MuteType.TIMED_NOT_IP) {
            if (isIpMuted(ip)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        BaseManager.getBaseManager().deleteFromNullMutedPlayers("-ip", ip);
                        BaseManager.getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        if(!isNull) {
                            OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                                BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_MUTES);

                            }
                        }
                        getMuteContainerManager().removeFromMuteContainer("-ip", ip);
                        getMuteContainerManager().addToMuteContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, uuid.toString(), time, fid);
                        if(isNull) {
                            initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-ip-mute-removed").replace("%1$f", ip)));
                        if (announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmute.ip-broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        BaseManager.getBaseManager().deleteFromNullMutedPlayers("-ip", ip);
                        BaseManager.getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                        BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                        if(!isNull) {
                            OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                                BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_MUTES);

                            }
                        }
                        if(isNull) {
                            initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.last-ip-mute-removed").replace("%1$f", ip)));
                        if (announceMute) {
                            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmute.ip-broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    BaseManager.getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    if(!isNull) {
                        OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                            BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_MUTES);

                        }
                    }
                    getMuteContainerManager().addToMuteContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, uuid.toString(), time, fid);
                    if(isNull) {
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmute.ip-broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                } else {
                    BaseManager.getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time, uuid, fid);
                    BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_MUTES);
                    if(!isNull) {
                        OfflineFunctionalPlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            BaseManager.getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getFunctionalId());
                            BaseManager.getBaseManager().updatePlayerStatsInfo(offlinePlayer.getFunctionalId(), StatsType.Player.STATS_MUTES);
                            
                        }
                    }
                    if(isNull) {
                        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceMute) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.tempmute.ip-broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                }
            }
        }
    }


    public void checkForNullMutedPlayer(FunctionalPlayer player) {
        FID fid = player.getFunctionalId();
        if(getConfigSettings().isAllowedUseRamAsContainer()) {

            if(getMutedPlayersContainer().getFids().contains(fid) && getMutedPlayersContainer().getIpContainer().get(getMutedPlayersContainer().getFids().indexOf(fid)).equalsIgnoreCase("NULL_PLAYER")) {
                int indexOf = getMutedPlayersContainer().getNameContainer().indexOf(player.nickname());
                String id = getMutedPlayersContainer().getIdsContainer().get(indexOf);
                String ip = player.address();
                String name = player.nickname();
                String initiatorName = getMutedPlayersContainer().getInitiatorNameContainer().get(indexOf);
                String reason = getMutedPlayersContainer().getReasonContainer().get(indexOf);
                MuteType muteType = getMutedPlayersContainer().getMuteTypesContainer().get(indexOf);
                String muteDate = getMutedPlayersContainer().getRealMuteDateContainer().get(indexOf);
                String muteTime = getMutedPlayersContainer().getRealMuteTimeContainer().get(indexOf);
                String uuid = String.valueOf(player.getUniqueId());
                long unmuteTime = getMutedPlayersContainer().getMuteTimeContainer().get(indexOf);
                getMuteContainerManager().removeFromMuteContainer("-n", name);

                BaseManager.getBaseManager().deleteFromNullMutedPlayers("-id", id);
                BaseManager.getBaseManager().insertIntoMutedPlayers(id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, UUID.fromString(uuid), unmuteTime, player.getFunctionalId());
                BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                

                getMuteContainerManager().addToMuteContainer(id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, uuid, unmuteTime, fid);
                return;
            }

            if(getMutedPlayersContainer().getIpContainer().contains(player.address())
                    && getMutedPlayersContainer().getUUIDContainer().get(getMutedPlayersContainer().getIpContainer().indexOf(player.address())).equalsIgnoreCase("NULL_PLAYER")) {

                int indexOf = getMutedPlayersContainer().getIpContainer().indexOf(player.address());
                String id = getMutedPlayersContainer().getIdsContainer().get(indexOf);
                String ip = player.address();
                String name = player.nickname();
                String initiatorName = getMutedPlayersContainer().getInitiatorNameContainer().get(indexOf);
                String reason = getMutedPlayersContainer().getReasonContainer().get(indexOf);
                MuteType muteType = getMutedPlayersContainer().getMuteTypesContainer().get(indexOf);
                String muteDate = getMutedPlayersContainer().getRealMuteDateContainer().get(indexOf);
                String muteTime = getMutedPlayersContainer().getRealMuteTimeContainer().get(indexOf);
                String uuid = String.valueOf(player.getUniqueId());
                long unmuteTime = getMutedPlayersContainer().getMuteTimeContainer().get(indexOf);
                getMuteContainerManager().removeFromMuteContainer("-ip", ip);

                BaseManager.getBaseManager().deleteFromNullMutedPlayers("-id", id);
                BaseManager.getBaseManager().insertIntoMutedPlayers(id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, UUID.fromString(uuid), unmuteTime, player.getFunctionalId());
                BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                

                getMuteContainerManager().addToMuteContainer(id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, uuid, unmuteTime, player.getFunctionalId());
                return;

            }

        } else {
            if(BaseManager.getBaseManager().getMutedPlayersNames().contains(player.nickname())
                    && BaseManager.getBaseManager().getMutedIps().get(BaseManager.getBaseManager().getMutedPlayersNames().indexOf(player.nickname())).equalsIgnoreCase("NULL_PLAYER")) {
                int indexOf = BaseManager.getBaseManager().getMutedPlayersNames().indexOf(player.nickname());
                String id = BaseManager.getBaseManager().getMutedIds().get(indexOf);
                String ip = player.address();
                String name = player.nickname();
                String initiatorName = BaseManager.getBaseManager().getMuteInitiators().get(indexOf);
                String reason = BaseManager.getBaseManager().getMuteReasons().get(indexOf);
                MuteType muteType = BaseManager.getBaseManager().getMuteTypes().get(indexOf);
                String muteDate = BaseManager.getBaseManager().getMuteDates().get(indexOf);
                String muteTime = BaseManager.getBaseManager().getMuteTimes().get(indexOf);
                String uuid = String.valueOf(player.getUniqueId());
                long unmuteTime = BaseManager.getBaseManager().getUnmuteTimes().get(indexOf);

                BaseManager.getBaseManager().deleteFromNullMutedPlayers("-id", id);
                BaseManager.getBaseManager().insertIntoMutedPlayers(id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, UUID.fromString(uuid), unmuteTime, player.getFunctionalId());
                BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);

                return;
            }

            if(BaseManager.getBaseManager().getMutedIps().contains(player.address())
                    && BaseManager.getBaseManager().getMutedUUIDs().get(BaseManager.getBaseManager().getMutedIps().indexOf(player.address())).equalsIgnoreCase("NULL_PLAYER")) {

                int indexOf = BaseManager.getBaseManager().getMutedIps().indexOf(player.address());
                String id = BaseManager.getBaseManager().getMutedIds().get(indexOf);
                String ip = player.address();
                String name = player.nickname();
                String initiatorName = BaseManager.getBaseManager().getMuteInitiators().get(indexOf);
                String reason = BaseManager.getBaseManager().getMuteReasons().get(indexOf);
                MuteType muteType = BaseManager.getBaseManager().getMuteTypes().get(indexOf);
                String muteDate = BaseManager.getBaseManager().getMuteDates().get(indexOf);
                String muteTime = BaseManager.getBaseManager().getMuteTimes().get(indexOf);
                String uuid = String.valueOf(player.getUniqueId());
                long unmuteTime = BaseManager.getBaseManager().getUnmuteTimes().get(indexOf);
                BaseManager.getBaseManager().deleteFromNullMutedPlayers("-id", id);
                BaseManager.getBaseManager().insertIntoMutedPlayers(id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, UUID.fromString(uuid), unmuteTime, player.getFunctionalId());
                BaseManager.getBaseManager().updatePlayerStatsInfo(fid, StatsType.Player.STATS_MUTES);
                return;
            }
        }
    }



    public void notifyAboutMuteOnJoin(FunctionalPlayer player) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            if(getMutedPlayersContainer().getUUIDContainer().contains(String.valueOf(player.getUniqueId()))) {
                int indexOf = getMutedPlayersContainer().getUUIDContainer().indexOf(String.valueOf(player.getUniqueId()));
                MuteType muteType = getMutedPlayersContainer().getMuteTypesContainer().get(indexOf);
                String translatedUnmuteTime = getGlobalVariables().getVariableNever();
                if(muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                    if(System.currentTimeMillis() >= getMutedPlayersContainer().getMuteTimeContainer().get(indexOf)) return;
                    TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                    translatedUnmuteTime = timeSettingsAccessor.getTimeManager().convertFromMillis(getMutedPlayersContainer().getMuteTimeContainer().get(indexOf) - System.currentTimeMillis());
                }
                player.expansion().message(Component.createHoverText(SFAccessor.getFileAccessor().getLang().getString("other.join.muted.text").replace("%1$f", TextUtils.setColors(translatedUnmuteTime)),
                        SFAccessor.getFileAccessor().getLang().getString("other.join.muted.hover-text")
                                .replace("%1$f", getMutedPlayersContainer().getInitiatorNameContainer().get(indexOf))
                                .replace("%2$f", getMutedPlayersContainer().getRealMuteDateContainer().get(indexOf))
                                .replace("%3$f", getMutedPlayersContainer().getRealMuteTimeContainer().get(indexOf))
                                .replace("%4$f", getMutedPlayersContainer().getReasonContainer().get(indexOf))
                                .replace("%5$f", getMutedPlayersContainer().getIdsContainer().get(indexOf))
                ).translateDefaultColorCodes().translateDefaultColorCodes());
            }
        } else {
            if(BaseManager.getBaseManager().getMutedUUIDs().contains(String.valueOf(player.getUniqueId()))) {
                int indexOf = BaseManager.getBaseManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId()));
                MuteType muteType = BaseManager.getBaseManager().getMuteTypes().get(indexOf);
                String translatedUnmuteTime = getGlobalVariables().getVariableNever();
                if(muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                    if(System.currentTimeMillis() >= BaseManager.getBaseManager().getUnmuteTimes().get(indexOf)) return;
                    TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                    translatedUnmuteTime = timeSettingsAccessor.getTimeManager().convertFromMillis(BaseManager.getBaseManager().getUnmuteTimes().get(indexOf) - System.currentTimeMillis());
                }
                player.expansion().message(Component.createHoverText(
                        SFAccessor.getFileAccessor().getLang().getString("other.join.muted.text").replace("%1$f", TextUtils.setColors(translatedUnmuteTime)),
                        SFAccessor.getFileAccessor().getLang().getString("other.join.muted.hover-text")
                                .replace("%1$f", BaseManager.getBaseManager().getMuteInitiators().get(indexOf))
                                .replace("%2$f", BaseManager.getBaseManager().getMuteDates().get(indexOf))
                                .replace("%3$f", BaseManager.getBaseManager().getMuteTimes().get(indexOf))
                                .replace("%4$f", BaseManager.getBaseManager().getMuteReasons().get(indexOf))
                                .replace("%5$f", BaseManager.getBaseManager().getMutedIds().get(indexOf))
                ).translateDefaultColorCodes());
            }
        }
    }

    public void notifyAboutMuteOnCommand(FunctionalPlayer player) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            int indexOf = getMutedPlayersContainer().getUUIDContainer().indexOf(String.valueOf(player.getUniqueId()));
            MuteType muteType = getMutedPlayersContainer().getMuteTypesContainer().get(indexOf);
            String translatedUnmuteTime = getGlobalVariables().getVariableNever();
            if(muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                translatedUnmuteTime = timeSettingsAccessor.getTimeManager().convertFromMillis(getMutedPlayersContainer().getMuteTimeContainer().get(indexOf) - System.currentTimeMillis());
            }
            player.expansion().message(Component.createHoverText(
                    SFAccessor.getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.text").replace("%1$f", TextUtils.setColors(translatedUnmuteTime)),
                    SFAccessor.getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.hover-text")
                            .replace("%1$f", getMutedPlayersContainer().getInitiatorNameContainer().get(indexOf))
                            .replace("%2$f", getMutedPlayersContainer().getRealMuteDateContainer().get(indexOf))
                            .replace("%3$f", getMutedPlayersContainer().getRealMuteTimeContainer().get(indexOf))
                            .replace("%4$f", getMutedPlayersContainer().getReasonContainer().get(indexOf))
                            .replace("%5$f", getMutedPlayersContainer().getIdsContainer().get(indexOf))
            ).translateDefaultColorCodes());
        } else {
            int indexOf = BaseManager.getBaseManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId()));
            MuteType muteType = BaseManager.getBaseManager().getMuteTypes().get(indexOf);
            String translatedUnmuteTime = getGlobalVariables().getVariableNever();
            if(muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                translatedUnmuteTime = timeSettingsAccessor.getTimeManager().convertFromMillis(BaseManager.getBaseManager().getUnmuteTimes().get(indexOf) - System.currentTimeMillis());
            }
            player.expansion().message(Component.createHoverText(
                    SFAccessor.getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.text").replace("%1$f", TextUtils.setColors(translatedUnmuteTime)),
                    SFAccessor.getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.hover-text")
                            .replace("%1$f", BaseManager.getBaseManager().getMuteInitiators().get(indexOf))
                            .replace("%2$f", BaseManager.getBaseManager().getMuteDates().get(indexOf))
                            .replace("%3$f", BaseManager.getBaseManager().getMuteTimes().get(indexOf))
                            .replace("%4$f", BaseManager.getBaseManager().getMuteReasons().get(indexOf))
                            .replace("%5$f", BaseManager.getBaseManager().getMutedIds().get(indexOf))
            ).translateDefaultColorCodes());
        }
    }

    public void notifyAboutMuteOnChat(FunctionalPlayer player) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            int indexOf = getMutedPlayersContainer().getUUIDContainer().indexOf(String.valueOf(player.getUniqueId()));
            MuteType muteType = getMutedPlayersContainer().getMuteTypesContainer().get(indexOf);
            String translatedUnmuteTime = getGlobalVariables().getVariableNever();
            if(muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                translatedUnmuteTime = timeSettingsAccessor.getTimeManager().convertFromMillis(getMutedPlayersContainer().getMuteTimeContainer().get(indexOf) - System.currentTimeMillis());
            }
            player.expansion().message(Component.createHoverText(
                    SFAccessor.getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.text").replace("%1$f", TextUtils.setColors(translatedUnmuteTime)),
                    SFAccessor.getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.hover-text")
                            .replace("%1$f", getMutedPlayersContainer().getInitiatorNameContainer().get(indexOf))
                            .replace("%2$f", getMutedPlayersContainer().getRealMuteDateContainer().get(indexOf))
                            .replace("%3$f", getMutedPlayersContainer().getRealMuteTimeContainer().get(indexOf))
                            .replace("%4$f", getMutedPlayersContainer().getReasonContainer().get(indexOf))
                            .replace("%5$f", getMutedPlayersContainer().getIdsContainer().get(indexOf))
            ).translateDefaultColorCodes());
        } else {
            int indexOf = BaseManager.getBaseManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId()));
            MuteType muteType = BaseManager.getBaseManager().getMuteTypes().get(indexOf);
            String translatedUnmuteTime = getGlobalVariables().getVariableNever();
            if(muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                translatedUnmuteTime = timeSettingsAccessor.getTimeManager().convertFromMillis(BaseManager.getBaseManager().getUnmuteTimes().get(indexOf) - System.currentTimeMillis());
            }
            player.expansion().message(Component.createHoverText(
                    SFAccessor.getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.text").replace("%1$f", TextUtils.setColors(translatedUnmuteTime)),
                    SFAccessor.getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.hover-text")
                            .replace("%1$f", BaseManager.getBaseManager().getMuteInitiators().get(indexOf))
                            .replace("%2$f", BaseManager.getBaseManager().getMuteDates().get(indexOf))
                            .replace("%3$f", BaseManager.getBaseManager().getMuteTimes().get(indexOf))
                            .replace("%4$f", BaseManager.getBaseManager().getMuteReasons().get(indexOf))
                            .replace("%5$f", BaseManager.getBaseManager().getMutedIds().get(indexOf))
            ).translateDefaultColorCodes());
        }
    }

    public void sendTitleMessageWhenMuted(FunctionalPlayer player, String initiatorName, String unmuteTime, String reason, String id) {
        player.title(TextUtils.setColors(getLanguage().getTitleWhenMuted()[0]), TextUtils.setColors(getLanguage().getTitleWhenMuted()[1].replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", unmuteTime)));
    }

    public static MuteContainerManager getMuteContainerManager() {
        return muteContainerManager;
    }
}
