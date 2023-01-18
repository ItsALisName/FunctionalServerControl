

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.handshaking.setprotocol;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.server.ServerVersion;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;

public class WrappedPacketHandshakingInSetProtocol extends WrappedPacket
{
    private static boolean v_1_17;
    
    public WrappedPacketHandshakingInSetProtocol(final NMSPacket packet) {
        super(packet);
    }
    
    @Override
    protected void load() {
        WrappedPacketHandshakingInSetProtocol.v_1_17 = WrappedPacketHandshakingInSetProtocol.version.isNewerThanOrEquals(ServerVersion.v_1_17);
    }
    
    public int getProtocolVersion() {
        return this.readInt(WrappedPacketHandshakingInSetProtocol.v_1_17 ? 1 : 0);
    }
    
    public void setProtocolVersion(final int protocolVersion) {
        this.writeInt(WrappedPacketHandshakingInSetProtocol.v_1_17 ? 1 : 0, protocolVersion);
    }
    
    public int getPort() {
        return this.readInt(WrappedPacketHandshakingInSetProtocol.v_1_17 ? 2 : 1);
    }
    
    public void setPort(final int port) {
        this.writeInt(WrappedPacketHandshakingInSetProtocol.v_1_17 ? 2 : 1, port);
    }
    
    public String getHostName() {
        return this.readString(0);
    }
    
    public void setHostName(final String hostName) {
        this.writeString(0, hostName);
    }
}
