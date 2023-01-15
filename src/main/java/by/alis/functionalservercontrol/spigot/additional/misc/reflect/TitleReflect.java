package by.alis.functionalservercontrol.spigot.additional.misc.reflect;

import by.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;

public class TitleReflect {

    public static void sendTitle(Player player, String text, int fadeIn, int stay, int fadeOut) {
        TaskManager.preformAsync(() -> {
            try {
                Object chatTitle = MinecraftReflection.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + text + "\"}");

                Constructor<?> titleConstructor = MinecraftReflection.getNMSClass("PacketPlayOutTitle").getConstructor(MinecraftReflection.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], MinecraftReflection.getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
                Object packet = titleConstructor.newInstance(MinecraftReflection.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle, fadeIn, stay, fadeOut);
                Object handle = player.getClass().getMethod("getHandle").invoke(player);
                Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
                playerConnection.getClass().getMethod("sendPacket", MinecraftReflection.getNMSClass("Packet")).invoke(playerConnection, packet);
            } catch (Exception ex) {
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] An error occurred while trying to send the Actionbar to the player, are you using an old version of Minecraft server?"));
                Bukkit.getConsoleSender().sendMessage(setColors("&4============ DO NOT REPORT THIS TO ALis IF YOU HAVE AN OLD VERSION OF MINECRAFT SERVER ============"));
                ex.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(setColors("&4============ DO NOT REPORT THIS TO ALis IF YOU HAVE AN OLD VERSION OF MINECRAFT SERVER ============"));
            }
        });
    }

}
