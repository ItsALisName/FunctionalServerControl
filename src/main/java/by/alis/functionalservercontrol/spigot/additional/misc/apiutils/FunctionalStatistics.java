package by.alis.functionalservercontrol.spigot.additional.misc.apiutils;

import by.alis.functionalservercontrol.api.enums.StatsType;
import org.bukkit.entity.Player;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getConfigSettings;

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
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    return getSQLiteManager().getAdminStatsInfo(admin, administratorStats);
                }
                case H2: {}
            }
            return "0";
        }

    }

    public static class PlayerStats {
        private final Player player;
        public PlayerStats(Player player) {
            this.player = player;
        }

        public String get(StatsType.Player playerStats) {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    return getSQLiteManager().getPlayerStatsInfo(player, playerStats);
                }
                case H2: {}
            }
            return "0";
        }
    }

}
