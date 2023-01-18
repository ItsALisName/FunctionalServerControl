 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes;

public interface CancellableEvent
{
    boolean isCancelled();
    
    void setCancelled(final boolean p0);
}
