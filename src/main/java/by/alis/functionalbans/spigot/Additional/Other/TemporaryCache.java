package by.alis.functionalbans.spigot.Additional.Other;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class TemporaryCache {

    private HashMap<OfflinePlayer, CommandSender> unsafeBannedPlayers = new HashMap<>();
    private HashMap<OfflinePlayer, CommandSender> unsafeMutedPlayers = new HashMap<>();


    public HashMap<OfflinePlayer, CommandSender> getUnsafeMutedPlayers() {
        return unsafeMutedPlayers;
    }

    public void setUnsafeMutedPlayers(OfflinePlayer whoBanned, CommandSender initiator) {
        this.unsafeMutedPlayers.put(whoBanned, initiator);
    }

    public HashMap<OfflinePlayer, CommandSender> getUnsafeBannedPlayers() {
        return unsafeBannedPlayers;
    }
    public void setUnsafeBannedPlayers(OfflinePlayer whoMuted, CommandSender initiator) {
        this.unsafeBannedPlayers.put(whoMuted, initiator);
    }
}
