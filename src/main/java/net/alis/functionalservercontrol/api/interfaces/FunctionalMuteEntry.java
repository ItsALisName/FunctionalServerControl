package net.alis.functionalservercontrol.api.interfaces;

import net.alis.functionalservercontrol.api.enums.MuteType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface FunctionalMuteEntry {

    /**
     * Returns the name of the muted player
     * @return player name
     */
    @Nullable String getName();

    /**
     * Returns the id of this mute
     * @return mute id
     */
    @NotNull String getId();

    /**
     * Returns the ip-address of the muted player
     * @return ip-address
     */
    @Nullable String getAddress();

    /**
     * Returns the type if this mute
     * @return type of mute
     */
    @NotNull MuteType getMuteType();

    /**
     * Returns the initiator name of this mute
     * @return initiator name
     */
    @NotNull String getInitiator();

    /**
     * Returns the reason of this mute
     * @return mute reason
     */
    @NotNull String getReason();

    /**
     * Returns the date of this mute
     * @return mute date
     */
    @NotNull String getMuteDate();

    /**
     * Returns the time of this mute
     * @return mute time
     */
    @NotNull String getMuteTime();

    /**
     * Returns the UUID of the muted player
     * @return UUID
     */
    @Nullable UUID getUniqueId();

    /**
     * Returns the unmute time in milliseconds
     * @return unmute time
     */
    long getUnmuteTime();

    void unmute();

}
