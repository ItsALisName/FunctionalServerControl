package net.alis.functionalservercontrol.spigot.commands;

import net.alis.functionalservercontrol.spigot.FunctionalServerControl;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.api.enums.BanType;
import net.alis.functionalservercontrol.spigot.commands.completers.TempBanIpCompleter;
import net.alis.functionalservercontrol.spigot.managers.ban.BanManager;
import net.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.getReason;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class TempBanIpCommand implements CommandExecutor {

    public TempBanIpCommand(FunctionalServerControl plugin) {
        plugin.getCommand("tempbanip").setExecutor(this);
        plugin.getCommand("tempbanip").setTabCompleter(new TempBanIpCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        TaskManager.preformAsync(() -> {
            if(sender.hasPermission("functionalservercontrol.tempban-ip")) {
                TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                BanManager banManager = new BanManager();

                if(args.length >= 1) {


                    if(args.length == 1 && args[0].equalsIgnoreCase("-s")) {
                        if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.description").replace("%1$f", label))); }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.usage").replace("%1$f", label)));
                        if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.example").replace("%1$f", label))); }
                        return;
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
                        return;
                    }

                    if(args.length == 2) {
                        if(timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[1])) {
                            if(args[1].startsWith("0")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.zero-time")));
                                return;
                            }
                            long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[1]);

                            if(OtherUtils.isArgumentIP(args[0])) {

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
                                        banManager.preformBan(player, BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true);
                                        return;
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        banManager.preformBanByIp(args[0], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, false);
                                        return;
                                    }
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    banManager.preformBanByIp(args[0], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, true);
                                    return;
                                }
                            }

                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                            if(!OtherUtils.isNotNullPlayer(args[0])) {
                                if(!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }

                                banManager.preformBan(args[0], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true);

                                return;
                            } else {
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

                                banManager.preformBan(player, BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true);

                            }

                        } else {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-time").replace("%1$f", args[1])));
                            return;
                        }
                    }

                    if(args.length >= 3) {

                        if(args.length == 3 && args[0].equalsIgnoreCase("-s")) {
                            if(timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
                                if(args[2].startsWith("0")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.zero-time")));
                                    return;
                                }
                                long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[2]);

                                if(OtherUtils.isArgumentIP(args[1])) {


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
                                            banManager.preformBan(player, BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false);
                                            return;
                                        } else {
                                            if(!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                            banManager.preformBanByIp(args[1], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, false);
                                            return;
                                        }
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        banManager.preformBanByIp(args[1], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, true);
                                        return;
                                    }
                                }

                                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                                if(!OtherUtils.isNotNullPlayer(args[1])) {
                                    if(!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }

                                    banManager.preformBan(args[1], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false);

                                    return;
                                } else {
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

                                    banManager.preformBan(player, BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false);

                                }

                            } else {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-time").replace("%1$f", args[2])));
                                return;
                            }
                        }

                        if(args.length > 3 && args[0].equalsIgnoreCase("-s")) {

                            if (timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
                                if(args[2].startsWith("0")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.zero-time")));
                                    return;
                                }
                                long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[2]);

                                if(OtherUtils.isArgumentIP(args[1])) {


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
                                            banManager.preformBan(player, BanType.TIMED_IP, getReason(args, 3), sender, time, false);
                                            return;
                                        } else {
                                            if (!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                            banManager.preformBanByIp(args[1], BanType.TIMED_IP, getReason(args, 3), sender, time, false, false);
                                            return;
                                        }
                                    } else {
                                        if (!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        banManager.preformBanByIp(args[1], BanType.TIMED_IP, getReason(args, 3), sender, time, false, true);
                                        return;
                                    }
                                }

                                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                                if (!OtherUtils.isNotNullPlayer(args[1])) {
                                    if (!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }

                                    banManager.preformBan(args[1], BanType.TIMED_IP, getReason(args, 3), sender, time, false);

                                    return;
                                } else {
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

                                    banManager.preformBan(player, BanType.TIMED_IP, getReason(args, 3), sender, time, false);

                                }
                                return;
                            } else {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-time").replace("%1$f", args[2])));
                            }
                            return;
                        }

                        if (timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[1])) {
                            if(args[1].startsWith("0")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.zero-time")));
                                return;
                            }
                            long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[1]);

                            if(OtherUtils.isArgumentIP(args[0])) {


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
                                        banManager.preformBan(player, BanType.TIMED_IP, getReason(args, 2), sender, time, true);
                                        return;
                                    } else {
                                        if (!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        banManager.preformBanByIp(args[0], BanType.TIMED_IP, getReason(args, 2), sender, time, true, false);
                                        return;
                                    }
                                } else {
                                    if (!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    banManager.preformBanByIp(args[0], BanType.TIMED_IP, getReason(args, 2), sender, time, true, true);
                                    return;
                                }
                            }

                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                            if (!OtherUtils.isNotNullPlayer(args[0])) {
                                if (!sender.hasPermission("functionalservercontrol.tempban-ip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }

                                banManager.preformBan(args[0], BanType.TIMED_IP, getReason(args, 2), sender, time, true);

                                return;
                            } else {
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

                                banManager.preformBan(player, BanType.TIMED_IP, getReason(args, 2), sender, time, true);

                            }
                            return;

                        } else {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.unknown-time").replace("%1$f", args[1])));
                        }
                        return;

                    }
                }


                if(args.length == 0) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempban-ip.example").replace("%1$f", label))); }
                    return;
                }
            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                return;
            }
        });
        return true;
    }
}
