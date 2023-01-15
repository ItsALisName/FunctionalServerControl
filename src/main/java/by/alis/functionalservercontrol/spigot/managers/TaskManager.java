package by.alis.functionalservercontrol.spigot.managers;

import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

public class TaskManager {

    private static final FunctionalServerControl plugin = FunctionalServerControl.getPlugin(FunctionalServerControl.class);
    private static final BukkitScheduler scheduler = Bukkit.getScheduler();

    public static void preformAsync(BukkitRunnable task) {
        task.runTaskAsynchronously(plugin);
    }
    public static void preformAsync(Runnable task) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    public static void preformAsyncLater(BukkitRunnable task, long delay) {
        task.runTaskLaterAsynchronously(plugin, delay);
    }

    public static void preformAsyncLater(Runnable task, long delay) {
        scheduler.runTaskLaterAsynchronously(plugin, task, delay);
    }

    public static void preformAsyncTimerTask(BukkitRunnable task, long delay, long period) {
        task.runTaskTimerAsynchronously(plugin, delay, period);
    }

    public static void preformAsyncTimerTask(Runnable task, long delay, long period) {
        scheduler.runTaskTimerAsynchronously(plugin, task, delay, period);
    }

    public static void preformSync(BukkitRunnable task) {
        task.runTask(plugin);
    }
    public static void preformSync(Runnable task) {
        scheduler.runTask(plugin, task);
    }

    public static void preformSyncLater(BukkitRunnable task, long delay) {
        task.runTaskLater(plugin, delay);
    }

    public static void preformSyncLater(Runnable task, long delay) {
        scheduler.runTaskLater(plugin, task, delay);
    }

    public static void preformSyncTimerTask(BukkitRunnable task, long delay, long period) {
        task.runTaskTimer(plugin, delay, period);
    }

    public static void preformSyncTimerTask(Runnable task, long delay, long period) {
        scheduler.runTaskTimer(plugin, task, delay, period);
    }

}
