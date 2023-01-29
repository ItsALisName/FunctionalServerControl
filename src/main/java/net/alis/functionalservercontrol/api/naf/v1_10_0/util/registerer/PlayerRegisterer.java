package net.alis.functionalservercontrol.api.naf.v1_10_0.util.registerer;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.Incore;

public class PlayerRegisterer extends Incore.Player {

    private final FunctionalPlayer player;

    public PlayerRegisterer(FunctionalPlayer player) {
        this.player = player;
    }

    @Override
    public void register() {
        Incore.players.add(this.player);
    }

    @Override
    public void unregister() {
        Incore.players.removeIf(functionalPlayer -> functionalPlayer.getFunctionalId().equals(player.getFunctionalId()));
    }

}
