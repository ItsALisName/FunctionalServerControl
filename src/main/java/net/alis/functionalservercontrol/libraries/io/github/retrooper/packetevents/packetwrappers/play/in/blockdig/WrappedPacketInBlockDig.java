 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.blockdig;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.enums.EnumUtil;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.player.Direction;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.vector.Vector3i;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.reflection.SubclassUtil;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.nms.NMSUtils;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.server.ServerVersion;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packettype.PacketTypeClasses;

public final class WrappedPacketInBlockDig extends WrappedPacket
{
    private static Class<? extends Enum<?>> digTypeClass;
    private static boolean isVersionLowerThan_v_1_8;
    
    public WrappedPacketInBlockDig(final NMSPacket packet) {
        super(packet);
    }
    
    @Override
    protected void load() {
        final Class<?> blockDigClass = PacketTypeClasses.Play.Client.BLOCK_DIG;
        WrappedPacketInBlockDig.isVersionLowerThan_v_1_8 = WrappedPacketInBlockDig.version.isOlderThan(ServerVersion.v_1_8);
        if (WrappedPacketInBlockDig.version != ServerVersion.v_1_7_10) {
            try {
                WrappedPacketInBlockDig.digTypeClass = NMSUtils.getNMSEnumClass("EnumPlayerDigType");
            }
            catch (ClassNotFoundException e) {
                WrappedPacketInBlockDig.digTypeClass = SubclassUtil.getEnumSubClass(blockDigClass, "EnumPlayerDigType");
            }
        }
    }
    
    public Vector3i getBlockPosition() {
        if (WrappedPacketInBlockDig.isVersionLowerThan_v_1_8) {
            final int x = this.readInt(0);
            final int y = this.readInt(1);
            final int z = this.readInt(2);
            return new Vector3i(x, y, z);
        }
        return this.readBlockPosition(0);
    }
    
    public void setBlockPosition(final Vector3i blockPos) {
        if (WrappedPacketInBlockDig.isVersionLowerThan_v_1_8) {
            this.writeInt(0, blockPos.x);
            this.writeInt(1, blockPos.y);
            this.writeInt(2, blockPos.z);
        }
        else {
            this.writeBlockPosition(0, blockPos);
        }
    }
    
    public Direction getDirection() {
        if (WrappedPacketInBlockDig.isVersionLowerThan_v_1_8) {
            return Direction.getDirection(this.readInt(3));
        }
        final Enum<?> enumDir = this.readEnumConstant(0, NMSUtils.enumDirectionClass);
        return Direction.values()[enumDir.ordinal()];
    }
    
    public void setDirection(final Direction direction) {
        if (WrappedPacketInBlockDig.isVersionLowerThan_v_1_8) {
            this.writeInt(3, direction.getFaceValue());
        }
        else {
            final Enum<?> enumConst = EnumUtil.valueByIndex(NMSUtils.enumDirectionClass, direction.ordinal());
            this.write(NMSUtils.enumDirectionClass, 0, enumConst);
        }
    }
    
    public PlayerDigType getDigType() {
        if (WrappedPacketInBlockDig.isVersionLowerThan_v_1_8) {
            return PlayerDigType.values()[this.readInt(4)];
        }
        return PlayerDigType.values()[this.readEnumConstant(0, WrappedPacketInBlockDig.digTypeClass).ordinal()];
    }
    
    public void setDigType(final PlayerDigType type) {
        if (WrappedPacketInBlockDig.isVersionLowerThan_v_1_8) {
            this.writeInt(4, type.ordinal());
        }
        else {
            final Enum<?> enumConst = EnumUtil.valueByIndex(WrappedPacketInBlockDig.digTypeClass, type.ordinal());
            this.writeEnumConstant(0, enumConst);
        }
    }
    
    public enum PlayerDigType
    {
        START_DESTROY_BLOCK, 
        ABORT_DESTROY_BLOCK, 
        STOP_DESTROY_BLOCK, 
        DROP_ALL_ITEMS, 
        DROP_ITEM, 
        RELEASE_USE_ITEM, 
        SWAP_ITEM_WITH_OFFHAND;
    }
}
