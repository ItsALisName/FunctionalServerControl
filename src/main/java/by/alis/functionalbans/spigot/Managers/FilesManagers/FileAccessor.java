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

    public FileConfiguration getLang() {
        if(getGeneralConfig().getString("plugin-settings.global-language").equalsIgnoreCase("ru_RU")) {
            return this.fileManager.langRU;
        } else if(getGeneralConfig().getString("plugin-settings.global-language").equalsIgnoreCase("en_US")) {
            return this.fileManager.langEN;
        } else {
            return this.fileManager.langEN;
        }
    }


    public void saveGeneralConfig() throws IOException {
        this.fileManager.configuration.save(this.fileManager.configFile);
    }

    public void saveLang() throws IOException {
        if(getGeneralConfig().getString("plugin-settings.global-language").equalsIgnoreCase("ru_RU")) {
            this.fileManager.langRU.save(this.fileManager.langFileRU);
        } else if(getGeneralConfig().getString("plugin-settings.global-language").equalsIgnoreCase("en_US")) {
            this.fileManager.langRU.save(this.fileManager.langFileEN);
        } else {
            this.fileManager.langRU.save(this.fileManager.langFileEN);
        }
    }

    public void reloadGeneralConfig() throws IOError {
        if(!this.fileManager.configFile.exists()) {
            FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("general.yml", false);
            return;
        }
        this.fileManager.configuration = YamlConfiguration.loadConfiguration(this.fileManager.configFile);
    }

    public void reloadLang() throws IOError {
        if(getGeneralConfig().getString("plugin-settings.global-language").equalsIgnoreCase("ru_RU")) {
            if(!this.fileManager.langFileRU.exists()) {
                FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("language/lang_ru.yml", false);
                return;
            }
            this.fileManager.langRU = YamlConfiguration.loadConfiguration(this.fileManager.langFileRU);
        } else if(getGeneralConfig().getString("plugin-settings.global-language").equalsIgnoreCase("en_US")) {
            if(!this.fileManager.langFileRU.exists()) {
                FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("language/lang_en.yml", false);
                return;
            }
            this.fileManager.langEN = YamlConfiguration.loadConfiguration(this.fileManager.langFileEN);
        } else {
            if(!this.fileManager.langFileRU.exists()) {
                FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("language/lang_en.yml", false);
                return;
            }
            this.fileManager.langEN = YamlConfiguration.loadConfiguration(this.fileManager.langFileEN);
        }
    }

    //SQL_File
    public File getSQLFile() {
        return this.fileManager.sqlFile;
    }
    //SQL_File

    public void basesFiles() {
        switch (getGeneralConfig().getString("plugin-settings.storage-method")) {
            case "sqlite": {
                if(!this.fileManager.sqlFile.exists()) {
                    FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("sqlite.db", false);
                }
                break;
            }
            case "mysql": {
                break;
            }
            case "h2": {
                break;
            }
            default: {
                if(!this.fileManager.sqlFile.exists()) {
                    FunctionalBansSpigot.getPlugin(FunctionalBansSpigot.class).saveResource("sqlite.db", false);
                }
                break;
            }
        }
    }


}
