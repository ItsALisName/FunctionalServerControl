package by.alis.functionalservercontrol.spigot.listeners;

import by.alis.functionalservercontrol.spigot.expansions.Expansions;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getCommandLimiterSettings;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class ServerSendCommandsListener implements Listener {

    @EventHandler
    public void onServerSendCommand(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        if (getConfigSettings().hideMainCommand() && !player.hasPermission("functionalservercontrol.commands.bypass")) {
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
                        
                        
                        if (getCommandLimiterSettings().getPerWorldGroups().contains(playerGroup) && getCommandLimiterSettings().getPerGroupWorlds().get(getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup)) == world) {
                            int indexOf = getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup);
                            if (getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                                event.getCommands().clear();
                                event.getCommands().addAll(getCommandLimiterSettings().getPerGroupCommands().get(indexOf));
                            } else {
                                event.getCommands().removeAll(getCommandLimiterSettings().getPerGroupCommands().get(indexOf));
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
                         
                        if (getCommandLimiterSettings().getPerWorldGroups().contains(playerGroup) && getCommandLimiterSettings().getPerGroupWorlds().get(getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup)) == world) {
                            int indexOf = getCommandLimiterSettings().getPerWorldGroups().indexOf(playerGroup);
                            if (getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                                event.getCommands().clear();
                                event.getCommands().addAll(getCommandLimiterSettings().getPerGroupCommands().get(indexOf));
                            } else {
                                event.getCommands().removeAll(getCommandLimiterSettings().getPerGroupCommands().get(indexOf));
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
            if (getCommandLimiterSettings().getPerWorldGroups().contains("global") && getCommandLimiterSettings().getPerGroupWorlds().get(getCommandLimiterSettings().getPerWorldGroups().indexOf("global")) == world) {
                int indexOf = getCommandLimiterSettings().getPerWorldGroups().indexOf("global");
                if (getCommandLimiterSettings().isPerWorldUseAsWhiteList()) {
                    event.getCommands().clear();
                    event.getCommands().addAll(getCommandLimiterSettings().getPerGroupCommands().get(indexOf));
                } else {
                    event.getCommands().removeAll(getCommandLimiterSettings().getPerGroupCommands().get(indexOf));
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
