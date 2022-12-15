package by.alis.functionalbans.spigot.Additional.TimerTasks.Tasks;

import by.alis.functionalbans.spigot.Additional.Other.TemporaryCache;
import org.bukkit.scheduler.BukkitRunnable;

public class PurgerTask extends BukkitRunnable {

    @Override
    public void run() {
        TemporaryCache.getUnsafeBannedPlayers().clear();
        TemporaryCache.getUnsafeMutedPlayers().clear();
    }

}
