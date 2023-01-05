package by.alis.functionalservercontrol.spigot.Additional.CoreAdapters;

import by.alis.functionalservercontrol.API.Enums.ProtocolVersions;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class Adapter {

    protected static boolean adventureApiExists;

    public abstract void sendActionBar(Player player, String param);

    public abstract String getPlayerMinecraftBrand(Player player);

    public abstract int getPlayerProtocolVersion(Player player);

    public abstract ProtocolVersions getPlayerVersion(Player player);

    public abstract @Nullable OfflinePlayer getOfflinePlayer(String param);

    public abstract @Nullable OfflinePlayer getOfflinePlayer(UUID uuid);

    public abstract void broadcast(@NotNull String message);

    public abstract void kick(@NotNull Player player, @Nullable String reason);

    public abstract void sendTitle(@NotNull Player player, String param, String param1);

}
