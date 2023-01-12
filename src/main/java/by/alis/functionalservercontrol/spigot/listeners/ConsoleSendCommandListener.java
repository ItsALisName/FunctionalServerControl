package by.alis.functionalservercontrol.spigot.listeners;

import by.alis.functionalservercontrol.spigot.managers.GlobalCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

import static by.alis.functionalservercontrol.spigot.additional.consolefilter.ConsoleFilterHelper.getConsoleFilterHelper;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getCommandLimiterSettings;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class ConsoleSendCommandListener implements Listener {

    @EventHandler
    public void onConsoleSendCommand(ServerCommandEvent event) {
        CommandSender sender = event.getSender();
        String command = event.getCommand();
        String[] args = command.split(" ");
        GlobalCommandManager commandManager = new GlobalCommandManager();
        if(commandManager.preventReloadCommand(sender, command)) {
            event.setCancelled(true);
            return;
        }
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

        if(getConfigSettings().isReplaceMinecraftCommand()) {
            event.setCommand(commandManager.replaceMinecraftCommand(command));
        }

        if(getConfigSettings().isPermissionsProtectionEnabled()) {
            if((command.startsWith("op") || command.startsWith("minecraft:op")) && args.length >= 2) {
                if(!getConfigSettings().getOpAllowedPlayers().contains(args[1])) {
                    event.setCancelled(true);
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.permissions-protection.player-cannot-be-operator").replace("%1$f", args[1])));
                    return;
                }
            }
        }

        if(getConsoleFilterHelper().getPluginCommands().contains("/" + command.split(" ")[0])) {
            sender.sendMessage(setColors("&e[FunctionalServerControl | Log] Console used the command: &6%command%".replace("%command%", "/" + event.getCommand())));
        }
    }

}
