 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.status.in.ping;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;

public class WrappedPacketStatusPing extends WrappedPacket
{
    public WrappedPacketStatusPing(final NMSPacket packet) {
        super(packet);
    }
    
    public long getPayload() {
        return this.readLong(0);
    }
    
    public void setPayload(final long payload) {
        this.writeLong(0, payload);
    }
}
