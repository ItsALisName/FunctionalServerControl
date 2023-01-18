 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.injector;

import org.bukkit.entity.Player;

public interface EarlyInjector extends ChannelInjector
{
    void updatePlayerObject(final Player p0, final Object p1);
}
