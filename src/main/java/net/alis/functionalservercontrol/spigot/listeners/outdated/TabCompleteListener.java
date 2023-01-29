package net.alis.functionalservercontrol.spigot.listeners.outdated;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.spigot.managers.GlobalCommandManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;

public class TabCompleteListener implements Listener {
    private final GlobalCommandManager commandManager = new GlobalCommandManager();
    @EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        if (event.getSender() instanceof FunctionalPlayer) {
            event.setCompletions(this.commandManager.getNewCompletions(event.getSender(), event.getBuffer().split(" ")[0].toLowerCase(), event.getCompletions()));
        }
    }

}
