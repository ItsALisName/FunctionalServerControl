package by.alis.functionalservercontrol.spigot.listeners;

import by.alis.functionalservercontrol.api.enums.MuteType;
import by.alis.functionalservercontrol.spigot.managers.AdvertiseManager;
import by.alis.functionalservercontrol.spigot.managers.mute.MuteChecker;
import by.alis.functionalservercontrol.spigot.managers.mute.MuteManager;
import by.alis.functionalservercontrol.spigot.managers.GlobalCommandManager;
import by.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.*;
import static by.alis.functionalservercontrol.spigot.additional.misc.cooldowns.Cooldowns.getCooldowns;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.ChatManager.getChatManager;
import static by.alis.functionalservercontrol.spigot.managers.CheatCheckerManager.getCheatCheckerManager;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class PlayerCommandsListener implements Listener {

    private final GlobalCommandManager commandManager = new GlobalCommandManager();

    @EventHandler
    public void onPlayerSendCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String command = event.getMessage();
        String[] args = command.split(" ");


        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(getCheatCheckerManager().isPlayerChecking(player)) {
                if (getConfigSettings().isPreventCommandsDuringCheatCheck()) {
                    if (!getConfigSettings().getIgnoredCommandsDuruingCheatCheck().contains(command.split(" ")[0])) {
                        if (!event.isCancelled()) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }

        if(getChatManager().isCommandContainsBlockedWord(player, command)) {
            event.setCancelled(true);
            return;
        }

        if(AdvertiseManager.isCommandContainsAdvertise(player, command)) {
            event.setCancelled(true);
            return;
        }

        if(commandManager.preventReloadCommand(player, command)) {
            event.setCancelled(true);
            return;
        }

        if(getCooldowns().playerHasCooldown(player, command.split(" ")[0].substring(1))) {
            getCooldowns().notifyAboutCooldown(player, command.split(" ")[0].substring(1));
            event.setCancelled(true);
            return;
        } else {
            getCooldowns().setUpCooldown(player, command.split(" ")[0].substring(1), command.split(" ").length - 1);
        }

        if(getCommandLimiterSettings().isFunctionEnabled()) {
            if(!this.commandManager.isPlayerCanUseCommand(player, command.split(" ")[0])) {
                event.setCancelled(true);
            }
        }

        if(getConfigSettings().isReplaceMinecraftCommand()) {
            event.setMessage("/" + this.commandManager.replaceMinecraftCommand(command));
        }

        if(getConfigSettings().isPermissionsProtectionEnabled()) {
            if ((command.startsWith("/op") || command.startsWith("/minecraft:op")) && args.length >= 2) {
                if (player.hasPermission("bukkit.command.op.give") || player.hasPermission("minecraft.command.op")) {
                    if (!getConfigSettings().getOpAllowedPlayers().contains(args[1])) {
                        event.setCancelled(true);
                        player.sendMessage(setColors(getFileAccessor().getLang().getString("other.permissions-protection.player-cannot-be-operator").replace("%1$f", args[1])));
                        return;
                    }
                }
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
                    }
                }
            }
        }
    }

}
