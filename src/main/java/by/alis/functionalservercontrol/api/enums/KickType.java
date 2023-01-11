package by.alis.functionalservercontrol.api.enums;

public enum KickType {

    /**
     * It is assumed that only one player will be kicked.
     */
    SINGLE,

    /**
     * It is assumed that most players will be kicked.
     */
    GLOBAL,

    /**
     * Same as single but invoked with "/crazykick"
     */
    CRAZY_KICK,

    ERROR


}
