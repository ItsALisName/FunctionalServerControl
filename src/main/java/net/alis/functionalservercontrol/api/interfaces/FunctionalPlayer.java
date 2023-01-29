package net.alis.functionalservercontrol.api.interfaces;

import net.alis.functionalservercontrol.api.naf.Incore;
import net.alis.functionalservercontrol.api.naf.v1_10_0.entity.sub.ExpansionedCraftPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FunctionalStatistics;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.messaging.PluginMessageRecipient;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface FunctionalPlayer extends OfflineFunctionalPlayer, CommandSender, PluginMessageRecipient {

    static FunctionalPlayer get(String name) {
        return Incore.Player.getByName(name);
    }

    @Deprecated
    static FunctionalPlayer get(UUID uuid) {
        return Incore.Player.getByUniqueId(uuid);
    }

    static FunctionalPlayer get(FID fid) {
        return Incore.Player.getByFunctionalId(fid);
    }

    int protocolVersion();

    String minecraftVersionName();

    String clientBrandName();

    String nickname();

    UUID getUniqueId();

    FID getFunctionalId();

    FunctionalStatistics.PlayerStats getStatsAsPlayer();

    FunctionalStatistics.AdminStats getStatsAsAdmin();

    ExpansionedCraftPlayer expansion();

    Player getBukkitPlayer();

    void message(String message);

    void title(String header);

    void title(String header, String footer);

    void title(String header, String footer, int fadeIn, int stay, int fadeOut);

    boolean hasPermission(@NotNull String permission);

    boolean hasPermission(@NotNull Permission permission);

    String address();

    World world();

    int ping();

    int packetsCount();

    boolean isOnline();

    void kill();

    void kick();

    void kick(String reason);

}
