package by.alis.functionalservercontrol.spigot.Listeners;

import by.alis.functionalservercontrol.spigot.Additional.Other.TemporaryCache;
import by.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import by.alis.functionalservercontrol.spigot.Managers.DupeIpManager;
import by.alis.functionalservercontrol.spigot.Managers.Mute.MuteManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoinToServer(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DupeIpManager.checkDupeIpOnJoin(player);
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControlSpigot.getProvidingPlugin(FunctionalServerControlSpigot.class), () -> {
            getSQLiteManager().insertIntoAllPlayers(player.getName(), player.getUniqueId(), player.getAddress().getAddress().getHostAddress());
            getSQLiteManager().updateAllPlayers(player);
            TemporaryCache.setOnlinePlayerNames(player);
            TemporaryCache.setOnlineIps(player);
            MuteManager muteManager = new MuteManager();
            muteManager.checkForNullMutedPlayer(player);
            muteManager.notifyAboutMuteOnJoin(player);
        });
    }
}
