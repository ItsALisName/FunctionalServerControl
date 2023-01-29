package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.data.container.CraftPlayersContainer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.registerer.PlayerRegisterer;
import net.alis.functionalservercontrol.spigot.additional.tasks.PacketLimiter;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.managers.CheatCheckerManager.getCheatCheckerManager;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        FunctionalPlayer player = FunctionalPlayer.get(event.getPlayer().getName());
        TaskManager.preformAsync(() -> {
            TemporaryCache.unsetOnlinePlayerName(player);
            TemporaryCache.unsetOnlineIps(player);
            if(getConfigSettings().isCheatCheckFunctionEnabled()) {
                getCheatCheckerManager().preformActionOnQuit(player);
            }
            PacketLimiter.update().remove(player.getFunctionalId());
            new PlayerRegisterer(player).unregister();
            CraftPlayersContainer.Online.Out.remove(player);
        });
    }

}
