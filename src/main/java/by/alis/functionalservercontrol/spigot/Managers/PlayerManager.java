package by.alis.functionalservercontrol.spigot.Managers;

import by.alis.functionalservercontrol.API.Enums.ProtocolVersions;
import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;

import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.OtherUtils.convertProtocolVersion;
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.OtherUtils.getServerVersion;
import static by.alis.functionalservercontrol.spigot.Expansions.Expansions.*;
import static org.bukkit.Bukkit.getServer;

public class PlayerManager {

    public ProtocolVersions getPlayerMinecraftVersion(Player player) {
        if(getViaVersionManager().isViaVersionSetuped()) {
            return convertProtocolVersion(getViaVersionManager().getViaVersion().getPlayerVersion(player.getUniqueId()));
        }
        if(getProtocolLibManager().isProtocolLibSetuped()) {
            return convertProtocolVersion(getProtocolLibManager().getProtocolManager().getProtocolVersion(player));
        }
        if(getProtocolSupportManager().isProtocolSupportSetuped()) {
            return convertProtocolVersion(ProtocolSupportAPI.getProtocolVersion(player).getId());
        }
        return getServerVersion(getServer());
    }

}
