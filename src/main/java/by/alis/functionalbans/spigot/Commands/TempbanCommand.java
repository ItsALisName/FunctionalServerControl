package by.alis.functionalbans.spigot.Commands;

import by.alis.functionalbans.spigot.Additional.Enums.BanType;
import by.alis.functionalbans.spigot.Additional.Other.OtherUtils;
import by.alis.functionalbans.spigot.Commands.Completers.TempbanCompleter;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.BansManagers.BanManager;
import by.alis.functionalbans.spigot.Managers.CooldownsManager;
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

public class TempbanCommand implements CommandExecutor {

    FunctionalBansSpigot plugin;
    public TempbanCommand(FunctionalBansSpigot plugin) {
        this.plugin = plugin;
        plugin.getCommand("tempban").setExecutor(this);
        plugin.getCommand("tempban").setTabCompleter(new TempbanCompleter());
    }
    private final FileAccessor accessor = new FileAccessor();
    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
    private final BanManager banManager = new BanManager();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender.hasPermission("functionalbans.temp-ban")) {

            if(args.length >= 1) {


                if(args.length == 1 && args[0].equalsIgnoreCase("-s")) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.tempban.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(this.accessor.getLang().getString("commands.tempban.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.tempban.example").replace("%1$f", label))); }
                    return true;
                }

                if(args.length == 1 && args[0].equalsIgnoreCase("-a")) {
                    if(sender.hasPermission("functionalbans.use.unsafe-flags")) {
                        sender.sendMessage(setColors(this.accessor.getLang().getString("other.flag-not-support").replace("%1$f", args[0])));
                    } else {
                        sender.sendMessage(setColors(this.accessor.getLang().getString("other.flag-no-perms").replace("%1$f", label)));
                    }
                    return true;
                }

                if(args.length == 1) {
                    if (getConfigSettings().showDescription()) {
                        sender.sendMessage(setColors(this.accessor.getLang().getString("commands.tempban.description").replace("%1$f", label)));
                    }
                    sender.sendMessage(setColors(this.accessor.getLang().getString("commands.tempban.usage").replace("%1$f", label)));
                    if (getConfigSettings().showExamples()) {
                        sender.sendMessage(setColors(this.accessor.getLang().getString("commands.tempban.example").replace("%1$f", label)));
                    }
                }

                if(args.length == 2 && args[0].equalsIgnoreCase("-s")) {
                    if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.tempban.description").replace("%1$f", label))); }
                    sender.sendMessage(setColors(this.accessor.getLang().getString("commands.tempban.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.tempban.example").replace("%1$f", label))); }
                    return true;
                }

                if(args.length == 2 && args[0].equalsIgnoreCase("-a")) {
                    if(sender.hasPermission("functionalbans.use.unsafe-flags")) {
                        sender.sendMessage(setColors(this.accessor.getLang().getString("other.flag-not-support").replace("%1$f", args[0])));
                    } else {
                        sender.sendMessage(setColors(this.accessor.getLang().getString("other.flag-no-perms").replace("%1$f", label)));
                    }
                    return true;
                }

                if(args.length == 2) {
                    if(this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[1])) {
                        if(!sender.hasPermission("functionalbans.use.no-reason")) {
                            sender.sendMessage(setColors(this.accessor.getLang().getString("other.no-reason")));
                            return true;
                        }

                        if(getConfigSettings().isProhibitYourselfInteraction()) {
                            if(args[0].equalsIgnoreCase(sender.getName())) {
                                sender.sendMessage(setColors(this.accessor.getLang().getString("other.no-yourself-actions")));
                                return true;
                            }
                        }

                        long time = this.timeSettingsAccessor.getTimeManager().convertToMillis(args[1]);
                        if(!sender.hasPermission("functionalbans.time-bypass")) {
                            if(time > this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player) sender)) {
                                sender.sendMessage(setColors(this.accessor.getLang().getString("other.ban-over-time").replace("%1$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player) sender)))));
                                return true;
                            }
                        }

                        if(sender instanceof Player) {
                            if(CooldownsManager.playerHasCooldown(((Player) sender).getPlayer(), command.getName())) {
                                CooldownsManager.notifyAboutCooldown(((Player) sender).getPlayer(), command.getName());
                                return true;
                            } else {
                                CooldownsManager.setCooldown(((Player) sender).getPlayer(), command.getName());
                            }
                        }

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                        if(OtherUtils.isNotNullPlayer(args[0])) {
                            if(!sender.hasPermission("functionalbans.temp-ban.offline")) {
                                sender.sendMessage(setColors(this.accessor.getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                this.banManager.preformBan(args[0], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true);
                            });
                            return true;
                        } else {
                            if(!player.isOnline()) {
                                if(!sender.hasPermission("functionalbans.temp-ban.offline")) {
                                    sender.sendMessage(setColors(this.accessor.getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if(player.isOnline()) {
                                if(player.getPlayer().hasPermission("functionalbans.ban.bypass") && !sender.hasPermission("functionalbans.bypass-break")) {
                                    sender.sendMessage(setColors(this.accessor.getLang().getString("commands.ban.target-bypass")));
                                    return true;
                                }
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                this.banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, true);
                            });
                        }

                    } else {
                        sender.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-time").replace("%1$f", args[1])));
                        return true;
                    }
                }

                if(args.length >= 3) {

                    if(args.length >= 3 && args[0].equalsIgnoreCase("-a")) {
                        if(sender.hasPermission("functionalbans.use.unsafe-flags")) {
                            sender.sendMessage(setColors(this.accessor.getLang().getString("other.flag-not-support").replace("%1$f", args[0])));
                        } else {
                            sender.sendMessage(setColors(this.accessor.getLang().getString("other.flag-no-perms").replace("%1$f", label)));
                        }
                        return true;
                    }

                    if(args.length == 3 && args[0].equalsIgnoreCase("-s")) {
                        if(this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {

                            if (!sender.hasPermission("functionalbans.use.silently")) {
                                sender.sendMessage(setColors(this.accessor.getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                                return true;
                            }

                            if(!sender.hasPermission("functionalbans.use.no-reason")) {
                                sender.sendMessage(setColors(this.accessor.getLang().getString("other.no-reason")));
                                return true;
                            }

                            if(getConfigSettings().isProhibitYourselfInteraction()) {
                                if(args[1].equalsIgnoreCase(sender.getName())) {
                                    sender.sendMessage(setColors(this.accessor.getLang().getString("other.no-yourself-actions")));
                                    return true;
                                }
                            }

                            long time = this.timeSettingsAccessor.getTimeManager().convertToMillis(args[2]);
                            if(!sender.hasPermission("functionalbans.time-bypass")) {
                                if(time > this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player) sender)) {
                                    sender.sendMessage(setColors(this.accessor.getLang().getString("other.ban-over-time").replace("%1$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player) sender)))));
                                    return true;
                                }
                            }

                            if(sender instanceof Player) {
                                if(CooldownsManager.playerHasCooldown(((Player) sender).getPlayer(), command.getName())) {
                                    CooldownsManager.notifyAboutCooldown(((Player) sender).getPlayer(), command.getName());
                                    return true;
                                } else {
                                    CooldownsManager.setCooldown(((Player) sender).getPlayer(), command.getName());
                                }
                            }

                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                            if(OtherUtils.isNotNullPlayer(args[1])) {
                                if(!sender.hasPermission("functionalbans.temp-ban.offline")) {
                                    sender.sendMessage(setColors(this.accessor.getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    this.banManager.preformBan(args[1], BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false);
                                });
                                return true;
                            } else {
                                if(!player.isOnline()) {
                                    if(!sender.hasPermission("functionalbans.temp-ban.offline")) {
                                        sender.sendMessage(setColors(this.accessor.getLang().getString("other.offline-no-perms")));
                                        return true;
                                    }
                                }
                                if(player.isOnline()) {
                                    if(player.getPlayer().hasPermission("functionalbans.ban.bypass") && !sender.hasPermission("functionalbans.bypass-break")) {
                                        sender.sendMessage(setColors(this.accessor.getLang().getString("commands.ban.target-bypass")));
                                        return true;
                                    }
                                }
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    this.banManager.preformBan(player, BanType.TIMED_NOT_IP, getGlobalVariables().getDefaultReason(), sender, time, false);
                                });
                            }

                        } else {
                            sender.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-time").replace("%1$f", args[2])));
                            return true;
                        }
                    }

                    if(args.length > 3 && args[0].equalsIgnoreCase("-s")) {

                        if (this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[2])) {
                            if (!sender.hasPermission("functionalbans.use.silently")) {
                                sender.sendMessage(setColors(this.accessor.getLang().getString("other.flag-no-perms").replace("%1$f", "-s")));
                                return true;
                            }

                            if (getConfigSettings().isProhibitYourselfInteraction()) {
                                if (args[1].equalsIgnoreCase(sender.getName())) {
                                    sender.sendMessage(setColors(this.accessor.getLang().getString("other.no-yourself-actions")));
                                    return true;
                                }
                            }

                            long time = this.timeSettingsAccessor.getTimeManager().convertToMillis(args[2]);
                            if (!sender.hasPermission("functionalbans.time-bypass")) {
                                if (time > this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player) sender)) {
                                    sender.sendMessage(setColors(this.accessor.getLang().getString("other.ban-over-time").replace("%1$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player) sender)))));
                                    return true;
                                }
                            }

                            if (sender instanceof Player) {
                                if (CooldownsManager.playerHasCooldown(((Player) sender).getPlayer(), command.getName())) {
                                    CooldownsManager.notifyAboutCooldown(((Player) sender).getPlayer(), command.getName());
                                    return true;
                                } else {
                                    CooldownsManager.setCooldown(((Player) sender).getPlayer(), command.getName());
                                }
                            }

                            OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                            if (OtherUtils.isNotNullPlayer(args[1])) {
                                if (!sender.hasPermission("functionalbans.temp-ban.offline")) {
                                    sender.sendMessage(setColors(this.accessor.getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    this.banManager.preformBan(args[1], BanType.TIMED_NOT_IP, getReason(args, 3), sender, time, false);
                                });
                                return true;
                            } else {
                                if (!player.isOnline()) {
                                    if (!sender.hasPermission("functionalbans.temp-ban.offline")) {
                                        sender.sendMessage(setColors(this.accessor.getLang().getString("other.offline-no-perms")));
                                        return true;
                                    }
                                }
                                if (player.isOnline()) {
                                    if (player.getPlayer().hasPermission("functionalbans.ban.bypass") && !sender.hasPermission("functionalbans.bypass-break")) {
                                        sender.sendMessage(setColors(this.accessor.getLang().getString("commands.ban.target-bypass")));
                                        return true;
                                    }
                                }
                                Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                    this.banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 3), sender, time, false);
                                });
                            }
                            return true;
                        } else {
                            sender.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-time").replace("%1$f", args[2])));
                        }
                        return true;
                    }

                    if (this.timeSettingsAccessor.getTimeChecker().checkInputTimeArgument(args[1])) {

                        if (getConfigSettings().isProhibitYourselfInteraction()) {
                            if (args[0].equalsIgnoreCase(sender.getName())) {
                                sender.sendMessage(setColors(this.accessor.getLang().getString("other.no-yourself-actions")));
                                return true;
                            }
                        }

                        long time = this.timeSettingsAccessor.getTimeManager().convertToMillis(args[1]);
                        if (!sender.hasPermission("functionalbans.time-bypass")) {
                            if (time > this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player) sender)) {
                                sender.sendMessage(setColors(this.accessor.getLang().getString("other.ban-over-time").replace("%1$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getMaxPlayerPunishTime((Player) sender)))));
                                return true;
                            }
                        }

                        if (sender instanceof Player) {
                            if (CooldownsManager.playerHasCooldown(((Player) sender).getPlayer(), command.getName())) {
                                CooldownsManager.notifyAboutCooldown(((Player) sender).getPlayer(), command.getName());
                                return true;
                            } else {
                                CooldownsManager.setCooldown(((Player) sender).getPlayer(), label);
                            }
                        }

                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                        if (OtherUtils.isNotNullPlayer(args[0])) {
                            if (!sender.hasPermission("functionalbans.temp-ban.offline")) {
                                sender.sendMessage(setColors(this.accessor.getLang().getString("other.offline-no-perms")));
                                return true;
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                this.banManager.preformBan(args[0], BanType.TIMED_NOT_IP, getReason(args, 2), sender, time, true);
                            });
                            return true;
                        } else {
                            if (!player.isOnline()) {
                                if (!sender.hasPermission("functionalbans.temp-ban.offline")) {
                                    sender.sendMessage(setColors(this.accessor.getLang().getString("other.offline-no-perms")));
                                    return true;
                                }
                            }
                            if (player.isOnline()) {
                                if (player.getPlayer().hasPermission("functionalbans.ban.bypass") && !sender.hasPermission("functionalbans.bypass-break")) {
                                    sender.sendMessage(setColors(this.accessor.getLang().getString("commands.ban.target-bypass")));
                                    return true;
                                }
                            }
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
                                this.banManager.preformBan(player, BanType.TIMED_NOT_IP, getReason(args, 2), sender, time, true);
                            });
                        }
                        return true;

                    } else {
                        sender.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-time").replace("%1$f", args[1])));
                    }
                    return true;

                }
            }


            if(args.length == 0) {
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.tempban.description").replace("%1$f", label))); }
                sender.sendMessage(setColors(this.accessor.getLang().getString("commands.tempban.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(this.accessor.getLang().getString("commands.tempban.example").replace("%1$f", label))); }
                return true;
            }
        } else {
            sender.sendMessage(setColors(this.accessor.getLang().getString("other.no-permissions")));
            return true;
        }

        return true;
    }
}
