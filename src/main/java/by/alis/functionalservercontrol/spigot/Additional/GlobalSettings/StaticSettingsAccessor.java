package by.alis.functionalservercontrol.spigot.Additional.GlobalSettings;

import org.jetbrains.annotations.Contract;

public class StaticSettingsAccessor {

    private final static GeneralConfigSettings configSettings = new GeneralConfigSettings();
    private final static GlobalVariables globalVariables = new GlobalVariables();
    private final static Language language = new Language();
    private final static CommandLimiterSettings commandLimiterSettings = new CommandLimiterSettings();
    private final static ChatSettings chatSettings = new ChatSettings();

    @Contract(pure = true)
    public static GeneralConfigSettings getConfigSettings() {
        return configSettings;
    }

    @Contract(pure = true)
    public static GlobalVariables getGlobalVariables() {
        return globalVariables;
    }

    @Contract(pure = true)
    public static Language getLanguage() {
        return language;
    }

    @Contract(pure = true)
    public static CommandLimiterSettings getCommandLimiterSettings() {
        return commandLimiterSettings;
    }

    @Contract(pure = true)
    public static ChatSettings getChatSettings() {
        return chatSettings;
    }
}
