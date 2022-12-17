package by.alis.functionalbans.spigot.Commands;

import by.alis.functionalbans.spigot.Commands.Completers.KickCompleter;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.CooldownsManager;
import by.alis.functionalbans.spigot.Managers.Files.FileAccessor;
import by.alis.functionalbans.spigot.Managers.Kick.KickManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.getReason;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalbans.spigot.Managers.Files.SFAccessor.getFileAccessor;

/**
 * The class responsible for executing the "/kick" command
 */
public class KickCommand implements CommandExecutor {

    FunctionalBansSpigot plugin;
    public KickCommand(FunctionalBansSpigot plugin) {
        this.plugin = plugin;
        plugin.getCommand("kick").setExecutor(this);
        plugin.getCommand("kick").setTabCompleter(new KickCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        KickManager kickManager = new KickManager();
        
        if(sender.hasPermission("functionalbans.kick")) {

            if(args.length == 0) {
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.description").replace("%1$f", label))); }
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.example").replace("%1$f", label))); }
                return true;
            }

            if(args.length == 1 && args[0].equalsIgnoreCase("-s")) {
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.description").replace("%1$f", label))); }
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.kick.example").replace("%1$f", label))); }
                return true;
            }

            if(args[0].equalsIgnoreCase("-a")) {
                if(sender.hasPermission("functionalbans.use.unsafe-flags")) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-not-support").replace("%1$f", "-a")));
                } else {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-flag-no-perms").replace("%1$f", "-a")));
                    return true;
                }
                return true;
            }

            if(args.length ==  2 && args[0].equalsIgnoreCase("-s")) {

                Player target = Bukkit.getPlayer(args[1]);
                if(target == null) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                    return true;
                }
                kickManager.preformKick(target, sender, getGlobalVariables().getDefaultReason(), false);
                return true;
            }

            if(args.length == 1) {

                Player target = Bukkit.getPlayer(args[0]);
                if(target == null) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[0])));
                    return true;
                }
                kickManager.preformKick(target, sender, getGlobalVariables().getDefaultReason(), true);
                return true;
            }

            if(args.length > 2 && args[0].equalsIgnoreCase("-s")) {

                Player target = Bukkit.getPlayer(args[1]);
                if(target == null) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                    return true;
                }
                kickManager.preformKick(target, sender, getReason(args, 2), false);
                return true;
            }

            if(args.length > 1) {

                Player target = Bukkit.getPlayer(args[0]);
                if(target == null) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[0])));
                    return true;
                }
                kickManager.preformKick(target, sender, getReason(args, 1), true);
                return true;
            }

        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            return true;
        }

        return true;
    }
}