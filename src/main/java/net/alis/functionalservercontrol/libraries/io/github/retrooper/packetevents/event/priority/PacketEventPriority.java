 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.priority;

@Deprecated
public enum PacketEventPriority
{
    LOWEST, 
    LOW, 
    NORMAL, 
    HIGH, 
    HIGHEST, 
    MONITOR;
    
    public static PacketEventPriority getPacketEventPriority(final byte bytePriority) {
        return values()[bytePriority];
    }
    
    public byte getPriorityValue() {
        return (byte)this.ordinal();
    }
}
