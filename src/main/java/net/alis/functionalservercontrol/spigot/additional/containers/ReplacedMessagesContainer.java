package net.alis.functionalservercontrol.spigot.additional.containers;

import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.libraries.org.apache.commons.lang3.StringUtils;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class ReplacedMessagesContainer {

    private final Map<String, String> replacedMessages = new HashMap<>();

    private String[] a;
    public Map<String, String> getReplacedMessages() {
        return replacedMessages;
    }

    public void loadReplacedMessages() {
        TaskManager.preformAsync(() -> {
            this.replacedMessages.clear();
            try {
                a = StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.console-logger.messages-replacer"), "[", "]").split(", ");
                for (String s : a) {
                    if (s != null) {
                        this.replacedMessages.put(TextUtils.stringToMonolith(s.split(" -> ")[0]).replace(":", "").replace("=", "").replace("/", "").replace("{", "").replace("}", ""), s.split(" -> ")[1].replace("{", "").replace("}", ""));
                    }
                }
            } catch (RuntimeException ignored) {
                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&4[FunctionalServerControlSpigot | Error] The list of replacement messages in the console could not be loaded into RAM"));
            }
        });
    }

    public void reloadReplacedMessages() {
        this.replacedMessages.clear();
        try {
            a = StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.console-logger.messages-replacer"), "[", "]").split(", ");
            for (String s : a) {
                if (s != null) {
                    this.replacedMessages.put(TextUtils.stringToMonolith(s.split(" -> ")[0]).replace(":", "").replace("=", "").replace("/", "").replace("{", "").replace("}", ""), s.split(" -> ")[1].replace("{", "").replace("}", ""));
                }
            }
        } catch (RuntimeException exception) {
            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&4[FunctionalServerControlSpigot] Failed to reload the list of commands to replace in the console"));
            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&4" + exception.fillInStackTrace().getMessage()));
            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&4[FunctionalServerControlSpigot] Failed to reload the list of commands to replace in the console"));
        }
    }

}
