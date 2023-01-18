

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.steervehicle;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;

public class WrappedPacketInSteerVehicle extends WrappedPacket
{
    public WrappedPacketInSteerVehicle(final NMSPacket packet) {
        super(packet);
    }
    
    public float getSideValue() {
        return this.readFloat(0);
    }
    
    public void setSideValue(final float value) {
        this.writeFloat(0, value);
    }
    
    public float getForwardValue() {
        return this.readFloat(1);
    }
    
    public void setForwardValue(final float value) {
        this.writeFloat(1, value);
    }
    
    public boolean isJump() {
        return this.readBoolean(0);
    }
    
    public void setJump(final boolean isJump) {
        this.writeBoolean(0, isJump);
    }
    
    public boolean isDismount() {
        return this.readBoolean(1);
    }
    
    public void setDismount(final boolean isDismount) {
        this.writeBoolean(1, isDismount);
    }
}
