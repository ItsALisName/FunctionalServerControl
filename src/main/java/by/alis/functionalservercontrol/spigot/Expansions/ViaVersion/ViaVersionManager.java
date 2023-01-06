package by.alis.functionalservercontrol.spigot.Expansions.ViaVersion;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;

import org.bukkit.Bukkit;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.Misc.TextUtils.setColors;
import static org.bukkit.Bukkit.getServer;

public class ViaVersionManager {

    ViaAPI viaVersion;
    boolean viaVersionSetuped;

    private boolean isViaVersionInstalled() {
        return getServer().getPluginManager().isPluginEnabled("ViaVersion");
    }

    public void setupViaVersion() {
        if(isViaVersionInstalled()) {
            if(!getConfigSettings().isLessInformation()){
                Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl -> ViaVersion] ViaVersion detected, connecting..."));
            }
            viaVersion = Via.getAPI();
            if(viaVersion != null) {
                this.viaVersionSetuped = true;
                if(!getConfigSettings().isLessInformation()){
                    Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl -> ViaVersion] Connection to ViaVersion was successful."));
                }
            } else {
                this.viaVersionSetuped = false;
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl -> ViaVersion] Failed to connect to ViaVersion"));
            }
        }
    }

    public boolean isViaVersionSetuped() {
        return viaVersionSetuped;
    }

    public ViaAPI getViaVersion() {
        return viaVersion;
    }
}
