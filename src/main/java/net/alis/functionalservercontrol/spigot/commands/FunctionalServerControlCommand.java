package net.alis.functionalservercontrol.spigot.commands;

import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.cooldowns.Cooldowns;
import net.alis.functionalservercontrol.spigot.managers.InetManager;
import net.alis.functionalservercontrol.spigot.commands.completers.FunctionalServerControlCompleter;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.managers.file.SFAccessor;
import net.alis.functionalservercontrol.spigot.managers.ImportManager;
import net.alis.functionalservercontrol.spigot.managers.InformationManager;
import net.alis.functionalservercontrol.spigot.managers.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;
import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class FunctionalServerControlCommand implements CommandExecutor {

    public FunctionalServerControlCommand(FunctionalServerControlSpigot plugin) {
        plugin.getCommand("functionalservercontrol").setExecutor(this);
        plugin.getCommand("functionalservercontrol").setTabCompleter(new FunctionalServerControlCompleter());
    }

    private final Map<CommandSender, Integer> confirmation = new HashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        TaskManager.preformAsync(() -> {
            if(args.length == 0) {
                if(sender.hasPermission("functionalservercontrol.help")) {
                    sender.sendMessage(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("commands.help")).replace("%1$f", getPlugin(FunctionalServerControlSpigot.class).getDescription().getVersion())));
                } else {
                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                }
                return;
            }

            if(args[0].equalsIgnoreCase("help")) {
                if(sender.hasPermission("functionalservercontrol.help")) {
                    sender.sendMessage(TextUtils.setColors(String.join("\n", getFileAccessor().getLang().getStringList("commands.help")).replace("%1$f", getPlugin(FunctionalServerControlSpigot.class).getDescription().getVersion())));
                } else {
                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                }
                return;
            }


            if(args[0].equalsIgnoreCase("reload")) {
                if(!sender.hasPermission("functionalservercontrol.reload"))  {
                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                    return;
                }
                if(args.length != 2) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.reload.description").replace("%1$f", command.getName()))); }
                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.reload.usage").replace("%1$f", command.getName())));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.reload.example").replace("%1$f", command.getName())));}
                    return;
                }

                if(args[1].equalsIgnoreCase("all")) {
                    try {
                        long start = System.currentTimeMillis();
                        SFAccessor.reloadFiles();
                        getConfigSettings().reloadConfig();
                        getGlobalVariables().reloadGlobalVariables();
                        getCommandLimiterSettings().reloadCommandLimiterSettings();
                        getLanguage().reloadLanguage();
                        Cooldowns.getCooldowns().reloadCooldowns(sender);
                        getChatSettings().reloadChatSettings();
                        getProtectionSettings().reloadProtectionSettings();
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.reload.done").replace("%1$f", getGlobalVariables().getVariableAll()).replace("%2$f", String.valueOf(System.currentTimeMillis() - start) + "ms.")));
                        return;
                    } catch (RuntimeException ex) {
                        sender.sendMessage(getFileAccessor().getLang().getString("commands.reload.failed"));
                        ex.printStackTrace();
                        return;
                    }
                }

                if(args[1].equalsIgnoreCase("globalvariables")) {
                    try{
                        long start = System.currentTimeMillis();
                        SFAccessor.reloadFiles();
                        getGlobalVariables().reloadGlobalVariables();
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.reload.done").replace("%1$f", args[1]).replace("%2$f", String.valueOf(System.currentTimeMillis() - start) + "ms.")));
                        return;
                    } catch (RuntimeException ex) {
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.reload.failed")));
                        ex.printStackTrace();
                        return;
                    }
                }

                if(args[1].equalsIgnoreCase("settings")) {
                    try{
                        long start = System.currentTimeMillis();
                        SFAccessor.reloadFiles();
                        getConfigSettings().reloadConfig();
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.reload.done").replace("%1$f", args[1]).replace("%2$f", String.valueOf(System.currentTimeMillis() - start) + "ms.")));
                        return;
                    } catch (RuntimeException ex) {
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.reload.failed")));
                        ex.printStackTrace();
                        return;
                    }
                }

                if(args[1].equalsIgnoreCase("commandlimiter")) {
                    try{
                        long start = System.currentTimeMillis();
                        SFAccessor.reloadFiles();
                        getCommandLimiterSettings().reloadCommandLimiterSettings();
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.reload.done").replace("%1$f", args[1]).replace("%2$f", String.valueOf(System.currentTimeMillis() - start) + "ms.")));
                        return;
                    } catch (RuntimeException ex) {
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.reload.failed")));
                        ex.printStackTrace();
                        return;
                    }
                }

                if(args[1].equalsIgnoreCase("protectionsettings")) {
                    try{
                        long start = System.currentTimeMillis();
                        SFAccessor.reloadFiles();
                        getProtectionSettings().reloadProtectionSettings();
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.reload.done").replace("%1$f", args[1]).replace("%2$f", String.valueOf(System.currentTimeMillis() - start) + "ms.")));
                        return;
                    } catch (RuntimeException ex) {
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.reload.failed")));
                        ex.printStackTrace();
                        return;
                    }
                }

                if(args[1].equalsIgnoreCase("cooldowns")) {
                    try{
                        long start = System.currentTimeMillis();
                        SFAccessor.reloadFiles();
                        Cooldowns.getCooldowns().reloadCooldowns(sender);
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.reload.done").replace("%1$f", args[1]).replace("%2$f", String.valueOf(System.currentTimeMillis() - start) + "ms.")));
                        return;
                    } catch (RuntimeException ex) {
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.reload.failed")));
                        ex.printStackTrace();
                        return;
                    }
                }

                if(args[1].equalsIgnoreCase("chatsettings")) {
                    try{
                        long start = System.currentTimeMillis();
                        SFAccessor.reloadFiles();
                        getChatSettings().reloadChatSettings();
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.reload.done").replace("%1$f", args[1]).replace("%2$f", String.valueOf(System.currentTimeMillis() - start) + "ms.")));
                        return;
                    } catch (RuntimeException ex) {
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.reload.failed")));
                        ex.printStackTrace();
                        return;
                    }
                }

                sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.reload.unknown-type").replace("%1$f", args[1])));
                return;

            }


            if(args[0].equalsIgnoreCase("purge")) {
                if(sender.hasPermission("functionalservercontrol.purge")) {
                    if(args.length == 1 || args.length > 3) {
                        if(getConfigSettings().showDescription()) { sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.purge.description").replace("%1$f",command.getName() + " purge"))); }
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.purge.usage").replace("%1$f", command.getName() + " purge")));
                        if(getConfigSettings().showExamples()) { sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.purge.example").replace("%1$f", command.getName() + " purge"))); }
                        return;
                    }
                    if(args[1].equalsIgnoreCase("history")) {
                        BaseManager.getBaseManager().clearHistory();
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.purge.history.cleared")));
                        return;
                    }
                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.purge.unknown-type").replace("%1$f", args[1])));
                    return;
                } else {
                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                    return;
                }
            }

            if(args[0].equalsIgnoreCase("import")) {
                if(sender.hasPermission("functionalservercontrol.import")) {
                    if (args.length == 1 || args.length > 2) {
                        if (getConfigSettings().showDescription())
                            sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.import.description").replace("%1$f", label + " " + args[0])));
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.import.usage").replace("%1$f", label + " " + args[0])));
                        if (getConfigSettings().showExamples())
                            sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.import.example").replace("%1$f", label + " " + args[0])));
                        return;
                    }

                    if (args[1].equalsIgnoreCase("vanilla")) {
                        ImportManager.importDataFromVanilla(sender);
                        return;
                    }

                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.import.unknown-type").replace("%1$f", args[1])));
                } else {
                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                }
                return;
            }

            if(args[0].equalsIgnoreCase("history")) {
                if(sender.hasPermission("functionalservercontrol.history")) {
                    if(args.length == 1) {
                        if(getConfigSettings().showDescription()) sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.history.description").replace("%1$f", label + " history")));
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.history.usage").replace("%1$f", label + " history")));
                        if(getConfigSettings().showExamples()) sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.history.example").replace("%1$f", label + " history")));
                        return;
                    }
                    if(args.length == 2) {
                        int lines;
                        try {
                            lines = Integer.parseInt(args[1]);
                        } catch (NumberFormatException ignored) {
                            sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.history.not-num").replace("%1$f", args[1])));
                            return;
                        }
                        InformationManager.sendHistory(sender, lines, null);
                        return;
                    }
                    if(args.length > 2) {
                        if(!args[2].equalsIgnoreCase("attribute")) {
                            if(getConfigSettings().showDescription()) sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.history.description").replace("%1$f", label + " history")));
                            sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.history.usage").replace("%1$f", label + " history")));
                            if(getConfigSettings().showExamples()) sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.history.example").replace("%1$f", label + " history")));
                            return;
                        }
                        if(args.length > 4) {
                            if(getConfigSettings().showDescription()) sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.history.description").replace("%1$f", label + " history")));
                            sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.history.usage").replace("%1$f", label + " history")));
                            if(getConfigSettings().showExamples()) sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.history.example").replace("%1$f", label + " history")));
                            return;
                        }
                        int lines;
                        try {
                            lines = Integer.parseInt(args[1]);
                        } catch (NumberFormatException ignored) {
                            sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.history.not-num").replace("%1$f", args[1])));
                            return;
                        }
                        InformationManager.sendHistory(sender, lines, args[3]);
                        return;
                    }
                } else {
                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                }
                return;
            }

            if(args[0].equalsIgnoreCase("getstatistic")) {
                if(sender.hasPermission("functionalservercontrol.getstatistic")) {
                    if(args.length != 3) {
                        if(getConfigSettings().showDescription()) sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.getstatistic.description").replace("%1$f", label + " getstatistic")));
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.getstatistic.usage").replace("%1$f", label + " getstatistic")));
                        if(getConfigSettings().showExamples()) sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.getstatistic.example").replace("%1$f", label + " getstatistic")));
                        return;
                    }
                    if(args[1].equalsIgnoreCase("player")) {
                        InformationManager.sendStatistic(sender, "player", args[2]);
                        return;
                    }
                    if(args[1].equalsIgnoreCase("admin")) {
                        InformationManager.sendStatistic(sender, "admin", args[2]);
                        return;
                    }
                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.getstatistic.unknown-type").replace("%1$f", args[1])));
                } else {
                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                }
                return;
            }

            if(args[0].equalsIgnoreCase("inetspeed")) {
                if(sender.hasPermission("fucntionalservercontrol.inetspeed")) {
                    new InetManager().preformInetTest(sender);
                } else {
                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                }
                return;
            }

            if(sender.hasPermission("functionalservercontrol.help")) {
                sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.unknown-subcommand").replace("%1$f", args[0])));
            } else {
                sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            }
        });
        return true;
    }
}
