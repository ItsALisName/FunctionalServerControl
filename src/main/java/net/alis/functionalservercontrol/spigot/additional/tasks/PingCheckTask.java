package net.alis.functionalservercontrol.spigot.additional.tasks;

import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.additional.coreadapters.Adapter;
import net.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getProtectionSettings;

public class PingCheckTask extends BukkitRunnable {

    Adapter adapter = CoreAdapter.getAdapter();

    @Override
    public void run() {
        if(getProtectionSettings().isPingLimiterEnabled()) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(adapter.getPing(player) > getProtectionSettings().getMaxAllowedPing()) {
                    for(String action : getProtectionSettings().getPingLimiterActions()) TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName())));
                }
            }
        }
    }

}
