package net.alis.functionalservercontrol.api.naf.v1_10_0.util;

import net.alis.functionalservercontrol.spigot.additional.misc.adapterutils.ExternalExpansionedPlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;

public enum RewritableCraftType {

    PROTOCOL_VERSION(Integer.class),
    MINECRAFT_CLIENT_NAME(String.class),
    MINECRAFT_VERSION(String.class),
    BOOL_BANNED(Boolean.class),
    BOOL_MUTED(Boolean.class),
    NICKNAME(String.class),
    UUID(java.util.UUID.class),
    FID(net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID.class),
    PLAYER_STATS(FunctionalStatistics.PlayerStats.class),
    ADMIN_STATS(FunctionalStatistics.AdminStats.class),
    EXPANSION(ExternalExpansionedPlayer.class),
    SERVER(Server.class),
    PACKETS_COUNT(Integer.class),
    ADDRESS(String.class),
    WORLD(World.class),
    PLAYER(Player.class),
    PING(Integer.class);

    public final Class<?> o;

    RewritableCraftType(Class<?> o) {
        this.o = o;
    }
}
