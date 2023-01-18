

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.useitem;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.vector.Vector3i;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.enums.EnumUtil;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.nms.NMSUtils;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.player.Hand;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.server.ServerVersion;

public class WrappedPacketInUseItem extends WrappedPacket
{
    private static boolean v_1_14;
    
    public WrappedPacketInUseItem(final NMSPacket packet) {
        super(packet);
    }
    
    @Override
    protected void load() {
        WrappedPacketInUseItem.v_1_14 = WrappedPacketInUseItem.version.isNewerThanOrEquals(ServerVersion.v_1_14);
    }
    
    public Hand getHand() {
        return Hand.values()[this.readEnumConstant(0, NMSUtils.enumHandClass).ordinal()];
    }
    
    public void setHand(final Hand hand) {
        final Enum<?> enumConstant = EnumUtil.valueByIndex(NMSUtils.enumHandClass, hand.ordinal());
        this.writeEnumConstant(0, enumConstant);
    }
    
    public Vector3i getBlockPosition() {
        if (WrappedPacketInUseItem.v_1_14) {
            final Object movingBlockPosition = this.readObject(0, NMSUtils.movingObjectPositionBlockClass);
            final WrappedPacket movingBlockPositionWrapper = new WrappedPacket(new NMSPacket(movingBlockPosition));
            return movingBlockPositionWrapper.readBlockPosition(0);
        }
        return this.readBlockPosition(0);
    }
    
    public void setBlockPosition(final Vector3i blockPosition) {
        if (WrappedPacketInUseItem.v_1_14) {
            final Object movingBlockPosition = this.readObject(0, NMSUtils.movingObjectPositionBlockClass);
            final WrappedPacket movingBlockPositionWrapper = new WrappedPacket(new NMSPacket(movingBlockPosition));
            movingBlockPositionWrapper.writeBlockPosition(0, blockPosition);
        }
        else {
            this.writeBlockPosition(0, blockPosition);
        }
    }
    
    @Override
    public boolean isSupported() {
        return WrappedPacketInUseItem.version.isNewerThanOrEquals(ServerVersion.v_1_9);
    }
}
