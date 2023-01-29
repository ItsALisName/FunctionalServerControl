package net.alis.functionalservercontrol.api.events;

import net.alis.functionalservercontrol.api.events.event.AbstractPlayerEvent;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncPlayerCheatCheckPreprocessEvent extends AbstractPlayerEvent implements Cancellable {

    private final static HandlerList handlerList = new HandlerList();
    private final CommandSender initiator;
    private String reason;
    private boolean cancelled;

    @Deprecated
    public AsyncPlayerCheatCheckPreprocessEvent(@NotNull Player player, @NotNull CommandSender initiator, String reason) {
        super(player, true);
        this.initiator = initiator;
        this.reason = reason;
    }

    public AsyncPlayerCheatCheckPreprocessEvent(@NotNull FunctionalPlayer player, CommandSender initiator, String reason) {
        super(player, true);
        this.initiator = initiator;
        this.reason = reason;
    }


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public CommandSender getInitiator() {
        return initiator;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Cancel this event
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    private static HandlerList getHandlerList() {
        return handlerList;
    }

}
