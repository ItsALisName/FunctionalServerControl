package by.alis.functionalbans.spigot.Additional.Enums;

public enum MuteType {
    /**
     * It is implied that the player is muted forever not by IP address
     */
    PERMANENT_NOT_IP,
    /**
     * It is implied that the player is muted forever by IP address
     */
    PERMANENT_IP,
    /**
     * It is assumed that the player muted for a while not by IP address
     */
    TIMED_NOT_IP,
    /**
     * It is assumed that the player muted for a while by IP address
     */
    TIMED_IP,
    BAN_TYPE_ERROR;
}
