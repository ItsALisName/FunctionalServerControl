package by.alis.functionalservercontrol.api.events;

import by.alis.functionalservercontrol.api.enums.MuteType;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AsyncMutePreprocessEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    private boolean canceled;
    private String reason;
    private final MuteType muteType;
    private long time;
    private final String muteId;
    private final String translatedTime;
    private OfflinePlayer player;
    private final CommandSender initiator;
    private final String realTime;
    private final String realDate;
    private String nullPlayer;



    public AsyncMutePreprocessEvent(String muteId, OfflinePlayer player, CommandSender initiator, MuteType muteType, long time, String reason, String realTime, String realDate, String translatedTime) {

        super(true);
        this.player = player;
        this.time = time;
        this.reason = reason;
        this.muteType = muteType;
        this.muteId = muteId;
        this.translatedTime = translatedTime;
        this.initiator = initiator;
        this.realTime = realTime;
        this.realDate = realDate;

    }

    public AsyncMutePreprocessEvent(String muteId, String player, CommandSender initiator, MuteType muteType, long time, String reason, String realTime, String realDate, String translatedTime) {

        super(true);
        this.muteId = muteId;
        this.muteType = muteType;
        this.initiator = initiator;
        this.time = time;
        this.nullPlayer = player;
        this.reason = reason;
        this.realTime = realTime;
        this.realDate = realDate;
        this.translatedTime = translatedTime;

    }

    public String getMuteId() {
        return this.muteId;
    }

    public String getNullPlayer() {
        if(this.player != null) {
            return "There is no need to use it now";
        }
        return nullPlayer;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public CommandSender getInitiator() {
        return initiator;
    }

    public MuteType getBanType() {
        return muteType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getMuteTime() {
        return time;
    }

    public void setMuteTime(long time) {
        this.time = System.currentTimeMillis() + time;
    }

    public String getTranslatedBanTime() {
        return translatedTime;
    }

    public String getRealTime() {
        return realTime;
    }

    public String getRealDate() {
        return realDate;
    }

    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.canceled = cancel;
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
