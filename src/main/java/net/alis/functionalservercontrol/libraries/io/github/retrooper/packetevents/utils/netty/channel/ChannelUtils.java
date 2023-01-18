 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.netty.channel;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.PacketEvents;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.server.ServerVersion;

import java.net.InetSocketAddress;

public final class ChannelUtils
{
    public static InetSocketAddress getSocketAddress(final Object ch) {
        if (ch == null) {
            return null;
        }
        if (PacketEvents.get().getServerUtils().getVersion() == ServerVersion.v_1_7_10) {
            return ChannelUtils7.getSocketAddress(ch);
        }
        return ChannelUtils8.getSocketAddress(ch);
    }
}
