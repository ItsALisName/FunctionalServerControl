

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;

public abstract class CancellableNMSPacketEvent extends NMSPacketEvent implements CancellableEvent
{
    private boolean cancelled;
    
    public CancellableNMSPacketEvent(final Object channel, final NMSPacket packet) {
        super(channel, packet);
    }
    
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    @Override
    public void setCancelled(final boolean value) {
        this.cancelled = value;
    }
}
