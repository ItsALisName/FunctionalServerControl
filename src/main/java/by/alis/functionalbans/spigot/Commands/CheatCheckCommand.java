package by.alis.functionalbans.spigot.Commands;

import by.alis.functionalbans.spigot.Commands.Completers.CheatCheckCompleter;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.getReason;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalbans.spigot.Managers.CheatCheckerManager.getCheatCheckerManager;
import static by.alis.functionalbans.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class CheatCheckCommand implements CommandExecutor {

    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();

    FunctionalBansSpigot plugin;
    public CheatCheckCommand(FunctionalBansSpigot plugin) {
        this.plugin = plugin;
        plugin.getCommand("cheatcheck").setExecutor(this);
        plugin.getCommand("cheatcheck").setTabCompleter(new CheatCheckCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(sender.hasPermission("functionalbans.cheatcheck")) {

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
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                        return true;
                    }
                    getCheatCheckerManager().startCheck(sender, player, null, getConfigSettings().getDefaultCheatCheckTime());
                    return true;
                }

                if(args.length == 3 && args[0].equalsIgnoreCase("start") && (param || this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2]))) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if(player == null) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                        return true;
                    }
                    int time = getConfigSettings().getDefaultCheatCheckTime();
                    if(this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
                        time = (int)(this.timeSettingsAccessor.getTimeManager().convertToMillis(args[2]) - System.currentTimeMillis()) / 1000;
                    } else {
                        time = Integer.parseInt(args[2]);
                    }
                    getCheatCheckerManager().startCheck(sender, player, null, time);
                    return true;
                }

                if(args.length >= 3 && args[0].equalsIgnoreCase("start")) {
                    if(args.length == 3 && (param || this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2]))) {
                        Player player = Bukkit.getPlayer(args[1]);
                        if(player == null) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                            return true;
                        }
                        int time = getConfigSettings().getDefaultCheatCheckTime();
                        if(this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
                            time = (int)(this.timeSettingsAccessor.getTimeManager().convertToMillis(args[2]) - System.currentTimeMillis()) / 1000;
                        } else {
                            time = Integer.parseInt(args[2]);
                        }
                        int finalTime = time;
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            getCheatCheckerManager().startCheck(sender, player, null, finalTime);
                        });
                        return true;
                    }
                    if(args.length > 3 && (param || this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2]))) {
                        Player player = Bukkit.getPlayer(args[1]);
                        if(player == null) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                            return true;
                        }
                        int time = getConfigSettings().getDefaultCheatCheckTime();
                        if(this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
                            time = (int)(this.timeSettingsAccessor.getTimeManager().convertToMillis(args[2]) - System.currentTimeMillis()) / 1000;
                        } else {
                            time = Integer.parseInt(args[2]);
                        }
                        int finalTime = time;
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            getCheatCheckerManager().startCheck(sender, player, getReason(args, 3), finalTime);
                        });
                        return true;
                    }

                    Player player = Bukkit.getPlayer(args[1]);
                    if(player == null) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                        return true;
                    }
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        getCheatCheckerManager().startCheck(sender, player, getReason(args, 2), getConfigSettings().getDefaultCheatCheckTime());
                    });
                    return true;
                }

                if(args.length == 2 && args[0].equalsIgnoreCase("stop")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if(player == null) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                        return true;
                    }
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        getCheatCheckerManager().stopCheck(sender, player);
                    });
                    return true;
                }

                if(args.length == 2 && args[0].equalsIgnoreCase("confirm")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if(player == null) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                        return true;
                    }
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        getCheatCheckerManager().preformActionOnConfirm(sender, player);
                    });
                    return true;
                }

                if(args.length == 2 && args[0].equalsIgnoreCase("refute")) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if(player == null) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.target-offline").replace("%1$f", args[1])));
                        return true;
                    }
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        getCheatCheckerManager().preformActionOnFail(sender, player);
                    });
                    return true;
                }

                // TODO: 23.12.2022 ПОЯСНЕНИЯ ДЛЯ КОМАНД

                if(args.length == 0) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.example").replace("%1$f", label))); }
                    return true;
                }


                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.cheatcheck.unknown-subcommand").replace("%1$f", args[1])));
                return true;

            }
        }
        return true;
    }
}
