 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.priority.PacketEventPriority;

@Deprecated
public abstract class PacketListenerDynamic extends AbstractPacketListener
{
    @Deprecated
    public PacketListenerDynamic(final PacketEventPriority priority) {
        super(priority);
    }
    
    public PacketListenerDynamic(final PacketListenerPriority priority) {
        super(priority);
    }
    
    public PacketListenerDynamic() {
        super(PacketListenerPriority.NORMAL);
    }
}
