

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.trsel;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;

public class WrappedPacketInTrSel extends WrappedPacket
{
    public WrappedPacketInTrSel(final NMSPacket packet) {
        super(packet);
    }
    
    public int getSlot() {
        return this.readInt(0);
    }
    
    public void setSlot(final int slot) {
        this.writeInt(0, slot);
    }
}
