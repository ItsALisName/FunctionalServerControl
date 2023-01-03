package by.alis.functionalservercontrol.spigot.Commands;

import by.alis.functionalservercontrol.API.Enums.MuteType;
import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.OtherUtils;
import by.alis.functionalservercontrol.spigot.Commands.Completers.TempMuteIpCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.Managers.Mute.MuteManager;
import by.alis.functionalservercontrol.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.getReason;
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class TempMuteIpCommand implements CommandExecutor {
    
    FunctionalServerControl plugin;
    public TempMuteIpCommand(FunctionalServerControl plugin) {
        this.plugin = plugin;
        plugin.getCommand("tempmuteip").setExecutor(this);
        plugin.getCommand("tempmuteip").setTabCompleter(new TempMuteIpCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
        MuteManager muteManager = new MuteManager();

        if(sender.hasPermission("functionalservercontrol.tempmuteip")) {

            if(args.length >= 1) {


                if(args.length == 1 && args[0].equalsIgnoreCase("-s")) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.example").replace("%1$f", label))); }
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
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.description").replace("%1$f", label)));
                    }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.usage").replace("%1$f", label)));
                    if (getConfigSettings().showExamples()) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.example").replace("%1$f", label)));
                    }
                }

                if(args.length == 2 && args[0].equalsIgnoreCase("-s")) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.example").replace("%1$f", label))); }
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

                        long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[1]);

                        if(OtherUtils.isArgumentIP(args[0])) {

                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                if(OtherUtils.isNotNullIp(args[0])) {
                                    if(OtherUtils.getPlayerByIP(args[0]) != null) {
                                        OfflinePlayer player = OtherUtils.getPlayerByIP(args[0]);
                                        if(!player.isOnline()) {
                                            if(!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                        }
                                        if(player.isOnline()) {
                                            if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.target-bypass")));
                                                return;
                                            }
                                        }
                                        muteManager.preformMute(player, MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "tempmuteip");
                                        return;
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        muteManager.preformMuteByIp(args[0], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "tempmuteip", false);
                                        return;
                                    }
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    muteManager.preformMuteByIp(args[0], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "tempmuteip", true);
                                    return;
                                }
                            });
                            return true;
                        }

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                        if(!OtherUtils.isNotNullPlayer(args[0])) {
                            if(!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                muteManager.preformMute(args[0], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "tempmuteip");
                            });
                            return true;
                        } else {
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.target-bypass")));
                                    return true;
                                }
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                muteManager.preformMute(player, MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, true, "tempmuteip");
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

                            long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[2]);

                            if(OtherUtils.isArgumentIP(args[1])) {

                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    if(OtherUtils.isNotNullIp(args[1])) {
                                        if(OtherUtils.getPlayerByIP(args[1]) != null) {
                                            OfflinePlayer player = OtherUtils.getPlayerByIP(args[1]);
                                            if(!player.isOnline()) {
                                                if(!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                    return;
                                                }
                                            }
                                            if(player.isOnline()) {
                                                if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.target-bypass")));
                                                    return;
                                                }
                                            }
                                            muteManager.preformMute(player, MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "tempmuteip");
                                            return;
                                        } else {
                                            if(!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                            muteManager.preformMuteByIp(args[1], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "tempmuteip", false);
                                            return;
                                        }
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        muteManager.preformMuteByIp(args[1], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "tempmuteip", true);
                                        return;
                                    }
                                });
                                return true;
                            }

                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                            if(!OtherUtils.isNotNullPlayer(args[1])) {
                                if(!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    muteManager.preformMute(args[1], MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "tempmuteip");
                                });
                                return true;
                            } else {
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return true;
                                    }
                                }
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.target-bypass")));
                                        return true;
                                    }
                                }
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    muteManager.preformMute(player, MuteType.TIMED_IP, getGlobalVariables().getDefaultReason(), sender, time, false, "tempmuteip");
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

                            if(OtherUtils.isArgumentIP(args[1])) {

                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    if(OtherUtils.isNotNullIp(args[1])) {
                                        if(OtherUtils.getPlayerByIP(args[1]) != null) {
                                            OfflinePlayer player = OtherUtils.getPlayerByIP(args[1]);
                                            if (!player.isOnline()) {
                                                if (!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                    return;
                                                }
                                            }
                                            if (player.isOnline()) {
                                                if (player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.target-bypass")));
                                                    return;
                                                }
                                            }
                                            muteManager.preformMute(player, MuteType.TIMED_IP, getReason(args, 3), sender, time, false, "tempmuteip");
                                            return;
                                        } else {
                                            if (!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                            muteManager.preformMuteByIp(args[1], MuteType.TIMED_IP, getReason(args, 3), sender, time, false, "tempmuteip", false);
                                            return;
                                        }
                                    } else {
                                        if (!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        muteManager.preformMuteByIp(args[1], MuteType.TIMED_IP, getReason(args, 3), sender, time, false, "tempmuteip", true);
                                        return;
                                    }
                                });
                                return true;
                            }

                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                            if (!OtherUtils.isNotNullPlayer(args[1])) {
                                if (!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    muteManager.preformMute(args[1], MuteType.TIMED_IP, getReason(args, 3), sender, time, false, "tempmuteip");
                                });
                                return true;
                            } else {
                                if (!player.isOnline()) {
                                    if (!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return true;
                                    }
                                }
                                if (player.isOnline()) {
                                    if (player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.target-bypass")));
                                        return true;
                                    }
                                }
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    muteManager.preformMute(player, MuteType.TIMED_IP, getReason(args, 3), sender, time, false, "tempmuteip");
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

                        if(OtherUtils.isArgumentIP(args[0])) {

                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                if(OtherUtils.isNotNullIp(args[0])) {
                                    if(OtherUtils.getPlayerByIP(args[0]) != null) {
                                        OfflinePlayer player = OtherUtils.getPlayerByIP(args[0]);
                                        if (!player.isOnline()) {
                                            if (!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                        }
                                        if (player.isOnline()) {
                                            if (player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.target-bypass")));
                                                return;
                                            }
                                        }
                                        muteManager.preformMute(player, MuteType.TIMED_IP, getReason(args, 2), sender, time, true, "tempmuteip");
                                        return;
                                    } else {
                                        if (!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        muteManager.preformMuteByIp(args[0], MuteType.TIMED_IP, getReason(args, 2), sender, time, true, "tempmuteip", false);
                                        return;
                                    }
                                } else {
                                    if (!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    muteManager.preformMuteByIp(args[0], MuteType.TIMED_IP, getReason(args, 2), sender, time, true, "tempmuteip", true);
                                    return;
                                }
                            });
                            return true;
                        }

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                        if (!OtherUtils.isNotNullPlayer(args[0])) {
                            if (!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                muteManager.preformMute(args[0], MuteType.TIMED_IP, getReason(args, 2), sender, time, true, "tempmuteip");
                            });
                            return true;
                        } else {
                            if (!player.isOnline()) {
                                if (!sender.hasPermission("functionalservercontrol.tempmuteip.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if (player.isOnline()) {
                                if (player.getPlayer().hasPermission("functionalservercontrol.muteip.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.target-bypass")));
                                    return true;
                                }
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                muteManager.preformMute(player, MuteType.TIMED_IP, getReason(args, 2), sender, time, true, "tempmuteip");
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
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.description").replace("%1$f", label))); }
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.tempmuteip.example").replace("%1$f", label))); }
                return true;
            }
        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            return true;
        }

        return true;
    }
    
}