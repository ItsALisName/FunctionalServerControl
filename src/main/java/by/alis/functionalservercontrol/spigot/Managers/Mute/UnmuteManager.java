package by.alis.functionalservercontrol.spigot.Managers.Mute;

import by.alis.functionalservercontrol.API.Spigot.Events.AsyncUnmutePreprocessEvent;
import by.alis.functionalservercontrol.spigot.Managers.CooldownsManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.*;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.*;
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;
import static by.alis.functionalservercontrol.spigot.Managers.Mute.MuteChecker.isPlayerMuted;
import static by.alis.functionalservercontrol.spigot.Managers.Mute.MuteManager.getMuteContainerManager;

public class UnmuteManager {

    public void preformUnmute(@NotNull OfflinePlayer player, @NotNull CommandSender unmuteInitiator, String unmuteReason, boolean announceUnmute) {
        String initiatorName = null;
        if(unmuteInitiator instanceof Player) {
            initiatorName = ((Player) unmuteInitiator).getPlayerListName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }
        if(!isPlayerMuted(player)) {
            unmuteInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.player-not-muted")).replace("%1$f", player.getName()));
            return;
        }

        AsyncUnmutePreprocessEvent asyncUnmutePreprocessEvent = new AsyncUnmutePreprocessEvent(player, unmuteInitiator, unmuteReason, getConfigSettings().isApiEnabled());

        if(getConfigSettings().isApiEnabled()) {
            if(getConfigSettings().isApiProtectedByPassword()) {
                if(asyncUnmutePreprocessEvent.getApiPassword() != null && asyncUnmutePreprocessEvent.getApiPassword().equalsIgnoreCase(getFileAccessor().getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
                }
            } else {
                Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
            }
        }

        if(asyncUnmutePreprocessEvent.isCancelled()) return;

        if(unmuteInitiator instanceof Player) {
            if(CooldownsManager.playerHasCooldown(((Player) unmuteInitiator).getPlayer(), "unmute")) {
                CooldownsManager.notifyAboutCooldown(((Player) unmuteInitiator).getPlayer(), "unmute");
                asyncUnmutePreprocessEvent.setCancelled(true);
                return;
            } else {
                CooldownsManager.setCooldown(((Player) unmuteInitiator).getPlayer(), "unmute");
            }
        }

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
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    getSQLiteManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
                    break;
                }
                case MYSQL: {}
                case H2: {}
            }
            getMuteContainerManager().removeFromMuteContainer("-u", String.valueOf(player.getUniqueId()));
            if(unmuteReason == null || unmuteReason.equalsIgnoreCase("")) {
                if(announceUnmute) {
                    Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName())));
                }
            } else {
                if(announceUnmute) {
                    Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", unmuteReason)));
                }
            }
            if(player.isOnline()) {
                if(getConfigSettings().isSendTitleWhenUnmuted()) {
                    Player onlinePlayer = player.getPlayer();
                    sendTitleWhenUnmuted(onlinePlayer, unmuteReason, initiatorName);
                }
            }
        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    try {
                        getSQLiteManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
                    } catch (NullPointerException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unmute player %player%".replace("%player%", player.getName())));
                    }
                    if(unmuteReason == null || unmuteReason.equalsIgnoreCase("")) {
                        if(announceUnmute) {
                            Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName())));
                        }
                    } else {
                        if(announceUnmute) {
                            Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", unmuteReason)));
                        }
                    }
                    if(player.isOnline()) {
                        if(getConfigSettings().isSendTitleWhenUnmuted()) {
                            Player onlinePlayer = player.getPlayer();
                            sendTitleWhenUnmuted(onlinePlayer, unmuteReason, initiatorName);
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

    public void preformUnmute(String player, CommandSender unmuteInitiator, String unmuteReason, boolean announceUnmute) {
        String initiatorName = null;
        if(!isPlayerMuted(player)) {
            unmuteInitiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.player-not-muted")).replace("%1$f", player));
            return;
        }
        if(unmuteInitiator instanceof Player) {
            initiatorName = ((Player) unmuteInitiator).getPlayerListName();
        } else if(unmuteInitiator instanceof ConsoleCommandSender) {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        } else {
            initiatorName = unmuteInitiator.getName();
        }

        AsyncUnmutePreprocessEvent asyncUnmutePreprocessEvent = new AsyncUnmutePreprocessEvent(player, unmuteInitiator, unmuteReason, getConfigSettings().isApiEnabled());

        if(getConfigSettings().isApiEnabled()) {
            if(getConfigSettings().isApiProtectedByPassword()) {
                if(asyncUnmutePreprocessEvent.getApiPassword() != null && asyncUnmutePreprocessEvent.getApiPassword().equalsIgnoreCase(getFileAccessor().getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
                }
            } else {
                Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
            }
        }

        if(asyncUnmutePreprocessEvent.isCancelled()) return;

        if(unmuteInitiator instanceof Player) {
            if(CooldownsManager.playerHasCooldown(((Player) unmuteInitiator).getPlayer(), "unmute")) {
                CooldownsManager.notifyAboutCooldown(((Player) unmuteInitiator).getPlayer(), "unmute");
                asyncUnmutePreprocessEvent.setCancelled(true);
                return;
            } else {
                CooldownsManager.setCooldown(((Player) unmuteInitiator).getPlayer(), "unmute");
            }
        }

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
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        getSQLiteManager().deleteFromNullMutedPlayers("-n", player);
                        break;
                    }
                    case MYSQL: {
                        break;
                    }
                    case H2: {
                        break;
                    }
                }
                getMuteContainerManager().removeFromMuteContainer("-n", player);
                if(unmuteReason == null || unmuteReason.equalsIgnoreCase("")) {
                    if(announceUnmute) {
                        Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
                    }
                } else {
                    if(announceUnmute) {
                        Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unmuteReason)));
                    }
                }
                return;
            } catch (NullPointerException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unmute player %player%".replace("%player%", player)));
            }

        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    try {
                        getSQLiteManager().deleteFromNullMutedPlayers("-n", player);
                    } catch (NullPointerException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unmute player %player%".replace("%player%", player)));
                    }
                    if(unmuteReason == null || unmuteReason.equalsIgnoreCase("")) {
                        if(announceUnmute) {
                            Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
                        }
                    } else {
                        if(announceUnmute) {
                            Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unmuteReason)));
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

    public void preformUnmute(OfflinePlayer player, String unmuteReason) { //NEW
        AsyncUnmutePreprocessEvent asyncUnmutePreprocessEvent = new AsyncUnmutePreprocessEvent(player, unmuteReason, getConfigSettings().isApiEnabled());

        if(getConfigSettings().isApiEnabled()) {
            if(getConfigSettings().isApiProtectedByPassword()) {
                if(asyncUnmutePreprocessEvent.getApiPassword() != null && asyncUnmutePreprocessEvent.getApiPassword().equalsIgnoreCase(getFileAccessor().getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
                }
            } else {
                Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
            }
        }

        if(asyncUnmutePreprocessEvent.isCancelled()) return;

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            try {
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        getSQLiteManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
                        break;
                    }
                    case MYSQL: {
                        break;
                    }
                    case H2: {
                        break;
                    }
                }
                getMuteContainerManager().removeFromMuteContainer("-u", String.valueOf(player.getUniqueId()));
                if(player.isOnline()) {
                    player.getPlayer().sendMessage(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.expired")));
                }
                return;
            } catch (NullPointerException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unmute player %player%".replace("%player%", player.getName())));
            }

        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    try {
                        getSQLiteManager().deleteFromMutedPlayers("-u", String.valueOf(player.getUniqueId()));
                        if(player.isOnline()) {
                            player.getPlayer().sendMessage(setColors(getFileAccessor().getLang().getString("other.player-notifying.when-muted.expired")));
                        }
                    } catch (NullPointerException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unmute player %player%".replace("%player%", player.getName())));
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

    public void preformUnmute(String player, String unmuteReason) { //Another

        AsyncUnmutePreprocessEvent asyncUnmutePreprocessEvent = new AsyncUnmutePreprocessEvent(player, unmuteReason, getConfigSettings().isApiEnabled());

        if(getConfigSettings().isApiEnabled()) {
            if(getConfigSettings().isApiProtectedByPassword()) {
                if(asyncUnmutePreprocessEvent.getApiPassword() != null && asyncUnmutePreprocessEvent.getApiPassword().equalsIgnoreCase(getFileAccessor().getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
                }
            } else {
                Bukkit.getPluginManager().callEvent(asyncUnmutePreprocessEvent);
            }
        }

        if(asyncUnmutePreprocessEvent.isCancelled()) return;

        unmuteReason = "The Ban time has expired";

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            try {
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        getSQLiteManager().deleteFromNullMutedPlayers("-n", player);
                        break;
                    }
                    case MYSQL: {
                        break;
                    }
                    case H2: {
                        break;
                    }
                }
                getMuteContainerManager().removeFromMuteContainer("-n", player);
                return;
            } catch (NullPointerException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unmute player %player%".replace("%player%", player)));
            }

        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    try {
                        getSQLiteManager().deleteFromNullMutedPlayers("-n", player);
                    } catch (NullPointerException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to unmute player %player%".replace("%player%", player)));
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

    public void preformGlobalUnmute(@Nullable CommandSender initiator, @Nullable boolean announceUnmute) {

        String initiatorName = null;
        if(initiator instanceof Player) {
            initiatorName = ((Player) initiator).getPlayerListName();
            if(CooldownsManager.playerHasCooldown(((Player) initiator).getPlayer(), "unmuteall")) {
                CooldownsManager.notifyAboutCooldown(((Player) initiator).getPlayer(), "unmuteall");
                return;
            } else {
                CooldownsManager.setCooldown(((Player) initiator).getPlayer(), "unmuteall");
            }
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
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

        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().clearMutes();
                break;
            }
            case MYSQL: {
                break;
            }
            case H2: {
                break;
            }
        }

        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmuteall.success").replace("%1$f", String.valueOf(count))));

        if(announceUnmute) {
            Bukkit.broadcastMessage(setColors(getFileAccessor().getLang().getString("commands.unmuteall.broadcast-message").replace("%1$f", initiatorName)));
        }

    }

    public void sendTitleWhenUnmuted(Player player, String reason, String initiatorName) {
        if(reason == null) {
            reason = getGlobalVariables().getDefaultReason();
        }
        player.sendTitle(setColors(getLanguage().getTitleWhenUnmuted()[0]), setColors(getLanguage().getTitleWhenUnmuted()[1].replace("%1$f", reason).replace("%2$f", initiatorName)), 10,70,20);
    }

}
