package by.alis.functionalservercontrol.spigot.Additional.TimerTasks;

import by.alis.functionalservercontrol.API.Enums.MuteType;
import by.alis.functionalservercontrol.spigot.Additional.CoreAdapters.CoreAdapter;
import by.alis.functionalservercontrol.spigot.Managers.Mute.UnmuteManager;
import by.alis.functionalservercontrol.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            if (getSQLiteManager().getMutedUUIDs().contains(String.valueOf(player.getUniqueId()))) {
                                int indexOf = getSQLiteManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId()));
                                MuteType muteType = getSQLiteManager().getMuteTypes().get(indexOf);
                                String translatedUnmuteTime = getGlobalVariables().getVariableNever();
                                long muteTime = getSQLiteManager().getUnmuteTimes().get(indexOf);
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
                        case H2: {}
                        case MYSQL: {}
                    }
                }
            }
        }
    }

}
