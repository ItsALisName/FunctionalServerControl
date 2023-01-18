

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.login.in.start;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packettype.PacketTypeClasses;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.gameprofile.GameProfileUtil;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.nms.NMSUtils;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.gameprofile.WrappedGameProfile;

public class WrappedPacketLoginInStart extends WrappedPacket
{
    public WrappedPacketLoginInStart(final NMSPacket packet) {
        super(packet);
    }
    
    public WrappedGameProfile getGameProfile() {
        return GameProfileUtil.getWrappedGameProfile(this.readObject(0, NMSUtils.gameProfileClass));
    }
    
    public void setGameProfile(final WrappedGameProfile wrappedGameProfile) {
        final Object gameProfile = GameProfileUtil.getGameProfile(wrappedGameProfile.getId(), wrappedGameProfile.getName());
        this.write(NMSUtils.gameProfileClass, 0, gameProfile);
    }
    
    @Override
    public boolean isSupported() {
        return PacketTypeClasses.Login.Client.START != null;
    }
}
