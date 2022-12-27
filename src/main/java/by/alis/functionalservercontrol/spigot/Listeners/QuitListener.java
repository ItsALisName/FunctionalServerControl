package by.alis.functionalservercontrol.spigot.Listeners;

import by.alis.functionalservercontrol.spigot.Additional.Other.TemporaryCache;
import by.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.TabCompleteEvent;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Managers.CheatCheckerManager.getCheatCheckerManager;

public class QuitListener implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControlSpigot.getProvidingPlugin(FunctionalServerControlSpigot.class), () -> {
            TemporaryCache.unsetOnlinePlayerName(player);
            TemporaryCache.unsetOnlineIps(player);
            if(getConfigSettings().isCheatCheckFunctionEnabled()) {
                getCheatCheckerManager().preformActionOnQuit(player);
            }
        });
    }

    @EventHandler
    public void onLeaveByKick(PlayerKickEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControlSpigot.getProvidingPlugin(FunctionalServerControlSpigot.class), () -> {
            TemporaryCache.unsetOnlinePlayerName(player);
            TemporaryCache.unsetOnlineIps(player);
        });
    }

    public void o(TabCompleteEvent event) {

    }

}
