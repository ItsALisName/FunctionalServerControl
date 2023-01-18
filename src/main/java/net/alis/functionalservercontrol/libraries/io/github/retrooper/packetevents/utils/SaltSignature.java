package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils;

public class SaltSignature {
    private final long salt;
    private final byte[] signature;

    public SaltSignature(long salt, byte[] signature) {
        this.salt = salt;
        this.signature = signature;
    }

    public long getSalt() {
        return salt;
    }

    public byte[] getSignature() {
        return signature;
    }
}
