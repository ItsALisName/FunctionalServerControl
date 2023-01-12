package by.alis.functionalservercontrol.spigot.commands;

import by.alis.functionalservercontrol.spigot.commands.completers.DupeIpCompleter;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.managers.DupeIpManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getDupeIpReports;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.getReason;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class DupeIpCommand implements CommandExecutor {

    FunctionalServerControl plugin;
    public DupeIpCommand(FunctionalServerControl plugin) {
        this.plugin = plugin;
        plugin.getCommand("dupeip").setExecutor(this);
        plugin.getCommand("dupeip").setTabCompleter(new DupeIpCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender.hasPermission("functionalservercontrol.dupeip")) {

            if(args.length == 0) {
                if(getConfigSettings().showDescription()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.description").replace("%1$f", label))); }
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.usage").replace("%1$f", label)));
                if(getConfigSettings().showExamples()) { sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.example").replace("%1$f", label))); }
                return true;
            }

            if(args[0].equalsIgnoreCase("createreport")) {
                if(sender.hasPermission("functionalservercontrol.dupeip.create-report")) {
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
                if(sender.hasPermission("functionalservercontrol.dupeip.delete-report")) {
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

                    Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
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
                if(!sender.hasPermission("functionalservercontrol.dupeip.kick")) {
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
