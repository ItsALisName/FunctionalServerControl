package net.alis.functionalservercontrol.spigot.commands;

import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.commands.completers.KickAllCompleter;
import net.alis.functionalservercontrol.spigot.managers.KickManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.getReason;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

/**
 * The class responsible for executing the "/kickall" command
 */
public class KickAllCommand implements CommandExecutor {

    public KickAllCommand(FunctionalServerControlSpigot plugin) {
        plugin.getCommand("kickall").setExecutor(this);
        plugin.getCommand("kickall").setTabCompleter(new KickAllCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        TaskManager.preformAsync(() -> {
            KickManager kickManager = new KickManager();

            if(sender.hasPermission("functionalservercontrol.kick-all")) {

                if(args.length == 0) {
                    kickManager.preformGlobalKick(sender, getGlobalVariables().getDefaultReason(), true);
                    return;
                }

                if(args[0].equalsIgnoreCase("-s")) {
                    if(args.length == 1) {
                        kickManager.preformGlobalKick(sender, getGlobalVariables().getDefaultReason(), false);
                    } else {
                        kickManager.preformGlobalKick(sender, getReason(args, 1), false);
                    }
                    return;
                }

                if(args.length > 0) {
                    kickManager.preformGlobalKick(sender, getReason(args, 0), true);
                }


            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                return;
            }
        });
        return true;
    }
}
