package net.alis.functionalservercontrol.api.naf.v1_10_0.util.data;

import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FunctionalStatistics;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.RewritableCraftType;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class WritableOfflinePlayerMeta extends DefaultMeta {

    private boolean isBanned;
    private boolean isMuted;
    private OfflinePlayer player;
    private FunctionalStatistics.PlayerStats playerStats;
    private FunctionalStatistics.AdminStats adminStats;

    public WritableOfflinePlayerMeta(String nickname, UUID uuid, FID fid, OfflinePlayer player, boolean isBanned, boolean isMuted, FunctionalStatistics.PlayerStats playerStats, FunctionalStatistics.AdminStats adminStats) {
        super(nickname, uuid, fid);
        this.isBanned = isBanned;
        this.isMuted = isMuted;
        this.player = player;
        this.playerStats = playerStats;
        this.adminStats = adminStats;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    @Override
    public String getNickname() {
        return this.nickname;
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public FID getFid() {
        return this.fid;
    }

    @Override
    public WritableOfflinePlayerMeta getMeta() {
        return null;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public FunctionalStatistics.AdminStats getAdminStats() {
        return adminStats;
    }

    public FunctionalStatistics.PlayerStats getPlayerStats() {
        return playerStats;
    }

    @Override
    public void rewrite(RewritableCraftType type, Object param) {
        switch (type) {
            case FID -> this.fid = (FID) param;
            case UUID -> this.uuid = (UUID) param;
            case NICKNAME -> this.nickname = (String) param;
            case BOOL_BANNED -> this.isBanned = (Boolean)param;
            case BOOL_MUTED -> this.isMuted = (Boolean)param;
            case PLAYER -> this.player = (OfflinePlayer) param;
            case PLAYER_STATS -> this.playerStats = (FunctionalStatistics.PlayerStats) param;
            case ADMIN_STATS -> this.adminStats = (FunctionalStatistics.AdminStats) param;
        }
    }
}
