 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.transaction;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packettype.PacketTypeClasses;

public final class WrappedPacketInTransaction extends WrappedPacket
{
    public WrappedPacketInTransaction(final NMSPacket packet) {
        super(packet);
    }
    
    public int getWindowId() {
        return this.readInt(0);
    }
    
    public void setWindowId(final int windowID) {
        this.writeInt(0, windowID);
    }
    
    public short getActionNumber() {
        return this.readShort(0);
    }
    
    public void setActionNumber(final short actionNumber) {
        this.writeShort(0, actionNumber);
    }
    
    public boolean isAccepted() {
        return this.readBoolean(0);
    }
    
    public void setAccepted(final boolean isAccepted) {
        this.writeBoolean(0, isAccepted);
    }
    
    @Override
    public boolean isSupported() {
        return PacketTypeClasses.Play.Client.TRANSACTION != null;
    }
}
