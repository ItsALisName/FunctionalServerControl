package by.alis.functionalservercontrol.spigot.Additional.CoreAdapters.Adapters;

import by.alis.functionalservercontrol.API.Enums.ProtocolVersions;
import by.alis.functionalservercontrol.spigot.Additional.CoreAdapters.Adapter;
import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.MD5TextUtils;
import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.OtherUtils;
import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.Reflect.ActionBarReflect;
import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.Reflect.TitleReflect;
import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TemporaryCache;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import protocolsupport.api.ProtocolSupportAPI;

import java.util.UUID;

import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.AdventureApiUtils.stringToComponent;
import static by.alis.functionalservercontrol.spigot.Expansions.Expansions.*;

public class PaperAdapter extends Adapter {

    @Override
    public void sendActionBar(Player player, String param) {
        if (adventureApiExists) {
            player.sendActionBar(stringToComponent(param));
        } else {
            try {
                Player.class.getMethod("sendActionBar", String.class);
                player.sendActionBar(param);
            } catch (NoSuchMethodException ignored) {
                try {
                    Player.class.getMethod("spigot");
                    Player.Spigot.class.getMethod("sendMessage");
                    MD5TextUtils.sendActionBarText(player, param);
                } catch (NoSuchMethodException ignored1) {
                    ActionBarReflect.sendActionBar(player, param);
                }
            }
        }
    }

    @Override
    public String getPlayerMinecraftBrand(Player player) {
        try {
            Player.class.getMethod("getClientBrandName");
            return player.getClientBrandName();
        } catch (NoSuchMethodException ignored) {
            return TemporaryCache.getClientBrands().get(player);
        }
    }

    @Override
    public int getPlayerProtocolVersion(Player player) {
        try {
            Player.class.getMethod("getProtocolVersion");
            player.getProtocolVersion();
        } catch (NoSuchMethodException ignored) {
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
        return 0;
    }

    @Override
    public ProtocolVersions getPlayerVersion(Player player) {
        return OtherUtils.convertProtocolVersion(this.getPlayerProtocolVersion(player));
    }

    @Override
    public @Nullable OfflinePlayer getOfflinePlayer(String param) {
        try {
            Bukkit.class.getMethod("getOfflinePlayerIfCached", String.class);
            return Bukkit.getOfflinePlayerIfCached(param);
        } catch (NoSuchMethodException e) {
            return Bukkit.getOfflinePlayer(param);
        }
    }

    @Override
    public @Nullable OfflinePlayer getOfflinePlayer(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid);
    }

    @Override
    public void broadcast(@NotNull String message) {
        if(adventureApiExists) {
            Bukkit.broadcast(stringToComponent(message));
        } else {
            Bukkit.broadcastMessage(message);
        }
    }

    @Override
    public void kick(@NotNull Player player, @Nullable String reason) {
        if(adventureApiExists) {
            if (reason != null && !reason.equalsIgnoreCase("")) player.kick(stringToComponent(reason));
            player.kick();
        } else {
            if (reason != null && !reason.equalsIgnoreCase("")) player.kickPlayer(reason);
            player.kickPlayer("");
        }
    }

    @Override
    public void sendTitle(@NotNull Player player, String param, String param1) {
        try {
            Player.class.getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
            player.sendTitle(param, param1, 10, 70, 20);
        } catch (NoSuchMethodException ignored) {
            try {
                Player.class.getMethod("sendTitle", String.class, String.class);
                player.sendTitle(param, param1);
            } catch (NoSuchMethodException ignored1) {
                TitleReflect.sendTitle(player, param + "\n" + param1, 10, 70, 20);
            }
        }
    }

}
