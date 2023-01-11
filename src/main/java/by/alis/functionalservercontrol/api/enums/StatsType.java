package by.alis.functionalservercontrol.api.enums;

public class StatsType {

    public enum Player {
        STATS_KICKS,
        STATS_MUTES,
        STATS_BANS,
        BLOCKED_COMMANDS_USED,
        BLOCKED_WORDS_USED,
        ADVERTISE_ATTEMPTS
    }

    public enum Administrator {
        STATS_KICKS,
        STATS_MUTES,
        STATS_BANS,

        STATS_UNMUTES,

        STATS_UNBANS
    }

}
