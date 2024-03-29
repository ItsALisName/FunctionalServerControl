

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.boatmove;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.server.ServerVersion;

public final class WrappedPacketInBoatMove extends WrappedPacket
{
    public WrappedPacketInBoatMove(final NMSPacket packet) {
        super(packet);
    }
    
    public boolean getLeftPaddle() {
        return this.readBoolean(0);
    }
    
    public void setLeftPaddle(final boolean turning) {
        this.writeBoolean(0, turning);
    }
    
    public boolean getRightPaddle() {
        return this.readBoolean(1);
    }
    
    public void setRightPaddle(final boolean turning) {
        this.writeBoolean(1, turning);
    }
    
    @Override
    public boolean isSupported() {
        return WrappedPacketInBoatMove.version.isNewerThan(ServerVersion.v_1_8);
    }
}
