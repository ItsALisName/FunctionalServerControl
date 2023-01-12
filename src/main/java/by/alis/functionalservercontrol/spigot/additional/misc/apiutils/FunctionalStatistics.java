package by.alis.functionalservercontrol.spigot.additional.misc.apiutils;

import by.alis.functionalservercontrol.api.enums.StatsType;
import org.bukkit.entity.Player;

import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;

public class FunctionalStatistics {

    public AdminStats getAsAdmin(Player player) {
        return new AdminStats(player);
    }

    public PlayerStats getAsPlayer(Player player) {
        return new PlayerStats(player);
    }

    public static class AdminStats {
        private final Player admin;
        public AdminStats(Player admin) {
            this.admin = admin;
        }

        public String get(StatsType.Administrator administratorStats) {
            return getBaseManager().getAdminStatsInfo(admin, administratorStats);
        }

    }

    public static class PlayerStats {
        private final Player player;
        PlayerStats(Player player) {
            this.player = player;
        }

        public String get(StatsType.Player playerStats) {
            return getBaseManager().getPlayerStatsInfo(player, playerStats);
        }
    }

}
