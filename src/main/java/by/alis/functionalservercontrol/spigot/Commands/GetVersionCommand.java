package by.alis.functionalservercontrol.spigot.Commands;

import by.alis.functionalservercontrol.spigot.Commands.Completers.GetVersionCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.Managers.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.getReason;
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class GetVersionCommand implements CommandExecutor {

    FunctionalServerControl plugin;
    public GetVersionCommand(FunctionalServerControl plugin) {
        this.plugin = plugin;
        plugin.getCommand("getversion").setExecutor(this);
        plugin.getCommand("getversion").setTabCompleter(new GetVersionCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender.hasPermission("functionalservercontrol.getversion")) {
            if(args.length == 0) {
                if(getConfigSettings().showDescription()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getversion.description").replace("%1$f", label)));
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getversion.description").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getversion.example").replace("%1$f", label)));
                return true;
            }
            if(args.length > 1) {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.too-many-arguments").replace("%1$f", getReason(args, 1))));
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[0])));
                return true;
            }
            PlayerManager playerManager = new PlayerManager();
            Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
                sender.sendMessage(setColors(
                        getFileAccessor().getLang().getString("commands.getversion.success")
                                .replace("%1$f", target.getPlayerListName())
                                .replace("%2$f", playerManager.getPlayerMinecraftVersion(target).v)
                ));
            });
        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
        }

        return true;
    }
}
