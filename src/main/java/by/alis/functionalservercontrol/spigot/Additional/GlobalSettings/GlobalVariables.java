package by.alis.functionalservercontrol.spigot.Additional.GlobalSettings;

import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

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
    private String VAR_IP;
    private String VAR_ID;
    private String VAR_NAME;
    private String VAR_UUID;
    private String VAR_STATUS_BANNED;
    private String VAR_STATUS_MUTED;

    private String BUTTON_UNBAN;
    private String BUTTON_UNMUTE;


    public String getVariableId() {
        return VAR_ID;
    }
    public String getVariableIp() {
        return VAR_IP;
    }
    public String getVariableName() {
        return VAR_NAME;
    }
    public String getVariableUUID() {
        return VAR_UUID;
    }
    public String getVariableStatusBanned() {
        return VAR_STATUS_BANNED;
    }
    public String getVariableStatusMuted() {
        return VAR_STATUS_MUTED;
    }

    public String getButtonUnban() {
        return BUTTON_UNBAN;
    }
    public String getButtonUnmute() {
        return BUTTON_UNMUTE;
    }
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
            try {
                BUTTON_UNBAN = getFileAccessor().getGeneralConfig().getString("global-variables.buttons.unban");
                BUTTON_UNMUTE = getFileAccessor().getGeneralConfig().getString("global-variables.buttons.unmute");
                VAR_STATUS_BANNED = getFileAccessor().getGeneralConfig().getString("global-variables.banned");
                VAR_STATUS_MUTED = getFileAccessor().getGeneralConfig().getString("global-variables.muted");
                VAR_NAME = getFileAccessor().getGeneralConfig().getString("global-variables.name");
                VAR_UUID = getFileAccessor().getGeneralConfig().getString("global-variables.uuid");
                VAR_ID = getFileAccessor().getGeneralConfig().getString("global-variables.id");
                VAR_IP = getFileAccessor().getGeneralConfig().getString("global-variables.ip");
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
                Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl] Global variables successfully reloaded"));
            } catch (ExceptionInInitializerError ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to reload global variables"));
            }
        return;
    }

    public void loadGlobalVariables() {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
            try {
                BUTTON_UNBAN = getFileAccessor().getGeneralConfig().getString("global-variables.buttons.unban");
                BUTTON_UNMUTE = getFileAccessor().getGeneralConfig().getString("global-variables.buttons.unmute");
                VAR_STATUS_BANNED = getFileAccessor().getGeneralConfig().getString("global-variables.banned");
                VAR_STATUS_MUTED = getFileAccessor().getGeneralConfig().getString("global-variables.muted");
                VAR_ID = getFileAccessor().getGeneralConfig().getString("global-variables.id");
                VAR_IP = getFileAccessor().getGeneralConfig().getString("global-variables.ip");
                VAR_NAME = getFileAccessor().getGeneralConfig().getString("global-variables.name");
                VAR_UUID = getFileAccessor().getGeneralConfig().getString("global-variables.uuid");
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
                    Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl] Global variables successfully reloaded"));
                }
            } catch (ExceptionInInitializerError ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to reload global variables"));
            }
        });
    }

}
