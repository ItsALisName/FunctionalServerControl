package net.alis.functionalservercontrol.api.naf.v1_10_0.util.registerer;

import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.Incore;
import net.alis.functionalservercontrol.api.naf.v1_10_0.entity.OfflineFunctionalCraftPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FunctionalStatistics;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.checkers.InternalBanChecker;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.checkers.InternalMuteChecker;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.data.WritableOfflinePlayerMeta;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.data.container.CraftPlayersContainer;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;

public class OfflinePlayerRegisterer extends Incore.Player {

    private final OfflineFunctionalPlayer player;

    public OfflinePlayerRegisterer(OfflineFunctionalPlayer player) {
        this.player = player;
    }


    @Override
    public void register() {
        Incore.offlinePlayers.add(player);
    }

    @Override
    public void unregister() {
        Incore.offlinePlayers.removeIf(offlineFunctionalPlayer -> offlineFunctionalPlayer.getFunctionalId().equals(player.getFunctionalId()));
    }


    public static void loadCached() {
        OtherUtils.loadCachedPlayers();
        TaskManager.preformAsync(() -> {
            List<String> names  = new ArrayList<>(getBaseManager().getNamesFromAllPlayers());
            List<UUID> uuids = new ArrayList<>(getBaseManager().getUUIDsFromAllPlayers());
            List<FID> fids = new ArrayList<>(getBaseManager().getFidsFromAllPlayers());
            int i = 0;
            for(FID fid : fids) {
                int indexOf = fids.indexOf(fid);
                if(new FID(names.get(indexOf)).equals(fid)) {
                    WritableOfflinePlayerMeta meta = new WritableOfflinePlayerMeta(
                            names.get(indexOf),
                            uuids.get(indexOf),
                            fid,
                            Bukkit.getOfflinePlayer(uuids.get(indexOf)),
                            InternalBanChecker.isPlayerBanned(fid),
                            InternalMuteChecker.isPlayerMuted(fid),
                            new FunctionalStatistics.PlayerStats(fid),
                            new FunctionalStatistics.AdminStats(fid)
                    );
                    OfflineFunctionalCraftPlayer craftPlayer = new OfflineFunctionalCraftPlayer(meta);
                    CraftPlayersContainer.Offline.In.add(craftPlayer);
                    new OfflinePlayerRegisterer(craftPlayer).register();
                    i = i + 1;
                }
            }
            Bukkit.getConsoleSender().sendMessage(setColors("&3[FunctionalServerControl] &3&oLoaded " + i + " players from database"));
        });
        new CraftPlayerUpdaterTask();
    }
}
