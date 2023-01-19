package net.alis.functionalservercontrol.api.events;

import net.alis.functionalservercontrol.api.enums.Chat;
import net.alis.functionalservercontrol.api.events.event.AbstractPlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PlayerAdvertiseEvent extends AbstractPlayerEvent {

    private static final HandlerList handlerList = new HandlerList();
    private final Chat.AdvertiseMethod advertiseMethod;
    private final String message;

    public PlayerAdvertiseEvent(Player player, Chat.AdvertiseMethod advertiseMethod, String message) {
        super(player);
        this.advertiseMethod = advertiseMethod;
        this.message = message;
    }

    public Chat.AdvertiseMethod getAdvertiseMethod() {
        return advertiseMethod;
    }

    public String getMessage() {
        return message;
    }

    private static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
