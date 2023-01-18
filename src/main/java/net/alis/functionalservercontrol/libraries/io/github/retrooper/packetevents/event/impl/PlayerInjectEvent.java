

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.PacketEvents;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.PacketEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.AbstractPacketListener;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.CancellableEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.PlayerEvent;
import org.jetbrains.annotations.Nullable;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.netty.channel.ChannelUtils;

import java.net.InetSocketAddress;
import org.bukkit.entity.Player;

public final class PlayerInjectEvent extends PacketEvent implements CancellableEvent, PlayerEvent
{
    private final Player player;
    private final InetSocketAddress address;
    private boolean cancelled;
    
    public PlayerInjectEvent(final Player player) {
        this.player = player;
        this.address = ChannelUtils.getSocketAddress(PacketEvents.get().getPlayerUtils().getChannel(player));
    }
    
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }
    
    @Override
    public void setCancelled(final boolean value) {
        this.cancelled = value;
    }
    
    @Nullable
    @Override
    public Player getPlayer() {
        return this.player;
    }
    
    @Nullable
    public InetSocketAddress getSocketAddress() {
        return this.address;
    }
    
    @Deprecated
    public boolean isAsync() {
        return false;
    }
    
    @Override
    public void call(final AbstractPacketListener listener) {
        listener.onPlayerInject(this);
    }
    
    @Override
    public boolean isInbuilt() {
        return true;
    }
}
