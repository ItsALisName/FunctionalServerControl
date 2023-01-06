package by.alis.functionalservercontrol.spigot.Additional.Misc.Reflect;

import by.alis.functionalservercontrol.spigot.Additional.Misc.OtherUtils;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static by.alis.functionalservercontrol.spigot.Additional.Misc.OtherUtils.getNmsClass;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;

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
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
            try {
                String serverVersion = OtherUtils.getServerPackageName(Bukkit.getServer());
                if (serverVersion.equalsIgnoreCase("v1_12_R1") ||
                        serverVersion.equalsIgnoreCase("v1_12_R2")) {
                    Object iChatBaseComponent = getNmsClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(ChatColor.translateAlternateColorCodes('&', message));
                    Object chatMessageType = getNmsClass("ChatMessageType").getField("GAME_INFO").get(null);
                    Object packetPlayOutChat = getNmsClass("PacketPlayOutChat").getConstructor(new Class[] { getNmsClass("IChatBaseComponent"), getNmsClass("ChatMessageType") }).newInstance(iChatBaseComponent, chatMessageType);
                    Object playerHandle = player.getClass().getMethod("getHandle").invoke(player);
                    Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
                    playerConnection.getClass().getMethod("sendPacket", getNmsClass("Packet")).invoke(playerConnection, packetPlayOutChat);
                } else if (serverVersion.equalsIgnoreCase("v1_9_R1") ||
                        serverVersion.equalsIgnoreCase("v1_9_R2") ||
                        serverVersion.equalsIgnoreCase("v1_10_R1") ||
                        serverVersion.equalsIgnoreCase("v1_11_R1")) {
                    Object iChatBaseComponent = getNmsClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(ChatColor.translateAlternateColorCodes('&', message));
                    Object packetPlayOutChat = getNmsClass("PacketPlayOutChat").getConstructor(new Class[] { getNmsClass("IChatBaseComponent"), byte.class }).newInstance(iChatBaseComponent, (byte) 2);
                    Object playerHandle = player.getClass().getMethod("getHandle").invoke(player);
                    Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
                    playerConnection.getClass().getMethod("sendPacket", getNmsClass("Packet")).invoke(playerConnection, packetPlayOutChat);
                } else if (serverVersion.equalsIgnoreCase("v1_8_R2") ||
                        serverVersion.equalsIgnoreCase("v1_8_R3")) {
                    Object iChatBaseComponent = getNmsClass("IChatBaseComponent$ChatSerializer").getMethod("a", String.class).invoke(null, "{'text': '" + message + "'}");
                    Object packetPlayerOutChat = getNmsClass("PacketPlayOutChat").getConstructor(new Class[] { getNmsClass("IChatBaseComponent"), byte.class }).newInstance(iChatBaseComponent, (byte) 2);
                    Object playerHandle = player.getClass().getMethod("getHandle").invoke(player);
                    Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
                    playerConnection.getClass().getMethod("sendPacket", getNmsClass("Packet")).invoke(playerConnection, packetPlayerOutChat);
                } else {
                    Object iChatBaseComponent = getNmsClass("ChatSerializer").getMethod("a", String.class).invoke(null, "{'text': '" + message + "'}");
                    Object packetPlayOutChat = getNmsClass("PacketPlayOutChat").getConstructor(new Class[] { getNmsClass("IChatBaseComponent"), byte.class }).newInstance(iChatBaseComponent, (byte) 2);
                    Object playerHandle = player.getClass().getMethod("getHandle").invoke(player);
                    Object playerConnection = playerHandle.getClass().getField("playerConnection").get(playerHandle);
                    playerConnection.getClass().getMethod("sendPacket", getNmsClass("Packet")).invoke(playerConnection, packetPlayOutChat);
                }
            } catch (IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException|NoSuchMethodException|SecurityException|ClassNotFoundException|InstantiationException|NoSuchFieldException e) {
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] An error occurred while trying to send the Actionbar to the player, are you using an old version of Minecraft server?"));
            }
        });
    }

}
