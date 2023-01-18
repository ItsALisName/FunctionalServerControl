 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.immutableset;

import java.util.List;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.PacketEvents;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.server.ServerVersion;

public class ImmutableSetCustom<T>
{
    private final ImmutableSetAbstract<T> immutableSetAbstract;
    
    public ImmutableSetCustom() {
        if (PacketEvents.get().getServerUtils().getVersion().isOlderThan(ServerVersion.v_1_8)) {
            this.immutableSetAbstract = new ImmutableSet_7<T>();
        }
        else {
            this.immutableSetAbstract = new ImmutableSet_8<T>();
        }
    }
    
    public ImmutableSetCustom(final List<T> data) {
        if (PacketEvents.get().getServerUtils().getVersion().isOlderThan(ServerVersion.v_1_8)) {
            this.immutableSetAbstract = new ImmutableSet_7<T>(data);
        }
        else {
            this.immutableSetAbstract = new ImmutableSet_8<T>(data);
        }
    }
    
    @SafeVarargs
    public ImmutableSetCustom(final T... data) {
        if (PacketEvents.get().getServerUtils().getVersion().isOlderThan(ServerVersion.v_1_8)) {
            this.immutableSetAbstract = new ImmutableSet_7<T>(data);
        }
        else {
            this.immutableSetAbstract = new ImmutableSet_8<T>(data);
        }
    }
    
    public boolean contains(final T element) {
        return this.immutableSetAbstract.contains(element);
    }
    
    public void add(final T element) {
        this.immutableSetAbstract.add(element);
    }
    
    @SafeVarargs
    public final void addAll(final T... elements) {
        this.immutableSetAbstract.addAll(elements);
    }
}
