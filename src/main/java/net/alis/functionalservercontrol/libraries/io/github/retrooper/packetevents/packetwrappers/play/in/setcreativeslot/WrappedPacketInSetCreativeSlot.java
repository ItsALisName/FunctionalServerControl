

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.setcreativeslot;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import org.bukkit.inventory.ItemStack;

public class WrappedPacketInSetCreativeSlot extends WrappedPacket
{
    public WrappedPacketInSetCreativeSlot(final NMSPacket packet) {
        super(packet);
    }
    
    public int getSlot() {
        return this.readInt(0);
    }
    
    public void setSlot(final int value) {
        this.writeInt(0, value);
    }
    
    public ItemStack getClickedItem() {
        return this.readItemStack(0);
    }
    
    public void setClickedItem(final ItemStack stack) {
        this.writeItemStack(0, stack);
    }
}
