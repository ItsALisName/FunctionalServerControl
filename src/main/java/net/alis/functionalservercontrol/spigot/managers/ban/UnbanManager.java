package net.alis.functionalservercontrol.spigot.managers.ban;

import net.alis.functionalservercontrol.api.enums.StatsType;
import net.alis.functionalservercontrol.api.events.AsyncUnbanPreprocessEvent;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;

import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.spigot.managers.IdsManager;
import net.alis.functionalservercontrol.spigot.managers.file.SFAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBanContainerManager;
import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static net.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getDate;
import static net.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getTime;

public class UnbanManager {

    public void preformUnban(@NotNull OfflineFunctionalPlayer player, @NotNull CommandSender unbanInitiator, String unbanReason, boolean announceUnban) {
        String initiatorName = null;
        if(unbanInitiator instanceof FunctionalPlayer) {
            initiatorName = unbanInitiator.getName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }
        if(!BanChecker.isPlayerBanned(player)) {
            unbanInitiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.unban.player-not-banned")).replace("%1$f", player.nickname()));
            return;
        }
        AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(player, unbanInitiator, unbanReason);

        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
        }

        if(asyncUnbanPreprocessEvent.isCancelled()) return;

        if(unbanReason == null) {
            if(unbanInitiator instanceof FunctionalPlayer) {
                if(!getConfigSettings().isAllowedUnbanWithoutReason() && !unbanInitiator.hasPermission("functionalservercontrol.use.no-reason")) {
                    unbanInitiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.no-reason")));
                    asyncUnbanPreprocessEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(!announceUnban) {
            if(!unbanInitiator.hasPermission("functionalservercontrol.use.silently")) {
                unbanInitiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                asyncUnbanPreprocessEvent.setCancelled(true);
                return;
            }
        }

        unbanReason = asyncUnbanPreprocessEvent.getReason();

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            try {
                BaseManager.getBaseManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.unban").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", TextUtils.isTextNotNull(unbanReason) ? unbanReason : getGlobalVariables().getDefaultReason()).replace("%4$f", getDate() + ", " + getTime()));
                if(unbanInitiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) unbanInitiator).getFunctionalId(), StatsType.Administrator.STATS_UNBANS);
                getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId()));
                if(!TextUtils.isTextNotNull(unbanReason)) {
                    if(announceUnban) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.nickname())));
                    }
                } else {
                    if(announceUnban) {
                        Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", unbanReason)));
                    }
                }
                return;
            } catch (NullPointerException ignored) {
                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player.nickname())));
            }

        } else {
            try {
                BaseManager.getBaseManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.unban").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", TextUtils.isTextNotNull(unbanReason) ? unbanReason : getGlobalVariables().getDefaultReason()).replace("%4$f", getDate() + ", " + getTime()));
                if(unbanInitiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) unbanInitiator).getFunctionalId(), StatsType.Administrator.STATS_UNBANS);
            } catch (NullPointerException ignored) {
                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player.nickname())));
            }
            if(!TextUtils.isTextNotNull(unbanReason)) {
                if(announceUnban) {
                    Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.nickname())));
                }
            } else {
                if(announceUnban) {
                    Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.nickname()).replace("%3$f", unbanReason)));
                }
            }
        }

    }

    public void preformUnban(String player, CommandSender unbanInitiator, String unbanReason, boolean announceUnban) {
        String initiatorName;
        if(!BanChecker.isPlayerBanned(player)) {
            unbanInitiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.unban.player-not-banned")).replace("%1$f", player));
            return;
        }
        if(unbanInitiator instanceof FunctionalPlayer) {
            initiatorName = unbanInitiator.getName();
        } else if(unbanInitiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = unbanInitiator.getName();
        }

        AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(player, unbanInitiator, unbanReason);

        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
        }

        if(asyncUnbanPreprocessEvent.isCancelled()) return;

        if(unbanReason == null) {
            if(unbanInitiator instanceof FunctionalPlayer) {
                if(!getConfigSettings().isAllowedUnbanWithoutReason() && !unbanInitiator.hasPermission("functionalservercontrol.use.no-reason")) {
                    unbanInitiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.no-reason")));
                    asyncUnbanPreprocessEvent.setCancelled(true);
                    return;
                }
            }
        }
        if(!announceUnban) {
            if(!unbanInitiator.hasPermission("functionalservercontrol.use.silently")) {
                unbanInitiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                asyncUnbanPreprocessEvent.setCancelled(true);
                return;
            }
        }
        unbanReason = asyncUnbanPreprocessEvent.getReason();
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            BaseManager.getBaseManager().deleteFromNullBannedPlayers("-n", player);
            BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.unban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", TextUtils.isTextNotNull(unbanReason) ? unbanReason : getGlobalVariables().getDefaultReason()).replace("%4$f", getDate() + ", " + getTime()));
            if(unbanInitiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) unbanInitiator).getFunctionalId(), StatsType.Administrator.STATS_UNBANS);
            getBanContainerManager().removeFromBanContainer("-n", player);
        } else {
            BaseManager.getBaseManager().deleteFromNullBannedPlayers("-n", player);
            BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.unban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", TextUtils.isTextNotNull(unbanReason) ? unbanReason : getGlobalVariables().getDefaultReason()).replace("%4$f", getDate() + ", " + getTime()));
            if(unbanInitiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) unbanInitiator).getFunctionalId(), StatsType.Administrator.STATS_UNBANS);
        }
        if(!TextUtils.isTextNotNull(unbanReason)) {
            if(announceUnban) {
                Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
            }
        } else {
            if(announceUnban) {
                Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unbanReason)));
            }
        }
    }

    public void preformUnban(OfflineFunctionalPlayer player, String unbanReason) {
        AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(player, unbanReason);

        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
        }

        if(asyncUnbanPreprocessEvent.isCancelled()) return;

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            BaseManager.getBaseManager().deleteFromBannedPlayers("-fid", player.getFunctionalId().toString());
            getBanContainerManager().removeFromBanContainer("-fid", player.getFunctionalId().toString());
        } else {
            BaseManager.getBaseManager().deleteFromBannedPlayers("-fid", player.getFunctionalId().toString());
        }
    }

    public void preformUnban(String player, String unbanReason) {

        AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(player, unbanReason);

        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
        }

        if(asyncUnbanPreprocessEvent.isCancelled()) return;

        unbanReason = "The Ban time has expired";

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            BaseManager.getBaseManager().deleteFromNullBannedPlayers("-n", player);
            getBanContainerManager().removeFromBanContainer("-n", player);
        } else {
            BaseManager.getBaseManager().deleteFromNullBannedPlayers("-n", player);
        }
    }

    public void preformUnbanById(CommandSender initiator, String id, String unbanReason, boolean announceUnban) {
        String initiatorName;
        if(initiator instanceof FunctionalPlayer) {
            initiatorName = initiator.getName();
        } else if(initiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = initiator.getName();
        }
        if(unbanReason == null) {
            if(initiator instanceof FunctionalPlayer) {
                if(!getConfigSettings().isAllowedUnbanWithoutReason() && !initiator.hasPermission("functionalservercontrol.use.no-reason")) {
                    initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.no-reason")));
                    return;
                }
            }
        }
        if(!announceUnban) {
            if(!initiator.hasPermission("functionalservercontrol.use.silently")) {
                initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                return;
            }
        }
        IdsManager idsManager = new IdsManager();
        if(!idsManager.isBannedId(id)) {
            if(idsManager.isMutedId(id)) {
                initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.unban.id-not-banned-but-muted").replace("%1$f", id)));
                return;
            }
            initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.unban.id-not-banned").replace("%1$f", id)));
            return;
        }
        String playerName;
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            playerName = getBannedPlayersContainer().getNameContainer().get(getBannedPlayersContainer().getIdsContainer().indexOf(id));
        } else {
            playerName = BaseManager.getBaseManager().getBannedPlayersNames().get(BaseManager.getBaseManager().getBannedIds().indexOf(id));
        }
        OfflineFunctionalPlayer player = OfflineFunctionalPlayer.get(playerName);
        if(player != null) {
            AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(player, initiator, unbanReason);
            if(getConfigSettings().isApiEnabled()) {
                Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
                if(asyncUnbanPreprocessEvent.isCancelled()) return;
                unbanReason = TextUtils.isTextNotNull(asyncUnbanPreprocessEvent.getReason()) ? asyncUnbanPreprocessEvent.getReason() : unbanReason;
            }
            if(getConfigSettings().isAllowedUseRamAsContainer()) {
                getBanContainerManager().removeFromBanContainer("-id", id);
            }
            BaseManager.getBaseManager().deleteFromBannedPlayers("-id", id);
            BaseManager.getBaseManager().deleteFromNullBannedPlayers("-id", id);
        } else {
            AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(playerName, initiator, unbanReason);
            if(getConfigSettings().isApiEnabled()) {
                Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
                if(asyncUnbanPreprocessEvent.isCancelled()) return;
                unbanReason = TextUtils.isTextNotNull(asyncUnbanPreprocessEvent.getReason()) ? asyncUnbanPreprocessEvent.getReason() : unbanReason;
            }
            if(getConfigSettings().isAllowedUseRamAsContainer()) {
                getBanContainerManager().removeFromBanContainer("-id", id);
            }
            BaseManager.getBaseManager().deleteFromBannedPlayers("-id", id);
            BaseManager.getBaseManager().deleteFromNullBannedPlayers("-id", id);
        }
        if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_UNBANS);
        if(!TextUtils.isTextNotNull(unbanReason)) {
            if(announceUnban) {
                Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", playerName)));
            }
        } else {
            if(announceUnban) {
                Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", playerName).replace("%3$f", unbanReason)));
            }
        }
    }

    public void preformGlobalUnban(CommandSender initiator, boolean announceUnban) {

        String initiatorName;
        if(initiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = initiator.getName();
        }

        if(!announceUnban) {
            if(!initiator.hasPermission("functionalservercontrol.use.silently")) {
                initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                return;
            }
        }

        int count = getBannedPlayersContainer().getIdsContainer().size();

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            getBannedPlayersContainer().getIdsContainer().clear();
            getBannedPlayersContainer().getIpContainer().clear();
            getBannedPlayersContainer().getUUIDContainer().clear();
            getBannedPlayersContainer().getNameContainer().clear();
            getBannedPlayersContainer().getBanTypesContainer().clear();
            getBannedPlayersContainer().getBanTimeContainer().clear();
            getBannedPlayersContainer().getRealBanDateContainer().clear();
            getBannedPlayersContainer().getRealBanTimeContainer().clear();
            getBannedPlayersContainer().getInitiatorNameContainer().clear();
            getBannedPlayersContainer().getReasonContainer().clear();
        }

        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                BaseManager.getBaseManager().clearBans();
                BaseManager.getBaseManager().insertIntoHistory(SFAccessor.getFileAccessor().getLang().getString("other.history-formats.unbanall").replace("%1$f", initiatorName).replace("%2$f", getDate() + ", " + getTime()));
                for(int i = 0; i < count; i++) {
                    if(initiator instanceof FunctionalPlayer) BaseManager.getBaseManager().updateAdminStatsInfo(((FunctionalPlayer) initiator).getFunctionalId(), StatsType.Administrator.STATS_UNBANS);
                }
                break;
            }
            case H2: {
                break;
            }
        }

        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.unbanall.success").replace("%1$f", String.valueOf(count))));

        if(announceUnban) {
            Bukkit.broadcastMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.unbanall.broadcast-message").replace("%1$f", initiatorName)));
        }

    }

}
