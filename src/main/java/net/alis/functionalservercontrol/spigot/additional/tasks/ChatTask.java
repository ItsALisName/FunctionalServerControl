package net.alis.functionalservercontrol.spigot.additional.tasks;

import net.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.UUID;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getChatSettings;

public class ChatTask extends BukkitRunnable {

    public ChatTask() {
        TaskManager.preformAsyncTimerTask(new RepeatingMessagesRemover(),0, 240L);
    }

    @Override
    public void run() {
        for(Map.Entry<UUID, Integer> entry : TemporaryCache.getChatDelays().entrySet()) {
            if(getChatSettings().isTickDelaysIfOffline()) {
                int timeLeft = entry.getValue();
                timeLeft = timeLeft - 1;
                if (timeLeft <= 0) {
                    TemporaryCache.getChatDelays().remove(entry.getKey());
                } else {
                    TemporaryCache.getChatDelays().replace(entry.getKey(), timeLeft);
                }
            } else {
                if(Bukkit.getPlayer(entry.getKey()) != null) {
                    int timeLeft = entry.getValue();
                    timeLeft = timeLeft - 1;
                    if (timeLeft <= 0) {
                        TemporaryCache.getChatDelays().remove(entry.getKey());
                    } else {
                        TemporaryCache.getChatDelays().replace(entry.getKey(), timeLeft);
                    }
                }
            }
        }
    }

    static class RepeatingMessagesRemover extends BukkitRunnable {
        @Override
        public void run() {
            TemporaryCache.getRepeatingMessages().clear();
        }
    }

}
