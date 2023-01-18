 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.versionlookup;

import org.bukkit.entity.Player;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.versionlookup.protocolsupport.ProtocolSupportVersionLookupUtils;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.versionlookup.viaversion.ViaVersionLookupUtils;

public class VersionLookupUtils
{
    public static boolean isDependencyAvailable() {
        return ViaVersionLookupUtils.isAvailable() || ProtocolSupportVersionLookupUtils.isAvailable();
    }
    
    public static int getProtocolVersion(final Player player) {
        if (ViaVersionLookupUtils.isAvailable()) {
            return ViaVersionLookupUtils.getProtocolVersion(player);
        }
        if (ProtocolSupportVersionLookupUtils.isAvailable()) {
            return ProtocolSupportVersionLookupUtils.getProtocolVersion(player);
        }
        return -1;
    }
}
