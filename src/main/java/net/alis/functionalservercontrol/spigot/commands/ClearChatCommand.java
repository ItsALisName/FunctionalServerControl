package net.alis.functionalservercontrol.spigot.commands;

import net.alis.functionalservercontrol.api.enums.Chat;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.commands.completers.ClearChatCompleter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class ClearChatCommand implements CommandExecutor {

    public ClearChatCommand(FunctionalServerControlSpigot plugin) {
        plugin.getCommand("clearchat").setExecutor(this);
        plugin.getCommand("clearchat").setTabCompleter(new ClearChatCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        TaskManager.preformAsync(() -> {
            if(sender.hasPermission("functionalservercontrol.clearchat")) {
                if(args.length != 1) {
                    if (getConfigSettings().showDescription()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.clearchat.description").replace("%1$f", label)));
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.clearchat.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.clearchat.example").replace("%1$f", label)));
                    return;
                }
                if(args[0].equalsIgnoreCase("all")) {
                    OtherUtils.clearChat(sender, Chat.ClearType.ALL, null);
                    return;
                } else {
                    FunctionalPlayer target = FunctionalPlayer.get(args[0]);
                    if(target == null) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[0])));
                        return;
                    }
                    OtherUtils.clearChat(sender, Chat.ClearType.PLAYER, target);
                    return;
                }
            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            }
        });
        return true;
    }
}
