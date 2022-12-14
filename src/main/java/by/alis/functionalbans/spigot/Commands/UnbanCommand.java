package by.alis.functionalbans.spigot.Commands;

import by.alis.functionalbans.spigot.Additional.Other.OtherUtils;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.Bans.UnbanManager;
import by.alis.functionalbans.spigot.Managers.Files.FileAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.getReason;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

/**
 * The class responsible for executing the "/unban" command
 */
public class UnbanCommand implements CommandExecutor {

    FunctionalBansSpigot plugin;
    public UnbanCommand(FunctionalBansSpigot plugin) {
        this.plugin = plugin;
        plugin.getCommand("unban").setExecutor(this);
    }

    private final UnbanManager unbanManager = new UnbanManager();
    private final FileAccessor accessor = new FileAccessor();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender.hasPermission("functionalbans.unban")) {

            if(args.length == 0) {
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.unban.description").replace("%1$f", label))); }
                sender.sendMessage(setColors(this.accessor.getLang().getString("commands.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.unban.example").replace("%1$f", label))); }
                return true;
            }

            if(args.length == 1) {
                if (!OtherUtils.isNotNullPlayer(Bukkit.getOfflinePlayer(args[0]).getUniqueId())) {
                    Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                        this.unbanManager.preformUnban(args[0], sender, null, true);
                    });
                } else {
                    Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                        this.unbanManager.preformUnban(Bukkit.getOfflinePlayer(args[0]), sender, null, true);
                    });
                }
                return true;
            }

            if(args[0].equalsIgnoreCase("-a")) {
                if(sender.hasPermission("functionalbans.use.unsafe-flags")) {
                    sender.sendMessage(setColors(this.accessor.getLang().getString("other.flag-not-support").replace("%1$f", "-a")));
                } else {
                    sender.sendMessage(setColors(this.accessor.getLang().getString("unsafe-actions.unsafe-flag-no-perms").replace("%1$f", "-a")));
                }
                return true;
            }

            if(args[0].equalsIgnoreCase("-s")) {
                if(args.length == 1) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.unban.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(this.accessor.getLang().getString("commands.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.unban.example").replace("%1$f", label))); }
                    return true;
                }
                if(args.length == 2) {
                    if (!OtherUtils.isNotNullPlayer(Bukkit.getOfflinePlayer(args[1]).getUniqueId())) {
                        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                            this.unbanManager.preformUnban(args[1], sender, null, false);
                        });
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                            this.unbanManager.preformUnban(Bukkit.getOfflinePlayer(args[1]), sender, null, false);
                        });
                    }
                    return true;
                }
                if(args.length > 2) {
                    if (!OtherUtils.isNotNullPlayer(Bukkit.getOfflinePlayer(args[1]).getUniqueId())) {
                        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                            this.unbanManager.preformUnban(args[1], sender, getReason(args, 2), false);
                        });
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                            this.unbanManager.preformUnban(Bukkit.getOfflinePlayer(args[1]), sender, getReason(args, 2), false);
                        });
                    }
                    return true;
                }
                return true;
            }



        } else {
            sender.sendMessage(setColors(this.accessor.getLang().getString("other.no-permissions")));
            return true;
        }

        return true;
    }
}
