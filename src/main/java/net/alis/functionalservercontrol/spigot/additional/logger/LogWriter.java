package net.alis.functionalservercontrol.spigot.additional.logger;

import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.additional.misc.WorldTimeAndDateClass;

import org.bukkit.Bukkit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class LogWriter {


    private final String currentSession = WorldTimeAndDateClass.getDateA() + "_" + WorldTimeAndDateClass.getTimeA();
    private final File logFile = new File("plugins/FunctionalServerControl/logs/", "log_" + currentSession + ".log");;

    public void createLogFile() {
        TaskManager.preformAsync(() -> {
            if(getConfigSettings().isLoggerEnabled()) {
                try {
                    Files.createDirectories(Paths.get("plugins/FunctionalServerControl/logs/"));
                    this.logFile.createNewFile();
                    Bukkit.getOfflinePlayers();
                } catch (IOException w) {
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&4[FunctionalServerControl | Error] Failed to create log file."));
                }
            }
        });
    }

    private void write(String message) {
        try {
            FileWriter fileWriter = new FileWriter(this.logFile, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(getConfigSettings().getLogFormat().replace("%1$f", WorldTimeAndDateClass.getDate() + " | " + WorldTimeAndDateClass.getTime()).replace("%2$f", TextUtils.removeColorCodes(message)));
            writer.append("\r\n");
            writer.close();
        } catch (IOException ignored) {
            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&4[FunctionalServerControl | Error] Failed to write log to file"));
        }
    }

    public void writeLog(String message) {
        for(String m : getConfigSettings().getMessagesToLog()) {
            if(message.contains(m)) {
                write(message);
                return;
            }
        }
    }

}
