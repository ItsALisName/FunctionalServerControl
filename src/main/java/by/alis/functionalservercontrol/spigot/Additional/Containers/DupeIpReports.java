package by.alis.functionalservercontrol.spigot.Additional.Containers;

import by.alis.functionalservercontrol.spigot.Managers.Kick.KickManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class DupeIpReports {

    private boolean reportExists;
    private String reportInitiator;
    private String time;
    private List<String> dupeIps = new ArrayList<>();
    private Map<Player, String> dupePlayers = new HashMap<>();

    public boolean isReportExists() {
        return reportExists;
    }

    public List<String> getDupeIps() {
        return dupeIps;
    }

    public Map<Player, String> getDupePlayers() {
        return dupePlayers;
    }

    public void setReportExists(boolean reportExists) {
        this.reportExists = reportExists;
    }

    public void setDupeIps(String ip) {
        this.dupeIps.add(ip);
    }

    public void setDupePlayers(Player player) {
        this.dupePlayers.put(player, player.getAddress().getAddress().getHostAddress());
    }

    public void setReportInitiator(String reportInitiator) {
        this.reportInitiator = reportInitiator;
    }

    public String getReportInitiator() {
        return reportInitiator;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getInfo() {
        List<String> info = new ArrayList<>();
        int a = getDupeIps().size();
        int c = 0;
        if(getConfigSettings().getGlobalLanguage().equalsIgnoreCase("ru_RU")) {
            for(int b = 0; b < a; b++) {
                String ip = getDupeIps().get(b);
                List<String> names = new ArrayList<>();
                for(Map.Entry<Player, String> e : getDupePlayers().entrySet()) {
                    if(!names.contains(e.getKey().getName())) {
                        if (e.getValue().equalsIgnoreCase(ip)) {
                            if(e.getKey() != null) {
                                c = c + 1;
                                names.add(e.getKey().getName());
                            }
                        }
                    }
                }
                info.add("&7Было замечено &c%count% &7игроков использующих IP &c%ip%&7: &c%players%".replace("%count%", String.valueOf(c)).replace("%ip%", ip).replace("%players%", String.join(", ", names)));
                c = 0;
                names.clear();
            }
        }
        if(getConfigSettings().getGlobalLanguage().equalsIgnoreCase("en_US")) {
            for(int b = 0; b <= a; b++) {
                String ip = getDupeIps().get(b);
                List<String> names = new ArrayList<>();
                for(Map.Entry<Player, String> e : getDupePlayers().entrySet()) {
                    if(!names.contains(e.getKey().getName())) {
                        if (e.getValue().equalsIgnoreCase(ip)) {
                            if(e.getKey() != null) {
                                c = c + 1;
                                names.add(e.getKey().getName());
                            }
                        }
                    }
                }
                info.add("&c%count% &7players were seen using IP &c%ip%&7: &c%players%".replace("%count%", String.valueOf(c)).replace("%ip%", ip).replace("%players%", String.join(", ", names)));
                c = 0;
                names.clear();
            }
        }
        return String.join("\n", info);
    }

    public void deleteReport() {
        this.reportExists = false;
        this.reportInitiator = null;
        this.dupeIps.clear();
        this.dupePlayers.clear();
        this.time = null;
    }

    public void preformKickDupeip(@NotNull CommandSender initiator, @Nullable String reason, boolean announceKick) {
        KickManager kickManager = new KickManager();
        int count = 0;
        boolean a = initiator instanceof Player;
        for(Map.Entry<Player, String> e : getDupePlayers().entrySet()) {
            if(a)
                if(e.getKey().equals((Player) initiator)) continue;
            count = count + 1;
            kickManager.preformKick(e.getKey(), initiator, reason, announceKick);
        }
        initiator.sendMessage(setColors(getFileAccessor().getLang().getString("commands.dupeip.reports.dupeips-kicked").replace("%1$f", String.valueOf(count))));
    }

}
