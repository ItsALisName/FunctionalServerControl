package net.alis.functionalservercontrol.spigot.dependencies;

import net.alis.functionalservercontrol.spigot.dependencies.soft.protocollib.ProtocolLibManager;
import net.alis.functionalservercontrol.spigot.dependencies.soft.protocolsupport.ProtocolSupportManager;
import net.alis.functionalservercontrol.spigot.dependencies.soft.vault.VaultManager;
import net.alis.functionalservercontrol.spigot.dependencies.soft.luckperms.LuckPermsManager;
import net.alis.functionalservercontrol.spigot.dependencies.soft.viaversion.ViaVersionManager;

public class Expansions {

    private static final VaultManager vaultManager = new VaultManager();
    private static final LuckPermsManager luckPermsManager = new LuckPermsManager();
    private static final ProtocolLibManager protocolLibManager = new ProtocolLibManager();
    private static final ViaVersionManager viaVersionManager = new ViaVersionManager();
    private static final ProtocolSupportManager protocolSupportManager = new ProtocolSupportManager();


    public static VaultManager getVaultManager() {
        return vaultManager;
    }
    public static LuckPermsManager getLuckPermsManager() { return luckPermsManager; }
    public static ProtocolLibManager getProtocolLibManager() {
        return protocolLibManager;
    }
    public static ViaVersionManager getViaVersionManager() {
        return viaVersionManager;
    }
    public static ProtocolSupportManager getProtocolSupportManager() {
        return protocolSupportManager;
    }
}
