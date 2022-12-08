package by.alis.functionalbans.spigot.Additional.Other;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class TemporaryCache {

    private final Map<OfflinePlayer, CommandSender> unsafeBannedPlayers = new HashMap<>();
    private final Map<OfflinePlayer, CommandSender> unsafeMutedPlayers = new HashMap<>();


    public Map<OfflinePlayer, CommandSender> getUnsafeMutedPlayers() {
        return unsafeMutedPlayers;
    }

    public void setUnsafeMutedPlayers(OfflinePlayer whoBanned, CommandSender initiator) {
        this.unsafeMutedPlayers.put(whoBanned, initiator);
    }

    public Map<OfflinePlayer, CommandSender> getUnsafeBannedPlayers() {
        return unsafeBannedPlayers;
    }
    public void setUnsafeBannedPlayers(OfflinePlayer whoMuted, CommandSender initiator) {
        this.unsafeBannedPlayers.put(whoMuted, initiator);
    }
}
