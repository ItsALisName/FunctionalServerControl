package net.alis.functionalservercontrol.spigot.commands;

import net.alis.functionalservercontrol.spigot.additional.misc.device.DeviceInformation;
import net.alis.functionalservercontrol.spigot.commands.completers.DeviceInfoCompleter;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class DeviceInfoCommand implements CommandExecutor {

    public DeviceInfoCommand(FunctionalServerControl plugin) {
        plugin.getCommand("deviceinfo").setExecutor(this);
        plugin.getCommand("deviceinfo").setTabCompleter(new DeviceInfoCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        TaskManager.preformAsync(() -> {
            if(sender.hasPermission("functionalservercontrol.deviceinfo")) {
                if(args.length != 1) {
                    if(getConfigSettings().showDescription()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.deviceinfo.description").replace("%1$f", label)));
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.deviceinfo.usage").replace("%1$f", label)));
                    if(getConfigSettings().showExamples()) sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.deviceinfo.example").replace("%1$f", label)));
                    return;
                }
                if(args[0].equalsIgnoreCase("serverpart")) {
                    sender.sendMessage(setColors(
                            String.join("\n", getFileAccessor().getLang().getStringList("commands.deviceinfo.serverpart-format"))
                                    .replace("%1$f", DeviceInformation.ServerInfo.getTpsLastSecond())
                                    .replace("%2$f", DeviceInformation.ServerInfo.getTpsLastFiveSeconds())
                                    .replace("%3$f", DeviceInformation.ServerInfo.getTpsLastThirtySeconds())
                                    .replace("%4$f", DeviceInformation.ServerInfo.getTpsLastMinute())
                                    .replace("%5$f", DeviceInformation.ServerInfo.getTpsLastFiveMinutes())
                                    .replace("%6$f", DeviceInformation.ServerInfo.getTpsLastTenMinutes())
                                    .replace("%7$f", DeviceInformation.ServerInfo.getAverageTps())
                                    .replace("%8$f", DeviceInformation.ServerInfo.getMaxMemory())
                                    .replace("%9$f", DeviceInformation.ServerInfo.getAvailableMemory())
                                    .replace("%10$f", DeviceInformation.ServerInfo.getUsedMemory())
                                    .replace("%11$f", DeviceInformation.ServerInfo.getFreeMemory())
                                    .replace("%12$f", DeviceInformation.ServerInfo.getCpuSystemUsage())
                                    .replace("%13$f", DeviceInformation.ServerInfo.getCpuJavaUsage())
                                    .replace("%14$f", DeviceInformation.ServerInfo.getAverageCpuUsage())
                                    .replace("%15$f", DeviceInformation.ServerInfo.getUptime())
                    ));
                    return;
                }
                if(args[0].equalsIgnoreCase("machinepart")) {
                    sender.sendMessage(setColors(
                            String.join("\n", getFileAccessor().getLang().getStringList("commands.deviceinfo.machinepart-format"))
                                    .replace("%1$f", DeviceInformation.MachineInfo.getJavaVersion())
                                    .replace("%2$f", DeviceInformation.MachineInfo.getJDKVersion())
                                    .replace("%3$f", DeviceInformation.MachineInfo.getJavaRuntimeVersion())
                                    .replace("%4$f", DeviceInformation.MachineInfo.getOSName())
                                    .replace("%5$f", DeviceInformation.MachineInfo.getOSVersion())
                                    .replace("%6$f", DeviceInformation.MachineInfo.getOSArch())
                                    .replace("%7$f", DeviceInformation.MachineInfo.getAvailableCores())
                    ));
                    return;
                }
            } else {
                sender.sendMessage(setColors(getFileAccessor().getLang().getString("other.no-permissions")));
            }
        });
        return true;
    }
}
