package by.alis.functionalservercontrol.spigot.Listeners;

import by.alis.functionalservercontrol.spigot.Managers.PlayerCommandManager;
import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class AsyncTabCompleteListener implements Listener {
    private final PlayerCommandManager commandManager = new PlayerCommandManager();
    @EventHandler
    public void onTabComplete(AsyncTabCompleteEvent event) {
        if (event.getSender() instanceof Player) {
            event.setCompletions(this.commandManager.getNewCompletions(event.getSender(), event.getBuffer().split(" ")[0].toLowerCase(), event.getCompletions()));
        }
    }

}
