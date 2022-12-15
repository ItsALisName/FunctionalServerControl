package by.alis.functionalbans.spigot.Commands;

import by.alis.functionalbans.spigot.Additional.Other.OtherUtils;
import by.alis.functionalbans.spigot.Commands.Completers.UnbanallCompleter;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.Bans.UnbanManager;
import by.alis.functionalbans.spigot.Managers.Files.FileAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class UnbanallCommand implements CommandExecutor {

    FunctionalBansSpigot plugin;
    public UnbanallCommand(FunctionalBansSpigot plugin) {
        this.plugin = plugin;
        plugin.getCommand("unbanall").setExecutor(this);
        plugin.getCommand("unbanall").setTabCompleter(new UnbanallCompleter());
    }

    private Map<CommandSender, Integer> confirmation = new HashMap<>();

    private final FileAccessor accessor = new FileAccessor();
    private final UnbanManager unbanManager = new UnbanManager();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender.hasPermission("functionalbans.unban-all")) {
            if(args.length > 0 && args[0].equalsIgnoreCase("-s")) {
                if (getConfigSettings().isUnsafeActionsConfirmation()) {
                    if (this.confirmation.containsKey(sender)) {
                        if (!args[1].equalsIgnoreCase(String.valueOf(this.confirmation.get(sender)))) {
                            sender.sendMessage(setColors(this.accessor.getLang().getString("unsafe-actions.unsafe-action-confirm").replace("%1$f", label + " -s " + this.confirmation.get(sender))));
                            return true;
                        } else {
                            this.confirmation.remove(sender);
                        }
                    } else {
                        this.confirmation.put(sender, OtherUtils.generateRandomNumber());
                        sender.sendMessage(setColors(this.accessor.getLang().getString("unsafe-actions.unsafe-action-confirm").replace("%1$f", label + " -s " + this.confirmation.get(sender))));
                        return true;
                    }
                }


                Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                    this.unbanManager.preformGlobalUnban(sender, false);
                });


            }
            if(getConfigSettings().isUnsafeActionsConfirmation()) {
                if(this.confirmation.containsKey(sender)) {
                    if(args.length == 0) {
                        sender.sendMessage(setColors(this.accessor.getLang().getString("unsafe-actions.unsafe-action-confirm").replace("%1$f", label + " " + this.confirmation.get(sender))));
                        return true;
                    }
                    if(!args[0].equalsIgnoreCase(String.valueOf(this.confirmation.get(sender)))) {
                        sender.sendMessage(setColors(this.accessor.getLang().getString("unsafe-actions.unsafe-action-confirm").replace("%1$f", label + " " + this.confirmation.get(sender))));
                        return true;
                    } else {
                        this.confirmation.remove(sender);
                    }
                } else {
                    this.confirmation.put(sender, OtherUtils.generateRandomNumber());
                    sender.sendMessage(setColors(this.accessor.getLang().getString("unsafe-actions.unsafe-action-confirm").replace("%1$f", label + " " + this.confirmation.get(sender))));
                    return true;
                }
            }


            Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                this.unbanManager.preformGlobalUnban(sender, true);
            });


        } else {
            sender.sendMessage(setColors(this.accessor.getLang().getString("other.no-permissions")));
            return true;
        }

        return true;
    }
}
