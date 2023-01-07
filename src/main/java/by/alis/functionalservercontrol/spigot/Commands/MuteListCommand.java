package by.alis.functionalservercontrol.spigot.Commands;

import by.alis.functionalservercontrol.spigot.Commands.Completers.MuteListCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getBanContainerManager;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;
import static by.alis.functionalservercontrol.spigot.Managers.Mute.MuteManager.getMuteContainerManager;

public class MuteListCommand implements CommandExecutor {

    FunctionalServerControl plugin;
    public MuteListCommand(FunctionalServerControl plugin) {
        this.plugin = plugin;
        plugin.getCommand("mutelist").setExecutor(this);
        plugin.getCommand("mutelist").setTabCompleter(new MuteListCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("functionalservercontrol.mutelist")) {
            if(args.length != 1) {
                if(getConfigSettings().showDescription()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mutelist.description").replace("%1$f", label)));
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mutelist.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mutelist.example").replace("%1$f", label)));
                return true;
            }
            int page = 1;
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mutelist.not-num").replace("%1$f", args[0])));
                return true;
            }
            getMuteContainerManager().sendMuteList(sender, page);
        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
        }
        return true;
    }
}
