package net.alis.functionalservercontrol.spigot.listeners.outdated;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.managers.CheatCheckerManager.getCheatCheckerManager;


public class PlayerItemPickupEvent implements Listener {

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(!event.isCancelled()) {
                if(getConfigSettings().isPreventPickupItemDuringCheatCheck()) {
                    if (getCheatCheckerManager().isPlayerChecking(FunctionalPlayer.get(event.getPlayer().getUniqueId()))) event.setCancelled(true);
                }
            }
        }
    }

}
