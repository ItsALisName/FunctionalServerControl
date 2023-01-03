package by.alis.functionalservercontrol.spigot.Additional.GlobalSettings;

public class StaticSettingsAccessor {

    private static final GeneralConfigSettings configSettings = new GeneralConfigSettings();
    private final static GlobalVariables globalVariables = new GlobalVariables();
    private final static Language language = new Language();
    private final static CommandLimiterSettings commandLimiterSettings = new CommandLimiterSettings();


    public static GeneralConfigSettings getConfigSettings() {
        return configSettings;
    }
    public static GlobalVariables getGlobalVariables() {
        return globalVariables;
    }
    public static Language getLanguage() {
        return language;
    }
    public static CommandLimiterSettings getCommandLimiterSettings() {
        return commandLimiterSettings;
    }
}
