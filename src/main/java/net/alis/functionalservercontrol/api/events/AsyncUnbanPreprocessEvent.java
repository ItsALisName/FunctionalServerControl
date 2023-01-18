package net.alis.functionalservercontrol.api.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AsyncUnbanPreprocessEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    private boolean cancelled;
    private OfflinePlayer player;
    private CommandSender initiator;
    private String reason;
    private String nullPlayer;

    public AsyncUnbanPreprocessEvent(OfflinePlayer player, CommandSender initiator, String reason) {
        super(true);
        this.initiator = initiator;
        this.player = player;
        this.reason = reason;
    }

    public AsyncUnbanPreprocessEvent(String nullPlayer, CommandSender initiator, String reason) {
        super(true);
        this.initiator = initiator;
        this.nullPlayer = nullPlayer;
        this.reason = reason;
    }

    public AsyncUnbanPreprocessEvent(OfflinePlayer player, String reason) {
        super(true);
        this.reason = reason;
        this.player = player;
    }

    public AsyncUnbanPreprocessEvent(String nullPlayer, String reason) {
        super(true);
        this.nullPlayer = nullPlayer;
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

    @Nullable
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Nullable
    public CommandSender getInitiator() {
        return initiator;
    }

    @Nullable
    public OfflinePlayer getPlayer() {
        return player;
    }

    @Nullable
    public String getNullPlayer() {
        return nullPlayer;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
    private static HandlerList getHandlerList() {
        return handlerList;
    }
}
