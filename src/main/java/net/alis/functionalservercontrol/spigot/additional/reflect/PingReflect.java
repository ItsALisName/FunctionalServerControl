package net.alis.functionalservercontrol.spigot.additional.reflect;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;

public class PingReflect {

    public static int getPing(Player player) {
        try {
            Object handle = MinecraftReflection.getHandle(player);
            Field pingField = handle.getClass().getField("ping");
            return pingField.getInt(handle);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] An error occurred while trying to get player ping, are you using an old version of Minecraft server?"));
            Bukkit.getConsoleSender().sendMessage(setColors("&4============ DO NOT REPORT THIS TO ALis IF YOU HAVE AN OLD VERSION OF MINECRAFT SERVER ============"));
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(setColors("&4============ DO NOT REPORT THIS TO ALis IF YOU HAVE AN OLD VERSION OF MINECRAFT SERVER ============"));
        }
        return 0;
    }

}
