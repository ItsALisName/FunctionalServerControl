package by.alis.functionalbans.spigot.Managers.FilesManagers;

import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class FileManager {


    public FileManager() {
        this.initializeFiles();
    }

    protected File configFile;
    protected FileConfiguration configuration;

    protected File langFile;
    protected FileConfiguration lang;

    protected File sqlFile;

    private void initializeFiles() {
        this.configFile = new File("plugins/FunctionalBans/", "general.yml");
        this.configuration = YamlConfiguration.loadConfiguration(this.configFile);
        this.langFile = new File("plugins/FunctionalBans/", "lang.yml");
        this.lang = YamlConfiguration.loadConfiguration(this.langFile);
        this.sqlFile = new File("plugins/FunctionalBans/", "sqlite.db");
    }

    public void initializeAndCreateFilesIfNotExists() {
        this.initializeFiles();

        if(!this.configFile.exists()) {
            FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("general.yml", false);
        }

        if(!this.langFile.exists()) {
            FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("lang.yml", false);
        }

        if(!this.sqlFile.exists()) {
            FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("sqlite.db", false);
        }

    }


}
