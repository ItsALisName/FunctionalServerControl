package by.alis.functionalbans.spigot.Expansions;

import by.alis.functionalbans.spigot.Expansions.LuckPerms.LuckPermsManager;
import by.alis.functionalbans.spigot.Expansions.ProtocolLib.ProtocolLibManager;
import by.alis.functionalbans.spigot.Expansions.Vault.VaultManager;

public class Expansions {

    private static final VaultManager vaultManager = new VaultManager();
    private static final LuckPermsManager luckPermsManager = new LuckPermsManager();
    private static final ProtocolLibManager protocolLibManager = new ProtocolLibManager();


    public static VaultManager getVaultManager() {
        return vaultManager;
    }
    public static LuckPermsManager getLuckPermsManager() { return luckPermsManager; }
    public static ProtocolLibManager getProtocolLibManager() { return protocolLibManager; }
}
