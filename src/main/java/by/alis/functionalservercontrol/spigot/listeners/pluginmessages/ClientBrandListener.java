package by.alis.functionalservercontrol.spigot.listeners.pluginmessages;

import by.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import by.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import by.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class ClientBrandListener implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if (!channel.equals("MC|Brand") && !channel.equals("minecraft:brand")) return;
        if(getConfigSettings().isAsyncClientsChecking()) {
            TaskManager.preformAsync(() -> {
                DataInputStream client = new DataInputStream(new ByteArrayInputStream(message));
                try {
                    String clientName = client.readLine().trim().toLowerCase();
                    if (!player.hasPermission("functionalservercontrol.clients.bypass")) {
                        if (clientName.contains("vanilla")) {
                            if (getConfigSettings().isBlockVanillaClient()) {
                                for (String action : getConfigSettings().getVanillaClientActions()) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                                }
                                return;
                            }
                        }
                        if (clientName.contains("fml") || clientName.contains("forge")) {
                            if (getConfigSettings().isBlockForgeClient()) {
                                for (String action : getConfigSettings().getForgeClientActions()) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                                }
                                return;
                            }
                        }
                        if (clientName.contains("lunar")) {
                            if (getConfigSettings().isBlockLunarClient()) {
                                for (String action : getConfigSettings().getLunarClientActions()) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                                }
                                return;
                            }
                        }
                        if (clientName.contains("badlion")) {
                            if (getConfigSettings().isBlockBadlionClient()) {
                                for (String action : getConfigSettings().getBadlionClientActions()) {
                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                                }
                                return;
                            }
                        }
                    }
                    if (getConfigSettings().isAnnounceConsoleAboutBrand()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.client.player-brand-notify").replace("%1$f", player.getName())).replace("%2$f", clientName).replace("%3$f", CoreAdapter.getAdapter().getPlayerVersion(player).toString()));
                    }
                    for (Player admin : Bukkit.getOnlinePlayers()) {
                        if (admin.hasPermission("functionalservercontrol.notification.clients")) {
                            admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.client.player-brand-notify").replace("%1$f", player.getName())).replace("%2$f", clientName).replace("%3$f", CoreAdapter.getAdapter().getPlayerVersion(player).toString()));
                        }
                    }
                    TemporaryCache.setClientBrands(player, clientName);
                } catch (IOException ignored) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Failed to get the %player% Minecraft brand".replace("%player%", player.getName())));
                }
            });
        } else {
            DataInputStream client = new DataInputStream(new ByteArrayInputStream(message));
            try {
                String clientName = client.readLine().trim().toLowerCase();
                if (!player.hasPermission("functionalservercontrol.clients.bypass")) {
                    if (clientName.contains("vanilla")) {
                        if (getConfigSettings().isBlockVanillaClient()) {
                            for (String action : getConfigSettings().getVanillaClientActions()) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                            }
                            return;
                        }
                    }
                    if (clientName.contains("fml") || clientName.contains("forge")) {
                        if (getConfigSettings().isBlockForgeClient()) {
                            for (String action : getConfigSettings().getForgeClientActions()) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                            }
                            return;
                        }
                    }
                    if (clientName.contains("lunar")) {
                        if (getConfigSettings().isBlockLunarClient()) {
                            for (String action : getConfigSettings().getLunarClientActions()) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                            }
                            return;
                        }
                    }
                    if (clientName.contains("badlion")) {
                        if (getConfigSettings().isBlockBadlionClient()) {
                            for (String action : getConfigSettings().getBadlionClientActions()) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                            }
                            return;
                        }
                    }
                }
                if (getConfigSettings().isAnnounceConsoleAboutBrand()) {
                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.client.player-brand-notify").replace("%1$f", player.getName())).replace("%2$f", clientName).replace("%3$f", CoreAdapter.getAdapter().getPlayerVersion(player).toString()));
                }
                for (Player admin : Bukkit.getOnlinePlayers()) {
                    if (admin.hasPermission("functionalservercontrol.notification.clients")) {
                        admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.client.player-brand-notify").replace("%1$f", player.getName())).replace("%2$f", clientName).replace("%3$f", CoreAdapter.getAdapter().getPlayerVersion(player).toString()));
                    }
                }
                TemporaryCache.setClientBrands(player, clientName);
            } catch (IOException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Failed to get the %player% Minecraft brand".replace("%player%", player.getName())));
            }
        }
    }



}
