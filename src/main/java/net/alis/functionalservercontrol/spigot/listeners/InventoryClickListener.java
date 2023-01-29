package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.libraries.ru.leymooo.fixer.ItemChecker;
import net.alis.functionalservercontrol.spigot.dependencies.Expansions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getProtectionSettings;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onPlayerClickingInventory(InventoryClickEvent event) {
        if(Expansions.getProtocolLibManager().isProtocolLibSetuped() && getProtectionSettings().isItemFixerEnabled()){
            if (event.getWhoClicked() instanceof Player) return;
            FunctionalPlayer whoClicked = FunctionalPlayer.get(event.getWhoClicked().getName());
            if (event.getCurrentItem() == null) return;
            if (ItemChecker.getItemChecker().isHackedItem(event.getCurrentItem(), whoClicked)) {
                event.setCancelled(true);
                whoClicked.getBukkitPlayer().updateInventory();
            }
        }
    }

}
