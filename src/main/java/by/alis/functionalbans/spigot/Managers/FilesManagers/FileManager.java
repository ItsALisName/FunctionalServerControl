package by.alis.functionalbans.spigot.Managers.FilesManagers;

import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

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
    protected File helpFile;

    private void initializeFiles() {
        this.configFile = new File("plugins/FunctionalBans/", "general.yml");
        this.configuration = YamlConfiguration.loadConfiguration(this.configFile);
        this.langFileRU = new File("plugins/FunctionalBans/language/", "language/lang_ru.yml");
        this.langRU = YamlConfiguration.loadConfiguration(this.langFileRU);
        this.sqlFile = new File("plugins/FunctionalBans/", "sqlite.db");
        this.helpFile = new File("plugins/FunctionalBans/", "about.txt");
        this.langFileEN = new File("plugins/FunctionalBans/language/", "lang_en.yml");
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

        if(!this.helpFile.exists()) {
            FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("about.txt", false);
        }

    }


}
