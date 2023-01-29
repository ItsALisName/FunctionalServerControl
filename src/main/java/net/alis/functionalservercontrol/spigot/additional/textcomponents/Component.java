package net.alis.functionalservercontrol.spigot.additional.textcomponents;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;
import net.alis.functionalservercontrol.libraries.net.md_5.bungee.api.chat.BaseComponent;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.api.enums.StatsType;
import net.alis.functionalservercontrol.libraries.net.md_5.bungee.api.chat.ClickEvent;
import net.alis.functionalservercontrol.libraries.net.md_5.bungee.api.chat.HoverEvent;
import net.alis.functionalservercontrol.libraries.net.md_5.bungee.api.chat.TextComponent;
import net.alis.functionalservercontrol.libraries.net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.isTextNotNull;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class Component {

    /**
     * Static class
     */
    public Component() {
    }

    public static SimplifiedComponent createHoverText(String inputText, String hoverText) {
        return new SimplifiedComponent(setColors(inputText)).setHoverEvent(HoverEvent.Action.SHOW_TEXT, setColors(hoverText));
    }

    public static SimplifiedComponent createClickableRunCommandHoverText(String inputText, String hoverText, String action) {
        return new SimplifiedComponent(setColors(inputText)).setClickEvent(ClickEvent.Action.RUN_COMMAND, action).setHoverEvent(HoverEvent.Action.SHOW_TEXT, setColors(hoverText));
    }

    public static SimplifiedComponent createClickableRunCommandText(String inputText, String action) {
        return new SimplifiedComponent(setColors(inputText)).setClickEvent(ClickEvent.Action.RUN_COMMAND, action);
    }

    public static SimplifiedComponent createClickableSuggestCommandText(String inputText, String action) {
        return new SimplifiedComponent(setColors(inputText)).setClickEvent(ClickEvent.Action.SUGGEST_COMMAND, action);
    }

    public static SimplifiedComponent stringToSimplifiedComponent(String param) {
        return new SimplifiedComponent(setColors(param));
    }

    public static SimplifiedComponent createHoverOpenURLText(String input, String hoverText, String link) {
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, stringToMdText(hoverText));
        ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, link);
        return new SimplifiedComponent(setColors(input)).setClickEvent(clickEvent).setHoverEvent(hoverEvent);
    }

    public static Text stringToMdText(String input) {
        return new Text(setColors(input));
    }

    public static SimplifiedComponent createPlayerInfoHoverText(String message, OfflineFunctionalPlayer player) {
        String playerBans = BaseManager.getBaseManager().getPlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_BANS);
        String playerKicks = BaseManager.getBaseManager().getPlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_KICKS);
        String playerMutes = BaseManager.getBaseManager().getPlayerStatsInfo(player.getFunctionalId(), StatsType.Player.STATS_MUTES);
        String playerBlockedCommand = BaseManager.getBaseManager().getPlayerStatsInfo(player.getFunctionalId(), StatsType.Player.BLOCKED_COMMANDS_USED);
        String playerBlockedWords = BaseManager.getBaseManager().getPlayerStatsInfo(player.getFunctionalId(), StatsType.Player.BLOCKED_WORDS_USED);
        String playerAdvertiseAttempts = BaseManager.getBaseManager().getPlayerStatsInfo(player.getFunctionalId(), StatsType.Player.ADVERTISE_ATTEMPTS);
        return new SimplifiedComponent(setColors(message)).setHoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                setColors(getFileAccessor().getLang().getString("other.notifications.player-info-hover-text")
                        .replace("%1$f", player.nickname())
                        .replace("%2$f", isTextNotNull(playerKicks) ? playerKicks : "0")
                        .replace("%3$f", isTextNotNull(playerBans) ? playerBans : "0")
                        .replace("%4$f", isTextNotNull(playerMutes) ? playerMutes : "0")
                        .replace("%5$f", isTextNotNull(playerBlockedCommand) ? playerBlockedCommand : "0")
                        .replace("%6$f", isTextNotNull(playerBlockedWords) ? playerBlockedWords : "0")
                        .replace("%7$f", isTextNotNull(playerAdvertiseAttempts) ? playerAdvertiseAttempts : "0"))
        );
    }

    public static SimplifiedComponent addPunishmentButtons(FunctionalPlayer admin, String who) {
        SimplifiedComponent component = new SimplifiedComponent(" ");
        if (admin.hasPermission("functionalservercontrol.use.no-reason")) {
            if (admin.hasPermission("functionalservercontrol.ban")) {
                component.append(createClickableRunCommandText(setColors(getGlobalVariables().getButtonBan() + " "), "/ban " + who));
            }
            if (admin.hasPermission("functionalservercontrol.mute")) {
                component.append(createClickableRunCommandText(setColors(getGlobalVariables().getButtonMute() + " "), "/mute " + who));
            }
            if (admin.hasPermission("functionalservercontrol.kick")) {
                component.append(createClickableRunCommandText(setColors(getGlobalVariables().getButtonKick() + " "), "/kick " + who));
            }
            return component;
        } else {
            if (admin.hasPermission("functionalservercontrol.ban")) {
                component.append(createClickableSuggestCommandText(setColors(getGlobalVariables().getButtonBan() + " "), "/ban " + who));
            }
            if (admin.hasPermission("functionalservercontrol.mute")) {
                component.append(createClickableSuggestCommandText(setColors(getGlobalVariables().getButtonMute() + " "), "/mute " + who));
            }
            if (admin.hasPermission("functionalservercontrol.kick")) {
                component.append(createClickableSuggestCommandText(setColors(getGlobalVariables().getButtonKick() + " "), "/kick " + who));
            }
            return component;
        }
    }

    public static SimplifiedComponent addPardonButtons(FunctionalPlayer admin, String who) {
        SimplifiedComponent component = new SimplifiedComponent(" ");
        if (admin.hasPermission("functionalservercontrol.use.no-reason")) {
            if (admin.hasPermission("functionalservercontrol.unban")) {
                component.append(createClickableRunCommandText(setColors(getGlobalVariables().getButtonUnban() + " "), "/unban " + who));
            }
            if (admin.hasPermission("functionalservercontrol.unmute")) {
                component.append(createClickableRunCommandText(setColors(getGlobalVariables().getButtonUnmute() + " "), "/unmute " + who));
            }
            return component;
        } else {
            if (admin.hasPermission("functionalservercontrol.unban")) {
                component.append(createClickableSuggestCommandText(setColors(getGlobalVariables().getButtonUnban() + " "), "/unban " + who));
            }
            if (admin.hasPermission("functionalservercontrol.unmute")) {
                component.append(createClickableSuggestCommandText(setColors(getGlobalVariables().getButtonUnmute() + " "), "/unmute " + who));
            }
            return component;
        }
    }

    public static class SimplifiedComponent {
        private final List<HoverEvent> hoverEvents = new ArrayList<>();
        private final List<ClickEvent> clickEvents = new ArrayList<>();

        private TextComponent component;

        public SimplifiedComponent() {
            this.component = new TextComponent("");
        }

        public SimplifiedComponent(String text) {
            if(text == null) {
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] String cannot be null"));
                return;
            }
            this.component = new TextComponent(setColors(text));
        }

        public SimplifiedComponent(TextComponent text) {
            this.component = text;
        }

        public SimplifiedComponent(BaseComponent text) {
            if (text == null) {
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] BaseComponent cannot be null"));
                return;
            }
            this.component = new TextComponent(text);
        }

        public SimplifiedComponent(BaseComponent... texts) {
            if (texts == null) {
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] BaseComponents cannot be null"));
                return;
            }
            TextComponent components = new TextComponent("");
            for (BaseComponent c : texts) components.addExtra(c);
            this.component = components;
        }

        public SimplifiedComponent(SimplifiedComponent text) {
            if (text == null) {
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] SimplifiedComponent cannot be null"));
                return;
            }
            this.component = text.component;
        }

        public SimplifiedComponent(SimplifiedComponent... texts) {
            if (texts == null) {
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] SimplifiedComponents cannot be null"));
                return;
            }
            SimplifiedComponent component = new SimplifiedComponent("");
            for (SimplifiedComponent c : texts) component.append(c);
            this.component = component.component;
        }

        public SimplifiedComponent appendOnStart(String extra) {
            char[] chars = extra.toCharArray();
            TextComponent n = new TextComponent(setColors(extra));
            n.addExtra(component);
            this.component = n;
            return this;
        }

        public SimplifiedComponent setHoverEvent(HoverEvent.Action action, @NotNull String extra) {
            if (hasHoverEvents()) {
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Component already has hover event"));
                return this;
            }
            HoverEvent event = new HoverEvent(action, new Text(setColors(extra)));
            this.component.setHoverEvent(event);
            this.hoverEvents.add(event);
            return this;
        }

        public List<HoverEvent> getHoverEvents() {
            if (!hasHoverEvents()) {
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Component has no hover events"));
                return new ArrayList<HoverEvent>();
            }
            return this.hoverEvents;
        }

        public List<ClickEvent> getClickEvents() {
            if (!hasClickEvents()) {
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Component has no click events"));
                return new ArrayList<ClickEvent>();
            }
            return this.clickEvents;
        }

        public SimplifiedComponent setClickEvent(ClickEvent event) {
            this.component.setClickEvent(event);
            return this;
        }

        public SimplifiedComponent setHoverEvent(HoverEvent event) {
            this.component.setHoverEvent(event);
            return this;
        }

        public SimplifiedComponent duplicate() {
            return new SimplifiedComponent(component);
        }

        public boolean hasHoverEvents() {
            return !this.hoverEvents.isEmpty();
        }

        public boolean hasClickEvents() {
            return !this.clickEvents.isEmpty();
        }

        public SimplifiedComponent setClickEvent(ClickEvent.Action action, @NotNull String extra) {
            if (hasClickEvents()) {
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Component already has click event"));
                return this;
            }
            ClickEvent event = new ClickEvent(action, extra);
            this.component.setClickEvent(event);
            this.clickEvents.add(event);
            return this;
        }

        public SimplifiedComponent append(SimplifiedComponent extra) {
            this.component.addExtra(extra.component);
            return this;
        }

        public SimplifiedComponent append(String delimiter, SimplifiedComponent... extra) {
            SimplifiedComponent component = new SimplifiedComponent();
            for(SimplifiedComponent c : extra) component.append(delimiter).append(c);
            this.component.addExtra(component.component);
            return this;
        }

        public SimplifiedComponent append(String extra) {
            this.component.addExtra(extra);
            return this;
        }

        public SimplifiedComponent append(String delimiter, String... extra) {
            SimplifiedComponent component = new SimplifiedComponent();
            for(String s : extra) component.append(delimiter).append(s);
            this.component.addExtra(component.component);
            return this;
        }

        public SimplifiedComponent append(TextComponent extra) {
            this.component.addExtra(extra);
            return this;
        }

        public SimplifiedComponent append(String delimiter, TextComponent... extra) {
            SimplifiedComponent component1 = new SimplifiedComponent();
            for(TextComponent c : extra) component1.append(delimiter).append(c);
            this.component.addExtra(component1.component);
            return this;
        }

        public SimplifiedComponent appendOnStart(TextComponent extra) {
            extra.addExtra(component);
            this.component = extra;
            return this;
        }

        public SimplifiedComponent appendOnStart(SimplifiedComponent extra) {
            this.component = extra.append(component).get();
            return this;
        }

        /**
         * <b>Warning:</b> Not recommended for the main thread
         * @return Simplified component with correctly translated default(&, §) color codes
         */
        public SimplifiedComponent translateDefaultColorCodes() {
            if(getString().contains("&") || getString().contains("§")) this.component.setText(pasteColorCodes(getString()));
            return this;
        }

        public TextComponent get() {
            return this.component;
        }

        public String getString() {
            return this.component.getText();
        }

        private String pasteColorCodes(String param) {
            boolean is = false;
            StringBuilder builder = new StringBuilder(param);
            StringBuilder newBuilder = new StringBuilder();
            String code = "";
            for (int i = 0; i < param.length(); i++) {
                if (i + 1 < param.length() && Pattern.compile("§[0-9-a-z]").matcher(Character.toString(builder.charAt(i)) + Character.toString(builder.charAt(i + 1))).find()) {
                    code = Character.toString(builder.charAt(i)) + Character.toString(builder.charAt(i + 1));
                    is = true; continue;
                }
                if (i + 1 < param.length() && Pattern.compile("&[0-9-a-z]").matcher(Character.toString(builder.charAt(i)) + Character.toString(builder.charAt(i + 1))).find()) {
                    code = Character.toString(builder.charAt(i)) + Character.toString(builder.charAt(i + 1));
                    is = true; continue;
                }
                if(is) {is = false; continue;}
                newBuilder.append(code + Character.toString(builder.charAt(i)));
            }
            return newBuilder.toString();
        }
    }
}
