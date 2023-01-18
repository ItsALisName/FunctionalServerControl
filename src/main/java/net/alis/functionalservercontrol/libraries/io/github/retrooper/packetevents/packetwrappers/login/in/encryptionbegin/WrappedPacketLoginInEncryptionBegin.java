 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.login.in.encryptionbegin;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packettype.PacketTypeClasses;

public class WrappedPacketLoginInEncryptionBegin extends WrappedPacket
{
    public WrappedPacketLoginInEncryptionBegin(final NMSPacket packet) {
        super(packet);
    }
    
    public byte[] getPublicKey() {
        return this.readByteArray(0);
    }
    
    public void setPublicKey(final byte[] key) {
        this.writeByteArray(0, key);
    }
    
    public byte[] getVerifyToken() {
        return this.readByteArray(1);
    }
    
    public void setVerifyToken(final byte[] token) {
        this.writeByteArray(1, token);
    }
    
    @Override
    public boolean isSupported() {
        return PacketTypeClasses.Login.Client.ENCRYPTION_BEGIN != null;
    }
}
