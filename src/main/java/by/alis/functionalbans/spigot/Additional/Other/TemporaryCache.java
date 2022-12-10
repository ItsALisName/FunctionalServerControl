package by.alis.functionalbans.spigot.Additional.Other;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.HashMap;

public class TemporaryCache {

    static HashMap<OfflinePlayer, CommandSender> unsafeBannedPlayers = new HashMap<>();
    static HashMap<OfflinePlayer, CommandSender> unsafeMutedPlayers = new HashMap<>();


    public HashMap<OfflinePlayer, CommandSender> getUnsafeMutedPlayers() {
        return unsafeMutedPlayers;
    }
    public static void setUnsafeMutedPlayers(OfflinePlayer whoBanned, CommandSender initiator) {
        unsafeMutedPlayers.put(whoBanned, initiator);
    }
    public static HashMap<OfflinePlayer, CommandSender> getUnsafeBannedPlayers() {
        return unsafeBannedPlayers;
    }
    public static void setUnsafeBannedPlayers(OfflinePlayer whoMuted, CommandSender initiator) {
        unsafeBannedPlayers.put(whoMuted, initiator);
    }
}
