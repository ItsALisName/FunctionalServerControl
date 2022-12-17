package by.alis.functionalbans.spigot.Additional.TimerTasks;

import by.alis.functionalbans.spigot.Additional.TimerTasks.Tasks.DupeIpTask;
import by.alis.functionalbans.spigot.Additional.TimerTasks.Tasks.PurgerTask;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

public class TimerSetuper {

    public void setupTimers() {
        if (getConfigSettings().isAutoPurgerEnabled()) {
            if (getConfigSettings().getAutoPurgerDelay() > 5) {
                new PurgerTask().runTaskTimerAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), 0, getConfigSettings().getAutoPurgerDelay() * 20L);
            }
        }
        if(getConfigSettings().isDupeIdModeEnabled()) {
            new DupeIpTask().runTaskTimerAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), 0, getConfigSettings().getDupeIpTimerDelay() * 20L);
        }
    }

}
