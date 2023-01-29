package net.alis.functionalservercontrol.api.events.event;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPlayerEvent extends Event {

    private final Player bukkitPlayer;

    private FunctionalPlayer functionalPlayer;

    @Deprecated
    public AbstractPlayerEvent(@NotNull Player player) {
        this.bukkitPlayer = player;
    }

    @Deprecated
    public AbstractPlayerEvent(@NotNull Player player, boolean async) {
        super(async);
        this.bukkitPlayer = player;
    }

    public AbstractPlayerEvent(@NotNull FunctionalPlayer player) {
        this.functionalPlayer = player;
        this.bukkitPlayer = player.getBukkitPlayer();
    }

    public AbstractPlayerEvent(@NotNull FunctionalPlayer player, boolean async) {
        super(async);
        this.functionalPlayer = player;
        this.bukkitPlayer = player.getBukkitPlayer();
    }

    /**
     * Returns player from called event
     * @return player from called event
     * <p>
     * @deprecated in favor of the <b>AbstractPlayerEvent#getFunctionalPlayer</b> method
     */
    @Deprecated
    public @NotNull Player getBukkitPlayer() {
        return bukkitPlayer;
    }

    public @NotNull FunctionalPlayer getFunctionalPlayer() {
        return functionalPlayer;
    }
}
