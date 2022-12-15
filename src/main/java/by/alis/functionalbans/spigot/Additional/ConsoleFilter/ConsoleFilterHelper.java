package by.alis.functionalbans.spigot.Additional.ConsoleFilter;

import by.alis.functionalbans.spigot.Additional.Containers.StaticContainers;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.ConsoleLanguages.LangEnglish;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.ConsoleLanguages.LangRussian;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static by.alis.functionalbans.spigot.Additional.Containers.StaticContainers.getReplacedMessagesContainer;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class ConsoleFilterHelper {

    private final List<String> functionalBansCommands = new ArrayList<>();
    private static final String ISSUED_COMMAND_TEXT = "issued server command: ";
    private static final String[] commands = {"/ban", "/tempban", "/kick", "/kickall", "/unban", "/mute", "/unmute", "/check", "/fb", "/functionalbans", "/fbans", "/funcbans", "/temporaryban", "/crazykick", "/permanentban", "/banip", "/tempbanip",
    "/temporarybanip", "/permanentbanip", "/unbanall", "/crazykick", "/ckick"};

    protected boolean isFunctionalBansCommand(String consoleMessage) {
        if(!this.functionalBansCommands.isEmpty()) {
            for (String s : this.functionalBansCommands) {
                if (consoleMessage.contains(" " + ISSUED_COMMAND_TEXT + s)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean isHidedMessage(String consoleMessage) {
        if(StaticContainers.getHidedMessagesContainer().getHidedConsoleMessages().isEmpty()) return false;
        for(String hMsg : StaticContainers.getHidedMessagesContainer().getHidedConsoleMessages()) {
            if(consoleMessage.contains(hMsg)) return true;
        }
        return false;
    }

    protected String getUsedFunctionalBansCommand(String consoleMessage) {
        if(consoleMessage == null) return StaticSettingsAccessor.getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU") ? LangRussian.FILTERED_COMMAND_ERROR : LangEnglish.FILTERED_COMMAND_ERROR;
        return consoleMessage.split(ISSUED_COMMAND_TEXT)[1];
    }

    public void loadFunctionalBansCommands() {
        this.functionalBansCommands.clear();
        try {
            Collections.addAll(this.functionalBansCommands, commands);
            if(getConfigSettings().isLessInformation()) {
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONTAINER_FILTERED_COMMANDS_LOADED.replace("%count%", String.valueOf(this.functionalBansCommands.size()))));
                        break;
                    case "en_US":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_FILTERED_COMMANDS_LOADED.replace("%count%", String.valueOf(this.functionalBansCommands.size()))));
                        break;
                    default:
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_FILTERED_COMMANDS_LOADED.replace("%count%", String.valueOf(this.functionalBansCommands.size()))));
                        break;
                }
            }
        } catch (Exception ignored) {
            switch (getConfigSettings().getConsoleLanguageMode()) {
                case "ru_RU":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONTAINER_FILTERED_COMMANDS_LOAD_ERROR));
                    break;
                case "en_US":
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_FILTERED_COMMANDS_LOAD_ERROR));
                    break;
                default:
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONTAINER_FILTERED_COMMANDS_LOAD_ERROR));
                    break;
            }
        }
    }

    public boolean isMessageToReplace(String consoleMessage) {
        for(Map.Entry<String, String> e : getReplacedMessagesContainer().getReplacedMessages().entrySet()) {
            if(e.getKey() != null) {
                if(consoleMessage.replace(":", "").replace("=", "").replace(" ", "").replace("/", "").replace("{", "").replace("}", "").equals(e.getKey())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String replaceConsoleMessage(String consoleMessage) {
        return getReplacedMessagesContainer().getReplacedMessages().get(consoleMessage.replace(":", "").replace("=", "").replace(" ", "").replace("/", "").replace("{", "").replace("}", ""));
    }

}
