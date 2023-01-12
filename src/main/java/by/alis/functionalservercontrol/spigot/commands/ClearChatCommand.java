package by.alis.functionalservercontrol.spigot.commands;

import by.alis.functionalservercontrol.api.enums.Chat;
import by.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import by.alis.functionalservercontrol.spigot.commands.completers.ClearChatCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class ClearChatCommand implements CommandExecutor {

    FunctionalServerControl plugin;
    public ClearChatCommand(FunctionalServerControl plugin) {
        this.plugin = plugin;
        plugin.getCommand("clearchat").setExecutor(this);
        plugin.getCommand("clearchat").setTabCompleter(new ClearChatCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender.hasPermission("functionalservercontrol.clearchat")) {
            if(args.length != 1) {
                if (getConfigSettings().showDescription()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.clearchat.description").replace("%1$f", label)));
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.clearchat.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.clearchat.example").replace("%1$f", label)));
                return true;
            }
            if(args[0].equalsIgnoreCase("all")) {
                OtherUtils.clearChat(sender, Chat.ClearType.ALL, null);
                return true;
            } else {
                Player player = Bukkit.getPlayer(args[0]);
                if(player == null) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[0])));
                    return true;
                }
                OtherUtils.clearChat(sender, Chat.ClearType.PLAYER, player);
                return true;
            }
        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
        }

        return true;
    }
}
