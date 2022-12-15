package by.alis.functionalbans.spigot.Commands;

import by.alis.functionalbans.spigot.Commands.Completers.CrazykickCompleter;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.Files.FileAccessor;

import by.alis.functionalbans.spigot.Managers.Kick.KickManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class CrazykickCommand implements CommandExecutor {

    FunctionalBansSpigot plugin;
    public CrazykickCommand(FunctionalBansSpigot plugin) {
        this.plugin = plugin;
        plugin.getCommand("crazykick").setExecutor(this);
        plugin.getCommand("crazykick").setTabCompleter(new CrazykickCompleter());
    }

    private final FileAccessor accessor = new FileAccessor();
    private final KickManager kickManager = new KickManager();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender.hasPermission("functionalbans.crazy-kick")) {

            if(args.length == 0) {
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.crazykick.description").replace("%1$f", label))); }
                sender.sendMessage(setColors(this.accessor.getLang().getString("commands.crazykick.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.crazykick.example").replace("%1$f", label))); }
                return true;
            }

            if(args[0].equalsIgnoreCase("-a")) {
                if(sender.hasPermission("functionalbans.use.unsafe-flags")) {
                    sender.sendMessage(setColors(this.accessor.getLang().getString("other.flag-not-support").replace("%1$f", "-a")));
                } else {
                    sender.sendMessage(setColors(this.accessor.getLang().getString("unsafe-actions.unsafe-flag-no-perms").replace("%1$f", "-a")));
                    return true;
                }
                return true;
            }

            if(args[0].equalsIgnoreCase("-s")) {

                if(args.length !=  3) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.crazykick.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(this.accessor.getLang().getString("commands.crazykick.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.crazykick.example").replace("%1$f", label))); }
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if(target == null) {
                    sender.sendMessage(setColors(this.accessor.getLang().getString("other.target-offline").replace("%1$f", args[1])));
                    return true;
                }

                switch (args[2]) {
                    case "red": {
                        this.kickManager.preformCrazyKick(target, sender, ChatColor.RED, false);
                        break;
                    }
                    case "dark_red": {
                        this.kickManager.preformCrazyKick(target, sender, ChatColor.DARK_RED, false);
                        break;
                    }
                    case "green": {
                        this.kickManager.preformCrazyKick(target, sender, ChatColor.GREEN, false);
                        break;
                    }
                    case "blue": {
                        this.kickManager.preformCrazyKick(target, sender, ChatColor.BLUE, false);
                        break;
                    }
                    default: {
                        sender.sendMessage(setColors(this.accessor.getLang().getString("commands.crazykick.unknown-color").replace("%1$f", args[2])));
                        return true;
                    }
                }

            }

            if(args.length !=  2) {
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.crazykick.description").replace("%1$f", label))); }
                sender.sendMessage(setColors(this.accessor.getLang().getString("commands.crazykick.usage")));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.crazykick.example").replace("%1$f", label))); }
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                sender.sendMessage(setColors(this.accessor.getLang().getString("other.target-offline").replace("%1$f", args[0])));
                return true;
            }

            switch (args[1]) {
                case "red": {
                    this.kickManager.preformCrazyKick(target, sender, ChatColor.RED, false);
                    break;
                }
                case "dark_red": {
                    this.kickManager.preformCrazyKick(target, sender, ChatColor.DARK_RED, false);
                    break;
                }
                case "green": {
                    this.kickManager.preformCrazyKick(target, sender, ChatColor.GREEN, false);
                    break;
                }
                case "blue": {
                    this.kickManager.preformCrazyKick(target, sender, ChatColor.BLUE, false);
                    break;
                }
                default: {
                    sender.sendMessage(setColors(this.accessor.getLang().getString("commands.crazykick.unknown-color").replace("%1$f", args[0])));
                    return true;
                }
            }

            return true;


        } else {
            sender.sendMessage(setColors(this.accessor.getLang().getString("other.no-permissions")));
            return true;
        }
    }
}
