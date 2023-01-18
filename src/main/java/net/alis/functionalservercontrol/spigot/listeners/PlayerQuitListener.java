package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.spigot.additional.tasks.PacketLimiterTask;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.managers.CheatCheckerManager.getCheatCheckerManager;

public class PlayerQuitListener implements Listener {

    PacketLimiterTask plCon;
    public PlayerQuitListener(PacketLimiterTask plCon) {
        this.plCon = plCon;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        TaskManager.preformAsync(() -> {
            TemporaryCache.unsetOnlinePlayerName(player);
            TemporaryCache.unsetOnlineIps(player);
            TemporaryCache.unsetClientBrand(player);
            if(getConfigSettings().isCheatCheckFunctionEnabled()) {
                getCheatCheckerManager().preformActionOnQuit(player);
            }
            this.plCon.packetMonitoringPlayers().remove(player);
        });
    }

}
