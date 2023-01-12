package by.alis.functionalservercontrol.spigot.commands;

import by.alis.functionalservercontrol.spigot.additional.globalsettings.GlobalVariables;
import by.alis.functionalservercontrol.spigot.additional.misc.cooldowns.Cooldowns;
import by.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import by.alis.functionalservercontrol.spigot.commands.completers.FunctionalServerControlCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.managers.file.SFAccessor;
import by.alis.functionalservercontrol.spigot.managers.ImportManager;
import by.alis.functionalservercontrol.spigot.managers.InformationManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;
import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class FunctionalServerControlCommand implements CommandExecutor {


    FunctionalServerControl plugin;
    public FunctionalServerControlCommand(FunctionalServerControl plugin) {
        this.plugin = plugin;
        plugin.getCommand("functionalservercontrol").setExecutor(this);
        plugin.getCommand("functionalservercontrol").setTabCompleter(new FunctionalServerControlCompleter());
    }

    private final Map<CommandSender, Integer> confirmation = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        GlobalVariables globalVariables = new GlobalVariables();

        if(args.length == 0) {
            if(sender.hasPermission("functionalservercontrol.help")) {
                sender.sendMessage(setColors(String.join("\n", getFileAccessor().getLang().getStringList("commands.help")).replace("%1$f", getPlugin(FunctionalServerControl.class).getDescription().getVersion())));
            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("help")) {
            if(sender.hasPermission("functionalservercontrol.help")) {
                sender.sendMessage(setColors(String.join("\n", getFileAccessor().getLang().getStringList("commands.help")).replace("%1$f", getPlugin(FunctionalServerControl.class).getDescription().getVersion())));
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
                        getCommandLimiterSettings().reloadCommandLimiterSettings();
                        getLanguage().reloadLanguage();
                        Cooldowns.getCooldowns().reloadCooldowns(sender);
                        getChatSettings().reloadChatSettings();
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

            if(args[1].equalsIgnoreCase("commandlimiter")) {
                try{
                    long start = System.currentTimeMillis();
                    SFAccessor.reloadFiles();
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        getCommandLimiterSettings().reloadCommandLimiterSettings();
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.reload.done").replace("%1$f", args[1]).replace("%2$f", String.valueOf(System.currentTimeMillis() - start) + "ms.")));
                    });
                    return true;
                } catch (RuntimeException ingored) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.reload.failed")));
                    return true;
                }
            }

            if(args[1].equalsIgnoreCase("cooldowns")) {
                try{
                    long start = System.currentTimeMillis();
                    SFAccessor.reloadFiles();
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        Cooldowns.getCooldowns().reloadCooldowns(sender);
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.reload.done").replace("%1$f", args[1]).replace("%2$f", String.valueOf(System.currentTimeMillis() - start) + "ms.")));
                    });
                    return true;
                } catch (RuntimeException ingored) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.reload.failed")));
                    return true;
                }
            }

            if(args[1].equalsIgnoreCase("chatsettings")) {
                try{
                    long start = System.currentTimeMillis();
                    SFAccessor.reloadFiles();
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        getChatSettings().reloadChatSettings();
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
                }
                if(args[1].equalsIgnoreCase("history")) {
                    getBaseManager().clearHistory();
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.purge.history.cleared")));
                    return true;
                }
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.purge.unknown-type").replace("%1$f", args[1])));
                return true;
            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                return true;
            }
        }

        if(args[0].equalsIgnoreCase("import")) {
            if(sender.hasPermission("functionalservercontrol.import")) {
                if (args.length == 1 || args.length > 2) {
                    if (getConfigSettings().showDescription())
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.import.description").replace("%1$f", label + " " + args[0])));
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.import.usage").replace("%1$f", label + " " + args[0])));
                    if (getConfigSettings().showExamples())
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.import.example").replace("%1$f", label + " " + args[0])));
                    return true;
                }

                if (args[1].equalsIgnoreCase("vanilla")) {
                    ImportManager.importDataFromVanilla(sender);
                    return true;
                }

                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.import.unknown-type").replace("%1$f", args[1])));
            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("history")) {
            if(sender.hasPermission("functionalservercontrol.history")) {
                if(args.length == 1) {
                    if(getConfigSettings().showDescription()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.description").replace("%1$f", label + " history")));
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.usage").replace("%1$f", label + " history")));
                    if(getConfigSettings().showExamples()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.example").replace("%1$f", label + " history")));
                    return true;
                }
                if(args.length == 2) {
                    int lines;
                    try {
                        lines = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.not-num").replace("%1$f", args[1])));
                        return true;
                    }
                    InformationManager.sendHistory(sender, lines, null);
                    return true;
                }
                if(args.length > 2) {
                    if(!args[2].equalsIgnoreCase("attribute")) {
                        if(getConfigSettings().showDescription()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.description").replace("%1$f", label + " history")));
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.usage").replace("%1$f", label + " history")));
                        if(getConfigSettings().showExamples()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.example").replace("%1$f", label + " history")));
                        return true;
                    }
                    if(args.length > 4) {
                        if(getConfigSettings().showDescription()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.description").replace("%1$f", label + " history")));
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.usage").replace("%1$f", label + " history")));
                        if(getConfigSettings().showExamples()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.example").replace("%1$f", label + " history")));
                        return true;
                    }
                    int lines;
                    try {
                        lines = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.not-num").replace("%1$f", args[1])));
                        return true;
                    }
                    InformationManager.sendHistory(sender, lines, args[3]);
                    return true;
                }
            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("getstatistic")) {
            if(sender.hasPermission("functionalservercontrol.getstatistic")) {
                if(args.length != 3) {
                    if(getConfigSettings().showDescription()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getstatistic.description").replace("%1$f", label + " getstatistic")));
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getstatistic.usage").replace("%1$f", label + " getstatistic")));
                    if(getConfigSettings().showExamples()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getstatistic.example").replace("%1$f", label + " getstatistic")));
                    return true;
                }
                if(args[1].equalsIgnoreCase("player")) {
                    InformationManager.sendStatistic(sender, "player", args[2]);
                    return true;
                }
                if(args[1].equalsIgnoreCase("admin")) {
                    InformationManager.sendStatistic(sender, "admin", args[2]);
                    return true;
                }
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.getstatistic.unknown-type").replace("%1$f", args[1])));
            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            }
            return true;
        }

        if(sender.hasPermission("functionalservercontrol.help")) {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-subcommand").replace("%1$f", args[0])));
        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
        }
        return true;
    }
}
