package by.alis.functionalservercontrol.spigot.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.managers.CheatCheckerManager.getCheatCheckerManager;

public class PlayerMovingListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(!event.isCancelled()) {
                if(getConfigSettings().isPreventMoveDuringCheatCheck()) {
                    if(getCheatCheckerManager().isPlayerChecking(event.getPlayer())) event.setCancelled(true);
                }
            }
        }
    }

}
