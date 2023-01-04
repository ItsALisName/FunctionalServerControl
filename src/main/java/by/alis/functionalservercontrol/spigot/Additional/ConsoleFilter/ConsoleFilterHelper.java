package by.alis.functionalservercontrol.spigot.Additional.ConsoleFilter;

import by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers;
import org.bukkit.Bukkit;

import java.util.*;

import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getReplacedMessagesContainer;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;

public class ConsoleFilterHelper {

    private final List<String> functionalServerControlCommands = new ArrayList<>(Arrays.asList("/banip", "/temporarybanip", "/tempbanip", "/ccheck", "/cheatcheck", "/dupeip", "/ckick", "/crazykick", "/unbanall", "/unban", "/kickall", "/temporaryban", "/tempban", "/ban",
            "/fsc", "/fscontrol", "/functionalservercontrol", "/kick", "/mute", "/tempmute", "/muteip", "/tempmuteip", "/unmute", "/temporarymute", "/temporarymuteip", "/unmuteall", "/getver", "/gv", "/getversion",
            "/getclient", "/gc", "/getc", "/getinfo", "/gi", "/getinformation"));
    private final String ISSUED_COMMAND_TEXT = "issued server command: ";

    public boolean isFunctionalServerControlCommand(String consoleMessage) {
        if(!this.functionalServerControlCommands.isEmpty()) {
            for (String s : this.functionalServerControlCommands) {
                if (consoleMessage.contains(" " + ISSUED_COMMAND_TEXT + s)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> getPluginCommands() {
        return this.functionalServerControlCommands;
    }

    protected boolean isHidedMessage(String consoleMessage) {
        if(StaticContainers.getHidedMessagesContainer().getHidedConsoleMessages().isEmpty()) return false;
        for(String hMsg : StaticContainers.getHidedMessagesContainer().getHidedConsoleMessages()) {
            if(consoleMessage.contains(hMsg)) return true;
        }
        return false;
    }

    public String getUsedFunctionalServerControlCommand(String consoleMessage) {
        if(consoleMessage == null) return "Could not identify the command";;
        return consoleMessage.split(ISSUED_COMMAND_TEXT)[1];
    }

    public void loadFunctionalServerControlCommands() {
        this.functionalServerControlCommands.clear();
        try {
            if(!getConfigSettings().isLessInformation()) {
                Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl | Plugin Loading] Console filtering commands have been successfully loaded into RAM (Total: %count%) ✔".replace("%count%", String.valueOf(this.functionalServerControlCommands.size()))));
            }
        } catch (Exception ignored) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to load filtering commands into RAM &4✘"));
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
