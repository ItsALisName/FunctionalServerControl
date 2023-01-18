 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.spectate;

import java.util.UUID;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;

public class WrappedPacketInSpectate extends WrappedPacket
{
    public WrappedPacketInSpectate(final NMSPacket packet) {
        super(packet);
    }
    
    public UUID getUUID() {
        return this.readObject(0, (Class<? extends UUID>)UUID.class);
    }
    
    public void setUUID(final UUID uuid) {
        this.writeObject(0, uuid);
    }
}
