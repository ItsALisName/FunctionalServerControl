

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.guava;

import com.google.common.collect.MapMaker;
import java.util.concurrent.ConcurrentMap;

public class GuavaUtils_7 {
    static <T, K> ConcurrentMap<T, K> makeMap() {
        return new MapMaker().weakValues().makeMap();
    }
}
