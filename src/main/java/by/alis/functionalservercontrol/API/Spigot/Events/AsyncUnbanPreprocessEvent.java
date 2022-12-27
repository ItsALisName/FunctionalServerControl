package by.alis.functionalservercontrol.API.Spigot.Events;

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
    private final boolean isApiEnabled;
    private String apiPassword;

    public AsyncUnbanPreprocessEvent(OfflinePlayer player, CommandSender initiator, String reason, boolean isApiEnabled) {
        super(true);
        this.initiator = initiator;
        this.player = player;
        this.reason = reason;
        this.isApiEnabled = isApiEnabled;
    }

    public AsyncUnbanPreprocessEvent(String nullPlayer, CommandSender initiator, String reason, boolean isApiEnabled) {
        super(true);
        this.isApiEnabled = isApiEnabled;
        this.initiator = initiator;
        this.nullPlayer = nullPlayer;
        this.reason = reason;
    }

    public AsyncUnbanPreprocessEvent(OfflinePlayer player, String reason, boolean isApiEnabled) {
        super(true);
        this.reason = reason;
        this.isApiEnabled = isApiEnabled;
        this.player = player;
    }

    public AsyncUnbanPreprocessEvent(String nullPlayer, String reason, boolean isApiEnabled) {
        super(true);
        this.nullPlayer = nullPlayer;
        this.reason = reason;
        this.isApiEnabled = isApiEnabled;
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

    /**
     * Checks whether the API is enabled in general.yml
     * @return true if API enabled
     */
    public boolean isApiEnabled() {
        return isApiEnabled;
    }

    /**
     * Used by the plugin to verify the entered password
     * @return entered password
     */
    public String getApiPassword() {
        return apiPassword;
    }

    /**
     * Used if API password protection is enabled in general.yml
     * Used to enter a password
     * @param apiPassword password to use event
     */
    public void inputApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
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
