package by.alis.functionalservercontrol.spigot.listeners.pluginmessages;

import by.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import by.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class ClientBrandListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
        if (!channel.equals("MC|Brand") && !channel.equals("minecraft:brand"))
            return;
        DataInputStream client = new DataInputStream(new ByteArrayInputStream(message));
        try {
            String clientName = client.readLine().trim().toLowerCase();
            if(clientName.contains("vanilla")) {
                if (getConfigSettings().isBlockVanilla()) {
                    for (String action : getConfigSettings().getVanillaActions()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                    }
                    return;
                }
            }
            if(clientName.contains("fml") || clientName.contains("forge")) {
                if(getConfigSettings().isBlockForge()) {
                    for (String action : getConfigSettings().getForgeActions()) {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                    }
                    return;
                }
            }
            if(getConfigSettings().isAnnounceConsoleAboutBrand()) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.client.player-brand-notify").replace("%1$f", player.getName())).replace("%2$f", clientName).replace("%3$f", CoreAdapter.getAdapter().getPlayerVersion(player).toString()));
            }
            for(Player admin : Bukkit.getOnlinePlayers()) {
                if(admin.hasPermission("functionalservercontrol.notification.clients")) {
                    admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.client.player-brand-notify").replace("%1$f", player.getName())).replace("%2$f", clientName).replace("%3$f", CoreAdapter.getAdapter().getPlayerVersion(player).toString()));
                }
            }
            TemporaryCache.setClientBrands(player, clientName);
        } catch (IOException ignored) {
            CoreAdapter.getAdapter().broadcast(setColors("&c[FunctionalServerControl] Failed to get the %player% Minecraft brand".replace("%player%", player.getName())));
        }
    }



}
