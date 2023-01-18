package net.alis.functionalservercontrol.spigot.additional.globalsettings;

import org.jetbrains.annotations.Contract;

public class SettingsAccessor {

    private final static GeneralConfigSettings configSettings = new GeneralConfigSettings();
    private final static GlobalVariables globalVariables = new GlobalVariables();
    private final static Language language = new Language();
    private final static CommandLimiterSettings commandLimiterSettings = new CommandLimiterSettings();
    private final static ChatSettings chatSettings = new ChatSettings();
    private final static ProtectionSettings protectionSettings = new ProtectionSettings();

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
    @Contract(pure = true)
    public static ProtectionSettings getProtectionSettings() {
        return protectionSettings;
    }
}
