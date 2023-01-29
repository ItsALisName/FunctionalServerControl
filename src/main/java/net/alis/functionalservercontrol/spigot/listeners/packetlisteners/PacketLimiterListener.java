package net.alis.functionalservercontrol.spigot.listeners.packetlisteners;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.AbstractPacketListener;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.PacketListenerPriority;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packettype.PacketType;
import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.spigot.additional.tasks.PacketLimiter;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getProtectionSettings;

public class PacketLimiterListener extends AbstractPacketListener {

    FunctionalServerControlSpigot plugin;
    PacketLimiter packetLimiterTask;
    public PacketLimiterListener(FunctionalServerControlSpigot plugin, PacketLimiter packetLimiterTask) {
        super(PacketListenerPriority.LOW);
        this.plugin = plugin;
        this.packetLimiterTask = packetLimiterTask;
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (!getProtectionSettings().isPacketLimiterEnabled()) return;
        if(event.getPacketId() == PacketType.Play.Client.CUSTOM_PAYLOAD) return;
        FunctionalPlayer player = FunctionalPlayer.get(event.getPlayer().getName());
        packetLimiterTask.update(player.getFunctionalId());
        if(packetLimiterTask.getBadPlayers().contains(player.getFunctionalId()) && player.hasPermission("functionalservercontrol.protection.packets.bypass")) {
            packetLimiterTask.getBadPlayers().remove(player.getFunctionalId());
        }
        if (packetLimiterTask.getBadPlayers().contains(player.getFunctionalId())) {
            event.setCancelled(true);
        }
    }
}
