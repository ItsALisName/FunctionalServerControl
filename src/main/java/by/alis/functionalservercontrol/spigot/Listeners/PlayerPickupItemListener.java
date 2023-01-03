package by.alis.functionalservercontrol.spigot.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Managers.CheatCheckerManager.getCheatCheckerManager;

public class PlayerPickupItemListener implements Listener {

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(!event.isCancelled()) {
                if(getConfigSettings().isPreventBlockPlaceDuringCheck()) {
                    if (event.getEntity() instanceof Player) {
                        if (getCheatCheckerManager().isPlayerChecking(((Player) event.getEntity()).getPlayer())) event.setCancelled(true);
                    }
                }
            }
        }
    }

}
