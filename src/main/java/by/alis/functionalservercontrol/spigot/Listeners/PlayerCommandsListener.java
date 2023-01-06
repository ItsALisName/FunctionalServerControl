package by.alis.functionalservercontrol.spigot.Listeners;

import by.alis.functionalservercontrol.API.Enums.MuteType;
import by.alis.functionalservercontrol.spigot.Managers.Mute.MuteChecker;
import by.alis.functionalservercontrol.spigot.Managers.Mute.MuteManager;
import by.alis.functionalservercontrol.spigot.Managers.PlayerCommandManager;
import by.alis.functionalservercontrol.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.*;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.Cooldowns.Cooldowns.getCooldowns;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.CheatCheckerManager.getCheatCheckerManager;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class PlayerCommandsListener implements Listener {

    @EventHandler
    public void onPlayerSendCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();
        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(getCheatCheckerManager().isPlayerChecking(player)) {
                if (getConfigSettings().isPreventCommandsDuringCheck()) {
                    if (!getConfigSettings().getIgnoredCommandsDuruingCheck().contains(command.split(" ")[0])) {
                        if (!event.isCancelled()) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }

        if(getCooldowns().playerHasCooldown(player, command.split(" ")[0].substring(1))) {
            getCooldowns().notifyAboutCooldown(player, command.split(" ")[0].substring(1));
            event.setCancelled(true);
            return;
        } else {
            getCooldowns().setUpCooldown(player, command.split(" ")[0].substring(1), command.split(" ").length - 1);
        }

        if(getCommandLimiterSettings().isFunctionEnabled()) {
            if(!new PlayerCommandManager().isPlayerCanUseCommand(player, command.split(" ")[0])) {
                event.setCancelled(true);
            }
        }

        if(MuteChecker.isPlayerMuted(player)) {
            if (getConfigSettings().getDisabledCommandsWhenMuted().contains(command.split(" ")[0])) {
                MuteManager muteManager = new MuteManager();
                TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    MuteType muteType = getMutedPlayersContainer().getMuteTypesContainer().get(getMutedPlayersContainer().getUUIDContainer().indexOf(String.valueOf(player.getUniqueId())));
                    long unmuteTime = getMutedPlayersContainer().getMuteTimeContainer().get(getMutedPlayersContainer().getUUIDContainer().indexOf(String.valueOf(player.getUniqueId())));
                    event.setCancelled(true);
                    muteManager.notifyAboutMuteOnCommand(player);
                    String translatedTime = getGlobalVariables().getVariableNever();
                    if (muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                        translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(unmuteTime));
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
                            muteManager.notifyAboutMuteOnCommand(player);
                            String translatedTime = getGlobalVariables().getVariableNever();
                            if (muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                                translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(unmuteTime));
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
                        case H2: {
                        }
                        case MYSQL: {
                        }
                    }
                }
            }
        }
    }

}
