 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.itemname;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;

public class WrappedPacketInItemName extends WrappedPacket
{
    public WrappedPacketInItemName(final NMSPacket packet) {
        super(packet);
    }
    
    public String getItemName() {
        return this.readString(0);
    }
    
    public void setItemName(final String itemName) {
        this.writeString(0, itemName);
    }
}
