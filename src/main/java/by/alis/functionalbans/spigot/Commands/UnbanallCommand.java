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
import static by.alis.functionalbans.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class UnbanallCommand implements CommandExecutor {

    FunctionalBansSpigot plugin;
    public UnbanallCommand(FunctionalBansSpigot plugin) {
        this.plugin = plugin;
        plugin.getCommand("unbanall").setExecutor(this);
        plugin.getCommand("unbanall").setTabCompleter(new UnbanallCompleter());
    }

    private final Map<CommandSender, Integer> confirmation = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        UnbanManager unbanManager = new UnbanManager();
        
        if(sender.hasPermission("functionalbans.unban-all")) {
            if(args.length > 0 && args[0].equalsIgnoreCase("-s")) {
                if (getConfigSettings().isUnsafeActionsConfirmation()) {
                    if (confirmation.containsKey(sender)) {
                        if (!args[1].equalsIgnoreCase(String.valueOf(confirmation.get(sender)))) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-action-confirm").replace("%1$f", label + " -s " + confirmation.get(sender))));
                            return true;
                        } else {
                            confirmation.remove(sender);
                        }
                    } else {
                        confirmation.put(sender, OtherUtils.generateRandomNumber());
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-action-confirm").replace("%1$f", label + " -s " + confirmation.get(sender))));
                        return true;
                    }
                }


                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                    unbanManager.preformGlobalUnban(sender, false);
                });


            }
            if(getConfigSettings().isUnsafeActionsConfirmation()) {
                if(confirmation.containsKey(sender)) {
                    if(args.length == 0) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-action-confirm").replace("%1$f", label + " " + confirmation.get(sender))));
                        return true;
                    }
                    if(!args[0].equalsIgnoreCase(String.valueOf(confirmation.get(sender)))) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-action-confirm").replace("%1$f", label + " " + confirmation.get(sender))));
                        return true;
                    } else {
                        confirmation.remove(sender);
                    }
                } else {
                    confirmation.put(sender, OtherUtils.generateRandomNumber());
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-action-confirm").replace("%1$f", label + " " + confirmation.get(sender))));
                    return true;
                }
            }


            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                unbanManager.preformGlobalUnban(sender, true);
            });


        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            return true;
        }

        return true;
    }
}
