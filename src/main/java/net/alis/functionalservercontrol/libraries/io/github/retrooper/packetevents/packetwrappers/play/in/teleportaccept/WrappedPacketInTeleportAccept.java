

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.teleportaccept;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;

public class WrappedPacketInTeleportAccept extends WrappedPacket
{
    public WrappedPacketInTeleportAccept(final NMSPacket packet) {
        super(packet);
    }
    
    public int getTeleportId() {
        return this.readInt(0);
    }
    
    public void setTeleportId(final int teleportId) {
        this.writeInt(0, teleportId);
    }
}
