

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.difficultylock;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.server.ServerVersion;

public final class WrappedPacketInDifficultyLock extends WrappedPacket
{
    public WrappedPacketInDifficultyLock(final NMSPacket packet) {
        super(packet);
    }
    
    public boolean isLocked() {
        return this.readBoolean(0);
    }
    
    public void setLocked(final boolean locked) {
        this.writeBoolean(0, locked);
    }
    
    @Override
    public boolean isSupported() {
        return WrappedPacketInDifficultyLock.version.isNewerThan(ServerVersion.v_1_15_2);
    }
}
