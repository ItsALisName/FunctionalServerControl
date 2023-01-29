package net.alis.functionalservercontrol.api.naf.v1_10_0.entity;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.entity.sub.ExpansionedCraftPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FunctionalStatistics;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.RewritableCraftType;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.data.MetaRewritable;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.data.WritableOfflinePlayerMeta;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OfflineFunctionalCraftPlayer implements OfflineFunctionalPlayer, MetaRewritable {

    private final WritableOfflinePlayerMeta meta;

    public OfflineFunctionalCraftPlayer(WritableOfflinePlayerMeta meta) {
        this.meta = meta;
    }

    @Override
    public boolean isBanned() {
        return this.meta.isBanned();
    }

    @Override
    public boolean isMuted() {
        return this.meta.isMuted();
    }

    @Override
    public String nickname() {
        return this.meta.getNickname();
    }

    @Override
    public UUID getUniqueId() {
        return this.meta.getUuid();
    }

    @Override
    public FID getFunctionalId() {
        return this.meta.getFid();
    }

    @Override
    public @Nullable Player getBukkitPlayer() {
        if(this.meta.getPlayer().isOnline()) {
            return this.meta.getPlayer().getPlayer();
        } else {
            return null;
        }
    }

    @Override
    public OfflinePlayer getOfflineBukkitPlayer() {
        return this.meta.getPlayer();
    }

    @Override
    public @Nullable FunctionalPlayer getPlayer() {
        return FunctionalPlayer.get(this.meta.getFid());
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    @Override
    public FunctionalStatistics.PlayerStats getStatsAsPlayer() {
        return this.meta.getPlayerStats();
    }

    @Override
    public FunctionalStatistics.AdminStats getStatsAsAdmin() {
        return this.meta.getAdminStats();
    }

    @Override
    public boolean isOp() {
        return this.meta.getPlayer().isOp();
    }

    @Override
    public void setOp(boolean bl) {
        this.meta.getPlayer().setOp(bl);
    }

    @Override
    public WritableOfflinePlayerMeta getMeta() {
        return meta;
    }

    @Override
    public void rewrite(RewritableCraftType type, Object param) {
        TaskManager.preformAsync(() -> {
            switch (type) {
                case FID -> {
                    if (param instanceof FID) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
                case NICKNAME -> {
                    if (param instanceof String) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
                case UUID -> {
                    if (param instanceof UUID) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
                case PLAYER_STATS -> {
                    if (param instanceof FunctionalStatistics.PlayerStats) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
                case BOOL_BANNED, BOOL_MUTED -> {
                    if (param instanceof Boolean) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
                case PLAYER -> {
                    if (param instanceof Player || param instanceof OfflinePlayer) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
                case ADMIN_STATS -> {
                    if (param instanceof FunctionalStatistics.AdminStats) {
                        this.meta.rewrite(type, param);
                        break;
                    } else {
                        throw new IllegalArgumentException("Object '" + param.getClass().getName() + "' cant rewrite type of '" + type.o.getName() + "'");
                    }
                }
            }
        });
    }

}
