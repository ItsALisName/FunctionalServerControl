package net.alis.functionalservercontrol.api.interfaces;

import net.alis.functionalservercontrol.api.enums.BanType;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface FunctionalBanEntry {

    /**
     * Returns the name of the banned player
     * @return player name
     */
    @Nullable String getName();

    /**
     * Returns the id of this ban
     * @return ban id
     */
    @NotNull String getId();

    /**
     * Returns the ip-address of the banned player
     * @return ip-address
     */
    @Nullable String getAddress();

    /**
     * Returns the type if this ban
     * @return type of ban
     */
    @NotNull BanType getBanType();

    /**
     * Returns the initiator name of this ban
     * @return initiator name
     */
    @NotNull String getInitiator();

    /**
     * Returns the reason of this ban
     * @return ban reason
     */
    @NotNull String getReason();

    /**
     * Returns the date of this ban
     * @return ban date
     */
    @NotNull String getBanDate();

    /**
     * Returns the time of this ban
     * @return ban time
     */
    @NotNull String getBanTime();

    /**
     * Returns the UUID of the banned player
     * @return UUID
     */
    @Nullable UUID getUniqueId();

    @Nullable FID getFunctionalId();

    /**
     * Returns the unban time in milliseconds
     * @return unban time
     */
    long getUnbanTime();

    void unban();

}
