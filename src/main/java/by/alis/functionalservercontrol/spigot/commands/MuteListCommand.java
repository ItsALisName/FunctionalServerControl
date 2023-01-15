package by.alis.functionalservercontrol.spigot.commands;

import by.alis.functionalservercontrol.spigot.commands.completers.MuteListCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;
import static by.alis.functionalservercontrol.spigot.managers.mute.MuteManager.getMuteContainerManager;

public class MuteListCommand implements CommandExecutor {

    public MuteListCommand(FunctionalServerControl plugin) {
        plugin.getCommand("mutelist").setExecutor(this);
        plugin.getCommand("mutelist").setTabCompleter(new MuteListCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        TaskManager.preformAsync(() -> {
            if(sender.hasPermission("functionalservercontrol.mutelist")) {
                if(args.length != 1) {
                    if(getConfigSettings().showDescription()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mutelist.description").replace("%1$f", label)));
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mutelist.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mutelist.example").replace("%1$f", label)));
                    return;
                }
                int page = 1;
                try {
                    page = Integer.parseInt(args[0]);
                } catch (NumberFormatException ignored) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mutelist.not-num").replace("%1$f", args[0])));
                    return;
                }
                getMuteContainerManager().sendMuteList(sender, page);
            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            }
        });
        return true;
    }
}
