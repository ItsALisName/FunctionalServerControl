package net.alis.functionalservercontrol.spigot.dependencies.soft.protocollib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import org.bukkit.Bukkit;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static org.bukkit.Bukkit.getServer;

public class ProtocolLibManager {

    ProtocolManager protocolManager;

    boolean protocolLibSetuped = false;

    private boolean isProtocolLibInstalled() {
        return getServer().getPluginManager().isPluginEnabled("ProtocolLib");
    }

    public void setupProtocolLib() {
        if(isProtocolLibInstalled()) {
            if(!getConfigSettings().isLessInformation()){
                Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControlSpigot -> ProtocolLib] ProtocolLib detected, connecting..."));
            }
            protocolManager = ProtocolLibrary.getProtocolManager();
            if(protocolManager != null) {
                protocolLibSetuped = true;
                if(!getConfigSettings().isLessInformation()){
                    Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControlSpigot -> ProtocolLib] Connection to ProtocolLib was successful."));
                }
            } else {
                protocolLibSetuped = false;
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControlSpigot -> ProtocolLib] Failed to connect to ProtocolLib"));
            }
        }
    }

    public boolean isProtocolLibSetuped() {
        return protocolLibSetuped;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}
