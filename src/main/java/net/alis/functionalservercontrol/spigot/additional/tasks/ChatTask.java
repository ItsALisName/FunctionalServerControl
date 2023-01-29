package net.alis.functionalservercontrol.spigot.additional.tasks;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getChatSettings;

public class ChatTask extends BukkitRunnable {

    public ChatTask() {
        TaskManager.preformAsyncTimerTask(new RepeatingMessagesRemover(),0, 240L);
    }

    @Override
    public void run() {
        for(Map.Entry<FID, Integer> entry : TemporaryCache.getChatDelays().entrySet()) {
            if(getChatSettings().isTickDelaysIfOffline()) {
                int timeLeft = entry.getValue();
                timeLeft = timeLeft - 1;
                if (timeLeft <= 0) {
                    TemporaryCache.getChatDelays().remove(entry.getKey());
                } else {
                    TemporaryCache.getChatDelays().replace(entry.getKey(), timeLeft);
                }
            } else {
                if(FunctionalPlayer.get(entry.getKey()) != null) {
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
