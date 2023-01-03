package by.alis.functionalservercontrol.spigot.Additional.SomeUtils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;

import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;

public class AdventureApiUtils {


    public static Component createHoverShowText(String text, String hoverText) {
        Component hoverComponent = Component.text(setColors(hoverText));
        return Component.text(setColors(text)).hoverEvent(HoverEvent.showText(hoverComponent));
    }


}
