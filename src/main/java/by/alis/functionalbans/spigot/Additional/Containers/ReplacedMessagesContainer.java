package by.alis.functionalbans.spigot.Additional.Containers;

import by.alis.functionalbans.spigot.Additional.GlobalSettings.Languages.LangEnglish;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.Languages.LangRussian;
import by.alis.functionalbans.spigot.Managers.FilesManagers.FileAccessor;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class ReplacedMessagesContainer {

    FileAccessor fileAccessor = new FileAccessor();

    private final Map<String, String> replacedMessages = new HashMap<>();

    private String[] a;
    public Map<String, String> getReplacedMessages() {
        return replacedMessages;
    }

    public void loadReplacedMessages() {
        this.replacedMessages.clear();
        try {
            a = StringUtils.substringBetween(this.fileAccessor.getGeneralConfig().getString("plugin-settings.console-logger.messages-replacer"), "[", "]").split(", ");
            for (String s : a) {
                if (s != null) {
                    this.replacedMessages.put(s.split(" -> ")[0].replace(":", "").replace("=", "").replace(" ", "").replace("/", "").replace("{", "").replace("}", ""), s.split(" -> ")[1].replace("{", "").replace("}", ""));
                }
            }
            int size = this.replacedMessages.size();
            switch (getConfigSettings().getConsoleLanguageMode()) {
                case "ru_RU":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONTAINER_REPLACED_MESSAGES_LOADED.replace("%count%", String.valueOf(size))));
                    break;
                case "en_US":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_REPLACED_MESSAGES_LOADED.replace("%count%", String.valueOf(size))));
                    break;
                default:
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_REPLACED_MESSAGES_LOADED.replace("%count%", String.valueOf(size))));
                    break;
            }
        } catch (RuntimeException ignored) {
            switch (getConfigSettings().getConsoleLanguageMode()) {
                case "ru_RU":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONTAINER_REPLACED_MESSAGES_LOAD_ERROR));
                    break;
                case "en_US":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_REPLACED_MESSAGES_LOAD_ERROR));
                    break;
                default:
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_REPLACED_MESSAGES_LOAD_ERROR));
                    break;
            }
        }
    }

    public void reloadReplacedMessages() {
        this.replacedMessages.clear();
        try {
            a = StringUtils.substringBetween(this.fileAccessor.getGeneralConfig().getString("plugin-settings.console-logger.messages-replacer"), "[", "]").split(", ");
            for (String s : a) {
                if (s != null) {
                    this.replacedMessages.put(s.split(" -> ")[0].replace(":", "").replace("=", "").replace(" ", "").replace("/", "").replace("{", "").replace("}", ""), s.split(" -> ")[1].replace("{", "").replace("}", ""));
                }
            }
            int size = this.replacedMessages.size();
            switch (getConfigSettings().getConsoleLanguageMode()) {
                case "ru_RU":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONTAINER_REPLACED_MESSAGES_RELOADED.replace("%count%", String.valueOf(size))));
                    break;
                case "en_US":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_REPLACED_MESSAGES_RELOADED.replace("%count%", String.valueOf(size))));
                    break;
                default:
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_REPLACED_MESSAGES_RELOADED.replace("%count%", String.valueOf(size))));
                    break;
            }
        } catch (RuntimeException ignored) {
            switch (getConfigSettings().getConsoleLanguageMode()) {
                case "ru_RU":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONTAINER_REPLACED_MESSAGES_RELOAD_ERROR));
                    break;
                case "en_US":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_REPLACED_MESSAGES_RELOAD_ERROR));
                    break;
                default:
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_REPLACED_MESSAGES_RELOAD_ERROR));
                    break;
            }
        }
    }

}
