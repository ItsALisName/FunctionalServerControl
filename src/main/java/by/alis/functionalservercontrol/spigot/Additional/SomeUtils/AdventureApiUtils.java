package by.alis.functionalservercontrol.spigot.Additional.SomeUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;

public class AdventureApiUtils {


    public static Component createHoverText(String text, String hoverText) {
        Component hoverComponent = Component.text(setColors(hoverText));
        return Component.text(setColors(text)).hoverEvent(HoverEvent.showText(hoverComponent));
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

}
