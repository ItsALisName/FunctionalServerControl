

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.keepalive;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.reflection.Reflection;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packettype.PacketTypeClasses;

public final class WrappedPacketInKeepAlive extends WrappedPacket
{
    private static boolean integerPresentInIndex0;
    
    public WrappedPacketInKeepAlive(final NMSPacket packet) {
        super(packet);
    }
    
    @Override
    protected void load() {
        final Class<?> packetClass = PacketTypeClasses.Play.Client.KEEP_ALIVE;
        WrappedPacketInKeepAlive.integerPresentInIndex0 = (Reflection.getField(packetClass, Integer.TYPE, 0) != null);
    }
    
    public long getId() {
        if (!WrappedPacketInKeepAlive.integerPresentInIndex0) {
            return this.readLong(0);
        }
        return this.readInt(0);
    }
    
    public void setId(long id) {
        if (!WrappedPacketInKeepAlive.integerPresentInIndex0) {
            this.writeLong(0, id);
        }
        else {
            if (id > 2147483647L) {
                id = 2147483647L;
            }
            else if (id < -2147483648L) {
                id = -2147483648L;
            }
            this.writeInt(0, (int)id);
        }
    }
}
