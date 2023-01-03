package by.alis.functionalservercontrol.spigot.Commands;

import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.OtherUtils;
import by.alis.functionalservercontrol.spigot.Commands.Completers.UnbanCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.Managers.Bans.UnbanManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.getReason;
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

/**
 * The class responsible for executing the "/unban" command
 */
public class UnbanCommand implements CommandExecutor {

    FunctionalServerControl plugin;
    public UnbanCommand(FunctionalServerControl plugin) {
        this.plugin = plugin;
        plugin.getCommand("unban").setExecutor(this);
        plugin.getCommand("unban").setTabCompleter(new UnbanCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        UnbanManager unbanManager = new UnbanManager();
        
        if(sender.hasPermission("functionalservercontrol.unban")) {

            if(args.length == 0) {
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unban.description").replace("%1$f", label))); }
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unban.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unban.example").replace("%1$f", label))); }
                return true;
            }

            if(args.length == 1) {
                if (!OtherUtils.isNotNullPlayer(Bukkit.getOfflinePlayer(args[0]).getUniqueId())) {
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        unbanManager.preformUnban(args[0], sender, null, true);
                    });
                } else {
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        unbanManager.preformUnban(Bukkit.getOfflinePlayer(args[0]), sender, null, true);
                    });
                }
                return true;
            }

            if(args[0].equalsIgnoreCase("-a")) {
                if(sender.hasPermission("functionalservercontrol.use.unsafe-flags")) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-not-support").replace("%1$f", "-a")));
                } else {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-flag-no-perms").replace("%1$f", "-a")));
                }
                return true;
            }

            if(args[0].equalsIgnoreCase("-s")) {
                if(args.length == 1) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unban.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unban.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.unban.example").replace("%1$f", label))); }
                    return true;
                }
                if(args.length == 2) {
                    if (!OtherUtils.isNotNullPlayer(Bukkit.getOfflinePlayer(args[1]).getUniqueId())) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            unbanManager.preformUnban(args[1], sender, null, false);
                        });
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            unbanManager.preformUnban(Bukkit.getOfflinePlayer(args[1]), sender, null, false);
                        });
                    }
                    return true;
                }
                if(args.length > 2) {
                    if (!OtherUtils.isNotNullPlayer(Bukkit.getOfflinePlayer(args[1]).getUniqueId())) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            unbanManager.preformUnban(args[1], sender, getReason(args, 2), false);
                        });
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            unbanManager.preformUnban(Bukkit.getOfflinePlayer(args[1]), sender, getReason(args, 2), false);
                        });
                    }
                    return true;
                }
                return true;
            }



        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            return true;
        }

        return true;
    }
}
