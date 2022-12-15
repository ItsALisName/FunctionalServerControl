package by.alis.functionalbans.spigot.Managers.Bans;

import by.alis.functionalbans.API.Spigot.Events.AsyncUnbanPreprocessEvent;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.ConsoleLanguages.LangEnglish;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.ConsoleLanguages.LangRussian;
import by.alis.functionalbans.spigot.Managers.CooldownsManager;
import by.alis.functionalbans.spigot.Managers.Files.FileAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

import static by.alis.functionalbans.databases.StaticBases.getSQLiteManager;
import static by.alis.functionalbans.spigot.Additional.Containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalbans.spigot.Managers.Bans.BanChecker.isPlayerBanned;

public class UnbanManager {

    private final FileAccessor accessor = new FileAccessor();
    private final BanManager banManager = new BanManager();

    public void preformUnban(OfflinePlayer player, CommandSender unbanInitiator, String unbanReason, boolean announceUnban) {
        String initiatorName = null;
        if(unbanInitiator instanceof Player) {
            initiatorName = ((Player) unbanInitiator).getPlayerListName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }
        if(!isPlayerBanned(player)) {
            unbanInitiator.sendMessage(setColors(this.accessor.getLang().getString("commands.unban.player-not-banned")).replace("%1$f", player.getName()));
            return;
        }

        AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(player, unbanInitiator, unbanReason, getConfigSettings().isApiEnabled());

        if(getConfigSettings().isApiEnabled()) {
            if(getConfigSettings().isApiProtectedByPassword()) {
                if(asyncUnbanPreprocessEvent.getApiPassword() != null && asyncUnbanPreprocessEvent.getApiPassword().equalsIgnoreCase(this.accessor.getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
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
                if(!getConfigSettings().isAllowedUnbanWithoutReason() && !unbanInitiator.hasPermission("functionalbans.use.no-reason")) {
                    unbanInitiator.sendMessage(setColors(this.accessor.getLang().getString("other.no-reason")));
                    asyncUnbanPreprocessEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(!announceUnban) {
            if(!unbanInitiator.hasPermission("functionalbans.use.silently")) {
                unbanInitiator.sendMessage(setColors(this.accessor.getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
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
                    default: {
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                        break;
                    }
                }
                this.banManager.getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId()));
                if(unbanReason == "" || unbanReason == null) {
                    if(announceUnban) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName())));
                    }
                } else {
                    if(announceUnban) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", unbanReason)));
                    }
                }
                return;
            } catch (NullPointerException ignored) {
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                        break;
                    }
                    case "en_US": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                        break;
                    }
                    default: {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                        break;
                    }
                }
            }

        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    try {
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                    } catch (NullPointerException ignored) {
                        switch (getConfigSettings().getConsoleLanguageMode()) {
                            case "ru_RU": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                                break;
                            }
                            case "en_US": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                            default: {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                        }
                    }
                    if(unbanReason == "" || unbanReason == null) {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName())));
                        }
                    } else {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", unbanReason)));
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
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                    } catch (NullPointerException ignored) {
                        switch (getConfigSettings().getConsoleLanguageMode()) {
                            case "ru_RU": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                                break;
                            }
                            case "en_US": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                            default: {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                        }
                    }
                    if(unbanReason == "" || unbanReason == null) {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName())));
                        }
                    } else {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", unbanReason)));
                        }
                    }
                    break;
                }
            }
        }

    }

    public void preformUnban(String player, CommandSender unbanInitiator, String unbanReason, boolean announceUnban) {
        String initiatorName = null;
        if(!isPlayerBanned(player)) {
            unbanInitiator.sendMessage(setColors(this.accessor.getLang().getString("commands.unban.player-not-banned")).replace("%1$f", player));
            return;
        }
        if(unbanInitiator instanceof Player) {
            initiatorName = ((Player) unbanInitiator).getPlayerListName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }

        AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(player, unbanInitiator, unbanReason, getConfigSettings().isApiEnabled());

        if(getConfigSettings().isApiEnabled()) {
            if(getConfigSettings().isApiProtectedByPassword()) {
                if(asyncUnbanPreprocessEvent.getApiPassword() != null && asyncUnbanPreprocessEvent.getApiPassword().equalsIgnoreCase(this.accessor.getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
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
                if(!getConfigSettings().isAllowedUnbanWithoutReason() && !unbanInitiator.hasPermission("functionalbans.use.no-reason")) {
                    unbanInitiator.sendMessage(setColors(this.accessor.getLang().getString("other.no-reason")));
                    asyncUnbanPreprocessEvent.setCancelled(true);
                    return;
                }
            }
        }

        if(!announceUnban) {
            if(!unbanInitiator.hasPermission("functionalbans.use.silently")) {
                unbanInitiator.sendMessage(setColors(this.accessor.getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
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
                    default: {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                        break;
                    }
                }
                this.banManager.getBanContainerManager().removeFromBanContainer("-n", player);
                if(unbanReason == "" || unbanReason == null) {
                    if(announceUnban) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
                    }
                } else {
                    if(announceUnban) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unbanReason)));
                    }
                }
                return;
            } catch (NullPointerException ignored) {
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                        break;
                    }
                    case "en_US": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                        break;
                    }
                    default: {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                        break;
                    }
                }
            }

        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    try {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                    } catch (NullPointerException ignored) {
                        switch (getConfigSettings().getConsoleLanguageMode()) {
                            case "ru_RU": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                                break;
                            }
                            case "en_US": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                            default: {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                        }
                    }
                    if(unbanReason == "" || unbanReason == null) {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
                        }
                    } else {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unbanReason)));
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
                        switch (getConfigSettings().getConsoleLanguageMode()) {
                            case "ru_RU": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                                break;
                            }
                            case "en_US": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                            default: {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                        }
                    }
                    if(unbanReason == "" || unbanReason == null) {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
                        }
                    } else {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unbanReason)));
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
                if(asyncUnbanPreprocessEvent.getApiPassword() != null && asyncUnbanPreprocessEvent.getApiPassword().equalsIgnoreCase(this.accessor.getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
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
                    default: {
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                        break;
                    }
                }
                this.banManager.getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId()));
                return;
            } catch (NullPointerException ignored) {
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                        break;
                    }
                    case "en_US": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                        break;
                    }
                    default: {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                        break;
                    }
                }
            }

        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    try {
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                    } catch (NullPointerException ignored) {
                        switch (getConfigSettings().getConsoleLanguageMode()) {
                            case "ru_RU": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                                break;
                            }
                            case "en_US": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                            default: {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
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
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                    } catch (NullPointerException ignored) {
                        switch (getConfigSettings().getConsoleLanguageMode()) {
                            case "ru_RU": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                                break;
                            }
                            case "en_US": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                            default: {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                        }
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
                if(asyncUnbanPreprocessEvent.getApiPassword() != null && asyncUnbanPreprocessEvent.getApiPassword().equalsIgnoreCase(this.accessor.getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
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
                    default: {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                        break;
                    }
                }
                this.banManager.getBanContainerManager().removeFromBanContainer("-n", player);
                return;
            } catch (NullPointerException ignored) {
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                        break;
                    }
                    case "en_US": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                        break;
                    }
                    default: {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                        break;
                    }
                }
            }

        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    try {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                    } catch (NullPointerException ignored) {
                        switch (getConfigSettings().getConsoleLanguageMode()) {
                            case "ru_RU": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                                break;
                            }
                            case "en_US": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                            default: {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
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
                        switch (getConfigSettings().getConsoleLanguageMode()) {
                            case "ru_RU": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                                break;
                            }
                            case "en_US": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                            default: {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                        }
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
            if(!initiator.hasPermission("functionalbans.use.silently")) {
                initiator.sendMessage(setColors(this.accessor.getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
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

        initiator.sendMessage(setColors(this.accessor.getLang().getString("commands.unbanall.success").replace("%1$f", String.valueOf(count))));

        if(announceUnban) {
            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unbanall.broadcast-message").replace("%1$f", initiatorName)));
        }

    }

}
