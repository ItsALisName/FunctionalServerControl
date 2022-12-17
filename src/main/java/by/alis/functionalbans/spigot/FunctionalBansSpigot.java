package by.alis.functionalbans.spigot;

import by.alis.functionalbans.spigot.Additional.ConsoleFilter.ConsoleFilterCore;
import by.alis.functionalbans.spigot.Additional.ConsoleFilter.L4JFilter;
import by.alis.functionalbans.spigot.Additional.Logger.LogWriter;
import by.alis.functionalbans.spigot.Additional.Other.OtherUtils;
import by.alis.functionalbans.spigot.Additional.TimerTasks.TimerSetuper;
import by.alis.functionalbans.spigot.Commands.*;
import by.alis.functionalbans.spigot.Expansions.Expansions;
import by.alis.functionalbans.spigot.Listeners.*;
import by.alis.functionalbans.spigot.Managers.CooldownsManager;
import by.alis.functionalbans.spigot.Managers.Files.FileManager;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


import static by.alis.functionalbans.spigot.Additional.ConsoleFilter.StaticConsoleFilterHelper.getConsoleFilterHelper;
import static by.alis.functionalbans.databases.DataBases.getSQLiteManager;
import static by.alis.functionalbans.spigot.Additional.Containers.StaticContainers.*;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

/**
 * Plugin main class
 * -> Author ALis
 */
public final class FunctionalBansSpigot extends JavaPlugin {
    private final FileManager fileManager = new FileManager();
    private final TimerSetuper timerSetuper = new TimerSetuper();
    private final LogWriter writer = new LogWriter();
    ConsoleFilterCore consoleFilterCore;

    @Override
    public void onEnable() {

        //Creating files if not exists
        this.fileManager.initializeAndCreateFilesIfNotExists();
        //Creating files if not exists

        //Settings initializer
        StaticSettingsAccessor.getConfigSettings().loadConfigSettings();
        StaticSettingsAccessor.getGlobalVariables().loadGlobalVariables();
        //Settings initializer

        //Bases functions
        getSQLiteManager().setupTables();

        CooldownsManager.loadCooldowns();
        getSQLiteManager().clearCooldowns();
        //Bases functions



        //Expansions
        Expansions.getVaultManager().setupVaultPermissions();
        Expansions.getLuckPermsManager().setupLuckPerms();
        Expansions.getProtocolLibManager().setupProtocolLib();
        //Expansions

        //Loaders
        getHidedMessagesContainer().loadHidedMessages();
        getReplacedMessagesContainer().loadReplacedMessages();
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            getBanContainerManager().loadBansIntoRAM();
        }
        //Loaders

        //Commands registering
        new Test(this);
        new KickCommand(this);
        new BanCommand(this);
        new FunctionalBansCommand(this);
        new TempbanCommand(this);
        new KickAllCommand(this);
        new UnbanCommand(this);
        new UnbanallCommand(this);
        new CrazykickCommand(this);
        new DupeIpCommand(this);
        //Commands registering

        //Events registering
        new JoinListener();
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        new QuitListener();
        Bukkit.getPluginManager().registerEvents(new QuitListener(), this);
        new CommandSendListener();
        if(OtherUtils.isClassExists("org.bukkit.event.player.PlayerCommandSendEvent")){
            Bukkit.getPluginManager().registerEvents(new CommandSendListener(), this);
        }
        new NullPlayerJoinListener();
        Bukkit.getPluginManager().registerEvents(new NullPlayerJoinListener(), this);
        new AsyncJoinListener();
        Bukkit.getPluginManager().registerEvents(new AsyncJoinListener(), this);
        //Events registering

        //Console filters
        this.writer.createLogFile();
        getConsoleFilterHelper().loadFunctionalBansCommands();
        this.consoleFilterCore = new L4JFilter();
        getConsoleFilterCore().eventLog();
        getConsoleFilterCore().replaceMessage();
        getConsoleFilterCore().hideMessage();

        //Console filters

        //Timers
        this.timerSetuper.setupTimers();
        //Timers

    }

    @Override
    public void onDisable() {
        CooldownsManager.saveCooldowns();
    }

    private ConsoleFilterCore getConsoleFilterCore() {
        return consoleFilterCore;
    }

}
