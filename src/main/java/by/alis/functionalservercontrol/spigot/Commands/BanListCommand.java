package by.alis.functionalservercontrol.spigot.Commands;

import by.alis.functionalservercontrol.spigot.Commands.Completers.BanListCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getBanContainerManager;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class BanListCommand implements CommandExecutor {

    FunctionalServerControl plugin;
    public BanListCommand(FunctionalServerControl plugin) {
        this.plugin = plugin;
        plugin.getCommand("banlist").setExecutor(this);
        plugin.getCommand("banlist").setTabCompleter(new BanListCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender.hasPermission("functionalservercontrol.banlist")) {
            if(args.length != 1) {
                if(getConfigSettings().showDescription()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.banlist.description").replace("%1$f", label)));
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.banlist.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.banlist.example").replace("%1$f", label)));
                return true;
            }
            int page = 1;
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.banlist.not-num").replace("%1$f", args[0])));
                return true;
            }
            getBanContainerManager().sendBanList(sender, page);
        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
        }
        return true;
    }
}
