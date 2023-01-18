 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.AbstractPacketListener;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.CancellableNMSPacketEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;

public class PacketLoginReceiveEvent extends CancellableNMSPacketEvent
{
    public PacketLoginReceiveEvent(final Object channel, final NMSPacket packet) {
        super(channel, packet);
    }
    
    @Override
    public void call(final AbstractPacketListener listener) {
        if (listener.clientSidedLoginAllowance == null || listener.clientSidedLoginAllowance.contains(this.getPacketId())) {
            listener.onPacketLoginReceive(this);
        }
    }
}
