package by.alis.functionalservercontrol.spigot.additional.misc;

import by.alis.functionalservercontrol.api.enums.StatsType;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.isTextNotNull;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

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
        String playerBans = getBaseManager().getPlayerStatsInfo(player, StatsType.Player.STATS_BANS);
        String playerKicks = getBaseManager().getPlayerStatsInfo(player, StatsType.Player.STATS_KICKS);
        String playerMutes = getBaseManager().getPlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
        String playerBlockedCommand = getBaseManager().getPlayerStatsInfo(player, StatsType.Player.BLOCKED_COMMANDS_USED);
        String playerBlockedWords = getBaseManager().getPlayerStatsInfo(player, StatsType.Player.BLOCKED_WORDS_USED);
        String playerAdvertiseAttempts = getBaseManager().getPlayerStatsInfo(player, StatsType.Player.ADVERTISE_ATTEMPTS);
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
