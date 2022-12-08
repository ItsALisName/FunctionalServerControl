package by.alis.functionalbans.spigot.Additional.GlobalSettings;

public class StaticSettingsAccessor {

    private static final GeneralConfigSettings configSettings = new GeneralConfigSettings();
    private static final GlobalVariables globalVariables = new GlobalVariables();

    public static GeneralConfigSettings getConfigSettings() {
        return configSettings;
    }

    public static GlobalVariables getGlobalVariables() {
        return globalVariables;
    }

}
