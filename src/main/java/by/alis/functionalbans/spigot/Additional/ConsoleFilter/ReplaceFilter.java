package by.alis.functionalbans.spigot.Additional.ConsoleFilter;

import by.alis.functionalbans.spigot.Additional.GlobalSettings.ConsoleLanguages.LangEnglish;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.ConsoleLanguages.LangRussian;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class ReplaceFilter implements Filter {

    public Filter.Result checkMessage(String message) {
        if (StaticConsoleFilterHelper.getConsoleFilterHelper().isFunctionalBansCommand(message)) {
            String playerName = message.split(" ")[0];
            Player player = Bukkit.getPlayer(playerName);
            if(player == null) {
                playerName = getGlobalVariables().getConsoleVariableName();
            } else {
                playerName = player.getPlayerListName();
            }
            switch (getConfigSettings().getConsoleLanguageMode()) {
                case "ru_RU":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.FILTER_PLAYER_USER_FUNCTIONAL_BANS_COMMAND.replace("%player%", playerName).replace("%command%", StaticConsoleFilterHelper.getConsoleFilterHelper().getUsedFunctionalBansCommand(message))));
                    break;
                case "en_US":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.FILTER_PLAYER_USER_FUNCTIONAL_BANS_COMAND.replace("%player%", playerName).replace("%command%", StaticConsoleFilterHelper.getConsoleFilterHelper().getUsedFunctionalBansCommand(message))));
                    break;
                default:
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.FILTER_PLAYER_USER_FUNCTIONAL_BANS_COMAND.replace("%player%", playerName).replace("%command%", StaticConsoleFilterHelper.getConsoleFilterHelper().getUsedFunctionalBansCommand(message))));
                    break;
            }
            return Filter.Result.DENY;
        } else if(StaticConsoleFilterHelper.getConsoleFilterHelper().isMessageToReplace(message)) {
            Bukkit.getConsoleSender().sendMessage(StaticConsoleFilterHelper.getConsoleFilterHelper().replaceConsoleMessage(message));
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
