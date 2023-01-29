package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.processor;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.PacketEvents;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl.PostPlayerInjectEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.player.ClientVersion;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.versionlookup.VersionLookupUtils;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.InetSocketAddress;
import java.util.UUID;

public class BukkitEventProcessorInternal implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        InetSocketAddress address = player.getAddress();
        boolean shouldInject = PacketEvents.get().getSettings().shouldUseCompatibilityInjector() || !(PacketEvents.get().getInjector().hasInjected(e.getPlayer()));
        if (shouldInject) {
            PacketEvents.get().getInjector().injectPlayer(player);
        }

        boolean dependencyAvailable = VersionLookupUtils.isDependencyAvailable();
        PacketEvents.get().getPlayerUtils().loginTime.put(player.getUniqueId(), System.currentTimeMillis());
        if (dependencyAvailable) {
            TaskManager.preformAsyncLater(() -> {
                try {
                    int protocolVersion = VersionLookupUtils.getProtocolVersion(player);
                    ClientVersion version = ClientVersion.getClientVersion(protocolVersion);
                    PacketEvents.get().getPlayerUtils().clientVersionsMap.put(address, version);
                } catch (Exception ignored) {

                }
                PacketEvents.get().getEventManager().callEvent(new PostPlayerInjectEvent(player, true));
            }, 1L);
        } else {
            PacketEvents.get().getEventManager().callEvent(new PostPlayerInjectEvent(e.getPlayer(), false));
        }
        PacketEvents.get().getServerUtils().entityCache.putIfAbsent(e.getPlayer().getEntityId(), e.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        InetSocketAddress address = player.getAddress();
        PacketEvents.get().getPlayerUtils().loginTime.remove(uuid);
        PacketEvents.get().getPlayerUtils().playerPingMap.remove(uuid);
        PacketEvents.get().getPlayerUtils().playerSmoothedPingMap.remove(uuid);
        PacketEvents.get().getPlayerUtils().clientVersionsMap.remove(address);
        PacketEvents.get().getPlayerUtils().tempClientVersionMap.remove(address);
        PacketEvents.get().getPlayerUtils().keepAliveMap.remove(uuid);
        PacketEvents.get().getPlayerUtils().channels.remove(player.getName());
        PacketEvents.get().getServerUtils().entityCache.remove(e.getPlayer().getEntityId());
    }


    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        Entity entity = event.getEntity();
        PacketEvents.get().getServerUtils().entityCache.putIfAbsent(entity.getEntityId(), entity);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        PacketEvents.get().getServerUtils().entityCache.remove(event.getEntity().getEntityId());
    }
}