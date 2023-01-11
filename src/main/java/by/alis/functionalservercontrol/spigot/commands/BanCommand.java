package by.alis.functionalservercontrol.spigot.commands;

import by.alis.functionalservercontrol.api.enums.BanType;
import by.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import by.alis.functionalservercontrol.spigot.commands.completers.BanCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.managers.ban.BanManager;
import by.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.getReason;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

/**
 * The class responsible for executing the "/ban" command
 */
public class BanCommand implements CommandExecutor {

    FunctionalServerControl plugin;
    public BanCommand(FunctionalServerControl plugin) {
        this.plugin = plugin;
        plugin.getCommand("ban").setExecutor(this);
        plugin.getCommand("ban").setTabCompleter(new BanCompleter());
    }
    private boolean unsafeConfirm;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
        BanManager banManager = new BanManager();
        
        if (sender.hasPermission("functionalservercontrol.ban")) {
            if(args.length >= 1) {

                if(args[0].equalsIgnoreCase("-a") && args.length >= 2) {

                    if(sender.hasPermission("functionalservercontrol.use.unsafe-flags")) {

                        if(args[1].equalsIgnoreCase("-s")) {
                            if (getConfigSettings().isUnsafeActionsConfirmation()) {
                                if (!unsafeConfirm) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-action-confirm-1")));
                                    unsafeConfirm = true;
                                    return true;
                                }
                            }
                            int a = 0;
                            for (Player target : Bukkit.getOnlinePlayers()) {
                                if(target.getName().equalsIgnoreCase(sender.getName())) continue;
                                if (!target.hasPermission("functionalservercontrol.ban.bypass")) {
                                    a = a + 1;
                                    TemporaryCache.setUnsafeBannedPlayers(((OfflinePlayer) target), sender);
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                            banManager.preformBan((target), BanType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false);
                                        });
                                    } else {
                                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                            banManager.preformBan(((OfflinePlayer) target), BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false);
                                        });
                                    }
                                } else if(target.hasPermission("functionalservercontrol.ban.bypass")) {
                                    if(sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        a = a + 1;
                                        TemporaryCache.setUnsafeBannedPlayers(((OfflinePlayer) target), sender);
                                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                                banManager.preformBan(((OfflinePlayer) target), BanType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false);
                                            });
                                        } else {
                                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                                banManager.preformBan(((OfflinePlayer) target), BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false);
                                            });
                                        }
                                    }
                                }
                            }
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-ban-success").replace("%1$f", String.valueOf(a))));
                            if(a != 0) sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-ban-cached-notify")));
                            unsafeConfirm = false;
                            return true;
                        }

                        if (getConfigSettings().isUnsafeActionsConfirmation()) {
                            if (!unsafeConfirm) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-action-confirm-1")));
                                unsafeConfirm = true;
                                return true;
                            }

                            int a = 0;
                            for (Player target : Bukkit.getOnlinePlayers()) {
                                if(target.getName().equalsIgnoreCase(sender.getName())) continue;
                                if (!target.hasPermission("functionalservercontrol.ban.bypass")) {
                                    a = a + 1;
                                    TemporaryCache.setUnsafeBannedPlayers(((OfflinePlayer) target), sender);
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                            banManager.preformBan(((OfflinePlayer) target), BanType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true);
                                        });
                                    } else {
                                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                            banManager.preformBan(((OfflinePlayer) target), BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, true);
                                        });
                                    }
                                } else if(target.hasPermission("functionalservercontrol.ban.bypass")) {
                                    if(sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        a = a + 1;
                                        TemporaryCache.setUnsafeBannedPlayers(((OfflinePlayer) target), sender);
                                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                                banManager.preformBan(((OfflinePlayer) target), BanType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true);
                                            });
                                        } else {
                                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                                banManager.preformBan(((OfflinePlayer) target), BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, true);
                                            });
                                        }
                                    }
                                }
                            }
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-ban-success").replace("%1$f", String.valueOf(a))));
                            if(a != 0) sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-ban-cached-notify")));
                            a = 0;
                            unsafeConfirm = false;
                            return true;
                        }
                    } else {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-flag-no-perms").replace("%1$f", "-a")));
                        return true;
                    }
                }

                if (args.length == 1) {
                    if(args[0].equalsIgnoreCase("-s")) {
                        if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.description").replace("%1$f", label))); }
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.usage").replace("%1$f", label)));
                        if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.example").replace("%1$f", label))); }
                        return true;
                    }

                    if(args[0].equalsIgnoreCase("-a")) {
                        if(sender.hasPermission("functionalservercontrol.use.unsafe-flags")) {

                            if(getConfigSettings().isUnsafeActionsConfirmation()) {
                                if (!unsafeConfirm) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-action-confirm-1")));
                                    unsafeConfirm = true;
                                    return true;
                                }
                            }
                            int a = 0;
                            for(Player target : Bukkit.getOnlinePlayers()) {
                                if(target.getName().equalsIgnoreCase(sender.getName())) continue;
                                if(!target.hasPermission("functionalservercontrol.ban.bypass")) {
                                    a = a + 1;
                                    TemporaryCache.setUnsafeBannedPlayers(((OfflinePlayer)target), sender);
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                            banManager.preformBan(((OfflinePlayer) target), BanType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true);
                                        });
                                    } else {
                                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                            banManager.preformBan(((OfflinePlayer) target), BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, true);
                                        });
                                    }
                                } else if(target.hasPermission("functionalservercontrol.ban.bypass")) {
                                    if(sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        a = a + 1;
                                        TemporaryCache.setUnsafeBannedPlayers(((OfflinePlayer)target), sender);
                                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                                banManager.preformBan(((OfflinePlayer) target), BanType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true);
                                            });
                                        } else {
                                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                                banManager.preformBan(((OfflinePlayer) target), BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, true);
                                            });
                                        }
                                    }
                                }
                            }
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-ban-success").replace("%1$f", String.valueOf(a))));
                            if(a != 0) sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-ban-cached-notify")));
                            a = 0;
                            unsafeConfirm = false;
                            return true;
                        } else {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("unsafe-actions.unsafe-flag-no-perms").replace("%1$f", "-a")));
                            return true;
                        }
                    }

                    if(OtherUtils.isArgumentIP(args[0])) {

                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
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
                                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                            banManager.preformBanByIp(args[0], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true, false);
                                        });
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
                        });
                        return true;
                    }

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                    if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                        if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(args[0], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true);
                            });
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(args[0], BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true);
                            });
                        }
                    } else {
                        if(player.isOnline()) {
                            if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                return true;
                            }
                        }
                        if(!player.isOnline()) {
                            if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                        }
                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true);
                            });
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(player, BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true);
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
                            });
                            return true;
                        }

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                        if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                            if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    banManager.preformBan(args[1], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false);
                                });
                            } else {
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    banManager.preformBan(args[1], BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false);
                                });
                            }
                        } else {
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                    return true;
                                }
                            }
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false);
                                });
                            } else {
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    banManager.preformBan(player, BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false);
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
                            });
                            return true;
                        }

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                        if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                            if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    banManager.preformBan(args[1], BanType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false);
                                });
                            } else {
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    banManager.preformBan(args[1], BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false);
                                });
                            }
                        } else {
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                    return true;
                                }
                            }
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), false);
                                });
                            } else {
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    banManager.preformBan(player, BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false);
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
                            });
                            return true;
                        }

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                        if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                            if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(args[0], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true);
                            });
                            return true;
                        } else {
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                    return true;
                                }
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true);
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
                            });
                            return true;
                        }

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                        if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                            if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(args[1], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false);
                            });
                            return true;
                        } else {
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                    return true;
                                }
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false);
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
                            });
                            return true;
                        }

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                        if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                            if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(args[0], BanType.TIMED_NOT_IP, getReason(args, 2), sender, time, true);
                            });
                            return true;
                        } else {
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                    return true;
                                }
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 2), sender, time, true);
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
                            });
                            return true;
                        }

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                        if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                            if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(args[1], BanType.TIMED_NOT_IP, getReason(args, 3), sender, time, false);
                            });
                            return true;
                        } else {
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                    return true;
                                }
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 3), sender, time, false);
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
                        });
                        return true;
                    }

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                    if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                        if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(args[0], BanType.TIMED_NOT_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true);
                            });
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(args[0], BanType.PERMANENT_NOT_IP, getReason(args, 1), sender, -1, true);
                            });
                        }
                        return true;
                    } else {
                        if(!player.isOnline()) {
                            if(!sender.hasPermission("functionalservercontrol.ban.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                        }
                        if(player.isOnline()) {
                            if(player.getPlayer().hasPermission("functionalservercontrol.ban.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.target-bypass")));
                                return true;
                            }
                        }
                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerBanPunishTime((Player)sender), true);
                            });
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                banManager.preformBan(player, BanType.PERMANENT_NOT_IP, getReason(args, 1), sender, -1, true);
                            });
                        }
                        return true;
                    }

                }

            }

            if (args.length == 0) {
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.description").replace("%1$f", label))); }
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.ban.example").replace("%1$f", label))); }
                return true;
            }
        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            return true;
        }

        return true;
    }
}
