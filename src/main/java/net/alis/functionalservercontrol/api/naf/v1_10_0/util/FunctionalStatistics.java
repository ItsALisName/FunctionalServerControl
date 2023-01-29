package net.alis.functionalservercontrol.api.naf.v1_10_0.util;

import net.alis.functionalservercontrol.api.enums.StatsType;
import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;

import static net.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;

public class FunctionalStatistics {

    public static AdminStats getAsAdmin(FID fid) {
        return new AdminStats(fid);
    }

    public static PlayerStats getAsPlayer(FID fid) {
        return new PlayerStats(fid);
    }

    public static class AdminStats {
        private final FID admin;
        public AdminStats(FID fid) {
            this.admin = fid;
        }

        public String get(StatsType.Administrator administratorStats) {
            return getBaseManager().getAdminStatsInfo(admin, administratorStats);
        }

    }

    public static class PlayerStats {
        private final FID player;
        public PlayerStats(FID fid) {
            this.player = fid;
        }

        public String get(StatsType.Player playerStats) {
            return getBaseManager().getPlayerStatsInfo(player, playerStats);
        }
    }

}
