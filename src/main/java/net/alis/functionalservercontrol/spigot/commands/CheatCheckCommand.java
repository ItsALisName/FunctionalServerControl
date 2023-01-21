package net.alis.functionalservercontrol.spigot.commands;

import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.commands.completers.CheatCheckCompleter;
import net.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.managers.CheatCheckerManager.getCheatCheckerManager;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class CheatCheckCommand implements CommandExecutor {

    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();

    public CheatCheckCommand(FunctionalServerControlSpigot plugin) {
        plugin.getCommand("cheatcheck").setExecutor(this);
        plugin.getCommand("cheatcheck").setTabCompleter(new CheatCheckCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        TaskManager.preformAsync(() -> {
            if(getConfigSettings().isCheatCheckFunctionEnabled()) {
                if(sender.hasPermission("functionalservercontrol.cheatcheck")) {

                    boolean param = false;
                    if(args.length >= 3){
                        try {
                            Integer.parseInt(args[2]);
                            param = true;
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    if(args.length == 2 && args[0].equalsIgnoreCase("start")) {
                        Player player = Bukkit.getPlayer(args[1]);
                        if(player == null) {
                            sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                            return;
                        }
                        getCheatCheckerManager().startCheck(sender, player, null, getConfigSettings().getDefaultCheatCheckTime());
                        return;
                    }

                    if(args.length == 1 && args[0].equalsIgnoreCase("start")) {
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.missing-argument").replace("%1$f", getConfigSettings().getGlobalLanguage().equalsIgnoreCase("ru_RU") ? "<Игрок>" : "<Player>")));
                        return;
                    }

                    if(args.length == 3 && args[0].equalsIgnoreCase("start") && (param || this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2]))) {
                        if(args[2].startsWith("0")) {
                            sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.zero-time")));
                            return;
                        }
                        Player player = Bukkit.getPlayer(args[1]);
                        if(player == null) {
                            sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                            return;
                        }
                        int time = getConfigSettings().getDefaultCheatCheckTime();
                        if(this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
                            if(args[2].startsWith("0")) {
                                sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.zero-time")));
                                return;
                            }
                            time = (int)(this.timeSettingsAccessor.getTimeManager().convertToMillis(args[2]) - System.currentTimeMillis()) / 1000;
                        } else {
                            time = Integer.parseInt(args[2]);
                        }
                        getCheatCheckerManager().startCheck(sender, player, null, time);
                        return;
                    }

                    if(args.length >= 3 && args[0].equalsIgnoreCase("start")) {
                        if(args.length == 3 && (param || this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2]))) {
                            if(args[2].startsWith("0")) {
                                sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.zero-time")));
                                return;
                            }
                            Player player = Bukkit.getPlayer(args[1]);
                            if(player == null) {
                                sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                                return;
                            }
                            int time = getConfigSettings().getDefaultCheatCheckTime();
                            if(this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
                                if(args[2].startsWith("0")) {
                                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.zero-time")));
                                    return;
                                }
                                time = (int)(this.timeSettingsAccessor.getTimeManager().convertToMillis(args[2]) - System.currentTimeMillis()) / 1000;
                            } else {
                                time = Integer.parseInt(args[2]);
                            }
                            int finalTime = time;
                            getCheatCheckerManager().startCheck(sender, player, null, finalTime);
                            return;
                        }
                        if(args.length > 3 && (param || this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2]))) {
                            if(args[2].startsWith("0")) {
                                sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.zero-time")));
                                return;
                            }
                            Player player = Bukkit.getPlayer(args[1]);
                            if(player == null) {
                                sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                                return;
                            }
                            int time = getConfigSettings().getDefaultCheatCheckTime();
                            if(this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
                                if(args[2].startsWith("0")) {
                                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.zero-time")));
                                    return;
                                }
                                time = (int)(this.timeSettingsAccessor.getTimeManager().convertToMillis(args[2]) - System.currentTimeMillis()) / 1000;
                            } else {
                                time = Integer.parseInt(args[2]);
                            }
                            int finalTime = time;
                            getCheatCheckerManager().startCheck(sender, player, TextUtils.getReason(args, 3), finalTime);
                            return;
                        }

                        Player player = Bukkit.getPlayer(args[1]);
                        if(player == null) {
                            sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                            return;
                        }
                        getCheatCheckerManager().startCheck(sender, player, TextUtils.getReason(args, 2), getConfigSettings().getDefaultCheatCheckTime());
                        return;
                    }

                    if(args.length == 2 && args[0].equalsIgnoreCase("stop")) {
                        Player player = Bukkit.getPlayer(args[1]);
                        if(player == null) {
                            sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                            return;
                        }
                        getCheatCheckerManager().stopCheck(sender, player);
                        return;
                    }

                    if(args.length == 1 && args[0].equalsIgnoreCase("stop")) {
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.missing-argument").replace("%1$f", getConfigSettings().getGlobalLanguage().equalsIgnoreCase("ru_RU") ? "<Игрок>" : "<Player>")));
                        return;
                    }

                    if(args.length >= 3 && args[0].equalsIgnoreCase("stop")) {
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.too-many-arguments").replace("%1$f", TextUtils.getReason(args, 2))));
                        return;
                    }

                    if(args.length == 2 && args[0].equalsIgnoreCase("confirm")) {
                        Player player = Bukkit.getPlayer(args[1]);
                        if(player == null) {
                            sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                            return;
                        }
                        getCheatCheckerManager().preformActionOnConfirm(sender, player);
                        return;
                    }

                    if(args.length == 1 && args[0].equalsIgnoreCase("confirm")) {
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.missing-argument").replace("%1$f", getConfigSettings().getGlobalLanguage().equalsIgnoreCase("ru_RU") ? "<Игрок>" : "<Player>")));
                        return;
                    }

                    if(args.length >= 3 && args[0].equalsIgnoreCase("confirm")) {
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.too-many-arguments").replace("%1$f", TextUtils.getReason(args, 2))));
                        return;
                    }

                    if(args.length == 2 && args[0].equalsIgnoreCase("refute")) {
                        Player player = Bukkit.getPlayer(args[1]);
                        if(player == null) {
                            sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                            return;
                        }
                        getCheatCheckerManager().preformActionOnFail(sender, player);
                        return;
                    }

                    if(args.length == 1 && args[0].equalsIgnoreCase("refute")) {
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.missing-argument").replace("%1$f", getConfigSettings().getGlobalLanguage().equalsIgnoreCase("ru_RU") ? "<Игрок>" : "<Player>")));
                        return;
                    }

                    if(args.length >= 3 && args[0].equalsIgnoreCase("refute")) {
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.too-many-arguments").replace("%1$f", TextUtils.getReason(args, 2))));
                        return;
                    }

                    if(args.length == 0) {
                        if(getConfigSettings().showDescription()) { sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.description").replace("%1$f", label))); }
                        sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.usage").replace("%1$f", label)));
                        if(getConfigSettings().showExamples()) { sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.example").replace("%1$f", label))); }
                        return;
                    }


                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("commands.cheatcheck.unknown-subcommand").replace("%1$f", args[1])));
                    return;

                } else {
                    sender.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                    return;
                }
            }
        });
        return true;
    }
}
