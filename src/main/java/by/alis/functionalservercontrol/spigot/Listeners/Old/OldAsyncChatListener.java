package by.alis.functionalservercontrol.spigot.Listeners.Old;

import by.alis.functionalservercontrol.API.Enums.MuteType;
import by.alis.functionalservercontrol.spigot.Managers.Mute.MuteManager;
import by.alis.functionalservercontrol.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;
import static by.alis.functionalservercontrol.spigot.Managers.Mute.MuteChecker.isPlayerMuted;

public class OldAsyncChatListener implements Listener, EventExecutor {
    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(isPlayerMuted(player)) {
            if(!event.isCancelled()) {
                if(getConfigSettings().isAllowedUseRamAsContainer()){
                    MuteType muteType = getMutedPlayersContainer().getMuteTypesContainer().get(getMutedPlayersContainer().getUUIDContainer().indexOf(String.valueOf(player.getUniqueId())));
                    long unmuteTime = getMutedPlayersContainer().getMuteTimeContainer().get(getMutedPlayersContainer().getUUIDContainer().indexOf(String.valueOf(player.getUniqueId())));
                    event.setCancelled(true);
                    MuteManager muteManager = new MuteManager();
                    muteManager.notifyAboutMuteOnChat(player);
                    String translatedTime = getGlobalVariables().getVariableNever();
                    if (muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                        translatedTime = this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(unmuteTime));
                    }
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.mute")
                                .replace("%1$f", player.getName()).replace("%2$f", event.getMessage()).replace("%3$f", translatedTime))
                        );
                    }
                    if (getConfigSettings().isPlayersNotification()) {
                        for (Player admin : Bukkit.getOnlinePlayers()) {
                            if (player.hasPermission("functionalservercontrol.notification.mute")) {
                                admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.mute")
                                        .replace("%1$f", player.getName()).replace("%2$f", event.getMessage()).replace("%3$f", translatedTime))
                                );
                            }
                        }
                    }
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            MuteType muteType = getSQLiteManager().getMuteTypes().get(getSQLiteManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId())));
                            long unmuteTime = getSQLiteManager().getUnmuteTimes().get(getSQLiteManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId())));
                            event.setCancelled(true);
                            MuteManager muteManager = new MuteManager();
                            muteManager.notifyAboutMuteOnChat(player);
                            String translatedTime = getGlobalVariables().getVariableNever();
                            if (muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                                translatedTime = this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(unmuteTime));
                            }
                            if (getConfigSettings().isConsoleNotification()) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.mute")
                                        .replace("%1$f", player.getName()).replace("%2$f", event.getMessage()).replace("%3$f", translatedTime))
                                );
                            }
                            if (getConfigSettings().isPlayersNotification()) {
                                for (Player admin : Bukkit.getOnlinePlayers()) {
                                    if (player.hasPermission("functionalservercontrol.notification.mute")) {
                                        admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.mute")
                                                .replace("%1$f", player.getName()).replace("%2$f", event.getMessage()).replace("%3$f", translatedTime))
                                        );
                                    }
                                }
                            }
                        }
                        case H2: {}
                        case MYSQL: {}
                    }
                }
            }
        }
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) {
        if (listener == this && event instanceof AsyncPlayerChatEvent) {
            if (((AsyncPlayerChatEvent)event).isCancelled())
                return;
            onPlayerChat((AsyncPlayerChatEvent)event);
        }
    }
}
