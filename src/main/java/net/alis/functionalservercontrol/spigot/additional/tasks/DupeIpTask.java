package net.alis.functionalservercontrol.spigot.additional.tasks;

import net.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class DupeIpTask extends BukkitRunnable {

    @Override
    public void run() {
        if(getConfigSettings().getDupeIpCheckMode().equalsIgnoreCase("timer")) {
            List<Player> dupeIpPlayers = new ArrayList<>();
            for(Player player : Bukkit.getOnlinePlayers()) {
                String playerIp = player.getAddress().getAddress().getHostAddress();
                for(Map.Entry<Player, String> e : TemporaryCache.getOnlineIps().entrySet()) {
                    if(e.getValue().equalsIgnoreCase(playerIp)) {
                        if(!dupeIpPlayers.contains(player)) {
                            dupeIpPlayers.add(player);
                        }
                    }
                }

                if(dupeIpPlayers.size() > getConfigSettings().getMaxIpsPerSession()) {
                    for(Player dupeIpPlayer : dupeIpPlayers) {
                        TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getConfigSettings().getDupeIpAction().replace("%1$f", dupeIpPlayer.getName())));
                    }
                }
            }
        }
    }

}
