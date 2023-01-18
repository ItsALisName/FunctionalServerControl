

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.AbstractPacketListener;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.CancellableNMSPacketEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;

public class PacketStatusReceiveEvent extends CancellableNMSPacketEvent
{
    public PacketStatusReceiveEvent(final Object channel, final NMSPacket packet) {
        super(channel, packet);
    }
    
    @Override
    public void call(final AbstractPacketListener listener) {
        if (listener.clientSidedStatusAllowance == null || listener.clientSidedStatusAllowance.contains(this.getPacketId())) {
            listener.onPacketStatusReceive(this);
        }
    }
}
