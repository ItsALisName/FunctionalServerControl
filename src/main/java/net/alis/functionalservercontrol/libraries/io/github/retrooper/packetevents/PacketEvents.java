package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl.PostPlayerInjectEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.manager.EventManager;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.manager.PEEventManager;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.exceptions.PacketEventsLoadFailureException;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.injector.GlobalChannelInjector;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packettype.PacketType;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packettype.PacketTypeClasses;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.out.entityequipment.WrappedPacketOutEntityEquipment;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.processor.BukkitEventProcessorInternal;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.processor.PacketProcessorInternal;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.settings.PacketEventsSettings;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.entityfinder.EntityFinderUtils;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.guava.GuavaUtils;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.netty.bytebuf.ByteBufUtil;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.netty.bytebuf.ByteBufUtil_7;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.netty.bytebuf.ByteBufUtil_8;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.nms.NMSUtils;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.player.PlayerUtils;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.server.ServerUtils;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.server.ServerVersion;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.version.PEVersion;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;

public final class PacketEvents implements Listener, EventManager {
    private static PacketEvents instance;
    private static Plugin plugin;
    private final PEVersion version = new PEVersion(1, 8, 4);
    private final EventManager eventManager = new PEEventManager();
    private final PlayerUtils playerUtils = new PlayerUtils();
    private final ServerUtils serverUtils = new ServerUtils();
    private final PacketProcessorInternal packetProcessorInternal = new PacketProcessorInternal();
    private final BukkitEventProcessorInternal bukkitEventProcessorInternal = new BukkitEventProcessorInternal();
    private final GlobalChannelInjector injector = new GlobalChannelInjector();
    private final AtomicBoolean injectorReady = new AtomicBoolean();
    private String handlerName;
    private PacketEventsSettings settings = new PacketEventsSettings();
    private ByteBufUtil byteBufUtil;
    private volatile boolean loading, loaded;
    private boolean initialized, initializing, terminating;
    private boolean lateBind = false;

    public static PacketEvents create(final Plugin plugin) {
        if (Bukkit.isPrimaryThread()) {
            //We are on the main thread
            if (!Bukkit.getServicesManager().isProvidedFor(PacketEvents.class)) {
                instance = new PacketEvents();
                Bukkit.getServicesManager().register(PacketEvents.class, instance,
                        plugin, ServicePriority.Normal);
                PacketEvents.plugin = plugin;
                return instance;
            } else {
                return instance = Bukkit.getServicesManager().load(PacketEvents.class);
            }
        } else {
            if (instance == null) {
                PacketEvents.plugin = plugin;
                instance = new PacketEvents();
            }
            return instance;
        }
    }

    public static PacketEvents get() {
        return instance;
    }

    @Deprecated
    public static PacketEvents getAPI() {
        return instance;
    }


    public void load() {
        if (!loaded && !loading) {
            loading = true;
            ServerVersion version = ServerVersion.getVersion();
            WrappedPacket.version = version;
            NMSUtils.version = version;
            EntityFinderUtils.version = version;
            handlerName = "pe-" + plugin.getName();
            try {
                NMSUtils.load();

                PacketTypeClasses.load();

                PacketType.load();

                EntityFinderUtils.load();

                getServerUtils().entityCache = GuavaUtils.makeMap();

                if (version.isNewerThanOrEquals(ServerVersion.v_1_9)) {
                    for (WrappedPacketOutEntityEquipment.EquipmentSlot slot : WrappedPacketOutEntityEquipment.EquipmentSlot.values()) {
                        slot.id = (byte) slot.ordinal();
                    }
                } else {
                    WrappedPacketOutEntityEquipment.EquipmentSlot.MAINHAND.id = 0;
                    WrappedPacketOutEntityEquipment.EquipmentSlot.OFFHAND.id = -1; //Invalid
                    WrappedPacketOutEntityEquipment.EquipmentSlot.BOOTS.id = 1;
                    WrappedPacketOutEntityEquipment.EquipmentSlot.LEGGINGS.id = 2;
                    WrappedPacketOutEntityEquipment.EquipmentSlot.CHESTPLATE.id = 3;
                    WrappedPacketOutEntityEquipment.EquipmentSlot.HELMET.id = 4;
                }
            } catch (Exception ex) {
                loading = false;
                throw new PacketEventsLoadFailureException(ex);
            }

            byteBufUtil = NMSUtils.legacyNettyImportMode ? new ByteBufUtil_7() : new ByteBufUtil_8();
            if (!injectorReady.get()) {
                injector.load();
                lateBind = !injector.isBound();
                if (!lateBind) {
                    injector.inject();
                }
                injectorReady.set(true);
            }

            loaded = true;
            loading = false;
        }
    }

    @Deprecated
    public void loadAsyncNewThread() {
        Bukkit.getConsoleSender().sendMessage(setColors("&3[FunctionalServerControl | PacketEvents] &3&oNew async thread loaded!"));
        new Thread(this::load).start();
    }

    @Deprecated
    public void loadAsync(ExecutorService executorService) {
        executorService.execute(this::load);
    }

    public void loadSettings(PacketEventsSettings settings) {
        this.settings = settings;
    }

    public void init() {
        init(getSettings());
    }

    public void init(PacketEventsSettings packetEventsSettings) {
        load();
        if (!initialized && !initializing) {
            initializing = true;
            settings = packetEventsSettings;
            settings.lock();

            while (!injectorReady.get()) {}

            Runnable postInjectTask = () -> {
                Bukkit.getPluginManager().registerEvents(bukkitEventProcessorInternal, plugin);
                for (final Player p : Bukkit.getOnlinePlayers()) {
                    try {
                        injector.injectPlayer(p);
                        getEventManager().callEvent(new PostPlayerInjectEvent(p, false));
                    } catch (Exception ex) {
                        p.kickPlayer(setColors("&6[FunctionalServerControl <-> PacketEvents] \n&eFailed to inject your... Please rejoin!"));
                    }
                }
            };

            if (lateBind) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, injector::inject);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, postInjectTask);
            } else {
                postInjectTask.run();
            }

            initialized = true;
            initializing = false;
        }
    }

    @Deprecated
    public void init(Plugin plugin) {
        init(plugin, settings);
    }

    @Deprecated
    public void init(Plugin plugin, PacketEventsSettings packetEventsSettings) {
        init(packetEventsSettings);
    }

    public void terminate() {
        if (initialized && !terminating) {
            //Eject all players
            for (Player p : Bukkit.getOnlinePlayers()) {
                injector.ejectPlayer(p);
            }
            //Eject the injector if needed
            injector.eject();
            //Unregister all our listeners
            getEventManager().unregisterAllListeners();
            initialized = false;
            terminating = false;
        }
    }

    @Deprecated
    public void stop() {
        terminate();
    }

    public boolean isLoading() {
        return loading;
    }

    public boolean hasLoaded() {
        return loaded;
    }

    public boolean isTerminating() {
        return terminating;
    }

    @Deprecated
    public boolean isStopping() {
        return isTerminating();
    }

    public boolean isInitializing() {
        return initializing;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public GlobalChannelInjector getInjector() {
        return injector;
    }

    public PacketProcessorInternal getInternalPacketProcessor() {
        return packetProcessorInternal;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public PacketEventsSettings getSettings() {
        return settings;
    }

    public PEVersion getVersion() {
        return version;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public PlayerUtils getPlayerUtils() {
        return playerUtils;
    }

    public ServerUtils getServerUtils() {
        return serverUtils;
    }

    public ByteBufUtil getByteBufUtil() {
        return byteBufUtil;
    }

}