package by.alis.functionalbans.spigot.Commands;

import by.alis.functionalbans.spigot.Commands.Completers.KickAllCompleter;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.Files.FileAccessor;
import by.alis.functionalbans.spigot.Managers.Kick.KickManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.getReason;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalbans.spigot.Managers.Files.SFAccessor.getFileAccessor;

/**
 * The class responsible for executing the "/kickall" command
 */
public class KickAllCommand implements CommandExecutor {

    FunctionalBansSpigot plugin;
    public KickAllCommand(FunctionalBansSpigot plugin) {
        this.plugin = plugin;
        plugin.getCommand("kickall").setExecutor(this);
        plugin.getCommand("kickall").setTabCompleter(new KickAllCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        KickManager kickManager = new KickManager();
        
        if(sender.hasPermission("functionalbans.kick-all")) {

            if(args.length == 0) {
                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                    kickManager.preformGlobalKick(sender, getGlobalVariables().getDefaultReason(), true);
                });
                return true;
            }

            if(args[0].equalsIgnoreCase("-s")) {
                if(args.length == 1) {
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        kickManager.preformGlobalKick(sender, getGlobalVariables().getDefaultReason(), false);
                    });
                } else {
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        kickManager.preformGlobalKick(sender, getReason(args, 1), false);
                    });
                }
                return true;
            }

            if(args[0].equalsIgnoreCase("-a")) {
                if(sender.hasPermission("functionalbans.use.unsafe-flags")) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-not-support").replace("%1$f", args[0])));
                } else {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", label)));
                }
                return true;
            }

            if(args.length > 0) {
                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                    kickManager.preformGlobalKick(sender, getReason(args, 0), true);
                });
            }


        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            return true;
        }

        return true;
    }
}
