package net.alis.functionalservercontrol.api.enums;

public enum BanType {
    /**
     * It is assumed that the player is banned forever not by IP address
     */
    PERMANENT_NOT_IP,

    /**
     * It is assumed that the player is banned forever by IP address
     */
    PERMANENT_IP,

    /**
     * It is assumed that the player is banned for a while not by IP address
     */
    TIMED_NOT_IP,

    /**
     * It is assumed that the player is banned for a while by IP address
     */
    TIMED_IP,
    BAN_TYPE_ERROR;
}
