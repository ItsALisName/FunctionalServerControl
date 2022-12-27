package by.alis.functionalservercontrol.spigot.Commands;

import by.alis.functionalservercontrol.spigot.Additional.Other.OtherUtils;
import by.alis.functionalservercontrol.spigot.Commands.Completers.UnmuteCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import by.alis.functionalservercontrol.spigot.Managers.Mute.UnmuteManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.Other.TextUtils.getReason;
import static by.alis.functionalservercontrol.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class UnmuteCommand implements CommandExecutor {

    FunctionalServerControlSpigot plugin;
    public UnmuteCommand(FunctionalServerControlSpigot plugin) {
        this.plugin = plugin;
        plugin.getCommand("unmute").setExecutor(this);
        plugin.getCommand("unmute").setTabCompleter(new UnmuteCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        UnmuteManager unmuteManager = new UnmuteManager();

        if(sender.hasPermission("functionalservercontrol.unmute")) {

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
                    if (!OtherUtils.isNotNullPlayer(Bukkit.getOfflinePlayer(args[1]).getUniqueId())) {
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
