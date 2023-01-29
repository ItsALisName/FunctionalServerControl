package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.managers.CheatCheckerManager.getCheatCheckerManager;

public class EntityDamagesListener implements Listener {

    @EventHandler
    public void onEntitiesDamage(EntityDamageByEntityEvent event) {
        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
                if(getCheatCheckerManager().isPlayerChecking(FunctionalPlayer.get(event.getDamager().getName()))) {
                    if(getConfigSettings().isPreventIflictDamageDuringCheatCheck()) event.setCancelled(true);
                    return;
                }
                if(getCheatCheckerManager().isPlayerChecking(FunctionalPlayer.get(event.getEntity().getName()))) {
                    if(getConfigSettings().isPreventTakingDamageDuringCheatCheck()) event.setCancelled(true);
                }
            }
        }
    }

}
