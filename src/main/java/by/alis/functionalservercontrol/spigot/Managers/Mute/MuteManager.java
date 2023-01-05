package by.alis.functionalservercontrol.spigot.Managers.Mute;

import by.alis.functionalservercontrol.API.Enums.MuteType;
import by.alis.functionalservercontrol.API.Spigot.Events.AsyncMutePreprocessEvent;
import by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers;
import by.alis.functionalservercontrol.spigot.Additional.CoreAdapters.CoreAdapter;
import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.AdventureApiUtils;
import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.MD5TextUtils;
import by.alis.functionalservercontrol.spigot.Additional.WorldDate.WorldTimeAndDateClass;
import by.alis.functionalservercontrol.spigot.Managers.CooldownsManager;
import by.alis.functionalservercontrol.spigot.Managers.IdsManager;
import by.alis.functionalservercontrol.spigot.Managers.TimeManagers.TimeManager;
import by.alis.functionalservercontrol.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.*;
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;
import static by.alis.functionalservercontrol.spigot.Managers.Mute.MuteChecker.isIpMuted;
import static by.alis.functionalservercontrol.spigot.Managers.Mute.MuteChecker.isPlayerMuted;

public class MuteManager {
    
    private static final MuteContainerManager muteContainerManager = new MuteContainerManager();

    public void preformMute(OfflinePlayer player, MuteType type, String reason, CommandSender initiator, long time, boolean announceMute, String command) {

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
        AsyncMutePreprocessEvent asyncMutePreprocessEvent = new AsyncMutePreprocessEvent(id, player, initiator, type, time, reason, realTime, realDate, getConfigSettings().isApiEnabled(), convertedTime);
        if(getConfigSettings().isApiEnabled()) {
            if(!getConfigSettings().isApiProtectedByPassword()) {
                Bukkit.getPluginManager().callEvent(asyncMutePreprocessEvent);
            } else {
                if(asyncMutePreprocessEvent.getApiPassword() != null && asyncMutePreprocessEvent.getApiPassword().equalsIgnoreCase(getFileAccessor().getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(asyncMutePreprocessEvent);
                }
            }
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

        if(initiator instanceof Player) {
            if(CooldownsManager.playerHasCooldown(((Player) initiator).getPlayer(), command)) {
                CooldownsManager.notifyAboutCooldown(((Player) initiator).getPlayer(), command);
                asyncMutePreprocessEvent.setCancelled(true);
                return;
            } else {
                CooldownsManager.setCooldown(((Player) initiator).getPlayer(), command);
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
            if(getConfigSettings().isCheatCheckFunctionEnabled() && getConfigSettings().isPreventMuteDuringCheck()) {
                asyncMutePreprocessEvent.setCancelled(true);
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.mute-player-on-check")));
                return;
            }
        }

        if(type == MuteType.PERMANENT_NOT_IP) {
            if (isPlayerMuted(player)) {
                if (initiator.hasPermission("functionalservercontrol.use.re-mute")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                try {
                                    getSQLiteManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ingored) {}
                                try {
                                    getSQLiteManager().deleteFromMutedPlayers("-ip", getSQLiteManager().getIpByUUID(player.getUniqueId()));} catch (NullPointerException ingored) {}
                                getSQLiteManager().insertIntoMutedPlayers(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason));
                                break;
                            }
                            case H2: { }
                        }
                        getMuteContainerManager().removeFromMuteContainer("-u", String.valueOf(player.getUniqueId()));
                        getMuteContainerManager().addToMuteContainer(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                try {
                                    getSQLiteManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ingored) {}
                                try {
                                    getSQLiteManager().deleteFromMutedPlayers("-ip", getSQLiteManager().getIpByUUID(player.getUniqueId()));} catch (NullPointerException ingored) {}
                                getSQLiteManager().insertIntoMutedPlayers(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason));
                                break;
                            }
                            case H2: {}
                        }
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoMutedPlayers(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason));
                            break;
                        }
                        case H2: {}
                    }
                    getMuteContainerManager().addToMuteContainer(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoMutedPlayers(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason));
                            break;
                        }
                        case H2: { }
                    }
                    getMuteContainerManager().addToMuteContainer(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                try {
                                    getSQLiteManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromMutedPlayers("-ip", getSQLiteManager().getIpByUUID(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                getSQLiteManager().insertIntoMutedPlayers(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason));
                                break;
                            }
                            case H2: {}
                        }
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-u", String.valueOf(player.getUniqueId())); } catch (NullPointerException ingored) {}
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-ip", getSQLiteManager().getIpByUUID(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        getMuteContainerManager().addToMuteContainer(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                try {
                                    getSQLiteManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromMutedPlayers("-ip", getSQLiteManager().getIpByUUID(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                getSQLiteManager().insertIntoMutedPlayers(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason));
                                break;
                            }
                            case H2: {}
                        }
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoMutedPlayers(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason));
                            break;
                        }
                        case H2: {}
                    }
                    getMuteContainerManager().addToMuteContainer(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoMutedPlayers(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason));
                            break;
                        }
                        case H2: {}
                    }
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                try {
                                    getSQLiteManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromMutedPlayers("-ip", getSQLiteManager().getIpByUUID(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                getSQLiteManager().insertIntoMutedPlayers(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime());
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason));
                                break;
                            }
                            case H2: { }
                        }
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-u", String.valueOf(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-ip", getSQLiteManager().getIpByUUID(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        getMuteContainerManager().addToMuteContainer(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), asyncMutePreprocessEvent.getMuteTime());
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                try {
                                    getSQLiteManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromMutedPlayers("-ip", getSQLiteManager().getIpByUUID(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                getSQLiteManager().insertIntoMutedPlayers(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime());
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason));
                                break;
                            }
                            case H2: {}
                        }
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoMutedPlayers(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime());
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason));
                            break;
                        }
                        case H2: {
                            break;
                        }
                    }
                    getMuteContainerManager().addToMuteContainer(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), asyncMutePreprocessEvent.getMuteTime());
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoMutedPlayers(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime());
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason));
                            break;
                        }
                        case H2: {
                            break;
                        }
                    }
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                try {
                                    getSQLiteManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromMutedPlayers("-ip", getSQLiteManager().getIpByUUID(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                getSQLiteManager().insertIntoMutedPlayers(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime());
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason));
                                break;
                            }
                            case H2: {
                                break;
                            }
                        }
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-u", String.valueOf(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        try {
                            getMuteContainerManager().removeFromMuteContainer("-ip", getSQLiteManager().getIpByUUID(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        getMuteContainerManager().addToMuteContainer(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), asyncMutePreprocessEvent.getMuteTime());
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                try {
                                    getSQLiteManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromMutedPlayers("-ip", getSQLiteManager().getIpByUUID(player.getUniqueId()));} catch (NullPointerException ingored) {}
                                getSQLiteManager().insertIntoMutedPlayers(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime());
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason));
                                break;
                            }
                            case H2: {
                                break;
                            }
                        }
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoMutedPlayers(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime());
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason));
                            break;
                        }
                        case H2: {
                            break;
                        }
                    }
                    getMuteContainerManager().addToMuteContainer(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), asyncMutePreprocessEvent.getMuteTime());
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoMutedPlayers(id, getSQLiteManager().getIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), asyncMutePreprocessEvent.getMuteTime());
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", convertedTime).replace("%4$f", reason));
                            break;
                        }
                        case H2: {
                            break;
                        }
                    }
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

    public void preformMute(String player, MuteType type, String reason, CommandSender initiator, long time, boolean announceMute, String command) {

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
        AsyncMutePreprocessEvent mutePreprocessEvent = new AsyncMutePreprocessEvent(id, player, initiator, type, time, reason, realTime, realDate, getConfigSettings().isApiEnabled(), convertedTime);
        reason = mutePreprocessEvent.getReason().replace("'", "\"");
        time = mutePreprocessEvent.getMuteTime();
        if (type == MuteType.PERMANENT_IP || type == MuteType.PERMANENT_NOT_IP) time = -1;
        if (getConfigSettings().isApiEnabled()) {
            if (!getConfigSettings().isApiProtectedByPassword()) {
                Bukkit.getPluginManager().callEvent(mutePreprocessEvent);
            } else {
                if (mutePreprocessEvent.getApiPassword() != null && mutePreprocessEvent.getApiPassword().equalsIgnoreCase(getFileAccessor().getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(mutePreprocessEvent);
                }
            }
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
            if(CooldownsManager.playerHasCooldown(((Player) initiator).getPlayer(), command)) {
                CooldownsManager.notifyAboutCooldown(((Player) initiator).getPlayer(), command);
                mutePreprocessEvent.setCancelled(true);
                return;
            } else {
                CooldownsManager.setCooldown(((Player) initiator).getPlayer(), command);
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullMutedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason));
                                break;
                            }
                            case H2: {
                                break;
                            }
                        }
                        getMuteContainerManager().removeFromMuteContainer("-n", player);
                        getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullMutedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason));
                                break;
                            }
                            case H2: {
                                break;
                            }
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason));
                            break;
                        }
                        case H2: {
                            break;
                        }
                    }
                    getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.mute.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.mute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason));
                            break;
                        }
                        case H2: {
                            break;
                        }
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullMutedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason));
                                break;
                            }
                            case H2: {
                                break;
                            }
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullMutedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason));
                                break;
                            }
                            case H2: {
                                break;
                            }
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason));
                            break;
                        }
                        case H2: {
                            break;
                        }
                    }
                    getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", mutePreprocessEvent.getMuteTime());
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.muteip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", reason));
                            break;
                        }
                        case H2: {
                            break;
                        }
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullMutedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason));
                                break;
                            }
                            case H2: {
                                break;
                            }
                        }
                        getMuteContainerManager().removeFromMuteContainer("-n", player);
                        getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullMutedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason));
                                break;
                            }
                            case H2: {
                                break;
                            }
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason));
                            break;
                        }
                        case H2: {
                            break;
                        }
                    }
                    getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason));
                            break;
                        }
                        case H2: {
                            break;
                        }
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullMutedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason));
                                break;
                            }
                            case H2: {
                                break;
                            }
                        }
                        getMuteContainerManager().removeFromMuteContainer("-n", player);
                        getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.last-mute-removed").replace("%1$f", player)));
                        if (announceMute) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullMutedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason));
                                break;
                            }
                            case H2: {
                                break;
                            }
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason));
                            break;
                        }
                        case H2: {
                            break;
                        }
                    }
                    getMuteContainerManager().addToMuteContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullMutedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", convertedTime).replace("%4$f", reason));
                            break;
                        }
                        case H2: {
                            break;
                        }
                    }
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if (announceMute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.tempmute.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", timeManager.convertFromMillis(timeManager.getPunishTime(mutePreprocessEvent.getMuteTime()))).replace("%4$f", reason)));
                    }
                }
            }
        }
    }

    public void preformMuteByIp(String ip, MuteType type, String reason, CommandSender initiator, long time, boolean announceMute, String command, boolean isNull) {

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
        AsyncMutePreprocessEvent mutePreprocessEvent = new AsyncMutePreprocessEvent(id, ip, initiator, type, time, reason, realTime, realDate, getConfigSettings().isApiEnabled(), convertedTime);
        reason = mutePreprocessEvent.getReason().replace("'", "\"");
        time = mutePreprocessEvent.getMuteTime();
        if (type == MuteType.PERMANENT_IP || type == MuteType.PERMANENT_NOT_IP) time = -1;
        if (getConfigSettings().isApiEnabled()) {
            if (!getConfigSettings().isApiProtectedByPassword()) {
                Bukkit.getPluginManager().callEvent(mutePreprocessEvent);
            } else {
                if (mutePreprocessEvent.getApiPassword() != null && mutePreprocessEvent.getApiPassword().equalsIgnoreCase(getFileAccessor().getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(mutePreprocessEvent);
                }
            }
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
            if(CooldownsManager.playerHasCooldown(((Player) initiator).getPlayer(), command)) {
                CooldownsManager.notifyAboutCooldown(((Player) initiator).getPlayer(), command);
                mutePreprocessEvent.setCancelled(true);
                return;
            } else {
                CooldownsManager.setCooldown(((Player) initiator).getPlayer(), command);
            }
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullMutedPlayers("-ip", ip);
                                getSQLiteManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName, reason, type, realDate, realTime, time);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason));
                                break;
                            }
                            case H2: {
                                break;
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullMutedPlayers("-ip", ip);
                                getSQLiteManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason));
                                break;
                            }
                            case H2: {
                                break;
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullMutedPlayersIP(id, ip,  initiatorName, reason, type, realDate, realTime, time);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason));
                            break;
                        }
                        case H2: {
                            break;
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullMutedPlayersIP(id, ip,  initiatorName, reason, type, realDate, realTime, time);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason));
                            break;
                        }
                        case H2: {
                            break;
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullMutedPlayers("-ip", ip);
                                getSQLiteManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason));
                                break;
                            }
                            case H2: {
                                break;
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullMutedPlayers("-ip", ip);
                                getSQLiteManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason));
                                break;
                            }
                            case H2: {
                                break;
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason));
                            break;
                        }
                        case H2: {
                            break;
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.muteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", reason));
                            break;
                        }
                        case H2: {
                            break;
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullMutedPlayers("-ip", ip);
                                getSQLiteManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason));
                                break;
                            }
                            case H2: {
                                break;
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullMutedPlayers("-ip", ip);
                                getSQLiteManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason));
                                break;
                            }
                            case H2: {
                                break;
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason));
                            break;
                        }
                        case H2: {
                            break;
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason));
                            break;
                        }
                        case H2: {
                            break;
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullMutedPlayers("-ip", ip);
                                getSQLiteManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason));
                                break;
                            }
                            case H2: {
                                break;
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullMutedPlayers("-ip", ip);
                                getSQLiteManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                                getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason));
                                break;
                            }
                            case H2: {
                                break;
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason));
                            break;
                        }
                        case H2: {
                            break;
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName,  reason, type, realDate, realTime, time);
                            getSQLiteManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.tempmuteip").replace("%1$f", initiatorName).replace("%2$f", ip).replace("%3$f", convertedTime).replace("%4$f", reason));
                            break;
                        }
                        case H2: {
                            break;
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

                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        getSQLiteManager().deleteFromNullMutedPlayers("-id", id);
                        getSQLiteManager().insertIntoMutedPlayers(id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, UUID.fromString(uuid), unmuteTime);
                    }
                    case MYSQL: {}
                    case H2: {}
                }

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

                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        getSQLiteManager().deleteFromNullMutedPlayers("-id", id);
                        getSQLiteManager().insertIntoMutedPlayers(id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, UUID.fromString(uuid), unmuteTime);
                    }
                    case MYSQL: {}
                    case H2: {}
                }

                getMuteContainerManager().addToMuteContainer(id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, uuid, unmuteTime);
                return;

            }

        } else {

            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    if(getSQLiteManager().getMutedPlayersNames().contains(player.getName())
                            && getSQLiteManager().getMutedIps().get(getSQLiteManager().getMutedPlayersNames().indexOf(player.getName())).equalsIgnoreCase("NULL_PLAYER")) {
                        int indexOf = getSQLiteManager().getMutedPlayersNames().indexOf(player.getName());
                        String id = getSQLiteManager().getMutedIds().get(indexOf);
                        String ip = player.getAddress().getAddress().getHostAddress();
                        String name = player.getName();
                        String initiatorName = getSQLiteManager().getMuteInitiators().get(indexOf);
                        String reason = getSQLiteManager().getMuteReasons().get(indexOf);
                        MuteType muteType = getSQLiteManager().getMuteTypes().get(indexOf);
                        String muteDate = getSQLiteManager().getMuteDates().get(indexOf);
                        String muteTime = getSQLiteManager().getMuteTimes().get(indexOf);
                        String uuid = String.valueOf(player.getUniqueId());
                        long unmuteTime = getSQLiteManager().getUnmuteTimes().get(indexOf);

                        getSQLiteManager().deleteFromNullMutedPlayers("-id", id);
                        getSQLiteManager().insertIntoMutedPlayers(id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, UUID.fromString(uuid), unmuteTime);

                        return;
                    }

                    if(getSQLiteManager().getMutedIps().contains(player.getAddress().getAddress().getHostAddress())
                            && getSQLiteManager().getMutedUUIDs().get(getSQLiteManager().getMutedIps().indexOf(player.getAddress().getAddress().getHostAddress())).equalsIgnoreCase("NULL_PLAYER")) {

                        int indexOf = getSQLiteManager().getMutedIps().indexOf(player.getAddress().getAddress().getHostAddress());
                        String id = getSQLiteManager().getMutedIds().get(indexOf);
                        String ip = player.getAddress().getAddress().getHostAddress();
                        String name = player.getName();
                        String initiatorName = getSQLiteManager().getMuteInitiators().get(indexOf);
                        String reason = getSQLiteManager().getMuteReasons().get(indexOf);
                        MuteType muteType = getSQLiteManager().getMuteTypes().get(indexOf);
                        String muteDate = getSQLiteManager().getMuteDates().get(indexOf);
                        String muteTime = getSQLiteManager().getMuteTimes().get(indexOf);
                        String uuid = String.valueOf(player.getUniqueId());
                        long unmuteTime = getSQLiteManager().getUnmuteTimes().get(indexOf);
                        getSQLiteManager().deleteFromNullMutedPlayers("-id", id);
                        getSQLiteManager().insertIntoMutedPlayers(id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, UUID.fromString(uuid), unmuteTime);
                        return;
                    }

                }
                case H2: {}
                case MYSQL: {}
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
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    if(getSQLiteManager().getMutedUUIDs().contains(String.valueOf(player.getUniqueId()))) {
                        int indexOf = getSQLiteManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId()));
                        MuteType muteType = getSQLiteManager().getMuteTypes().get(indexOf);
                        String translatedUnmuteTime = getGlobalVariables().getVariableNever();
                        if(muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                            if(System.currentTimeMillis() >= getSQLiteManager().getUnmuteTimes().get(indexOf)) return;
                            TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                            translatedUnmuteTime = timeSettingsAccessor.getTimeManager().convertFromMillis(getSQLiteManager().getUnmuteTimes().get(indexOf) - System.currentTimeMillis());
                        }
                        if(getConfigSettings().isServerSupportsHoverEvents()) {
                            if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                player.spigot().sendMessage(MD5TextUtils.createHoverText(
                                        getFileAccessor().getLang().getString("other.join.muted.text").replace("%1$f", setColors(translatedUnmuteTime)),
                                        getFileAccessor().getLang().getString("other.join.muted.hover-text")
                                                .replace("%1$f", getSQLiteManager().getMuteInitiators().get(indexOf))
                                                .replace("%2$f", getSQLiteManager().getMuteDates().get(indexOf))
                                                .replace("%3$f", getSQLiteManager().getMuteTimes().get(indexOf))
                                                .replace("%4$f", getSQLiteManager().getMuteReasons().get(indexOf))
                                                .replace("%5$f", getSQLiteManager().getMutedIds().get(indexOf))
                                ));
                            } else {
                                player.sendMessage(AdventureApiUtils.createHoverText(setColors(getFileAccessor().getLang().getString("other.join.muted.text").replace("%1$f", setColors(translatedUnmuteTime))), setColors(getFileAccessor().getLang().getString("other.join.muted.hover-text")
                                        .replace("%1$f", getSQLiteManager().getMuteInitiators().get(indexOf))
                                        .replace("%2$f", getSQLiteManager().getMuteDates().get(indexOf))
                                        .replace("%3$f", getSQLiteManager().getMuteTimes().get(indexOf))
                                        .replace("%4$f", getSQLiteManager().getMuteReasons().get(indexOf))
                                        .replace("%5$f", getSQLiteManager().getMutedIds().get(indexOf)))));
                            }
                        } else {
                            player.sendMessage(setColors(getFileAccessor().getLang().getString("other.join.muted.text").replace("%1$f", setColors(translatedUnmuteTime))));
                        }
                    }
                }
                case H2: {

                }
                case MYSQL: {

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
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    int indexOf = getSQLiteManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId()));
                    MuteType muteType = getSQLiteManager().getMuteTypes().get(indexOf);
                    String translatedUnmuteTime = getGlobalVariables().getVariableNever();
                    if(muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                        TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                        translatedUnmuteTime = timeSettingsAccessor.getTimeManager().convertFromMillis(getSQLiteManager().getUnmuteTimes().get(indexOf) - System.currentTimeMillis());
                    }
                    if(getConfigSettings().isServerSupportsHoverEvents()) {
                        if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                            player.spigot().sendMessage(MD5TextUtils.createHoverText(
                                    getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.text").replace("%1$f", setColors(translatedUnmuteTime)),
                                    getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.hover-text")
                                            .replace("%1$f", getSQLiteManager().getMuteInitiators().get(indexOf))
                                            .replace("%2$f", getSQLiteManager().getMuteDates().get(indexOf))
                                            .replace("%3$f", getSQLiteManager().getMuteTimes().get(indexOf))
                                            .replace("%4$f", getSQLiteManager().getMuteReasons().get(indexOf))
                                            .replace("%5$f", getSQLiteManager().getMutedIds().get(indexOf))
                            ));
                        } else {
                            player.sendMessage(AdventureApiUtils.createHoverText(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.text").replace("%1$f", setColors(translatedUnmuteTime))), setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.hover-text")
                                    .replace("%1$f", getSQLiteManager().getMuteInitiators().get(indexOf))
                                    .replace("%2$f", getSQLiteManager().getMuteDates().get(indexOf))
                                    .replace("%3$f", getSQLiteManager().getMuteTimes().get(indexOf))
                                    .replace("%4$f", getSQLiteManager().getMuteReasons().get(indexOf))
                                    .replace("%5$f", getSQLiteManager().getMutedIds().get(indexOf)))));
                        }
                    } else {
                        player.sendMessage(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.command.text").replace("%1$f", setColors(translatedUnmuteTime))));
                    }
                }
                case H2: {

                }
                case MYSQL: {

                }
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
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    int indexOf = getSQLiteManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId()));
                    MuteType muteType = getSQLiteManager().getMuteTypes().get(indexOf);
                    String translatedUnmuteTime = getGlobalVariables().getVariableNever();
                    if(muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                        TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                        translatedUnmuteTime = timeSettingsAccessor.getTimeManager().convertFromMillis(getSQLiteManager().getUnmuteTimes().get(indexOf) - System.currentTimeMillis());
                    }
                    if(getConfigSettings().isServerSupportsHoverEvents()) {
                        if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                            player.spigot().sendMessage(MD5TextUtils.createHoverText(
                                    getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.text").replace("%1$f", setColors(translatedUnmuteTime)),
                                    getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.hover-text")
                                            .replace("%1$f", getSQLiteManager().getMuteInitiators().get(indexOf))
                                            .replace("%2$f", getSQLiteManager().getMuteDates().get(indexOf))
                                            .replace("%3$f", getSQLiteManager().getMuteTimes().get(indexOf))
                                            .replace("%4$f", getSQLiteManager().getMuteReasons().get(indexOf))
                                            .replace("%5$f", getSQLiteManager().getMutedIds().get(indexOf))
                            ));
                        } else {
                            player.sendMessage(AdventureApiUtils.createHoverText(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.text").replace("%1$f", setColors(translatedUnmuteTime))), setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.hover-text")
                                    .replace("%1$f", getSQLiteManager().getMuteInitiators().get(indexOf))
                                    .replace("%2$f", getSQLiteManager().getMuteDates().get(indexOf))
                                    .replace("%3$f", getSQLiteManager().getMuteTimes().get(indexOf))
                                    .replace("%4$f", getSQLiteManager().getMuteReasons().get(indexOf))
                                    .replace("%5$f", getSQLiteManager().getMutedIds().get(indexOf)))));
                        }
                    } else {
                        player.sendMessage(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.chat.text").replace("%1$f", setColors(translatedUnmuteTime))));
                    }
                }
                case H2: {

                }
                case MYSQL: {

                }
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
