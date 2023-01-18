package net.alis.functionalservercontrol.spigot.additional.misc.protocolutils.inwrappers;

import net.alis.functionalservercontrol.spigot.additional.misc.protocolutils.AbstractPacket;
import net.alis.functionalservercontrol.api.enums.InventoryClickType;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.inventory.ItemStack;

public class WrapperPlayClientWindowClick extends AbstractPacket {
    public static final PacketType TYPE = PacketType.Play.Client.WINDOW_CLICK;

    public WrapperPlayClientWindowClick() {
        super(new PacketContainer(TYPE), TYPE);
        this.handle.getModifier().writeDefaults();
    }

    public WrapperPlayClientWindowClick(PacketContainer packet) {
        super(packet, TYPE);
    }

    public int getWindowId() {
        return this.handle.getIntegers().read(0);
    }

    public void setWindowId(int value) {
        this.handle.getIntegers().write(0, value);
    }

    public int getSlot() {
        return this.handle.getIntegers().read(1);
    }

    public void setSlot(int value) {
        this.handle.getIntegers().write(1, value);
    }

    public int getButton() {
        return this.handle.getIntegers().read(2);
    }

    public void setButton(int value) {
        this.handle.getIntegers().write(2, value);
    }

    public short getActionNumber() {
        return this.handle.getShorts().read(0);
    }

    public void setActionNumber(short value) {
        this.handle.getShorts().write(0, value);
    }

    public ItemStack getClickedItem() {
        return this.handle.getItemModifier().read(0);
    }

    public void setClickedItem(ItemStack value) {
        this.handle.getItemModifier().write(0, value);
    }

    public InventoryClickType getClickType() {
        return this.handle.getEnumModifier(InventoryClickType.class, 4).read(0);
    }

    public void setShift(InventoryClickType value) {
        this.handle.getEnumModifier(InventoryClickType.class, 5).write(0, value);
    }
}
