package net.alis.functionalservercontrol.spigot.listeners.pluginmessages;

import net.alis.functionalservercontrol.spigot.coreadapters.CoreAdapter;
import net.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

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
                                    TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName())));
                                }
                                return;
                            }
                        }
                        if (clientName.contains("fml") || clientName.contains("forge")) {
                            if (getConfigSettings().isBlockForgeClient()) {
                                for (String action : getConfigSettings().getForgeClientActions()) {
                                    TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName())));
                                }
                                return;
                            }
                        }
                        if (clientName.contains("lunar")) {
                            if (getConfigSettings().isBlockLunarClient()) {
                                for (String action : getConfigSettings().getLunarClientActions()) {
                                    TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName())));
                                }
                                return;
                            }
                        }
                        if (clientName.contains("badlion")) {
                            if (getConfigSettings().isBlockBadlionClient()) {
                                for (String action : getConfigSettings().getBadlionClientActions()) {
                                    TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName())));
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
                    Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControlSpigot] Failed to get the %player% Minecraft brand".replace("%player%", player.getName())));
                }
            });
        } else {
            DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(message));
            try {
                String clientName = dataInputStream.readLine().trim().toLowerCase();
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
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControlSpigot] Failed to get the %player% Minecraft brand".replace("%player%", player.getName())));
            }
        }
    }



}
