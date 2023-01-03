package by.alis.functionalservercontrol.spigot.Managers.Files;

import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;

public class FileManager {


    public FileManager() {
        this.initializeFiles();
    }

    protected File configFile;
    protected FileConfiguration configuration;
    protected File langFileRU;
    protected FileConfiguration langRU;
    protected File langFileEN;
    protected FileConfiguration langEN;
    protected File sqlFile;
    protected File commandLimiterFile;
    protected FileConfiguration commandLimiterConfig;

    public void initializeFiles() {
        File logsFolder = new File("plugins/FunctionalServerControl/logs");
        if(!logsFolder.exists() && !logsFolder.mkdirs()) {
            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Failed to create 'logs' folder (You can ignore it)"));
        }
        this.configFile = new File("plugins/FunctionalServerControl/", "general.yml");
        this.configuration = YamlConfiguration.loadConfiguration(this.configFile);
        this.langFileRU = new File("plugins/FunctionalServerControl/language/", "lang_ru.yml");
        this.langRU = YamlConfiguration.loadConfiguration(this.langFileRU);
        this.sqlFile = new File("plugins/FunctionalServerControl/", "sqlite.db");
        this.langFileEN = new File("plugins/FunctionalServerControl/language/", "lang_en.yml");
        this.langEN = YamlConfiguration.loadConfiguration(this.langFileEN);
        this.commandLimiterFile = new File("plugins/FunctionalServerControl/", "commands-limiter.yml");
        this.commandLimiterConfig = YamlConfiguration.loadConfiguration(this.commandLimiterFile);
    }

    public void initializeAndCreateFilesIfNotExists() {
        this.initializeFiles();

        if(!this.configFile.exists()) {
            FunctionalServerControl.getPlugin(FunctionalServerControl.class).saveResource("general.yml", false);
        }

        if(!this.langFileRU.exists()) {
            FunctionalServerControl.getPlugin(FunctionalServerControl.class).saveResource("language/lang_ru.yml", false);
        }

        if(!this.langFileEN.exists()) {
            FunctionalServerControl.getPlugin(FunctionalServerControl.class).saveResource("language/lang_en.yml", false);
        }

        if(!this.sqlFile.exists()) {
            FunctionalServerControl.getPlugin(FunctionalServerControl.class).saveResource("sqlite.db", false);
        }

        if(!this.commandLimiterFile.exists()) {
            FunctionalServerControl.getPlugin(FunctionalServerControl.class).saveResource("commands-limiter.yml", false);
        }

    }
}
