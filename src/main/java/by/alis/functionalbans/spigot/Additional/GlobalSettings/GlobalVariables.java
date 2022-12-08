package by.alis.functionalbans.spigot.Additional.GlobalSettings;

import by.alis.functionalbans.spigot.Additional.GlobalSettings.Languages.LangEnglish;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.Languages.LangRussian;
import by.alis.functionalbans.spigot.Managers.FilesManagers.FileAccessor;
import org.bukkit.Bukkit;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class GlobalVariables {

    FileAccessor accessor = new FileAccessor();

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

    public String getConsoleVariableName() { return this.VAR_REPLACED_CONSOLE_NAME; }
    public String[] getMultiVariableSecond() {
        return this.VAR_TIME_SECONDS;
    }

    public String[] getMultiVariableMinutes() {
        return this.VAR_TIME_MINUTES;
    }

    public String[] getMultiVariableHours() {
        return this.VAR_TIME_HOURS;
    }

    public String[] getMultiVariableMonths() {
        return this.VAR_TIME_MONTHS;
    }

    public String[] getMultiVariableYears() {
        return this.VAR_TIME_YEARS;
    }

    public String[] getMultiVariableDays() {
        return this.VAR_TIME_DAYS;
    }

    public String getVarUnknownTime() { return this.VAR_UNKNOWN_TIME; }

    public String getVariableNever() {
        return this.VAR_TIME_NEVER;
    }

    public String getDefaultReason() { return VAR_DEFAULT_REASON; }
    public String getVariableAll() {
        return VAR_ALL;
    }

    public void reloadGlobalVariables() {
        try {
            this.VAR_REPLACED_CONSOLE_NAME = this.accessor.getGeneralConfig().getString("global-variables.console");
            this.VAR_TIME_NEVER = this.accessor.getGeneralConfig().getString("global-variables.never");
            this.VAR_TIME_YEARS = this.accessor.getGeneralConfig().getString("global-variables.years").split("\\|");
            this.VAR_TIME_MONTHS = this.accessor.getGeneralConfig().getString("global-variables.months").split("\\|");
            this.VAR_TIME_HOURS = this.accessor.getGeneralConfig().getString("global-variables.hours").split("\\|");
            this.VAR_TIME_DAYS = this.accessor.getGeneralConfig().getString("global-variables.days").split("\\|");
            this.VAR_TIME_MINUTES = this.accessor.getGeneralConfig().getString("global-variables.minutes").split("\\|");
            this.VAR_TIME_SECONDS = this.accessor.getGeneralConfig().getString("global-variables.seconds").split("\\|");
            this.VAR_UNKNOWN_TIME = this.accessor.getGeneralConfig().getString("global-variables.unknown-time");
            this.VAR_DEFAULT_REASON = this.accessor.getGeneralConfig().getString("plugin-settings.reason-settings.default-reason");
            this.VAR_ALL = this.accessor.getGeneralConfig().getString("global-variables.all");
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
    }

    public void loadGlobalVariables() {
        try {
            this.VAR_REPLACED_CONSOLE_NAME = this.accessor.getGeneralConfig().getString("global-variables.console");
            this.VAR_TIME_NEVER = this.accessor.getGeneralConfig().getString("global-variables.never");
            this.VAR_TIME_YEARS = this.accessor.getGeneralConfig().getString("global-variables.years").split("\\|");
            this.VAR_TIME_MONTHS = this.accessor.getGeneralConfig().getString("global-variables.months").split("\\|");
            this.VAR_TIME_HOURS = this.accessor.getGeneralConfig().getString("global-variables.hours").split("\\|");
            this.VAR_TIME_DAYS = this.accessor.getGeneralConfig().getString("global-variables.days").split("\\|");
            this.VAR_TIME_MINUTES = this.accessor.getGeneralConfig().getString("global-variables.minutes").split("\\|");
            this.VAR_TIME_SECONDS = this.accessor.getGeneralConfig().getString("global-variables.seconds").split("\\|");
            this.VAR_UNKNOWN_TIME = this.accessor.getGeneralConfig().getString("global-variables.unknown-time");
            this.VAR_DEFAULT_REASON = this.accessor.getGeneralConfig().getString("plugin-settings.reason-settings.default-reason");
            this.VAR_ALL = this.accessor.getGeneralConfig().getString("global-variables.all");
            if(getConfigSettings().isLessInformation()) {
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
    }

}
