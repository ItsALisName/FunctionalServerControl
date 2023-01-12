package by.alis.functionalservercontrol.spigot.managers.file;

import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;

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
    protected File cooldownsFile;
    protected FileConfiguration cooldownsConfig;
    protected File chatFile;
    protected FileConfiguration chatConfig;

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
        this.cooldownsFile = new File("plugins/FunctionalServerControl/", "global-cooldowns.yml");
        this.cooldownsConfig = YamlConfiguration.loadConfiguration(this.cooldownsFile);
        this.chatFile = new File("plugins/FunctionalServerControl/", "chat-settings.yml");
        this.chatConfig = YamlConfiguration.loadConfiguration(this.chatFile);
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

        if(!this.commandLimiterFile.exists()) {
            FunctionalServerControl.getPlugin(FunctionalServerControl.class).saveResource("commands-limiter.yml", false);
        }

        if(!this.cooldownsFile.exists()) {
            FunctionalServerControl.getPlugin(FunctionalServerControl.class).saveResource("global-cooldowns.yml", false);
        }

        if(!this.chatFile.exists()) {
            FunctionalServerControl.getPlugin(FunctionalServerControl.class).saveResource("chat-settings.yml", false);
        }

    }
}
