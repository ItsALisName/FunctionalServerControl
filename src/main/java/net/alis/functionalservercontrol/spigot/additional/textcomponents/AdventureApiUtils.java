package net.alis.functionalservercontrol.spigot.additional.textcomponents;

import net.alis.functionalservercontrol.api.enums.StatsType;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.spigot.managers.file.SFAccessor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;

public class AdventureApiUtils {

    public static Component createHoverText(String text, String hoverText) {
        Component hoverComponent = Component.text(TextUtils.setColors(hoverText));
        return Component.text(TextUtils.setColors(text)).hoverEvent(HoverEvent.showText(hoverComponent));
    }

    public static String componentToString(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    public static Component createClickableRunCommandText(String inputText, String action) {
        Component component = Component.text(TextUtils.setColors(inputText));
        return component.clickEvent(ClickEvent.runCommand(action));
    }

    public static Component createClickableRunCommandHoverText(String inputText, String hoverText, String action) {
        Component component = Component.text(TextUtils.setColors(inputText));
        return component.hoverEvent(HoverEvent.showText(Component.text(TextUtils.setColors(hoverText)))).clickEvent(ClickEvent.runCommand(action));
    }

    public static Component createClickableSuggestCommandText(String inputText, String action) {
        Component component = Component.text(TextUtils.setColors(inputText));
        return component.clickEvent(ClickEvent.suggestCommand(action));
    }

    public static Component stringToComponent(String param) {
        return Component.text(param);
    }

    public static Component createPlayerInfoHoverText(String message, OfflinePlayer player) {
        String playerBans = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.STATS_BANS);
        String playerKicks = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.STATS_KICKS);
        String playerMutes = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.STATS_MUTES);
        String playerBlockedCommand = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.BLOCKED_COMMANDS_USED);
        String playerBlockedWords = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.BLOCKED_WORDS_USED);
        String playerAdvertiseAttempts = BaseManager.getBaseManager().getPlayerStatsInfo(player, StatsType.Player.ADVERTISE_ATTEMPTS);
        return createHoverText(TextUtils.setColors(message), TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.notifications.player-info-hover-text").replace("%1$f", player.getName()).replace("%2$f", TextUtils.isTextNotNull(playerKicks) ? playerKicks : "0").replace("%3$f", TextUtils.isTextNotNull(playerBans) ? playerBans : "0").replace("%4$f", TextUtils.isTextNotNull(playerMutes) ? playerMutes : "0").replace("%5$f", TextUtils.isTextNotNull(playerBlockedCommand) ? playerBlockedCommand : "0").replace("%6$f", TextUtils.isTextNotNull(playerBlockedWords) ? playerBlockedWords : "0").replace("%7$f", TextUtils.isTextNotNull(playerAdvertiseAttempts) ? playerAdvertiseAttempts : "0")));
    }

    public static Component createHoverOpenURLText(String input, String hoverText, String link) {
        Component component = Component.text(setColors(input));
        component = component.hoverEvent(HoverEvent.showText(stringToComponent(setColors(hoverText))))
                .clickEvent(ClickEvent.openUrl(link));
        return component;
    }

    public static Component addPunishmentButtons(Player admin, String who) {
        Component component = Component.text(" ");
        if(admin.hasPermission("functionalservercontrol.use.no-reason")) {
            if(admin.hasPermission("functionalservercontrol.ban")) {
                component = component.append(createClickableRunCommandText(TextUtils.setColors(getGlobalVariables().getButtonBan() + " "), "/ban " + who));
            }
            if(admin.hasPermission("functionalservercontrol.mute")) {
                component = component.append(createClickableRunCommandText(TextUtils.setColors(getGlobalVariables().getButtonMute() + " "), "/mute " + who));
            }
            if(admin.hasPermission("functionalservercontrol.kick")) {
                component = component.append(createClickableRunCommandText(TextUtils.setColors(getGlobalVariables().getButtonKick() + " "), "/kick " + who));
            }
            return component;
        } else {
            if(admin.hasPermission("functionalservercontrol.ban")) {
                component = component.append(createClickableSuggestCommandText(TextUtils.setColors(getGlobalVariables().getButtonBan() + " "), "/ban " + who));
            }
            if(admin.hasPermission("functionalservercontrol.mute")) {
                component = component.append(createClickableSuggestCommandText(TextUtils.setColors(getGlobalVariables().getButtonMute() + " "), "/mute " + who));
            }
            if(admin.hasPermission("functionalservercontrol.kick")) {
                component = component.append(createClickableSuggestCommandText(TextUtils.setColors(getGlobalVariables().getButtonKick() + " "), "/kick " + who));
            }
            return component;
        }
    }

    public static Component addPardonButtons(Player admin, String who) {
        Component component = Component.text(" ");
        if(admin.hasPermission("functionalservercontrol.use.no-reason")) {
            if(admin.hasPermission("functionalservercontrol.unban")) {
                component = component.append(createClickableRunCommandText(TextUtils.setColors(getGlobalVariables().getButtonUnban() + " "), "/unban " + who));
            }
            if(admin.hasPermission("functionalservercontrol.unmute")) {
                component = component.append(createClickableRunCommandText(TextUtils.setColors(getGlobalVariables().getButtonUnmute() + " "), "/unmute " + who));
            }
            return component;
        } else {
            if(admin.hasPermission("functionalservercontrol.unban")) {
                component = component.append(createClickableSuggestCommandText(TextUtils.setColors(getGlobalVariables().getButtonUnban() + " "), "/unban " + who));
            }
            if(admin.hasPermission("functionalservercontrol.unmute")) {
                component = component.append(createClickableSuggestCommandText(TextUtils.setColors(getGlobalVariables().getButtonUnmute() + " "), "/unmute " + who));
            }
            return component;
        }
    }

    public static class NoClassComponent {
        private Component component;

        public NoClassComponent() {
            this.component = Component.text("");
        }

        public NoClassComponent(String text) {
            this.component = Component.text(setColors(text));
        }

        public NoClassComponent(Component text) {
            this.component = text;
        }

        public NoClassComponent addExtra(AdventureApiUtils.NoClassComponent extra) {
            component = component.append(extra.component);
            return this;
        }

        public NoClassComponent addExtra(String extra) {
            component = component.append(stringToComponent(extra));
            return this;
        }

        public NoClassComponent addExtra(Component extra) {
            component = component.append(extra);
            return this;
        }

        public NoClassComponent addOnStart(String extra) {
            this.component = Component.text(extra).append(component);
            return this;
        }

        public Component get() {
            return this.component;
        }

        public String getString() {
            return componentToString(this.component);
        }

    }

}
