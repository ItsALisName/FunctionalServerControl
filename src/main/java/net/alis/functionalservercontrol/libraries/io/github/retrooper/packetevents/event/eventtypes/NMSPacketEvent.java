

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.eventtypes;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packettype.PacketType;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.reflection.ClassUtil;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.netty.channel.ChannelUtils;

import java.net.InetSocketAddress;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.PacketEvent;

public abstract class NMSPacketEvent extends PacketEvent implements CallableEvent
{
    private final Object channel;
    private final InetSocketAddress socketAddress;
    private final byte packetID;
    protected NMSPacket packet;
    
    public NMSPacketEvent(final Object channel, final NMSPacket packet) {
        this.channel = channel;
        this.socketAddress = ChannelUtils.getSocketAddress(channel);
        this.packet = packet;
        this.packetID = PacketType.packetIDMap.getOrDefault(packet.getRawNMSPacket().getClass(), (byte) -128);
    }
    
    public final InetSocketAddress getSocketAddress() {
        return this.socketAddress;
    }
    
    public Object getChannel() {
        return this.channel;
    }
    
    @Deprecated
    public final String getPacketName() {
        return ClassUtil.getClassSimpleName(this.packet.getRawNMSPacket().getClass());
    }
    
    public final NMSPacket getNMSPacket() {
        return this.packet;
    }
    
    public final void setNMSPacket(final NMSPacket packet) {
        this.packet = packet;
    }
    
    public byte getPacketId() {
        return this.packetID;
    }
    
    @Override
    public boolean isInbuilt() {
        return true;
    }
}
