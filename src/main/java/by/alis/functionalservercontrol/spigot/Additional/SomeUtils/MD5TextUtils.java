package by.alis.functionalservercontrol.spigot.Additional.SomeUtils;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;

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

}
