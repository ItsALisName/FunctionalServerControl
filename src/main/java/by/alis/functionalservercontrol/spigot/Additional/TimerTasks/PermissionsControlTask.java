package by.alis.functionalservercontrol.spigot.Additional.TimerTasks;

import by.alis.functionalservercontrol.spigot.Expansions.Expansions;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

public class PermissionsControlTask extends BukkitRunnable {

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(player.isOp() && !getConfigSettings().getOpAllowedPlayers().contains(player.getName())) {
                if(getConfigSettings().isPermissionsProtectionAutoDeop()) player.setOp(false);
                for(String action : getConfigSettings().getOpProtectionActions()) Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName())));
            }
            if(player.isOnline()) {
                for(String permission : getConfigSettings().getProtectedPermissions()) {
                    if(player.hasPermission(permission) && !getConfigSettings().getPermissionAllowedPlayers().contains(player.getName())) {
                        for(String action : getConfigSettings().getPermissionsProtectionActions()) Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", permission)));
                    }
                }
            }
            if(player.isOnline()) {
                if(Expansions.getVaultManager().isVaultSetuped()) {
                    String playerGroup = Expansions.getVaultManager().getPlayerGroup(player);
                    if(getConfigSettings().getProtectedGroups().contains(playerGroup) && !getConfigSettings().getGroupAllowedPlayers().contains(player.getName())) {
                        for(String action : getConfigSettings().getGroupProtectionActions()) Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", playerGroup)));
                    }
                    continue;
                }
                if(Expansions.getLuckPermsManager().isLuckPermsSetuped()) {
                    String playerGroup = Expansions.getLuckPermsManager().getPlayerGroup(player);
                    if(getConfigSettings().getProtectedGroups().contains(playerGroup) && !getConfigSettings().getGroupAllowedPlayers().contains(player.getName())) {
                        for(String action : getConfigSettings().getGroupProtectionActions()) Bukkit.getScheduler().runTask(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", playerGroup)));
                    }
                    continue;
                }
            }
        }
    }

}
