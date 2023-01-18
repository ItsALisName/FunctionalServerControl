 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.tabcomplete;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;

public class WrappedPacketInTabComplete extends WrappedPacket
{
    public WrappedPacketInTabComplete(final NMSPacket packet) {
        super(packet);
    }
    
    public String getText() {
        return this.readString(0);
    }
    
    public void setText(final String text) {
        this.writeString(0, text);
    }
}
