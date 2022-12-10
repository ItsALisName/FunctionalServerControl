package by.alis.functionalbans.spigot;

import by.alis.functionalbans.spigot.Additional.ConsoleFilter.ConsoleFilterCore;
import by.alis.functionalbans.spigot.Additional.ConsoleFilter.L4JFilter;
import by.alis.functionalbans.spigot.Additional.Other.OtherUtils;
import by.alis.functionalbans.spigot.Additional.Other.TemporaryCache;
import by.alis.functionalbans.spigot.Commands.BanCommand;
import by.alis.functionalbans.spigot.Commands.FunctionalBansCommand;
import by.alis.functionalbans.spigot.Commands.KickCommand;
import by.alis.functionalbans.spigot.Expansions.StaticExpansions;
import by.alis.functionalbans.spigot.Listeners.AsyncJoinListener;
import by.alis.functionalbans.spigot.Listeners.CommandSendListener;
import by.alis.functionalbans.spigot.Listeners.JoinListener;
import by.alis.functionalbans.spigot.Listeners.NullPlayerJoinListener;
import by.alis.functionalbans.spigot.Managers.BansManagers.BanManager;
import by.alis.functionalbans.spigot.Managers.CooldownsManager;
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
    private final FileManager fileManager = new FileManager();
    private final BanManager banManager = new BanManager();
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
        CooldownsManager.loadCooldowns();
        getSQLiteManager().clearCooldowns();
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
        new JoinListener();
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        if(OtherUtils.isClassExists("org.bukkit.event.player.PlayerCommandSendEvent")){
            new CommandSendListener();
            Bukkit.getPluginManager().registerEvents(new CommandSendListener(), this);
        }
        new NullPlayerJoinListener();
        Bukkit.getPluginManager().registerEvents(new NullPlayerJoinListener(), this);
        new AsyncJoinListener();
        Bukkit.getPluginManager().registerEvents(new AsyncJoinListener(), this);
        //Events registering

        //Other
        //Other

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
        CooldownsManager.saveCooldowns();
    }

    private ConsoleFilterCore getConsoleFilterCore() {
        return consoleFilterCore;
    }
}
