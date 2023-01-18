package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.spigot.managers.CheatCheckerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class PlayerInteractionListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(!event.isCancelled()) {
                if(getConfigSettings().isPreventInteractionDuringCheatCheck()) {
                    if(CheatCheckerManager.getCheatCheckerManager().isPlayerChecking(event.getPlayer())) event.setCancelled(true);
                }
            }
        }
    }

}
