package by.alis.functionalservercontrol.spigot;

import by.alis.functionalservercontrol.API.Enums.ProtocolVersions;
import by.alis.functionalservercontrol.spigot.Additional.ConsoleFilter.ConsoleFilterCore;
import by.alis.functionalservercontrol.spigot.Additional.ConsoleFilter.L4JFilter;
import by.alis.functionalservercontrol.spigot.Additional.CoreAdapters.CoreAdapter;
import by.alis.functionalservercontrol.spigot.Additional.Logger.LogWriter;
import by.alis.functionalservercontrol.spigot.Additional.Misc.Cooldowns.Cooldowns;
import by.alis.functionalservercontrol.spigot.Additional.Misc.Metrics;
import by.alis.functionalservercontrol.spigot.Additional.Misc.OtherUtils;
import by.alis.functionalservercontrol.spigot.Commands.*;
import by.alis.functionalservercontrol.spigot.Expansions.Expansions;
import by.alis.functionalservercontrol.spigot.Listeners.*;
import by.alis.functionalservercontrol.spigot.Listeners.Old.OldAsyncChatListener;
import by.alis.functionalservercontrol.spigot.Listeners.Old.PlayerItemPickupEvent;
import by.alis.functionalservercontrol.spigot.Listeners.Old.TabCompleteListener;
import by.alis.functionalservercontrol.spigot.Listeners.PluginMessages.ClientBrandListener;
import by.alis.functionalservercontrol.spigot.Listeners.PluginMessages.WorldDownloaderChannelListener;
import by.alis.functionalservercontrol.spigot.Listeners.ProtocolLibListeners.PacketCommandsListener;
import by.alis.functionalservercontrol.spigot.Managers.Files.FileManager;
import by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;


import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.*;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Expansions.Expansions.getProtocolLibManager;

/**
 * Plugin main class
 * <p>
 * Author ALis
 */
public final class FunctionalServerControl extends JavaPlugin {
    private final FileManager fileManager = new FileManager();
    private final LogWriter writer = new LogWriter();
    ConsoleFilterCore consoleFilterCore;

    @Override
    public void onEnable() {
        if(OtherUtils.isSuppotedVersion(getServer()) && !OtherUtils.getServerCoreName(getServer()).toLowerCase().contains("bukkit")) {
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl] Starting on " + OtherUtils.getServerCoreName(getServer()) + " " + OtherUtils.getServerVersion(getServer()).toString + " server version &a(Supported)"));
        } else {
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl] Starting on " + OtherUtils.getServerCoreName(getServer()) + " " + OtherUtils.getServerVersion(getServer()).toString + " server version &c(Not supported)"));
            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Disabling..."));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        OtherUtils.plugmanInjection();
        if(!CoreAdapter.setAdapter()) {
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        //Plugin Metrics
        new Metrics(this, 17278);
        //Plugin Metrics

        //Creating files if not exists
        this.fileManager.initializeAndCreateFilesIfNotExists();
        //Creating files if not exists

        //Settings initializer
        Cooldowns.getCooldowns().loadCooldowns();
        StaticSettingsAccessor.getConfigSettings().loadConfigSettings();
        StaticSettingsAccessor.getGlobalVariables().loadGlobalVariables();
        StaticSettingsAccessor.getLanguage().loadLanguage();
        StaticSettingsAccessor.getCommandLimiterSettings().loadCommandLimiterSettings();
        //Settings initializer

        //Bases functions
        getSQLiteManager().setupTables();
        //Bases functions

        //Expansions
        Expansions.getVaultManager().setupVaultPermissions();
        Expansions.getLuckPermsManager().setupLuckPerms();
        Expansions.getProtocolLibManager().setupProtocolLib();
        Expansions.getViaVersionManager().setupViaVersion();
        //Expansions


        //Commands registering
        new Test(this);
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
        new GetVersionCommand(this);
        new GetClientCommand(this);
        new GetInfoCommand(this);
        //Commands registering

        //Loaders
        getHidedMessagesContainer().loadHidedMessages();
        getReplacedMessagesContainer().loadReplacedMessages();
        //Loaders


        //Listeners registering
        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new QuitListener(), this);
        if(OtherUtils.isClassExists("org.bukkit.event.player.PlayerCommandSendEvent")){
            Bukkit.getPluginManager().registerEvents(new ServerSendCommandsListener(), this);
        } else {
            if(getProtocolLibManager().isProtocolLibSetuped()) {
                new PacketCommandsListener(this).onTabComplete();
            }
        }
        Bukkit.getPluginManager().registerEvents(new NullPlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new AsyncJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlaceBreakListeners(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractionListener(), this);
        Bukkit.getPluginManager().registerEvents(new EntityDamagesListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerMovingListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerCommandsListener(), this);
        if(OtherUtils.isClassExists("org.bukkit.event.entity.EntityPickupItemEvent")) {
            Bukkit.getPluginManager().registerEvents(new PlayerPickupItemListener(), this);
        } else {
            Bukkit.getPluginManager().registerEvents(new PlayerItemPickupEvent(), this);
        }
        Bukkit.getPluginManager().registerEvents(new PlayerDropItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new ConsoleSendCommandListener(), this);
        Bukkit.getPluginManager().registerEvents(new RemoteCommandsListener(), this);
        if(OtherUtils.isClassExists("org.bukkit.event.player.AsyncPlayerChatEvent")) {
            OldAsyncChatListener chatListener = new OldAsyncChatListener();
            Bukkit.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, chatListener, getConfigSettings().getChatListenerPriority(), chatListener, this, true);
        } else {
            AsyncChatListener asyncChatListener = new AsyncChatListener();
            Bukkit.getPluginManager().registerEvent(AsyncChatEvent.class, asyncChatListener, getConfigSettings().getChatListenerPriority(), asyncChatListener, this, true);
        }
        if(OtherUtils.isClassExists("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent")) {
            Bukkit.getPluginManager().registerEvents(new AsyncTabCompleteListener(), this);
        } else {
            Bukkit.getPluginManager().registerEvents(new TabCompleteListener(), this);
        }
        //Listeners registering

        this.registerPluginChannels();

        //Console filters
        this.writer.createLogFile();
        this.consoleFilterCore = new L4JFilter();
        getConsoleFilterCore().eventLog();
        getConsoleFilterCore().replaceMessage();
        getConsoleFilterCore().hideMessage();
        //Console filters
    }

    @Override
    public void onDisable() {
        this.unregisterPluginChannels();
    }

    private ConsoleFilterCore getConsoleFilterCore() {
        return consoleFilterCore;
    }

    private void registerPluginChannels() {
        if (OtherUtils.getServerVersion(this.getServer()).ordinal() < ProtocolVersions.V13.ordinal()) {
            getServer().getMessenger().registerIncomingPluginChannel(this, "MC|Brand", new ClientBrandListener());
            getServer().getMessenger().registerIncomingPluginChannel(this, "WDL|INIT", new WorldDownloaderChannelListener());
            getServer().getMessenger().registerOutgoingPluginChannel(this, "WDL|CONTROL");
        } else {
            getServer().getMessenger().registerIncomingPluginChannel(this, "minecraft:brand", new ClientBrandListener());
            getServer().getMessenger().registerIncomingPluginChannel(this, "wdl:init", new WorldDownloaderChannelListener());
            getServer().getMessenger().registerOutgoingPluginChannel(this, "wdl:control");
        }
    }

    private void unregisterPluginChannels() {
        if (OtherUtils.getServerVersion(this.getServer()).ordinal() < ProtocolVersions.V13.ordinal()) {
            getServer().getMessenger().unregisterIncomingPluginChannel(this, "WDL|INIT");
            getServer().getMessenger().unregisterOutgoingPluginChannel(this, "WDL|CONTROL");
            getServer().getMessenger().unregisterIncomingPluginChannel(this, "MC|Brand");
        } else {
            getServer().getMessenger().unregisterIncomingPluginChannel(this, "wdl:init");
            getServer().getMessenger().unregisterOutgoingPluginChannel(this, "wdl:control");
            getServer().getMessenger().unregisterOutgoingPluginChannel(this, "minecraft:brand");
        }
    }

}
