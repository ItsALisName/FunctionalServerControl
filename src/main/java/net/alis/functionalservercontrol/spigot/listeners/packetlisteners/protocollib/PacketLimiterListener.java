package net.alis.functionalservercontrol.spigot.listeners.packetlisteners.protocollib;

import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import net.alis.functionalservercontrol.spigot.additional.tasks.PacketLimiterTask;
import net.alis.functionalservercontrol.spigot.dependencies.Expansions;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getProtectionSettings;

public class PacketLimiterListener {

    FunctionalServerControlSpigot plugin;
    PacketLimiterTask packetLimiterTask;
    public PacketLimiterListener(FunctionalServerControlSpigot plugin, PacketLimiterTask packetLimiterTask) {
        this.plugin = plugin;
        this.packetLimiterTask = packetLimiterTask;
    }

    public void listeningPackets() {
        if(Expansions.getProtocolLibManager().isProtocolLibSetuped()) {
            Expansions.getProtocolLibManager().getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.LOWEST, getPackets(), ListenerOptions.ASYNC) {
                @Override
                public void onPacketReceiving(PacketEvent packetEvent) {
                    if (!getProtectionSettings().isPacketLimiterEnabled()) return;
                    if (!packetEvent.getPacketType().isClient()) return;
                    if (packetEvent.getPacketType() == PacketType.Play.Client.CUSTOM_PAYLOAD) return;
                    if (packetEvent.getPlayer() != null) {
                        Player player = packetEvent.getPlayer();
                        packetLimiterTask.packetMonitoringPlayers(player);
                        if(packetLimiterTask.getBadPlayers().contains(player) && player.hasPermission("functionalservercontrol.protection.packets.bypass")) {
                            packetLimiterTask.getBadPlayers().remove(player);
                        }
                        if (packetLimiterTask.getBadPlayers().contains(player)) {
                            packetEvent.setCancelled(true);
                        }
                    }
                }

                @Override
                public ListeningWhitelist getReceivingWhitelist() {
                    ListeningWhitelist listeningWhitelist = ListeningWhitelist.newBuilder().build();
                    for (PacketType packetType : PacketType.values()) {
                        if (!packetType.isSupported() ||
                                !packetType.isClient())
                            continue;
                        listeningWhitelist.getTypes().add(packetType);
                    }
                    return listeningWhitelist;
                }
            });
        }

    }

    private List<PacketType> getPackets() {
        List<PacketType> packets = new ArrayList<>();
        for (final PacketType type : PacketType.values()) {
            if (type.isClient() && type.getProtocol() == PacketType.Protocol.PLAY && type.isSupported()) {
                packets.add(type);
            }
        }
        return packets;
    }

}
