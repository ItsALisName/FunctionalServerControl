package by.alis.functionalservercontrol.spigot.managers;

import by.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getDupeIpReports;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getDate;
import static by.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass.getTime;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

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
        TaskManager.preformAsync(() -> {
            if(getConfigSettings().isDupeIdModeEnabled() && getConfigSettings().getDupeIpCheckMode().equalsIgnoreCase("join")) {
                String joinedIp = player.getAddress().getAddress().getHostAddress();
                List<Player> similarPlayers = new ArrayList<>();
                for(Map.Entry<Player, String> e : TemporaryCache.getOnlineIps().entrySet()) {
                    if(e.getValue().equalsIgnoreCase(joinedIp)) {
                        if(!similarPlayers.contains(e.getKey())){
                            similarPlayers.add(e.getKey());
                        }
                        similarPlayers.add(player);
                    }
                }
                if(similarPlayers.size() > getConfigSettings().getMaxIpsPerSession()) {
                    for(Player similarPlayer : similarPlayers) {
                        if (!similarPlayer.hasPermission("functionalservercontrol.dupeip.bypass")) {
                            TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getConfigSettings().getDupeIpAction().replace("%1$f", similarPlayer.getName())));
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
        TaskManager.preformAsync(() -> {
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
                getDupeIpReports().setReportInitiator(initiator instanceof Player ? ((Player) initiator).getName() : getGlobalVariables().getConsoleVariableName());
                initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.reports.report-created").replace("%1$f", String.valueOf(120))));
            }
            TaskManager.preformAsyncLater(new startRemoveTimer(), 2400L);
        });
    }


    static class startRemoveTimer extends BukkitRunnable {
        @Override
        public void run() {
            if(getDupeIpReports().isReportExists()) {
                getDupeIpReports().deleteReport();
                this.cancel();
            } else { this.cancel(); }
        }

    }


}
