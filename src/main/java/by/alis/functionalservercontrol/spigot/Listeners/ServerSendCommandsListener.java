package by.alis.functionalservercontrol.spigot.Listeners;

import by.alis.functionalservercontrol.spigot.Expansions.Expansions;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getCommandLimiterSettings;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

public class ServerSendCommandsListener implements Listener {

    @EventHandler
    public void onServerSendCommand(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        if (getConfigSettings().hideMainCommand()) {
            event.getCommands().removeIf(cmd -> cmd.contains("fsc") || cmd.contains("functionalservercontrol") || cmd.contains("fscontrol"));
        }
        if (getCommandLimiterSettings().isHideCompletionsFully()) {
            if (getCommandLimiterSettings().isBlockSyntaxCommand()) {
                event.getCommands().removeIf(cmd -> cmd.contains(":"));
            }
            if (getCommandLimiterSettings().isUseGroups()) {
                if (Expansions.getVaultManager().isVaultSetuped()) {
                    String playerGroup = Expansions.getVaultManager().getPlayerGroup(player);
                    if (getCommandLimiterSettings().getGlobalBlockedCommands().containsKey(playerGroup)) {
                        World world = player.getWorld();
                        if (getCommandLimiterSettings().getPerWorldBlockedCommands().containsKey(world)) {
                            if (getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                                event.getCommands().clear();
                                event.getCommands().addAll(getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get(playerGroup));
                            } else {
                                event.getCommands().removeAll(getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get(playerGroup));
                            }
                            return;
                        }
                        if (getCommandLimiterSettings().isGlobalUseAsWhiteList()) {
                            event.getCommands().clear();
                            event.getCommands().addAll(getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup));
                        } else {
                            event.getCommands().removeAll(getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup));
                        }
                        return;
                    }
                }
                if (Expansions.getLuckPermsManager().isLuckPermsSetuped()) {
                    if (getCommandLimiterSettings().getGlobalBlockedCommands().containsKey(Expansions.getLuckPermsManager().getPlayerGroup(player))) {
                        String playerGroup = Expansions.getLuckPermsManager().getPlayerGroup(player);
                        World world = player.getWorld();
                        if (getCommandLimiterSettings().getPerWorldBlockedCommands().containsKey(world)) {
                            if (getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                                event.getCommands().clear();
                                event.getCommands().addAll(getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get(playerGroup));
                            } else {
                                event.getCommands().removeAll(getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get(playerGroup));
                            }
                            return;
                        }
                        if (getCommandLimiterSettings().isGlobalUseAsWhiteList()) {
                            event.getCommands().clear();
                            event.getCommands().addAll(getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup));
                        } else {
                            event.getCommands().removeAll(getCommandLimiterSettings().getGlobalBlockedCommands().get(playerGroup));
                        }
                        return;
                    }
                }
            }
            World world = player.getWorld();
            if (getCommandLimiterSettings().getPerWorldBlockedCommands().containsKey(world)) {
                if (getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                    event.getCommands().clear();
                    event.getCommands().addAll(getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get("global"));
                } else {
                    event.getCommands().removeAll(getCommandLimiterSettings().getPerWorldBlockedCommands().get(world).get("global"));
                }
                return;
            }
            if (getCommandLimiterSettings().isGlobalUseAsWhiteList()) {
                event.getCommands().clear();
                event.getCommands().addAll(getCommandLimiterSettings().getGlobalBlockedCommands().get("global"));
                return;
            } else {
                event.getCommands().removeAll(getCommandLimiterSettings().getGlobalBlockedCommands().get("global"));
            }
        }
    }

}
