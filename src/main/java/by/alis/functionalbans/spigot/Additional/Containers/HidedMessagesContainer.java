package by.alis.functionalbans.spigot.Additional.Containers;

import by.alis.functionalbans.spigot.Additional.GlobalSettings.ConsoleLanguages.LangEnglish;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.ConsoleLanguages.LangRussian;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.Files.FileAccessor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class HidedMessagesContainer {

    private final List<String> hidedMessages = new ArrayList<>();
    FileAccessor fileAccessor = new FileAccessor();
    public List<String> getHidedConsoleMessages() {
        return this.hidedMessages;
    }

    private final String[] a = StringUtils.substringBetween(this.fileAccessor.getGeneralConfig().getString("plugin-settings.console-logger.messages-filter"), "[", "]").split(", ");
    public void loadHidedMessages() {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
            this.hidedMessages.clear();
            try {
                Collections.addAll(this.hidedMessages, a);
                int size = this.hidedMessages.size();
                if(!getConfigSettings().isLessInformation()){
                    switch (getConfigSettings().getConsoleLanguageMode()) {
                        case "ru_RU":
                            Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONTAINER_HIDDEN_MESSAGES_LOADED.replace("%count%", String.valueOf(size))));
                            break;
                        case "en_US":
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_HIDDEN_MESSAGES_LOADED.replace("%count%", String.valueOf(size))));
                            break;
                        default:
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_HIDDEN_MESSAGES_LOADED.replace("%count%", String.valueOf(size))));
                            break;
                    }
                }
            } catch (RuntimeException ignored) {
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONTAINER_HIDDEN_MESSAGES_LOAD_ERROR));
                        break;
                    case "en_US":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_HIDDEN_MESSAGES_LOAD_ERROR));
                        break;
                    default:
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_HIDDEN_MESSAGES_LOAD_ERROR));
                        break;
                }
            }
        });
    }


    public void reloadHidedMessages() {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
            this.hidedMessages.clear();
            try {
                Collections.addAll(this.hidedMessages, a);
                int size = this.hidedMessages.size();
                if(!getConfigSettings().isLessInformation()){
                    switch (getConfigSettings().getConsoleLanguageMode()) {
                        case "ru_RU":
                            Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONTAINER_HIDDEN_MESSAGES_RELOADED.replace("%count%", String.valueOf(size))));
                            break;
                        case "en_US":
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_HIDDEN_MESSAGES_RELOADED.replace("%count%", String.valueOf(size))));
                            break;
                        default:
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_HIDDEN_MESSAGES_RELOADED.replace("%count%", String.valueOf(size))));
                            break;
                    }
                }
            } catch (RuntimeException ignored) {
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONTAINER_HIDDEN_MESSAGES_RELOAD_ERROR));
                        break;
                    case "en_US":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_HIDDEN_MESSAGES_RELOAD_ERROR));
                        break;
                    default:
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_HIDDEN_MESSAGES_RELOAD_ERROR));
                        break;
                }
            }
        });
    }

}
