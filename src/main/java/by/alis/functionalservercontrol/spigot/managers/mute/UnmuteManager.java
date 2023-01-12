package by.alis.functionalservercontrol.spigot.managers.mute;

import by.alis.functionalservercontrol.api.enums.StatsType;
import by.alis.functionalservercontrol.api.events.AsyncUnmutePreprocessEvent;
import by.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.*;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.isTextNotNull;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getDate;
import static by.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getTime;
import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;
import static by.alis.functionalservercontrol.spigot.managers.mute.MuteChecker.isPlayerMuted;
import static by.alis.functionalservercontrol.spigot.managers.mute.MuteManager.getMuteContainerManager;

public class UnmuteManager {

    public void preformUnmute(@NotNull OfflinePlayer player, @NotNull CommandSender unmuteInitiator, String unmuteReason, boolean announceUnmute) {
        String initiatorName = null;
        if(unmuteInitiator instanceof Player) {
            initiatorName = ((Player) unmuteInitiator).getName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }
        if(!isPlayerMuted(player)) {
            unmuteInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.player-not-muted")).replace("%1$f", player.getName()));
            return;
        }

        AsyncUnmutePreprocessEvent asyncUnmutePreprocessEvent = new AsyncUnmutePreprocessEvent(player, unmuteInitiator, unmuteReason);

        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
        }

        if(asyncUnmutePreprocessEvent.isCancelled()) return;

        if(unmuteReason == null) {
            if(unmuteInitiator instanceof Player) {
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
            getBaseManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
            getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.unmute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", isTextNotNull(unmuteReason) ? unmuteReason : getGlobalVariables().getDefaultReason()).replace("%4$f", getDate() + ", " + getTime()));
            if(unmuteInitiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)unmuteInitiator, StatsType.Administrator.STATS_UNMUTES);
            getMuteContainerManager().removeFromMuteContainer("-u", String.valueOf(player.getUniqueId()));
            if(!isTextNotNull(unmuteReason)) {
                if(announceUnmute) {
                    CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName())));
                }
            } else {
                if(announceUnmute) {
                    CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", unmuteReason)));
                }
            }
            if(player.isOnline()) {
                if(getConfigSettings().isSendTitleWhenUnmuted()) {
                    Player onlinePlayer = player.getPlayer();
                    sendTitleWhenUnmuted(onlinePlayer, unmuteReason, initiatorName);
                }
            }
        } else {
            try {
                getBaseManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
                getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.unmute").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", isTextNotNull(unmuteReason) ? unmuteReason : getGlobalVariables().getDefaultReason()).replace("%4$f", getDate() + ", " + getTime()));
                if(unmuteInitiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)unmuteInitiator, StatsType.Administrator.STATS_UNMUTES);
            } catch (NullPointerException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unmute player %player%".replace("%player%", player.getName())));
            }
            if(!isTextNotNull(unmuteReason)) {
                if(announceUnmute) {
                    CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName())));
                }
            } else {
                if(announceUnmute) {
                    CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", unmuteReason)));
                }
            }
            if(player.isOnline()) {
                if(getConfigSettings().isSendTitleWhenUnmuted()) {
                    Player onlinePlayer = player.getPlayer();
                    sendTitleWhenUnmuted(onlinePlayer, unmuteReason, initiatorName);
                }
            }
        }
    }

    public void preformUnmute(String player, CommandSender unmuteInitiator, String unmuteReason, boolean announceUnmute) {
        String initiatorName = null;
        if(!isPlayerMuted(player)) {
            unmuteInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.player-not-muted")).replace("%1$f", player));
            return;
        }
        if(unmuteInitiator instanceof Player) {
            initiatorName = ((Player) unmuteInitiator).getName();
        } else if(unmuteInitiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = unmuteInitiator.getName();
        }

        AsyncUnmutePreprocessEvent asyncUnmutePreprocessEvent = new AsyncUnmutePreprocessEvent(player, unmuteInitiator, unmuteReason);

        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
        }

        if(asyncUnmutePreprocessEvent.isCancelled()) return;

        if(unmuteReason == null) {
            if(unmuteInitiator instanceof Player) {
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
            try {
                getBaseManager().deleteFromNullMutedPlayers("-n", player);
                getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.unmute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", isTextNotNull(unmuteReason) ? unmuteReason : getGlobalVariables().getDefaultReason()).replace("%4$f", getDate() + ", " + getTime()));
                if(unmuteInitiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)unmuteInitiator, StatsType.Administrator.STATS_UNMUTES);
                getMuteContainerManager().removeFromMuteContainer("-n", player);
                if(!isTextNotNull(unmuteReason)) {
                    if(announceUnmute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
                    }
                } else {
                    if(announceUnmute) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unmuteReason)));
                    }
                }
                return;
            } catch (NullPointerException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unmute player %player%".replace("%player%", player)));
            }

        } else {
            try {
                getBaseManager().deleteFromNullMutedPlayers("-n", player);
                getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.unmute").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", isTextNotNull(unmuteReason) ? unmuteReason : getGlobalVariables().getDefaultReason()).replace("%4$f", getDate() + ", " + getTime()));
                if(unmuteInitiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)unmuteInitiator, StatsType.Administrator.STATS_UNMUTES);
            } catch (NullPointerException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unmute player %player%".replace("%player%", player)));
            }
            if(!isTextNotNull(unmuteReason)) {
                if(announceUnmute) {
                    CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
                }
            } else {
                if(announceUnmute) {
                    CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unmuteReason)));
                }
            }
        }
    }

    public void preformUnmute(OfflinePlayer player, String unmuteReason) {
        AsyncUnmutePreprocessEvent asyncUnmutePreprocessEvent = new AsyncUnmutePreprocessEvent(player, unmuteReason);

        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
        }

        if(asyncUnmutePreprocessEvent.isCancelled()) return;

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            try {
                getBaseManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
                getMuteContainerManager().removeFromMuteContainer("-u", String.valueOf(player.getUniqueId()));
                if(player.isOnline()) {
                    player.getPlayer().sendMessage(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.expired")));
                }
                return;
            } catch (NullPointerException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unmute player %player%".replace("%player%", player.getName())));
            }

        } else {
            try {
                getBaseManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
                if(player.isOnline()) {
                    player.getPlayer().sendMessage(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.expired")));
                }
            } catch (NullPointerException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unmute player %player%".replace("%player%", player.getName())));
            }
        }
    }

    public void preformUnmute(String player, String unmuteReason) { //Another

        AsyncUnmutePreprocessEvent asyncUnmutePreprocessEvent = new AsyncUnmutePreprocessEvent(player, unmuteReason);

        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
        }

        if(asyncUnmutePreprocessEvent.isCancelled()) return;

        unmuteReason = "The Ban time has expired";

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            try {
                getBaseManager().deleteFromNullMutedPlayers("-n", player);
                getMuteContainerManager().removeFromMuteContainer("-n", player);
                return;
            } catch (NullPointerException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unmute player %player%".replace("%player%", player)));
            }

        } else {
            getBaseManager().deleteFromNullMutedPlayers("-n", player);
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
        }
        getBaseManager().clearMutes();
        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.unmuteall").replace("%1$f", initiatorName).replace("%2$f", getDate() + ", " + getTime()));
        if(initiator instanceof Player) {
            for (int i = 0; i < count; i++) {
                getBaseManager().updateAdminStatsInfo((Player) initiator, StatsType.Administrator.STATS_UNMUTES);
            }
        }
        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmuteall.success").replace("%1$f", String.valueOf(count))));
        if(announceUnmute) {
            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unmuteall.broadcast-message").replace("%1$f", initiatorName)));
        }

    }

    public void sendTitleWhenUnmuted(Player player, String reason, String initiatorName) {
        if(reason == null) {
            reason = getGlobalVariables().getDefaultReason();
        }
        CoreAdapter.getAdapter().sendTitle(player, setColors(getLanguage().getTitleWhenUnmuted()[0]), setColors(getLanguage().getTitleWhenUnmuted()[1].replace("%1$f", reason).replace("%2$f", initiatorName)));
    }

}
