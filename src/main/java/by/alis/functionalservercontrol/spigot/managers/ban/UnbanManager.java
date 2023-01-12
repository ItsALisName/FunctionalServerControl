package by.alis.functionalservercontrol.spigot.managers.ban;

import by.alis.functionalservercontrol.api.enums.StatsType;
import by.alis.functionalservercontrol.api.events.AsyncUnbanPreprocessEvent;
import by.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBanContainerManager;
import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.isTextNotNull;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getDate;
import static by.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getTime;
import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;
import static by.alis.functionalservercontrol.spigot.managers.ban.BanChecker.isPlayerBanned;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class UnbanManager {

    public void preformUnban(@NotNull OfflinePlayer player, @NotNull CommandSender unbanInitiator, String unbanReason, boolean announceUnban) {
        String initiatorName = null;
        if(unbanInitiator instanceof Player) {
            initiatorName = ((Player) unbanInitiator).getName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }
        if(!isPlayerBanned(player)) {
            unbanInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unban.player-not-banned")).replace("%1$f", player.getName()));
            return;
        }

        AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(player, unbanInitiator, unbanReason);

        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
        }

        if(asyncUnbanPreprocessEvent.isCancelled()) return;

        if(unbanReason == null) {
            if(unbanInitiator instanceof Player) {
                if(!getConfigSettings().isAllowedUnbanWithoutReason() && !unbanInitiator.hasPermission("functionalservercontrol.use.no-reason")) {
                    unbanInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-reason")));
                    asyncUnbanPreprocessEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(!announceUnban) {
            if(!unbanInitiator.hasPermission("functionalservercontrol.use.silently")) {
                unbanInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                asyncUnbanPreprocessEvent.setCancelled(true);
                return;
            }
        }

        unbanReason = asyncUnbanPreprocessEvent.getReason();

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            try {
                getBaseManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.unban").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", isTextNotNull(unbanReason) ? unbanReason : getGlobalVariables().getDefaultReason()).replace("%4$f", getDate() + ", " + getTime()));
                if(unbanInitiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)unbanInitiator, StatsType.Administrator.STATS_UNBANS);
                getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId()));
                if(!isTextNotNull(unbanReason)) {
                    if(announceUnban) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName())));
                    }
                } else {
                    if(announceUnban) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", unbanReason)));
                    }
                }
                return;
            } catch (NullPointerException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player.getName())));
            }

        } else {
            try {
                getBaseManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.unban").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", isTextNotNull(unbanReason) ? unbanReason : getGlobalVariables().getDefaultReason()).replace("%4$f", getDate() + ", " + getTime()));
                if(unbanInitiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)unbanInitiator, StatsType.Administrator.STATS_UNBANS);
            } catch (NullPointerException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player.getName())));
            }
            if(!isTextNotNull(unbanReason)) {
                if(announceUnban) {
                    CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName())));
                }
            } else {
                if(announceUnban) {
                    CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", unbanReason)));
                }
            }
        }

    }

    public void preformUnban(String player, CommandSender unbanInitiator, String unbanReason, boolean announceUnban) {
        String initiatorName = null;
        if(!isPlayerBanned(player)) {
            unbanInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unban.player-not-banned")).replace("%1$f", player));
            return;
        }
        if(unbanInitiator instanceof Player) {
            initiatorName = ((Player) unbanInitiator).getName();
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
            if(unbanInitiator instanceof Player) {
                if(!getConfigSettings().isAllowedUnbanWithoutReason() && !unbanInitiator.hasPermission("functionalservercontrol.use.no-reason")) {
                    unbanInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-reason")));
                    asyncUnbanPreprocessEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(!announceUnban) {
            if(!unbanInitiator.hasPermission("functionalservercontrol.use.silently")) {
                unbanInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                asyncUnbanPreprocessEvent.setCancelled(true);
                return;
            }
        }

        unbanReason = asyncUnbanPreprocessEvent.getReason();



        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            try {
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        getBaseManager().deleteFromNullBannedPlayers("-n", player);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.unban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", isTextNotNull(unbanReason) ? unbanReason : getGlobalVariables().getDefaultReason()).replace("%4$f", getDate() + ", " + getTime()));
                        if(unbanInitiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)unbanInitiator, StatsType.Administrator.STATS_UNBANS);
                        break;
                    }
                    
                    case H2: {
                        break;
                    }
                }
                getBanContainerManager().removeFromBanContainer("-n", player);
                if(!isTextNotNull(unbanReason)) {
                    if(announceUnban) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
                    }
                } else {
                    if(announceUnban) {
                        CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unbanReason)));
                    }
                }
                return;
            } catch (NullPointerException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player)));
            }

        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    try {
                        getBaseManager().deleteFromNullBannedPlayers("-n", player);
                        getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.unban").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", isTextNotNull(unbanReason) ? unbanReason : getGlobalVariables().getDefaultReason()).replace("%4$f", getDate() + ", " + getTime()));
                        if(unbanInitiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)unbanInitiator, StatsType.Administrator.STATS_UNBANS);
                    } catch (NullPointerException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player)));
                    }
                    if(!isTextNotNull(unbanReason)) {
                        if(announceUnban) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
                        }
                    } else {
                        if(announceUnban) {
                            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unbanReason)));
                        }
                    }
                    break;
                }
                case H2: {
                    break;
                }
            }
        }
    }

    public void preformUnban(OfflinePlayer player, String unbanReason) {
        AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(player, unbanReason);

        if(getConfigSettings().isApiEnabled()) {
            Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
        }

        if(asyncUnbanPreprocessEvent.isCancelled()) return;

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            try {
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        getBaseManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                        break;
                    }
                    
                    case H2: {
                        break;
                    }
                }
                getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId()));
                return;
            } catch (NullPointerException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player.getName())));
            }

        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    try {
                        getBaseManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                    } catch (NullPointerException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player.getName())));
                    }
                    break;
                }
                case H2: {
                    break;
                }
            }
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
            try {
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        getBaseManager().deleteFromNullBannedPlayers("-n", player);
                        break;
                    }
                    
                    case H2: {
                        break;
                    }
                }
                getBanContainerManager().removeFromBanContainer("-n", player);
                return;
            } catch (NullPointerException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player)));
            }

        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    try {
                        getBaseManager().deleteFromNullBannedPlayers("-n", player);
                    } catch (NullPointerException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player)));
                    }
                    break;
                }
                case H2: {
                    break;
                }
            }
        }
    }

    public void preformGlobalUnban(CommandSender initiator, boolean announceUnban) {

        String initiatorName = null;
        if(initiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = initiator.getName();
        }

        if(!announceUnban) {
            if(!initiator.hasPermission("functionalservercontrol.use.silently")) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
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
                getBaseManager().clearBans();
                getBaseManager().insertIntoHistory(getFileAccessor().getLang().getString("other.history-formats.unbanall").replace("%1$f", initiatorName).replace("%2$f", getDate() + ", " + getTime()));
                for(int i = 0; i < count; i++) {
                    if(initiator instanceof Player) getBaseManager().updateAdminStatsInfo((Player)initiator, StatsType.Administrator.STATS_UNBANS);
                }
                break;
            }
            case H2: {
                break;
            }
        }

        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unbanall.success").replace("%1$f", String.valueOf(count))));

        if(announceUnban) {
            CoreAdapter.getAdapter().broadcast(setColors(getFileAccessor().getLang().getString("commands.unbanall.broadcast-message").replace("%1$f", initiatorName)));
        }

    }

}
