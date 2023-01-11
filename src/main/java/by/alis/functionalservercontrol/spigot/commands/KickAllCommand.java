package by.alis.functionalservercontrol.spigot.commands;

import by.alis.functionalservercontrol.spigot.commands.completers.KickAllCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.managers.KickManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.getReason;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

/**
 * The class responsible for executing the "/kickall" command
 */
public class KickAllCommand implements CommandExecutor {

    FunctionalServerControl plugin;
    public KickAllCommand(FunctionalServerControl plugin) {
        this.plugin = plugin;
        plugin.getCommand("kickall").setExecutor(this);
        plugin.getCommand("kickall").setTabCompleter(new KickAllCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        KickManager kickManager = new KickManager();
        
        if(sender.hasPermission("functionalservercontrol.kick-all")) {

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
                if(sender.hasPermission("functionalservercontrol.use.unsafe-flags")) {
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
