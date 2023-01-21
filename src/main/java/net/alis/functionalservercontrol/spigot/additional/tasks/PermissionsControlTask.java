package net.alis.functionalservercontrol.spigot.additional.tasks;

import net.alis.functionalservercontrol.spigot.dependencies.Expansions;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class PermissionsControlTask extends BukkitRunnable {

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.isOp() && !getConfigSettings().getOpAllowedPlayers().contains(player.getName())) {
                if(getConfigSettings().isPermissionsProtectionAutoDeop()) player.setOp(false);
                for(String action : getConfigSettings().getOpProtectionActions()) TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName())));
            }
            if(player.isOnline()) {
                for(String permission : getConfigSettings().getProtectedPermissions()) {
                    if(player.hasPermission(permission) && !getConfigSettings().getPermissionAllowedPlayers().contains(player.getName())) {
                        for(String action : getConfigSettings().getPermissionsProtectionActions()) TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", permission)));
                    }
                }
            }
            if(player.isOnline()) {
                if(Expansions.getVaultManager().isVaultSetuped()) {
                    String playerGroup = Expansions.getVaultManager().getPlayerGroup(player);
                    if(getConfigSettings().getProtectedGroups().contains(playerGroup) && !getConfigSettings().getGroupAllowedPlayers().contains(player.getName())) {
                        for(String action : getConfigSettings().getGroupProtectionActions()) TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", playerGroup)));
                    }
                    continue;
                }
                if(Expansions.getLuckPermsManager().isLuckPermsSetuped()) {
                    String playerGroup = Expansions.getLuckPermsManager().getPlayerGroup(player);
                    if(getConfigSettings().getProtectedGroups().contains(playerGroup) && !getConfigSettings().getGroupAllowedPlayers().contains(player.getName())) {
                        for(String action : getConfigSettings().getGroupProtectionActions()) TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", playerGroup)));
                    }
                    continue;
                }
            }
        }
    }

}
