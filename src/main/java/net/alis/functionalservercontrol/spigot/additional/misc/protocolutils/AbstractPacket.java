package net.alis.functionalservercontrol.spigot.additional.misc.protocolutils;

import net.alis.functionalservercontrol.spigot.dependencies.Expansions;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.google.common.base.Objects;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.lang.reflect.InvocationTargetException;
import com.comphenix.protocol.ProtocolLibrary;

import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;

public abstract class AbstractPacket {
    protected PacketContainer handle;

    protected AbstractPacket(PacketContainer handle, PacketType type) {
        if (handle == null) {
            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] AbstractPacket handle must not be null"));
            return;
        }
        if (!Objects.equal(handle.getType(), type)) {
            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] " + handle.getHandle() + " is not a packet of type " + type));
            return;
        }
        this.handle = handle;
    }

    public PacketContainer getHandle() {
        return this.handle;
    }

    public void sendPacket(Player receiver) {
        try {
            Expansions.getProtocolLibManager().getProtocolManager().sendServerPacket(receiver, getHandle());
        } catch (InvocationTargetException e) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl] Failed to send packet. Before reporting this to ALis, check if you have the latest version of ProtocolLib"));
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl] Failed to send packet. Before reporting this to ALis, check if you have the latest version of ProtocolLib"));
        }
    }

    public void broadcastPacket() {
        Expansions.getProtocolLibManager().getProtocolManager().broadcastServerPacket(getHandle());
    }

    @Deprecated
    public void recievePacket(Player sender) {
        try {
            ProtocolLibrary.getProtocolManager().recieveClientPacket(sender,
                    getHandle());
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl] Cannot receive packet. Before reporting this to ALis, check if you have the latest version of ProtocolLib"));
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl] Cannot receive packet. Before reporting this to ALis, check if you have the latest version of ProtocolLib"));
        }
    }

    public void receivePacket(Player sender) {
        try {
            ProtocolLibrary.getProtocolManager().recieveClientPacket(sender,
                    getHandle());
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl] Cannot receive packet. Before reporting this to ALis, check if you have the latest version of ProtocolLib"));
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl] Cannot receive packet. Before reporting this to ALis, check if you have the latest version of ProtocolLib"));
        }
    }
}
