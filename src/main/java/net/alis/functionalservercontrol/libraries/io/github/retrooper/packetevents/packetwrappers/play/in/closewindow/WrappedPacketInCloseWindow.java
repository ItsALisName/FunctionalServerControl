 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.closewindow;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;

public class WrappedPacketInCloseWindow extends WrappedPacket
{
    public WrappedPacketInCloseWindow(final NMSPacket packet) {
        super(packet);
    }
    
    public int getWindowId() {
        return this.readInt(0);
    }
    
    public void setWindowId(final int windowID) {
        this.writeInt(0, windowID);
    }
}
