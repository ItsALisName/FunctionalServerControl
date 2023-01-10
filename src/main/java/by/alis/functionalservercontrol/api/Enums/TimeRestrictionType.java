package by.alis.functionalservercontrol.api.Enums;

public enum TimeRestrictionType {

    /**
     * It is assumed that the player has a standard time limit of punishments
     * (Used if a plugin supporting the group system was not found)
     */
    DEFAULT,
    /**
     * It is implied that the player has a time limit for punishments
     * coming from his group on the server
     */
    GROUP,
    ERROR;

}
