package by.alis.functionalservercontrol.spigot.Commands;

import by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.GlobalVariables;
import by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor;
import by.alis.functionalservercontrol.spigot.Additional.Other.OtherUtils;
import by.alis.functionalservercontrol.spigot.Additional.Other.TemporaryCache;
import by.alis.functionalservercontrol.spigot.Commands.Completers.FunctionalServerControlCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.*;
import static by.alis.functionalservercontrol.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;
import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class FunctionalServerControlCommand implements CommandExecutor {


    FunctionalServerControlSpigot plugin;
    public FunctionalServerControlCommand(FunctionalServerControlSpigot plugin) {
        this.plugin = plugin;
        plugin.getCommand("functionalservercontrol").setExecutor(this);
        plugin.getCommand("functionalservercontrol").setTabCompleter(new FunctionalServerControlCompleter());
    }


    private boolean purgeConfirmation = false;
    private Map<CommandSender, Integer> confirmation = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        GlobalVariables globalVariables = new GlobalVariables();

        if(args.length == 0) {
            if(sender.hasPermission("functionalservercontrol.help")) {
                sender.sendMessage(setColors(String.join("\n", getFileAccessor().getLang().getStringList("commands.help")).replace("%1$f", getPlugin(FunctionalServerControlSpigot.class).getDescription().getVersion())));
            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("help")) {
            if(sender.hasPermission("functionalservercontrol.help")) {
                sender.sendMessage(setColors(String.join("\n", getFileAccessor().getLang().getStringList("commands.help")).replace("%1$f", getPlugin(FunctionalServerControlSpigot.class).getDescription().getVersion())));
            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            }
            return true;
        }


        if(args[0].equalsIgnoreCase("reload")) {
            if(!sender.hasPermission("functionalservercontrol.reload"))  {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                return true;
            }
            if(args.length != 2) {
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.reload.description").replace("%1$f", command.getName()))); }
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.reload.usage").replace("%1$f", command.getName())));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.reload.example").replace("%1$f", command.getName())));}
                return true;
            }

            if(args[1].equalsIgnoreCase("all")) {
                try {
                    long start = System.currentTimeMillis();
                    SFAccessor.reloadFiles();
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        getConfigSettings().reloadConfig();
                        getGlobalVariables().reloadGlobalVariables();
                        getLanguage().reloadLanguage();
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.reload.done").replace("%1$f", getGlobalVariables().getVariableAll()).replace("%2$f", String.valueOf(System.currentTimeMillis() - start) + "ms.")));
                    });
                    return true;
                } catch (RuntimeException ignored) {
                    sender.sendMessage(getFileAccessor().getLang().getString("commands.reload.failed"));
                    return true;
                }
            }

            if(args[1].equalsIgnoreCase("globalvariables")) {
                try{
                    long start = System.currentTimeMillis();
                    SFAccessor.reloadFiles();
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        getGlobalVariables().reloadGlobalVariables();
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.reload.done").replace("%1$f", args[1]).replace("%2$f", String.valueOf(System.currentTimeMillis() - start) + "ms.")));
                    });
                    return true;
                } catch (RuntimeException ingored) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.reload.failed")));
                    return true;
                }
            }

            if(args[1].equalsIgnoreCase("settings")) {
                try{
                    long start = System.currentTimeMillis();
                    SFAccessor.reloadFiles();
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        getConfigSettings().reloadConfig();
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.reload.done").replace("%1$f", args[1]).replace("%2$f", String.valueOf(System.currentTimeMillis() - start) + "ms.")));
                    });
                    return true;
                } catch (RuntimeException ingored) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.reload.failed")));
                    return true;
                }
            }

            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.reload.unknown-type").replace("%1$f", args[1])));
            return true;

        }


        if(args[0].equalsIgnoreCase("undo")) {
            if(sender.hasPermission("functionalservercontrol.undo")) {
                TemporaryCache.preformCommandUndo(sender);
            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                return true;
            }
            return true;
        }


        if(args[0].equalsIgnoreCase("purge")) {
            if(sender.hasPermission("functionalservercontrol.purge")) {

                if(args.length == 1 || args.length > 3) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.purge.description").replace("%1$f",command.getName() + " purge"))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.purge.usage").replace("%1$f", command.getName() + " purge")));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.purge.example").replace("%1$f", command.getName() + " purge"))); }
                    return true;
                }

                if(args[1].equalsIgnoreCase("cache")) {
                    if(TemporaryCache.getUnsafeBannedPlayers().isEmpty() && TemporaryCache.getUnsafeMutedPlayers().isEmpty()) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.purge.cache.empty")));
                        return true;
                    } else {
                        if(getConfigSettings().isPurgeConfirmation()) {
                            if (this.confirmation.containsKey(sender)) {
                                if (!args[2].equalsIgnoreCase(String.valueOf(this.confirmation.get(sender)))) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-action-confirm").replace("%1$f", label + " cache " + this.confirmation.get(sender))));
                                    return true;
                                } else {
                                    this.confirmation.remove(sender);
                                }
                            } else {
                                this.confirmation.put(sender, OtherUtils.generateRandomNumber());
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-action-confirm").replace("%1$f", label + " cache " + this.confirmation.get(sender))));
                                return true;
                            }
                        }
                        TemporaryCache.getUnsafeBannedPlayers().clear();
                        TemporaryCache.getUnsafeMutedPlayers().clear();
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.purge.cache.cleared")));
                        return true;
                    }
                } else {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.purge.unknown-type").replace("%1$f", "cache")));
                    return true;
                }
            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                return true;
            }
        }


        if(args[0].equalsIgnoreCase("playerinfo")) {



        }


        if(sender.hasPermission("functionalservercontrol.help")) {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-subcommand").replace("%1$f", args[0] == null ? "" : args[0])));
        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
        }
        return true;
    }
}
