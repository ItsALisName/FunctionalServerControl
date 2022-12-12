package by.alis.functionalbans.spigot.Managers.FilesManagers;

import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

public class FileAccessor {

    public FileManager fileManager = new FileManager();

    public FileAccessor() {}

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
        Bukkit.getConsoleSender().sendMessage("1: " + this.fileManager.configuration.getName());
        Bukkit.getConsoleSender().sendMessage("2: " + this.fileManager.configFile.getPath());
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
