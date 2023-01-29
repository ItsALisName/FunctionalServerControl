package net.alis.functionalservercontrol.spigot.commands;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.commands.completers.KickCompleter;
import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import net.alis.functionalservercontrol.spigot.managers.KickManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.getReason;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

/**
 * The class responsible for executing the "/kick" command
 */
public class KickCommand implements CommandExecutor {

    public KickCommand(FunctionalServerControlSpigot plugin) {
        plugin.getCommand("kick").setExecutor(this);
        plugin.getCommand("kick").setTabCompleter(new KickCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        TaskManager.preformAsync(() -> {
            KickManager kickManager = new KickManager();
            if(sender.hasPermission("functionalservercontrol.kick")) {

                if(args.length == 0) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.example").replace("%1$f", label))); }
                    return;
                }

                if(args.length == 1 && args[0].equalsIgnoreCase("-s")) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.example").replace("%1$f", label))); }
                    return;
                }

                if(args.length ==  2 && args[0].equalsIgnoreCase("-s")) {

                    FunctionalPlayer target = FunctionalPlayer.get(args[1]);
                    if(target == null) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                        return;
                    }
                    TaskManager.preformSync(() -> kickManager.preformKick(target, sender, getGlobalVariables().getDefaultReason(), false));
                    return;
                }

                if(args.length == 1) {

                    FunctionalPlayer target = FunctionalPlayer.get(args[0]);
                    if(target == null) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[0])));
                        return;
                    }
                    TaskManager.preformSync(() -> kickManager.preformKick(target, sender, getGlobalVariables().getDefaultReason(), true));
                    return;
                }

                if(args.length > 2 && args[0].equalsIgnoreCase("-s")) {

                    FunctionalPlayer target = FunctionalPlayer.get(args[1]);
                    if(target == null) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                        return;
                    }
                    TaskManager.preformSync(() -> kickManager.preformKick(target, sender, getReason(args, 2), false));
                    return;
                }

                if(args.length > 1) {

                    FunctionalPlayer target = FunctionalPlayer.get(args[0]);
                    if(target == null) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[0])));
                        return;
                    }
                    TaskManager.preformSync(() -> kickManager.preformKick(target, sender, getReason(args, 1), true));
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