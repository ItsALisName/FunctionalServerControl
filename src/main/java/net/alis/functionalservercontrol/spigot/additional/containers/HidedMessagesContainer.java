package net.alis.functionalservercontrol.spigot.additional.containers;

import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.libraries.org.apache.commons.lang3.StringUtils;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class HidedMessagesContainer {

    private final List<String> hidedMessages = new ArrayList<>();
    public List<String> getHidedConsoleMessages() {
        return this.hidedMessages;
    }

    private final String[] a = StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.console-logger.messages-filter"), "[", "]").split(", ");
    public void loadHidedMessages() {
        TaskManager.preformAsync(() -> {
            this.hidedMessages.clear();
            try {
                Collections.addAll(this.hidedMessages, a);
            } catch (RuntimeException ignored) {
                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&4[FunctionalServerControlSpigot | Error] Failed to load the list of messages hidden in the console ✘"));
            }
        });
    }


    public void reloadHidedMessages() {
        this.hidedMessages.clear();
        try {
            Collections.addAll(this.hidedMessages, a);
        } catch (RuntimeException ignored) {
            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&4[FunctionalServerControlSpigot | Error] Failed to reload the list of messages hidden in the console."));
        }
    }

}
