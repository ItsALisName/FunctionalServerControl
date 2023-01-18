 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.reflection.ClassUtil;

public class NMSPacket
{
    private final Object rawNMSPacket;
    
    public NMSPacket(final Object rawNMSPacket) {
        this.rawNMSPacket = rawNMSPacket;
    }
    
    public Object getRawNMSPacket() {
        return this.rawNMSPacket;
    }
    
    public String getName() {
        return ClassUtil.getClassSimpleName(this.rawNMSPacket.getClass());
    }
}
