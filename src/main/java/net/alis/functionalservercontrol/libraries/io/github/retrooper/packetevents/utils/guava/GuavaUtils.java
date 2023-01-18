 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.guava;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.PacketEvents;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.server.ServerVersion;

import java.util.concurrent.ConcurrentMap;

public class GuavaUtils
{
    public static <T, K> ConcurrentMap<T, K> makeMap() {
        if (PacketEvents.get().getServerUtils().getVersion().isNewerThan(ServerVersion.v_1_7_10)) {
            return GuavaUtils_8.makeMap();
        }
        return GuavaUtils_7.makeMap();
    }
}
