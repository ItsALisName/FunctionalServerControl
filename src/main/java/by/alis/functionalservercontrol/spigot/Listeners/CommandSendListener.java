package by.alis.functionalservercontrol.spigot.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;

import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;

public class CommandSendListener implements Listener {

    @EventHandler
    public void onServerSendCommand(PlayerCommandSendEvent event) {
        if(!getConfigSettings().hideMainCommand()) return;
        String[] a = new String[]{"functionalservercontrol", "fcs", "fscontrol"};
        event.getCommands().removeIf((cmd) -> {
            for(String c : a) {
                if(cmd.equalsIgnoreCase(c) || cmd.startsWith("functionalservercontrol:")) {
                    return true;
                }
            }
            return false;
        });
    }

}
