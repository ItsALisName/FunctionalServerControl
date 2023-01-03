package by.alis.functionalservercontrol.spigot.Expansions;

import by.alis.functionalservercontrol.spigot.Expansions.LuckPerms.LuckPermsManager;
import by.alis.functionalservercontrol.spigot.Expansions.ProtocolLib.ProtocolLibManager;
import by.alis.functionalservercontrol.spigot.Expansions.ProtocolSupport.ProtocolSupportManager;
import by.alis.functionalservercontrol.spigot.Expansions.Vault.VaultManager;
import by.alis.functionalservercontrol.spigot.Expansions.ViaVersion.ViaVersionManager;

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
