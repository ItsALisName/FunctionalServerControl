

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.AbstractPacketListener;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.CancellableNMSPacketEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;

public class PacketHandshakeReceiveEvent extends CancellableNMSPacketEvent
{
    public PacketHandshakeReceiveEvent(final Object channel, final NMSPacket packet) {
        super(channel, packet);
    }
    
    @Override
    public void call(final AbstractPacketListener listener) {
        if (listener.clientSidedLoginAllowance == null || listener.clientSidedLoginAllowance.contains(this.getPacketId())) {
            listener.onPacketHandshakeReceive(this);
        }
    }
}
