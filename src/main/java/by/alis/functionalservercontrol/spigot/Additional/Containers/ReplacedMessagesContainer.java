package by.alis.functionalservercontrol.spigot.Additional.Containers;

import by.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class ReplacedMessagesContainer {

    private final Map<String, String> replacedMessages = new HashMap<>();

    private String[] a;
    public Map<String, String> getReplacedMessages() {
        return replacedMessages;
    }

    public void loadReplacedMessages() {

        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControlSpigot.getProvidingPlugin(FunctionalServerControlSpigot.class), () -> {
            this.replacedMessages.clear();
            try {
                a = StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.console-logger.messages-replacer"), "[", "]").split(", ");
                for (String s : a) {
                    if (s != null) {
                        this.replacedMessages.put(s.split(" -> ")[0].replace(":", "").replace("=", "").replace(" ", "").replace("/", "").replace("{", "").replace("}", ""), s.split(" -> ")[1].replace("{", "").replace("}", ""));
                    }
                }
                int size = this.replacedMessages.size();
                if(!getConfigSettings().isLessInformation()){
                    Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl | Plugin loading] Message list for replacement in console successfully loaded into RAM (Total: %count%) ✔ ".replace("%count%", String.valueOf(size))));
                }
            } catch (RuntimeException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] The list of replacement messages in the console could not be loaded into RAM"));
            }
        });
    }

    public void reloadReplacedMessages() {
        this.replacedMessages.clear();
        try {
            a = StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.console-logger.messages-replacer"), "[", "]").split(", ");
            for (String s : a) {
                if (s != null) {
                    this.replacedMessages.put(s.split(" -> ")[0].replace(":", "").replace("=", "").replace(" ", "").replace("/", "").replace("{", "").replace("}", ""), s.split(" -> ")[1].replace("{", "").replace("}", ""));
                }
            }
            int size = this.replacedMessages.size();
            if(!getConfigSettings().isLessInformation()){
                Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl] The replacement message list in the console has been successfully reloaded (Total: %count%)".replace("%count%", String.valueOf(size))));
            }
        } catch (RuntimeException ignored) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl] Failed to reload the list of commands to replace in the console"));
        }
    }

}
