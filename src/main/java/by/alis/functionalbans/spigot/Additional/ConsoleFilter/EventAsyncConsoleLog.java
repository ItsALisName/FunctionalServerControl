package by.alis.functionalbans.spigot.Additional.ConsoleFilter;

import by.alis.functionalbans.API.Spigot.Events.AsyncConsoleLogOutEvent;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.FilesManagers.FileAccessor;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static org.bukkit.Bukkit.getServer;

public class EventAsyncConsoleLog implements Filter {

    FileAccessor accessor = new FileAccessor();


    public Filter.Result checkMessage(String message) {
        AsyncConsoleLogOutEvent asyncConsoleLogOutEvent = new AsyncConsoleLogOutEvent(message, getConfigSettings().isApiEnabled());
        if(!getConfigSettings().isApiEnabled()) return Result.NEUTRAL;
        if(!getConfigSettings().isApiProtectedByPassword()) {
            if (Bukkit.getPluginManager().isPluginEnabled(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class))) {
                Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                    Bukkit.getPluginManager().callEvent(asyncConsoleLogOutEvent);
                });
            }
        } else {
            if(asyncConsoleLogOutEvent.getApiPassword() != null && asyncConsoleLogOutEvent.getApiPassword().equalsIgnoreCase(accessor.getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                if (Bukkit.getPluginManager().isPluginEnabled(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class))) {
                    Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                        Bukkit.getPluginManager().callEvent(asyncConsoleLogOutEvent);
                    });
                }
            }
        }
        if(asyncConsoleLogOutEvent.isCancelled()) return Result.DENY;
        if(!asyncConsoleLogOutEvent.getMessage().equalsIgnoreCase(message) || !message.equalsIgnoreCase(asyncConsoleLogOutEvent.getMessage())) {
            Bukkit.getConsoleSender().sendMessage(asyncConsoleLogOutEvent.getMessage());
            return Result.DENY;
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

