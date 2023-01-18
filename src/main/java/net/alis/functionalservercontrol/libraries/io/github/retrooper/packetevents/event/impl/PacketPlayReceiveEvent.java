 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.AbstractPacketListener;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.CancellableNMSPacketEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.PlayerEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.Player;

public final class PacketPlayReceiveEvent extends CancellableNMSPacketEvent implements PlayerEvent
{
    private final Player player;
    
    public PacketPlayReceiveEvent(final Player player, final Object channel, final NMSPacket packet) {
        super(channel, packet);
        this.player = player;
    }
    
    @NotNull
    @Override
    public Player getPlayer() {
        return this.player;
    }
    
    @Override
    public void call(final AbstractPacketListener listener) {
        if (listener.clientSidedPlayAllowance == null || listener.clientSidedPlayAllowance.contains(this.getPacketId())) {
            listener.onPacketPlayReceive(this);
        }
    }
}
