package by.alis.functionalservercontrol.spigot.additional.tasks;

import by.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import org.bukkit.scheduler.BukkitRunnable;

import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;

public class PurgerTask extends BukkitRunnable {

    @Override
    public void run() {
        TemporaryCache.getUnsafeBannedPlayers().clear();
        TemporaryCache.getUnsafeMutedPlayers().clear();
        getBaseManager().clearHistory();
    }

}
