package net.alis.functionalservercontrol.spigot.additional.reflect;

import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;

/**
 * Using for send message to players actionbar on old versions servers
 */
public class ActionBarReflect {

    /**
     * Full static class
     */
    public ActionBarReflect() {}

    /**
     * Sends a message to the actionbar using reflection
     * @param player the player to whom the message will be sent
     * @param message message to be sent
     */
    public static void sendActionBar(Player player, String message) {
        TaskManager.preformAsync(() -> {
            try {
                String serverVersion = MinecraftReflection.getVersion();
                if (serverVersion.equalsIgnoreCase("v1_12_R1") ||
                        serverVersion.equalsIgnoreCase("v1_12_R2")) {
                    Object iChatBaseComponent = MinecraftReflection.getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(ChatColor.translateAlternateColorCodes('&', message));
                    Object chatMessageType = MinecraftReflection.getNMSClass("ChatMessageType").getField("GAME_INFO").get(null);
                    Object packetPlayOutChat = MinecraftReflection.getNMSClass("PacketPlayOutChat").getConstructor(new Class[] { MinecraftReflection.getNMSClass("IChatBaseComponent"), MinecraftReflection.getNMSClass("ChatMessageType") }).newInstance(iChatBaseComponent, chatMessageType);
                    Object playerHandle = player.getClass().getMethod("getHandle").invoke(player);
                    Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
                    playerConnection.getClass().getMethod("sendPacket", MinecraftReflection.getNMSClass("AbstractPacket")).invoke(playerConnection, packetPlayOutChat);
                } else if (serverVersion.equalsIgnoreCase("v1_9_R1") ||
                        serverVersion.equalsIgnoreCase("v1_9_R2") ||
                        serverVersion.equalsIgnoreCase("v1_10_R1") ||
                        serverVersion.equalsIgnoreCase("v1_11_R1")) {
                    Object iChatBaseComponent = MinecraftReflection.getNMSClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(ChatColor.translateAlternateColorCodes('&', message));
                    Object packetPlayOutChat = MinecraftReflection.getNMSClass("PacketPlayOutChat").getConstructor(new Class[] { MinecraftReflection.getNMSClass("IChatBaseComponent"), byte.class }).newInstance(iChatBaseComponent, (byte) 2);
                    Object playerHandle = player.getClass().getMethod("getHandle").invoke(player);
                    Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
                    playerConnection.getClass().getMethod("sendPacket", MinecraftReflection.getNMSClass("AbstractPacket")).invoke(playerConnection, packetPlayOutChat);
                } else if (serverVersion.equalsIgnoreCase("v1_8_R2") ||
                        serverVersion.equalsIgnoreCase("v1_8_R3")) {
                    Object iChatBaseComponent = MinecraftReflection.getNMSClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class).invoke(null, "{'text': '" + message + "'}");
                    Object packetPlayerOutChat = MinecraftReflection.getNMSClass("PacketPlayOutChat").getConstructor(new Class[] { MinecraftReflection.getNMSClass("IChatBaseComponent"), byte.class }).newInstance(iChatBaseComponent, (byte) 2);
                    Object playerHandle = player.getClass().getMethod("getHandle").invoke(player);
                    Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
                    playerConnection.getClass().getMethod("sendPacket", MinecraftReflection.getNMSClass("AbstractPacket")).invoke(playerConnection, packetPlayerOutChat);
                } else {
                    Object iChatBaseComponent = MinecraftReflection.getNMSClass("ChatSerializer").getMethod("a", String.class).invoke(null, "{'text': '" + message + "'}");
                    Object packetPlayOutChat = MinecraftReflection.getNMSClass("PacketPlayOutChat").getConstructor(new Class[] { MinecraftReflection.getNMSClass("IChatBaseComponent"), byte.class }).newInstance(iChatBaseComponent, (byte) 2);
                    Object playerHandle = player.getClass().getMethod("getHandle").invoke(player);
                    Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
                    playerConnection.getClass().getMethod("sendPacket", MinecraftReflection.getNMSClass("AbstractPacket")).invoke(playerConnection, packetPlayOutChat);
                }
            } catch (IllegalAccessException | IllegalArgumentException | java.lang.reflect.InvocationTargetException | NoSuchMethodException | SecurityException | InstantiationException | NoSuchFieldException e) {
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] An error occurred while trying to send the Actionbar to the player, are you using an old version of Minecraft server?"));
                Bukkit.getConsoleSender().sendMessage(setColors("&4============ DO NOT REPORT THIS TO ALis IF YOU HAVE AN OLD VERSION OF MINECRAFT SERVER ============"));
                e.printStackTrace();
                Bukkit.getConsoleSender().sendMessage(setColors("&4============ DO NOT REPORT THIS TO ALis IF YOU HAVE AN OLD VERSION OF MINECRAFT SERVER ============"));
            }
        });
    }

}
