package by.alis.functionalservercontrol.spigot.Additional.Misc;

import by.alis.functionalservercontrol.api.Enums.StatsType;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.isTextNotNull;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

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
