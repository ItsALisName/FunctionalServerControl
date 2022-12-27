package by.alis.functionalservercontrol.spigot.Additional.Other;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;

import static by.alis.functionalservercontrol.spigot.Additional.Other.TextUtils.setColors;

public class AdventureApiUtils {


    public static Component createHoverShowText(String text, String hoverText) {
        Component hoverComponent = Component.text(setColors(hoverText));
        return Component.text(setColors(text)).hoverEvent(HoverEvent.showText(hoverComponent));
    }


}
