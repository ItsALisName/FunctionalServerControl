package by.alis.functionalbans.spigot.Listeners;

import by.alis.functionalbans.databases.StaticBases;
import by.alis.functionalbans.spigot.Additional.Enums.BanType;
import by.alis.functionalbans.spigot.Managers.BansManagers.BanManager;
import by.alis.functionalbans.spigot.Managers.FilesManagers.FileAccessor;
import by.alis.functionalbans.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import static by.alis.functionalbans.databases.StaticBases.getSQLiteManager;
import static by.alis.functionalbans.spigot.Additional.Containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoinFirstTime(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getSQLiteManager().insertIntoAllPlayers(player.getName(), player.getUniqueId(), player.getAddress().getAddress().getHostAddress());
        getSQLiteManager().updateAllPlayers(player);
    }
}
