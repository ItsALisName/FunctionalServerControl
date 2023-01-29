package net.alis.functionalservercontrol.spigot.additional.containers;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.KickManager;
import net.alis.functionalservercontrol.spigot.managers.file.SFAccessor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class DupeIpReports {

    private boolean reportExists;
    private String reportInitiator;
    private String time;
    private final List<String> dupeIps = new ArrayList<>();
    private final Map<FunctionalPlayer, String> dupePlayers = new HashMap<>();

    public boolean isReportExists() {
        return reportExists;
    }

    public List<String> getDupeIps() {
        return dupeIps;
    }

    public Map<FunctionalPlayer, String> getDupePlayers() {
        return dupePlayers;
    }

    public void setReportExists(boolean reportExists) {
        this.reportExists = reportExists;
    }

    public void setDupeIps(String ip) {
        this.dupeIps.add(ip);
    }

    public void setDupePlayers(FunctionalPlayer player) {
        this.dupePlayers.put(player, player.address());
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
                for(Map.Entry<FunctionalPlayer, String> e : getDupePlayers().entrySet()) {
                    if(!names.contains(e.getKey().nickname())) {
                        if (e.getValue().equalsIgnoreCase(ip)) {
                            if(e.getKey() != null) {
                                c = c + 1;
                                names.add(e.getKey().nickname());
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
                for(Map.Entry<FunctionalPlayer, String> e : getDupePlayers().entrySet()) {
                    if(!names.contains(e.getKey().nickname())) {
                        if (e.getValue().equalsIgnoreCase(ip)) {
                            if(e.getKey() != null) {
                                c = c + 1;
                                names.add(e.getKey().nickname());
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
        boolean a = initiator instanceof FunctionalPlayer;
        for(Map.Entry<FunctionalPlayer, String> e : getDupePlayers().entrySet()) {
            if(a)
                if(e.getKey().equals(initiator)) continue;
            count = count + 1;
            kickManager.preformKick(e.getKey(), initiator, reason, announceKick);
        }
        initiator.sendMessage(TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("commands.dupeip.reports.dupeips-kicked").replace("%1$f", String.valueOf(count))));
    }

}
