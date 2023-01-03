package by.alis.functionalservercontrol.spigot.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

import static by.alis.functionalservercontrol.spigot.Additional.ConsoleFilter.StaticConsoleFilterHelper.getConsoleFilterHelper;
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;

public class ConsoleSendCommandListener implements Listener {

    @EventHandler
    public void onConsoleSendCommand(ServerCommandEvent event) {
        String command = event.getCommand();
        if(getConsoleFilterHelper().getPluginCommands().contains("/" + command.split(" ")[0])) {
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl | Log] Console used the command: &6%command%".replace("%command%", "/" + event.getCommand())));
        }
        if((command.contains("plugman") || command.contains("pg")) && event.getCommand().contains("FunctionalServerControl")) {
            event.getSender().sendMessage(setColors("&4&o[FunctionalServerControl] Reloading the plugin in this way is not allowed!"));
            event.getSender().sendMessage(setColors("&4&o[FunctionalServerControl]  Use a full server restart for the safety of the plugin and the server!"));
            event.setCancelled(true);
        }

    }

}
