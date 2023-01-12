package by.alis.functionalservercontrol.spigot.listeners.packetlisteners;

import by.alis.functionalservercontrol.spigot.FunctionalServerControl;

import by.alis.functionalservercontrol.spigot.managers.GlobalCommandManager;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getCommandLimiterSettings;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.expansions.Expansions.getProtocolLibManager;

public class PacketCommandsListener {
    private final FunctionalServerControl plugin;
    public PacketCommandsListener(FunctionalServerControl plugin) {
        this.plugin = plugin;
        if(!getConfigSettings().isLessInformation()) Bukkit.getConsoleSender().sendMessage(setColors("&a&o[FunctionalServerControl | ProtocolLib] Added packet listener PacketCommandsListener"));
    }
    public void onTabComplete() {
        getProtocolLibManager().getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, ListenerPriority.HIGH, PacketType.Play.Server.TAB_COMPLETE) {
            @Override
            public void onPacketSending(PacketEvent e){
                if (e.getPacketType() == PacketType.Play.Server.TAB_COMPLETE) {
                    if (getCommandLimiterSettings().isHideCompletionsFully()) {
                        Player player = e.getPlayer();
                        String[] completions = e.getPacket().getStringArrays().read(0);
                        if (!player.hasPermission("functionalservercontrol.tab-complete.bypass")) {
                            GlobalCommandManager commandManager = new GlobalCommandManager();
                            e.getPacket().getStringArrays().write(0, commandManager.getCommandsToFullyHide(player, Arrays.asList(completions)).toArray(new String[0]));
                        }
                    }
                }
            }
        });

    }
}
