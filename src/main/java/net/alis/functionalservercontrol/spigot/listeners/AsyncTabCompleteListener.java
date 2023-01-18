package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.spigot.managers.GlobalCommandManager;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class AsyncTabCompleteListener implements Listener {
    private final GlobalCommandManager commandManager = new GlobalCommandManager();
    @EventHandler
    public void onTabComplete(AsyncTabCompleteEvent event) {
        if (event.getSender() instanceof Player) {
            event.setCompletions(this.commandManager.getNewCompletions(event.getSender(), event.getBuffer().split(" ")[0].toLowerCase(), event.getCompletions()));
        }
    }

}
