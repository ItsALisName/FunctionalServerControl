package net.alis.functionalservercontrol.spigot;

import net.alis.functionalservercontrol.api.enums.ProtocolVersions;
import net.alis.functionalservercontrol.api.naf.Incore;
import net.alis.functionalservercontrol.api.naf.v1_10_0.connection.ConnectionListener;
import net.alis.functionalservercontrol.libraries.com.jeff_media.updatechecker.UpdateCheckSource;
import net.alis.functionalservercontrol.libraries.com.jeff_media.updatechecker.UpdateChecker;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.settings.PacketEventsSettings;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.registerer.OfflinePlayerRegisterer;
import net.alis.functionalservercontrol.spigot.additional.misc.metrics.Metrics;
import net.alis.functionalservercontrol.spigot.additional.tasks.PacketLimiter;
import net.alis.functionalservercontrol.spigot.commands.*;
import net.alis.functionalservercontrol.spigot.dependencies.Expansions;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.server.ServerVersion;
import net.alis.functionalservercontrol.spigot.listeners.*;
import net.alis.functionalservercontrol.spigot.listeners.outdated.*;
import net.alis.functionalservercontrol.spigot.listeners.packetlisteners.LecternCrashListener;
import net.alis.functionalservercontrol.spigot.listeners.packetlisteners.PacketCommandsListener;
import net.alis.functionalservercontrol.spigot.listeners.packetlisteners.PacketLimiterListener;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.managers.cooldowns.Cooldowns;
import net.alis.functionalservercontrol.spigot.additional.consolefilter.Filter;
import net.alis.functionalservercontrol.spigot.additional.consolefilter.L4JFilter;
import net.alis.functionalservercontrol.spigot.additional.logger.LogWriter;
import net.alis.functionalservercontrol.api.naf.ApiCore;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.PacketEvents;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.managers.file.FileManager;
import net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;


import static net.alis.functionalservercontrol.databases.DataBases.*;
import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.*;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;

/**
 * Plugin main class
 * <p>
 * Author ALis
 */
public final class FunctionalServerControlSpigot extends JavaPlugin {
    private final FileManager fileManager = new FileManager(this);
    private final LogWriter writer = new LogWriter();
    private Filter consoleFilterCore;
    private String serverVersion;

    @Override
    public void onLoad() {
        serverVersion = OtherUtils.getServerVersion(getServer()).toString();
        Bukkit.getConsoleSender().sendMessage(setColors("&3[FunctionalServerControl] &3&oPre-initializing plugin..."));
        PacketEventsSettings settings = PacketEvents.create(this).getSettings();
        settings.fallbackServerVersion(ServerVersion.getLatest()).compatInjector(false);
        PacketEvents.get().loadAsyncNewThread();
        if(serverVersion.startsWith("1.8") || serverVersion.startsWith("1.9") || serverVersion.startsWith("1.10") || serverVersion.startsWith("1.11") || serverVersion.startsWith("1.12") || serverVersion.startsWith("1.13") || serverVersion.startsWith("1.14") || serverVersion.startsWith("1.15")) {
            Bukkit.getConsoleSender().sendMessage(setColors("&3[FunctionalServerControl] &3&oYou are using an old version of the Minecraft server!"));
            Bukkit.getConsoleSender().sendMessage(setColors("&3[FunctionalServerControl] &3&oThis version greatly limits the capabilities of the plugin"));
            Bukkit.getConsoleSender().sendMessage(setColors("&3[FunctionalServerControl] &3&oPlease update the Minecraft server version!"));
            Bukkit.getConsoleSender().sendMessage(setColors("&3[FunctionalServerControl] &3&oYou can download the new version of the server by following the links"));
            Bukkit.getConsoleSender().sendMessage(setColors("&3[FunctionalServerControl] &3&oSpigot: &3&l&ohttps://getbukkit.org/download/spigot"));
            Bukkit.getConsoleSender().sendMessage(setColors("&3[FunctionalServerControl] &3&oPaper: &3&l&ohttps://papermc.io/downloads"));
        }
    }

    @Override
    public void onEnable() {
        if(OtherUtils.isSuppotedVersion(getServer()) && !OtherUtils.getServerCoreName(getServer()).toLowerCase().contains("bukkit")) {
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl] Starting on " + OtherUtils.getServerCoreName(getServer()) + " " + serverVersion + " server version &a(Supported)"));
        } else {
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl] Starting on " + OtherUtils.getServerCoreName(getServer()) + " " + serverVersion + " server version &c(Not supported)"));
            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Disabling..."));
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
        SettingsAccessor.getConfigSettings().setStorageType();
        SettingsAccessor.getConfigSettings().loadConfigSettings();
        SettingsAccessor.getGlobalVariables().loadGlobalVariables();
        SettingsAccessor.getLanguage().loadLanguage();
        SettingsAccessor.getChatSettings().loadChatSettings();
        SettingsAccessor.getCommandLimiterSettings().loadCommandLimiterSettings();
        SettingsAccessor.getProtectionSettings().loadProtectionSettings();
        //Settings initializer

        //Bases
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: getSQLiteManager().setupTables(); break;
            case MYSQL: getMySQLManager().setupTables(); break;
            case H2: break;
        }
        //Bases

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
        PacketLimiter plCon = new PacketLimiter(this);
        Bukkit.getPluginManager().registerEvents(new ConnectionListener(), this);
        PacketEvents.get().registerListener(new ConnectionListener());
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(plCon), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        PacketEvents.get().registerListener(new PacketCommandsListener());
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
        Bukkit.getPluginManager().registerEvents(new PlayerItemHeldListener(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerTeleportationListener(), this);
        Bukkit.getPluginManager().registerEvents(new SignChangeListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerEditBooksListener(), this);
        PacketEvents.get().registerListener(new LecternCrashListener());
        TaskManager.preformAsyncTimerTask(plCon, 0, getProtectionSettings().getPacketsCheckInterval() * 20L);
        PacketEvents.get().registerListener(new PacketLimiterListener(this, plCon));
        //Listeners registering

        //Console filters
        this.writer.createLogFile();
        this.consoleFilterCore = new L4JFilter();
        getConsoleFilterCore().eventLog();
        getConsoleFilterCore().replaceMessage();
        getConsoleFilterCore().hideMessage();
        //Console filters

        //Packetevents initializing
        PacketEvents.get().init();
        OfflinePlayerRegisterer.loadCached();

        if(getConfigSettings().isApiEnabled()) Incore.setApi(new ApiCore());

        if(getConfigSettings().isCheckForUpdates()) {
            new UpdateChecker(this, UpdateCheckSource.SPIGOT, String.valueOf(107463))
                    .checkEveryXHours(1)
                    .setDonationLink("https://www.donationalerts.com/r/relogg_alis")
                    .setDownloadLink(107463)
                    .setSupportLink("https://vk.com/alphatwo")
                    .checkNow();
        }

    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        PacketEvents.get().terminate();
    }

    private Filter getConsoleFilterCore() {
        return consoleFilterCore;
    }

}
