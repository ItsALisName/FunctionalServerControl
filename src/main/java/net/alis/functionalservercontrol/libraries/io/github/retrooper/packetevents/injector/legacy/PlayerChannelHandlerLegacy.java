

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.injector.legacy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.PacketEvents;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.processor.PacketProcessorInternal;
import org.bukkit.entity.Player;

@ChannelHandler.Sharable
public class PlayerChannelHandlerLegacy extends ChannelDuplexHandler
{
    public volatile Player player;
    
    public void channelRead(final ChannelHandlerContext ctx, final Object packet) throws Exception {
        final PacketProcessorInternal.PacketData data = PacketEvents.get().getInternalPacketProcessor().read(this.player, ctx.channel(), packet);
        if (data.packet != null) {
            super.channelRead(ctx, data.packet);
            PacketEvents.get().getInternalPacketProcessor().postRead(this.player, ctx.channel(), data.packet);
        }
    }
    
    public void write(final ChannelHandlerContext ctx, final Object packet, final ChannelPromise promise) throws Exception {
        if (packet instanceof ByteBuf) {
            super.write(ctx, packet, promise);
            return;
        }
        final PacketProcessorInternal.PacketData data = PacketEvents.get().getInternalPacketProcessor().write(this.player, ctx.channel(), packet);
        if (data.postAction != null) {
            promise.addListener(f -> data.postAction.run());
        }
        if (data.packet != null) {
            super.write(ctx, data.packet, promise);
            PacketEvents.get().getInternalPacketProcessor().postWrite(this.player, ctx.channel(), data.packet);
        }
    }
}
