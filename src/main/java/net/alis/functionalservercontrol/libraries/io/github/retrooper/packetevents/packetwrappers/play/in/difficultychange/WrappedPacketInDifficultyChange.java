 

package net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.difficultychange;

import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.NMSPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.WrappedPacket;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.utils.world.Difficulty;

public final class WrappedPacketInDifficultyChange extends WrappedPacket
{
    public WrappedPacketInDifficultyChange(final NMSPacket packet) {
        super(packet);
    }
    
    public Difficulty getDifficulty() {
        return this.readDifficulty(0);
    }
    
    public void setDifficulty(final Difficulty difficulty) {
        this.writeDifficulty(0, difficulty);
    }
}
