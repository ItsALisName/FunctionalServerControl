package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.managers.CheatCheckerManager.getCheatCheckerManager;

public class PlayerPickupItemListener implements Listener {

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(!event.isCancelled()) {
                if(getConfigSettings().isPreventPickupItemDuringCheatCheck()) {
                    if (event.getEntity() instanceof Player) {
                        if (getCheatCheckerManager().isPlayerChecking(FunctionalPlayer.get(event.getEntity().getName()))) event.setCancelled(true);
                    }
                }
            }
        }
    }

}
