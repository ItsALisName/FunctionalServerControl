

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.AbstractPacketListener;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.NMSPacketEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes.PlayerEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import org.bukkit.entity.Player;

public class PostPacketPlaySendEvent extends NMSPacketEvent implements PlayerEvent
{
    private final Player player;
    
    public PostPacketPlaySendEvent(final Player player, final Object channel, final NMSPacket packet) {
        super(channel, packet);
        this.player = player;
    }
    
    @Override
    public Player getPlayer() {
        return this.player;
    }
    
    @Override
    public void call(final AbstractPacketListener listener) {
        if (listener.serverSidedPlayAllowance == null || listener.serverSidedPlayAllowance.contains(this.getPacketId())) {
            listener.onPostPacketPlaySend(this);
        }
    }
}
