package by.alis.functionalservercontrol.spigot.Expansions.ProtocolLib;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import org.bukkit.Bukkit;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;
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
                Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl -> ProtocolLib] ProtocolLib detected, connecting..."));
            }
            protocolManager = ProtocolLibrary.getProtocolManager();
            if(protocolManager != null) {
                protocolLibSetuped = true;
                if(!getConfigSettings().isLessInformation()){
                    Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl -> ProtocolLib] Connection to ProtocolLib was successful."));
                }
            } else {
                protocolLibSetuped = false;
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl -> ProtocolLib] Failed to connect to ProtocolLib"));
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
