package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.cooldowns.Cooldowns;
import net.alis.functionalservercontrol.api.enums.MuteType;
import net.alis.functionalservercontrol.spigot.managers.AdvertiseManager;
import net.alis.functionalservercontrol.spigot.managers.mute.MuteChecker;
import net.alis.functionalservercontrol.spigot.managers.mute.MuteManager;
import net.alis.functionalservercontrol.spigot.managers.GlobalCommandManager;
import net.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import net.alis.functionalservercontrol.spigot.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getMutedPlayersContainer;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class PlayerCommandsListener implements Listener {

    private final GlobalCommandManager commandManager = new GlobalCommandManager();

    @EventHandler
    public void onPlayerSendCommand(PlayerCommandPreprocessEvent event) {
        FunctionalPlayer player = FunctionalPlayer.get(event.getPlayer().getName());
        String command = event.getMessage();
        String[] args = command.split(" ");
        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(CheatCheckerManager.getCheatCheckerManager().isPlayerChecking(player)) {
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

        if(ChatManager.getChatManager().isCommandContainsBlockedWord(player, command)) {
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

        if(Cooldowns.getCooldowns().playerHasCooldown(player, command.split(" ")[0].substring(1))) {
            Cooldowns.getCooldowns().notifyAboutCooldown(player, command.split(" ")[0].substring(1));
            event.setCancelled(true);
            return;
        } else {
            Cooldowns.getCooldowns().setUpCooldown(player, command.split(" ")[0].substring(1), command.split(" ").length - 1);
        }

        if(getCommandLimiterSettings().isFunctionEnabled()) {
            if(!this.commandManager.isPlayerCanUseCommand(player, command)) {
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
                        player.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.permissions-protection.player-cannot-be-operator").replace("%1$f", args[1])));
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
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.mute")
                                .replace("%1$f", player.getName()).replace("%2$f", event.getMessage()).replace("%3$f", translatedTime))
                        );
                    }
                    if (getConfigSettings().isPlayersNotification()) {
                        for (FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                            if (player.hasPermission("functionalservercontrol.notification.mute")) {
                                admin.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.mute")
                                        .replace("%1$f", player.getName()).replace("%2$f", event.getMessage()).replace("%3$f", translatedTime))
                                );
                            }
                        }
                    }
                } else {
                    MuteType muteType = BaseManager.getBaseManager().getMuteTypes().get(BaseManager.getBaseManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId())));
                    long unmuteTime = BaseManager.getBaseManager().getUnmuteTimes().get(BaseManager.getBaseManager().getMutedUUIDs().indexOf(String.valueOf(player.getUniqueId())));
                    event.setCancelled(true);
                    muteManager.notifyAboutMuteOnCommand(player);
                    String translatedTime = getGlobalVariables().getVariableNever();
                    if (muteType != MuteType.PERMANENT_IP && muteType != MuteType.PERMANENT_NOT_IP) {
                        translatedTime = timeSettingsAccessor.getTimeManager().convertFromMillis(timeSettingsAccessor.getTimeManager().getPunishTime(unmuteTime));
                    }
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.mute")
                                .replace("%1$f", player.getName()).replace("%2$f", event.getMessage()).replace("%3$f", translatedTime))
                        );
                    }
                    if (getConfigSettings().isPlayersNotification()) {
                        for (FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                            if (player.hasPermission("functionalservercontrol.notification.mute")) {
                                admin.sendMessage(TextUtils.setColors(getFileAccessor().getLang().getString("other.notifications.mute")
                                        .replace("%1$f", player.getName()).replace("%2$f", event.getMessage()).replace("%3$f", translatedTime))
                                );
                            }
                        }
                    }
                }
            }
        }
    }

}
