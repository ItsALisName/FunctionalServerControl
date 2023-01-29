package net.alis.functionalservercontrol.spigot.additional.tasks;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.spigot.managers.cooldowns.Cooldowns;
import net.alis.functionalservercontrol.spigot.managers.cooldowns.TrackedCommand;
import net.alis.functionalservercontrol.spigot.dependencies.Expansions;
import org.bukkit.scheduler.BukkitRunnable;

import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;

public class CooldownsTask extends BukkitRunnable {

    @Override
    public void run() {
        if (Cooldowns.getCooldowns().isFunctionEnabled()) {
            for (FunctionalPlayer player : FunctionalApi.getOnlinePlayers()) {
                for (TrackedCommand tc : Cooldowns.getCooldowns().getTrackedCommands()) {
                    if (tc.getPlayers().containsKey(player.getFunctionalId())) {
                        if (Cooldowns.getCooldowns().isUseGroups()) {
                            if (Expansions.getVaultManager().isVaultSetuped() && tc.getGroupsTime().containsKey(Expansions.getVaultManager().getPlayerGroup(player))) {
                                if (((tc.getPlayers().get(player.getFunctionalId()) + (tc.getGroupsTime().get(Expansions.getVaultManager().getPlayerGroup(player)) * 1000)) - System.currentTimeMillis()) < 0) {
                                    TrackedCommand trackedCommand = Cooldowns.getCooldowns().getTrackedCommands().get(Cooldowns.getCooldowns().getTrackedCommands().indexOf(tc));
                                    trackedCommand.removePlayer(player);
                                    if (trackedCommand.isSendExpireMessage()) {
                                        if (trackedCommand.isExpireMessageAsTitle()) {
                                            player.title(setColors(trackedCommand.getExpireMessage()));
                                        } else {
                                            player.sendMessage(setColors(trackedCommand.getExpireMessage()));
                                        }
                                        return;
                                    }
                                }
                                return;
                            }
                            if (Expansions.getLuckPermsManager().isLuckPermsSetuped() && tc.getGroupsTime().containsKey(Expansions.getLuckPermsManager().getPlayerGroup(player))) {
                                if (((tc.getPlayers().get(player.getFunctionalId()) + (tc.getGroupsTime().get(Expansions.getLuckPermsManager().getPlayerGroup(player)) * 1000)) - System.currentTimeMillis()) < 0) {
                                    TrackedCommand trackedCommand = Cooldowns.getCooldowns().getTrackedCommands().get(Cooldowns.getCooldowns().getTrackedCommands().indexOf(tc));
                                    trackedCommand.removePlayer(player);
                                    if (trackedCommand.isSendExpireMessage()) {
                                        if (trackedCommand.isExpireMessageAsTitle()) {
                                            player.title(setColors(trackedCommand.getExpireMessage()));
                                        } else {
                                            player.sendMessage(setColors(trackedCommand.getExpireMessage()));
                                        }
                                        return;
                                    }
                                }
                                return;
                            }
                            if (((tc.getPlayers().get(player.getFunctionalId()) + (tc.getCooldownTime() * 1000)) - System.currentTimeMillis()) < 0) {
                                TrackedCommand trackedCommand = Cooldowns.getCooldowns().getTrackedCommands().get(Cooldowns.getCooldowns().getTrackedCommands().indexOf(tc));
                                trackedCommand.removePlayer(player);
                                if (trackedCommand.isSendExpireMessage()) {
                                    if (trackedCommand.isExpireMessageAsTitle()) {
                                        player.title(setColors(trackedCommand.getExpireMessage()));
                                    } else {
                                        player.sendMessage(setColors(trackedCommand.getExpireMessage()));
                                    }
                                    return;
                                }
                            }
                            return;
                        } else {
                            if (((tc.getPlayers().get(player.getFunctionalId()) + (tc.getCooldownTime() * 1000)) - System.currentTimeMillis()) < 0) {
                                TrackedCommand trackedCommand = Cooldowns.getCooldowns().getTrackedCommands().get(Cooldowns.getCooldowns().getTrackedCommands().indexOf(tc));
                                trackedCommand.removePlayer(player);
                                if (trackedCommand.isSendExpireMessage()) {
                                    if (trackedCommand.isExpireMessageAsTitle()) {
                                        player.title(setColors(trackedCommand.getExpireMessage()));
                                    } else {
                                        player.sendMessage(setColors(trackedCommand.getExpireMessage()));
                                    }
                                    return;
                                }
                            }
                            return;
                        }
                    }
                }
            }
        }
    }
}
