 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.versionlookup.viaversion;

import java.lang.reflect.InvocationTargetException;
import org.bukkit.entity.Player;
import java.lang.reflect.Method;

public class ViaVersionAccessorImplLegacy implements ViaVersionAccessor
{
    private static Class<?> viaClass;
    private static Method apiAccessor;
    private static Method getPlayerVersionMethod;
    
    @Override
    public int getProtocolVersion(final Player player) {
        if (ViaVersionAccessorImplLegacy.viaClass == null) {
            try {
                ViaVersionAccessorImplLegacy.viaClass = Class.forName("us.myles.ViaVersion.api.Via");
                final Class<?> viaAPIClass = Class.forName("us.myles.ViaVersion.api.ViaAPI");
                ViaVersionAccessorImplLegacy.apiAccessor = ViaVersionAccessorImplLegacy.viaClass.getMethod("getAPI");
                ViaVersionAccessorImplLegacy.getPlayerVersionMethod = viaAPIClass.getMethod("getPlayerVersion", Object.class);
            }
            catch (ClassNotFoundException | NoSuchMethodException ex3) {
                ex3.printStackTrace();
            }
        }
        try {
            final Object viaAPI = ViaVersionAccessorImplLegacy.apiAccessor.invoke(null, new Object[0]);
            return (int)ViaVersionAccessorImplLegacy.getPlayerVersionMethod.invoke(viaAPI, player);
        }
        catch (IllegalAccessException | InvocationTargetException ex4) {
            ex4.printStackTrace();
            return -1;
        }
    }
}
