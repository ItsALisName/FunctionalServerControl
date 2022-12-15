package by.alis.functionalbans.API.Spigot.Events;

import by.alis.functionalbans.API.Enums.KickType;
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
    private final boolean isApiEnabled;
    private String reason;
    private String apiPassword;
    private final Player player;
    private final CommandSender initiator;
    private final KickType kickType;

    public KickPreprocessEvent(boolean async, Player player, CommandSender initiator, String reason, KickType kickType, boolean isApiEnabled) {
        super(async);
        this.player = player;
        this.initiator = initiator;
        this.reason = reason;
        this.isApiEnabled = isApiEnabled;
        this.kickType = kickType;
    }

    public KickPreprocessEvent(Player player, CommandSender initiator, KickType type, boolean isApiEnabled) {
        this.player = player;
        this.kickType = type;
        this.initiator = initiator;
        this.isApiEnabled = isApiEnabled;
    }

    public void inputApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
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

    @Nullable
    public String getApiPassword() {
        return apiPassword;
    }

    public boolean isApiEnabled() {
        return isApiEnabled;
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
