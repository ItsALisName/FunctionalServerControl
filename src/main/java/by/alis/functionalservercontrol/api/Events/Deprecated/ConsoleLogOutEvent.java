package by.alis.functionalservercontrol.api.Events.Deprecated;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * It may not be safe to use this event, it is called more than 100 times per second
 * NOT in an asynchronous thread
 * @deprecated use AsyncConsoleLogOutEvent
 */
@Deprecated
public class ConsoleLogOutEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final String consoleMessage;

    private final boolean isApiEnabled;

    private String apiPassword;

    public ConsoleLogOutEvent(String consoleMessage, boolean isApiEnabled) {
        this.consoleMessage = consoleMessage;
        this.isApiEnabled = isApiEnabled;
    }

    public void inputApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    public String getApiPassword() {
        if(this.apiPassword == null || this.apiPassword.equalsIgnoreCase("")) {
            return null;
        }
        return apiPassword;
    }

    public String getConsoleMessage() {
        return this.consoleMessage;
    }

    public boolean isApiEnabled() {
        return this.isApiEnabled;
    }

    @Override
    public @NotNull HandlerList getHandlers() { return handlerList; }
    private static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }
}
