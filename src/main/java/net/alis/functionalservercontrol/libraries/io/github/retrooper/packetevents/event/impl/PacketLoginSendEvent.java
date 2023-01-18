

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.AbstractPacketListener;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.CancellableNMSPacketEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.PostTaskEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import org.jetbrains.annotations.NotNull;

public class PacketLoginSendEvent extends CancellableNMSPacketEvent implements PostTaskEvent
{
    private Runnable postTask;
    
    public PacketLoginSendEvent(final Object channel, final NMSPacket packet) {
        super(channel, packet);
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
        if (listener.serverSidedLoginAllowance == null || listener.serverSidedLoginAllowance.contains(this.getPacketId())) {
            listener.onPacketLoginSend(this);
        }
    }
}
