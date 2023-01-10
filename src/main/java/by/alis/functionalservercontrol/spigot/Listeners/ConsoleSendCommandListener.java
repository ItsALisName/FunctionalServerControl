package by.alis.functionalservercontrol.spigot.Listeners;

import by.alis.functionalservercontrol.spigot.Managers.GlobalCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

import static by.alis.functionalservercontrol.spigot.Additional.ConsoleFilter.StaticConsoleFilterHelper.getConsoleFilterHelper;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getCommandLimiterSettings;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

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
