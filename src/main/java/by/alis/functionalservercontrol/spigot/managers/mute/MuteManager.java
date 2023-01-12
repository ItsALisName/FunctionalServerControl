package by.alis.functionalservercontrol.spigot.managers.mute;

import by.alis.functionalservercontrol.api.enums.MuteType;
import by.alis.functionalservercontrol.api.enums.StatsType;
import by.alis.functionalservercontrol.api.events.AsyncMutePreprocessEvent;
import by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers;
import by.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import by.alis.functionalservercontrol.spigot.additional.misc.AdventureApiUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.MD5TextUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass;
import by.alis.functionalservercontrol.spigot.managers.IdsManager;
import by.alis.functionalservercontrol.spigot.managers.time.TimeManager;
import by.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getDate;
import static by.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getTime;
import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;
import static by.alis.functionalservercontrol.spigot.managers.mute.MuteChecker.isIpMuted;
import static by.alis.functionalservercontrol.spigot.managers.mute.MuteChecker.isPlayerMuted;

public class MuteManager {
    
    private static final MuteContainerManager muteContainerManager = new MuteContainerManager();

    public void preformMute(OfflinePlayer player, MuteType type, String reason, CommandSender initiator, long time, boolean announceMute) {

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
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                asyncMutePreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(reason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
            if(!getConfigSettings().isMuteAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-reason")));
                asyncMutePreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(initiator instanceof Player) {
            if (!initiator.hasPermission("functionalservercontrol.time-bypass")) {
                if (time > timeManager.getMaxPlayerMutePunishTime((Player) initiator)) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.mute-over-time").replace("%1$f", timeManager.convertFromMillis(timeManager.getMaxPlayerMutePunishTime((Player) initiator)))));
                    asyncMutePreprocessEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(getConfigSettings().isProhibitYourselfInteraction()) {
            if(initiator.getName().equalsIgnoreCase(player.getName())) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-yourself-actions")));
                asyncMutePreprocessEvent.setCancelled(true);
                return;
            }
        }

        reason = asyncMutePreprocessEvent.getReason().replace("'", "\"");
        String initiatorName = null;
        if(initiator instanceof Player) {
            initiatorName = ((Player)initiator).getName();
        } else if(initiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = initiator.getName();
        }

        if(StaticContainers.getCheckingCheatsPlayers().getCheckingPlayers().contains(player)) {
            if(getConfigSettings().isCheatCheckFunctionEnabled() && getConfigSettings().isPreventMuteDuringCheatCheck()) {
                asyncMutePreprocessEvent.setCancelled(true);
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.mute-player-on-check")));
                return;
            }
        }

        if(type == MuteType.PERMANENT_NOT_IP) {
            if (isPlayerMuted(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
                        getBaseManager().deleteFromMutedPlayers("-ip", getBaseManager().getIpByUUID(player.getUniqueId()));
                        getBaseManager().insertIntoMutedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                        getMuteContainerManager().removeFromMuteContainer("-u", String.valueOf(player.getUniqueId()));
                        getMuteContainerManager().addToMuteContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player.getName())));
                        if(announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        if(player.isOnline()) {
                            if(getConfigSettings().isSendTitleWhenMuted()) {
                                Player onlinePlayer = player.getPlayer();
                                sendTitleMessageWhenMuted(onlinePlayer, initiatorName, getGlobalVariables().getVariableNever(), reason, id);
                            }
                        }
                        return;
                    } else {
                        getBaseManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
                        getBaseManager().deleteFromMutedPlayers("-ip", getBaseManager().getIpByUUID(player.getUniqueId()));
                        getBaseManager().insertIntoMutedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player.getName())));
                        if(announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        if(player.isOnline()) {
                            if(getConfigSettings().isSendTitleWhenMuted()) {
                                Player onlinePlayer = player.getPlayer();
                                sendTitleMessageWhenMuted(onlinePlayer, initiatorName, getGlobalVariables().getVariableNever(), reason, id);
                            }
                        }
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {

                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoMutedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                    getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                    getMuteContainerManager().addToMuteContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
                    if(announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        if(getConfigSettings().isSendTitleWhenMuted()) {
                            Player onlinePlayer = player.getPlayer();
                            sendTitleMessageWhenMuted(onlinePlayer, initiatorName, getGlobalVariables().getVariableNever(), reason, id);
                        }
                    }
                } else {
                    getBaseManager().insertIntoMutedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                    getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                    getMuteContainerManager().addToMuteContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
                    if(announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        if(getConfigSettings().isSendTitleWhenMuted()) {
                            Player onlinePlayer = player.getPlayer();
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
                        getBaseManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
                        getBaseManager().deleteFromMutedPlayers("-ip", getBaseManager().getIpByUUID(player.getUniqueId()));
                        getBaseManager().insertIntoMutedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-u", String.valueOf(player.getUniqueId())); } catch (NullPointerException ingored) {}
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-ip", getBaseManager().getIpByUUID(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        getMuteContainerManager().addToMuteContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player.getName())));
                        if(announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason)));
                        }
                        if(player.isOnline()) {
                            if(getConfigSettings().isSendTitleWhenMuted()) {
                                Player onlinePlayer = player.getPlayer();
                                sendTitleMessageWhenMuted(onlinePlayer, initiatorName, getGlobalVariables().getVariableNever(), reason, id);
                            }
                        }
                    } else {
                        getBaseManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
                        getBaseManager().deleteFromMutedPlayers("-ip", getBaseManager().getIpByUUID(player.getUniqueId()));
                        getBaseManager().insertIntoMutedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player.getName())));
                        if(announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason)));
                        }
                        if(player.isOnline()) {
                            if(getConfigSettings().isSendTitleWhenMuted()) {
                                Player onlinePlayer = player.getPlayer();
                                sendTitleMessageWhenMuted(onlinePlayer, initiatorName, getGlobalVariables().getVariableNever(), reason, id);
                            }
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoMutedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                    getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                    getMuteContainerManager().addToMuteContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
                    if(announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        if(getConfigSettings().isSendTitleWhenMuted()) {
                            Player onlinePlayer = player.getPlayer();
                            assert onlinePlayer != null;
                            sendTitleMessageWhenMuted(onlinePlayer, initiatorName, getGlobalVariables().getVariableNever(), reason, id);
                        }
                    }
                } else {
                    getBaseManager().insertIntoMutedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                    getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                    if(announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        if(getConfigSettings().isSendTitleWhenMuted()) {
                            Player onlinePlayer = player.getPlayer();
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
                        getBaseManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
                        getBaseManager().deleteFromMutedPlayers("-ip", getBaseManager().getIpByUUID(player.getUniqueId()));
                        getBaseManager().insertIntoMutedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime());
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-u", String.valueOf(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-ip", getBaseManager().getIpByUUID(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        getMuteContainerManager().addToMuteContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), asyncMutePreprocessEvent.getMuteTime());
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player.getName())));
                        if(announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime()))).replace("%1$f", initiatorName).replace("%4$f", reason)));
                        }
                        if(player.isOnline()) {
                            if(getConfigSettings().isSendTitleWhenMuted()) {
                                Player onlinePlayer = player.getPlayer();
                                sendTitleMessageWhenMuted(onlinePlayer, initiatorName, timeManager.convertFromMillis(asyncMutePreprocessEvent.getMuteTime() - System.currentTimeMillis()), reason, id);
                            }
                        }
                        return;
                    } else {
                        getBaseManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
                        getBaseManager().deleteFromMutedPlayers("-ip", getBaseManager().getIpByUUID(player.getUniqueId()));
                        getBaseManager().insertIntoMutedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime());
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player.getName())));
                        if(announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime()))).replace("%1$f", initiatorName).replace("%4$f", reason)));
                        }
                        if(player.isOnline()) {
                            if(getConfigSettings().isSendTitleWhenMuted()) {
                                Player onlinePlayer = player.getPlayer();
                                sendTitleMessageWhenMuted(onlinePlayer, initiatorName, timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime())), reason, id);
                            }
                        }
                    }
                    return;
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {

                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoMutedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime());
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                    getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                    getMuteContainerManager().addToMuteContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), asyncMutePreprocessEvent.getMuteTime());
                    if(announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime()))).replace("%1$f", initiatorName).replace("%4$f", reason)));
                    }
                    if(player.isOnline()) {
                        if(getConfigSettings().isSendTitleWhenMuted()) {
                            Player onlinePlayer = player.getPlayer();
                            sendTitleMessageWhenMuted(onlinePlayer, initiatorName, timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime())), reason, id);
                        }
                    }
                } else {
                    getBaseManager().insertIntoMutedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime());
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                    getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                    if(announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime()))).replace("%1$f", initiatorName).replace("%4$f", reason)));
                    }
                    if(player.isOnline()) {
                        if(getConfigSettings().isSendTitleWhenMuted()) {
                            Player onlinePlayer = player.getPlayer();
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
                        getBaseManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
                        getBaseManager().deleteFromMutedPlayers("-ip", getBaseManager().getIpByUUID(player.getUniqueId()));
                        getBaseManager().insertIntoMutedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime());
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-u", String.valueOf(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-ip", getBaseManager().getIpByUUID(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        getMuteContainerManager().addToMuteContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), asyncMutePreprocessEvent.getMuteTime());
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player.getName())));
                        if(announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                        if(player.isOnline()) {
                            if(getConfigSettings().isSendTitleWhenMuted()) {
                                Player onlinePlayer = player.getPlayer();
                                sendTitleMessageWhenMuted(onlinePlayer, initiatorName, timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime())), reason, id);
                            }
                        }
                    } else {
                        getBaseManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
                        getBaseManager().deleteFromMutedPlayers("-ip", getBaseManager().getIpByUUID(player.getUniqueId()));
                        getBaseManager().insertIntoMutedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime());
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player.getName())));
                        if(announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                        if(player.isOnline()) {
                            if(getConfigSettings().isSendTitleWhenMuted()) {
                                Player onlinePlayer = player.getPlayer();
                                sendTitleMessageWhenMuted(onlinePlayer, initiatorName, timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime())), reason, id);
                            }
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoMutedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime());
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                    getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                    getMuteContainerManager().addToMuteContainer(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), asyncMutePreprocessEvent.getMuteTime());
                    if(announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                    if(player.isOnline()) {
                        if(getConfigSettings().isSendTitleWhenMuted()) {
                            Player onlinePlayer = player.getPlayer();
                            sendTitleMessageWhenMuted(onlinePlayer, initiatorName, timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime())), reason, id);
                        }
                    }
                } else {
                    getBaseManager().insertIntoMutedPlayers(id, getBaseManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime());
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                    getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                    if(announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                    if(player.isOnline()) {
                        if(getConfigSettings().isSendTitleWhenMuted()) {
                            Player onlinePlayer = player.getPlayer();
                            sendTitleMessageWhenMuted(onlinePlayer, initiatorName, timeManager.convertFromMillis(timeManager.getPunishTime(asyncMutePreprocessEvent.getMuteTime())), reason, id);
                        }
                    }
                }
            }
        }
    }

    public void preformMute(String player, MuteType type, String reason, CommandSender initiator, long time, boolean announceMute) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player);
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
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                mutePreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(reason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
            if(!getConfigSettings().isMuteAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-reason")));
                mutePreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(initiator instanceof Player) {
            if (!initiator.hasPermission("functionalservercontrol.time-bypass")) {
                if (time > timeManager.getMaxPlayerMutePunishTime((Player) initiator)) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.mute-over-time").replace("%1$f", timeManager.convertFromMillis(timeManager.getMaxPlayerMutePunishTime((Player) initiator)))));
                    mutePreprocessEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(getConfigSettings().isProhibitYourselfInteraction()) {
            if(initiator.getName().equalsIgnoreCase(player)) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-yourself-actions")));
                mutePreprocessEvent.setCancelled(true);
                return;
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

        if (type == MuteType.PERMANENT_NOT_IP) {
            if (isPlayerMuted(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromNullMutedPlayers("-n", player);
                        getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_MUTES);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                        }
                        getMuteContainerManager().removeFromMuteContainer("-n", player);
                        getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(offlinePlayer.getUniqueId()), time);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        getBaseManager().deleteFromNullMutedPlayers("-n", player);
                        getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_MUTES);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_MUTES);

                    if (offlinePlayer != null) {
                        getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                        getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                        
                    }
                    getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(offlinePlayer.getUniqueId()), time);
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_MUTES);

                    if (offlinePlayer != null) {
                        getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                        getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                        
                    }
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if (type == MuteType.PERMANENT_IP) {
            if (isPlayerMuted(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromNullMutedPlayers("-n", player);
                        getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_MUTES);

                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                            
                        }
                        getMuteContainerManager().removeFromMuteContainer("-n", player);
                        getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", mutePreprocessEvent.getMuteTime());
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        getBaseManager().deleteFromNullMutedPlayers("-n", player);
                        getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_MUTES);

                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                            
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_MUTES);

                    if (offlinePlayer != null) {
                        getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                        getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                        
                    }
                    getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", mutePreprocessEvent.getMuteTime());
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_MUTES);

                    if (offlinePlayer != null) {
                        getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                        getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                        
                    }
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if (type == MuteType.TIMED_IP) {
            if (isPlayerMuted(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromNullMutedPlayers("-n", player);
                        getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_MUTES);

                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                            
                        }
                        getMuteContainerManager().removeFromMuteContainer("-n", player);
                        getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(offlinePlayer.getUniqueId()), time);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        getBaseManager().deleteFromNullMutedPlayers("-n", player);
                        getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_MUTES);

                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                            
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_MUTES);

                    if (offlinePlayer != null) {
                        getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                        getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                        
                    }
                    getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(offlinePlayer.getUniqueId()), time);
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                } else {
                    getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_MUTES);

                    if (offlinePlayer != null) {
                        getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                        getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                        
                    }
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                }
            }
        }

        if (type == MuteType.TIMED_NOT_IP) {
            if (isPlayerMuted(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromNullMutedPlayers("-n", player);
                        getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_MUTES);

                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                            
                        }
                        getMuteContainerManager().removeFromMuteContainer("-n", player);
                        getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(offlinePlayer.getUniqueId()), time);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        getBaseManager().deleteFromNullMutedPlayers("-n", player);
                        getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_MUTES);

                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                            
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_MUTES);

                    if (offlinePlayer != null) {
                        getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                        getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                        
                    }
                    getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, String.valueOf(offlinePlayer.getUniqueId()), time);
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                } else {
                    getBaseManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, offlinePlayer.getUniqueId(), time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if (initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_MUTES);

                    if (offlinePlayer != null) {
                        getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                        getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                        
                    }
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
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
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                mutePreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(reason.equalsIgnoreCase(getGlobalVariables().getDefaultReason())) {
            if(!getConfigSettings().isMuteAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-reason")));
                mutePreprocessEvent.setCancelled(true);
                return;
            }
        }

        if(initiator instanceof Player) {
            if (!initiator.hasPermission("functionalservercontrol.time-bypass")) {
                if (time > timeManager.getMaxPlayerMutePunishTime((Player) initiator)) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.mute-over-time").replace("%1$f", timeManager.convertFromMillis(timeManager.getMaxPlayerMutePunishTime((Player) initiator)))));
                    mutePreprocessEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(initiator instanceof Player) {
            if(getConfigSettings().isProhibitYourselfInteraction()) {
                if(((Player) initiator).getPlayer().getAddress().getAddress().getHostAddress().equalsIgnoreCase(ip)) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-yourself-actions")));
                    mutePreprocessEvent.setCancelled(true);
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

        if (type == MuteType.PERMANENT_NOT_IP) {
            if (isIpMuted(ip)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromNullMutedPlayers("-ip", ip);
                        getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName, reason, type, realDate, realTime, time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                        if(!isNull) {
                            OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                                getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                                
                            }
                        }
                        getMuteContainerManager().removeFromMuteContainer("-ip", ip);
                        getMuteContainerManager().addToMuteContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                        if(isNull) {
                            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ip-mute-removed").replace("%1$f", ip)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.mute.ip-broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        getBaseManager().deleteFromNullMutedPlayers("-ip", ip);
                        getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                        if(!isNull) {
                            OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                                getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                                
                            }
                        }
                        if(isNull) {
                            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ip-mute-removed").replace("%1$f", ip)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.mute.ip-broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoNullMutedPlayersIP(id, ip,  initiatorName, reason, type, realDate, realTime, time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                    if(!isNull) {
                        OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                            
                        }
                    }
                    getMuteContainerManager().addToMuteContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                    if(isNull) {
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.mute.ip-broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    getBaseManager().insertIntoNullMutedPlayersIP(id, ip,  initiatorName, reason, type, realDate, realTime, time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                    if(!isNull) {
                        OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                            
                        }
                    }
                    if(isNull) {
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.mute.ip-broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if (type == MuteType.PERMANENT_IP) {
            if (isIpMuted(ip)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromNullMutedPlayers("-ip", ip);
                        getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                        if(!isNull) {
                            OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                                getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                                
                            }
                        }
                        getMuteContainerManager().removeFromMuteContainer("-ip", ip);
                        getMuteContainerManager().addToMuteContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", mutePreprocessEvent.getMuteTime());
                        if(isNull) {
                            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ip-mute-removed").replace("%1$f", ip)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        getBaseManager().deleteFromNullMutedPlayers("-ip", ip);
                        getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                        if(!isNull) {
                            OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                                getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                                
                            }
                        }
                        if(isNull) {
                            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ip-mute-removed").replace("%1$f", ip)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                    if(!isNull) {
                        OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                            
                        }
                    }
                    getMuteContainerManager().addToMuteContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", mutePreprocessEvent.getMuteTime());
                    if(isNull) {
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason).replace("%4$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                    if(!isNull) {
                        OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                            
                        }
                    }
                    if(isNull) {
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", ip).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if (type == MuteType.TIMED_IP) {
            if (isIpMuted(ip)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromNullMutedPlayers("-ip", ip);
                        getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                        if(!isNull) {
                            OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                                getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                                
                            }
                        }
                        getMuteContainerManager().removeFromMuteContainer("-ip", ip);
                        getMuteContainerManager().addToMuteContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                        if(isNull) {
                            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ip-mute-removed").replace("%1$f", ip)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        getBaseManager().deleteFromNullMutedPlayers("-ip", ip);
                        getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                        if(!isNull) {
                            OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                                getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                                
                            }
                        }
                        if(isNull) {
                            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ip-mute-removed").replace("%1$f", ip)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                    if(!isNull) {
                        OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                            
                        }
                    }
                    getMuteContainerManager().addToMuteContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                    if(isNull) {
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                } else {
                    getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                    if(!isNull) {
                        OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                            
                        }
                    }
                    if(isNull) {
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                }
            }
        }

        if (type == MuteType.TIMED_NOT_IP) {
            if (isIpMuted(ip)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        getBaseManager().deleteFromNullMutedPlayers("-ip", ip);
                        getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                        if(!isNull) {
                            OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                                getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                                
                            }
                        }
                        getMuteContainerManager().removeFromMuteContainer("-ip", ip);
                        getMuteContainerManager().addToMuteContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                        if(isNull) {
                            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ip-mute-removed").replace("%1$f", ip)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmute.ip-broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        getBaseManager().deleteFromNullMutedPlayers("-ip", ip);
                        getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                        if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                        if(!isNull) {
                            OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                            if (offlinePlayer != null) {
                                getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                                getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                                
                            }
                        }
                        if(isNull) {
                            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                        }
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-ip-mute-removed").replace("%1$f", ip)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmute.ip-broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.already-muted")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                    if(!isNull) {
                        OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                            
                        }
                    }
                    getMuteContainerManager().addToMuteContainer(id, ip, "NULL_PLAYER", initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                    if(isNull) {
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmute.ip-broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                } else {
                    getBaseManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                    getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason).replace("%5$f", getDate() + ", " + getTime()));
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_MUTES);
                    if(!isNull) {
                        OfflinePlayer offlinePlayer = OtherUtils.getPlayerByIP(ip);
                        if (offlinePlayer != null) {
                            getBaseManager().insertIntoPlayersPunishInfo(offlinePlayer.getUniqueId());
                            getBaseManager().updatePlayerStatsInfo(offlinePlayer, StatsType.Player.STATS_MUTES);
                            
                        }
                    }
                    if(isNull) {
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-ip").replace("%1$f", ip)));
                    }
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmute.ip-broadcast-message").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                }
            }
        }
    }


    public void checkForNullMutedPlayer(Player player) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {

            if(getMutedPlayersContainer().getNameContainer().contains(player.getName())
                    && getMutedPlayersContainer().getIpContainer().get(getMutedPlayersContainer().getNameContainer().indexOf(player.getName())).equalsIgnoreCase("NULL_PLAYER")) {
                int indexOf = getMutedPlayersContainer().getNameContainer().indexOf(player.getName());
                String id = getMutedPlayersContainer().getIdsContainer().get(indexOf);
                String ip = player.getAddress().getAddress().getHostAddress();
                String name = player.getName();
                String initiatorName = getMutedPlayersContainer().getInitiatorNameContainer().get(indexOf);
                String reason = getMutedPlayersContainer().getReasonContainer().get(indexOf);
                MuteType muteType = getMutedPlayersContainer().getMuteTypesContainer().get(indexOf);
                String muteDate = getMutedPlayersContainer().getRealMuteDateContainer().get(indexOf);
                String muteTime = getMutedPlayersContainer().getRealMuteTimeContainer().get(indexOf);
                String uuid = String.valueOf(player.getUniqueId());
                long unmuteTime = getMutedPlayersContainer().getMuteTimeContainer().get(indexOf);
                getMuteContainerManager().removeFromMuteContainer("-n", name);

                getBaseManager().deleteFromNullMutedPlayers("-id", id);
                getBaseManager().insertIntoMutedPlayers(id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, UUID.fromString(uuid), unmuteTime);
                getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                

                getMuteContainerManager().addToMuteContainer(id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, uuid, unmuteTime);
                return;
            }

            if(getMutedPlayersContainer().getIpContainer().contains(player.getAddress().getAddress().getHostAddress())
                    && getMutedPlayersContainer().getUUIDContainer().get(getMutedPlayersContainer().getIpContainer().indexOf(player.getAddress().getAddress().getHostAddress())).equalsIgnoreCase("NULL_PLAYER")) {

                int indexOf = getMutedPlayersContainer().getIpContainer().indexOf(player.getAddress().getAddress().getHostAddress());
                String id = getMutedPlayersContainer().getIdsContainer().get(indexOf);
                String ip = player.getAddress().getAddress().getHostAddress();
                String name = player.getName();
                String initiatorName = getMutedPlayersContainer().getInitiatorNameContainer().get(indexOf);
                String reason = getMutedPlayersContainer().getReasonContainer().get(indexOf);
                MuteType muteType = getMutedPlayersContainer().getMuteTypesContainer().get(indexOf);
                String muteDate = getMutedPlayersContainer().getRealMuteDateContainer().get(indexOf);
                String muteTime = getMutedPlayersContainer().getRealMuteTimeContainer().get(indexOf);
                String uuid = String.valueOf(player.getUniqueId());
                long unmuteTime = getMutedPlayersContainer().getMuteTimeContainer().get(indexOf);
                getMuteContainerManager().removeFromMuteContainer("-ip", ip);

                getBaseManager().deleteFromNullMutedPlayers("-id", id);
                getBaseManager().insertIntoMutedPlayers(id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, UUID.fromString(uuid), unmuteTime);
                getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                

                getMuteContainerManager().addToMuteContainer(id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, uuid, unmuteTime);
                return;

            }

        } else {
            if(getBaseManager().getMutedPlayersNames().contains(player.getName())
                    && getBaseManager().getMutedIps().get(getBaseManager().getMutedPlayersNames().indexOf(player.getName())).equalsIgnoreCase("NULL_PLAYER")) {
                int indexOf = getBaseManager().getMutedPlayersNames().indexOf(player.getName());
                String id = getBaseManager().getMutedIds().get(indexOf);
                String ip = player.getAddress().getAddress().getHostAddress();
                String name = player.getName();
                String initiatorName = getBaseManager().getMuteInitiators().get(indexOf);
                String reason = getBaseManager().getMuteReasons().get(indexOf);
                MuteType muteType = getBaseManager().getMuteTypes().get(indexOf);
                String muteDate = getBaseManager().getMuteDates().get(indexOf);
                String muteTime = getBaseManager().getMuteTimes().get(indexOf);
                String uuid = String.valueOf(player.getUniqueId());
                long unmuteTime = getBaseManager().getUnmuteTimes().get(indexOf);

                getBaseManager().deleteFromNullMutedPlayers("-id", id);
                getBaseManager().insertIntoMutedPlayers(id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, UUID.fromString(uuid), unmuteTime);
                getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);

                return;
            }

            if(getBaseManager().getMutedIps().contains(player.getAddress().getAddress().getHostAddress())
                    && getBaseManager().getMutedUUIDs().get(getBaseManager().getMutedIps().indexOf(player.getAddress().getAddress().getHostAddress())).equalsIgnoreCase("NULL_PLAYER")) {

                int indexOf = getBaseManager().getMutedIps().indexOf(player.getAddress().getAddress().getHostAddress());
                String id = getBaseManager().getMutedIds().get(indexOf);
                String ip = player.getAddress().getAddress().getHostAddress();
                String name = player.getName();
                String initiatorName = getBaseManager().getMuteInitiators().get(indexOf);
                String reason = getBaseManager().getMuteReasons().get(indexOf);
                MuteType muteType = getBaseManager().getMuteTypes().get(indexOf);
                String muteDate = getBaseManager().getMuteDates().get(indexOf);
                String muteTime = getBaseManager().getMuteTimes().get(indexOf);
                String uuid = String.valueOf(player.getUniqueId());
                long unmuteTime = getBaseManager().getUnmuteTimes().get(indexOf);
                getBaseManager().deleteFromNullMutedPlayers("-id", id);
                getBaseManager().insertIntoMutedPlayers(id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, UUID.fromString(uuid), unmuteTime);
                getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                return;
            }
        }
    }



    public void notifyAboutMuteOnJoin(Player player) {
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
                if(getConfigSettings().isServerSupportsHoverEvents()) {
                    if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                        player.spigot().sendMessage(MD5TextUtils.createHoverText(getFileAccessor().getLang().getString("other.join.muted.text").replace("%1$f", setColors(translatedUnmuteTime)),
                                getFileAccessor().getLang().getString("other.join.muted.hover-text")
                                        .replace("%1$f", getMutedPlayersContainer().getInitiatorNameContainer().get(indexOf))
                                        .replace("%2$f", getMutedPlayersContainer().getRealMuteDateContainer().get(indexOf))
                                        .replace("%3$f", getMutedPlayersContainer().getRealMuteTimeContainer().get(indexOf))
                                        .replace("%4$f", getMutedPlayersContainer().getReasonContainer().get(indexOf))
                                        .replace("%5$f", getMutedPlayersContainer().getIdsContainer().get(indexOf))));
                    } else {
                        player.sendMessage(AdventureApiUtils.createHoverText(setColors(getFileAccessor().getLang().getString("other.join.muted.text").replace("%1$f", setColors(translatedUnmuteTime))), setColors(getFileAccessor().getLang().getString("other.join.muted.hover-text")
                                .replace("%1$f", getMutedPlayersContainer().getInitiatorNameContainer().get(indexOf))
                                .replace("%2$f", getMutedPlayersContainer().getRealMuteDateContainer().get(indexOf))
                                .replace("%3$f", getMutedPlayersContainer().getRealMuteTimeContainer().get(indexOf))
                                .replace("%4$f", getMutedPlayersContainer().getReasonContainer().get(indexOf))
                                .replace("%5$f", getMutedPlayersContainer().getIdsContainer().get(indexOf)))));
                    }
                } else {
                    player.sendMessage(setColors(getFileAccessor().getLang().getString("other.join.muted.text").replace("%1$f", setColors(translatedUnmuteTime))));
                }
            }
        } else {
            if(getBaseManager().getMutedUUIDs().contains(String.valueOf(player.getUniqueId()))) {
                int indexOf = getBaseManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId()));
                MuteType muteType = getBaseManager().getMuteTypes().get(indexOf);
                String translatedUnmuteTime = getGlobalVariables().getVariableNever();
                if(muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                    if(System.currentTimeMillis() >= getBaseManager().getUnmuteTimes().get(indexOf)) return;
                    TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                    translatedUnmuteTime = timeSettingsAccessor.getTimeManager().convertFromMillis(getBaseManager().getUnmuteTimes().get(indexOf) - System.currentTimeMillis());
                }
                if(getConfigSettings().isServerSupportsHoverEvents()) {
                    if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                        player.spigot().sendMessage(MD5TextUtils.createHoverText(
                                getFileAccessor().getLang().getString("other.join.muted.text").replace("%1$f", setColors(translatedUnmuteTime)),
                                getFileAccessor().getLang().getString("other.join.muted.hover-text")
                                        .replace("%1$f", getBaseManager().getMuteInitiators().get(indexOf))
                                        .replace("%2$f", getBaseManager().getMuteDates().get(indexOf))
                                        .replace("%3$f", getBaseManager().getMuteTimes().get(indexOf))
                                        .replace("%4$f", getBaseManager().getMuteReasons().get(indexOf))
                                        .replace("%5$f", getBaseManager().getMutedIds().get(indexOf))
                        ));
                    } else {
                        player.sendMessage(AdventureApiUtils.createHoverText(setColors(getFileAccessor().getLang().getString("other.join.muted.text").replace("%1$f", setColors(translatedUnmuteTime))), setColors(getFileAccessor().getLang().getString("other.join.muted.hover-text")
                                .replace("%1$f", getBaseManager().getMuteInitiators().get(indexOf))
                                .replace("%2$f", getBaseManager().getMuteDates().get(indexOf))
                                .replace("%3$f", getBaseManager().getMuteTimes().get(indexOf))
                                .replace("%4$f", getBaseManager().getMuteReasons().get(indexOf))
                                .replace("%5$f", getBaseManager().getMutedIds().get(indexOf)))));
                    }
                } else {
                    player.sendMessage(setColors(getFileAccessor().getLang().getString("other.join.muted.text").replace("%1$f", setColors(translatedUnmuteTime))));
                }
            }
        }
    }

    public void notifyAboutMuteOnCommand(Player player) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            int indexOf = getMutedPlayersContainer().getUUIDContainer().indexOf(String.valueOf(player.getUniqueId()));
            MuteType muteType = getMutedPlayersContainer().getMuteTypesContainer().get(indexOf);
            String translatedUnmuteTime = getGlobalVariables().getVariableNever();
            if(muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                translatedUnmuteTime = timeSettingsAccessor.getTimeManager().convertFromMillis(getMutedPlayersContainer().getMuteTimeContainer().get(indexOf) - System.currentTimeMillis());
            }
            if(getConfigSettings().isServerSupportsHoverEvents()) {
                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                    player.spigot().sendMessage(MD5TextUtils.createHoverText(
                            getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.text").replace("%1$f", setColors(translatedUnmuteTime)),
                            getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.hover-text")
                                    .replace("%1$f", getMutedPlayersContainer().getInitiatorNameContainer().get(indexOf))
                                    .replace("%2$f", getMutedPlayersContainer().getRealMuteDateContainer().get(indexOf))
                                    .replace("%3$f", getMutedPlayersContainer().getRealMuteTimeContainer().get(indexOf))
                                    .replace("%4$f", getMutedPlayersContainer().getReasonContainer().get(indexOf))
                                    .replace("%5$f", getMutedPlayersContainer().getIdsContainer().get(indexOf))
                    ));
                } else {
                    player.sendMessage(AdventureApiUtils.createHoverText(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.text").replace("%1$f", setColors(translatedUnmuteTime))), setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.hover-text")
                            .replace("%1$f", getMutedPlayersContainer().getInitiatorNameContainer().get(indexOf))
                            .replace("%2$f", getMutedPlayersContainer().getRealMuteDateContainer().get(indexOf))
                            .replace("%3$f", getMutedPlayersContainer().getRealMuteTimeContainer().get(indexOf))
                            .replace("%4$f", getMutedPlayersContainer().getReasonContainer().get(indexOf))
                            .replace("%5$f", getMutedPlayersContainer().getIdsContainer().get(indexOf)))));
                }
            } else {
                player.sendMessage(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.text").replace("%1$f", setColors(translatedUnmuteTime))));
            }
        } else {
            int indexOf = getBaseManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId()));
            MuteType muteType = getBaseManager().getMuteTypes().get(indexOf);
            String translatedUnmuteTime = getGlobalVariables().getVariableNever();
            if(muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                translatedUnmuteTime = timeSettingsAccessor.getTimeManager().convertFromMillis(getBaseManager().getUnmuteTimes().get(indexOf) - System.currentTimeMillis());
            }
            if(getConfigSettings().isServerSupportsHoverEvents()) {
                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                    player.spigot().sendMessage(MD5TextUtils.createHoverText(
                            getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.text").replace("%1$f", setColors(translatedUnmuteTime)),
                            getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.hover-text")
                                    .replace("%1$f", getBaseManager().getMuteInitiators().get(indexOf))
                                    .replace("%2$f", getBaseManager().getMuteDates().get(indexOf))
                                    .replace("%3$f", getBaseManager().getMuteTimes().get(indexOf))
                                    .replace("%4$f", getBaseManager().getMuteReasons().get(indexOf))
                                    .replace("%5$f", getBaseManager().getMutedIds().get(indexOf))
                    ));
                } else {
                    player.sendMessage(AdventureApiUtils.createHoverText(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.text").replace("%1$f", setColors(translatedUnmuteTime))), setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.hover-text")
                            .replace("%1$f", getBaseManager().getMuteInitiators().get(indexOf))
                            .replace("%2$f", getBaseManager().getMuteDates().get(indexOf))
                            .replace("%3$f", getBaseManager().getMuteTimes().get(indexOf))
                            .replace("%4$f", getBaseManager().getMuteReasons().get(indexOf))
                            .replace("%5$f", getBaseManager().getMutedIds().get(indexOf)))));
                }
            } else {
                player.sendMessage(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.text").replace("%1$f", setColors(translatedUnmuteTime))));
            }
        }
    }

    public void notifyAboutMuteOnChat(Player player) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            int indexOf = getMutedPlayersContainer().getUUIDContainer().indexOf(String.valueOf(player.getUniqueId()));
            MuteType muteType = getMutedPlayersContainer().getMuteTypesContainer().get(indexOf);
            String translatedUnmuteTime = getGlobalVariables().getVariableNever();
            if(muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                translatedUnmuteTime = timeSettingsAccessor.getTimeManager().convertFromMillis(getMutedPlayersContainer().getMuteTimeContainer().get(indexOf) - System.currentTimeMillis());
            }
            if(getConfigSettings().isServerSupportsHoverEvents()) {
                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")){
                    player.spigot().sendMessage(MD5TextUtils.createHoverText(
                            getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.text").replace("%1$f", setColors(translatedUnmuteTime)),
                            getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.hover-text")
                                    .replace("%1$f", getMutedPlayersContainer().getInitiatorNameContainer().get(indexOf))
                                    .replace("%2$f", getMutedPlayersContainer().getRealMuteDateContainer().get(indexOf))
                                    .replace("%3$f", getMutedPlayersContainer().getRealMuteTimeContainer().get(indexOf))
                                    .replace("%4$f", getMutedPlayersContainer().getReasonContainer().get(indexOf))
                                    .replace("%5$f", getMutedPlayersContainer().getIdsContainer().get(indexOf))
                    ));
                } else {
                    player.sendMessage(AdventureApiUtils.createHoverText(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.text").replace("%1$f", setColors(translatedUnmuteTime))), setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.hover-text")
                            .replace("%1$f", getMutedPlayersContainer().getInitiatorNameContainer().get(indexOf))
                            .replace("%2$f", getMutedPlayersContainer().getRealMuteDateContainer().get(indexOf))
                            .replace("%3$f", getMutedPlayersContainer().getRealMuteTimeContainer().get(indexOf))
                            .replace("%4$f", getMutedPlayersContainer().getReasonContainer().get(indexOf))
                            .replace("%5$f", getMutedPlayersContainer().getIdsContainer().get(indexOf)))));
                }
            } else {
                player.sendMessage(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.text").replace("%1$f", setColors(translatedUnmuteTime))));
            }
        } else {
            int indexOf = getBaseManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId()));
            MuteType muteType = getBaseManager().getMuteTypes().get(indexOf);
            String translatedUnmuteTime = getGlobalVariables().getVariableNever();
            if(muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                translatedUnmuteTime = timeSettingsAccessor.getTimeManager().convertFromMillis(getBaseManager().getUnmuteTimes().get(indexOf) - System.currentTimeMillis());
            }
            if(getConfigSettings().isServerSupportsHoverEvents()) {
                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                    player.spigot().sendMessage(MD5TextUtils.createHoverText(
                            getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.text").replace("%1$f", setColors(translatedUnmuteTime)),
                            getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.hover-text")
                                    .replace("%1$f", getBaseManager().getMuteInitiators().get(indexOf))
                                    .replace("%2$f", getBaseManager().getMuteDates().get(indexOf))
                                    .replace("%3$f", getBaseManager().getMuteTimes().get(indexOf))
                                    .replace("%4$f", getBaseManager().getMuteReasons().get(indexOf))
                                    .replace("%5$f", getBaseManager().getMutedIds().get(indexOf))
                    ));
                } else {
                    player.sendMessage(AdventureApiUtils.createHoverText(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.text").replace("%1$f", setColors(translatedUnmuteTime))), setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.hover-text")
                            .replace("%1$f", getBaseManager().getMuteInitiators().get(indexOf))
                            .replace("%2$f", getBaseManager().getMuteDates().get(indexOf))
                            .replace("%3$f", getBaseManager().getMuteTimes().get(indexOf))
                            .replace("%4$f", getBaseManager().getMuteReasons().get(indexOf))
                            .replace("%5$f", getBaseManager().getMutedIds().get(indexOf)))));
                }
            } else {
                player.sendMessage(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.text").replace("%1$f", setColors(translatedUnmuteTime))));
            }
        }
    }

    public void sendTitleMessageWhenMuted(Player player, String initiatorName, String unmuteTime, String reason, String id) {
        CoreAdapter.getAdapter().sendTitle(player, setColors(getLanguage().getTitleWhenMuted()[0]), setColors(getLanguage().getTitleWhenMuted()[1].replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", unmuteTime)));
    }

    public static MuteContainerManager getMuteContainerManager() {
        return muteContainerManager;
    }
}
