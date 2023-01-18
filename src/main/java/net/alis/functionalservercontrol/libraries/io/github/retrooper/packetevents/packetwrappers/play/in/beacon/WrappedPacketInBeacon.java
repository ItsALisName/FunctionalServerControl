 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.beacon;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;

public class WrappedPacketInBeacon extends WrappedPacket
{
    public WrappedPacketInBeacon(final NMSPacket packet) {
        super(packet);
    }
    
    public int getPrimaryEffect() {
        return this.readInt(0);
    }
    
    public void setPrimaryEffect(final int primaryEffect) {
        this.writeInt(0, primaryEffect);
    }
    
    public int getSecondaryEffect() {
        return this.readInt(1);
    }
    
    public void setSecondaryEffect(final int secondaryEffect) {
        this.writeInt(1, secondaryEffect);
    }
}
