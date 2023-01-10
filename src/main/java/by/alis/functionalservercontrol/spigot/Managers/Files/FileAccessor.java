package by.alis.functionalservercontrol.spigot.Managers.Files;

import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOError;
import java.io.IOException;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

public class FileAccessor {

    private final FileManager fileManager = new FileManager();

    public FileConfiguration getGeneralConfig() {
        return this.fileManager.configuration;
    }

    public FileConfiguration getLang() {
        if(this.fileManager.configuration.getString("plugin-settings.global-language").equalsIgnoreCase("ru_RU")) {
            return this.fileManager.langRU;
        } else if(this.fileManager.configuration.getString("plugin-settings.global-language").equalsIgnoreCase("en_US")) {
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
        this.fileManager.langEN.save(this.fileManager.langFileEN);
    }

    public void reloadGeneralConfig() throws IOError {
        this.fileManager.configuration = YamlConfiguration.loadConfiguration(this.fileManager.configFile);
    }

    public void reloadLang() throws IOError {
        if(!this.fileManager.langFileRU.exists()) {
            FunctionalServerControl.getPlugin(FunctionalServerControl.class).saveResource("language/lang_ru.yml", false);
            this.fileManager.langRU = YamlConfiguration.loadConfiguration(this.fileManager.langFileRU);
        }
        this.fileManager.langRU = YamlConfiguration.loadConfiguration(this.fileManager.langFileRU);
        if (!this.fileManager.langFileEN.exists()) {
            FunctionalServerControl.getPlugin(FunctionalServerControl.class).saveResource("language/lang_en.yml", false);
            this.fileManager.langEN = YamlConfiguration.loadConfiguration(this.fileManager.langFileEN);
        }
        this.fileManager.langEN = YamlConfiguration.loadConfiguration(this.fileManager.langFileEN);
    }

    //SQL_File
    public File getSQLiteFile() {
        return this.fileManager.sqlFile;
    }
    //SQL_File

    public FileConfiguration getCommandsLimiterConfig() {
        return this.fileManager.commandLimiterConfig;
    }

    public void reloadCommandLimiterFile() {
        this.fileManager.commandLimiterConfig = YamlConfiguration.loadConfiguration(this.fileManager.commandLimiterFile);
    }

    public void saveCommandLimiterFile() {
        try {
            this.fileManager.commandLimiterConfig.save(this.fileManager.commandLimiterFile);
        } catch (IOException ignored) {}
    }

    public FileConfiguration getCooldownsConfig() {
        return this.fileManager.cooldownsConfig;
    }

    public void reloadCooldownsConfig() {
        this.fileManager.cooldownsConfig = YamlConfiguration.loadConfiguration(this.fileManager.cooldownsFile);
    }

    public FileConfiguration getChatConfig() {
        return this.fileManager.chatConfig;
    }

    public void reloadChatConfig() {
        this.fileManager.chatConfig = YamlConfiguration.loadConfiguration(this.fileManager.chatFile);
    }

}
