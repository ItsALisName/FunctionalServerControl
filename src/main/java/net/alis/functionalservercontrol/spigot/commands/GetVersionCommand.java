package net.alis.functionalservercontrol.spigot.commands;

import net.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.commands.completers.GetVersionCompleter;
import net.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.getReason;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class GetVersionCommand implements CommandExecutor {

    public GetVersionCommand(FunctionalServerControl plugin) {
        plugin.getCommand("getversion").setExecutor(this);
        plugin.getCommand("getversion").setTabCompleter(new GetVersionCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        TaskManager.preformAsync(() -> {
            if(sender.hasPermission("functionalservercontrol.getversion")) {
                if(args.length == 0) {
                    if(getConfigSettings().showDescription()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getversion.description").replace("%1$f", label)));
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getversion.description").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getversion.example").replace("%1$f", label)));
                    return;
                }
                if(args.length > 1) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.too-many-arguments").replace("%1$f", getReason(args, 1))));
                    return;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if(target == null) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[0])));
                    return;
                }
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getversion.success").replace("%1$f", target.getName()).replace("%2$f", CoreAdapter.getAdapter().getPlayerVersion(target).toString())));
            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            }
        });
        return true;
    }
}
