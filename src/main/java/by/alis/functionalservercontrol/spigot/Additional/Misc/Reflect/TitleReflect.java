package by.alis.functionalservercontrol.spigot.Additional.Misc.Reflect;

import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

import static by.alis.functionalservercontrol.spigot.Additional.Misc.OtherUtils.getNmsClass;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;

public class TitleReflect {

    public static void sendTitle(Player player, String text, int fadeIn, int stay, int fadeOut) {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
            try {
                Object chatTitle = getNmsClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + text + "\"}");

                Constructor<?> titleConstructor = getNmsClass("PacketPlayOutTitle").getConstructor(getNmsClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNmsClass("IChatBaseComponent"), int.class, int.class, int.class);
                Object packet = titleConstructor.newInstance(getNmsClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle, fadeIn, stay, fadeOut);
                Object handle = player.getClass().getMethod("getHandle").invoke(player);
                Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
                playerConnection.getClass().getMethod("sendPacket", getNmsClass("Packet")).invoke(playerConnection, packet);
            } catch (Exception ex) {
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] An error occurred while trying to send the Title Message to the player, are you using an old version of Minecraft server?"));
            }
        });
    }

}
