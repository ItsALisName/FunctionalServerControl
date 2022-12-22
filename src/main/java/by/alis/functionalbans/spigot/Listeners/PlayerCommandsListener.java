package by.alis.functionalbans.spigot.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Managers.CheatCheckerManager.getCheatCheckerManager;

public class PlayerCommandsListener implements Listener {

    @EventHandler
    public void onPlayerSendCommand(PlayerCommandPreprocessEvent event) {
        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(getCheatCheckerManager().isPlayerChecking(event.getPlayer())) {
                if (getConfigSettings().isPreventCommandsDuringCheck()) {
                    String command = event.getMessage().split(" ")[0];
                    if (!getConfigSettings().getIgnoredCommandsDuruingCheck().contains(command)) {
                        if (!event.isCancelled()) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

}
