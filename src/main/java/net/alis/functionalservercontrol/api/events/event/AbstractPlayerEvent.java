package net.alis.functionalservercontrol.api.events.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPlayerEvent extends Event {

    protected Player player;

    protected AbstractPlayerEvent(@NotNull Player player) {
        this.player = player;
    }

    protected AbstractPlayerEvent(@NotNull Player player, boolean async) {
        super(async);
        this.player = player;
    }

    public @NotNull Player getPlayer() {
        return player;
    }


}
