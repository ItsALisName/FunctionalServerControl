package by.alis.functionalservercontrol.spigot.Additional.Misc;

import by.alis.functionalservercontrol.api.Enums.StatsType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.isTextNotNull;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class AdventureApiUtils {


    public static Component createHoverText(String text, String hoverText) {
        Component hoverComponent = Component.text(setColors(hoverText));
        return Component.text(setColors(text)).hoverEvent(HoverEvent.showText(hoverComponent));
    }

    public static String componentToString(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    public static Component createClickableRunCommandText(String inputText, String action) {
        Component component = Component.text(setColors(inputText));
        return component.clickEvent(ClickEvent.runCommand(action));
    }

    public static Component createClickableRunCommandHoverText(String inputText, String hoverText, String action) {
        Component component = Component.text(setColors(inputText));
        return component.hoverEvent(HoverEvent.showText(Component.text(setColors(hoverText)))).clickEvent(ClickEvent.runCommand(action));
    }

    public static Component createClickableSuggestCommandText(String inputText, String action) {
        Component component = Component.text(setColors(inputText));
        return component.clickEvent(ClickEvent.suggestCommand(action));
    }

    public static Component stringToComponent(String param) {
        return Component.text(param);
    }

    public static Component createPlayerInfoHoverText(String message, OfflinePlayer player) {
        String playerBans = "0", playerMutes = "0", playerKicks = "0", playerBlockedCommand = "0", playerBlockedWords = "0", playerAdvertiseAttempts = "0";
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                playerBans = getSQLiteManager().getPlayerStatsInfo(player, StatsType.Player.STATS_BANS);
                playerKicks = getSQLiteManager().getPlayerStatsInfo(player, StatsType.Player.STATS_KICKS);
                playerMutes = getSQLiteManager().getPlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
                playerBlockedCommand = getSQLiteManager().getPlayerStatsInfo(player, StatsType.Player.BLOCKED_COMMANDS_USED);
                playerBlockedWords = getSQLiteManager().getPlayerStatsInfo(player, StatsType.Player.BLOCKED_WORDS_USED);
                playerAdvertiseAttempts = getSQLiteManager().getPlayerStatsInfo(player, StatsType.Player.ADVERTISE_ATTEMPTS);
            }
            case H2: {
            }
        }
        return createHoverText(setColors(message), setColors(getFileAccessor().getLang().getString("other.notifications.player-info-hover-text").replace("%1$f", player.getName()).replace("%2$f", isTextNotNull(playerKicks) ? playerKicks : "0").replace("%3$f", isTextNotNull(playerBans) ? playerBans : "0").replace("%4$f", isTextNotNull(playerMutes) ? playerMutes : "0").replace("%5$f", isTextNotNull(playerBlockedCommand) ? playerBlockedCommand : "0").replace("%6$f", isTextNotNull(playerBlockedWords) ? playerBlockedWords : "0").replace("%7$f", isTextNotNull(playerAdvertiseAttempts) ? playerAdvertiseAttempts : "0")));
    }

    public static Component addPunishmentButtons(Player admin, String who) {
        Component component = Component.text(" ");
        if(admin.hasPermission("functionalservercontrol.use.no-reason")) {
            if(admin.hasPermission("functionalservercontrol.ban")) {
                component = component.append(createClickableRunCommandText(setColors(getGlobalVariables().getButtonBan() + " "), "/ban " + who));
            }
            if(admin.hasPermission("functionalservercontrol.mute")) {
                component = component.append(createClickableRunCommandText(setColors(getGlobalVariables().getButtonMute() + " "), "/mute " + who));
            }
            if(admin.hasPermission("functionalservercontrol.kick")) {
                component = component.append(createClickableRunCommandText(setColors(getGlobalVariables().getButtonKick() + " "), "/kick " + who));
            }
            return component;
        } else {
            if(admin.hasPermission("functionalservercontrol.ban")) {
                component = component.append(createClickableSuggestCommandText(setColors(getGlobalVariables().getButtonBan() + " "), "/ban " + who));
            }
            if(admin.hasPermission("functionalservercontrol.mute")) {
                component = component.append(createClickableSuggestCommandText(setColors(getGlobalVariables().getButtonMute() + " "), "/mute " + who));
            }
            if(admin.hasPermission("functionalservercontrol.kick")) {
                component = component.append(createClickableSuggestCommandText(setColors(getGlobalVariables().getButtonKick() + " "), "/kick " + who));
            }
            return component;
        }
    }

    public static Component addPardonButtons(Player admin, String who) {
        Component component = Component.text(" ");
        if(admin.hasPermission("functionalservercontrol.use.no-reason")) {
            if(admin.hasPermission("functionalservercontrol.unban")) {
                component = component.append(createClickableRunCommandText(setColors(getGlobalVariables().getButtonUnban() + " "), "/unban " + who));
            }
            if(admin.hasPermission("functionalservercontrol.unmute")) {
                component = component.append(createClickableRunCommandText(setColors(getGlobalVariables().getButtonUnmute() + " "), "/unmute " + who));
            }
            return component;
        } else {
            if(admin.hasPermission("functionalservercontrol.unban")) {
                component = component.append(createClickableSuggestCommandText(setColors(getGlobalVariables().getButtonUnban() + " "), "/unban " + who));
            }
            if(admin.hasPermission("functionalservercontrol.unmute")) {
                component = component.append(createClickableSuggestCommandText(setColors(getGlobalVariables().getButtonUnmute() + " "), "/unmute " + who));
            }
            return component;
        }
    }

}
