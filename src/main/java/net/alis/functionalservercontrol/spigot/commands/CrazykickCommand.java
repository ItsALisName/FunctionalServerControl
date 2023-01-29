package net.alis.functionalservercontrol.spigot.commands;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.commands.completers.CrazykickCompleter;
import net.alis.functionalservercontrol.spigot.managers.KickManager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.jetbrains.annotations.NotNull;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class CrazykickCommand implements CommandExecutor {

    public CrazykickCommand(FunctionalServerControlSpigot plugin) {
        plugin.getCommand("crazykick").setExecutor(this);
        plugin.getCommand("crazykick").setTabCompleter(new CrazykickCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        TaskManager.preformAsync(() -> {
            KickManager kickManager = new KickManager();

            if(sender.hasPermission("functionalservercontrol.crazy-kick")) {

                if(args.length == 0) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.crazykick.description").replace("%1$f", label))); }
                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.crazykick.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.crazykick.example").replace("%1$f", label))); }
                    return;
                }

                if(args[0].equalsIgnoreCase("-s")) {

                    if(args.length !=  3) {
                        if(getConfigSettings().showDescription()) { sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.crazykick.description").replace("%1$f", label))); }
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.crazykick.usage").replace("%1$f", label)));
                        if(getConfigSettings().showExamples()) { sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.crazykick.example").replace("%1$f", label))); }
                        return;
                    }

                    FunctionalPlayer target = FunctionalPlayer.get(args[1]);
                    if(target == null) {
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                        return;
                    }

                    switch (args[2]) {
                        case "red": {
                            kickManager.preformCrazyKick(target, sender, ChatColor.RED, false);
                            break;
                        }
                        case "dark_red": {
                            kickManager.preformCrazyKick(target, sender, ChatColor.DARK_RED, false);
                            break;
                        }
                        case "green": {
                            kickManager.preformCrazyKick(target, sender, ChatColor.GREEN, false);
                            break;
                        }
                        case "blue": {
                            kickManager.preformCrazyKick(target, sender, ChatColor.BLUE, false);
                            break;
                        }
                        default: {
                            sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.crazykick.unknown-color").replace("%1$f", args[2])));
                            return;
                        }
                    }
                    return;

                }

                if(args.length !=  2) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.crazykick.description").replace("%1$f", label))); }
                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.crazykick.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.crazykick.example").replace("%1$f", label))); }
                    return;
                }

                FunctionalPlayer target = FunctionalPlayer.get(args[0]);
                if(target == null) {
                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[0])));
                    return;
                }

                switch (args[1]) {
                    case "red": {
                        kickManager.preformCrazyKick(target, sender, ChatColor.RED, false);
                        break;
                    }
                    case "dark_red": {
                        kickManager.preformCrazyKick(target, sender, ChatColor.DARK_RED, false);
                        break;
                    }
                    case "green": {
                        kickManager.preformCrazyKick(target, sender, ChatColor.GREEN, false);
                        break;
                    }
                    case "blue": {
                        kickManager.preformCrazyKick(target, sender, ChatColor.BLUE, false);
                        break;
                    }
                    default: {
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.crazykick.unknown-color").replace("%1$f", args[0])));
                        return;
                    }
                }

                return;


            } else {
                sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                return;
            }
        });
        return true;
    }
}
