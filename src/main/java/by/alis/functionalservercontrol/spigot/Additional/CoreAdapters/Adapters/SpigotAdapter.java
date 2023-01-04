package by.alis.functionalservercontrol.spigot.Additional.CoreAdapters.Adapters;

import by.alis.functionalservercontrol.API.Enums.ProtocolVersions;
import by.alis.functionalservercontrol.spigot.Additional.CoreAdapters.Adapter;
import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.OtherUtils;
import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TemporaryCache;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import protocolsupport.api.ProtocolSupportAPI;

import static by.alis.functionalservercontrol.spigot.Expansions.Expansions.*;

public class SpigotAdapter extends Adapter {

    @Override
    public void sendActionBar(Player player, String param) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(param));
    }

    @Override
    public String getPlayerMinecraftBrand(Player player) {
        return TemporaryCache.getClientBrands().get(player);
    }

    @Override
    public int getPlayerProtocolVersion(Player player) {
        if(getViaVersionManager().isViaVersionSetuped()) {
            return getViaVersionManager().getViaVersion().getPlayerVersion(player.getUniqueId());
        }
        if(getProtocolLibManager().isProtocolLibSetuped()) {
            return getProtocolLibManager().getProtocolManager().getProtocolVersion(player);
        }
        if(getProtocolSupportManager().isProtocolSupportSetuped()) {
            return ProtocolSupportAPI.getProtocolVersion(player).getId();
        }
        return 0;
    }

    @Override
    public ProtocolVersions getPlayerVersion(Player player) {
        return OtherUtils.convertProtocolVersion(this.getPlayerProtocolVersion(player));
    }
}
