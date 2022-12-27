package by.alis.functionalservercontrol.spigot.Managers.Files;

import by.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static by.alis.functionalservercontrol.spigot.Additional.Other.TextUtils.setColors;

public class FileManager {


    public FileManager() {
        this.initializeFiles();
    }

    public File configFile;
    public FileConfiguration configuration;
    public File langFileRU;
    public FileConfiguration langRU;
    public File langFileEN;
    public FileConfiguration langEN;
    public File sqlFile;
    public File helpFile;

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
        this.helpFile = new File("plugins/FunctionalServerControl/", "about.txt");
        this.langFileEN = new File("plugins/FunctionalServerControl/language/", "lang_en.yml");
        this.langEN = YamlConfiguration.loadConfiguration(this.langFileEN);
    }

    public void initializeAndCreateFilesIfNotExists() {
        this.initializeFiles();

        if(!this.configFile.exists()) {
            FunctionalServerControlSpigot.getPlugin(FunctionalServerControlSpigot.class).saveResource("general.yml", false);
        }

        if(!this.langFileRU.exists()) {
            FunctionalServerControlSpigot.getPlugin(FunctionalServerControlSpigot.class).saveResource("language/lang_ru.yml", false);
        }

        if(!this.langFileEN.exists()) {
            FunctionalServerControlSpigot.getPlugin(FunctionalServerControlSpigot.class).saveResource("language/lang_en.yml", false);
        }

        if(!this.sqlFile.exists()) {
            FunctionalServerControlSpigot.getPlugin(FunctionalServerControlSpigot.class).saveResource("sqlite.db", false);
        }

        if(!this.helpFile.exists()) {
            FunctionalServerControlSpigot.getPlugin(FunctionalServerControlSpigot.class).saveResource("about.txt", false);
        }

    }

    protected void reloadFiles() {

    }


}
