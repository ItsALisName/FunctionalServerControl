package by.alis.functionalservercontrol.spigot.Commands;

import by.alis.functionalservercontrol.API.Enums.BanType;
import by.alis.functionalservercontrol.spigot.Additional.Misc.OtherUtils;
import by.alis.functionalservercontrol.spigot.Commands.Completers.BanIpCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.Managers.Bans.BanManager;
import by.alis.functionalservercontrol.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.getReason;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class BanIpCommand implements CommandExecutor {

    FunctionalServerControl plugin;
    public BanIpCommand(FunctionalServerControl plugin) {
        this.plugin = plugin;
        plugin.getCommand("banip").setExecutor(this);
        plugin.getCommand("banip").setTabCompleter(new BanIpCompleter());
    }

    private final BanManager banManager = new BanManager();
    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender.hasPermission("functionalservercontrol.ban-ip")) {

            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("-s")) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban-ip.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban-ip.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban-ip.example").replace("%1$f", label))); }
                    return true;
                }

                if(OtherUtils.isArgumentIP(args[0])) {
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        if(OtherUtils.isNotNullIp(args[0])) {
                            if(OtherUtils.getPlayerByIP(args[0]) != null) {

                                OfflinePlayer player = OtherUtils.getPlayerByIP(args[0]);
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalservercontrol.ban-ip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                        return;
                                    }
                                }
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                }

                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                    banManager.preformBan(player, BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true, "banip");
                                    return;
                                } else {
                                    banManager.preformBan(player, BanType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true, "banip");
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        banManager.preformBanByIp(args[0], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true, "banip", false);
                                } else {
                                        banManager.preformBanByIp(args[0], BanType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true, "banip", false);
                                }
                                return;
                            }
                        } else {
                            if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return;
                            }
                            if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                    banManager.preformBanByIp(args[0], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true, "banip", true);
                            } else {
                                    banManager.preformBanByIp(args[0], BanType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true, "banip", true);
                            }
                            return;
                        }
                    });
                    return true;
                }

                OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                    if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                        return true;
                    }
                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            banManager.preformBan(args[0], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true, "banip");
                        });
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            banManager.preformBan(args[0], BanType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true, "banip");
                        });
                    }
                } else {
                    if(player.isOnline()) {
                        if(player.getPlayer().hasPermission("functionalservercontrol.ban-ip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                            return true;
                        }
                    }
                    if(!player.isOnline()) {
                        if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                    }
                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            banManager.preformBan(player, BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true, "banip");
                        });
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            banManager.preformBan(player, BanType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true, "banip");
                        });
                    }
                }
                return true;
            }

            if(args.length >= 2) {
                if(args[0].equalsIgnoreCase("-s") && args.length == 2) {

                    if(OtherUtils.isArgumentIP(args[1])) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            if(OtherUtils.isNotNullIp(args[1])) {
                                if(OtherUtils.getPlayerByIP(args[1]) != null) {
                                    OfflinePlayer player = OtherUtils.getPlayerByIP(args[1]);
                                    if(player.isOnline()) {
                                        if(player.getPlayer().hasPermission("functionalservercontrol.ban-ip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                            return;
                                        }
                                    }
                                    if(!player.isOnline()) {
                                        if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                            banManager.preformBan(player, BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false, "banip");
                                    } else {
                                            banManager.preformBan(player, BanType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false, "banip");
                                    }
                                    return;
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                            banManager.preformBanByIp(args[1], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false, "banip", false);
                                    } else {
                                            banManager.preformBanByIp(args[1], BanType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false, "banip", false);
                                    }
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        banManager.preformBanByIp(args[1], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false, "banip", true);
                                } else {
                                        banManager.preformBanByIp(args[1], BanType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false, "banip", true);
                                }
                                return;
                            }
                        });
                        return true;
                    }

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                        if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(args[1], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false, "banip");
                            });
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(args[1], BanType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false, "banip");
                            });
                        }
                    } else {
                        if(player.isOnline()) {
                            if(player.getPlayer().hasPermission("functionalservercontrol.ban-ip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                return true;
                            }
                        }
                        if(!player.isOnline()) {
                            if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                        }
                        if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(player, BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false, "banip");
                            });
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(player, BanType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false, "banip");
                            });
                        }
                    }
                    return true;
                }

                if(args.length > 2 && args[0].equalsIgnoreCase("-s") && !timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
                    if(args[2].startsWith("0")) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.zero-time")));
                        return true;
                    }
                    if(OtherUtils.isArgumentIP(args[1])) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            if(OtherUtils.isNotNullIp(args[1])) {
                                if(OtherUtils.getPlayerByIP(args[1]) != null) {
                                    OfflinePlayer player = OtherUtils.getPlayerByIP(args[1]);
                                    if(player.isOnline()) {
                                        if(player.getPlayer().hasPermission("functionalservercontrol.ban-ip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                            return;
                                        }
                                    }
                                    if(!player.isOnline()) {
                                        if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                            banManager.preformBan(player, BanType.TIMED_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false, "banip");
                                    } else {
                                            banManager.preformBan(player, BanType.PERMANENT_IP, getReason(args, 2), sender, -1, false, "banip");
                                    }
                                    return;
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                            banManager.preformBanByIp(args[1], BanType.TIMED_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false, "banip", false);
                                    } else {
                                            banManager.preformBanByIp(args[1], BanType.PERMANENT_IP, getReason(args, 2), sender, -1, false, "banip", false);
                                    }
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        banManager.preformBanByIp(args[1], BanType.TIMED_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false, "banip", true);
                                } else {
                                        banManager.preformBanByIp(args[1], BanType.PERMANENT_IP, getReason(args, 2), sender, -1, false, "banip", true);
                                }
                                return;
                            }
                        });
                        return true;
                    }

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                        if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(args[1], BanType.TIMED_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false, "banip");
                            });
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(args[1], BanType.PERMANENT_IP, getReason(args, 2), sender, -1, false, "banip");
                            });
                        }
                    } else {
                        if(player.isOnline()) {
                            if(player.getPlayer().hasPermission("functionalservercontrol.ban-ip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                return true;
                            }
                        }
                        if(!player.isOnline()) {
                            if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                        }
                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(player, BanType.TIMED_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false, "banip");
                            });
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(player, BanType.PERMANENT_IP, getReason(args, 2), sender, -1, false, "banip");
                            });
                        }
                    }
                    return true;
                }
                // A Bans with time

                if(timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[1]) && args.length == 2) {
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
                                        if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
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
                                        banManager.preformBan(player, BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "banip");
                                    return;
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                        banManager.preformBanByIp(args[0], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "banip", false);
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                    banManager.preformBanByIp(args[0], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "banip", true);
                                return;
                            }
                        });
                        return true;
                    }

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                    if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                        if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            banManager.preformBan(args[0], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "banip");
                        });
                        return true;
                    } else {
                        if(!player.isOnline()) {
                            if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
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
                            banManager.preformBan(player, BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "banip");
                        });
                    }
                    return true;
                }

                if(args.length == 3 && args[0].equalsIgnoreCase("-s") && timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
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
                                        if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
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
                                        banManager.preformBan(player, BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "banip");
                                    return;
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                        banManager.preformBanByIp(args[1], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "banip", false);
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                    banManager.preformBanByIp(args[1], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "banip", true);
                                return;
                            }
                        });
                        return true;
                    }

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                        if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            banManager.preformBan(args[1], BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "banip");
                        });
                        return true;
                    } else {
                        if(!player.isOnline()) {
                            if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
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
                            banManager.preformBan(player, BanType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "banip");
                        });
                        return true;
                    }

                }

                if(args.length > 2 && timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[1])) {
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
                                        if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
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
                                    banManager.preformBan(player, BanType.TIMED_IP, getReason(args, 2), sender, time, true, "banip");
                                    return;
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    banManager.preformBanByIp(args[0], BanType.TIMED_IP, getReason(args, 2), sender, time, true, "banip", false);
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                banManager.preformBanByIp(args[0], BanType.TIMED_IP, getReason(args, 2), sender, time, true, "banip", true);
                                return;
                            }
                        });
                        return true;
                    }

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                    if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                        if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            banManager.preformBan(args[0], BanType.TIMED_IP, getReason(args, 2), sender, time, true, "banip");
                        });
                        return true;
                    } else {
                        if(!player.isOnline()) {
                            if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
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
                            banManager.preformBan(player, BanType.TIMED_IP, getReason(args, 2), sender, time, true, "banip");
                        });
                        return true;
                    }
                }

                if(args.length > 3 && args[0].equalsIgnoreCase("-s") && timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
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
                                        if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
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
                                    banManager.preformBan(player, BanType.TIMED_IP, getReason(args, 3), sender, time, false, "banip");
                                    return;
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    banManager.preformBanByIp(args[1], BanType.TIMED_IP, getReason(args, 3), sender, time, false, "banip", false);
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                banManager.preformBanByIp(args[1], BanType.TIMED_IP, getReason(args, 3), sender, time, false, "banip", true);
                                return;
                            }
                        });
                        return true;
                    }

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                        if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            banManager.preformBan(args[1], BanType.TIMED_IP, getReason(args, 3), sender, time, false, "banip");
                        });
                        return true;
                    } else {
                        if(!player.isOnline()) {
                            if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
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
                            banManager.preformBan(player, BanType.TIMED_IP, getReason(args, 3), sender, time, false, "banip");
                        });
                        return true;
                    }
                }

                // A Bans with time

                if(OtherUtils.isArgumentIP(args[0])) {
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        if(OtherUtils.isNotNullIp(args[0])) {
                            if(OtherUtils.getPlayerByIP(args[0]) != null) {
                                OfflinePlayer player = OtherUtils.getPlayerByIP(args[0]);
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
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
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        banManager.preformBan(player, BanType.TIMED_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true, "banip");
                                } else {
                                        banManager.preformBan(player, BanType.PERMANENT_IP, getReason(args, 1), sender, -1, true, "banip");
                                }
                                return;
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        banManager.preformBanByIp(args[0], BanType.TIMED_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true, "banip", false);
                                } else {
                                    banManager.preformBanByIp(args[0], BanType.PERMANENT_IP, getReason(args, 1), sender, -1, true, "banip", false);
                                }
                                return;
                            }
                        } else {
                            if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return;
                            }
                            if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                banManager.preformBanByIp(args[0], BanType.TIMED_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true, "banip", true);
                            } else {
                                banManager.preformBanByIp(args[0], BanType.PERMANENT_IP, getReason(args, 1), sender, -1, true, "banip", true);
                            }
                        }
                    });
                    return true;
                }

                OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                    if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                        return true;
                    }
                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            banManager.preformBan(args[0], BanType.TIMED_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true, "banip");
                        });
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            banManager.preformBan(args[0], BanType.PERMANENT_IP, getReason(args, 1), sender, -1, true, "banip");
                        });
                    }
                    return true;
                } else {
                    if(!player.isOnline()) {
                        if(!sender.hasPermission("functionalservercontrol.ban-ip.offline")) {
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
                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            banManager.preformBan(player, BanType.TIMED_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true, "banip");
                        });
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            banManager.preformBan(player, BanType.PERMANENT_IP, getReason(args, 1), sender, -1, true, "banip");
                        });
                    }
                    return true;
                }

            }

            if (args.length == 0) {
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban-ip.description").replace("%1$f", label))); }
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban-ip.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban-ip.example").replace("%1$f", label))); }
                return true;
            }

        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            return true;
        }

        return true;
    }
}
