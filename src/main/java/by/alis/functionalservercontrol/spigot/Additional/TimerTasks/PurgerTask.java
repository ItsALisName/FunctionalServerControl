package by.alis.functionalservercontrol.spigot.Additional.TimerTasks;

import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TemporaryCache;
import org.bukkit.scheduler.BukkitRunnable;

public class PurgerTask extends BukkitRunnable {

    @Override
    public void run() {
        TemporaryCache.getUnsafeBannedPlayers().clear();
        TemporaryCache.getUnsafeMutedPlayers().clear();
    }

}
