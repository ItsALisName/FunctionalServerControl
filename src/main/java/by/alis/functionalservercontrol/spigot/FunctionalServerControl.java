package by.alis.functionalservercontrol.spigot;

import by.alis.functionalservercontrol.api.FunctionalApi;
import by.alis.functionalservercontrol.api.enums.ProtocolVersions;
import by.alis.functionalservercontrol.spigot.additional.consolefilter.Filter;
import by.alis.functionalservercontrol.spigot.additional.consolefilter.L4JFilter;
import by.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import by.alis.functionalservercontrol.spigot.additional.logger.LogWriter;
import by.alis.functionalservercontrol.spigot.additional.misc.apiutils.ApiCore;
import by.alis.functionalservercontrol.spigot.managers.cooldowns.Cooldowns;
import by.alis.functionalservercontrol.spigot.additional.misc.Metrics;
import by.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import by.alis.functionalservercontrol.spigot.commands.*;
import by.alis.functionalservercontrol.spigot.expansions.Expansions;
import by.alis.functionalservercontrol.spigot.listeners.*;
import by.alis.functionalservercontrol.spigot.listeners.outdated.OldAsyncChatListener;
import by.alis.functionalservercontrol.spigot.listeners.outdated.PlayerItemPickupEvent;
import by.alis.functionalservercontrol.spigot.listeners.outdated.TabCompleteListener;
import by.alis.functionalservercontrol.spigot.listeners.pluginmessages.ClientBrandListener;
import by.alis.functionalservercontrol.spigot.listeners.pluginmessages.WorldDownloaderChannelListener;
import by.alis.functionalservercontrol.spigot.listeners.packetlisteners.PacketCommandsListener;
import by.alis.functionalservercontrol.spigot.managers.file.FileManager;
import by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;


import static by.alis.functionalservercontrol.databases.DataBases.getMySQLManager;
import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.*;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.expansions.Expansions.getProtocolLibManager;

/**
 * Plugin main class
 * <p>
 * Author ALis
 */
public final class FunctionalServerControl extends JavaPlugin {
    private final FileManager fileManager = new FileManager();
    private final LogWriter writer = new LogWriter();
    private Filter consoleFilterCore;

    @Override
    public void onEnable() {
        String version = OtherUtils.getServerVersion(getServer()).toString();
        if(OtherUtils.isSuppotedVersion(getServer()) && !OtherUtils.getServerCoreName(getServer()).toLowerCase().contains("bukkit")) {
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl] Starting on " + OtherUtils.getServerCoreName(getServer()) + " " + version + " server version &a(Supported)"));
        } else {
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl] Starting on " + OtherUtils.getServerCoreName(getServer()) + " " + version + " server version &c(Not supported)"));
            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Disabling..."));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if(version.startsWith("1.8") || version.startsWith("1.9") || version.startsWith("1.10") || version.startsWith("1.11") || version.startsWith("1.12") || version.startsWith("1.13") || version.startsWith("1.14") || version.startsWith("1.15")) {
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl] You are using an old version of the Minecraft server!"));
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl] This version greatly limits the capabilities of the plugin"));
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl] Please update the Minecraft server version!"));
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl] You can download the new version of the server by following the links"));
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl] Spigot: &6https://getbukkit.org/download/spigot"));
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl] Paper: &6https://papermc.io/downloads"));
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
        SettingsAccessor.getConfigSettings().loadConfigSettings();
        SettingsAccessor.getGlobalVariables().loadGlobalVariables();
        SettingsAccessor.getLanguage().loadLanguage();
        SettingsAccessor.getChatSettings().loadChatSettings();
        SettingsAccessor.getCommandLimiterSettings().loadCommandLimiterSettings();
        //Settings initializer

        //Bases functions
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: getSQLiteManager().setupTables(); break;
            case MYSQL: getMySQLManager().setupTables(); break;
            case H2: {}
        }
        //Bases functions

        //Expansions
        Expansions.getVaultManager().setupVaultPermissions();
        Expansions.getLuckPermsManager().setupLuckPerms();
        Expansions.getProtocolLibManager().setupProtocolLib();
        Expansions.getViaVersionManager().setupViaVersion();
        //Expansions

        //Commands registering
        //new Test(this);
        new DeviceInfoCommand(this);
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
        new BanListCommand(this);
        new MuteListCommand(this);
        new ClearChatCommand(this);
        //Commands registering

        //Loaders
        getHidedMessagesContainer().loadHidedMessages();
        getReplacedMessagesContainer().loadReplacedMessages();
        //Loaders


        //Listeners registering
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
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
        if(OtherUtils.isClassExists("io.papermc.paper.event.player.AsyncChatEvent")) {
            AsyncChatListener asyncChatListener = new AsyncChatListener();
            Bukkit.getPluginManager().registerEvent(AsyncChatEvent.class, asyncChatListener, getConfigSettings().getChatListenerPriority(), asyncChatListener, this, true);
        } else {
            OldAsyncChatListener chatListener = new OldAsyncChatListener();
            Bukkit.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, chatListener, getConfigSettings().getChatListenerPriority(), chatListener, this, true);
        }
        if(OtherUtils.isClassExists("com.destroystokyo.paper.event.server.AsyncTabCompleteEvent")) {
            Bukkit.getPluginManager().registerEvents(new AsyncTabCompleteListener(), this);
        } else {
            Bukkit.getPluginManager().registerEvents(new TabCompleteListener(), this);
        }
        Bukkit.getPluginManager().registerEvents(new PlayerTeleportationListener(), this);
        Bukkit.getPluginManager().registerEvents(new SignChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEditBooksListener(), this);
        //Listeners registering

        this.registerPluginChannels();

        //Console filters
        this.writer.createLogFile();
        this.consoleFilterCore = new L4JFilter();
        getConsoleFilterCore().eventLog();
        getConsoleFilterCore().replaceMessage();
        getConsoleFilterCore().hideMessage();
        //Console filters
        if(getConfigSettings().isApiEnabled()) FunctionalApi.ApiGetter.setApi(new ApiCore());
    }

    @Override
    public void onDisable() {
        this.unregisterPluginChannels();
    }

    private Filter getConsoleFilterCore() {
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
