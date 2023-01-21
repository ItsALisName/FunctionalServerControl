package net.alis.functionalservercontrol.spigot.commands;

import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.api.enums.BanType;
import net.alis.functionalservercontrol.spigot.commands.completers.BanCompleter;
import net.alis.functionalservercontrol.spigot.managers.ban.BanManager;
import net.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.getReason;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

/**
 * The class responsible for executing the "/ban" command
 */
public class BanCommand implements CommandExecutor {

    public BanCommand(FunctionalServerControlSpigot plugin) {
        plugin.getCommand("ban").setExecutor(this);
        plugin.getCommand("ban").setTabCompleter(new BanCompleter());
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        TaskManager.preformAsync(() -> {
            TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
            BanManager banManager = new BanManager();
            if (sender.hasPermission("functionalservercontrol.ban")) {
                if(args.length >= 1) {
                    if (args.length == 1) {
                        if(args[0].equalsIgnoreCase("-s")) {
                            if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.description").replace("%1$f", label))); }
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.usage").replace("%1$f", label)));
                            if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.example").replace("%1$f", label))); }
                            return;
                        }

                        if(OtherUtils.isArgumentIP(args[0])) {
                            if(OtherUtils.isNotNullIp(args[0])) {
                                if(OtherUtils.getPlayerByIP(args[0]) != null) {
                                    OfflinePlayer player = OtherUtils.getPlayerByIP(args[0]);
                                    if(player.isOnline()) {
                                        if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                            return;
                                        }
                                    }
                                    if(!player.isOnline()) {
                                        if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true);
                                    } else {
                                        banManager.preformBan(player, BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true);
                                    }
                                    return;
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        banManager.preformBanByIp(args[0], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true, false);
                                    } else {
                                        banManager.preformBanByIp(args[0], BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true, false);
                                    }
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                    banManager.preformBanByIp(args[0], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true, true);
                                } else {
                                    banManager.preformBanByIp(args[0], BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true, true);
                                }
                            }
                            return;
                        }

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                        if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                            if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return;
                            }
                            if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                banManager.preformBan(args[0], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true);
                            } else {
                                banManager.preformBan(args[0], BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true);
                            }
                        } else {
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                    return;
                                }
                            }
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                            }
                            if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true);
                            } else {
                                banManager.preformBan(player, BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true);
                            }
                        }
                        return;
                    }


                    if(args.length >= 2) {
                        if(args[0].equalsIgnoreCase("-s") && args.length == 2) {
                            if(OtherUtils.isArgumentIP(args[1])) {
                                if(OtherUtils.isNotNullIp(args[1])) {
                                    if(OtherUtils.getPlayerByIP(args[1]) != null) {
                                        OfflinePlayer player = OtherUtils.getPlayerByIP(args[1]);
                                        if(player.isOnline()) {
                                            if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                                return;
                                            }
                                        }
                                        if(!player.isOnline()) {
                                            if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                        }
                                        if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                            banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false);
                                        } else {
                                            banManager.preformBan(player, BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false);
                                        }
                                        return;
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                            banManager.preformBanByIp(args[1], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false, false);
                                        } else {
                                            banManager.preformBanByIp(args[1], BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false, false);
                                        }
                                        return;
                                    }
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        banManager.preformBanByIp(args[1], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false, true);
                                    } else {
                                        banManager.preformBanByIp(args[1], BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false, true);
                                    }
                                    return;
                                }
                            }

                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                            if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                                if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                    banManager.preformBan(args[1], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false);
                                } else {
                                    banManager.preformBan(args[1], BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false);
                                }
                            } else {
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                        return;
                                    }
                                }
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                }
                                if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                    banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false);
                                } else {
                                    banManager.preformBan(player, BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false);
                                }
                            }
                            return;
                        }

                        if(args.length > 2 && args[0].equalsIgnoreCase("-s") && !timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {

                            if(args[2].startsWith("0")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.zero-time")));
                                return;
                            }

                            if(OtherUtils.isArgumentIP(args[1])) {
                                if(OtherUtils.isNotNullIp(args[1])) {
                                    if(OtherUtils.getPlayerByIP(args[1]) != null) {
                                        OfflinePlayer player = OtherUtils.getPlayerByIP(args[1]);
                                        if(player.isOnline()) {
                                            if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                                return;
                                            }
                                        }
                                        if(!player.isOnline()) {
                                            if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                        }
                                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                            banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false);
                                        } else {
                                            banManager.preformBan(player, BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false);
                                        }
                                        return;
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                            banManager.preformBanByIp(args[1], BanType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false, false);
                                        } else {
                                            banManager.preformBanByIp(args[1], BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false, false);
                                        }
                                        return;
                                    }
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        banManager.preformBanByIp(args[1], BanType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false, true);
                                    } else {
                                        banManager.preformBanByIp(args[1], BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false, true);
                                    }
                                    return;
                                }
                            }

                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                            if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                                if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                    banManager.preformBan(args[1], BanType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false);
                                } else {
                                    banManager.preformBan(args[1], BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false);
                                }
                            } else {
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                        return;
                                    }
                                }
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                    banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false);
                                } else {
                                    banManager.preformBan(player, BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false);
                                }
                            }
                            return;
                        }
                        // A Bans with time

                        if(timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[1]) && args.length == 2) {
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
                                            if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                        }
                                        if(player.isOnline()) {
                                            if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                                return;
                                            }
                                        }
                                        banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true);
                                        return;
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        banManager.preformBanByIp(args[0], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true, false);
                                        return;
                                    }
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    banManager.preformBanByIp(args[0], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true, true);
                                    return;
                                }
                            }

                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                            if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                                if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                banManager.preformBan(args[0], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true);
                                return;
                            } else {
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                }
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                        return;
                                    }
                                }
                                banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true);
                            }
                            return;
                        }

                        if(args.length == 3 && args[0].equalsIgnoreCase("-s") && timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
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
                                            if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                        }
                                        if(player.isOnline()) {
                                            if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                                return;
                                            }
                                        }
                                        banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false);
                                        return;
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        banManager.preformBanByIp(args[1], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false, false);
                                        return;
                                    }
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    banManager.preformBanByIp(args[1], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false, true);
                                    return;
                                }
                            }

                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                            if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                                if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                banManager.preformBan(args[1], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false);
                                return;
                            } else {
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                }
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                        return;
                                    }
                                }
                                banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false);
                                return;
                            }

                        }

                        if(args.length > 2 && timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[1])) {
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
                                            if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                        }
                                        if(player.isOnline()) {
                                            if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                                return;
                                            }
                                        }
                                        banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 2), sender, time, true);
                                        return;
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        banManager.preformBanByIp(args[0], BanType.TIMED_NOT_IP, getReason(args, 2), sender, time, true, false);
                                        return;
                                    }
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    banManager.preformBanByIp(args[0], BanType.TIMED_NOT_IP, getReason(args, 2), sender, time, true, true);
                                    return;
                                }
                            }

                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                            if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                                if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                banManager.preformBan(args[0], BanType.TIMED_NOT_IP, getReason(args, 2), sender, time, true);
                                return;
                            } else {
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                }
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                        return;
                                    }
                                }
                                banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 2), sender, time, true);
                                return;
                            }
                        }

                        if(args.length > 3 && args[0].equalsIgnoreCase("-s") && timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
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
                                            if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                        }
                                        if(player.isOnline()) {
                                            if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                                return;
                                            }
                                        }
                                        banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 3), sender, time, false);
                                        return;
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        banManager.preformBanByIp(args[1], BanType.TIMED_NOT_IP, getReason(args, 3), sender, time, false, false);
                                        return;
                                    }
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    banManager.preformBanByIp(args[1], BanType.TIMED_NOT_IP, getReason(args, 3), sender, time, false, true);
                                    return;
                                }
                            }

                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                            if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                                if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                banManager.preformBan(args[1], BanType.TIMED_NOT_IP, getReason(args, 3), sender, time, false);
                                return;
                            } else {
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                }
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                        return;
                                    }
                                }
                                banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 3), sender, time, false);
                                return;
                            }
                        }

                        // A Bans with time

                        if(OtherUtils.isArgumentIP(args[0])) {
                            if(OtherUtils.isNotNullIp(args[0])) {
                                if(OtherUtils.getPlayerByIP(args[0]) != null) {
                                    OfflinePlayer player = OtherUtils.getPlayerByIP(args[0]);
                                    if(!player.isOnline()) {
                                        if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                    }
                                    if(player.isOnline()) {
                                        if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                            return;
                                        }
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true);
                                    } else {
                                        banManager.preformBan(player, BanType.PERMANENT_NOT_IP, getReason(args, 1), sender, -1, true);
                                    }
                                    return;
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        banManager.preformBanByIp(args[0], BanType.TIMED_NOT_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true, false);
                                    } else {
                                        banManager.preformBanByIp(args[0], BanType.PERMANENT_NOT_IP, getReason(args, 1), sender, -1, true, false);
                                    }
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                    banManager.preformBanByIp(args[0], BanType.TIMED_NOT_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true, true);
                                } else {
                                    banManager.preformBanByIp(args[0], BanType.PERMANENT_NOT_IP, getReason(args, 1), sender, -1, true, true);
                                }
                                return;
                            }
                        }

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                        if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                            if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return;
                            }
                            if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                banManager.preformBan(args[0], BanType.TIMED_NOT_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true);
                            } else {
                                banManager.preformBan(args[0], BanType.PERMANENT_NOT_IP, getReason(args, 1), sender, -1, true);
                            }
                            return;
                        } else {
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                            }
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                    return;
                                }
                            }
                            if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true);
                            } else {
                                banManager.preformBan(player, BanType.PERMANENT_NOT_IP, getReason(args, 1), sender, -1, true);
                            }
                            return;
                        }

                    }

                }

                if (args.length == 0) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.example").replace("%1$f", label))); }
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
