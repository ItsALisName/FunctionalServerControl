package by.alis.functionalservercontrol.spigot.commands;

import by.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import by.alis.functionalservercontrol.spigot.commands.completers.UnmuteCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.managers.mute.UnmuteManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.getReason;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class UnmuteCommand implements CommandExecutor {

    FunctionalServerControl plugin;
    public UnmuteCommand(FunctionalServerControl plugin) {
        this.plugin = plugin;
        plugin.getCommand("unmute").setExecutor(this);
        plugin.getCommand("unmute").setTabCompleter(new UnmuteCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {


        if(sender.hasPermission("functionalservercontrol.unmute")) {
            UnmuteManager unmuteManager = new UnmuteManager();

            if(args.length == 0) {
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.description").replace("%1$f", label))); }
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.example").replace("%1$f", label))); }
                return true;
            }

            if(args.length == 1) {
                if (!OtherUtils.isNotNullPlayer(Bukkit.getOfflinePlayer(args[0]).getUniqueId())) {
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        unmuteManager.preformUnmute(args[0], sender, null, true);
                    });
                } else {
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        unmuteManager.preformUnmute(Bukkit.getOfflinePlayer(args[0]), sender, null, true);
                    });
                }
                return true;
            }

            if(args.length > 1) {
                if (!OtherUtils.isNotNullPlayer(Bukkit.getOfflinePlayer(args[0]).getUniqueId())) {
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        unmuteManager.preformUnmute(args[0], sender, getReason(args, 1), true);
                    });
                } else {
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        unmuteManager.preformUnmute(Bukkit.getOfflinePlayer(args[0]), sender, getReason(args, 1), true);
                    });
                }
                return true;
            }

            if(args[0].equalsIgnoreCase("-a")) {
                if(sender.hasPermission("functionalservercontrol.use.unsafe-flags")) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-not-support").replace("%1$f", "-a")));
                } else {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-flag-no-perms").replace("%1$f", "-a")));
                }
                return true;
            }

            if(args[0].equalsIgnoreCase("-s")) {
                if(args.length == 1) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unmute.example").replace("%1$f", label))); }
                    return true;
                }
                if(args.length == 2) {
                    if (!OtherUtils.isNotNullPlayer(Bukkit.getOfflinePlayer(args[2]).getUniqueId())) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            unmuteManager.preformUnmute(args[1], sender, null, false);
                        });
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            unmuteManager.preformUnmute(Bukkit.getOfflinePlayer(args[1]), sender, null, false);
                        });
                    }
                    return true;
                }
                if(args.length > 2) {
                    if (!OtherUtils.isNotNullPlayer(Bukkit.getOfflinePlayer(args[1]).getUniqueId())) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            unmuteManager.preformUnmute(args[1], sender, getReason(args, 2), false);
                        });
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            unmuteManager.preformUnmute(Bukkit.getOfflinePlayer(args[1]), sender, getReason(args, 2), false);
                        });
                    }
                    return true;
                }
                return true;
            }



        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            return true;
        }

        return true;
    }

}
