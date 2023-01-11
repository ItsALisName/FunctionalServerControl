package by.alis.functionalservercontrol.spigot.listeners;

import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.managers.CheatCheckerManager.getCheatCheckerManager;

public class EntityDamagesListener implements Listener {

    @EventHandler
    public void onEntitiesDamage(EntityDamageByEntityEvent event) {
        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
                Player player = (Player) event.getEntity();
                Player damager = (Player) event.getDamager();
                Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                    if(getCheatCheckerManager().isPlayerChecking(damager)) {
                        if(getConfigSettings().isPreventIflictDamageDuringCheatCheck()) event.setCancelled(true);
                        return;
                    }
                    if(getCheatCheckerManager().isPlayerChecking(player)) {
                        if(getConfigSettings().isPreventTakingDamageDuringCheatCheck()) event.setCancelled(true);
                    }
                });
            }
        }
    }

}
