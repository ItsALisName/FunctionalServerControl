package by.alis.functionalservercontrol.spigot.Additional.CoreAdapters;

import by.alis.functionalservercontrol.API.Enums.ProtocolVersions;
import org.bukkit.entity.Player;

public abstract class Adapter {

    public abstract void sendActionBar(Player player, String param);

    public abstract String getPlayerMinecraftBrand(Player player);

    public abstract int getPlayerProtocolVersion(Player player);

    public abstract ProtocolVersions getPlayerVersion(Player player);

}
