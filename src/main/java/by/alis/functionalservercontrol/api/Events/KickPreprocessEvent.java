package by.alis.functionalservercontrol.api.Events;

import by.alis.functionalservercontrol.api.Enums.KickType;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class KickPreprocessEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    private boolean cancelled;
    private String reason;
    private final Player player;
    private final CommandSender initiator;
    private final KickType kickType;

    public KickPreprocessEvent(boolean async, Player player, CommandSender initiator, String reason, KickType kickType) {
        super(async);
        this.player = player;
        this.initiator = initiator;
        this.reason = reason;
        this.kickType = kickType;
    }

    public KickPreprocessEvent(Player player, CommandSender initiator, KickType type) {
        this.player = player;
        this.kickType = type;
        this.initiator = initiator;
    }

    @NotNull
    public CommandSender getInitiator() {
        return initiator;
    }

    @Nullable
    public String getReason() {
        return reason;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @NotNull
    private static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    public KickType getKickType() {
        return kickType;
    }
}
