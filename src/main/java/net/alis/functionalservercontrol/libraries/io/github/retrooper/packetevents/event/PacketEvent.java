 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.CallableEvent;

public abstract class PacketEvent implements CallableEvent {
    private long timestamp;
    
    public PacketEvent() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public long getTimestamp() {
        return this.timestamp;
    }
    
    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }
    
    public void callPacketEventExternal(final AbstractPacketListener listener) {
        listener.onPacketEventExternal(this);
    }
    
    public boolean isInbuilt() {
        return false;
    }
}
