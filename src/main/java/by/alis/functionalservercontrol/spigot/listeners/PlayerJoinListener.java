package by.alis.functionalservercontrol.spigot.listeners;

import by.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import by.alis.functionalservercontrol.spigot.managers.TaskManager;
import by.alis.functionalservercontrol.spigot.managers.ban.BanChecker;
import by.alis.functionalservercontrol.spigot.managers.DupeIpManager;
import by.alis.functionalservercontrol.spigot.managers.mute.MuteManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoinToServer(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DupeIpManager.checkDupeIpOnJoin(player);
        TaskManager.preformAsync(() -> {
            getBaseManager().insertIntoAllPlayers(player.getName(), player.getUniqueId(), player.getAddress().getAddress().getHostAddress());
            getBaseManager().insertIntoPlayersPunishInfo(player.getUniqueId());
            getBaseManager().updateAllPlayers(player);
            TemporaryCache.setOnlinePlayerNames(player);
            TemporaryCache.setOnlineIps(player);
            MuteManager muteManager = new MuteManager();
            muteManager.checkForNullMutedPlayer(player);
            muteManager.notifyAboutMuteOnJoin(player);
            BanChecker.bannedIpNotify(player);
        });
    }
}
