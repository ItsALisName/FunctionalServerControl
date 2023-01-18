

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.custompayload;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.PacketEvents;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.nms.NMSUtils;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.reflection.Reflection;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packettype.PacketTypeClasses;

public final class WrappedPacketInCustomPayload extends WrappedPacket
{
    private static boolean strPresent;
    private static boolean byteArrayPresent;
    
    public WrappedPacketInCustomPayload(final NMSPacket packet) {
        super(packet);
    }
    
    @Override
    protected void load() {
        WrappedPacketInCustomPayload.strPresent = (Reflection.getField(PacketTypeClasses.Play.Client.CUSTOM_PAYLOAD, String.class, 0) != null);
        WrappedPacketInCustomPayload.byteArrayPresent = (Reflection.getField(PacketTypeClasses.Play.Client.CUSTOM_PAYLOAD, byte[].class, 0) != null);
    }
    
    public String getChannelName() {
        if (WrappedPacketInCustomPayload.strPresent) {
            return this.readString(0);
        }
        return this.readMinecraftKey(1);
    }
    
    public void setChannelName(final String channelName) {
        if (WrappedPacketInCustomPayload.strPresent) {
            this.writeString(0, channelName);
        }
        else {
            this.writeMinecraftKey(1, channelName);
        }
    }
    
    public byte[] getData() {
        if (WrappedPacketInCustomPayload.byteArrayPresent) {
            return this.readByteArray(0);
        }
        return PacketEvents.get().getByteBufUtil().getBytes(this.getBuffer());
    }
    
    public void setData(final byte[] data) {
        if (WrappedPacketInCustomPayload.byteArrayPresent) {
            this.writeByteArray(0, data);
        }
        else {
            PacketEvents.get().getByteBufUtil().setBytes(this.getBuffer(), data);
        }
    }
    
    private Object getBuffer() {
        final Object dataSerializer = this.readObject(0, NMSUtils.packetDataSerializerClass);
        final WrappedPacket dataSerializerWrapper = new WrappedPacket(new NMSPacket(dataSerializer));
        return dataSerializerWrapper.readObject(0, NMSUtils.byteBufClass);
    }
    
    public void retain() {
        if (this.packet != null && !WrappedPacketInCustomPayload.byteArrayPresent) {
            PacketEvents.get().getByteBufUtil().retain(this.getBuffer());
        }
    }
    
    public void release() {
        if (this.packet != null && !WrappedPacketInCustomPayload.byteArrayPresent) {
            PacketEvents.get().getByteBufUtil().release(this.getBuffer());
        }
    }
}
