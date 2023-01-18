 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.manager;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.PacketEvents;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.PacketEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.AbstractPacketListener;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.PacketListenerPriority;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.CancellableEvent;

import java.util.logging.Level;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.Map;

class EventManagerModern
{
    private final Map<Byte, HashSet<AbstractPacketListener>> listenersMap;
    
    EventManagerModern() {
        this.listenersMap = new ConcurrentHashMap<Byte, HashSet<AbstractPacketListener>>();
    }
    
    public void callEvent(final PacketEvent event) {
        byte highestReachedPriority = (byte)(PacketListenerPriority.LOWEST.getId() - 1);
        for (byte priority = PacketListenerPriority.LOWEST.getId(); priority <= PacketListenerPriority.MONITOR.getId(); ++priority) {
            final HashSet<AbstractPacketListener> listeners = this.listenersMap.get(priority);
            if (listeners != null) {
                for (final AbstractPacketListener listener : listeners) {
                    try {
                        event.call(listener);
                    }
                    catch (Exception ex) {
                        PacketEvents.get().getPlugin().getLogger().log(Level.SEVERE, "PacketEvents found an exception while calling a packet listener.", ex);
                    }
                    if (event instanceof CancellableEvent && priority > highestReachedPriority) {
                        highestReachedPriority = priority;
                    }
                }
            }
        }
        PEEventManager.EVENT_MANAGER_LEGACY.callEvent(event, highestReachedPriority);
    }
    
    public synchronized void registerListener(final AbstractPacketListener listener) {
        final byte priority = listener.getPriority().getId();
        HashSet<AbstractPacketListener> listenerSet = this.listenersMap.get(priority);
        if (listenerSet == null) {
            listenerSet = new HashSet<AbstractPacketListener>();
        }
        listenerSet.add(listener);
        this.listenersMap.put(priority, listenerSet);
    }
    
    public synchronized void registerListeners(final AbstractPacketListener... listeners) {
        for (final AbstractPacketListener listener : listeners) {
            this.registerListener(listener);
        }
    }
    
    public synchronized void unregisterListener(final AbstractPacketListener listener) {
        final byte priority = listener.getPriority().getId();
        final HashSet<AbstractPacketListener> listenerSet = this.listenersMap.get(priority);
        if (listenerSet != null) {
            listenerSet.remove(listener);
        }
    }
    
    public synchronized void unregisterListeners(final AbstractPacketListener... listeners) {
        for (final AbstractPacketListener listener : listeners) {
            this.unregisterListener(listener);
        }
    }
    
    public synchronized void unregisterAllListeners() {
        this.listenersMap.clear();
    }
}
