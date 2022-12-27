package by.alis.functionalservercontrol.spigot.Listeners;

import by.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Managers.CheatCheckerManager.getCheatCheckerManager;

public class EntityDamagesListener implements Listener {

    @EventHandler
    public void onEntitiesDamage(EntityDamageByEntityEvent event) {
        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
                Player player = (Player) event.getEntity();
                Player damager = (Player) event.getDamager();
                Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControlSpigot.getProvidingPlugin(FunctionalServerControlSpigot.class), () -> {
                    if(getCheatCheckerManager().isPlayerChecking(damager)) {
                        if(getConfigSettings().isPreventIflictDamageDuringCheck()) event.setCancelled(true);
                        return;
                    }
                    if(getCheatCheckerManager().isPlayerChecking(player)) {
                        if(getConfigSettings().isPreventTakingDamageDuringCheck()) event.setCancelled(true);
                    }
                });
            }
        }
    }

}
