package net.alis.functionalservercontrol.spigot.additional.tasks;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.Component;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;

import java.util.*;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getProtectionSettings;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class PacketLimiter implements Runnable {

    FunctionalServerControlSpigot plugin;
    public PacketLimiter(FunctionalServerControlSpigot plugin) {
        this.plugin = plugin;
    }

    private static final Map<FID, Integer> playersPacketsInfo = new HashMap<>();

    private static final List<FID> badPlayers = new ArrayList<>();

    @Override
    public void run() {
        if (!getProtectionSettings().isPacketLimiterEnabled()) return;
        if (playersPacketsInfo.isEmpty()) return;
        int maxPlayerPackets = getProtectionSettings().getMaxPlayerPackets();
        for (FID fid : playersPacketsInfo.keySet()) {
            FunctionalPlayer player = FunctionalPlayer.get(fid);
            if(!player.isOnline()) {
                playersPacketsInfo.remove(fid);
                getBadPlayers().remove(fid);
            }
            int totalPackets = playersPacketsInfo.get(fid);
            if (totalPackets >= maxPlayerPackets) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.player-send-over-packets").replace("%1$f", player.nickname()).replace("%2$f", String.valueOf(totalPackets)).replace("%3$f", String.valueOf(maxPlayerPackets))));
                for(String action : getProtectionSettings().getOverPacketPunish()) TaskManager.preformSync(() -> action.replace("%1$f", player.nickname()).replace("%2$f", String.valueOf(maxPlayerPackets)).replace("%3$f", String.valueOf(totalPackets)));
                if(getProtectionSettings().isNotifyAdminsAboutOverPackets()) {
                    for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                        if(player.hasPermission("functionalservercontrol.notification.player-over-packets")) {
                            if(getConfigSettings().isButtonsOnNotifications()) {
                                admin.expansion().message(Component.stringToSimplifiedComponent(setColors(getFileAccessor().getLang().getString("other.notifications.player-send-over-packets").replace("%1$f", player.nickname()).replace("%2$f", String.valueOf(totalPackets)).replace("%3$f", String.valueOf(maxPlayerPackets))))
                                                .append(Component.addPunishmentButtons(admin, player.nickname())).translateDefaultColorCodes()
                                );
                                continue;
                            } else {
                                admin.message(setColors(getFileAccessor().getLang().getString("other.notifications.player-send-over-packets").replace("%1$f", player.nickname()).replace("%2$f", String.valueOf(totalPackets)).replace("%3$f", String.valueOf(maxPlayerPackets))));
                            }
                        }
                    }
                }
            } else {
                playersPacketsInfo.remove(fid);
            }
            playersPacketsInfo.put(fid, 0);
        }
    }

    public void update(FID fid) {
        if (playersPacketsInfo.containsKey(fid)) playersPacketsInfo.put(fid, playersPacketsInfo.get(fid) + 1);
    }

    public static Map<FID, Integer> update() {
        return playersPacketsInfo;
    }

    public List<FID> getBadPlayers() {
        return badPlayers;
    }

}
