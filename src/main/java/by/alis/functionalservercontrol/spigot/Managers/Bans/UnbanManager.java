package by.alis.functionalservercontrol.spigot.Managers.Bans;

import by.alis.functionalservercontrol.API.Spigot.Events.AsyncUnbanPreprocessEvent;
import by.alis.functionalservercontrol.spigot.Managers.CooldownsManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getBanContainerManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Bans.BanChecker.isPlayerBanned;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class UnbanManager {

    public void preformUnban(@NotNull OfflinePlayer player, @NotNull CommandSender unbanInitiator, String unbanReason, boolean announceUnban) {
        String initiatorName = null;
        if(unbanInitiator instanceof Player) {
            initiatorName = ((Player) unbanInitiator).getPlayerListName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }
        if(!isPlayerBanned(player)) {
            unbanInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unban.player-not-banned")).replace("%1$f", player.getName()));
            return;
        }

        AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(player, unbanInitiator, unbanReason, getConfigSettings().isApiEnabled());

        if(getConfigSettings().isApiEnabled()) {
            if(getConfigSettings().isApiProtectedByPassword()) {
                if(asyncUnbanPreprocessEvent.getApiPassword() != null && asyncUnbanPreprocessEvent.getApiPassword().equalsIgnoreCase(getFileAccessor().getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
                }
            } else {
                Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
            }
        }

        if(asyncUnbanPreprocessEvent.isCancelled()) return;

        if(unbanInitiator instanceof Player) {
            if(CooldownsManager.playerHasCooldown(((Player) unbanInitiator).getPlayer(), "unban")) {
                CooldownsManager.notifyAboutCooldown(((Player) unbanInitiator).getPlayer(), "unban");
                asyncUnbanPreprocessEvent.setCancelled(true);
                return;
            } else {
                CooldownsManager.setCooldown(((Player) unbanInitiator).getPlayer(), "unban");
            }
        }

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
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                        break;
                    }
                    case MYSQL: {
                        break;
                    }
                    case H2: {
                        break;
                    }
                }
                getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId()));
                if(unbanReason == null || unbanReason.equalsIgnoreCase("")) {
                    if(announceUnban) {
                        Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName())));
                    }
                } else {
                    if(announceUnban) {
                        Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", unbanReason)));
                    }
                }
                return;
            } catch (NullPointerException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player.getName())));
            }

        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    try {
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                    } catch (NullPointerException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player.getName())));
                    }
                    if(unbanReason == null || unbanReason.equalsIgnoreCase("")) {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName())));
                        }
                    } else {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", unbanReason)));
                        }
                    }
                    break;
                }
                case MYSQL: {
                    break;
                }
                case H2: {
                    break;
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
            initiatorName = ((Player) unbanInitiator).getPlayerListName();
        } else if(unbanInitiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = unbanInitiator.getName();
        }

        AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(player, unbanInitiator, unbanReason, getConfigSettings().isApiEnabled());

        if(getConfigSettings().isApiEnabled()) {
            if(getConfigSettings().isApiProtectedByPassword()) {
                if(asyncUnbanPreprocessEvent.getApiPassword() != null && asyncUnbanPreprocessEvent.getApiPassword().equalsIgnoreCase(getFileAccessor().getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
                }
            } else {
                Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
            }
        }

        if(asyncUnbanPreprocessEvent.isCancelled()) return;

        if(unbanInitiator instanceof Player) {
            if(CooldownsManager.playerHasCooldown(((Player) unbanInitiator).getPlayer(), "unban")) {
                CooldownsManager.notifyAboutCooldown(((Player) unbanInitiator).getPlayer(), "unban");
                asyncUnbanPreprocessEvent.setCancelled(true);
                return;
            } else {
                CooldownsManager.setCooldown(((Player) unbanInitiator).getPlayer(), "unban");
            }
        }

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
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                        break;
                    }
                    case MYSQL: {
                        break;
                    }
                    case H2: {
                        break;
                    }
                }
                getBanContainerManager().removeFromBanContainer("-n", player);
                if(unbanReason == null || unbanReason.equalsIgnoreCase("")) {
                    if(announceUnban) {
                        Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
                    }
                } else {
                    if(announceUnban) {
                        Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unbanReason)));
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
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                    } catch (NullPointerException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player)));
                    }
                    if(unbanReason == null || unbanReason.equalsIgnoreCase("")) {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
                        }
                    } else {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unbanReason)));
                        }
                    }
                    break;
                }
                case MYSQL: {
                    break;
                }
                case H2: {
                    break;
                }
                default: {
                    try {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                    } catch (NullPointerException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player)));
                    }
                    if(unbanReason == null || unbanReason.equalsIgnoreCase("")) {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
                        }
                    } else {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unbanReason)));
                        }
                    }
                    break;
                }
            }
        }
    }

    public void preformUnban(OfflinePlayer player, String unbanReason) { //NEW
        AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(player, unbanReason, getConfigSettings().isApiEnabled());

        if(getConfigSettings().isApiEnabled()) {
            if(getConfigSettings().isApiProtectedByPassword()) {
                if(asyncUnbanPreprocessEvent.getApiPassword() != null && asyncUnbanPreprocessEvent.getApiPassword().equalsIgnoreCase(getFileAccessor().getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
                }
            } else {
                Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
            }
        }

        if(asyncUnbanPreprocessEvent.isCancelled()) return;

        unbanReason = "The Ban time has expired";

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            try {
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                        break;
                    }
                    case MYSQL: {
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
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                    } catch (NullPointerException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player.getName())));
                    }
                    break;
                }
                case MYSQL: {
                    break;
                }
                case H2: {
                    break;
                }
                default: {
                    try {
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                    } catch (NullPointerException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player.getName())));
                    }
                    break;
                }
            }
        }
    }

    public void preformUnban(String player, String unbanReason) { //Another

        AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(player, unbanReason, getConfigSettings().isApiEnabled());

        if(getConfigSettings().isApiEnabled()) {
            if(getConfigSettings().isApiProtectedByPassword()) {
                if(asyncUnbanPreprocessEvent.getApiPassword() != null && asyncUnbanPreprocessEvent.getApiPassword().equalsIgnoreCase(getFileAccessor().getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
                }
            } else {
                Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
            }
        }

        if(asyncUnbanPreprocessEvent.isCancelled()) return;

        unbanReason = "The Ban time has expired";

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            try {
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                        break;
                    }
                    case MYSQL: {
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
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                    } catch (NullPointerException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player)));
                    }
                    break;
                }
                case MYSQL: {
                    break;
                }
                case H2: {
                    break;
                }
                default: {
                    try {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                    } catch (NullPointerException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unban player %player%".replace("%player%", player)));
                    }
                    break;
                }
            }
        }
    }

    public void preformGlobalUnban(CommandSender initiator, boolean announceUnban) {

        String initiatorName = null;
        if(initiator instanceof Player) {
            initiatorName = ((Player) initiator).getPlayerListName();
            if(CooldownsManager.playerHasCooldown(((Player) initiator).getPlayer(), "unbanall")) {
                CooldownsManager.notifyAboutCooldown(((Player) initiator).getPlayer(), "unbanall");
                return;
            } else {
                CooldownsManager.setCooldown(((Player) initiator).getPlayer(), "unbanall");
            }
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
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
                getSQLiteManager().clearBans();
                break;
            }
            case MYSQL: {
                break;
            }
            case H2: {
                break;
            }
        }

        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unbanall.success").replace("%1$f", String.valueOf(count))));

        if(announceUnban) {
            Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unbanall.broadcast-message").replace("%1$f", initiatorName)));
        }

    }

}
