 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.AbstractPacketListener;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.CancellableNMSPacketEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.PlayerEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.PostTaskEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import org.jetbrains.annotations.NotNull;
import org.bukkit.entity.Player;

public final class PacketPlaySendEvent extends CancellableNMSPacketEvent implements PlayerEvent, PostTaskEvent
{
    private final Player player;
    private Runnable postTask;
    
    public PacketPlaySendEvent(final Player player, final Object channel, final NMSPacket packet) {
        super(channel, packet);
        this.player = player;
    }
    
    @NotNull
    @Override
    public Player getPlayer() {
        return this.player;
    }
    
    @Override
    public boolean isPostTaskAvailable() {
        return this.postTask != null;
    }
    
    @Override
    public Runnable getPostTask() {
        return this.postTask;
    }
    
    @Override
    public void setPostTask(@NotNull final Runnable postTask) {
        this.postTask = postTask;
    }
    
    @Override
    public void call(final AbstractPacketListener listener) {
        if (listener.serverSidedPlayAllowance == null || listener.serverSidedPlayAllowance.contains(this.getPacketId())) {
            listener.onPacketPlaySend(this);
        }
    }
}
