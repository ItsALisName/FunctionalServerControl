package by.alis.functionalservercontrol.spigot.Additional.TimerTasks;

import by.alis.functionalservercontrol.spigot.Additional.Other.TemporaryCache;
import by.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

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
                        Bukkit.getScheduler().runTask(FunctionalServerControlSpigot.getProvidingPlugin(FunctionalServerControlSpigot.class), () -> {
                           Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getConfigSettings().getDupeIpAction().replace("%1$f", dupeIpPlayer.getName()));
                        });
                    }
                }
            }
        }
    }

}
