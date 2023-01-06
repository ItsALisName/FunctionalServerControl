package by.alis.functionalservercontrol.spigot.Additional.TimerTasks;

import by.alis.functionalservercontrol.spigot.Additional.Misc.TemporaryCache;
import org.bukkit.scheduler.BukkitRunnable;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

public class PurgerTask extends BukkitRunnable {

    @Override
    public void run() {
        TemporaryCache.getUnsafeBannedPlayers().clear();
        TemporaryCache.getUnsafeMutedPlayers().clear();
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: getSQLiteManager().clearHistory();
            case H2: {}
        }
    }

}
