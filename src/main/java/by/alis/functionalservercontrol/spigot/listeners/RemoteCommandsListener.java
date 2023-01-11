package by.alis.functionalservercontrol.spigot.listeners;

import by.alis.functionalservercontrol.spigot.managers.GlobalCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.server.RemoteServerCommandEvent;

import static by.alis.functionalservercontrol.spigot.additional.consolefilter.StaticConsoleFilterHelper.getConsoleFilterHelper;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;

public class RemoteCommandsListener implements Listener {

    @EventHandler
    public void onRemoteCommandReceiving(RemoteServerCommandEvent event) {
        String command = event.getCommand();
        CommandSender sender = event.getSender();

        if(new GlobalCommandManager().preventReloadCommand(sender, command)) {
            event.setCancelled(true);
            return;
        }

        if(getConsoleFilterHelper().getPluginCommands().contains("/" + command.split(" ")[0])) {
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl | Log] %sender% used the command: &6%command%".replace("%command%", "/" + event.getCommand()).replace("%sender%", sender.getName())));
        }
        if((command.contains("plugman") || command.contains("pg")) && event.getCommand().contains("FunctionalServerControl")) {
            sender.sendMessage(setColors("&4&o[FunctionalServerControl] Reloading the plugin in this way is not allowed!"));
            sender.sendMessage(setColors("&4&o[FunctionalServerControl] Use a full server restart for the safety of the plugin and the server!"));
            event.setCancelled(true);
        }
    }

}
