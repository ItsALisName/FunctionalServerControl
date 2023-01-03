package by.alis.functionalservercontrol.spigot.Commands;

import by.alis.functionalservercontrol.API.Enums.MuteType;
import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.OtherUtils;
import by.alis.functionalservercontrol.spigot.Commands.Completers.MuteIpCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.Managers.Mute.MuteManager;
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
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.getReason;
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class MuteIpCommand implements CommandExecutor {
    
    FunctionalServerControl plugin;
    public MuteIpCommand(FunctionalServerControl plugin) {
        this.plugin = plugin;
        plugin.getCommand("muteip").setExecutor(this);
        plugin.getCommand("muteip").setTabCompleter(new MuteIpCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender.hasPermission("functionalservercontrol.muteip")) {

            MuteManager muteManager = new MuteManager();
            TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();

            if(args.length == 1) {
                if(args[0].equalsIgnoreCase("-s")) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.example").replace("%1$f", label))); }
                    return true;
                }

                if(OtherUtils.isArgumentIP(args[0])) {
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        if(OtherUtils.isNotNullIp(args[0])) {
                            if(OtherUtils.getPlayerByIP(args[0]) != null) {

                                OfflinePlayer player = OtherUtils.getPlayerByIP(args[0]);
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.target-bypass")));
                                        return;
                                    }
                                }
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                }

                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                    muteManager.preformMute(player, MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), true, "muteip");
                                    return;
                                } else {
                                    muteManager.preformMute(player, MuteType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true, "muteip");
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                    muteManager.preformMuteByIp(args[0], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), true, "muteip", false);
                                } else {
                                    muteManager.preformMuteByIp(args[0], MuteType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true, "muteip", false);
                                }
                                return;
                            }
                        } else {
                            if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return;
                            }
                            if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                muteManager.preformMuteByIp(args[0], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), true, "muteip", true);
                            } else {
                                muteManager.preformMuteByIp(args[0], MuteType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true, "muteip", true);
                            }
                            return;
                        }
                    });
                    return true;
                }

                OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                    if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                        return true;
                    }
                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            muteManager.preformMute(args[0], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), true, "muteip");
                        });
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            muteManager.preformMute(args[0], MuteType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true, "muteip");
                        });
                    }
                } else {
                    if(player.isOnline()) {
                        if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.target-bypass")));
                            return true;
                        }
                    }
                    if(!player.isOnline()) {
                        if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                    }
                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            muteManager.preformMute(player, MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), true, "muteip");
                        });
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            muteManager.preformMute(player, MuteType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true, "muteip");
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
                                        if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.target-bypass")));
                                            return;
                                        }
                                    }
                                    if(!player.isOnline()) {
                                        if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        muteManager.preformMute(player, MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), false, "muteip");
                                    } else {
                                        muteManager.preformMute(player, MuteType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false, "muteip");
                                    }
                                    return;
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        muteManager.preformMuteByIp(args[1], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), false, "muteip", false);
                                    } else {
                                        muteManager.preformMuteByIp(args[1], MuteType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false, "muteip", false);
                                    }
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                    muteManager.preformMuteByIp(args[1], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), false, "muteip", true);
                                } else {
                                    muteManager.preformMuteByIp(args[1], MuteType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false, "muteip", true);
                                }
                                return;
                            }
                        });
                        return true;
                    }

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                        if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                muteManager.preformMute(args[1], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), false, "muteip");
                            });
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                muteManager.preformMute(args[1], MuteType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false, "muteip");
                            });
                        }
                    } else {
                        if(player.isOnline()) {
                            if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.target-bypass")));
                                return true;
                            }
                        }
                        if(!player.isOnline()) {
                            if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                        }
                        if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                muteManager.preformMute(player, MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), false, "muteip");
                            });
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                muteManager.preformMute(player, MuteType.PERMANENT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false, "muteip");
                            });
                        }
                    }
                    return true;
                }

                if(args.length > 2 && args[0].equalsIgnoreCase("-s") && !timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {

                    if(OtherUtils.isArgumentIP(args[1])) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            if(OtherUtils.isNotNullIp(args[1])) {
                                if(OtherUtils.getPlayerByIP(args[1]) != null) {
                                    OfflinePlayer player = OtherUtils.getPlayerByIP(args[1]);
                                    if(player.isOnline()) {
                                        if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.target-bypass")));
                                            return;
                                        }
                                    }
                                    if(!player.isOnline()) {
                                        if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        muteManager.preformMute(player, MuteType.TIMED_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), false, "muteip");
                                    } else {
                                        muteManager.preformMute(player, MuteType.PERMANENT_IP, getReason(args, 2), sender, -1, false, "muteip");
                                    }
                                    return;
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        muteManager.preformMuteByIp(args[1], MuteType.TIMED_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), false, "muteip", false);
                                    } else {
                                        muteManager.preformMuteByIp(args[1], MuteType.PERMANENT_IP, getReason(args, 2), sender, -1, false, "muteip", false);
                                    }
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                    muteManager.preformMuteByIp(args[1], MuteType.TIMED_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), false, "muteip", true);
                                } else {
                                    muteManager.preformMuteByIp(args[1], MuteType.PERMANENT_IP, getReason(args, 2), sender, -1, false, "muteip", true);
                                }
                                return;
                            }
                        });
                        return true;
                    }

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                        if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                muteManager.preformMute(args[1], MuteType.TIMED_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), false, "muteip");
                            });
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                muteManager.preformMute(args[1], MuteType.PERMANENT_IP, getReason(args, 2), sender, -1, false, "muteip");
                            });
                        }
                    } else {
                        if(player.isOnline()) {
                            if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.target-bypass")));
                                return true;
                            }
                        }
                        if(!player.isOnline()) {
                            if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                        }
                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                muteManager.preformMute(player, MuteType.TIMED_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), false, "muteip");
                            });
                        } else {
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                muteManager.preformMute(player, MuteType.PERMANENT_IP, getReason(args, 2), sender, -1, false, "muteip");
                            });
                        }
                    }
                    return true;
                }

                if(timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[1]) && args.length == 2) {
                    long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[1]);

                    if(OtherUtils.isArgumentIP(args[0])) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            if(OtherUtils.isNotNullIp(args[0])) {
                                if(OtherUtils.getPlayerByIP(args[0]) != null) {
                                    OfflinePlayer player = OtherUtils.getPlayerByIP(args[0]);
                                    if(!player.isOnline()) {
                                        if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                    }
                                    if(player.isOnline()) {
                                        if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.target-bypass")));
                                            return;
                                        }
                                    }
                                    muteManager.preformMute(player, MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "muteip");
                                    return;
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    muteManager.preformMuteByIp(args[0], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "muteip", false);
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                muteManager.preformMuteByIp(args[0], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "muteip", true);
                                return;
                            }
                        });
                        return true;
                    }

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                    if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                        if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            muteManager.preformMute(args[0], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "muteip");
                        });
                        return true;
                    } else {
                        if(!player.isOnline()) {
                            if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                        }
                        if(player.isOnline()) {
                            if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.target-bypass")));
                                return true;
                            }
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            muteManager.preformMute(player, MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "muteip");
                        });
                    }
                    return true;
                }

                if(args.length == 3 && args[0].equalsIgnoreCase("-s") && timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
                    long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[2]);

                    if(OtherUtils.isArgumentIP(args[1])) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            if(OtherUtils.isNotNullIp(args[1])) {
                                if(OtherUtils.getPlayerByIP(args[1]) != null) {
                                    OfflinePlayer player = OtherUtils.getPlayerByIP(args[1]);
                                    if(!player.isOnline()) {
                                        if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                    }
                                    if(player.isOnline()) {
                                        if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.target-bypass")));
                                            return;
                                        }
                                    }
                                    muteManager.preformMute(player, MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "muteip");
                                    return;
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    muteManager.preformMuteByIp(args[1], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "muteip", false);
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                muteManager.preformMuteByIp(args[1], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "muteip", true);
                                return;
                            }
                        });
                        return true;
                    }

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                        if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            muteManager.preformMute(args[1], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "muteip");
                        });
                        return true;
                    } else {
                        if(!player.isOnline()) {
                            if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                        }
                        if(player.isOnline()) {
                            if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.target-bypass")));
                                return true;
                            }
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            muteManager.preformMute(player, MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "muteip");
                        });
                        return true;
                    }

                }

                if(args.length > 2 && timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[1])) {
                    long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[1]);

                    if(OtherUtils.isArgumentIP(args[0])) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            if(OtherUtils.isNotNullIp(args[0])) {
                                if(OtherUtils.getPlayerByIP(args[0]) != null) {
                                    OfflinePlayer player = OtherUtils.getPlayerByIP(args[0]);
                                    if(!player.isOnline()) {
                                        if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                    }
                                    if(player.isOnline()) {
                                        if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.target-bypass")));
                                            return;
                                        }
                                    }
                                    muteManager.preformMute(player, MuteType.TIMED_IP, getReason(args, 2), sender, time, true, "muteip");
                                    return;
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    muteManager.preformMuteByIp(args[0], MuteType.TIMED_IP, getReason(args, 2), sender, time, true, "muteip", false);
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                muteManager.preformMuteByIp(args[0], MuteType.TIMED_IP, getReason(args, 2), sender, time, true, "muteip", true);
                                return;
                            }
                        });
                        return true;
                    }

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                    if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                        if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            muteManager.preformMute(args[0], MuteType.TIMED_IP, getReason(args, 2), sender, time, true, "muteip");
                        });
                        return true;
                    } else {
                        if(!player.isOnline()) {
                            if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                        }
                        if(player.isOnline()) {
                            if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.target-bypass")));
                                return true;
                            }
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            muteManager.preformMute(player, MuteType.TIMED_IP, getReason(args, 2), sender, time, true, "muteip");
                        });
                        return true;
                    }
                }

                if(args.length > 3 && args[0].equalsIgnoreCase("-s") && timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
                    long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[2]);

                    if(OtherUtils.isArgumentIP(args[1])) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            if(OtherUtils.isNotNullIp(args[1])) {
                                if(OtherUtils.getPlayerByIP(args[1]) != null) {
                                    OfflinePlayer player = OtherUtils.getPlayerByIP(args[1]);
                                    if(!player.isOnline()) {
                                        if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                    }
                                    if(player.isOnline()) {
                                        if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.target-bypass")));
                                            return;
                                        }
                                    }
                                    muteManager.preformMute(player, MuteType.TIMED_IP, getReason(args, 3), sender, time, false, "muteip");
                                    return;
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    muteManager.preformMuteByIp(args[1], MuteType.TIMED_IP, getReason(args, 3), sender, time, false, "muteip", false);
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                muteManager.preformMuteByIp(args[1], MuteType.TIMED_IP, getReason(args, 3), sender, time, false, "muteip", true);
                                return;
                            }
                        });
                        return true;
                    }

                    OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                    if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                        if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            muteManager.preformMute(args[1], MuteType.TIMED_IP, getReason(args, 3), sender, time, false, "muteip");
                        });
                        return true;
                    } else {
                        if(!player.isOnline()) {
                            if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                        }
                        if(player.isOnline()) {
                            if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.target-bypass")));
                                return true;
                            }
                        }
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            muteManager.preformMute(player, MuteType.TIMED_IP, getReason(args, 3), sender, time, false, "muteip");
                        });
                        return true;
                    }
                }

                if(OtherUtils.isArgumentIP(args[0])) {
                    Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                        if(OtherUtils.isNotNullIp(args[0])) {
                            if(OtherUtils.getPlayerByIP(args[0]) != null) {
                                OfflinePlayer player = OtherUtils.getPlayerByIP(args[0]);
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                }
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.target-bypass")));
                                        return;
                                    }
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                    muteManager.preformMute(player, MuteType.TIMED_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), true, "muteip");
                                } else {
                                    muteManager.preformMute(player, MuteType.PERMANENT_IP, getReason(args, 1), sender, -1, true, "muteip");
                                }
                                return;
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                    muteManager.preformMuteByIp(args[0], MuteType.TIMED_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), true, "muteip", false);
                                } else {
                                    muteManager.preformMuteByIp(args[0], MuteType.PERMANENT_IP, getReason(args, 1), sender, -1, true, "muteip", false);
                                }
                                return;
                            }
                        } else {
                            if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return;
                            }
                            if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                muteManager.preformMuteByIp(args[0], MuteType.TIMED_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), true, "muteip", true);
                            } else {
                                muteManager.preformMuteByIp(args[0], MuteType.PERMANENT_IP, getReason(args, 1), sender, -1, true, "muteip", true);
                            }
                        }
                    });
                    return true;
                }

                OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                if(!OtherUtils.isNotNullPlayer(player.getUniqueId())) {
                    if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                        return true;
                    }
                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            muteManager.preformMute(args[0], MuteType.TIMED_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), true, "muteip");
                        });
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            muteManager.preformMute(args[0], MuteType.PERMANENT_IP, getReason(args, 1), sender, -1, true, "muteip");
                        });
                    }
                    return true;
                } else {
                    if(!player.isOnline()) {
                        if(!sender.hasPermission("functionalservercontrol.muteip.offline")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                            return true;
                        }
                    }
                    if(player.isOnline()) {
                        if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.target-bypass")));
                            return true;
                        }
                    }
                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            muteManager.preformMute(player, MuteType.TIMED_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((Player)sender), true, "muteip");
                        });
                    } else {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                            muteManager.preformMute(player, MuteType.PERMANENT_IP, getReason(args, 1), sender, -1, true, "muteip");
                        });
                    }
                    return true;
                }

            }

            if (args.length == 0) {
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.description").replace("%1$f", label))); }
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.muteip.example").replace("%1$f", label))); }
                return true;
            }

        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            return true;
        }

        return true;
    }
    
}
