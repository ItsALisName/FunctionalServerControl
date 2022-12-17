package by.alis.functionalbans.spigot.Managers;

import by.alis.functionalbans.spigot.Additional.Other.TemporaryCache;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static by.alis.functionalbans.spigot.Additional.Containers.StaticContainers.getDupeIpReports;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalbans.spigot.Additional.WorldDate.WorldTimeAndDateClass.getDate;
import static by.alis.functionalbans.spigot.Additional.WorldDate.WorldTimeAndDateClass.getTime;
import static by.alis.functionalbans.spigot.Managers.Files.SFAccessor.getFileAccessor;

/**
 * The class responsible for the DupeIp validation methods
 */
public class DupeIpManager {

    /**
     * Full static class
     */
    public DupeIpManager() { }

    /**
     * Asynchronously checks for similar IPs when a new player logs in
     * @param player Player to be tested
     */
    public static void checkDupeIpOnJoin(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
            if(getConfigSettings().isDupeIdModeEnabled() && getConfigSettings().getDupeIpCheckMode().equalsIgnoreCase("join")) {
                int count = 0;
                String joinedIp = player.getAddress().getAddress().getHostAddress();
                List<Player> similarPlayers = new ArrayList<>();
                for(Map.Entry<Player, String> e : TemporaryCache.getOnlineIps().entrySet()) {
                    if(e.getValue().equalsIgnoreCase(joinedIp)) {
                        count = count + 1;
                        similarPlayers.add(e.getKey());
                    }
                }

                if(count > getConfigSettings().getMaxIpsPerSession()) {
                    for(Player similarPlayer : similarPlayers) {
                        if (!similarPlayer.hasPermission("functionalbans.dupeip.bypass")) {
                            Bukkit.getScheduler().runTask(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getConfigSettings().getDupeIpAction().replace("%1$f", similarPlayer.getName()));
                            });
                        }
                    }
                }
            }
        });
    }

    /**
     * Asynchronously creates a report about similar IP addresses
     */
    public static void prepareDupeIpReport(@Nullable CommandSender initiator) {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
            if(initiator != null) {
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.reports.report-preparing")));
            }
            for(Player player : Bukkit.getOnlinePlayers()) {
                String currentIp = player.getAddress().getAddress().getHostAddress();
                for(Player checkedPlayer : Bukkit.getOnlinePlayers()) {
                    if(checkedPlayer.getAddress().getAddress().getHostAddress().equalsIgnoreCase(currentIp) && !checkedPlayer.getName().equalsIgnoreCase(player.getName())) {
                        if(!getDupeIpReports().getDupePlayers().containsKey(checkedPlayer)) {
                            if(!getDupeIpReports().getDupeIps().contains(currentIp)) {
                                getDupeIpReports().setDupeIps(currentIp);
                            }
                            getDupeIpReports().setDupePlayers(checkedPlayer);
                        }
                    }
                }
            }
            getDupeIpReports().setTime(getDate() + " " + getTime());
            getDupeIpReports().setReportExists(true);
            if(initiator != null) {
                getDupeIpReports().setReportInitiator(initiator instanceof Player ? ((Player) initiator).getPlayerListName() : getGlobalVariables().getConsoleVariableName());
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.reports.report-created")));
            }
        });
    }


}
