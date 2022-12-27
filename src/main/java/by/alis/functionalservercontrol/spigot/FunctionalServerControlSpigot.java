package by.alis.functionalservercontrol.spigot;

import by.alis.functionalservercontrol.spigot.Additional.ConsoleFilter.ConsoleFilterCore;
import by.alis.functionalservercontrol.spigot.Additional.ConsoleFilter.L4JFilter;
import by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.Language;
import by.alis.functionalservercontrol.spigot.Additional.Logger.LogWriter;
import by.alis.functionalservercontrol.spigot.Additional.Other.OtherUtils;
import by.alis.functionalservercontrol.spigot.Commands.*;
import by.alis.functionalservercontrol.spigot.Expansions.Expansions;
import by.alis.functionalservercontrol.spigot.Listeners.*;
import by.alis.functionalservercontrol.spigot.Listeners.ProtocolLibListeners.PacketTabCompleteListener;
import by.alis.functionalservercontrol.spigot.Managers.CommandsRegistrationManager;
import by.alis.functionalservercontrol.spigot.Managers.CooldownsManager;
import by.alis.functionalservercontrol.spigot.Managers.Files.FileManager;
import by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;


import static by.alis.functionalservercontrol.spigot.Additional.ConsoleFilter.StaticConsoleFilterHelper.getConsoleFilterHelper;
import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.*;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Expansions.Expansions.getProtocolLibManager;

/**
 * Plugin main class
 * <p>
 * Author ALis
 */
public final class FunctionalServerControlSpigot extends JavaPlugin {
    private final FileManager fileManager = new FileManager();
    private final LogWriter writer = new LogWriter();
    ConsoleFilterCore consoleFilterCore;

    @Override
    public void onEnable() {
        if(OtherUtils.isSuppotedVersion(getServer())) {
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl] Starting on " + OtherUtils.getServerPackageName(getServer()) + " server version &a(Supported)"));
        } else {
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl] Starting on " + OtherUtils.getServerPackageName(getServer()) + " server version &c(Not supported)"));
            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Disabling..."));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //Creating files if not exists
        this.fileManager.initializeAndCreateFilesIfNotExists();
        //Creating files if not exists

        //Settings initializer
        StaticSettingsAccessor.getConfigSettings().loadConfigSettings();
        StaticSettingsAccessor.getGlobalVariables().loadGlobalVariables();
        StaticSettingsAccessor.getLanguage().loadLanguage();
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


        //Commands registering
        CommandsRegistrationManager.registerCommand(this, new Test(this), new String[]{"test"}, "", "");
        //new Test(this);
        new KickCommand(this);
        new BanCommand(this);
        new FunctionalServerControlCommand(this);
        new TempbanCommand(this);
        new KickAllCommand(this);
        new UnbanCommand(this);
        new UnbanallCommand(this);
        new CrazykickCommand(this);
        new DupeIpCommand(this);
        new CheatCheckCommand(this);
        new BanIpCommand(this);
        new TempBanIpCommand(this);
        new MuteCommand(this);
        new MuteIpCommand(this);
        new TempMuteCommand(this);
        new TempMuteIpCommand(this);
        new UnmuteCommand(this);
        new UnmuteallCommand(this);
        //Commands registering

        //Loaders
        getHidedMessagesContainer().loadHidedMessages();
        getReplacedMessagesContainer().loadReplacedMessages();
        //Loaders


        //Events registering
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new QuitListener(), this);
        if(OtherUtils.isClassExists("org.bukkit.event.player.PlayerCommandSendEvent")){
            Bukkit.getPluginManager().registerEvents(new CommandSendListener(), this);
        }
        Bukkit.getPluginManager().registerEvents(new NullPlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new AsyncJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlaceBreakListeners(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractionListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamagesListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMovingListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerCommandsListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerItemDropPickupListener(), this);
        Bukkit.getPluginManager().registerEvents(new ConsoleSendCommandListener(), this);
        Bukkit.getPluginManager().registerEvents(new RemoteCommandsListener(), this);
        AsyncChatListener chatListener = new AsyncChatListener();
        Bukkit.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, chatListener, getConfigSettings().getChatListenerPriority(), chatListener, this, true);
        //Events registering

        //Packet events registering
        if(getProtocolLibManager().isProtocolLibSetuped()) {
            new PacketTabCompleteListener(this).onTabComplete();
        }
        //Packet events registering

        //Console filters
        this.writer.createLogFile();
        getConsoleFilterHelper().loadFunctionalServerControlCommands();
        this.consoleFilterCore = new L4JFilter();
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
