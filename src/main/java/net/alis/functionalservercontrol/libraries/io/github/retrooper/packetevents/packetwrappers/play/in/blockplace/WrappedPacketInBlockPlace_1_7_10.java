

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.blockplace;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.vector.Vector3f;
import org.bukkit.inventory.ItemStack;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.vector.Vector3i;

final class WrappedPacketInBlockPlace_1_7_10 extends WrappedPacket
{
    WrappedPacketInBlockPlace_1_7_10(final NMSPacket packet) {
        super(packet);
    }
    
    public Vector3i getBlockPosition() {
        return new Vector3i(this.readInt(0), this.readInt(1), this.readInt(2));
    }
    
    public void setBlockPosition(final Vector3i blockPos) {
        this.writeInt(0, blockPos.x);
        this.writeInt(1, blockPos.y);
        this.writeInt(2, blockPos.z);
    }
    
    public int getFace() {
        return this.readInt(3);
    }
    
    public void setFace(final int face) {
        this.writeInt(3, face);
    }
    
    public ItemStack getItemStack() {
        return this.readItemStack(0);
    }
    
    public void setItemStack(final ItemStack stack) {
        this.writeItemStack(0, stack);
    }
    
    public Vector3f getCursorPosition() {
        return new Vector3f(this.readFloat(0), this.readFloat(1), this.readFloat(2));
    }
    
    public void setCursorPosition(final Vector3f cursorPos) {
        this.writeFloat(0, cursorPos.x);
        this.writeFloat(1, cursorPos.y);
        this.writeFloat(2, cursorPos.z);
    }
}
