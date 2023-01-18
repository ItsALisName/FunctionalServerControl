package net.alis.functionalservercontrol.api.events.deprecated;

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

    public ConsoleLogOutEvent(String consoleMessage) {
        this.consoleMessage = consoleMessage;
    }

    public String getConsoleMessage() {
        return this.consoleMessage;
    }

    @Override
    public @NotNull HandlerList getHandlers() { return handlerList; }
    private static @NotNull HandlerList getHandlerList() {
        return handlerList;
    }
}
