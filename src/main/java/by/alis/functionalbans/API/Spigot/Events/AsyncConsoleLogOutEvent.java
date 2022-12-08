package by.alis.functionalbans.API.Spigot.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncConsoleLogOutEvent extends Event implements Cancellable {


    private boolean cancelled;
    private static final HandlerList handlerList = new HandlerList();
    private String message;
    private final boolean isApiEnabled;
    private String apiPassword;

    public AsyncConsoleLogOutEvent(String message, boolean apiEnabled) {
        super(true);
        this.message = message;
        this.isApiEnabled = apiEnabled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    private static HandlerList getHandlerList() {
        return handlerList;
    }

    public boolean isApiEnabled() {
        return isApiEnabled;
    }

    public String getApiPassword() {
        return apiPassword;
    }

    public void inputApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String getEventName() {
        return super.getEventName();
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
