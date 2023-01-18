package net.alis.functionalservercontrol.spigot.commands;

import net.alis.functionalservercontrol.spigot.FunctionalServerControl;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.managers.mute.UnmuteManager;
import net.alis.functionalservercontrol.spigot.commands.completers.UnmuteCompleter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.getReason;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class UnmuteCommand implements CommandExecutor {

    FunctionalServerControl plugin;
    public UnmuteCommand(FunctionalServerControl plugin) {
        this.plugin = plugin;
        plugin.getCommand("unmute").setExecutor(this);
        plugin.getCommand("unmute").setTabCompleter(new UnmuteCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        TaskManager.preformAsync(() -> {
            if(sender.hasPermission("functionalservercontrol.unmute")) {
                UnmuteManager unmuteManager = new UnmuteManager();
                if(args.length == 0) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.example").replace("%1$f", label))); }
                    return;
                }

                if(((args[0].equalsIgnoreCase("-s") && args[1].equalsIgnoreCase("-id")) || (args[0].equalsIgnoreCase("-id") && args[1].equalsIgnoreCase("-s")))) {
                    if(args.length == 2){
                        if (getConfigSettings().showDescription()) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.description").replace("%1$f", label)));
                        }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.usage").replace("%1$f", label)));
                        if (getConfigSettings().showExamples()) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.example").replace("%1$f", label)));
                        }
                        return;
                    }
                }

                if(args[0].equalsIgnoreCase("-id") && args[1].equalsIgnoreCase("-s")) {
                    int id = -1;
                    if(args.length == 3) {
                        try {
                            id = Integer.parseInt(args[2]);
                        } catch (NumberFormatException ignored) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.not-num").replace("%1$f", args[2])));
                            return;
                        }
                        unmuteManager.preformUnmuteById(sender, String.valueOf(id), null, false);
                        return;
                    }
                    if(args.length > 3) {
                        try {
                            id = Integer.parseInt(args[2]);
                        } catch (NumberFormatException ignored) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.not-num").replace("%1$f", args[2])));
                            return;
                        }
                        unmuteManager.preformUnmuteById(sender, String.valueOf(id), getReason(args, 3), false);
                        return;
                    }
                    return;
                }

                if(args[0].equalsIgnoreCase("-id")) {
                    if(args.length == 1) {
                        if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.description").replace("%1$f", label))); }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.usage").replace("%1$f", label)));
                        if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.example").replace("%1$f", label))); }
                        return;
                    }
                    int id = -1;
                    if(args.length == 2) {
                        try {
                            id = Integer.parseInt(args[1]);
                        } catch (NumberFormatException ignored) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.not-num").replace("%1$f", args[1])));
                            return;
                        }
                        unmuteManager.preformUnmuteById(sender, String.valueOf(id), null, true);
                        return;
                    }
                    if(args.length > 2) {
                        try {
                            id = Integer.parseInt(args[1]);
                        } catch (NumberFormatException ignored) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.not-num").replace("%1$f", args[1])));
                            return;
                        }
                        unmuteManager.preformUnmuteById(sender, String.valueOf(id), getReason(args, 2), true);
                        return;
                    }
                }

                if(args[0].equalsIgnoreCase("-s") && args[1].equalsIgnoreCase("-id")) {
                    int id = -1;
                    if(args.length == 3) {
                        try {
                            id = Integer.parseInt(args[2]);
                        } catch (NumberFormatException ignored) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.not-num").replace("%1$f", args[2])));
                            return;
                        }
                        unmuteManager.preformUnmuteById(sender, String.valueOf(id), null, false);
                        return;
                    }
                    if(args.length > 3) {
                        try {
                            id = Integer.parseInt(args[2]);
                        } catch (NumberFormatException ignored) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.not-num").replace("%1$f", args[2])));
                            return;
                        }
                        unmuteManager.preformUnmuteById(sender, String.valueOf(id), getReason(args, 3), false);
                        return;
                    }
                    return;
                }

                if(args[0].equalsIgnoreCase("-s")) {
                    if(args.length == 1) {
                        if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.description").replace("%1$f", label))); }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.usage").replace("%1$f", label)));
                        if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.example").replace("%1$f", label))); }
                        return;
                    }
                    if(args.length == 2) {
                        if (!OtherUtils.isNotNullPlayer(Bukkit.getOfflinePlayer(args[2]).getUniqueId())) {
                            unmuteManager.preformUnmute(args[1], sender, null, false);
                        } else {
                            unmuteManager.preformUnmute(Bukkit.getOfflinePlayer(args[1]), sender, null, false);
                        }
                        return;
                    }
                    if(args.length > 2) {
                        if (!OtherUtils.isNotNullPlayer(Bukkit.getOfflinePlayer(args[1]).getUniqueId())) {
                            unmuteManager.preformUnmute(args[1], sender, getReason(args, 2), false);
                        } else {
                            unmuteManager.preformUnmute(Bukkit.getOfflinePlayer(args[1]), sender, getReason(args, 2), false);
                        }
                        return;
                    }
                    return;
                }

                if(args.length == 1) {
                    if (!OtherUtils.isNotNullPlayer(Bukkit.getOfflinePlayer(args[0]).getUniqueId())) {
                        unmuteManager.preformUnmute(args[0], sender, null, true);
                    } else {
                        unmuteManager.preformUnmute(Bukkit.getOfflinePlayer(args[0]), sender, null, true);
                    }
                    return;
                }

                if(args.length > 1) {
                    if (!OtherUtils.isNotNullPlayer(Bukkit.getOfflinePlayer(args[0]).getUniqueId())) {
                        unmuteManager.preformUnmute(args[0], sender, getReason(args, 1), true);
                    } else {
                        unmuteManager.preformUnmute(Bukkit.getOfflinePlayer(args[0]), sender, getReason(args, 1), true);
                    }
                    return;
                }

            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                return;
            }
        });
        return true;
    }

}
