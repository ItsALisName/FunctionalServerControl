package net.alis.functionalservercontrol.api.naf.v1_10_0.util.registerer;

import net.alis.functionalservercontrol.api.naf.Incore;
import net.alis.functionalservercontrol.api.naf.v1_10_0.InternalAdapter;
import net.alis.functionalservercontrol.api.naf.v1_10_0.entity.FunctionalCraftPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.entity.OfflineFunctionalCraftPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FunctionalStatistics;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.RewritableCraftType;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.checkers.InternalBanChecker;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.checkers.InternalMuteChecker;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.data.container.CraftPlayersContainer;
import net.alis.functionalservercontrol.spigot.additional.tasks.PacketLimiter;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CraftPlayerUpdaterTask extends BukkitRunnable {

    public CraftPlayerUpdaterTask() {
        TaskManager.preformAsyncTimerTask(this, 0, 30);
    }

    @Override
    public void run() {
        for(FunctionalCraftPlayer craftPlayer : CraftPlayersContainer.Online.Out.get()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if(new FID(player.getName()).equals(craftPlayer.getFunctionalId())) {
                    OfflineFunctionalCraftPlayer offlineCraftPlayer = CraftPlayersContainer.Offline.Out.get(craftPlayer.getFunctionalId());
                    craftPlayer.rewrite(RewritableCraftType.PLAYER, player);
                    offlineCraftPlayer.rewrite(RewritableCraftType.PLAYER, player);

                    if (player.getAddress().getAddress() != null && !player.getAddress().getAddress().getHostAddress().equalsIgnoreCase(craftPlayer.address())) {
                        craftPlayer.rewrite(RewritableCraftType.ADDRESS, player.getAddress().getAddress().getHostAddress());
                    }

                    boolean isBannedNow = InternalBanChecker.isPlayerBanned(craftPlayer.getFunctionalId());
                    boolean isMutedNow = InternalMuteChecker.isPlayerMuted(craftPlayer.getFunctionalId());
                    craftPlayer.rewrite(RewritableCraftType.BOOL_BANNED, isBannedNow);
                    craftPlayer.rewrite(RewritableCraftType.BOOL_MUTED, isMutedNow);
                    offlineCraftPlayer.rewrite(RewritableCraftType.BOOL_BANNED, isBannedNow);
                    offlineCraftPlayer.rewrite(RewritableCraftType.BOOL_MUTED, isMutedNow);

                    if(PacketLimiter.update().get(craftPlayer.getFunctionalId()) != null) {
                        craftPlayer.rewrite(RewritableCraftType.PACKETS_COUNT, PacketLimiter.update().get(craftPlayer.getFunctionalId()));
                    }

                    FunctionalStatistics.PlayerStats stats = new FunctionalStatistics.PlayerStats(craftPlayer.getFunctionalId());
                    FunctionalStatistics.AdminStats stats1 = new FunctionalStatistics.AdminStats(craftPlayer.getFunctionalId());
                    craftPlayer.rewrite(RewritableCraftType.ADMIN_STATS, stats1);
                    craftPlayer.rewrite(RewritableCraftType.PLAYER_STATS, stats);
                    offlineCraftPlayer.rewrite(RewritableCraftType.ADMIN_STATS, stats1);
                    offlineCraftPlayer.rewrite(RewritableCraftType.PLAYER_STATS, stats);

                    craftPlayer.rewrite(RewritableCraftType.PING, InternalAdapter.getPlayerPing(player));
                }
            }
        }

        Incore.offlinePlayers.clear();
        Incore.offlinePlayers.addAll(CraftPlayersContainer.Offline.Out.get());

        Incore.players.clear();
        Incore.players.addAll(CraftPlayersContainer.Online.Out.get());

    }

}
