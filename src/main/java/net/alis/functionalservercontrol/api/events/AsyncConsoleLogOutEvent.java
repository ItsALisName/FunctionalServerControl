package net.alis.functionalservercontrol.api.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncConsoleLogOutEvent extends Event implements Cancellable {


    private boolean cancelled;
    private static final HandlerList handlerList = new HandlerList();
    private String message;

    public AsyncConsoleLogOutEvent(String message) {
        super(true);
        this.message = message;
    }

    /**
     * Checks if the event has been canceled
     * @return true, if event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Cancels this event (i.e. cancels the account ban)
     * @param cancelled true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Returns a message that should be sent to the console
     * @return Message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Modifies the message that is sent to the console
     * @param message New console message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    private static HandlerList getHandlerList() {
        return handlerList;
    }
}
