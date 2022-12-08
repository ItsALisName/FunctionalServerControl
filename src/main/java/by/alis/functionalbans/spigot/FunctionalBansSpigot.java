package by.alis.functionalbans.spigot;

import by.alis.functionalbans.spigot.Additional.ConsoleFilter.ConsoleFilterCore;
import by.alis.functionalbans.spigot.Additional.ConsoleFilter.L4JFilter;
import by.alis.functionalbans.spigot.Additional.Other.OtherUtils;
import by.alis.functionalbans.spigot.Commands.BanCommand;
import by.alis.functionalbans.spigot.Commands.FunctionalBansCommand;
import by.alis.functionalbans.spigot.Commands.KickCommand;
import by.alis.functionalbans.spigot.Expansions.StaticExpansions;
import by.alis.functionalbans.spigot.Listeners.AsyncJoinListener;
import by.alis.functionalbans.spigot.Listeners.CommandSendListener;
import by.alis.functionalbans.spigot.Listeners.FirstPlayerJoinListener;
import by.alis.functionalbans.spigot.Managers.BansManagers.BanManager;
import by.alis.functionalbans.spigot.Managers.FilesManagers.FileManager;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


import static by.alis.functionalbans.spigot.Additional.ConsoleFilter.StaticConsoleFilterHelper.getConsoleFilterHelper;
import static by.alis.functionalbans.spigot.Additional.Containers.StaticContainers.getHidedMessagesContainer;
import static by.alis.functionalbans.spigot.Additional.Containers.StaticContainers.getReplacedMessagesContainer;
import static by.alis.functionalbans.databases.StaticBases.getSQLiteManager;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;


public final class FunctionalBansSpigot extends JavaPlugin {
    FileManager fileManager = new FileManager();

    BanManager banManager = new BanManager();
    ConsoleFilterCore consoleFilterCore;

    @Override
    public void onEnable() {

        //Creating files if not exists
        this.fileManager.initializeAndCreateFilesIfNotExists();
        //Creating files if not exists

        //SQL functions
        getSQLiteManager().setupTables();
        //SQL functions


        //Settings initializer
        getConfigSettings().loadConfigSettings();
        StaticSettingsAccessor.getGlobalVariables().loadGlobalVariables();
        //Settings initializer

        //Expansions
        StaticExpansions.getVaultManager().setupVaultPermissions();
        StaticExpansions.getLuckPermsManager().setupLuckPerms();
        StaticExpansions.getProtocolLibManager().setupProtocolLib();
        //Expansions

        //Loaders
        getHidedMessagesContainer().loadHidedMessages();
        getReplacedMessagesContainer().loadReplacedMessages();
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            this.banManager.getBanContainerManager().loadBansIntoRAM();
        }
        //Loaders

        //Commands registering
        new KickCommand(this);
        new BanCommand(this);
        new FunctionalBansCommand(this);
        //End commands registering

        //Events registering
        new FirstPlayerJoinListener();
        Bukkit.getPluginManager().registerEvents(new FirstPlayerJoinListener(), this);
        if(OtherUtils.isClassExists("org.bukkit.event.player.PlayerCommandSendEvent")){
            new CommandSendListener();
            Bukkit.getPluginManager().registerEvents(new CommandSendListener(), this);
        }
        new AsyncJoinListener();
        Bukkit.getPluginManager().registerEvents(new AsyncJoinListener(), this);
        //Events registering

        //Packet events

        //Packet events

        //Console filters
        getConsoleFilterHelper().loadFunctionalBansCommands();
        consoleFilterCore = new L4JFilter();
        getConsoleFilterCore().eventLog();
        getConsoleFilterCore().replaceMessage();
        getConsoleFilterCore().hideMessage();
        //Console filters

    }

    @Override
    public void onDisable() {

    }

    public ConsoleFilterCore getConsoleFilterCore() {
        return consoleFilterCore;
    }
}
