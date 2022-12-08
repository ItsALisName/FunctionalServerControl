package by.alis.functionalbans.spigot.Managers.FilesManagers;

import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

public class FileAccessor {

    FileManager fileManager = new FileManager();

    public FileAccessor() {}

    public FileConfiguration getGeneralConfig() {
        return this.fileManager.configuration;
    }

    public FileConfiguration getLang() { return this.fileManager.lang; }


    public void saveGeneralConfig() throws IOException {
        this.fileManager.configuration.save(this.fileManager.configFile);
    }

    public void saveLang() throws IOException {
        this.fileManager.lang.save(this.fileManager.langFile);
    }

    public void reloadGeneralConfig() throws IOError {
        if(!this.fileManager.configFile.exists()) {
            FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("general.yml", false);
            return;
        }
        this.fileManager.configuration = YamlConfiguration.loadConfiguration(this.fileManager.configFile);
    }

    public void reloadLang() throws IOError {
        if(!this.fileManager.langFile.exists()) {
            FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("lang.yml", false);
            return;
        }
        this.fileManager.lang = YamlConfiguration.loadConfiguration(this.fileManager.langFile);
    }

    //SQL_File
    public File getSQLFile() {
        return this.fileManager.sqlFile;
    }
    //SQL_File

}
