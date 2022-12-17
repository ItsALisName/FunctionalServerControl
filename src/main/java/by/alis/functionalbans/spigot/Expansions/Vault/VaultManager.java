package by.alis.functionalbans.spigot.Expansions.Vault;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;
import static org.bukkit.Bukkit.getServer;

public class VaultManager {


    private boolean vaultSetuped;
    private Permission permission;
    RegisteredServiceProvider<Permission> permissionRegisteredServiceProvider;

    public void setupVaultPermissions() {
        if(vaultInstalled()) {
            if(permissionsProviderInstalled()) {
                if(!getConfigSettings().isLessInformation()){
                    Bukkit.getConsoleSender().sendMessage(setColors("&e[Functionalbans -> Vault] Vault detected, connecting..."));
                }
                permission = getPermissionRegisteredServiceProvider().getProvider();
                if(permission != null) {
                    this.vaultSetuped = true;
                    if(!getConfigSettings().isLessInformation()){
                        Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalBans -> Vault] Connection to Vault was successful"));
                    }
                } else {
                    this.vaultSetuped = false;
                    Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalBans -> Vault] Failed to connect to Vault"));
                }
            }
        }
    }

    public boolean isVaultSetuped() {
        return vaultSetuped;
    }

    private boolean vaultInstalled() {
        return getServer().getPluginManager().isPluginEnabled("Vault");
    }

    private boolean permissionsProviderInstalled() {
        permissionRegisteredServiceProvider = getServer().getServicesManager().getRegistration(Permission.class);
        return permissionRegisteredServiceProvider != null;
    }

    private RegisteredServiceProvider<Permission> getPermissionRegisteredServiceProvider() {
        return permissionRegisteredServiceProvider;
    }

    private Permission getPermission() {
        return permission;
    }

    public String getPlayerGroup(Player player) {
        return getPermission().getPrimaryGroup(player);
    }

    public String[] getPlayerGroups(Player player) {
        return getPermission().getPlayerGroups(player);
    }

    public String[] getAllGroups() {
        return getPermission().getGroups();
    }

}
