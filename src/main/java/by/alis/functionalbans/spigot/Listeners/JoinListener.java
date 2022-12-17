package by.alis.functionalbans.spigot.Listeners;

import by.alis.functionalbans.spigot.Additional.Other.TemporaryCache;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.DupeIpManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import static by.alis.functionalbans.databases.DataBases.getSQLiteManager;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoinToServer(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DupeIpManager.checkDupeIpOnJoin(player);
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
            getSQLiteManager().insertIntoAllPlayers(player.getName(), player.getUniqueId(), player.getAddress().getAddress().getHostAddress());
            getSQLiteManager().updateAllPlayers(player);
            TemporaryCache.setOnlinePlayerNames(player);
            TemporaryCache.setOnlineIps(player);
        });
    }
}
