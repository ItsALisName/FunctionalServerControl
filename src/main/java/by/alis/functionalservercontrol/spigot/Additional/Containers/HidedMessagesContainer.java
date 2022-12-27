package by.alis.functionalservercontrol.spigot.Additional.Containers;

import by.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class HidedMessagesContainer {

    private final List<String> hidedMessages = new ArrayList<>();
    public List<String> getHidedConsoleMessages() {
        return this.hidedMessages;
    }

    private final String[] a = StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.console-logger.messages-filter"), "[", "]").split(", ");
    public void loadHidedMessages() {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControlSpigot.getProvidingPlugin(FunctionalServerControlSpigot.class), () -> {
            this.hidedMessages.clear();
            try {
                Collections.addAll(this.hidedMessages, a);
                int size = this.hidedMessages.size();
                if(!getConfigSettings().isLessInformation()){
                    Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl | Plugin loading] The list of messages hidden in the console has been successfully loaded into RAM (Total: %count%) ✔".replace("%count%", String.valueOf(size))));
                }
            } catch (RuntimeException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to load the list of messages hidden in the console ✘"));
            }
        });
    }


    public void reloadHidedMessages() {
        this.hidedMessages.clear();
        try {
            Collections.addAll(this.hidedMessages, a);
            int size = this.hidedMessages.size();
            if(!getConfigSettings().isLessInformation()){
                Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl | Plugin loading] The list of messages hidden in the console has been successfully reloaded (Total: %count%)".replace("%count%", String.valueOf(size))));
            }
        } catch (RuntimeException ignored) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Failed to reload the list of messages hidden in the console."));
        }
    }

}
