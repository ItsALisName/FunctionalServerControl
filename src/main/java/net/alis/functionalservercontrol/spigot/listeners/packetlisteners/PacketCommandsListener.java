package net.alis.functionalservercontrol.spigot.listeners.packetlisteners;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.AbstractPacketListener;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.PacketListenerPriority;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.event.impl.PacketPlaySendEvent;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packettype.PacketType;
import net.alis.functionalservercontrol.libraries.io.github.retrooper.packetevents.packetwrappers.play.out.tabcomplete.WrappedPacketOutTabComplete;
import net.alis.functionalservercontrol.spigot.managers.GlobalCommandManager;

import java.util.Arrays;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getCommandLimiterSettings;

public class PacketCommandsListener extends AbstractPacketListener {

    public PacketCommandsListener() {
        super(PacketListenerPriority.LOW);
    }

    @Override
    public void onPacketPlaySend(PacketPlaySendEvent event) {
        if(event.getPacketId() == PacketType.Play.Server.TAB_COMPLETE) {
            if (getCommandLimiterSettings().isHideCompletionsFully()) {
                FunctionalPlayer player = FunctionalPlayer.get(event.getPlayer().getName());
                if(player != null){
                    WrappedPacketOutTabComplete packet = new WrappedPacketOutTabComplete(event.getNMSPacket());
                    String[] cCmds = packet.readStringArray(0);
                    if (!player.hasPermission("functionalservercontrol.tab-complete.bypass")) {
                        GlobalCommandManager commandManager = new GlobalCommandManager();
                        packet.writeStringArray(0, commandManager.getCommandsToFullyHide(player, Arrays.asList(cCmds)).toArray(new String[0]));
                    }
                }
            }
        }
    }
}
