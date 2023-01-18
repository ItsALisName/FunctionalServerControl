 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.guava;

import com.google.common.collect.MapMaker;
import java.util.concurrent.ConcurrentMap;

class GuavaUtils_8
{
    static <T, K> ConcurrentMap<T, K> makeMap() {
        return (ConcurrentMap<T, K>)new MapMaker().weakValues().makeMap();
    }
}
