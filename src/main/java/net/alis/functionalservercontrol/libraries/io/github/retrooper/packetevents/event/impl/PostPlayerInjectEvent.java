 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.PacketEvents;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.PacketEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.AbstractPacketListener;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.PlayerEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.player.ClientVersion;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.netty.channel.ChannelUtils;
import java.net.InetSocketAddress;

import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.Player;

public class PostPlayerInjectEvent extends PacketEvent implements PlayerEvent
{
    private final Player player;
    private final boolean async;
    
    public PostPlayerInjectEvent(final Player player, final boolean async) {
        this.player = player;
        this.async = async;
    }
    
    @NotNull
    @Override
    public Player getPlayer() {
        return this.player;
    }
    
    @NotNull
    public Object getChannel() {
        return PacketEvents.get().getPlayerUtils().getChannel(this.player);
    }
    
    @NotNull
    public InetSocketAddress getSocketAddress() {
        return ChannelUtils.getSocketAddress(this.getChannel());
    }
    
    @NotNull
    public ClientVersion getClientVersion() {
        return PacketEvents.get().getPlayerUtils().getClientVersion(this.player);
    }
    
    public boolean isAsync() {
        return this.async;
    }
    
    @Override
    public void call(final AbstractPacketListener listener) {
        listener.onPostPlayerInject(this);
    }
    
    @Override
    public boolean isInbuilt() {
        return true;
    }
}
