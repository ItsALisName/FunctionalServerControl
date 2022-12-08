package by.alis.functionalbans.spigot.Expansions.Vault;

import by.alis.functionalbans.spigot.Additional.GlobalSettings.Languages.LangEnglish;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.Languages.LangRussian;

import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.VAULT_FINDED));
                        break;
                    case "en_US":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.VAULT_FINDED));
                        break;
                    default:
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.VAULT_FINDED));
                        break;
                }
                permission = getPermissionRegisteredServiceProvider().getProvider();
                if(permission != null) {
                    this.vaultSetuped = true;
                    switch (getConfigSettings().getConsoleLanguageMode()) {
                        case "ru_RU":
                            Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.VAULT_HOOKED));
                            break;
                        case "en_US":
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.VAULT_HOOKED));
                            break;
                        default:
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.VAULT_HOOKED));
                            break;
                    }
                } else {
                    this.vaultSetuped = false;
                    switch (getConfigSettings().getConsoleLanguageMode()) {
                        case "ru_RU":
                            Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.VAULT_ERROR));
                            break;
                        case "en_US":
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.VAULT_ERROR));
                            break;
                        default:
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.VAULT_ERROR));
                            break;
                    }
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
