package by.alis.functionalservercontrol.spigot.Listeners;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

import static by.alis.functionalservercontrol.spigot.Additional.ConsoleFilter.StaticConsoleFilterHelper.getConsoleFilterHelper;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getCommandLimiterSettings;
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;

public class ConsoleSendCommandListener implements Listener {

    @EventHandler
    public void onConsoleSendCommand(ServerCommandEvent event) {
        CommandSender sender = event.getSender();
        String command = event.getCommand();
        if(getCommandLimiterSettings().isFunctionEnabled()) {
            if(getCommandLimiterSettings().isConsoleBlockedCommandsUseAsWhiteList()) {
                if (getCommandLimiterSettings().getCheckMode().equalsIgnoreCase("first_arg")) {
                    if(!getCommandLimiterSettings().getConsoleBlockedCommands().contains("/" + command.split(" ")[0])) {
                        sender.sendMessage(setColors(getCommandLimiterSettings().getConsoleCommandsDenyMessage()));
                        event.setCancelled(true);
                        return;
                    }
                }
                if(getCommandLimiterSettings().getCheckMode().equalsIgnoreCase("all_args")) {
                    if(!getCommandLimiterSettings().getConsoleBlockedCommands().contains("/" + command)) {
                        sender.sendMessage(setColors(getCommandLimiterSettings().getConsoleCommandsDenyMessage()));
                        event.setCancelled(true);
                        return;
                    }
                }
            } else {
                if (getCommandLimiterSettings().getCheckMode().equalsIgnoreCase("first_arg")) {
                    if(getCommandLimiterSettings().getConsoleBlockedCommands().contains("/" + command.split(" ")[0])) {
                        sender.sendMessage(setColors(getCommandLimiterSettings().getConsoleCommandsDenyMessage()));
                        event.setCancelled(true);
                        return;
                    }
                } else {
                    if(getCommandLimiterSettings().getConsoleBlockedCommands().contains("/" + command)) {
                        sender.sendMessage(setColors(getCommandLimiterSettings().getConsoleCommandsDenyMessage()));
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
        if(getConsoleFilterHelper().getPluginCommands().contains("/" + command.split(" ")[0])) {
            sender.sendMessage(setColors("&e[FunctionalServerControl | Log] Console used the command: &6%command%".replace("%command%", "/" + event.getCommand())));
        }
    }

}
