package by.alis.functionalservercontrol.spigot.Additional.GlobalSettings;

public class StaticSettingsAccessor {

    static GeneralConfigSettings configSettings = new GeneralConfigSettings();
    static GlobalVariables globalVariables = new GlobalVariables();
    static Language language = new Language();


    public static GeneralConfigSettings getConfigSettings() {
        return configSettings;
    }
    public static GlobalVariables getGlobalVariables() {
        return globalVariables;
    }
    public static Language getLanguage() {
        return language;
    }
}
