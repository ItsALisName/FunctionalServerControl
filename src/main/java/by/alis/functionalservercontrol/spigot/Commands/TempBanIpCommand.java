package by.alis.functionalservercontrol.spigot.Commands;

import by.alis.functionalservercontrol.API.Enums.BanType;
import by.alis.functionalservercontrol.spigot.Additional.Misc.OtherUtils;
import by.alis.functionalservercontrol.spigot.Commands.Completers.TempBanIpCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.Managers.Bans.BanManager;
import by.alis.functionalservercontrol.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.getReason;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class TempBanIpCommand implements CommandExecutor {

    FunctionalServerControl plugin;
    public TempBanIpCommand(FunctionalServerControl plugin) {
        this.plugin = plugin;
        plugin.getCommand("tempbanip").setExecutor(this);
        plugin.getCommand("tempbanip").setTabCompleter(new TempBanIpCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
        BanManager banManager = new BanManager();

        if(sender.hasPermission("functionalservercontrol.tempban-ip")) {

            if(args.length >= 1) {


                if(args.length == 1 && args[0].equalsIgnoreCase("-s")) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.example").replace("%1$f", label))); }
                    return true;
                }

                if(args.length == 1 && args[0].equalsIgnoreCase("-a")) {
                    if(sender.hasPermission("functionalservercontrol.use.unsafe-flags")) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-not-support").replace("%1$f", args[0])));
                    } else {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", label)));
                    }
                    return true;
                }

                if(args.length == 1) {
                    if (getConfigSettings().showDescription()) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.description").replace("%1$f", label)));
                    }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.usage").replace("%1$f", label)));
                    if (getConfigSettings().showExamples()) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.example").replace("%1$f", label)));
                    }
                }

                if(args.length == 2 && args[0].equalsIgnoreCase("-s")) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.example").replace("%1$f", label))); }
                    return true;
                }

                if(args.length == 2 && args[0].equalsIgnoreCase("-a")) {
                    if(sender.hasPermission("functionalservercontrol.use.unsafe-flags")) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-not-support").replace("%1$f", args[0])));
                    } else {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", label)));
                    }
                    return true;
                }

                if(args.length == 2) {
                    if(timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[1])) {
                        if(args[1].startsWith("0")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.zero-time")));
                            return true;
                        }
                        long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[1]);

                        if(OtherUtils.isArgumentIP(args[0])) {

                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                if(OtherUtils.isNotNullIp(args[0])) {
                                    if(OtherUtils.getPlayerByIP(args[0]) != null) {
                                        OfflinePlayer player = OtherUtils.getPlayerByIP(args[0]);
                                        if(!player.isOnline()) {
                                            if(!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                        }
                                        if(player.isOnline()) {
                                            if(player.getPlayer().hasPermission("functionalservercontrol.ban-ip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                                return;
                                            }
                                        }
                                        banManager.preformBan(player, BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "tempbanip");
                                        return;
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        banManager.preformBanByIp(args[0], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "tempbanip", false);
                                        return;
                                    }
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    banManager.preformBanByIp(args[0], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "tempbanip", true);
                                    return;
                                }
                            });
                            return true;
                        }

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                        if(!OtherUtils.isNotNullPlayer(args[0])) {
                            if(!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(args[0], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "tempbanip");
                            });
                            return true;
                        } else {
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalservercontrol.ban-ip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                    return true;
                                }
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(player, BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "tempbanip");
                            });
                        }

                    } else {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-time").replace("%1$f", args[1])));
                        return true;
                    }
                }

                if(args.length >= 3) {

                    if(args.length >= 3 && args[0].equalsIgnoreCase("-a")) {
                        if(sender.hasPermission("functionalservercontrol.use.unsafe-flags")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-not-support").replace("%1$f", args[0])));
                        } else {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.flag-no-perms").replace("%1$f", label)));
                        }
                        return true;
                    }

                    if(args.length == 3 && args[0].equalsIgnoreCase("-s")) {
                        if(timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
                            if(args[2].startsWith("0")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.zero-time")));
                                return true;
                            }
                            long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[2]);

                            if(OtherUtils.isArgumentIP(args[1])) {

                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    if(OtherUtils.isNotNullIp(args[1])) {
                                        if(OtherUtils.getPlayerByIP(args[1]) != null) {
                                            OfflinePlayer player = OtherUtils.getPlayerByIP(args[1]);
                                            if(!player.isOnline()) {
                                                if(!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                    return;
                                                }
                                            }
                                            if(player.isOnline()) {
                                                if(player.getPlayer().hasPermission("functionalservercontrol.ban-ip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                                    return;
                                                }
                                            }
                                            banManager.preformBan(player, BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "tempbanip");
                                            return;
                                        } else {
                                            if(!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                            banManager.preformBanByIp(args[1], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "tempbanip", false);
                                            return;
                                        }
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        banManager.preformBanByIp(args[1], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "tempbanip", true);
                                        return;
                                    }
                                });
                                return true;
                            }

                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                            if(!OtherUtils.isNotNullPlayer(args[1])) {
                                if(!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    banManager.preformBan(args[1], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "tempbanip");
                                });
                                return true;
                            } else {
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return true;
                                    }
                                }
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalservercontrol.ban-ip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                        return true;
                                    }
                                }
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    banManager.preformBan(player, BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "tempbanip");
                                });
                            }

                        } else {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-time").replace("%1$f", args[2])));
                            return true;
                        }
                    }

                    if(args.length > 3 && args[0].equalsIgnoreCase("-s")) {

                        if (timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
                            if(args[2].startsWith("0")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.zero-time")));
                                return true;
                            }
                            long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[2]);

                            if(OtherUtils.isArgumentIP(args[1])) {

                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    if(OtherUtils.isNotNullIp(args[1])) {
                                        if(OtherUtils.getPlayerByIP(args[1]) != null) {
                                            OfflinePlayer player = OtherUtils.getPlayerByIP(args[1]);
                                            if (!player.isOnline()) {
                                                if (!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                    return;
                                                }
                                            }
                                            if (player.isOnline()) {
                                                if (player.getPlayer().hasPermission("functionalservercontrol.ban-ip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                                    return;
                                                }
                                            }
                                            banManager.preformBan(player, BanType.TIMED_IP, getReason(args, 3), sender, time, false, "tempbanip");
                                            return;
                                        } else {
                                            if (!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                            banManager.preformBanByIp(args[1], BanType.TIMED_IP, getReason(args, 3), sender, time, false, "tempbanip", false);
                                            return;
                                        }
                                    } else {
                                        if (!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        banManager.preformBanByIp(args[1], BanType.TIMED_IP, getReason(args, 3), sender, time, false, "tempbanip", true);
                                        return;
                                    }
                                });
                                return true;
                            }

                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                            if (!OtherUtils.isNotNullPlayer(args[1])) {
                                if (!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    banManager.preformBan(args[1], BanType.TIMED_IP, getReason(args, 3), sender, time, false, "tempbanip");
                                });
                                return true;
                            } else {
                                if (!player.isOnline()) {
                                    if (!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return true;
                                    }
                                }
                                if (player.isOnline()) {
                                    if (player.getPlayer().hasPermission("functionalservercontrol.ban-ip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                        return true;
                                    }
                                }
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    banManager.preformBan(player, BanType.TIMED_IP, getReason(args, 3), sender, time, false, "tempbanip");
                                });
                            }
                            return true;
                        } else {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-time").replace("%1$f", args[2])));
                        }
                        return true;
                    }

                    if (timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[1])) {
                        if(args[1].startsWith("0")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.zero-time")));
                            return true;
                        }
                        long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[1]);

                        if(OtherUtils.isArgumentIP(args[0])) {

                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                if(OtherUtils.isNotNullIp(args[0])) {
                                    if(OtherUtils.getPlayerByIP(args[0]) != null) {
                                        OfflinePlayer player = OtherUtils.getPlayerByIP(args[0]);
                                        if (!player.isOnline()) {
                                            if (!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                        }
                                        if (player.isOnline()) {
                                            if (player.getPlayer().hasPermission("functionalservercontrol.ban-ip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                                return;
                                            }
                                        }
                                        banManager.preformBan(player, BanType.TIMED_IP, getReason(args, 2), sender, time, true, "tempbanip");
                                        return;
                                    } else {
                                        if (!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        banManager.preformBanByIp(args[0], BanType.TIMED_IP, getReason(args, 2), sender, time, true, "tempbanip", false);
                                        return;
                                    }
                                } else {
                                    if (!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    banManager.preformBanByIp(args[0], BanType.TIMED_IP, getReason(args, 2), sender, time, true, "tempbanip", true);
                                    return;
                                }
                            });
                            return true;
                        }

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                        if (!OtherUtils.isNotNullPlayer(args[0])) {
                            if (!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(args[0], BanType.TIMED_IP, getReason(args, 2), sender, time, true, "tempbanip");
                            });
                            return true;
                        } else {
                            if (!player.isOnline()) {
                                if (!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if (player.isOnline()) {
                                if (player.getPlayer().hasPermission("functionalservercontrol.ban-ip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                    return true;
                                }
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(player, BanType.TIMED_IP, getReason(args, 2), sender, time, true, "tempbanip");
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
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.description").replace("%1$f", label))); }
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.example").replace("%1$f", label))); }
                return true;
            }
        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            return true;
        }

        return true;
    }
}
