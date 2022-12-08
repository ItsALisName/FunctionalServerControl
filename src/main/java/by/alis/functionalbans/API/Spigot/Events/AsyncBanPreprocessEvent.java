package by.alis.functionalbans.API.Spigot.Events;

import by.alis.functionalbans.spigot.Additional.Enums.BanType;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.Nullable;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

public class AsyncBanPreprocessEvent extends Event implements Cancellable {

    public static final HandlerList handlers = new HandlerList();

    private boolean canceled;

    private String reason;

    private final BanType banType;

    private long time;

    private final String banId;
    private final String translatedTime;

    private OfflinePlayer player;
    private final CommandSender initiator;

    private final String realTime;

    private String apiPassword;

    private final String realDate;

    private final boolean isApiEnabled;

    private String nullPlayer;


    public AsyncBanPreprocessEvent(String banId, OfflinePlayer player, CommandSender initiator, BanType banType, long time, String reason, String realTime, String realDate, boolean isApiEnabled, String translatedTime) {

        super(true);
        this.banId = banId;
        this.banType = banType;
        this.initiator = initiator;
        this.time = time;
        this.player = player;
        this.reason = reason;
        this.realTime = realTime;
        this.realDate = realDate;
        this.isApiEnabled = isApiEnabled;
        this.translatedTime = translatedTime;

    }

    public AsyncBanPreprocessEvent(String banId, String player, CommandSender initiator, BanType banType, long time, String reason, String realTime, String realDate, boolean isApiEnabled, String translatedTime) {

        super(true);
        this.banId = banId;
        this.banType = banType;
        this.initiator = initiator;
        this.time = time;
        this.nullPlayer = player;
        this.reason = reason;
        this.realTime = realTime;
        this.realDate = realDate;
        this.isApiEnabled = isApiEnabled;
        this.translatedTime = translatedTime;

    }

    public void inputApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }

    public String getBanId() {
        return this.banId;
    }

    public String getNullPlayer() {
        if(this.player != null) {
            return getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU") ? "Нет необходимости использовать это сейчас" : "There is no need to use it now";
        }
        return nullPlayer;
    }

    public String getApiPassword() {
        if(this.apiPassword == null || this.apiPassword.equalsIgnoreCase("")) {
            return null;
        }
        return apiPassword;
    }

    public boolean isApiEnabled() {
        return isApiEnabled;
    }

    private static HandlerList getHandlerList() {
        return handlers;
    }


    @Override
    public boolean isCancelled() {
        return canceled;
    }

    @Override
    public void setCancelled(boolean canceled) {
        this.canceled = canceled;
    }

    @Nullable
    public OfflinePlayer getPlayer() {
        return player;
    }

    public CommandSender getInitiator() {
        return initiator;
    }

    public BanType getBanType() {
        return banType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getBanTime() {
        return time;
    }

    public void setBanTime(long time) {
        this.time = System.currentTimeMillis() + time;
    }

    public String getTranslatedTime() {
        return translatedTime;
    }


    public String getRealTime() {
        return realTime;
    }

    public String getRealDate() {
        return realDate;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
