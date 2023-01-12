package by.alis.functionalservercontrol.spigot.commands;

import by.alis.functionalservercontrol.spigot.commands.completers.GetInfoCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.managers.InformationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class GetInfoCommand implements CommandExecutor {

    FunctionalServerControl plugin;
    public GetInfoCommand(FunctionalServerControl plugin) {
        this.plugin = plugin;
        plugin.getCommand("getinfo").setExecutor(this);
        plugin.getCommand("getinfo").setTabCompleter(new GetInfoCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender.hasPermission("functionalservercontrol.getinfo")) {
            if(args.length != 2) {
                if(getConfigSettings().showDescription()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.description").replace("%1$f", label)));
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getinfo.example").replace("%1$f", label)));
                return true;
            }
            if(args[0].equalsIgnoreCase("-id")) {
                InformationManager.getCachedInformation(sender, "-id", args[1]);
                return true;
            }
            if(args[0].equalsIgnoreCase("-ip")) {
                InformationManager.getCachedInformation(sender, "-ip", args[1]);
                return true;
            }
            if(args[0].equalsIgnoreCase("-name")) {
                InformationManager.getCachedInformation(sender, "-name", args[1]);
                return true;
            }
            if(args[0].equalsIgnoreCase("-uuid")) {
                InformationManager.getCachedInformation(sender, "-uuid", args[1]);
                return true;
            }
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-flag").replace("%1$f", args[0])));
        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            return true;
        }

        return true;
    }
}
