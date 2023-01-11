package by.alis.functionalservercontrol.spigot.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.managers.CheatCheckerManager.getCheatCheckerManager;

public class PlayerInteractionListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(!event.isCancelled()) {
                if(getConfigSettings().isPreventInteractionDuringCheatCheck()) {
                    if(getCheatCheckerManager().isPlayerChecking(event.getPlayer())) event.setCancelled(true);
                }
            }
        }
    }

}
