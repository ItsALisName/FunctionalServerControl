package by.alis.functionalbans.spigot.Additional.Containers;

import by.alis.functionalbans.spigot.Additional.GlobalSettings.ConsoleLanguages.LangEnglish;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.ConsoleLanguages.LangRussian;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.Files.FileAccessor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class ReplacedMessagesContainer {

    private final Map<String, String> replacedMessages = new HashMap<>();

    private String[] a;
    public Map<String, String> getReplacedMessages() {
        return replacedMessages;
    }

    public void loadReplacedMessages() {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
            FileAccessor accessor = new FileAccessor();
            this.replacedMessages.clear();
            try {
                a = StringUtils.substringBetween(accessor.getGeneralConfig().getString("plugin-settings.console-logger.messages-replacer"), "[", "]").split(", ");
                for (String s : a) {
                    if (s != null) {
                        this.replacedMessages.put(s.split(" -> ")[0].replace(":", "").replace("=", "").replace(" ", "").replace("/", "").replace("{", "").replace("}", ""), s.split(" -> ")[1].replace("{", "").replace("}", ""));
                    }
                }
                int size = this.replacedMessages.size();
                if(!getConfigSettings().isLessInformation()){
                    switch (getConfigSettings().getConsoleLanguageMode()) {
                        case "ru_RU": {
                            Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONTAINER_REPLACED_MESSAGES_LOADED.replace("%count%", String.valueOf(size))));
                            break;
                        }
                        case "en_US": {
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_REPLACED_MESSAGES_LOADED.replace("%count%", String.valueOf(size))));
                            break;
                        }
                        default: {
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_REPLACED_MESSAGES_LOADED.replace("%count%", String.valueOf(size))));
                            break;
                        }
                    }
                }
            } catch (RuntimeException ignored) {
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONTAINER_REPLACED_MESSAGES_LOAD_ERROR));
                        break;
                    }
                    case "en_US": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_REPLACED_MESSAGES_LOAD_ERROR));
                        break;
                    }
                    default: {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_REPLACED_MESSAGES_LOAD_ERROR));
                        break;
                    }
                }
            }
        });
    }

    public void reloadReplacedMessages() {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
            FileAccessor accessor = new FileAccessor();
            this.replacedMessages.clear();
            try {
                a = StringUtils.substringBetween(accessor.getGeneralConfig().getString("plugin-settings.console-logger.messages-replacer"), "[", "]").split(", ");
                for (String s : a) {
                    if (s != null) {
                        this.replacedMessages.put(s.split(" -> ")[0].replace(":", "").replace("=", "").replace(" ", "").replace("/", "").replace("{", "").replace("}", ""), s.split(" -> ")[1].replace("{", "").replace("}", ""));
                    }
                }
                int size = this.replacedMessages.size();
                if(!getConfigSettings().isLessInformation()){
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
        });
    }

}
