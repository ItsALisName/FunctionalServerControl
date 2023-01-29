package net.alis.functionalservercontrol.spigot.managers.mute;

import net.alis.functionalservercontrol.api.enums.StatsType;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;

import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.api.events.AsyncUnmutePreprocessEvent;
import net.alis.functionalservercontrol.spigot.managers.IdsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.*;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.isTextNotNull;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getDate;
import static net.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getTime;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;
import static net.alis.functionalservercontrol.spigot.managers.mute.MuteChecker.isPlayerMuted;
import static net.alis.functionalservercontrol.spigot.managers.mute.MuteManager.getMuteContainerManager;

public class UnmuteManager {

    public void preformUnmute(@NotNull OfflineFunctionalPlayer player, @NotNull CommandSender unmuteInitiator, String unmuteReason, boolean announceUnmute) {
        String initiatorName;
        if(unmuteInitiator instanceof FunctionalPlayer) {
            initiatorName = unmuteInitiator.getName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }
        if(!isPlayerMuted(player)) {
            unmuteInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.player-not-muted")).replace("%1$f", player.nickname()));
            return;
        }
        if(!player.isOnline() && !unmuteInitiator.hasPermission("functionalservercontrol.unmute.offline")) {
            unmuteInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
            return;
        }
        AsyncUnmutePreprocessEvent asyncUnmutePreprocessEvent = new AsyncUnmutePreprocessEvent(player, unmuteInitiator, unmuteReason);

        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
        }

        if(asyncUnmutePreprocessEvent.isCancelled()) return;

        if(unmuteReason == null) {
            if(unmuteInitiator instanceof FunctionalPlayer) {
                if(!getConfigSettings().isUnmuteAllowedWithoutReason() && !unmuteInitiator.hasPermission("functionalservercontrol.use.no-reason")) {
                    unmuteInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-reason")));
                    asyncUnmutePreprocessEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(!announceUnmute) {
            if(!unmuteInitiator.hasPermission("functionalservercontrol.use.silently")) {
                unmuteInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                asyncUnmutePreprocessEvent.setCancelled(true);
                return;
            }
        }

        unmuteReason = asyncUnmutePreprocessEvent.getReason();

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            BaseManager.getBaseManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
            BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.unmute").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", isTextNotNull(unmuteReason) ? unmuteReason : getGlobalVariables().getDefaultReason()).replace("%4$f", getDate() + ", " + getTime()));
            if(unmuteInitiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)unmuteInitiator).getFunctionalId(), StatsType.Administrator.STATS_UNMUTES);
            getMuteContainerManager().removeFromMuteContainer("-u", String.valueOf(player.getUniqueId()));
            if(!isTextNotNull(unmuteReason)) {
                if(announceUnmute) {
                    Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.nickname())));
                }
            } else {
                if(announceUnmute) {
                    Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", unmuteReason)));
                }
            }
            if(player.isOnline()) {
                if(getConfigSettings().isSendTitleWhenUnmuted()) {
                    sendTitleWhenUnmuted(player.getPlayer(), unmuteReason, initiatorName);
                }
            }
        } else {
            BaseManager.getBaseManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
            BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.unmute").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", isTextNotNull(unmuteReason) ? unmuteReason : getGlobalVariables().getDefaultReason()).replace("%4$f", getDate() + ", " + getTime()));
            if(unmuteInitiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)unmuteInitiator).getFunctionalId(), StatsType.Administrator.STATS_UNMUTES);
            if(!isTextNotNull(unmuteReason)) {
                if(announceUnmute) {
                    Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.nickname())));
                }
            } else {
                if(announceUnmute) {
                    Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", unmuteReason)));
                }
            }
            if(player.isOnline()) {
                if(getConfigSettings().isSendTitleWhenUnmuted()) {
                    sendTitleWhenUnmuted(player.getPlayer(), unmuteReason, initiatorName);
                }
            }
        }
    }

    public void preformUnmuteById(CommandSender initiator, String id, String unmuteReason, boolean announceUnmute) {
        String initiatorName;
        if(initiator instanceof FunctionalPlayer) {
            initiatorName = initiator.getName();
        } else if(initiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = initiator.getName();
        }
        if(unmuteReason == null) {
            if(initiator instanceof FunctionalPlayer) {
                if(!getConfigSettings().isMuteAllowedWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                    initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-reason")));
                    return;
                }
            }
        }
        if(!announceUnmute) {
            if(!initiator.hasPermission("functionalservercontrol.use.silently")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                return;
            }
        }
        IdsManager idsManager = new IdsManager();
        if(!idsManager.isMutedId(id)) {
            if(idsManager.isBannedId(id)) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.id-not-muted-but-banned").replace("%1$f", id)));
                return;
            }
            initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.id-not-muted").replace("%1$f", id)));
            return;
        }
        String playerName;
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            playerName = getMutedPlayersContainer().getNameContainer().get(getMutedPlayersContainer().getIdsContainer().indexOf(id));
        } else {
            playerName = BaseManager.getBaseManager().getMutedPlayersNames().get(BaseManager.getBaseManager().getMutedIds().indexOf(id));
        }
        OfflineFunctionalPlayer player = OfflineFunctionalPlayer.get(playerName);
        if(player != null) {
            if(!player.isOnline() && !initiator.hasPermission("functionalservercontrol.unmute.offline")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                return;
            }
            AsyncUnmutePreprocessEvent asyncUnmutePreprocessEvent = new AsyncUnmutePreprocessEvent(player, initiator, unmuteReason);
            if(getConfigSettings().isApiEnabled()) {
                Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
                if(asyncUnmutePreprocessEvent.isCancelled()) return;
                unmuteReason = isTextNotNull(asyncUnmutePreprocessEvent.getReason()) ? asyncUnmutePreprocessEvent.getReason() : unmuteReason;
            }
            if(getConfigSettings().isAllowedUseRamAsContainer()) {
                getMuteContainerManager().removeFromMuteContainer("-id", id);
            }
            BaseManager.getBaseManager().deleteFromMutedPlayers("-id", id);
            BaseManager.getBaseManager().deleteFromNullMutedPlayers("-id", id);
        } else {
            if(!initiator.hasPermission("functionalservercontrol.unmute.offline")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                return;
            }
            AsyncUnmutePreprocessEvent asyncUnmutePreprocessEvent = new AsyncUnmutePreprocessEvent(playerName, initiator, unmuteReason);
            if(getConfigSettings().isApiEnabled()) {
                Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
                if(asyncUnmutePreprocessEvent.isCancelled()) return;
                unmuteReason = isTextNotNull(asyncUnmutePreprocessEvent.getReason()) ? asyncUnmutePreprocessEvent.getReason() : unmuteReason;
            }
            if(getConfigSettings().isAllowedUseRamAsContainer()) {
                getMuteContainerManager().removeFromMuteContainer("-id", id);
            }
            BaseManager.getBaseManager().deleteFromMutedPlayers("-id", id);
            BaseManager.getBaseManager().deleteFromNullMutedPlayers("-id", id);
        }
        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(FunctionalPlayer.get(initiator.getName()).getFunctionalId(), StatsType.Administrator.STATS_UNMUTES);
        if(!isTextNotNull(unmuteReason)) {
            if(announceUnmute) {
                Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", playerName)));
            }
        } else {
            if(announceUnmute) {
                Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", playerName).replace("%3$f", unmuteReason)));
            }
        }
        return;
    }

    public void preformUnmute(String player, CommandSender unmuteInitiator, String unmuteReason, boolean announceUnmute) {
        String initiatorName = null;
        if(!isPlayerMuted(player)) {
            unmuteInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.player-not-muted")).replace("%1$f", player));
            return;
        }
        if(unmuteInitiator instanceof FunctionalPlayer) {
            initiatorName = unmuteInitiator.getName();
        } else if(unmuteInitiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = unmuteInitiator.getName();
        }
        if(!unmuteInitiator.hasPermission("functionalservercontrol.unmute.offline")) {
            unmuteInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
            return;
        }
        AsyncUnmutePreprocessEvent asyncUnmutePreprocessEvent = new AsyncUnmutePreprocessEvent(player, unmuteInitiator, unmuteReason);

        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
        }

        if(asyncUnmutePreprocessEvent.isCancelled()) return;

        if(unmuteReason == null) {
            if(unmuteInitiator instanceof FunctionalPlayer) {
                if(!getConfigSettings().isUnmuteAllowedWithoutReason() && !unmuteInitiator.hasPermission("functionalservercontrol.use.no-reason")) {
                    unmuteInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-reason")));
                    asyncUnmutePreprocessEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(!announceUnmute) {
            if(!unmuteInitiator.hasPermission("functionalservercontrol.use.silently")) {
                unmuteInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                asyncUnmutePreprocessEvent.setCancelled(true);
                return;
            }
        }

        unmuteReason = asyncUnmutePreprocessEvent.getReason();



        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            BaseManager.getBaseManager().deleteFromNullMutedPlayers("-n", player);
            BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.unmute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", isTextNotNull(unmuteReason) ? unmuteReason : getGlobalVariables().getDefaultReason()).replace("%4$f", getDate() + ", " + getTime()));
            if(unmuteInitiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)unmuteInitiator).getFunctionalId(), StatsType.Administrator.STATS_UNMUTES);
            getMuteContainerManager().removeFromMuteContainer("-n", player);
            if(!isTextNotNull(unmuteReason)) {
                if(announceUnmute) {
                    Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
                }
            } else {
                if(announceUnmute) {
                    Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unmuteReason)));
                }
            }
        } else {
            BaseManager.getBaseManager().deleteFromNullMutedPlayers("-n", player);
            BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.unmute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", isTextNotNull(unmuteReason) ? unmuteReason : getGlobalVariables().getDefaultReason()).replace("%4$f", getDate() + ", " + getTime()));
            if(unmuteInitiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)unmuteInitiator).getFunctionalId(), StatsType.Administrator.STATS_UNMUTES);
            if(!isTextNotNull(unmuteReason)) {
                if(announceUnmute) {
                    Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
                }
            } else {
                if(announceUnmute) {
                    Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unmuteReason)));
                }
            }
        }
    }

    public void preformUnmute(OfflineFunctionalPlayer player, String unmuteReason) {
        AsyncUnmutePreprocessEvent asyncUnmutePreprocessEvent = new AsyncUnmutePreprocessEvent(player, unmuteReason);

        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
        }

        if(asyncUnmutePreprocessEvent.isCancelled()) return;

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            BaseManager.getBaseManager().deleteFromMutedPlayers("-fid", String.valueOf(player.getFunctionalId()));
            getMuteContainerManager().removeFromMuteContainer("-fid", String.valueOf(player.getFunctionalId()));
            if(player.isOnline()) {
                player.getPlayer().sendMessage(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.expired")));
            }
        } else {
            BaseManager.getBaseManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
            if(player.isOnline()) {
                player.getPlayer().sendMessage(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.expired")));
            }
        }
    }

    public void preformUnmute(String player, String unmuteReason) { //Another

        AsyncUnmutePreprocessEvent asyncUnmutePreprocessEvent = new AsyncUnmutePreprocessEvent(player, unmuteReason);

        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
        }

        if(asyncUnmutePreprocessEvent.isCancelled()) return;
        FID fid = new FID(player);
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            BaseManager.getBaseManager().deleteFromNullMutedPlayers("-fid", fid.toString());
            getMuteContainerManager().removeFromMuteContainer("-fid", fid.toString());
        } else {
            BaseManager.getBaseManager().deleteFromNullMutedPlayers("-fid", fid.toString());
        }
    }

    public void preformGlobalUnmute(@NotNull CommandSender initiator, boolean announceUnmute) {
        String initiatorName = null;
        if(initiator instanceof ConsoleCommandSender){
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = initiator.getName();
        }
        if(!announceUnmute) {
            if(!initiator.hasPermission("functionalservercontrol.use.silently")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                return;
            }
        }
        int count = getBannedPlayersContainer().getIdsContainer().size();
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            getMutedPlayersContainer().getIdsContainer().clear();
            getMutedPlayersContainer().getIpContainer().clear();
            getMutedPlayersContainer().getUUIDContainer().clear();
            getMutedPlayersContainer().getNameContainer().clear();
            getMutedPlayersContainer().getMuteTypesContainer().clear();
            getMutedPlayersContainer().getMuteTimeContainer().clear();
            getMutedPlayersContainer().getRealMuteDateContainer().clear();
            getMutedPlayersContainer().getRealMuteTimeContainer().clear();
            getMutedPlayersContainer().getInitiatorNameContainer().clear();
            getMutedPlayersContainer().getReasonContainer().clear();
            getMutedPlayersContainer().getFids().clear();
        }
        BaseManager.getBaseManager().clearMutes();
        BaseManager.getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.unmuteall").replace("%1$f", initiatorName).replace("%2$f", getDate() + ", " + getTime()));
        if(initiator instanceof FunctionalPlayer) {
            for (int i = 0; i < count; i++) {
                BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer)initiator).getFunctionalId(), StatsType.Administrator.STATS_UNMUTES);
            }
        }
        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmuteall.success").replace("%1$f", String.valueOf(count))));
        if(announceUnmute) {
            Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmuteall.broadcast-message").replace("%1$f", initiatorName)));
        }

    }

    public void sendTitleWhenUnmuted(FunctionalPlayer player, String reason, String initiatorName) {
        if(reason == null) {
            reason = getGlobalVariables().getDefaultReason();
        }
        player.title(setColors(getLanguage().getTitleWhenUnmuted()[0]), setColors(getLanguage().getTitleWhenUnmuted()[1].replace("%1$f", reason).replace("%2$f", initiatorName)));
    }

}
