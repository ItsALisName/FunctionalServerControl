 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.exceptions;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.reflection.ClassUtil;

public class WrapperFieldNotFoundException extends RuntimeException
{
    public WrapperFieldNotFoundException(final String message) {
        super(message);
    }
    
    public WrapperFieldNotFoundException(final Class<?> packetClass, final Class<?> type, final int index) {
        this("PacketEvents failed to find a " + ClassUtil.getClassSimpleName(type) + " indexed " + index + " by its type in the " + packetClass.getName() + " class!");
    }
}
