

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.blockplace;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.enums.EnumUtil;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.player.Direction;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.nms.NMSUtils;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.vector.Vector3i;

final class WrappedPacketInBlockPlace_1_9 extends WrappedPacket
{
    private Object movingObjPos;
    
    public WrappedPacketInBlockPlace_1_9(final NMSPacket packet) {
        super(packet);
    }
    
    public Vector3i getBlockPosition() {
        if (NMSUtils.movingObjectPositionBlockClass == null) {
            return this.readBlockPosition(0);
        }
        if (this.movingObjPos == null) {
            this.movingObjPos = this.readObject(0, NMSUtils.movingObjectPositionBlockClass);
        }
        final WrappedPacket movingObjectPosWrapper = new WrappedPacket(new NMSPacket(this.movingObjPos));
        return movingObjectPosWrapper.readBlockPosition(0);
    }
    
    public void setBlockPosition(final Vector3i blockPos) {
        if (NMSUtils.movingObjectPositionBlockClass == null) {
            this.writeBlockPosition(0, blockPos);
        }
        else {
            if (this.movingObjPos == null) {
                this.movingObjPos = this.readObject(0, NMSUtils.movingObjectPositionBlockClass);
            }
            final WrappedPacket movingObjectPosWrapper = new WrappedPacket(new NMSPacket(this.movingObjPos));
            movingObjectPosWrapper.writeBlockPosition(0, blockPos);
        }
    }
    
    public Direction getDirection() {
        Enum<?> enumConst;
        if (NMSUtils.movingObjectPositionBlockClass == null) {
            enumConst = this.readEnumConstant(0, NMSUtils.enumDirectionClass);
        }
        else {
            if (this.movingObjPos == null) {
                this.movingObjPos = this.readObject(0, NMSUtils.movingObjectPositionBlockClass);
            }
            final WrappedPacket movingObjectPosWrapper = new WrappedPacket(new NMSPacket(this.movingObjPos));
            enumConst = movingObjectPosWrapper.readEnumConstant(0, NMSUtils.enumDirectionClass);
        }
        return Direction.values()[enumConst.ordinal()];
    }
    
    public void setDirection(final Direction direction) {
        final Enum<?> enumConst = EnumUtil.valueByIndex(NMSUtils.enumDirectionClass, direction.ordinal());
        if (NMSUtils.movingObjectPositionBlockClass == null) {
            this.writeEnumConstant(0, enumConst);
        }
        else {
            if (this.movingObjPos == null) {
                this.movingObjPos = this.readObject(0, NMSUtils.movingObjectPositionBlockClass);
            }
            final WrappedPacket movingObjectPosWrapper = new WrappedPacket(new NMSPacket(this.movingObjPos));
            movingObjectPosWrapper.writeEnumConstant(0, enumConst);
        }
    }
}
