

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.pong;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.server.ServerVersion;

public class WrappedPacketInPong extends WrappedPacket
{
    private int id;
    
    public WrappedPacketInPong(final NMSPacket packet) {
        super(packet);
    }
    
    public WrappedPacketInPong(final int id) {
        this.id = id;
    }
    
    public int getId() {
        if (this.packet != null) {
            return this.readInt(0);
        }
        return this.id;
    }
    
    public void setId(final int id) {
        if (this.packet != null) {
            this.writeInt(0, id);
        }
        else {
            this.id = id;
        }
    }
    
    @Override
    public boolean isSupported() {
        return WrappedPacketInPong.version.isNewerThanOrEquals(ServerVersion.v_1_17);
    }
}
