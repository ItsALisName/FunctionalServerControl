package by.alis.functionalservercontrol.spigot.Additional.CoreAdapters.Adapters;

import by.alis.functionalservercontrol.API.Enums.ProtocolVersions;
import by.alis.functionalservercontrol.spigot.Additional.CoreAdapters.Adapter;
import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.OtherUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class PaperAdapter extends Adapter {


    @Override
    public void sendActionBar(Player player, String param) {
        player.sendActionBar(Component.text(param));
    }

    @Override
    public String getPlayerMinecraftBrand(Player player) {
        return player.getClientBrandName();
    }

    @Override
    public int getPlayerProtocolVersion(Player player) {
        return player.getProtocolVersion();
    }

    @Override
    public ProtocolVersions getPlayerVersion(Player player) {
        return OtherUtils.convertProtocolVersion(this.getPlayerProtocolVersion(player));
    }
}
