package by.alis.functionalservercontrol.spigot.additional.containers;

import by.alis.functionalservercontrol.spigot.additional.libraries.org.apache.commons.lang3.StringUtils;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class HidedMessagesContainer {

    private final List<String> hidedMessages = new ArrayList<>();
    public List<String> getHidedConsoleMessages() {
        return this.hidedMessages;
    }

    private final String[] a = StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.console-logger.messages-filter"), "[", "]").split(", ");
    public void loadHidedMessages() {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
            this.hidedMessages.clear();
            try {
                Collections.addAll(this.hidedMessages, a);
            } catch (RuntimeException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to load the list of messages hidden in the console ✘"));
            }
        });
    }


    public void reloadHidedMessages() {
        this.hidedMessages.clear();
        try {
            Collections.addAll(this.hidedMessages, a);
        } catch (RuntimeException ignored) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to reload the list of messages hidden in the console."));
        }
    }

}
