package by.alis.functionalbans.spigot.Additional.GlobalSettings;

import org.jetbrains.annotations.Contract;

public class StaticSettingsAccessor {

    static GeneralConfigSettings configSettings = new GeneralConfigSettings();
    static GlobalVariables globalVariables = new GlobalVariables();


    public static GeneralConfigSettings getConfigSettings() {
        return configSettings;
    }
    public static GlobalVariables getGlobalVariables() {
        return globalVariables;
    }

}
