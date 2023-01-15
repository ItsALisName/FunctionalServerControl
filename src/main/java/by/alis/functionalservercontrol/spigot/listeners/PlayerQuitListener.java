package by.alis.functionalservercontrol.spigot.listeners;

import by.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import by.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.managers.CheatCheckerManager.getCheatCheckerManager;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TaskManager.preformAsync(() -> {
            TemporaryCache.unsetOnlinePlayerName(player);
            TemporaryCache.unsetOnlineIps(player);
            TemporaryCache.unsetClientBrand(player);
            if(getConfigSettings().isCheatCheckFunctionEnabled()) {
                getCheatCheckerManager().preformActionOnQuit(player);
            }
        });
    }

}
