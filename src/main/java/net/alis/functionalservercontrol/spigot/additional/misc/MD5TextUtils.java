package net.alis.functionalservercontrol.spigot.additional.misc;

import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.api.enums.StatsType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.isTextNotNull;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class MD5TextUtils {

    /**
     * Static class
     */
    public MD5TextUtils() {}

    public static TextComponent createHoverText(String inputText, String hoverText) {
        TextComponent component = new TextComponent(setColors(inputText));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(setColors(hoverText))));
        return component;
    }

    public static TextComponent createClickableRunCommandHoverText(String inputText, String hoverText, String action) {
        TextComponent component = new TextComponent(setColors(inputText));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(setColors(hoverText))));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, action));
        return component;
    }

    public static TextComponent createClickableRunCommandText(String inputText, String action) {
        TextComponent component = new TextComponent(setColors(inputText));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, action));
        return component;
    }

    public static TextComponent createClickableSuggestCommandText(String inputText, String action) {
        TextComponent component = new TextComponent(setColors(inputText));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, action));
        return component;
    }

    public static void sendActionBarText(Player player, String text) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
    }

    public static TextComponent stringToTextComponent(String param) {
        return new TextComponent(param);
    }

    @Contract("_, _ -> param1")
    public static TextComponent appendTwo(TextComponent param1, TextComponent param2) {
        param1.addExtra(param2);
        return param1;
    }

    public static TextComponent createPlayerInfoHoverText(String message, OfflinePlayer player) {
        String playerBans = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.STATS_BANS);
        String playerKicks = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.STATS_KICKS);
        String playerMutes = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
        String playerBlockedCommand = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.BLOCKED_COMMANDS_USED);
        String playerBlockedWords = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.BLOCKED_WORDS_USED);
        String playerAdvertiseAttempts = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.ADVERTISE_ATTEMPTS);
        return createHoverText(setColors(message), setColors(getFileAccessor().getLang().getString("other.notifications.player-info-hover-text").replace("%1$f", player.getName()).replace("%2$f", isTextNotNull(playerKicks) ? playerKicks : "0").replace("%3$f", isTextNotNull(playerBans) ? playerBans : "0").replace("%4$f", isTextNotNull(playerMutes) ? playerMutes : "0").replace("%5$f", isTextNotNull(playerBlockedCommand) ? playerBlockedCommand : "0").replace("%6$f", isTextNotNull(playerBlockedWords) ? playerBlockedWords : "0").replace("%7$f", isTextNotNull(playerAdvertiseAttempts) ? playerAdvertiseAttempts : "0")));
    }

    public static TextComponent addPunishmentButtons(Player admin, String who) {
        TextComponent component = new TextComponent(" ");
        if(admin.hasPermission("functionalservercontrol.use.no-reason")) {
            if(admin.hasPermission("functionalservercontrol.ban")) {
                component.addExtra(createClickableRunCommandText(setColors(getGlobalVariables().getButtonBan() + " "), "/ban " + who));
            }
            if(admin.hasPermission("functionalservercontrol.mute")) {
                component.addExtra(createClickableRunCommandText(setColors(getGlobalVariables().getButtonMute() + " "), "/mute " + who));
            }
            if(admin.hasPermission("functionalservercontrol.kick")) {
                component.addExtra(createClickableRunCommandText(setColors(getGlobalVariables().getButtonKick() + " "), "/kick " + who));
            }
            return component;
        } else {
            if(admin.hasPermission("functionalservercontrol.ban")) {
                component.addExtra(createClickableSuggestCommandText(setColors(getGlobalVariables().getButtonBan() + " "), "/ban " + who));
            }
            if(admin.hasPermission("functionalservercontrol.mute")) {
                component.addExtra(createClickableSuggestCommandText(setColors(getGlobalVariables().getButtonMute() + " "), "/mute " + who));
            }
            if(admin.hasPermission("functionalservercontrol.kick")) {
                component.addExtra(createClickableSuggestCommandText(setColors(getGlobalVariables().getButtonKick() + " "), "/kick " + who));
            }
            return component;
        }
    }

    public static TextComponent addPardonButtons(Player admin, String who) {
        TextComponent component = new TextComponent(" ");
        if(admin.hasPermission("functionalservercontrol.use.no-reason")) {
            if(admin.hasPermission("functionalservercontrol.unban")) {
                component.addExtra(createClickableRunCommandText(setColors(getGlobalVariables().getButtonUnban() + " "), "/unban " + who));
            }
            if(admin.hasPermission("functionalservercontrol.unmute")) {
                component.addExtra(createClickableRunCommandText(setColors(getGlobalVariables().getButtonUnmute() + " "), "/unmute " + who));
            }
            return component;
        } else {
            if(admin.hasPermission("functionalservercontrol.unban")) {
                component.addExtra(createClickableSuggestCommandText(setColors(getGlobalVariables().getButtonUnban() + " "), "/unban " + who));
            }
            if(admin.hasPermission("functionalservercontrol.unmute")) {
                component.addExtra(createClickableSuggestCommandText(setColors(getGlobalVariables().getButtonUnmute() + " "), "/unmute " + who));
            }
            return component;
        }
    }

}
