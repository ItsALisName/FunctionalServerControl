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


    /**
     * Used if API password protection is enabled in general.yml
     * Used to enter a password
     * @param apiPassword password to use event
     */
    public void inputApiPassword(String apiPassword) {
        this.apiPassword = apiPassword;
    }


    /**
     * Gets the ban id
     * @return ban id
     */
    public String getBanId() {
        return this.banId;
    }

    /**
     * Used if the banned player has never played on the server
     * @return nickname of the banned player
     */
    public String getNullPlayer() {
        if(this.player != null) {
            return getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU") ? "Нет необходимости использовать это сейчас" : "There is no need to use it now";
        }
        return nullPlayer;
    }

    /**
     * Used by the plugin to verify the entered password
     * @return entered password
     */
    public String getApiPassword() {
        if(this.apiPassword == null || this.apiPassword.equalsIgnoreCase("")) {
            return null;
        }
        return apiPassword;
    }

    /**
     * Checks whether the API is enabled in general.yml
     * @return true if API enabled
     */
    public boolean isApiEnabled() {
        return isApiEnabled;
    }


    /**
     * Checks if the event has been canceled
     * @return true, if event is cancelled
     */
    @Override
    public boolean isCancelled() {
        return canceled;
    }

    /**
     * Cancels this event (i.e. cancels the account ban)
     * @param canceled true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean canceled) {
        this.canceled = canceled;
    }


    /**
     * Returns the offline player who was banned
     * @return Offline Player(null if the player has never played on the server before)
     */
    @Nullable
    public OfflinePlayer getPlayer() {
        return player;
    }

    /**
     * Returns the initiator of the ban (Can be either a Console or a Player)
     * @return Ban Initiator(Console or Player)
     */
    public CommandSender getInitiator() {
        return initiator;
    }

    /**
     * Returns the type of account ban
     * @return Ban Type
     */
    public BanType getBanType() {
        return banType;
    }

    /**
     * Returns the reason for the account ban
     * @return Ban Reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * Changes the reason for the account ban
     * @param reason New Ban Reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Returns the account ban time (in milliseconds)
     * @return Ban Time
     */
    public long getBanTime() {
        return time;
    }

    /**
     * Changes the account ban time (in milliseconds)
     * @param time New Ban Time
     */
    public void setBanTime(long time) {
        this.time = System.currentTimeMillis() + time;
    }

    /**
     * Returns the account ban time as a string
     * @return Ban Time
     */
    public String getTranslatedBanTime() {
        return translatedTime;
    }


    /**
     * Returns the current time at which the ban was made
     * @return Current time
     */
    public String getRealTime() {
        return realTime;
    }

    /**
     * Returns the current date on which the ban was made
     * @return Current date
     */
    public String getRealDate() {
        return realDate;
    }


    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    private static HandlerList getHandlerList() {
        return handlers;
    }
}
