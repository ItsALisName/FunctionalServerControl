package by.alis.functionalservercontrol.spigot.Listeners.ProtocolLibListeners;

import by.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Expansions.Expansions.getProtocolLibManager;

public class PacketTabCompleteListener {
    ProtocolManager protocolManager;
    FunctionalServerControlSpigot plugin;
    public PacketTabCompleteListener(FunctionalServerControlSpigot plugin) {
        this.plugin = plugin;
        if(!getConfigSettings().isLessInformation()) Bukkit.getConsoleSender().sendMessage(setColors("&a&o[FunctionalServerControl | ProtocolLib] Added packet listener PacketTabCompleteListener"));
    }
    public void onTabComplete() {
        this.protocolManager = getProtocolLibManager().getProtocolManager();
        protocolManager.addPacketListener(new PacketAdapter(this.plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.TAB_COMPLETE) {

            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.TAB_COMPLETE) {
                    PacketContainer packetContainer = event.getPacket();
                    String msg = (packetContainer.getSpecificModifier(String.class).read(0)).toLowerCase();
                    if ((msg.startsWith("/fu") || msg.startsWith("/func") || msg.startsWith("/functi") || msg.startsWith("/functional")) && !event.getPlayer().hasPermission("functionalservercontrol.help")) {
                        event.setCancelled(true);
                    }
                }
            }
        });
    }
}
