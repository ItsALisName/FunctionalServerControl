package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.libraries.ru.leymooo.fixer.ItemChecker;
import net.alis.functionalservercontrol.spigot.dependencies.Expansions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getProtectionSettings;

public class PlayerItemHeldListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerItemHolding(PlayerItemHeldEvent event) {
        if(Expansions.getProtocolLibManager().isProtocolLibSetuped() && getProtectionSettings().isItemFixerEnabled()) {
            Player player = event.getPlayer();
            ItemStack stack = player.getInventory().getItem(event.getNewSlot());
            if (ItemChecker.getItemChecker().isHackedItem(stack, player)) {
                event.setCancelled(true);
                player.updateInventory();
            }
        }
    }

}
