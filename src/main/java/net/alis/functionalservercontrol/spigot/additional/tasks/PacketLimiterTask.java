package net.alis.functionalservercontrol.spigot.additional.tasks;

import net.alis.functionalservercontrol.spigot.additional.textcomponents.MD5TextUtils;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.FunctionalServerControl;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.AdventureApiUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getProtectionSettings;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class PacketLimiterTask implements Runnable {

    FunctionalServerControl plugin;
    public PacketLimiterTask(FunctionalServerControl plugin) {
        this.plugin = plugin;
    }

    private static final Map<Player, Integer> playersPacketsInfo = new HashMap<>();

    private static final List<Player> badPlayers = new ArrayList<>();

    @Override
    public void run() {
        if (!getProtectionSettings().isPacketLimiterEnabled()) return;
        if (playersPacketsInfo.isEmpty()) return;
        int maxPlayerPackets = getProtectionSettings().getMaxPlayerPackets();
        for (Player player : playersPacketsInfo.keySet()) {
            if(!player.isOnline()) {
                playersPacketsInfo.remove(player);
                getBadPlayers().remove(player);
            }
            int totalPackets = playersPacketsInfo.get(player);
            if (totalPackets >= maxPlayerPackets) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.player-send-over-packets").replace("%1$f", player.getName()).replace("%2$f", String.valueOf(totalPackets)).replace("%3$f", String.valueOf(maxPlayerPackets))));
                for(String action : getProtectionSettings().getOverPacketPunish()) TaskManager.preformSync(() -> action.replace("%1$f", player.getName()).replace("%2$f", String.valueOf(maxPlayerPackets)).replace("%3$f", String.valueOf(totalPackets)));
                if(getProtectionSettings().isNotifyAdminsAboutOverPackets()) {
                    for(Player admin : Bukkit.getOnlinePlayers()) {
                        if(player.hasPermission("functionalservercontrol.notification.player-over-packets")) {
                            if(getConfigSettings().isServerSupportsHoverEvents() && getConfigSettings().isButtonsOnNotifications()) {
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                    admin.spigot().sendMessage(MD5TextUtils.appendTwo(
                                            MD5TextUtils.stringToTextComponent(setColors(getFileAccessor().getLang().getString("other.notifications.player-send-over-packets").replace("%1$f", player.getName()).replace("%2$f", String.valueOf(totalPackets)).replace("%3$f", String.valueOf(maxPlayerPackets)))),
                                            MD5TextUtils.addPunishmentButtons(admin, player.getName())
                                            ));
                                    continue;
                                }
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                    player.sendMessage(
                                            AdventureApiUtils.stringToComponent(setColors(getFileAccessor().getLang().getString("other.notifications.player-send-over-packets").replace("%1$f", player.getName()).replace("%2$f", String.valueOf(totalPackets)).replace("%3$f", String.valueOf(maxPlayerPackets))))
                                                    .append(AdventureApiUtils.addPunishmentButtons(admin, player.getName()))
                                    );
                                    continue;
                                }
                            } else {
                                admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.player-send-over-packets").replace("%1$f", player.getName()).replace("%2$f", String.valueOf(totalPackets)).replace("%3$f", String.valueOf(maxPlayerPackets))));
                            }
                        }
                    }
                }
            } else {
                playersPacketsInfo.remove(player);
            }
            playersPacketsInfo.put(player, 0);
        }
    }

    public void packetMonitoringPlayers(Player player) {
        if (playersPacketsInfo.containsKey(player)) playersPacketsInfo.put(player, playersPacketsInfo.get(player) + 1);
    }

    public Map<Player, Integer> packetMonitoringPlayers() {
        return playersPacketsInfo;
    }

    public List<Player> getBadPlayers() {
        return badPlayers;
    }

}
