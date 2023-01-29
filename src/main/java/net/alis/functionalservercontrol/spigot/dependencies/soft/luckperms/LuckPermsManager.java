package net.alis.functionalservercontrol.spigot.dependencies.soft.luckperms;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.dependencies.Expansions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static org.bukkit.Bukkit.getServer;

public class LuckPermsManager {

    private boolean isLuckPermsSetuped;

    LuckPerms luckPermsProvider;
    private boolean isLuckPermsInstalled() {
        return getServer().getPluginManager().isPluginEnabled("LuckPerms");
    }

    public void setupLuckPerms() {
        if (isLuckPermsInstalled() && !Expansions.getVaultManager().isVaultSetuped()) {
            luckPermsProvider = LuckPermsProvider.get();
            if(!getConfigSettings().isLessInformation()){
                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&e[FunctionalBans -> LuckPerms] LuckPerms detected, connecting..."));
            }
            if (luckPermsProvider != null) {
                this.isLuckPermsSetuped = true;
                if(!getConfigSettings().isLessInformation()){
                    Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&a[FunctionalBans -> LuckPerms] Connection to LuckPerms was successful."));
                }
            } else {
                this.isLuckPermsSetuped = false;
                Bukkit.getConsoleSender().sendMessage(TextUtils.setColors("&c[FunctionalBans -> LuckPerms] Failed to connect to LuckPerms"));
            }
        }
    }

    public boolean isLuckPermsSetuped() {
        return isLuckPermsSetuped;
    }

    public String getPlayerGroup(Player player) {
        try {
            return this.luckPermsProvider.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup();
        } catch (NullPointerException ignored) {
            return null;
        }
    }

    public String getPlayerGroup(FunctionalPlayer player) {
        try {
            return this.luckPermsProvider.getUserManager().getUser(player.getUniqueId()).getPrimaryGroup();
        } catch (NullPointerException ignored) {
            return null;
        }
    }

}
