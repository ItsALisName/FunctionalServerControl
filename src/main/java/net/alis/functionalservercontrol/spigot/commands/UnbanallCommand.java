package net.alis.functionalservercontrol.spigot.commands;

import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.commands.completers.UnbanallCompleter;
import net.alis.functionalservercontrol.spigot.managers.ban.UnbanManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class UnbanallCommand implements CommandExecutor {

    public UnbanallCommand(FunctionalServerControlSpigot plugin) {
        plugin.getCommand("unbanall").setExecutor(this);
        plugin.getCommand("unbanall").setTabCompleter(new UnbanallCompleter());
    }

    private final Map<CommandSender, Integer> confirmation = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        TaskManager.preformAsync(() -> {
            UnbanManager unbanManager = new UnbanManager();

            if(sender.hasPermission("functionalservercontrol.unban-all")) {
                if(args.length > 0 && args[0].equalsIgnoreCase("-s")) {
                    if (getConfigSettings().isUnsafeActionsConfirmation()) {
                        if (confirmation.containsKey(sender)) {
                            if (!args[1].equalsIgnoreCase(String.valueOf(confirmation.get(sender)))) {
                                sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-action-confirm").replace("%1$f", label + " -s " + confirmation.get(sender))));
                                return;
                            } else {
                                confirmation.remove(sender);
                            }
                        } else {
                            confirmation.put(sender, OtherUtils.generateRandomNumber());
                            sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-action-confirm").replace("%1$f", label + " -s " + confirmation.get(sender))));
                            return;
                        }
                    }
                    unbanManager.preformGlobalUnban(sender, false);
                }
                if(getConfigSettings().isUnsafeActionsConfirmation()) {
                    if(confirmation.containsKey(sender)) {
                        if(args.length == 0) {
                            sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-action-confirm").replace("%1$f", label + " " + confirmation.get(sender))));
                            return;
                        }
                        if(!args[0].equalsIgnoreCase(String.valueOf(confirmation.get(sender)))) {
                            sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-action-confirm").replace("%1$f", label + " " + confirmation.get(sender))));
                            return;
                        } else {
                            confirmation.remove(sender);
                        }
                    } else {
                        confirmation.put(sender, OtherUtils.generateRandomNumber());
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-action-confirm").replace("%1$f", label + " " + confirmation.get(sender))));
                        return;
                    }
                }
                unbanManager.preformGlobalUnban(sender, true);
            } else {
                sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                return;
            }
        });
        return true;
    }
}
