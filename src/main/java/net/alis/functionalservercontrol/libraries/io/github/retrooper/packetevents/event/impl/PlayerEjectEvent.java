 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.PacketEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.AbstractPacketListener;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.CancellableEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.Player;

public final class PlayerEjectEvent extends PacketEvent implements CancellableEvent, PlayerEvent
{
    private final Player player;
    private boolean cancelled;
    
    public PlayerEjectEvent(final Player player) {
        this.player = player;
    }
    
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    @Override
    public void setCancelled(final boolean value) {
        this.cancelled = value;
    }
    
    @NotNull
    @Override
    public Player getPlayer() {
        return this.player;
    }
    
    @Deprecated
    public boolean isAsync() {
        return false;
    }
    
    @Override
    public void call(final AbstractPacketListener listener) {
        listener.onPlayerEject(this);
    }
    
    @Override
    public boolean isInbuilt() {
        return true;
    }
}
