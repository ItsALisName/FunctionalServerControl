package by.alis.functionalbans.spigot.Additional.GlobalSettings;

import by.alis.functionalbans.spigot.Additional.GlobalSettings.Languages.LangEnglish;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.Languages.LangRussian;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.FilesManagers.FileAccessor;
import org.bukkit.Bukkit;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class GlobalVariables {

    private String[] VAR_TIME_SECONDS;
    private String[] VAR_TIME_MINUTES;
    private String[] VAR_TIME_HOURS;
    private String[] VAR_TIME_DAYS;
    private String[] VAR_TIME_MONTHS;
    private String[] VAR_TIME_YEARS;

    private String VAR_UNKNOWN_TIME;
    private String VAR_ALL;
    private String VAR_DEFAULT_REASON;

    private String VAR_TIME_NEVER;

    private String VAR_REPLACED_CONSOLE_NAME;

    public String getConsoleVariableName() { return VAR_REPLACED_CONSOLE_NAME; }
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

    public String getVarUnknownTime() { return VAR_UNKNOWN_TIME; }

    public String getVariableNever() {
        return VAR_TIME_NEVER;
    }

    public String getDefaultReason() { return VAR_DEFAULT_REASON; }
    public String getVariableAll() {
        return VAR_ALL;
    }

    public void reloadGlobalVariables() {
        FileAccessor accessor = new FileAccessor();
            try {
                VAR_REPLACED_CONSOLE_NAME = accessor.getGeneralConfig().getString("global-variables.console-name");
                VAR_TIME_NEVER = accessor.getGeneralConfig().getString("global-variables.never");
                VAR_TIME_YEARS = accessor.getGeneralConfig().getString("global-variables.years").split("\\|");
                VAR_TIME_MONTHS = accessor.getGeneralConfig().getString("global-variables.months").split("\\|");
                VAR_TIME_HOURS = accessor.getGeneralConfig().getString("global-variables.hours").split("\\|");
                VAR_TIME_DAYS = accessor.getGeneralConfig().getString("global-variables.days").split("\\|");
                VAR_TIME_MINUTES = accessor.getGeneralConfig().getString("global-variables.minutes").split("\\|");
                VAR_TIME_SECONDS = accessor.getGeneralConfig().getString("global-variables.seconds").split("\\|");
                VAR_UNKNOWN_TIME = accessor.getGeneralConfig().getString("global-variables.unknown-time");
                VAR_DEFAULT_REASON = accessor.getGeneralConfig().getString("plugin-settings.reason-settings.default-reason");
                VAR_ALL = accessor.getGeneralConfig().getString("global-variables.all");
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONFIG_VARIABLES_RELOADED));
                        break;
                    case "en_US":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_VARIABLES_RELOADED));
                        break;
                    default:
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_VARIABLES_RELOADED));
                        break;
                }
            } catch (ExceptionInInitializerError ignored) {
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONFIG_VARIABLES_RELOAD_ERROR));
                        break;
                    case "en_US":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_VARIABLES_RELOAD_ERROR));
                        break;
                    default:
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_VARIABLES_RELOAD_ERROR));
                        break;
                }
            }
        return;
    }

    public void loadGlobalVariables() {
        FileAccessor accessor = new FileAccessor();
        try {
            VAR_REPLACED_CONSOLE_NAME = accessor.getGeneralConfig().getString("global-variables.console-name");
            VAR_TIME_NEVER = accessor.getGeneralConfig().getString("global-variables.never");
            VAR_TIME_YEARS = accessor.getGeneralConfig().getString("global-variables.years").split("\\|");
            VAR_TIME_MONTHS = accessor.getGeneralConfig().getString("global-variables.months").split("\\|");
            VAR_TIME_HOURS = accessor.getGeneralConfig().getString("global-variables.hours").split("\\|");
            VAR_TIME_DAYS = accessor.getGeneralConfig().getString("global-variables.days").split("\\|");
            VAR_TIME_MINUTES = accessor.getGeneralConfig().getString("global-variables.minutes").split("\\|");
            VAR_TIME_SECONDS = accessor.getGeneralConfig().getString("global-variables.seconds").split("\\|");
            VAR_UNKNOWN_TIME = accessor.getGeneralConfig().getString("global-variables.unknown-time");
            VAR_DEFAULT_REASON = accessor.getGeneralConfig().getString("plugin-settings.reason-settings.default-reason");
            VAR_ALL = accessor.getGeneralConfig().getString("global-variables.all");
            if(!getConfigSettings().isLessInformation()) {
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONFIG_VARIABLES_RELOADED));
                        break;
                    case "en_US":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_VARIABLES_RELOADED));
                        break;
                    default:
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_VARIABLES_RELOADED));
                        break;
                }
            }
        } catch (ExceptionInInitializerError ignored) {
            switch (getConfigSettings().getConsoleLanguageMode()) {
                case "ru_RU":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONFIG_VARIABLES_RELOAD_ERROR));
                    break;
                case "en_US":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_VARIABLES_RELOAD_ERROR));
                    break;
                default:
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_VARIABLES_RELOAD_ERROR));
                    break;
            }
        }
        return;
    }

}
