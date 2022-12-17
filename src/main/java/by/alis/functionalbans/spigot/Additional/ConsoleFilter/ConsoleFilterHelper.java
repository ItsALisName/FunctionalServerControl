package by.alis.functionalbans.spigot.Additional.ConsoleFilter;

import by.alis.functionalbans.spigot.Additional.Containers.StaticContainers;
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
    "/temporarybanip", "/permanentbanip", "/unbanall", "/crazykick", "/ckick", "/dupeip"};

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
        if(consoleMessage == null) return "Could not identify the command";;
        return consoleMessage.split(ISSUED_COMMAND_TEXT)[1];
    }

    public void loadFunctionalBansCommands() {
        this.functionalBansCommands.clear();
        try {
            Collections.addAll(this.functionalBansCommands, commands);
            if(!getConfigSettings().isLessInformation()) {
                Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalBans | Plugin Loading] Console filtering commands have been successfully loaded into RAM (Total: %count%) ✔".replace("%count%", String.valueOf(this.functionalBansCommands.size()))));
            }
        } catch (Exception ignored) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalBans | Error] Failed to load filtering commands into RAM &4✘"));
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
