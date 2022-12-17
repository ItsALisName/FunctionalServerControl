package by.alis.functionalbans.spigot.Additional.Logger;

import by.alis.functionalbans.spigot.Additional.WorldDate.WorldTimeAndDateClass;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;

import org.bukkit.Bukkit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class LogWriter {


    private final String currentSession = WorldTimeAndDateClass.getDateA() + "_" + WorldTimeAndDateClass.getTimeA();
    private final File logFile = new File("plugins/FunctionalBans/logs/", "log_" + currentSession + ".log");;

    public void createLogFile() {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
            if(getConfigSettings().isLoggerEnabled()) {
                try {
                    Files.createDirectories(Paths.get("plugins/FunctionalBans/logs/"));
                    this.logFile.createNewFile();
                    Bukkit.getOfflinePlayers();
                } catch (IOException w) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalBans | Error] Failed to create log file."));
                }
            }
        });
    }

    private void write(String message) {
        try {
            FileWriter fileWriter = new FileWriter(this.logFile, true);
            BufferedWriter writer = new BufferedWriter(fileWriter);
            writer.write(getConfigSettings().getLogFormat().replace("%1$f", WorldTimeAndDateClass.getDate() + " | " + WorldTimeAndDateClass.getTime()).replace("%2$f", message));
            writer.append("\r\n");
            writer.close();
        } catch (IOException ignored) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalBans | Error] Failed to write log to file"));
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
