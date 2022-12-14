package by.alis.functionalbans.spigot.Listeners;

import by.alis.functionalbans.spigot.Additional.Other.TemporaryCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static by.alis.functionalbans.databases.StaticBases.getSQLiteManager;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoinToServer(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getSQLiteManager().insertIntoAllPlayers(player.getName(), player.getUniqueId(), player.getAddress().getAddress().getHostAddress());
        getSQLiteManager().updateAllPlayers(player);
        TemporaryCache.setOnlinePlayerNames(player);
    }
}
