package net.alis.functionalservercontrol.spigot.listeners.packetlisteners.packeteventsapi;
import net.alis.functionalservercontrol.api.enums.InventoryClickType;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.PacketListenerPriority;
import net.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import net.alis.functionalservercontrol.spigot.additional.misc.AdventureApiUtils;
import net.alis.functionalservercontrol.spigot.additional.misc.MD5TextUtils;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.AbstractPacketListener;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packettype.PacketType;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.windowclick.WrappedPacketInWindowClick;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class LecternCrashListener extends AbstractPacketListener {

    public LecternCrashListener() {
        super(PacketListenerPriority.LOWEST);
    }

    @Override
    public void onPacketPlayReceive(PacketPlayReceiveEvent event) {
        if (event.getPacketId() == PacketType.Play.Client.WINDOW_CLICK) {
            WrappedPacketInWindowClick windowClick = new WrappedPacketInWindowClick(event.getNMSPacket());
            InventoryClickType inventoryClickType = windowClick.readObject(4, InventoryClickType.class);
            Player player = event.getPlayer();
            InventoryView inventoryView = player.getOpenInventory();
            if (inventoryView.getType() == InventoryType.LECTERN && inventoryClickType == InventoryClickType.QUICK_MOVE) {
                event.setCancelled(true);
                String reason = getFileAccessor().getProtectionConfig().getString("lectern-crash-fixer.kick-message").replace("%1$f", player.getName());
                notifyAdmins(player.getName());
                TaskManager.preformSync(() -> CoreAdapter.getAdapter().kick(player, setColors(getFileAccessor().getLang().getString("kick-format").replace("%1$f", reason).replace("%1$f", getGlobalVariables().getConsoleVariableName()))));
            }
        }
    }

    private void notifyAdmins(String guilty) {
        if(getProtectionSettings().isNotifyAboutLecternCrash()) {
            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.lectern-crash-try").replace("%1$f", guilty)));
            for(Player admin : Bukkit.getOnlinePlayers()) {
                if(admin.hasPermission("functionalservercontrol.notification.lectern-crash")){
                    if (getConfigSettings().isServerSupportsHoverEvents()) {
                        if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                            admin.spigot().sendMessage(MD5TextUtils.appendTwo(
                                    MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.lectern-crash-try").replace("%1$f", guilty)), Bukkit.getOfflinePlayer(guilty)),
                                    MD5TextUtils.addPunishmentButtons(admin, guilty)
                            ));
                            continue;
                        }
                        if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                            admin.sendMessage(
                                    AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.lectern-crash-try").replace("%1$f", guilty)), Bukkit.getOfflinePlayer(guilty))
                                            .append(AdventureApiUtils.addPunishmentButtons(admin, guilty))
                            );
                            continue;
                        }
                    } else {
                        admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.lectern-crash-try").replace("%1$f", guilty)));
                    }
                }
            }
        }
    }
}
