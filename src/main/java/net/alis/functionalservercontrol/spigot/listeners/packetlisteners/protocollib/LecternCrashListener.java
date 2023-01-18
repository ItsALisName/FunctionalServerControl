package net.alis.functionalservercontrol.spigot.listeners.packetlisteners.protocollib;

import net.alis.functionalservercontrol.spigot.additional.misc.MD5TextUtils;
import net.alis.functionalservercontrol.spigot.additional.misc.protocolutils.inwrappers.WrapperPlayClientWindowClick;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.api.enums.InventoryClickType;
import net.alis.functionalservercontrol.spigot.FunctionalServerControl;
import net.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import net.alis.functionalservercontrol.spigot.additional.misc.AdventureApiUtils;
import net.alis.functionalservercontrol.spigot.dependencies.Expansions;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

import java.util.ArrayList;
import java.util.List;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class LecternCrashListener {

    private final FunctionalServerControl plugin;
    public LecternCrashListener(FunctionalServerControl plugin) {
        this.plugin = plugin;
    }

    public void listenLecternCrash() {
        if(Expansions.getProtocolLibManager().isProtocolLibSetuped()) {
            Expansions.getProtocolLibManager().getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.LOWEST, getPackets(), ListenerOptions.ASYNC) {
                @Override
                public void onPacketReceiving(PacketEvent event) {
                    if(getProtectionSettings().isFixLecternCrash() && event.getPacket().getType() == PacketType.Play.Client.WINDOW_CLICK && event.getPlayer() != null) {
                        WrapperPlayClientWindowClick packet = new WrapperPlayClientWindowClick(event.getPacket());
                        Player player = event.getPlayer();
                        InventoryView inventoryView = player.getOpenInventory();
                        if (inventoryView.getType() == InventoryType.LECTERN && packet.getClickType() == InventoryClickType.QUICK_MOVE) {
                            event.setCancelled(true);
                            String reason = getFileAccessor().getProtectionConfig().getString("lectern-crash-fixer.kick-message").replace("%1$f", player.getName());
                            notifyAdmins(player.getName());
                            TaskManager.preformSync(() -> CoreAdapter.getAdapter().kick(player, setColors(getFileAccessor().getLang().getString("kick-format").replace("%1$f", reason).replace("%1$f", getGlobalVariables().getConsoleVariableName()))));
                        }
                    }
                }

            });
        }
    }

    private List<PacketType> getPackets() {
        List<PacketType> packets = new ArrayList<>();
        for (final PacketType type : PacketType.values()) {
            if (type == PacketType.Play.Client.WINDOW_CLICK) {
                packets.add(type);
            }
        }
        return packets;
    }

    private void notifyAdmins(String guilty) {
        if(getProtectionSettings().isNotifyAboutLecternCrash()) {
            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.lectern-crash-try").replace("%1$f", guilty)));
            for(Player admin : Bukkit.getOnlinePlayers()){
                if(admin.hasPermission("functionalservercontrol.notification.lectern-crash")) {
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
                        }
                    } else {
                        admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.lectern-crash-try").replace("%1$f", guilty)));
                    }
                }
            }
        }
    }

}
