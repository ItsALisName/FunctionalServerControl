package by.alis.functionalservercontrol.spigot.Expansions.LuckPerms;

import by.alis.functionalservercontrol.spigot.Expansions.Expansions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

import java.util.Collection;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.Other.TextUtils.setColors;
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
                Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalBans -> LuckPerms] LuckPerms detected, connecting..."));
            }
            if (luckPermsProvider != null) {
                this.isLuckPermsSetuped = true;
                if(!getConfigSettings().isLessInformation()){
                    Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalBans -> LuckPerms] Connection to LuckPerms was successful."));
                }
            } else {
                this.isLuckPermsSetuped = false;
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalBans -> LuckPerms] Failed to connect to LuckPerms"));
            }
        }
    }

    public boolean isLuckPermsSetuped() {
        return isLuckPermsSetuped;
    }

    public String getPlayerGroup(Player player, Collection<String> possibleGroups) {
        if (isLuckPermsSetuped()) {
            for (String group : possibleGroups) {
                if (player.hasPermission("group." + group)) {
                    return group;
                }
            }
        }
        return null;
    }

}
