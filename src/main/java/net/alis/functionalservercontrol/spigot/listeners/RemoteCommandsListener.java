package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.spigot.additional.consolefilter.ConsoleFilterHelper;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.GlobalCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import org.bukkit.event.server.RemoteServerCommandEvent;

public class RemoteCommandsListener implements Listener {

    @EventHandler
    public void onRemoteCommandReceiving(RemoteServerCommandEvent event) {
        String command = event.getCommand();
        CommandSender sender = event.getSender();

        if(new GlobalCommandManager().preventReloadCommand(sender, command)) {
            event.setCancelled(true);
            return;
        }

        if(ConsoleFilterHelper.getConsoleFilterHelper().getPluginCommands().contains("/" + command.split(" ")[0])) {
            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&e[FunctionalServerControlSpigot | Log] %sender% used the command: &6%command%".replace("%command%", "/" + event.getCommand()).replace("%sender%", sender.getName())));
        }
        if((command.contains("plugman") || command.contains("pg")) && event.getCommand().contains("FunctionalServerControlSpigot")) {
            sender.sendMessage(TextUtils.setColors("&4&o[FunctionalServerControlSpigot] Reloading the plugin in this way is not allowed!"));
            sender.sendMessage(TextUtils.setColors("&4&o[FunctionalServerControlSpigot] Use a full server restart for the safety of the plugin and the server!"));
            event.setCancelled(true);
        }
    }

}
