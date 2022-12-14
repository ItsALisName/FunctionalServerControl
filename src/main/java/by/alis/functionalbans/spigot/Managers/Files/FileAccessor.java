package by.alis.functionalbans.spigot.Managers.Files;

import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

public class FileAccessor {

    private FileManager fileManager = new FileManager();

    public FileAccessor() { }

    public FileConfiguration getGeneralConfig() {
        return this.fileManager.configuration;
    }

    public FileConfiguration getLang() {
        if(getConfigSettings().getGlobalLanguage().equalsIgnoreCase("ru_RU")) {
            return this.fileManager.langRU;
        } else if(getConfigSettings().getGlobalLanguage().equalsIgnoreCase("en_US")) {
            return this.fileManager.langEN;
        } else {
            return this.fileManager.langEN;
        }
    }


    public void saveGeneralConfig() throws IOException {
        this.fileManager.configuration.save(this.fileManager.configFile);
    }

    public void saveLang() throws IOException {
        this.fileManager.langRU.save(this.fileManager.langFileRU);
        this.fileManager.langRU.save(this.fileManager.langFileEN);
        this.fileManager.langRU.save(this.fileManager.langFileEN);
    }

    public void reloadGeneralConfig() throws IOError {
        this.fileManager.configuration = YamlConfiguration.loadConfiguration(this.fileManager.configFile);
    }

    public void reloadLang() throws IOError {
        if(!this.fileManager.langFileRU.exists()) {
            FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("language/lang_ru.yml", false);
            this.fileManager.langRU = YamlConfiguration.loadConfiguration(this.fileManager.langFileRU);
        }
        this.fileManager.langRU = YamlConfiguration.loadConfiguration(this.fileManager.langFileRU);
        if (!this.fileManager.langFileEN.exists()) {
            FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("language/lang_en.yml", false);
            this.fileManager.langEN = YamlConfiguration.loadConfiguration(this.fileManager.langFileEN);
        }
        this.fileManager.langEN = YamlConfiguration.loadConfiguration(this.fileManager.langFileEN);
    }

    //SQL_File
    public File getSQLiteFile() {
        return this.fileManager.sqlFile;
    }
    //SQL_File


}
