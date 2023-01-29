package net.alis.functionalservercontrol.spigot.additional.tasks;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getProtectionSettings;

public class PingCheckTask extends BukkitRunnable {

    @Override
    public void run() {
        if(getProtectionSettings().isPingLimiterEnabled()) {
            for(FunctionalPlayer player : FunctionalApi.getOnlinePlayers()) {
                if(player.ping() > getProtectionSettings().getMaxAllowedPing()) {
                    for(String action : getProtectionSettings().getPingLimiterActions()) TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName())));
                }
            }
        }
    }

}
