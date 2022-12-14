package by.alis.functionalbans.spigot.Managers.Files;

import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class FileManager {


    public FileManager() {this.initializeFiles();}

    public File configFile;
    public FileConfiguration configuration;
    public File langFileRU;
    public FileConfiguration langRU;
    public File langFileEN;
    public FileConfiguration langEN;
    public File sqlFile;
    public File helpFile;

    public void initializeFiles() {
        this.configFile = new File("plugins/FunctionalBans/", "general.yml");
        this.configuration = YamlConfiguration.loadConfiguration(this.configFile);
        this.langFileRU = new File("plugins/FunctionalBans/language/", "lang_ru.yml");
        this.langRU = YamlConfiguration.loadConfiguration(this.langFileRU);
        this.sqlFile = new File("plugins/FunctionalBans/", "sqlite.db");
        this.helpFile = new File("plugins/FunctionalBans/", "about.txt");
        this.langFileEN = new File("plugins/FunctionalBans/language/", "lang_en.yml");
        this.langEN = YamlConfiguration.loadConfiguration(this.langFileEN);
    }

    public void initializeAndCreateFilesIfNotExists() {
        this.initializeFiles();

        if(!this.configFile.exists()) {
            FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("general.yml", false);
        }

        if(!this.langFileRU.exists()) {
            FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("language/lang_ru.yml", false);
        }

        if(!this.langFileEN.exists()) {
            FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("language/lang_en.yml", false);
        }

        if(!this.sqlFile.exists()) {
            FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("sqlite.db", false);
        }

        if(!this.helpFile.exists()) {
            FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("about.txt", false);
        }

    }


}
