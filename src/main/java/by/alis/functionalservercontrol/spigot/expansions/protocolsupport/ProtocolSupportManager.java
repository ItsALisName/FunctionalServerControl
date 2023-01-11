package by.alis.functionalservercontrol.spigot.expansions.protocolsupport;

import static org.bukkit.Bukkit.getServer;

public class ProtocolSupportManager {
    boolean protocolSupportSetuped;

    private boolean isProtocolSupportInstalled() {
        return getServer().getPluginManager().isPluginEnabled("ProtocolSupport");
    }

    public void setupProtocolSupport() {
        this.protocolSupportSetuped = isProtocolSupportInstalled();
    }

    public boolean isProtocolSupportSetuped() {
        return this.protocolSupportSetuped;
    }
}
