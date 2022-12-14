package by.alis.functionalbans.spigot.Expansions.ProtocolLib;

import by.alis.functionalbans.spigot.Additional.GlobalSettings.ConsoleLanguages.LangEnglish;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.ConsoleLanguages.LangRussian;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import org.bukkit.Bukkit;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;
import static org.bukkit.Bukkit.getServer;

public class ProtocolLibManager {

    ProtocolManager protocolManager;

    boolean protocolLibSetuped = false;

    private boolean isProtocolLibInstalled() {
        return getServer().getPluginManager().isPluginEnabled("ProtocolLib");
    }

    public void setupProtocolLib() {
        if(isProtocolLibInstalled()) {
            switch (getConfigSettings().getConsoleLanguageMode()) {
                case "ru_RU":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.PROTOCOL_LIB_FINDED));
                    break;
                case "en_US":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.PROTOCOL_LIB_FINDED));
                    break;
                default:
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.PROTOCOL_LIB_FINDED));
                    break;
            }
            protocolManager = ProtocolLibrary.getProtocolManager();
            if(protocolManager != null) {
                protocolLibSetuped = true;
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.PROTOCOL_LIB_HOOKED));
                        break;
                    case "en_US":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.PROTOCOL_LIB_HOOKED));
                        break;
                    default:
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.PROTOCOL_LIB_HOOKED));
                        break;
                }
            } else {
                protocolLibSetuped = false;
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.PROTOCOL_LIB_ERROR));
                        break;
                    case "en_US":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.PROTOCOL_LIB_ERROR));
                        break;
                    default:
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.PROTOCOL_LIB_ERROR));
                        break;
                }
            }
        }
    }

    public boolean isProtocolLibSetuped() {
        return protocolLibSetuped;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}
