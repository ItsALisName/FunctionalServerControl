package by.alis.functionalservercontrol.api.events;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerCheatCheckPreprocessEvent extends PlayerEvent implements Cancellable {

    private final static HandlerList handlerList = new HandlerList();
    private CommandSender initiator;
    private String reason;
    private boolean cancelled;



    public PlayerCheatCheckPreprocessEvent(@NotNull Player who, @NotNull CommandSender initiator, String reason) {
        super(who);
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
