package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.spigot.additional.consolefilter.ConsoleFilterHelper;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.GlobalCommandManager;
import net.alis.functionalservercontrol.spigot.managers.file.SFAccessor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getCommandLimiterSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

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
                if(!getCommandLimiterSettings().getConsoleBlockedCommands().contains("/" + command.split(" ")[0])) {
                    sender.sendMessage(TextUtils.setColors(getCommandLimiterSettings().getConsoleCommandsDenyMessage()));
                    event.setCancelled(true);
                    return;
                }
            } else {
                if(getCommandLimiterSettings().getConsoleBlockedCommands().contains("/" + command.split(" ")[0])) {
                    sender.sendMessage(TextUtils.setColors(getCommandLimiterSettings().getConsoleCommandsDenyMessage()));
                    event.setCancelled(true);
                    return;
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
                    sender.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.permissions-protection.player-cannot-be-operator").replace("%1$f", args[1])));
                    return;
                }
            }
        }

        if(ConsoleFilterHelper.getConsoleFilterHelper().getPluginCommands().contains("/" + command.split(" ")[0])) {
            sender.sendMessage(TextUtils.setColors("&e[FunctionalServerControl | Log] Console used the command: &6%command%".replace("%command%", "/" + event.getCommand())));
        }
    }

}
