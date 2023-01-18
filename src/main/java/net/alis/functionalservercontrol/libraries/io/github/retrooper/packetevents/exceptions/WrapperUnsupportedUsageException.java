

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.exceptions;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.reflection.ClassUtil;

public class WrapperUnsupportedUsageException extends RuntimeException
{
    public WrapperUnsupportedUsageException(final String message) {
        super(message);
    }
    
    public WrapperUnsupportedUsageException(final Class<? extends WrappedPacket> wrapperClass) {
        this("You are using a packet wrapper which happens to be unsupported on the local server version. Packet wrapper you attempted to use: " + ClassUtil.getClassSimpleName(wrapperClass));
    }
}
