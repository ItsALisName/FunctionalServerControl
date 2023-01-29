package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.libraries.com.jeff_media.updatechecker.UpdateChecker;
import net.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import net.alis.functionalservercontrol.spigot.additional.tasks.PacketLimiter;
import net.alis.functionalservercontrol.spigot.dependencies.Expansions;
import net.alis.functionalservercontrol.libraries.ru.leymooo.fixer.ItemChecker;
import net.alis.functionalservercontrol.spigot.managers.DupeIpManager;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.managers.ban.BanChecker;
import net.alis.functionalservercontrol.spigot.managers.mute.MuteManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getProtectionSettings;

public class PlayerJoinListener implements Listener {
    PacketLimiter packetLimiterTask;
    public PlayerJoinListener(PacketLimiter plCon) {
        this.packetLimiterTask = plCon;
    }
    @EventHandler
    public void onPlayerJoinToServer(PlayerJoinEvent event) {
        FunctionalPlayer player = FunctionalPlayer.get(event.getPlayer().getName());
        DupeIpManager.checkDupeIpOnJoin(player);
        TaskManager.preformAsync(() -> {
            TemporaryCache.setOnlinePlayerNames(player);
            TemporaryCache.setOnlineIps(player);
            MuteManager muteManager = new MuteManager();
            muteManager.checkForNullMutedPlayer(player);
            muteManager.notifyAboutMuteOnJoin(player);
            BanChecker.bannedIpNotify(player);
            if(getProtectionSettings().isPacketLimiterEnabled()){
                PacketLimiter.update().put(player.getFunctionalId(), 0);
            }
            if(Expansions.getProtocolLibManager().isProtocolLibSetuped() && getProtectionSettings().isItemFixerEnabled()) {
                for (ItemStack stack : player.getBukkitPlayer().getInventory().getContents()) {
                    ItemChecker.getItemChecker().isHackedItem(stack, player);
                }
            }
            if (getConfigSettings().isCheckForUpdates() && player.hasPermission("functionalservercontrol.notification.update")) {
                UpdateChecker.getInstance().checkNow(player);
            }
        });
    }
}
