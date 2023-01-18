

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.versionlookup.viaversion;

import com.viaversion.viaversion.api.Via;
import org.bukkit.entity.Player;

public class ViaVersionAccessorImpl implements ViaVersionAccessor
{
    @Override
    public int getProtocolVersion(final Player player) {
        return Via.getAPI().getPlayerVersion((Object)player);
    }
}
