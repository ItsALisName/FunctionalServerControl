

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.versionlookup.v_1_7_10;

import java.lang.reflect.InvocationTargetException;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.nms.NMSUtils;
import org.bukkit.entity.Player;
import java.lang.reflect.Method;

public class SpigotVersionLookup_1_7
{
    private static Method getPlayerVersionMethod;
    
    public static int getProtocolVersion(final Player player) {
        if (SpigotVersionLookup_1_7.getPlayerVersionMethod == null) {
            try {
                SpigotVersionLookup_1_7.getPlayerVersionMethod = NMSUtils.networkManagerClass.getMethod("getVersion", (Class<?>[])new Class[0]);
            }
            catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        final Object networkManager = NMSUtils.getNetworkManager(player);
        try {
            return (int)SpigotVersionLookup_1_7.getPlayerVersionMethod.invoke(networkManager, new Object[0]);
        }
        catch (IllegalAccessException | InvocationTargetException ex2) {
            ex2.printStackTrace();
            return -1;
        }
    }
    
    static {
        SpigotVersionLookup_1_7.getPlayerVersionMethod = null;
    }
}
