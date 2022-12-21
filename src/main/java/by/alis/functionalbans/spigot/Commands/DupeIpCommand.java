package by.alis.functionalbans.spigot.Commands;

import by.alis.functionalbans.spigot.Commands.Completers.DupeIpCompleter;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.DupeIpManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalbans.spigot.Additional.Containers.StaticContainers.getDupeIpReports;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.getReason;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalbans.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class DupeIpCommand implements CommandExecutor {

    FunctionalBansSpigot plugin;
    public DupeIpCommand(FunctionalBansSpigot plugin) {
        this.plugin = plugin;
        plugin.getCommand("dupeip").setExecutor(this);
        plugin.getCommand("dupeip").setTabCompleter(new DupeIpCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender.hasPermission("functionalbans.dupeip")) {

            if(args.length == 0) {
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.description").replace("%1$f", label))); }
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.example").replace("%1$f", label))); }
                return true;
            }

            if(args[0].equalsIgnoreCase("createreport")) {
                if(sender.hasPermission("functionalbans.dupeip.create-report")) {
                    if(getDupeIpReports().isReportExists()) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.reports.report-deleted")));
                        getDupeIpReports().deleteReport();
                    }
                    DupeIpManager.prepareDupeIpReport(sender);
                } else {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.reports.create-no-perms")));
                }
                return true;
            }
            
            if(args[0].equalsIgnoreCase("deletereport")) {
                if(sender.hasPermission("functionalbans.dupeip.delete-report")) {
                    if(getDupeIpReports().isReportExists()) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.reports.report-deleted")));
                        getDupeIpReports().deleteReport();
                    } else {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.reports.nothing-to-delete")));
                    }
                } else {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.reports.delete-no-perms")));
                }
                return true;
            }

            if(args[0].equalsIgnoreCase("info")) {
                if(getDupeIpReports().isReportExists()) {

                    if(getDupeIpReports().getDupeIps().size() == 0) {
                        sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.reports.similar-ips-not-found")));
                        getDupeIpReports().deleteReport();
                        return true;
                    }

                    Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                        String reportFormat = String.join("\n", getFileAccessor().getLang().getStringList("commands.dupeip.reports.report-format")).replace("%1$f", getDupeIpReports().getTime()).replace("%2$f", getDupeIpReports().getReportInitiator());
                        reportFormat = reportFormat
                                .replace(
                                        "%3$f",
                                        getDupeIpReports().getInfo()
                                );
                        sender.sendMessage(setColors(reportFormat));
                    });

                    return true;
                } else {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.reports.report-not-found")));
                    return true;
                }
            }

            if(args[0].equalsIgnoreCase("kick")) {
                if(!sender.hasPermission("functionalbans.dupeip.kick")) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
                    return true;
                }
                if(getDupeIpReports().isReportExists()) {
                    if(args.length == 2 && args[1].equalsIgnoreCase("-s")) {
                        getDupeIpReports().preformKickDupeip(sender, null, false);
                        getDupeIpReports().deleteReport();
                        return true;
                    }

                    if(args.length > 2 && args[1].equalsIgnoreCase("-s")) {
                        getDupeIpReports().preformKickDupeip(sender, getReason(args, 2), false);
                        getDupeIpReports().deleteReport();
                        return true;
                    }

                    if(args.length == 1) {
                        getDupeIpReports().preformKickDupeip(sender, null, true);
                        getDupeIpReports().deleteReport();
                        return true;
                    }

                    getDupeIpReports().preformKickDupeip(sender, getReason(args, 1), true);
                    getDupeIpReports().deleteReport();
                } else {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.reports.report-not-found")));
                }
                return true;
            }

        } else {
            sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            return true;
        }

        return true;
    }
}
