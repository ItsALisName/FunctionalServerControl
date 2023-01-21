package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.libraries.com.jeff_media.updatechecker.UpdateChecker;
import net.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import net.alis.functionalservercontrol.spigot.additional.tasks.PacketLimiterTask;
import net.alis.functionalservercontrol.spigot.dependencies.Expansions;
import net.alis.functionalservercontrol.libraries.ru.leymooo.fixer.ItemChecker;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.spigot.managers.DupeIpManager;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.managers.ban.BanChecker;
import net.alis.functionalservercontrol.spigot.managers.mute.MuteManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getProtectionSettings;

public class PlayerJoinListener implements Listener {
    PacketLimiterTask packetLimiterTask;
    public PlayerJoinListener(PacketLimiterTask plCon) {
        this.packetLimiterTask = plCon;
    }
    @EventHandler
    public void onPlayerJoinToServer(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DupeIpManager.checkDupeIpOnJoin(player);
        TaskManager.preformAsync(() -> {
            BaseManager.getBaseManager().insertIntoAllPlayers(player.getName(), player.getUniqueId(), player.getAddress().getAddress().getHostAddress());
            BaseManager.getBaseManager().insertIntoPlayersPunishInfo(player.getUniqueId());
            BaseManager.getBaseManager().updateAllPlayers(player);
            TemporaryCache.setOnlinePlayerNames(player);
            TemporaryCache.setOnlineIps(player);
            MuteManager muteManager = new MuteManager();
            muteManager.checkForNullMutedPlayer(player);
            muteManager.notifyAboutMuteOnJoin(player);
            BanChecker.bannedIpNotify(player);
            if(getProtectionSettings().isPacketLimiterEnabled()){
                this.packetLimiterTask.packetMonitoringPlayers().put(event.getPlayer(), 0);
            }
            if(Expansions.getProtocolLibManager().isProtocolLibSetuped() && getProtectionSettings().isItemFixerEnabled()) {
                for (ItemStack stack : event.getPlayer().getInventory().getContents()) {
                    ItemChecker.getItemChecker().isHackedItem(stack, event.getPlayer());
                }
            }
            if (getConfigSettings().isCheckForUpdates() && player.hasPermission("functionalservercontrol.notification.update")) {
                UpdateChecker.getInstance().checkNow(player);
            }
        });
    }
}
