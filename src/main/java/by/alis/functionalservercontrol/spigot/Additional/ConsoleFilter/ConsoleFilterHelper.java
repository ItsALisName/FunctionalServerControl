package by.alis.functionalservercontrol.spigot.Additional.ConsoleFilter;

import by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers;
import by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils;

import java.util.*;

import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getReplacedMessagesContainer;

public class ConsoleFilterHelper {

    private final List<String> functionalServerControlCommands = new ArrayList<>(Arrays.asList("/banip", "/temporarybanip", "/tempbanip", "/ccheck", "/cheatcheck", "/dupeip", "/ckick", "/crazykick", "/unbanall", "/unban", "/kickall", "/temporaryban", "/tempban", "/ban",
            "/fsc", "/fscontrol", "/functionalservercontrol", "/kick", "/mute", "/tempmute", "/muteip", "/tempmuteip", "/unmute", "/temporarymute", "/temporarymuteip", "/unmuteall", "/getver", "/gv", "/getversion",
            "/getclient", "/gc", "/getc", "/getinfo", "/gi", "/getinformation", "/banlist", "/banslist", "/mutelist", "/muteslist", "/cleanchat", "/clearchat"));
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
        return consoleMessage == null ? "Could not identify the command" : consoleMessage.split(ISSUED_COMMAND_TEXT)[1];
    }

    public boolean isMessageToReplace(String consoleMessage) {
        for(Map.Entry<String, String> e : getReplacedMessagesContainer().getReplacedMessages().entrySet()) {
            if(e.getKey() != null) {
                if(TextUtils.stringToMonolith(consoleMessage).replace(":", "").replace("=", "").replace("/", "").replace("{", "").replace("}", "").equals(e.getKey())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String replaceConsoleMessage(String consoleMessage) {
        return getReplacedMessagesContainer().getReplacedMessages().get(TextUtils.stringToMonolith(consoleMessage).replace(":", "").replace("=", "").replace("/", "").replace("{", "").replace("}", ""));
    }

}
