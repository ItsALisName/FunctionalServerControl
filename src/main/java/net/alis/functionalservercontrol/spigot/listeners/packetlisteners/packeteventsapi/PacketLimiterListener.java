package net.alis.functionalservercontrol.spigot.listeners.packetlisteners.packeteventsapi;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.AbstractPacketListener;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.PacketListenerPriority;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packettype.PacketType;
import net.alis.functionalservercontrol.spigot.FunctionalServerControl;
import net.alis.functionalservercontrol.spigot.additional.tasks.PacketLimiterTask;
import org.bukkit.entity.Player;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getProtectionSettings;

public class PacketLimiterListener extends AbstractPacketListener {

    FunctionalServerControl plugin;
    PacketLimiterTask packetLimiterTask;
    public PacketLimiterListener(FunctionalServerControl plugin, PacketLimiterTask packetLimiterTask) {
        super(PacketListenerPriority.LOW);
        this.plugin = plugin;
        this.packetLimiterTask = packetLimiterTask;
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (!getProtectionSettings().isPacketLimiterEnabled()) return;
        if(event.getPacketId() == PacketType.Play.Client.CUSTOM_PAYLOAD) return;
        Player player = event.getPlayer();
        packetLimiterTask.packetMonitoringPlayers(player);
        if(packetLimiterTask.getBadPlayers().contains(player) && player.hasPermission("functionalservercontrol.protection.packets.bypass")) {
            packetLimiterTask.getBadPlayers().remove(player);
        }
        if (packetLimiterTask.getBadPlayers().contains(player)) {
            event.setCancelled(true);
        }
    }
}
