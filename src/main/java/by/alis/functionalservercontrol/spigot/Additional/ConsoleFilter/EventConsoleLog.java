package by.alis.functionalservercontrol.spigot.Additional.ConsoleFilter;

import by.alis.functionalservercontrol.API.Spigot.Events.deprecated.ConsoleLogOutEvent;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.Managers.Files.FileAccessor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

@Deprecated
public class EventConsoleLog implements Filter {

    FileAccessor accessor = new FileAccessor();


    public Filter.Result checkMessage(String message) {
        if(runCheck(message).equals(Result.DENY)) {
            return Result.DENY;
        } else {
            return Result.NEUTRAL;
        }
    }

    private Result runCheck(String consoleMessage) {
        if(consoleMessage == null) return Result.NEUTRAL;
        String[] message = {consoleMessage};

            if (Bukkit.getPluginManager().isPluginEnabled(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class))) {
                (new BukkitRunnable() {
                    @Override
                    public void run() {
                        ConsoleLogOutEvent consoleLogEvent = new ConsoleLogOutEvent(consoleMessage, getConfigSettings().isApiEnabled());
                        if(consoleLogEvent.isApiEnabled()) {
                            if(!getConfigSettings().isApiProtectedByPassword()) {
                                Bukkit.getPluginManager().callEvent(consoleLogEvent);
                            } else {
                                if(consoleLogEvent.getApiPassword() != null && consoleLogEvent.getApiPassword().equalsIgnoreCase(accessor.getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                                    Bukkit.getPluginManager().callEvent(consoleLogEvent);
                                }
                            }
                        } else {
                            cancel();
                        }
                    }
                }).runTask(FunctionalServerControl.getPlugin(FunctionalServerControl.class));
            }
        return Result.NEUTRAL;
    }

    public LifeCycle.State getState() {
        try {
            return LifeCycle.State.STARTED;
        } catch (Exception exception) {
            return null;
        }
    }

    public void initialize() {}

    public boolean isStarted() {
        return true;
    }

    public boolean isStopped() {
        return false;
    }

    public void start() {}

    public void stop() {}

    public Filter.Result filter(LogEvent event) {
        return checkMessage(event.getMessage().getFormattedMessage());
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object... arg4) {
        return checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4) {
        return checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, Object message, Throwable arg4) {
        return checkMessage(message.toString());
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, Message message, Throwable arg4) {
        return checkMessage(message.getFormattedMessage());
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5) {
        return checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6) {
        return checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7) {
        return checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8) {
        return checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9) {
        return checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10) {
        return checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11) {
        return checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12) {
        return checkMessage(message);
    }

    public Filter.Result filter(Logger arg0, Level arg1, Marker arg2, String message, Object arg4, Object arg5, Object arg6, Object arg7, Object arg8, Object arg9, Object arg10, Object arg11, Object arg12, Object arg13) {
        return checkMessage(message);
    }

    public Filter.Result getOnMatch() {
        return Filter.Result.NEUTRAL;
    }

    public Filter.Result getOnMismatch() {
        return Filter.Result.NEUTRAL;
    }
}
