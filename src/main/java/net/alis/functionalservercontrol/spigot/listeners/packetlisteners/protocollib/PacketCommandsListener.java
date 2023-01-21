package net.alis.functionalservercontrol.spigot.listeners.packetlisteners.protocollib;

import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.dependencies.Expansions;
import net.alis.functionalservercontrol.spigot.managers.GlobalCommandManager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getCommandLimiterSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class PacketCommandsListener {
    private final FunctionalServerControlSpigot plugin;
    public PacketCommandsListener(FunctionalServerControlSpigot plugin) {
        this.plugin = plugin;
        if(!getConfigSettings().isLessInformation()) Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&a&o[FunctionalServerControlSpigot | ProtocolLib] Added packet listener PacketCommandsListener"));
    }
    public void onTabComplete() {
        Expansions.getProtocolLibManager().getProtocolManager().addPacketListener(new PacketAdapter(this.plugin, ListenerPriority.HIGH, Collections.singletonList(PacketType.Play.Server.TAB_COMPLETE), ListenerOptions.ASYNC) {
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
