package by.alis.functionalservercontrol.spigot.Listeners.PluginMessages;

import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class WorldDownloaderChannelListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte @NotNull [] message) {
        if(getConfigSettings().isBlockWorldDownloader()) {
            if ((channel.equals("WDL|INIT"))) {
                for(String action : getConfigSettings().getActionsOnWDL()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                }
            }
        }
        if(getConfigSettings().isPlayersNotification()) {
            for(Player admin : Bukkit.getOnlinePlayers()) {
                if(admin.hasPermission("functionalservercontrol.notification.clients")) {
                    admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.client.world-downloader").replace("%1$f", player.getName())));
                }
            }
        }
        if(getConfigSettings().isConsoleNotification()) {
            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.client.world-downloader").replace("%1$f", player.getName())));
        }
    }
}
