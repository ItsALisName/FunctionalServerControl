package by.alis.functionalbans.spigot.Commands;

import by.alis.functionalbans.spigot.Additional.Enums.BanType;
import by.alis.functionalbans.spigot.Additional.Other.TemporaryCache;
import by.alis.functionalbans.spigot.Commands.Completers.BanCompleter;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.BansManagers.BanManager;
import by.alis.functionalbans.spigot.Managers.FilesManagers.FileAccessor;
import by.alis.functionalbans.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.getReason;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class BanCommand implements CommandExecutor {

    FunctionalBansSpigot plugin;
    public BanCommand(FunctionalBansSpigot plugin) {
        this.plugin = plugin;
        plugin.getCommand("ban").setExecutor(this);
        plugin.getCommand("ban").setTabCompleter(new BanCompleter());
    }

    private final FileAccessor fileAccessor = new FileAccessor();
    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
    private final BanManager banManager = new BanManager();
    private boolean unsafeConfirm;
    private final TemporaryCache cache = new TemporaryCache();
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender.hasPermission("functionalbans.ban")) {
            if(args.length >= 1) {

                if(args[0].equalsIgnoreCase("-a") && args.length >= 2) {

                    if(sender.hasPermission("functionalbans.use.unsafe-flags")) {

                        if(args[1].equalsIgnoreCase("-s")) {
                            if(!sender.hasPermission("functionalbans.use.silently")) {
                                sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                                return true;
                            }
                            if (getConfigSettings().isUnsafeActionsConfirmation()) {
                                if (!unsafeConfirm) {
                                    sender.sendMessage(setColors(this.fileAccessor.getLang().getString("unsafe-actions.unsafe-action-confirm")));
                                    unsafeConfirm = true;
                                    return true;
                                }
                            }
                            int a = 0;
                            for (Player target : Bukkit.getOnlinePlayers()) {
                                if (!target.hasPermission("functionalbans.ban.bypass")) {
                                    a = a + 1;
                                    this.cache.setUnsafeBannedPlayers(((OfflinePlayer) target), sender);
                                    if(!sender.hasPermission("functionalbans.time-bypass")) {
                                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                            this.banManager.preformBan(((OfflinePlayer) target), BanType.TIMED_NOT_IP, getReason(args, 2), sender, this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player)sender), false);
                                        });
                                    } else {
                                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                            this.banManager.preformBan(((OfflinePlayer) target), BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false);
                                        });
                                    }
                                } else {
                                    if(sender.hasPermission("functionalbans.bypass-break")) {
                                        a = a + 1;
                                        this.cache.setUnsafeBannedPlayers(((OfflinePlayer) target), sender);
                                        if(!sender.hasPermission("functionalbans.time-bypass")) {
                                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                                this.banManager.preformBan(((OfflinePlayer) target), BanType.TIMED_NOT_IP, getReason(args, 2), sender, this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player)sender), false);
                                            });
                                        } else {
                                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                                this.banManager.preformBan(((OfflinePlayer) target), BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false);
                                            });
                                        }
                                    }
                                }
                            }
                            sender.sendMessage(setColors(this.fileAccessor.getLang().getString("unsafe-actions.unsafe-ban-success").replace("%1$f", String.valueOf(a))));
                            if(a != 0) sender.sendMessage(setColors(this.fileAccessor.getLang().getString("unsafe-actions.unsafe-ban-cached-notify")));
                            a = 0;
                            unsafeConfirm = false;
                            return true;
                        }

                        if (getConfigSettings().isUnsafeActionsConfirmation()) {
                            if (!unsafeConfirm) {
                                sender.sendMessage(setColors(this.fileAccessor.getLang().getString("unsafe-actions.unsafe-action-confirm")));
                                unsafeConfirm = true;
                                return true;
                            }
                            int a = 0;
                            for (Player target : Bukkit.getOnlinePlayers()) {
                                if (!target.hasPermission("functionalbans.ban.bypass")) {
                                    a = a + 1;
                                    this.cache.setUnsafeBannedPlayers(((OfflinePlayer) target), sender);
                                    if(!sender.hasPermission("functionalbans.time-bypass")) {
                                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                            this.banManager.preformBan(((OfflinePlayer) target), BanType.TIMED_NOT_IP, getReason(args, 2), sender, this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player)sender), true);
                                        });
                                    } else {
                                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                            this.banManager.preformBan(((OfflinePlayer) target), BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, true);
                                        });
                                    }
                                }
                            }
                            sender.sendMessage(setColors(this.fileAccessor.getLang().getString("unsafe-actions.unsafe-ban-success").replace("%1$f", String.valueOf(a))));
                            if(a != 0) sender.sendMessage(setColors(this.fileAccessor.getLang().getString("unsafe-actions.unsafe-ban-cached-notify")));
                            a = 0;
                            unsafeConfirm = false;
                            return true;
                        }
                    } else {
                        sender.sendMessage(setColors(this.fileAccessor.getLang().getString("unsafe-actions.unsafe-flag-no-perms").replace("%1$f", "-a")));
                        return true;
                    }
                }

                if (args.length == 1) {

                    if(args[0].equalsIgnoreCase("-s")) {
                        if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(this.fileAccessor.getLang().getString("commands.ban.description").replace("%1$f", command.getName()))); }
                        sender.sendMessage(setColors(this.fileAccessor.getLang().getString("commands.ban.usage").replace("%1$f", command.getName())));
                        if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(this.fileAccessor.getLang().getString("commands.ban.example").replace("%1$f", command.getName()))); }
                        return true;
                    }

                    if(args[0].equalsIgnoreCase("-a")) {
                        if(sender.hasPermission("functionalbans.use.unsafe-flags")) {

                            if(!sender.hasPermission("functionalbans.use.no-reason")) {
                                sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.no-reason")));
                                return true;
                            }

                            if(getConfigSettings().isUnsafeActionsConfirmation()) {
                                if (!unsafeConfirm) {
                                    sender.sendMessage(setColors(this.fileAccessor.getLang().getString("unsafe-actions.unsafe-action-confirm")));
                                    unsafeConfirm = true;
                                    return true;
                                }
                            }
                            int a = 0;
                            for(Player target : Bukkit.getOnlinePlayers()) {
                                if(!target.hasPermission("functionalbans.ban.bypass")) {
                                    a = a + 1;
                                    this.cache.setUnsafeBannedPlayers(((OfflinePlayer)target), sender);
                                    if(!sender.hasPermission("functionalbans.time-bypass")) {
                                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                            this.banManager.preformBan(((OfflinePlayer) target), BanType.TIMED_NOT_IP, getReason(args, 2), sender, this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player)sender), true);
                                        });
                                    } else {
                                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                            this.banManager.preformBan(((OfflinePlayer) target), BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, true);
                                        });
                                    }
                                }
                            }
                            sender.sendMessage(setColors(this.fileAccessor.getLang().getString("unsafe-actions.unsafe-ban-success").replace("%1$f", String.valueOf(a))));
                            if(a != 0) sender.sendMessage(setColors(this.fileAccessor.getLang().getString("unsafe-actions.unsafe-ban-cached-notify")));
                            a = 0;
                            unsafeConfirm = false;
                            return true;
                        } else {
                            sender.sendMessage(setColors(this.fileAccessor.getLang().getString("unsafe-actions.unsafe-flag-no-perms").replace("%1$f", "-a")));
                            return true;
                        }
                    }

                    if(!sender.hasPermission("functionalbans.use.no-reason")) {
                        sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.no-reason")));
                        return true;
                    }

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                    if(player == null) {
                        if(!sender.hasPermission("functionalbans.ban.offline")) {
                            sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        if(!sender.hasPermission("functionalbans.time-bypass")) {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                this.banManager.preformBan(args[0], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player)sender), true);
                            });
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                this.banManager.preformBan(args[0], BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true);
                            });
                        }
                    } else {
                        if(player.isOnline()) {
                            if(player.getPlayer().hasPermission("functionalbans.ban.bypass") && !sender.hasPermission("functionalbans.bypass-break")) {
                                sender.sendMessage(setColors(this.fileAccessor.getLang().getString("commands.ban.target-bypass")));
                                return true;
                            }
                        }
                        if(!player.isOnline()) {
                            if(!sender.hasPermission("functionalbans.ban.offline")) {
                                sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                        }
                        if(!sender.hasPermission("functionalbans.time-bypass")) {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                this.banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player)sender), true);
                            });
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                this.banManager.preformBan(player, BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true);
                            });
                        }
                    }
                    return true;
                }




                if(args.length >= 2) {
                    if(args[0].equalsIgnoreCase("-s") && args.length == 2) {
                        if(!sender.hasPermission("functionalbans.use.silently")) {
                            sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                            return true;
                        }
                        if(!sender.hasPermission("functionalbans.use.no-reason")) {
                            sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.no-reason")));
                            return true;
                        }
                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                        if(player == null) {
                            if(!sender.hasPermission("functionalbans.ban.offline")) {
                                sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            if(!sender.hasPermission("functionalbans.time-bypass")) {
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    this.banManager.preformBan(args[1], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player)sender), false);
                                });
                            } else {
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    this.banManager.preformBan(args[1], BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false);
                                });
                            }
                        } else {
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalbans.ban.bypass") && !sender.hasPermission("functionalbans.bypass-break")) {
                                    sender.sendMessage(setColors(this.fileAccessor.getLang().getString("commands.ban.target-bypass")));
                                    return true;
                                }
                            }
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalbans.ban.offline")) {
                                    sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if(!sender.hasPermission("functionalbans.ban.offline")) {
                                sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            if(!sender.hasPermission("functionalbans.time-bypass")) {
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    this.banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player)sender), false);
                                });
                            } else {
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    this.banManager.preformBan(player, BanType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false);
                                });
                            }
                        }
                        return true;
                    }

                    if(args[0].equalsIgnoreCase("-s") && args.length > 2 && !this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
                        if(!sender.hasPermission("functionalbans.use.silently")) {
                            sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                            return true;
                        }
                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                        if(player == null) {
                            if(!sender.hasPermission("functionalbans.ban.offline")) {
                                sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            if(!sender.hasPermission("functionalbans.time-bypass")) {
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    this.banManager.preformBan(args[1], BanType.TIMED_NOT_IP, getReason(args, 2), sender, this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player)sender), false);
                                });
                            } else {
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    this.banManager.preformBan(args[1], BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false);
                                });
                            }
                        } else {
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalbans.ban.bypass") && !sender.hasPermission("functionalbans.bypass-break")) {
                                    sender.sendMessage(setColors(this.fileAccessor.getLang().getString("commands.ban.target-bypass")));
                                    return true;
                                }
                            }
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalbans.ban.offline")) {
                                    sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if(!sender.hasPermission("functionalbans.time-bypass")) {
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    this.banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 2), sender, this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player)sender), false);
                                });
                            } else {
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    this.banManager.preformBan(player, BanType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false);
                                });
                            }
                        }
                        return true;
                    }


                    // A Bans with time
                    if(this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[1]) && args.length == 2) {
                        if(!sender.hasPermission("functionalbans.use.no-reason")) {
                            sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.no-reason")));
                            return true;
                        }
                        long time = this.timeSettingsAccessor.getTimeManager().convertToMillis(args[1]);
                        if(!sender.hasPermission("functionalbans.time-bypass")) {
                            if(time > this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player) sender)) {
                                sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.ban-over-time").replace("%1$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player) sender)))));
                                return true;
                            }
                        }
                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                        if(player == null) {
                            if(!sender.hasPermission("functionalbans.ban.offline")) {
                                sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                this.banManager.preformBan(args[0], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true);
                            });
                            return true;
                        } else {
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalbans.ban.offline")) {
                                    sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalbans.ban.bypass") && !sender.hasPermission("functionalbans.bypass-break")) {
                                    sender.sendMessage(setColors(this.fileAccessor.getLang().getString("commands.ban.target-bypass")));
                                    return true;
                                }
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                this.banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true);
                            });
                            return true;
                        }
                    }

                    if(this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2]) && args[0].equalsIgnoreCase("-s") && args.length == 3) {
                        if(!sender.hasPermission("functionalbans.use.no-reason")) {
                            sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.no-reason")));
                            return true;
                        }
                        long time = this.timeSettingsAccessor.getTimeManager().convertToMillis(args[2]);
                        if(!sender.hasPermission("functionalbans.time-bypass")) {
                            if(time > this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player) sender)) {
                                sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.ban-over-time").replace("%1$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player) sender)))));
                                return true;
                            }
                        }

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                        if(player == null) {
                            if(!sender.hasPermission("functionalbans.ban.offline")) {
                                sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                this.banManager.preformBan(args[1], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false);
                            });
                            return true;
                        } else {
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalbans.ban.offline")) {
                                    sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalbans.ban.bypass") && !sender.hasPermission("functionalbans.bypass-break")) {
                                    sender.sendMessage(setColors(this.fileAccessor.getLang().getString("commands.ban.target-bypass")));
                                    return true;
                                }
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                this.banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false);
                            });
                            return true;
                        }

                    }

                    // A Bans with time

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                    if(player == null) {
                        if(!sender.hasPermission("functionalbans.ban.offline")) {
                            sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        if(!sender.hasPermission("functionalbans.time-bypass")) {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                this.banManager.preformBan(args[1], BanType.TIMED_NOT_IP, getReason(args, 1), sender, this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player)sender), true);
                            });
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                this.banManager.preformBan(args[1], BanType.PERMANENT_NOT_IP, getReason(args, 1), sender, -1, true);
                            });
                        }
                        return true;
                    } else {
                        if(!player.isOnline()) {
                            if(!sender.hasPermission("functionalbans.ban.offline")) {
                                sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                        }
                        if(player.isOnline()) {
                            if(player.getPlayer().hasPermission("functionalbans.ban.bypass") && !sender.hasPermission("functionalbans.bypass-break")) {
                                sender.sendMessage(setColors(this.fileAccessor.getLang().getString("commands.ban.target-bypass")));
                                return true;
                            }
                        }
                        if(!sender.hasPermission("functionalbans.time-bypass")) {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                this.banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 1), sender, this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player)sender), true);
                            });
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                this.banManager.preformBan(player, BanType.PERMANENT_NOT_IP, getReason(args, 1), sender, -1, true);
                            });
                        }
                        return true;
                    }

                }

            }

            if (args.length == 0) {
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(this.fileAccessor.getLang().getString("commands.ban.description").replace("%1$f", command.getName()))); }
                sender.sendMessage(setColors(this.fileAccessor.getLang().getString("commands.ban.usage").replace("%1$f", command.getName())));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(this.fileAccessor.getLang().getString("commands.ban.example").replace("%1$f", command.getName()))); }
                return true;
            }
        } else {
            sender.sendMessage(setColors(this.fileAccessor.getLang().getString("other.no-permissions")));
            return true;
        }

        return true;
    }
}
