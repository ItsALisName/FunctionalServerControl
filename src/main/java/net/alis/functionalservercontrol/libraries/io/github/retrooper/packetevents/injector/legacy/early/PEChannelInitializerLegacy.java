

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.injector.legacy.early;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.PacketEvents;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.injector.legacy.PlayerChannelHandlerLegacy;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.reflection.Reflection;
import org.bukkit.Bukkit;

import java.lang.reflect.Method;

import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;

public class PEChannelInitializerLegacy extends ChannelInitializer<Channel>
{
    private final ChannelInitializer<?> oldChannelInitializer;
    private Method initChannelMethod;
    
    public PEChannelInitializerLegacy(final ChannelInitializer<?> oldChannelInitializer) {
        this.oldChannelInitializer = oldChannelInitializer;
        this.load();
    }
    
    private void load() {
        this.initChannelMethod = Reflection.getMethod(this.oldChannelInitializer.getClass(), "initChannel", 0);
    }
    
    public ChannelInitializer<?> getOldChannelInitializer() {
        return this.oldChannelInitializer;
    }
    
    protected void initChannel(final Channel channel) throws Exception {
        this.initChannelMethod.invoke(this.oldChannelInitializer, channel);
        final PlayerChannelHandlerLegacy channelHandler = new PlayerChannelHandlerLegacy();
        if (channel.getClass().equals(NioSocketChannel.class) && channel.pipeline().get("packet_handler") != null) {
            final String handlerName = PacketEvents.get().getHandlerName();
            if (channel.pipeline().get(handlerName) != null) {
                Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl | PacketEvents] Attempted to initialize a channel twice!"));
            }
            else {
                channel.pipeline().addBefore("packet_handler", handlerName, channelHandler);
            }
        }
    }
}
