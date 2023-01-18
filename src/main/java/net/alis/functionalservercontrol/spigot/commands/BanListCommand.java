package net.alis.functionalservercontrol.spigot.commands;

import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.commands.completers.BanListCompleter;
import net.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBanContainerManager;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class BanListCommand implements CommandExecutor {

    public BanListCommand(FunctionalServerControl plugin) {
        plugin.getCommand("banlist").setExecutor(this);
        plugin.getCommand("banlist").setTabCompleter(new BanListCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        TaskManager.preformAsync(() -> {
            if(sender.hasPermission("functionalservercontrol.banlist")) {
                if(args.length != 1) {
                    if(getConfigSettings().showDescription()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.banlist.description").replace("%1$f", label)));
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.banlist.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.banlist.example").replace("%1$f", label)));
                    return;
                }
                int page = 1;
                try {
                    page = Integer.parseInt(args[0]);
                } catch (NumberFormatException ignored) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.not-num").replace("%1$f", args[0])));
                    return;
                }
                getBanContainerManager().sendBanList(sender, page);
            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            }
        });
        return true;
    }
}
