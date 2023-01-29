package net.alis.functionalservercontrol.spigot.additional.misc;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;

import java.util.*;

public class TemporaryCache {
    private static final List<String> onlinePlayerNames = new ArrayList<>();
    private static final Map<FID, String> onlineIps = new HashMap<>();
    private static final List<String> checkingPlayersNames = new ArrayList<>();
    private static final HashMap<FID, Integer> chatDelays = new HashMap<>();
    private static final HashMap<FID, String> repeatingMessages = new HashMap<>();


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
    public static void setOnlinePlayerNames(FunctionalPlayer player) {
        TemporaryCache.onlinePlayerNames.add(player.nickname());
    }

    /**
     * Removes a specific name from the list
     * @param player Player whose name will be removed
     */
    public static void unsetOnlinePlayerName(FunctionalPlayer player) {
        try {
            onlinePlayerNames.remove(player.nickname());
        } catch (NullPointerException ignored) {}
    }

    public static Map<FID, String> getOnlineIps() {
        return onlineIps;
    }

    public static void setOnlineIps(FunctionalPlayer player) {
        TemporaryCache.onlineIps.put(player.getFunctionalId(), player.address());
    }

    public static void unsetOnlineIps(FunctionalPlayer player) {
        TemporaryCache.onlineIps.remove(player.getFunctionalId());
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
    public static HashMap<FID, Integer> getChatDelays() {
        return chatDelays;
    }
    public static void addChatDelay(FunctionalPlayer player, int time) {
        chatDelays.put(player.getFunctionalId(), time);
    }
    public static HashMap<FID, String> getRepeatingMessages() {
        return repeatingMessages;
    }
    public static void addRepeatingMessage(FunctionalPlayer player, String message) {
        repeatingMessages.put(player.getFunctionalId(), message);
    }
}
