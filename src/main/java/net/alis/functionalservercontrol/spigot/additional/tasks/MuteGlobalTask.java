package net.alis.functionalservercontrol.spigot.additional.tasks;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.enums.MuteType;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.libraries.net.md_5.bungee.api.ChatMessageType;
import net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.spigot.managers.file.SFAccessor;
import net.alis.functionalservercontrol.spigot.managers.mute.UnmuteManager;
import net.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.scheduler.BukkitRunnable;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;

public class MuteGlobalTask extends BukkitRunnable {
    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();

    @Override
    public void run() {
        if(getConfigSettings().isSendActionbarWhileMuted()) {
            for (FunctionalPlayer player : FunctionalApi.getOnlinePlayers()) {
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    if (StaticContainers.getMutedPlayersContainer().getUUIDContainer().contains(String.valueOf(player.getUniqueId()))) {
                        int indexOf = StaticContainers.getMutedPlayersContainer().getUUIDContainer().indexOf(String.valueOf(player.getUniqueId()));
                        MuteType muteType = StaticContainers.getMutedPlayersContainer().getMuteTypesContainer().get(indexOf);
                        String translatedUnmuteTime = getGlobalVariables().getVariableNever();
                        long muteTime = StaticContainers.getMutedPlayersContainer().getMuteTimeContainer().get(indexOf);
                        if (muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                            translatedUnmuteTime = this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(muteTime));
                            if(System.currentTimeMillis() >= muteTime) {
                                UnmuteManager unmuteManager = new UnmuteManager();
                                unmuteManager.preformUnmute(player, getConfigSettings().getMuteTimeExpired());
                                break;
                            }
                        }
                        player.expansion().message(ChatMessageType.ACTION_BAR, TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.actionbar.mute-format").replace("%1$f", TextUtils.setColors(translatedUnmuteTime))));
                    }
                } else {
                    if (BaseManager.getBaseManager().getMutedUUIDs().contains(String.valueOf(player.getUniqueId()))) {
                        int indexOf = BaseManager.getBaseManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId()));
                        MuteType muteType = BaseManager.getBaseManager().getMuteTypes().get(indexOf);
                        String translatedUnmuteTime = getGlobalVariables().getVariableNever();
                        long muteTime = BaseManager.getBaseManager().getUnmuteTimes().get(indexOf);
                        if (muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                            translatedUnmuteTime = this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(muteTime));
                            if(System.currentTimeMillis() >= muteTime) {
                                UnmuteManager unmuteManager = new UnmuteManager();
                                unmuteManager.preformUnmute(player, getConfigSettings().getMuteTimeExpired());
                                break;
                            }
                        }
                        player.expansion().message(ChatMessageType.ACTION_BAR, TextUtils.setColors(SFAccessor.getFileAccessor().getLang().getString("other.actionbar.mute-format").replace("%1$f", TextUtils.setColors(translatedUnmuteTime))));
                    }
                }
            }
        }
    }

}
