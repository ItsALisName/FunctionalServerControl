package by.alis.functionalbans.spigot.Listeners;

import by.alis.functionalbans.spigot.Additional.Other.TemporaryCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitListener implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        TemporaryCache.unsetOnlinePlayerName(event.getPlayer());
    }

    @EventHandler
    public void onLeaveByKick(PlayerKickEvent event) {
        TemporaryCache.unsetOnlinePlayerName(event.getPlayer());
    }

}
