

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.injector.legacy.early;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.PacketEvents;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.injector.legacy.PlayerChannelHandlerLegacy;

import java.util.HashMap;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.list.ListWrapper;
import java.util.Iterator;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.nms.NMSUtils;
import java.util.ArrayList;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.List;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.injector.EarlyInjector;

public class EarlyChannelInjectorLegacy implements EarlyInjector
{
    private final List<ChannelFuture> injectedFutures;
    private final List<Map<Field, Object>> injectedLists;
    
    public EarlyChannelInjectorLegacy() {
        this.injectedFutures = new ArrayList<ChannelFuture>();
        this.injectedLists = new ArrayList<Map<Field, Object>>();
    }
    
    @Override
    public boolean isBound() {
        try {
            final Object connection = NMSUtils.getMinecraftServerConnection();
            if (connection == null) {
                return false;
            }
            for (final Field field : connection.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                final Object value = field.get(connection);
                if (value instanceof List) {
                    synchronized (value) {
                        final Iterator<Object> iterator = (Iterator<Object>)((List)value).iterator();
                        if (iterator.hasNext()) {
                            final Object o = iterator.next();
                            if (o instanceof ChannelFuture) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex) {}
        return false;
    }
    
    @Override
    public void inject() {
        try {
            final Object serverConnection = NMSUtils.getMinecraftServerConnection();
            for (final Field field : serverConnection.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = null;
                try {
                    value = field.get(serverConnection);
                }
                catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (value instanceof List) {
                    final List<?> listWrapper = (List<?>)new ListWrapper((List)value) {
                        @Override
                        public void processAdd(final Object o) {
                            if (o instanceof ChannelFuture) {
                                try {
                                    EarlyChannelInjectorLegacy.this.injectChannelFuture((ChannelFuture)o);
                                }
                                catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    };
                    final HashMap<Field, Object> map = new HashMap<Field, Object>();
                    map.put(field, serverConnection);
                    this.injectedLists.add(map);
                    field.set(serverConnection, listWrapper);
                    synchronized (listWrapper) {
                        for (final Object serverChannel : (List)value) {
                            if (!(serverChannel instanceof ChannelFuture)) {
                                break;
                            }
                            this.injectChannelFuture((ChannelFuture)serverChannel);
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            PacketEvents.get().getPlugin().getLogger().severe("PacketEvents failed to inject!");
            ex.printStackTrace();
        }
        final List<Object> networkManagers = NMSUtils.getNetworkManagers();
        synchronized (networkManagers) {
            for (final Object networkManager : networkManagers) {
                final WrappedPacket networkManagerWrapper = new WrappedPacket(new NMSPacket(networkManager), NMSUtils.networkManagerClass);
                final Channel channel = networkManagerWrapper.readObject(0, (Class<? extends Channel>)NMSUtils.nettyChannelClass);
                if (channel != null) {
                    if (!channel.getClass().equals(NioSocketChannel.class)) {
                        continue;
                    }
                    if (channel.pipeline().get(PacketEvents.get().getHandlerName()) != null) {
                        channel.pipeline().remove(PacketEvents.get().getHandlerName());
                    }
                    if (channel.pipeline().get("packet_handler") == null) {
                        continue;
                    }
                    channel.pipeline().addBefore("packet_handler", PacketEvents.get().getHandlerName(), (ChannelHandler)new PlayerChannelHandlerLegacy());
                }
            }
        }
    }
    
    private void injectChannelFuture(final ChannelFuture channelFuture) {
        final List<String> channelHandlerNames = (List<String>)channelFuture.channel().pipeline().names();
        ChannelHandler bootstrapAcceptor = null;
        Field bootstrapAcceptorField = null;
        for (final String handlerName : channelHandlerNames) {
            final ChannelHandler handler = channelFuture.channel().pipeline().get(handlerName);
            try {
                bootstrapAcceptorField = handler.getClass().getDeclaredField("childHandler");
                bootstrapAcceptorField.setAccessible(true);
                bootstrapAcceptorField.get(handler);
                bootstrapAcceptor = handler;
            }
            catch (Exception ex2) {}
        }
        if (bootstrapAcceptor == null) {
            bootstrapAcceptor = channelFuture.channel().pipeline().first();
        }
        ChannelInitializer<?> oldChannelInitializer = null;
        try {
            oldChannelInitializer = (ChannelInitializer<?>)bootstrapAcceptorField.get(bootstrapAcceptor);
            final ChannelInitializer<?> channelInitializer = new PEChannelInitializerLegacy(oldChannelInitializer);
            bootstrapAcceptorField.setAccessible(true);
            bootstrapAcceptorField.set(bootstrapAcceptor, channelInitializer);
            this.injectedFutures.add(channelFuture);
        }
        catch (IllegalAccessException e3) {
            final ClassLoader cl = bootstrapAcceptor.getClass().getClassLoader();
            if (cl.getClass().getName().equals("org.bukkit.plugin.java.PluginClassLoader")) {
                PluginDescriptionFile yaml = null;
                try {
                    yaml = (PluginDescriptionFile)PluginDescriptionFile.class.getDeclaredField("description").get(cl);
                }
                catch (IllegalAccessException | NoSuchFieldException ex3) {
                    ex3.printStackTrace();
                }
                throw new IllegalStateException("PacketEvents failed to inject, because of " + bootstrapAcceptor.getClass().getName() + ", you might want to try running without " + yaml.getName() + "?");
            }
            throw new IllegalStateException("PacketEvents failed to find core component 'childHandler', please check your plugins. issue: " + bootstrapAcceptor.getClass().getName());
        }
    }
    
    @Override
    public void eject() {
        Field childHandlerField = null;
        for (final ChannelFuture future : this.injectedFutures) {
            final List<String> names = (List<String>)future.channel().pipeline().names();
            ChannelHandler bootstrapAcceptor = null;
            for (final String name : names) {
                try {
                    final ChannelHandler handler = future.channel().pipeline().get(name);
                    if (childHandlerField == null) {
                        childHandlerField = handler.getClass().getDeclaredField("childHandler");
                        childHandlerField.setAccessible(true);
                    }
                    final ChannelInitializer<Channel> oldInit = (ChannelInitializer<Channel>)childHandlerField.get(handler);
                    if (!(oldInit instanceof PEChannelInitializerLegacy)) {
                        continue;
                    }
                    bootstrapAcceptor = handler;
                }
                catch (Exception ex) {}
            }
            if (bootstrapAcceptor == null) {
                bootstrapAcceptor = future.channel().pipeline().first();
            }
            try {
                final ChannelInitializer<Channel> oldInit2 = (ChannelInitializer<Channel>)childHandlerField.get(bootstrapAcceptor);
                if (!(oldInit2 instanceof PEChannelInitializerLegacy)) {
                    continue;
                }
                childHandlerField.setAccessible(true);
                childHandlerField.set(bootstrapAcceptor, ((PEChannelInitializerLegacy)oldInit2).getOldChannelInitializer());
            }
            catch (Exception e) {
                PacketEvents.get().getPlugin().getLogger().severe("PacketEvents failed to eject the injection handler! Please reboot!");
            }
        }
        this.injectedFutures.clear();
        for (final Map<Field, Object> map : this.injectedLists) {
            try {
                for (final Field key : map.keySet()) {
                    key.setAccessible(true);
                    final Object o = map.get(key);
                    if (o instanceof ListWrapper) {
                        key.set(o, ((ListWrapper)o).getOriginalList());
                    }
                }
            }
            catch (IllegalAccessException e2) {
                PacketEvents.get().getPlugin().getLogger().severe("PacketEvents failed to eject the injection handler! Please reboot!!");
            }
        }
        this.injectedLists.clear();
    }
    
    @Override
    public void injectPlayer(final Player player) {
        final Object channel = PacketEvents.get().getPlayerUtils().getChannel(player);
        if (channel != null) {
            this.updatePlayerObject(player, channel);
        }
    }
    
    @Override
    public void ejectPlayer(final Player player) {
        final Object channel = PacketEvents.get().getPlayerUtils().getChannel(player);
        if (channel != null) {
            try {
                ((Channel)channel).pipeline().remove(PacketEvents.get().getHandlerName());
            }
            catch (Exception ex) {}
        }
    }
    
    @Override
    public boolean hasInjected(final Player player) {
        final Object channel = PacketEvents.get().getPlayerUtils().getChannel(player);
        if (channel == null) {
            return false;
        }
        final PlayerChannelHandlerLegacy handler = this.getHandler(channel);
        return handler != null && handler.player != null;
    }
    
    @Override
    public void writePacket(final Object ch, final Object rawNMSPacket) {
        final Channel channel = (Channel)ch;
        channel.write(rawNMSPacket);
    }
    
    @Override
    public void flushPackets(final Object ch) {
        final Channel channel = (Channel)ch;
        channel.flush();
    }
    
    @Override
    public void sendPacket(final Object ch, final Object rawNMSPacket) {
        final Channel channel = (Channel)ch;
        channel.writeAndFlush(rawNMSPacket);
    }
    
    private PlayerChannelHandlerLegacy getHandler(final Object rawChannel) {
        final Channel channel = (Channel)rawChannel;
        final ChannelHandler handler = channel.pipeline().get(PacketEvents.get().getHandlerName());
        if (handler instanceof PlayerChannelHandlerLegacy) {
            return (PlayerChannelHandlerLegacy)handler;
        }
        return null;
    }
    
    @Override
    public void updatePlayerObject(final Player player, final Object rawChannel) {
        final PlayerChannelHandlerLegacy handler = this.getHandler(rawChannel);
        if (handler != null) {
            handler.player = player;
        }
    }
}
