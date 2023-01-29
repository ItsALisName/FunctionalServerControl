package net.alis.functionalservercontrol.spigot.listeners.packetlisteners;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.PacketListenerPriority;

import net.alis.functionalservercontrol.spigot.additional.textcomponents.Component;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.AbstractPacketListener;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl.PacketPlayReceiveEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packettype.PacketType;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.in.windowclick.WrappedPacketInWindowClick;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryView;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
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
            Player player = event.getPlayer();
            InventoryView inventoryView = player.getOpenInventory();
            if (inventoryView.getType() == InventoryType.LECTERN && windowClick.getActionNumber() == 4) {
                event.setCancelled(true);
                String reason = getFileAccessor().getProtectionConfig().getString("lectern-crash-fixer.kick-message").replace("%1$f", player.getName());
                notifyAdmins(player.getName());
                FunctionalPlayer.get(player.getName()).kick(setColors(getFileAccessor().getLang().getString("kick-format").replace("%1$f", reason).replace("%1$f", getGlobalVariables().getConsoleVariableName())));
            }
        }
    }

    private void notifyAdmins(String guilty) {
        if(getProtectionSettings().isNotifyAboutLecternCrash()) {
            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.lectern-crash-try").replace("%1$f", guilty)));
            for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                if(admin.hasPermission("functionalservercontrol.notification.lectern-crash")){
                    admin.expansion().message(
                            Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.lectern-crash-try").replace("%1$f", guilty)), OfflineFunctionalPlayer.get(guilty))
                                    .append(Component.addPunishmentButtons(admin, guilty)).translateDefaultColorCodes()
                    );
                }
            }
        }
    }
}
