 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.armanimation;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.enums.EnumUtil;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.nms.NMSUtils;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.player.Hand;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.server.ServerVersion;

public class WrappedPacketInArmAnimation extends WrappedPacket
{
    private static boolean v_1_9;
    
    public WrappedPacketInArmAnimation(final NMSPacket packet) {
        super(packet);
    }
    
    @Override
    protected void load() {
        WrappedPacketInArmAnimation.v_1_9 = WrappedPacketInArmAnimation.version.isNewerThanOrEquals(ServerVersion.v_1_9);
    }
    
    public Hand getHand() {
        if (WrappedPacketInArmAnimation.v_1_9) {
            final Enum<?> enumConst = this.readEnumConstant(0, NMSUtils.enumHandClass);
            return Hand.values()[enumConst.ordinal()];
        }
        return Hand.MAIN_HAND;
    }
    
    public void setHand(final Hand hand) {
        if (WrappedPacketInArmAnimation.v_1_9) {
            final Enum<?> enumConst = EnumUtil.valueByIndex(NMSUtils.enumHandClass, hand.ordinal());
            this.writeEnumConstant(0, enumConst);
        }
    }
}
