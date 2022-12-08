package by.alis.functionalbans.spigot.Listeners;

import by.alis.functionalbans.databases.SQLite.StaticSQL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class FirstPlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoinFirstTime(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        StaticSQL.getSQLManager().insertIntoAllPlayers(player.getName(), player.getUniqueId(), player.getAddress().getAddress().getHostAddress());
    }

}
