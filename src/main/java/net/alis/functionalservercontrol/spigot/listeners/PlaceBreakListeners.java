package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.spigot.managers.CheatCheckerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class PlaceBreakListeners implements Listener {

    @EventHandler
    public void onPlayerBlockBreak(BlockBreakEvent event) {
        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(!event.isCancelled()) {
                if(getConfigSettings().isPreventBlockBreakDuringCheatCheck()) {
                    if(CheatCheckerManager.getCheatCheckerManager().isPlayerChecking(event.getPlayer())) event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerBlockPlace(BlockPlaceEvent event) {
        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(!event.isCancelled()) {
                if(getConfigSettings().isPreventBlockPlaceDuringCheatCheck()) {
                    if(CheatCheckerManager.getCheatCheckerManager().isPlayerChecking(event.getPlayer())) event.setCancelled(true);
                }
            }
        }
    }

}
