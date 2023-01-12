package by.alis.functionalservercontrol.spigot.additional.tasks;

import by.alis.functionalservercontrol.api.enums.MuteType;
import by.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import by.alis.functionalservercontrol.spigot.managers.mute.UnmuteManager;
import by.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class MuteGlobalTask extends BukkitRunnable {
    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();

    @Override
    public void run() {
        if(getConfigSettings().isSendActionbarWhileMuted()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    if (getMutedPlayersContainer().getUUIDContainer().contains(String.valueOf(player.getUniqueId()))) {
                        int indexOf = getMutedPlayersContainer().getUUIDContainer().indexOf(String.valueOf(player.getUniqueId()));
                        MuteType muteType = getMutedPlayersContainer().getMuteTypesContainer().get(indexOf);
                        String translatedUnmuteTime = getGlobalVariables().getVariableNever();
                        long muteTime = getMutedPlayersContainer().getMuteTimeContainer().get(indexOf);
                        if (muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                            translatedUnmuteTime = this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(muteTime));
                            if(System.currentTimeMillis() >= muteTime) {
                                UnmuteManager unmuteManager = new UnmuteManager();
                                unmuteManager.preformUnmute(player, getConfigSettings().getMuteTimeExpired());
                                break;
                            }
                        }
                        CoreAdapter.getAdapter().sendActionBar(player, setColors(getFileAccessor().getLang().getString("other.actionbar.mute-format").replace("%1$f", setColors(translatedUnmuteTime))));
                    }
                } else {
                    if (getBaseManager().getMutedUUIDs().contains(String.valueOf(player.getUniqueId()))) {
                        int indexOf = getBaseManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId()));
                        MuteType muteType = getBaseManager().getMuteTypes().get(indexOf);
                        String translatedUnmuteTime = getGlobalVariables().getVariableNever();
                        long muteTime = getBaseManager().getUnmuteTimes().get(indexOf);
                        if (muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                            translatedUnmuteTime = this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(muteTime));
                            if(System.currentTimeMillis() >= muteTime) {
                                UnmuteManager unmuteManager = new UnmuteManager();
                                unmuteManager.preformUnmute(player, getConfigSettings().getMuteTimeExpired());
                                break;
                            }
                        }
                        CoreAdapter.getAdapter().sendActionBar(player, setColors(getFileAccessor().getLang().getString("other.actionbar.mute-format").replace("%1$f", setColors(translatedUnmuteTime))));
                    }
                }
            }
        }
    }

}
