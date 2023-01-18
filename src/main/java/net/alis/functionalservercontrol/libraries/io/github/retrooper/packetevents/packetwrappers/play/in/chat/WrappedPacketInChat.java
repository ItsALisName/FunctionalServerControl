 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.chat;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;

public final class WrappedPacketInChat extends WrappedPacket
{
    public WrappedPacketInChat(final NMSPacket packet) {
        super(packet);
    }
    
    public String getMessage() {
        return this.readString(0);
    }
    
    public void setMessage(final String message) {
        this.writeString(0, message);
    }
}
