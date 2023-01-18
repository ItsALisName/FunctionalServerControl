package net.alis.functionalservercontrol.spigot.managers.time;

public class TimeSettingsAccessor {

    private final InputTimeChecker timeChecker = new InputTimeChecker();
    private final TimeManager timeManager = new TimeManager();

    public InputTimeChecker getTimeChecker() {
        return timeChecker;
    }
    public TimeManager getTimeManager() {
        return timeManager;
    }

}
