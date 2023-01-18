package net.alis.functionalservercontrol.spigot.managers.file;

import net.alis.functionalservercontrol.spigot.FunctionalServerControl;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.libraries.com.tchristofferson.configupdater.ConfigUpdater;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class FileManager {

    private final FunctionalServerControl plugin;
    private final String configsVersion = "1.08-build1";
    public FileManager(FunctionalServerControl plugin) {
        this.plugin = plugin;
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
    protected File protectionFile;
    protected FileConfiguration protectionConfig;


    public void initializeFiles() {
        File logsFolder = new File("plugins/FunctionalServerControl/logs");
        if(!logsFolder.exists() && !logsFolder.mkdirs()) {
            Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&c[FunctionalServerControl] Failed to create 'logs' folder (You can ignore it)"));
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
        this.protectionFile = new File("plugins/FunctionalServerControl/", "protection.yml");
        this.protectionConfig = YamlConfiguration.loadConfiguration(this.protectionFile);
    }

    public void initializeAndCreateFilesIfNotExists() {
        this.initializeFiles();
        boolean b1 = true,b2 = true,b3 = true,b4 = true,b5 = true,b6 = true, b7 = true;

        if(!this.configFile.exists()) {
            plugin.saveResource("general.yml", false);
            b1 = false;
        }
        if(b1){
            if (this.configuration.getString("file-version") == null || !this.configuration.getString("file-version").equalsIgnoreCase(configsVersion)) {
                try {
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&e[FunctionalServerControl] Started updating configuration 'general.yml'"));
                    ConfigUpdater.updateConfiguration(FunctionalServerControl.getPlugin(FunctionalServerControl.class), "general.yml", this.configFile);
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&a[FunctionalServerControl] 'general.yml' config updated"));
                } catch (IOException ioException) {
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&4[FunctionalServerControl] Failed to update config 'general.yml'. See below for details"));
                    ioException.printStackTrace();
                }
            }
        }

        if(!this.langFileRU.exists()) {
            plugin.saveResource("language/lang_ru.yml", false);
            b2 = false;
        }
        if(b2){
            if (this.langRU.getString("file-version") == null || !this.langRU.getString("file-version").equalsIgnoreCase(configsVersion)) {
                try {
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&e[FunctionalServerControl] Started updating configuration 'lang_ru.yml'"));
                    ConfigUpdater.updateConfiguration(plugin, "language/lang_ru.yml", this.langFileRU);
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&a[FunctionalServerControl] 'lang_ru.yml' config updated"));
                } catch (IOException ioException) {
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&4[FunctionalServerControl] Failed to update config 'lang_ru.yml'. See below for details"));
                    ioException.printStackTrace();
                }
            }
        }

        if(!this.langFileEN.exists()) {
            plugin.saveResource("language/lang_en.yml", false);
            b3 = false;
        }
        if(b3){
            if (this.langEN.getString("file-version") == null || !this.langEN.getString("file-version").equalsIgnoreCase(configsVersion)) {
                try {
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&e[FunctionalServerControl] Started updating configuration 'lang_en.yml'"));
                    ConfigUpdater.updateConfiguration(plugin, "language/lang_en.yml", this.langFileEN);
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&a[FunctionalServerControl] 'lang_en.yml' config updated"));
                } catch (IOException ioException) {
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&4[FunctionalServerControl] Failed to update config 'lang_en.yml'. See below for details"));
                    ioException.printStackTrace();
                }
            }
        }

        if(!this.commandLimiterFile.exists()) {
            plugin.saveResource("commands-limiter.yml", false);
            b4 = false;
        }
        if(b4){
            if (this.commandLimiterConfig.getString("file-version") == null || !this.commandLimiterConfig.getString("file-version").equalsIgnoreCase(configsVersion)) {
                try {
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&e[FunctionalServerControl] Started updating configuration 'commands-limiter.yml'"));
                    ConfigUpdater.updateConfiguration(plugin, "commands-limiter.yml", this.commandLimiterFile);
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&a[FunctionalServerControl] 'commands-limiter.yml' config updated"));
                } catch (IOException ioException) {
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&4[FunctionalServerControl] Failed to update config 'commands-limiter.yml'. See below for details"));
                    ioException.printStackTrace();
                }
            }
        }

        if(!this.cooldownsFile.exists()) {
            plugin.saveResource("global-cooldowns.yml", false);
            b5 = false;
        }
        if(b5){
            if (this.cooldownsConfig.getString("file-version") == null || !this.cooldownsConfig.getString("file-version").equalsIgnoreCase(configsVersion)) {
                try {
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&e[FunctionalServerControl] Started updating configuration 'global-cooldowns.yml'"));
                    ConfigUpdater.updateConfiguration(plugin, "global-cooldowns.yml", this.cooldownsFile);
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&a[FunctionalServerControl] 'global-cooldowns.yml' config updated"));
                } catch (IOException ioException) {
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&4[FunctionalServerControl] Failed to update config 'global-cooldowns.yml'. See below for details"));
                    ioException.printStackTrace();
                }
            }
        }

        if(!this.chatFile.exists()) {
            plugin.saveResource("chat-settings.yml", false);
            b6 = false;
        }
        if(b6){
            if (this.chatConfig.getString("file-version") == null || !this.chatConfig.getString("file-version").equalsIgnoreCase(configsVersion)) {
                try {
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&e[FunctionalServerControl] Started updating configuration 'chat-settings.yml'"));
                    ConfigUpdater.updateConfiguration(plugin, "chat-settings.yml", this.chatFile);
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&a[FunctionalServerControl] 'chat-settings.yml' config updated"));
                } catch (IOException ioException) {
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&4[FunctionalServerControl] Failed to update config 'chat-settings.yml'. See below for details"));
                    ioException.printStackTrace();
                }
            }
        }
        if(!this.protectionFile.exists()) {
            plugin.saveResource("protection.yml", false);
            b7 = false;
        }
        if(b7){
            if (this.protectionConfig.getString("file-version") == null || !this.protectionConfig.getString("file-version").equalsIgnoreCase(configsVersion)) {
                try {
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&e[FunctionalServerControl] Started updating configuration 'protection.yml'"));
                    ConfigUpdater.updateConfiguration(plugin, "protection.yml", this.protectionFile);
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&a[FunctionalServerControl] 'chat-settings.yml' config updated"));
                } catch (IOException ioException) {
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&4[FunctionalServerControl] Failed to update config 'protection.yml'. See below for details"));
                    ioException.printStackTrace();
                }
            }
        }
    }
}
