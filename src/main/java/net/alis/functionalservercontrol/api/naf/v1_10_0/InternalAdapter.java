package net.alis.functionalservercontrol.api.naf.v1_10_0;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.PacketEvents;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.api.SendableWrapper;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.login.in.custompayload.WrappedPacketLoginInCustomPayload;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.login.in.start.WrappedPacketLoginInStart;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.custompayload.WrappedPacketInCustomPayload;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import static net.alis.functionalservercontrol.spigot.dependencies.Expansions.*;

public class InternalAdapter {

    public static void sendPacketAsync(Player player, SendableWrapper packet) {
        TaskManager.preformAsync(() -> PacketEvents.get().getPlayerUtils().sendPacket(player, packet));
    }

    public static void sendPacketsAsync(Player player, SendableWrapper... packets) {
        TaskManager.preformAsync(() -> {
            for(SendableWrapper packet : packets) sendPacketAsync(player, packet);
        });
    }

    public static int getPlayerProtocolVersion(Player player) {
        try {
            Player.class.getMethod("getProtocolVersion");
            return player.getProtocolVersion();
        } catch (Exception ignored) {
            if(getViaVersionManager().isViaVersionSetuped()) {
                return getViaVersionManager().getViaVersion().getPlayerVersion(player.getUniqueId());
            }
            if(getProtocolLibManager().isProtocolLibSetuped()) {
                return getProtocolLibManager().getProtocolManager().getProtocolVersion(player);
            }
            if(getProtocolSupportManager().isProtocolSupportSetuped()) {
                return ProtocolSupportAPI.getProtocolVersion(player).getId();
            }
            return 0;
        }
    }

    public static String getPlayerClientName(WrappedPacketInCustomPayload packet) {
        try {
            return new DataInputStream(new ByteArrayInputStream(packet.getData())).readLine().trim().toLowerCase();
        } catch (IOException e) {
            return "illegal";
        }
    }

    public static int getPlayerPing(Player player) {
        try {
            Player.class.getMethod("getPing");
            return player.getPing();
        } catch (NoSuchMethodException e) {
            return PacketEvents.get().getPlayerUtils().getPing(player);
        }
    }



}
