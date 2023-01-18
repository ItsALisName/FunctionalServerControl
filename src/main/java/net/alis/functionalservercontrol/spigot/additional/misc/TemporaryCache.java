package net.alis.functionalservercontrol.spigot.additional.misc;

import org.bukkit.entity.Player;

import java.util.*;

public class TemporaryCache {
    private static final List<String> onlinePlayerNames = new ArrayList<>();
    private static final Map<Player, String> onlineIps = new HashMap<>();
    private static final List<String> checkingPlayersNames = new ArrayList<>();
    private static final HashMap<Player, String> clientBrands = new HashMap<>();
    private static final HashMap<UUID, Integer> chatDelays = new HashMap<>();
    private static final HashMap<UUID, String> repeatingMessages = new HashMap<>();


    /**
     * Used to get a list of names of online players
     * @return List of online player names
     */
    public static List<String> getOnlinePlayerNames() {
        return onlinePlayerNames;
    }

    /**
     * Expands the list of names of online players
     * @param player The player whose name will be added
     */
    public static void setOnlinePlayerNames(Player player) {
        TemporaryCache.onlinePlayerNames.add(player.getName());
    }

    /**
     * Removes a specific name from the list
     * @param player Player whose name will be removed
     */
    public static void unsetOnlinePlayerName(Player player) {
        try {
            onlinePlayerNames.remove(player.getName());
        } catch (NullPointerException ignored) {}
    }

    public static Map<Player, String> getOnlineIps() {
        return onlineIps;
    }

    public static void setOnlineIps(Player player) {
        TemporaryCache.onlineIps.put(player, player.getAddress().getAddress().getHostAddress());
    }

    public static void unsetOnlineIps(Player player) {
        TemporaryCache.onlineIps.remove(player);
    }

    public static List<String> getCheckingPlayersNames() {
        return checkingPlayersNames;
    }
    public static void setCheckingPlayersNames(String name) {
        checkingPlayersNames.add(name);
    }
    public static void unsetCheckingPlayersNames(String name) {
        checkingPlayersNames.remove(name);
    }

    public static HashMap<Player, String> getClientBrands() {
        return clientBrands;
    }
    public static void setClientBrands(Player player, String brand) {
        if(!clientBrands.containsKey(player)) {
            clientBrands.put(player, brand);
        }
    }
    public static void unsetClientBrand(Player player) {
        clientBrands.remove(player);
    }
    public static HashMap<UUID, Integer> getChatDelays() {
        return chatDelays;
    }
    public static void addChatDelay(Player player, int time) {
        chatDelays.put(player.getUniqueId(), time);
    }
    public static HashMap<UUID, String> getRepeatingMessages() {
        return repeatingMessages;
    }
    public static void addRepeatingMessage(Player player, String message) {
        repeatingMessages.put(player.getUniqueId(), message);
    }
}
