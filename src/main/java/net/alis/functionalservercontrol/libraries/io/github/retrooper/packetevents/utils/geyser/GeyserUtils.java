 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.geyser;

import java.lang.reflect.InvocationTargetException;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.reflection.Reflection;

import java.util.UUID;
import java.lang.reflect.Method;

public class GeyserUtils
{
    private static Class<?> GEYSER_CLASS;
    private static Class<?> GEYSER_API_CLASS;
    private static Method GEYSER_API_METHOD;
    private static Method CONNECTION_BY_UUID_METHOD;
    
    public static boolean isGeyserPlayer(final UUID uuid) {
        if (GeyserUtils.GEYSER_CLASS == null) {
            GeyserUtils.GEYSER_CLASS = Reflection.getClassByNameWithoutException("org.geysermc.api.Geyser");
        }
        if (GeyserUtils.GEYSER_CLASS == null) {
            return false;
        }
        if (GeyserUtils.GEYSER_API_CLASS == null) {
            GeyserUtils.GEYSER_API_CLASS = Reflection.getClassByNameWithoutException("org.geysermc.api.GeyserApiBase");
        }
        if (GeyserUtils.GEYSER_API_METHOD == null) {
            GeyserUtils.GEYSER_API_METHOD = Reflection.getMethod(GeyserUtils.GEYSER_CLASS, "api", null, (Class<?>[])new Class[0]);
        }
        if (GeyserUtils.CONNECTION_BY_UUID_METHOD == null) {
            GeyserUtils.CONNECTION_BY_UUID_METHOD = Reflection.getMethod(GeyserUtils.GEYSER_API_CLASS, "connectionByUuid", 0);
        }
        Object apiInstance = null;
        try {
            apiInstance = GeyserUtils.GEYSER_API_METHOD.invoke(null, new Object[0]);
        }
        catch (IllegalAccessException | InvocationTargetException ex3) {
            ex3.printStackTrace();
        }
        Object connection = null;
        try {
            if (apiInstance != null) {
                connection = GeyserUtils.CONNECTION_BY_UUID_METHOD.invoke(apiInstance, uuid);
            }
        }
        catch (IllegalAccessException | InvocationTargetException ex4) {
            ex4.printStackTrace();
        }
        return connection != null;
    }
}
