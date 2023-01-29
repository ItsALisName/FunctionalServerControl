package net.alis.functionalservercontrol.api.interfaces;

import net.alis.functionalservercontrol.api.naf.Incore;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FunctionalStatistics;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.ServerOperator;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface OfflineFunctionalPlayer extends ServerOperator {

    static @Nullable OfflineFunctionalPlayer get(String name) {
        return Incore.Player.getOfflineByName(name);
    }

    @Deprecated
    @Nullable
    static OfflineFunctionalPlayer get(UUID uuid) {
        return Incore.Player.getOfflineByUniqueId(uuid);
    }

    static @Nullable OfflineFunctionalPlayer get(FID fid) {
        return Incore.Player.getOfflineByFunctionalId(fid);
    }

    boolean isBanned();

    boolean isMuted();

    String nickname();

    UUID getUniqueId();

    FID getFunctionalId();

    @Nullable Player getBukkitPlayer();

    OfflinePlayer getOfflineBukkitPlayer();

    @Nullable FunctionalPlayer getPlayer();

    boolean isOnline();

    FunctionalStatistics.PlayerStats getStatsAsPlayer();

    FunctionalStatistics.AdminStats getStatsAsAdmin();

}
