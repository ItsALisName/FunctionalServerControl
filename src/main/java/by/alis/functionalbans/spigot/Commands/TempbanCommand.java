package by.alis.functionalbans.spigot.Commands;

import by.alis.functionalbans.API.Enums.BanType;
import by.alis.functionalbans.spigot.Additional.Other.OtherUtils;
import by.alis.functionalbans.spigot.Commands.Completers.TempbanCompleter;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.Bans.BanManager;
import by.alis.functionalbans.spigot.Managers.Files.FileAccessor;
import by.alis.functionalbans.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.getReason;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalbans.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class TempbanCommand implements CommandExecutor {

    FunctionalBansSpigot plugin;
    public TempbanCommand(FunctionalBansSpigot plugin) {
        this.plugin = plugin;
        plugin.getCommand("tempban").setExecutor(this);
        plugin.getCommand("tempban").setTabCompleter(new TempbanCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
        BanManager banManager = new BanManager();
        
        if(sender.hasPermission("functionalbans.temp-ban")) {

            if(args.length >= 1) {


                if(args.length == 1 && args[0].equalsIgnoreCase("-s")) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban.example").replace("%1$f", label))); }
                    return true;
                }

                if(args.length == 1 && args[0].equalsIgnoreCase("-a")) {
                    if(sender.hasPermission("functionalbans.use.unsafe-flags")) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-not-support").replace("%1$f", args[0])));
                    } else {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", label)));
                    }
                    return true;
                }

                if(args.length == 1) {
                    if (getConfigSettings().showDescription()) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban.description").replace("%1$f", label)));
                    }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban.usage").replace("%1$f", label)));
                    if (getConfigSettings().showExamples()) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban.example").replace("%1$f", label)));
                    }
                }

                if(args.length == 2 && args[0].equalsIgnoreCase("-s")) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban.example").replace("%1$f", label))); }
                    return true;
                }

                if(args.length == 2 && args[0].equalsIgnoreCase("-a")) {
                    if(sender.hasPermission("functionalbans.use.unsafe-flags")) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-not-support").replace("%1$f", args[0])));
                    } else {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", label)));
                    }
                    return true;
                }

                if(args.length == 2) {
                    if(timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[1])) {

                        long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[1]);

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                        if(!OtherUtils.isNotNullPlayer(args[0])) {
                            if(!sender.hasPermission("functionalbans.temp-ban.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(args[0], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true);
                            });
                            return true;
                        } else {
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalbans.temp-ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalbans.ban.bypass") && !sender.hasPermission("functionalbans.bypass-break")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                    return true;
                                }
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true);
                            });
                        }

                    } else {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-time").replace("%1$f", args[1])));
                        return true;
                    }
                }

                if(args.length >= 3) {

                    if(args.length >= 3 && args[0].equalsIgnoreCase("-a")) {
                        if(sender.hasPermission("functionalbans.use.unsafe-flags")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-not-support").replace("%1$f", args[0])));
                        } else {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", label)));
                        }
                        return true;
                    }

                    if(args.length == 3 && args[0].equalsIgnoreCase("-s")) {
                        if(timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {

                            long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[2]);

                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                            if(!OtherUtils.isNotNullPlayer(args[1])) {
                                if(!sender.hasPermission("functionalbans.temp-ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    banManager.preformBan(args[1], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false);
                                });
                                return true;
                            } else {
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalbans.temp-ban.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return true;
                                    }
                                }
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalbans.ban.bypass") && !sender.hasPermission("functionalbans.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                        return true;
                                    }
                                }
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false);
                                });
                            }

                        } else {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-time").replace("%1$f", args[2])));
                            return true;
                        }
                    }

                    if(args.length > 3 && args[0].equalsIgnoreCase("-s")) {

                        if (timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {

                            long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[2]);

                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                            if (!OtherUtils.isNotNullPlayer(args[1])) {
                                if (!sender.hasPermission("functionalbans.temp-ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    banManager.preformBan(args[1], BanType.TIMED_NOT_IP, getReason(args, 3), sender, time, false);
                                });
                                return true;
                            } else {
                                if (!player.isOnline()) {
                                    if (!sender.hasPermission("functionalbans.temp-ban.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return true;
                                    }
                                }
                                if (player.isOnline()) {
                                    if (player.getPlayer().hasPermission("functionalbans.ban.bypass") && !sender.hasPermission("functionalbans.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                        return true;
                                    }
                                }
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 3), sender, time, false);
                                });
                            }
                            return true;
                        } else {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-time").replace("%1$f", args[2])));
                        }
                        return true;
                    }

                    if (timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[1])) {

                        long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[1]);

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                        if (!OtherUtils.isNotNullPlayer(args[0])) {
                            if (!sender.hasPermission("functionalbans.temp-ban.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(args[0], BanType.TIMED_NOT_IP, getReason(args, 2), sender, time, true);
                            });
                            return true;
                        } else {
                            if (!player.isOnline()) {
                                if (!sender.hasPermission("functionalbans.temp-ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if (player.isOnline()) {
                                if (player.getPlayer().hasPermission("functionalbans.ban.bypass") && !sender.hasPermission("functionalbans.bypass-break")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                    return true;
                                }
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 2), sender, time, true);
                            });
                        }
                        return true;

                    } else {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-time").replace("%1$f", args[1])));
                    }
                    return true;

                }
            }


            if(args.length == 0) {
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban.description").replace("%1$f", label))); }
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban.example").replace("%1$f", label))); }
                return true;
            }
        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            return true;
        }

        return true;
    }
}
