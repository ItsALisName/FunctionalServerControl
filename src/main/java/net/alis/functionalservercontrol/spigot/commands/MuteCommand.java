package net.alis.functionalservercontrol.spigot.commands;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;
import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.api.enums.MuteType;
import net.alis.functionalservercontrol.spigot.commands.completers.MuteCompleter;
import net.alis.functionalservercontrol.spigot.managers.mute.MuteManager;
import net.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.getReason;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class MuteCommand implements CommandExecutor {

    public MuteCommand(FunctionalServerControlSpigot plugin) {
        plugin.getCommand("mute").setExecutor(this);
        plugin.getCommand("mute").setTabCompleter(new MuteCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        TaskManager.preformAsync(() -> {
            TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
            MuteManager muteManager = new MuteManager();
            if (sender.hasPermission("functionalservercontrol.mute")) {
                if(args.length >= 1) {

                    if (args.length == 1) {
                        if(args[0].equalsIgnoreCase("-s")) {
                            if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.description").replace("%1$f", label))); }
                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.usage").replace("%1$f", label)));
                            if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.example").replace("%1$f", label))); }
                            return;
                        }

                        if(OtherUtils.isArgumentIP(args[0])) {


                            if(OtherUtils.isNotNullIp(args[0])) {
                                if(OtherUtils.getPlayerByIP(args[0]) != null) {
                                    OfflineFunctionalPlayer player = OtherUtils.getPlayerByIP(args[0]);
                                    if(player.isOnline()) {
                                        if(player.getPlayer().hasPermission("functionalservercontrol.mute.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.target-bypass")));
                                            return;
                                        }
                                    }
                                    if(!player.isOnline()) {
                                        if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        muteManager.preformMute(player, MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), true);
                                    } else {
                                        muteManager.preformMute(player, MuteType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true);
                                    }
                                    return;
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {

                                        muteManager.preformMuteByIp(args[0], MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), true, false);

                                    } else {
                                        muteManager.preformMuteByIp(args[0], MuteType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true, false);
                                    }
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                    muteManager.preformMuteByIp(args[0], MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), true, true);
                                } else {
                                    muteManager.preformMuteByIp(args[0], MuteType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true, true);
                                }
                            }

                            return;
                        }

                        OfflineFunctionalPlayer player = OfflineFunctionalPlayer.get(args[0]);
                        if(player == null) {
                            if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return;
                            }
                            if(!sender.hasPermission("functionalservercontrol.time-bypass")) {

                                muteManager.preformMute(args[0], MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), true);

                            } else {

                                muteManager.preformMute(args[0], MuteType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true);

                            }
                        } else {
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalservercontrol.mute.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.target-bypass")));
                                    return;
                                }
                            }
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                            }
                            if(!sender.hasPermission("functionalservercontrol.time-bypass")) {

                                muteManager.preformMute(player, MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), true);

                            } else {

                                muteManager.preformMute(player, MuteType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, true);

                            }
                        }
                        return;
                    }




                    if(args.length >= 2) {
                        if(args[0].equalsIgnoreCase("-s") && args.length == 2) {

                            if(OtherUtils.isArgumentIP(args[1])) {


                                if(OtherUtils.isNotNullIp(args[1])) {
                                    if(OtherUtils.getPlayerByIP(args[0]) != null) {
                                        OfflineFunctionalPlayer player = OtherUtils.getPlayerByIP(args[0]);
                                        if(player.isOnline()) {
                                            if(player.getPlayer().hasPermission("functionalservercontrol.mute.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.target-bypass")));
                                                return;
                                            }
                                        }
                                        if(!player.isOnline()) {
                                            if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                        }
                                        if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                            muteManager.preformMute(player, MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), false);
                                        } else {
                                            muteManager.preformMute(player, MuteType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false);
                                        }
                                        return;
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                            muteManager.preformMuteByIp(args[1], MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), false, false);
                                        } else {
                                            muteManager.preformMuteByIp(args[1], MuteType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false, false);
                                        }
                                        return;
                                    }
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        muteManager.preformMuteByIp(args[1], MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), false, true);
                                    } else {
                                        muteManager.preformMuteByIp(args[1], MuteType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false, true);
                                    }
                                    return;
                                }
                            }

                            OfflineFunctionalPlayer player = OfflineFunctionalPlayer.get(args[1]);
                            if(player == null) {
                                if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {

                                    muteManager.preformMute(args[1], MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), false);

                                } else {

                                    muteManager.preformMute(args[1], MuteType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false);

                                }
                            } else {
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalservercontrol.mute.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.target-bypass")));
                                        return;
                                    }
                                }
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                }
                                if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {

                                    muteManager.preformMute(player, MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), false);

                                } else {

                                    muteManager.preformMute(player, MuteType.PERMANENT_NOT_IP, getGlobalVariables().getDefaultReason(), sender, -1, false);

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
                                    if(OtherUtils.getPlayerByIP(args[0]) != null) {
                                        OfflineFunctionalPlayer player = OtherUtils.getPlayerByIP(args[0]);
                                        if(player.isOnline()) {
                                            if(player.getPlayer().hasPermission("functionalservercontrol.mute.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.target-bypass")));
                                                return;
                                            }
                                        }
                                        if(!player.isOnline()) {
                                            if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                        }
                                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                            muteManager.preformMute(player, MuteType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), false);
                                        } else {
                                            muteManager.preformMute(player, MuteType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false);
                                        }
                                        return;
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                            muteManager.preformMuteByIp(args[1], MuteType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), false, false);
                                        } else {
                                            muteManager.preformMuteByIp(args[1], MuteType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false, false);
                                        }
                                        return;
                                    }
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        muteManager.preformMuteByIp(args[1], MuteType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), false, true);
                                    } else {
                                        muteManager.preformMuteByIp(args[1], MuteType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false, true);
                                    }
                                    return;
                                }
                            }

                            OfflineFunctionalPlayer player = OfflineFunctionalPlayer.get(args[1]);
                            if(player == null) {
                                if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {

                                    muteManager.preformMute(args[1], MuteType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), false);

                                } else {

                                    muteManager.preformMute(args[1], MuteType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false);

                                }
                            } else {
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalservercontrol.mute.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.target-bypass")));
                                        return;
                                    }
                                }
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {

                                    muteManager.preformMute(player, MuteType.TIMED_NOT_IP, getReason(args, 2), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), false);

                                } else {

                                    muteManager.preformMute(player, MuteType.PERMANENT_NOT_IP, getReason(args, 2), sender, -1, false);

                                }
                            }
                            return;
                        }

                        if(args.length == 2 && timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[1])) {
                            if(args[1].startsWith("0")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.zero-time")));
                                return;
                            }
                            long time = timeSettingsAccessor.getTimeManager().convertToMillis(args[1]);

                            if(OtherUtils.isArgumentIP(args[0])) {


                                if(OtherUtils.isNotNullIp(args[0])) {
                                    if(OtherUtils.getPlayerByIP(args[0]) != null) {
                                        OfflineFunctionalPlayer player = OtherUtils.getPlayerByIP(args[0]);
                                        if(!player.isOnline()) {
                                            if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                        }
                                        if(player.isOnline()) {
                                            if(player.getPlayer().hasPermission("functionalservercontrol.mute.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.target-bypass")));
                                                return;
                                            }
                                        }
                                        muteManager.preformMute(player, MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true);
                                        return;
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        muteManager.preformMuteByIp(args[0], MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true, false);
                                        return;
                                    }
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    muteManager.preformMuteByIp(args[0], MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true, true);
                                    return;
                                }
                            }

                            OfflineFunctionalPlayer player = OfflineFunctionalPlayer.get(args[0]);
                            if(player == null) {
                                if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }

                                muteManager.preformMute(args[0], MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true);

                                return;
                            } else {
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                }
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalservercontrol.mute.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.target-bypass")));
                                        return;
                                    }
                                }

                                muteManager.preformMute(player, MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true);

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
                                    if(OtherUtils.getPlayerByIP(args[0]) != null) {
                                        OfflineFunctionalPlayer player = OtherUtils.getPlayerByIP(args[0]);
                                        if(!player.isOnline()) {
                                            if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                        } else {
                                            if(player.getPlayer().hasPermission("functionalservercontrol.mute.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.target-bypass")));
                                                return;
                                            }
                                        }
                                        muteManager.preformMute(player, MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false);
                                        return;
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        muteManager.preformMuteByIp(args[1], MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false, false);
                                        return;
                                    }
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    muteManager.preformMuteByIp(args[1], MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false, true);
                                    return;
                                }
                            }

                            OfflineFunctionalPlayer player = OfflineFunctionalPlayer.get(args[1]);
                            if(player == null) {
                                if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }

                                muteManager.preformMute(args[1], MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false);

                                return;
                            } else {
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                }
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalservercontrol.mute.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.target-bypass")));
                                        return;
                                    }
                                }

                                muteManager.preformMute(player, MuteType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false);

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
                                        OfflineFunctionalPlayer player = OtherUtils.getPlayerByIP(args[0]);
                                        if(!player.isOnline()) {
                                            if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                        }
                                        if(player.isOnline()) {
                                            if(player.getPlayer().hasPermission("functionalservercontrol.mute.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.target-bypass")));
                                                return;
                                            }
                                        }
                                        muteManager.preformMute(player, MuteType.TIMED_NOT_IP, getReason(args, 2), sender, time, true);
                                        return;
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        muteManager.preformMuteByIp(args[0], MuteType.TIMED_NOT_IP, getReason(args, 2), sender, time, true, false);
                                        return;
                                    }
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    muteManager.preformMuteByIp(args[0], MuteType.TIMED_NOT_IP, getReason(args, 2), sender, time, true, true);
                                    return;
                                }
                            }

                            OfflineFunctionalPlayer player = OfflineFunctionalPlayer.get(args[0]);
                            if(player == null) {
                                if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }

                                muteManager.preformMute(args[0], MuteType.TIMED_NOT_IP, getReason(args, 2), sender, time, true);

                                return;
                            } else {
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                }
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalservercontrol.mute.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.target-bypass")));
                                        return;
                                    }
                                }

                                muteManager.preformMute(player, MuteType.TIMED_NOT_IP, getReason(args, 2), sender, time, true);

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
                                    if(OtherUtils.getPlayerByIP(args[0]) != null) {
                                        OfflineFunctionalPlayer player = OtherUtils.getPlayerByIP(args[0]);
                                        if(!player.isOnline()) {
                                            if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                                return;
                                            }
                                        }
                                        if(player.isOnline()) {
                                            if(player.getPlayer().hasPermission("functionalservercontrol.mute.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.target-bypass")));
                                                return;
                                            }
                                        }
                                        muteManager.preformMute(player, MuteType.TIMED_NOT_IP, getReason(args, 3), sender, time, false);
                                        return;
                                    } else {
                                        if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                        muteManager.preformMuteByIp(args[1], MuteType.TIMED_NOT_IP, getReason(args, 3), sender, time, false, false);
                                        return;
                                    }
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    muteManager.preformMuteByIp(args[1], MuteType.TIMED_NOT_IP, getReason(args, 3), sender, time, false, true);
                                    return;
                                }
                            }

                            OfflineFunctionalPlayer player = OfflineFunctionalPlayer.get(args[1]);
                            if(player == null) {
                                if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }

                                muteManager.preformMute(args[1], MuteType.TIMED_NOT_IP, getReason(args, 3), sender, time, false);

                                return;
                            } else {
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                }
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalservercontrol.mute.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.target-bypass")));
                                        return;
                                    }
                                }

                                muteManager.preformMute(player, MuteType.TIMED_NOT_IP, getReason(args, 3), sender, time, false);

                                return;
                            }
                        }


                        if(OtherUtils.isArgumentIP(args[0])) {


                            if(OtherUtils.isNotNullIp(args[0])) {
                                if(OtherUtils.getPlayerByIP(args[0]) != null) {
                                    OfflineFunctionalPlayer player = OtherUtils.getPlayerByIP(args[0]);
                                    if(!player.isOnline()) {
                                        if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                            return;
                                        }
                                    }
                                    if(player.isOnline()) {
                                        if(player.getPlayer().hasPermission("functionalservercontrol.mute.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                            sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.target-bypass")));
                                            return;
                                        }
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        muteManager.preformMute(player, MuteType.TIMED_NOT_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), true);
                                    } else {
                                        muteManager.preformMute(player, MuteType.PERMANENT_NOT_IP, getReason(args, 1), sender, -1, true);
                                    }
                                    return;
                                } else {
                                    if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                        return;
                                    }
                                    if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                        muteManager.preformMuteByIp(args[0], MuteType.TIMED_NOT_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), true, false);
                                    } else {
                                        muteManager.preformMuteByIp(args[0], MuteType.PERMANENT_NOT_IP, getReason(args, 1), sender, -1, true, false);
                                    }
                                    return;
                                }
                            } else {
                                if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                                if(!sender.hasPermission("functionalservercontrol.time-bypass")) {
                                    muteManager.preformMuteByIp(args[0], MuteType.TIMED_NOT_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), true, true);
                                } else {
                                    muteManager.preformMuteByIp(args[0], MuteType.PERMANENT_NOT_IP, getReason(args, 1), sender, -1, true, true);
                                }
                                return;
                            }
                        }

                        OfflineFunctionalPlayer player = OfflineFunctionalPlayer.get(args[0]);
                        if(player == null) {
                            if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                return;
                            }
                            if(!sender.hasPermission("functionalservercontrol.time-bypass")) {

                                muteManager.preformMute(args[0], MuteType.TIMED_NOT_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), true);

                            } else {

                                muteManager.preformMute(args[0], MuteType.PERMANENT_NOT_IP, getReason(args, 1), sender, -1, true);

                            }
                            return;
                        } else {
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalservercontrol.mute.offline")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.offline-no-perms")));
                                    return;
                                }
                            }
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalservercontrol.mute.bypass") && !sender.hasPermission("functionalservercontrol.bypass-break")) {
                                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.target-bypass")));
                                    return;
                                }
                            }
                            if(!sender.hasPermission("functionalservercontrol.time-bypass")) {

                                muteManager.preformMute(player, MuteType.TIMED_NOT_IP, getReason(args, 1), sender, timeSettingsAccessor.getTimeManager().getMaxPlayerMutePunishTime((FunctionalPlayer)sender), true);

                            } else {

                                muteManager.preformMute(player, MuteType.PERMANENT_NOT_IP, getReason(args, 1), sender, -1, true);

                            }
                            return;
                        }

                    }

                }

                if (args.length == 0) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.mute.example").replace("%1$f", label))); }
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
