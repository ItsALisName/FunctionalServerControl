package net.alis.functionalservercontrol.spigot.additional.consolefilter;

import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;

public class ReplaceFilter implements Filter {

    public Filter.Result checkMessage(String message) {
        if(message.contains("Exception") && message.contains("at net.alis.functionalservercontrol.spigot")) {
            TaskManager.preformAsync(() -> {
                String exception = "UnknownException";
                for(String ex : message.split(" ")) {
                    String[] eArgs = ex.split("\\.");
                    if(ex.startsWith("java.lang") && ex.replace(":", "").endsWith("Exception")) {
                        exception = eArgs[eArgs.length - 1].replace("java.lang.", "");
                        break;
                    }
                    if(ex.startsWith("org.bukkit.event") && ex.replace(":", "").endsWith("Exception")) {
                        exception = eArgs[eArgs.length - 1].replace(":", "");
                        break;
                    }
                }
                for(Player admin : Bukkit.getOnlinePlayers()) {
                    if(admin.hasPermission("functionalservercontrol.notification.plugin-error")) {
                        admin.sendMessage(setColors("&4[FunctionalServerControlSpigot] An '%e%' exception was thrown while the plugin was running. Check the console for details and contact the plugin author to fix the bug!").replace("%e%", exception));
                    }
                }
            });
        }

        if(ConsoleFilterHelper.getConsoleFilterHelper().isMessageToReplace(message)) {
            Bukkit.getConsoleSender().sendMessage(ConsoleFilterHelper.getConsoleFilterHelper().replaceConsoleMessage(message));
            return Filter.Result.DENY;
        }
        if (ConsoleFilterHelper.getConsoleFilterHelper().isFunctionalServerControlCommand(message)) {
            String playerName = message.split(" ")[0];
            Player player = Bukkit.getPlayer(playerName);
            if(player == null) {
                playerName = getGlobalVariables().getConsoleVariableName();
            } else {
                playerName = player.getName();
            }
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControlSpigot | Log] Player %player% &eused the command: &6%command%".replace("%player%", playerName).replace("%command%", ConsoleFilterHelper.getConsoleFilterHelper().getUsedFunctionalServerControlCommand(message))));
            return Filter.Result.DENY;
        }
        return Filter.Result.NEUTRAL;
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
