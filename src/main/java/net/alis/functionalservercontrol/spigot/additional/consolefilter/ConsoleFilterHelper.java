package net.alis.functionalservercontrol.spigot.additional.consolefilter;

import net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import org.apache.logging.log4j.core.Filter;

import java.util.*;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getReplacedMessagesContainer;

public class ConsoleFilterHelper {

    private final List<String> functionalServerControlCommands = new ArrayList<>(Arrays.asList("/banip", "/temporarybanip", "/tempbanip", "/ccheck", "/cheatcheck", "/dupeip", "/ckick", "/crazykick", "/unbanall", "/unban", "/kickall", "/temporaryban", "/tempban", "/ban",
            "/fsc", "/fscontrol", "/functionalservercontrol", "/kick", "/mute", "/tempmute", "/muteip", "/tempmuteip", "/unmute", "/temporarymute", "/temporarymuteip", "/unmuteall", "/getver", "/gv", "/getversion",
            "/getclient", "/gc", "/getc", "/getinfo", "/gi", "/getinformation", "/banlist", "/banslist", "/mutelist", "/muteslist", "/cleanchat", "/clearchat", "/deviceinfo", "/dinfo"));
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

    protected Filter.Result isHidedMessage(String consoleMessage) {
        if(StaticContainers.getHidedMessagesContainer().getHidedConsoleMessages().isEmpty()) return Filter.Result.NEUTRAL;
        for(String hMsg : StaticContainers.getHidedMessagesContainer().getHidedConsoleMessages()) {
            if(consoleMessage.contains(hMsg)) return Filter.Result.DENY;
        }
        return Filter.Result.NEUTRAL;
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

    private static final ConsoleFilterHelper consoleFilterHelper = new ConsoleFilterHelper();
    public static ConsoleFilterHelper getConsoleFilterHelper() {
        return consoleFilterHelper;
    }

}
