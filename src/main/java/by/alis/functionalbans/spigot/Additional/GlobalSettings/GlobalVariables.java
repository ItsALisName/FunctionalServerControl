package by.alis.functionalbans.spigot.Additional.GlobalSettings;

import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import org.bukkit.Bukkit;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalbans.spigot.Managers.Files.SFAccessor.getFileAccessor;

/**
 * The class responsible for the global variables of the plugin, their loading, reloading and getting
 */
public class GlobalVariables {

    private String[] VAR_TIME_SECONDS;
    private String[] VAR_TIME_MINUTES;
    private String[] VAR_TIME_HOURS;
    private String[] VAR_TIME_DAYS;
    private String[] VAR_TIME_MONTHS;
    private String[] VAR_TIME_YEARS;

    private String VAR_UNBANNED;
    private String VAR_UNMUTED;

    private String VAR_UNKNOWN_TIME;
    private String VAR_ALL;
    private String VAR_DEFAULT_REASON;

    private String VAR_TIME_NEVER;

    private String VAR_REPLACED_CONSOLE_NAME;

    public String getConsoleVariableName() {
        return VAR_REPLACED_CONSOLE_NAME;
    }
    public String[] getMultiVariableSecond() {
        return VAR_TIME_SECONDS;
    }

    public String[] getMultiVariableMinutes() {
        return VAR_TIME_MINUTES;
    }

    public String[] getMultiVariableHours() {
        return VAR_TIME_HOURS;
    }

    public String[] getMultiVariableMonths() {
        return VAR_TIME_MONTHS;
    }

    public String[] getMultiVariableYears() {
        return VAR_TIME_YEARS;
    }

    public String[] getMultiVariableDays() {
        return VAR_TIME_DAYS;
    }

    public String getVarUnknownTime() {
        return VAR_UNKNOWN_TIME;
    }

    public String getVariableNever() {
        return VAR_TIME_NEVER;
    }

    public String getDefaultReason() {
        return VAR_DEFAULT_REASON;
    }
    public String getVariableAll() {
        return VAR_ALL;
    }

    public String getVarUnbanned() {
        return VAR_UNBANNED;
    }

    public String getVarUnmuted() {
        return VAR_UNMUTED;
    }

    public void reloadGlobalVariables() {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
            try {
                VAR_REPLACED_CONSOLE_NAME = getFileAccessor().getGeneralConfig().getString("global-variables.console-name");
                VAR_TIME_NEVER = getFileAccessor().getGeneralConfig().getString("global-variables.never");
                VAR_TIME_YEARS = getFileAccessor().getGeneralConfig().getString("global-variables.years").split("\\|");
                VAR_TIME_MONTHS = getFileAccessor().getGeneralConfig().getString("global-variables.months").split("\\|");
                VAR_TIME_HOURS = getFileAccessor().getGeneralConfig().getString("global-variables.hours").split("\\|");
                VAR_TIME_DAYS = getFileAccessor().getGeneralConfig().getString("global-variables.days").split("\\|");
                VAR_TIME_MINUTES = getFileAccessor().getGeneralConfig().getString("global-variables.minutes").split("\\|");
                VAR_TIME_SECONDS = getFileAccessor().getGeneralConfig().getString("global-variables.seconds").split("\\|");
                VAR_UNKNOWN_TIME = getFileAccessor().getGeneralConfig().getString("global-variables.unknown-time");
                VAR_DEFAULT_REASON = getFileAccessor().getGeneralConfig().getString("plugin-settings.reason-settings.default-reason");
                VAR_ALL = getFileAccessor().getGeneralConfig().getString("global-variables.all");
                VAR_UNBANNED = getFileAccessor().getGeneralConfig().getString("global-variables.unbanned");
                VAR_UNMUTED = getFileAccessor().getGeneralConfig().getString("global-variables.unmuted");
                Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalBans] Global variables successfully reloaded"));
            } catch (ExceptionInInitializerError ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalBans | Error] Failed to reload global variables"));
            }
        });
        return;
    }

    public void loadGlobalVariables() {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
            try {
                VAR_REPLACED_CONSOLE_NAME = getFileAccessor().getGeneralConfig().getString("global-variables.console-name");
                VAR_TIME_NEVER = getFileAccessor().getGeneralConfig().getString("global-variables.never");
                VAR_TIME_YEARS = getFileAccessor().getGeneralConfig().getString("global-variables.years").split("\\|");
                VAR_TIME_MONTHS = getFileAccessor().getGeneralConfig().getString("global-variables.months").split("\\|");
                VAR_TIME_HOURS = getFileAccessor().getGeneralConfig().getString("global-variables.hours").split("\\|");
                VAR_TIME_DAYS = getFileAccessor().getGeneralConfig().getString("global-variables.days").split("\\|");
                VAR_TIME_MINUTES = getFileAccessor().getGeneralConfig().getString("global-variables.minutes").split("\\|");
                VAR_TIME_SECONDS = getFileAccessor().getGeneralConfig().getString("global-variables.seconds").split("\\|");
                VAR_UNKNOWN_TIME = getFileAccessor().getGeneralConfig().getString("global-variables.unknown-time");
                VAR_DEFAULT_REASON = getFileAccessor().getGeneralConfig().getString("plugin-settings.reason-settings.default-reason");
                VAR_ALL = getFileAccessor().getGeneralConfig().getString("global-variables.all");
                VAR_UNBANNED = getFileAccessor().getGeneralConfig().getString("global-variables.unbanned");
                VAR_UNMUTED = getFileAccessor().getGeneralConfig().getString("global-variables.unmuted");
                if(!getConfigSettings().isLessInformation()) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalBans] Global variables successfully reloaded"));
                }
            } catch (ExceptionInInitializerError ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalBans | Error] Failed to reload global variables"));
            }
        });
    }

}
