package by.alis.functionalservercontrol.spigot.listeners;

import by.alis.functionalservercontrol.api.enums.Chat;
import by.alis.functionalservercontrol.api.enums.StatsType;
import by.alis.functionalservercontrol.api.events.PlayerAdvertiseEvent;
import by.alis.functionalservercontrol.spigot.additional.misc.AdventureApiUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.MD5TextUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getChatSettings;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.CheatCheckerManager.getCheatCheckerManager;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class PlayerDropItemListener implements Listener {

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if(getConfigSettings().isCheatCheckFunctionEnabled()) {
            if(getConfigSettings().isPreventDropItemDuringCheatCheck()) {
                if(getCheatCheckerManager().isPlayerChecking(event.getPlayer())) event.setCancelled(true);
            }
        }
        if(getChatSettings().isFunctionEnabled()) {
            if (event.getItemDrop().getItemStack().hasItemMeta()) {
                ItemMeta droppedItemMeta = event.getItemDrop().getItemStack().getItemMeta();
                if (droppedItemMeta.hasDisplayName()) {
                    if (getChatSettings().isBlockedWordsEnabled() && getChatSettings().isCheckItemsForBlockedWords() && !player.hasPermission("functionalservercontrol.items.blocked-words.bypass") && !getChatSettings().getDisabledWorldsForBlockedWords().contains(player.getWorld().getName())) {
                        for(String blockedWord : getChatSettings().getBlockedWords()) {
                            if(droppedItemMeta.getDisplayName().contains(blockedWord)) {
                                event.setCancelled(true);
                                switch (getConfigSettings().getStorageType()) {
                                    case SQLITE:
                                        getSQLiteManager().updatePlayerStatsInfo(player, StatsType.Player.BLOCKED_WORDS_USED);
                                        break;
                                    case H2: {
                                    }
                                }
                                player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.blocked-word-on-item").replace("%1$f", blockedWord)));
                                notifyAdmins(player, droppedItemMeta.getDisplayName(), blockedWord, false);
                                if(getChatSettings().isPunishEnabledForBlockedWords()) {
                                    for (String action : getChatSettings().getPunishForBlockedWords()) {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", blockedWord));
                                    }
                                }
                                return;
                            }
                            if(droppedItemMeta.hasLore()) {
                                for (String lore : droppedItemMeta.getLore()) {
                                    for(String loreArg : lore.split(" ")) {
                                        if(loreArg.equalsIgnoreCase(blockedWord)) {
                                            event.setCancelled(true);
                                            switch (getConfigSettings().getStorageType()) {
                                                case SQLITE:
                                                    getSQLiteManager().updatePlayerStatsInfo(player, StatsType.Player.BLOCKED_WORDS_USED);
                                                    break;
                                                case H2: {
                                                }
                                            }
                                            player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.blocked-word-on-item").replace("%1$f", blockedWord)));
                                            notifyAdmins(player, lore, blockedWord, false);
                                            if(getChatSettings().isPunishEnabledForBlockedWords()) {
                                                for (String action : getChatSettings().getPunishForBlockedWords()) {
                                                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", blockedWord));
                                                }
                                            }
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(getChatSettings().isItemsIpProtectionEnabled() && !player.hasPermission("functionalservercontrol.advertise.items.bypass") && !player.hasPermission("functionalservercontrol.advertise.bypass")) {
                        if(OtherUtils.isArgumentIP(TextUtils.stringToMonolith(droppedItemMeta.getDisplayName()))) {
                            switch (getConfigSettings().getStorageType()) {
                                case SQLITE: getSQLiteManager().updatePlayerStatsInfo(player, StatsType.Player.ADVERTISE_ATTEMPTS); break;
                                case H2: {}
                            }
                            player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-on-item")));
                            notifyAdmins(player, droppedItemMeta.getDisplayName(), null, true);
                            for(String action : getChatSettings().getItemsIpProtectionActions()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", droppedItemMeta.getDisplayName()));
                            PlayerAdvertiseEvent asyncPlayerAdvertiseEvent = new PlayerAdvertiseEvent(player, Chat.AdvertiseMethod.ITEM, droppedItemMeta.getDisplayName());
                            if(getConfigSettings().isApiEnabled()) Bukkit.getPluginManager().callEvent(asyncPlayerAdvertiseEvent);
                            event.setCancelled(true);
                            return;
                        }
                        if(droppedItemMeta.hasLore()) {
                            for(String lore : droppedItemMeta.getLore()) {
                                if(OtherUtils.isArgumentIP(TextUtils.stringToMonolith(lore))) {
                                    switch (getConfigSettings().getStorageType()) {
                                        case SQLITE: getSQLiteManager().updatePlayerStatsInfo(player, StatsType.Player.ADVERTISE_ATTEMPTS); break;
                                        case H2: {}
                                    }
                                    player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-on-item")));
                                    notifyAdmins(player, lore, null, true);
                                    for(String action : getChatSettings().getItemsIpProtectionActions()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", lore));
                                    PlayerAdvertiseEvent asyncPlayerAdvertiseEvent = new PlayerAdvertiseEvent(player, Chat.AdvertiseMethod.ITEM, lore);
                                    if(getConfigSettings().isApiEnabled()) Bukkit.getPluginManager().callEvent(asyncPlayerAdvertiseEvent);
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                        }
                    }
                    if(getChatSettings().isItemsDomainsProtectionEnabled() && !player.hasPermission("functionalservercontrol.advertise.items.bypass") && !player.hasPermission("functionalservercontrol.advertise.bypass")) {
                        if(OtherUtils.isArgumentDomain(TextUtils.stringToMonolith(droppedItemMeta.getDisplayName()))) {
                            switch (getConfigSettings().getStorageType()) {
                                case SQLITE: getSQLiteManager().updatePlayerStatsInfo(player, StatsType.Player.ADVERTISE_ATTEMPTS); break;
                                case H2: {}
                            }
                            player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-on-item")));
                            notifyAdmins(player, droppedItemMeta.getDisplayName(), null, true);
                            for(String action : getChatSettings().getItemsDomainsProtectionActions()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", droppedItemMeta.getDisplayName()));
                            PlayerAdvertiseEvent asyncPlayerAdvertiseEvent = new PlayerAdvertiseEvent(player, Chat.AdvertiseMethod.ITEM, droppedItemMeta.getDisplayName());
                            if(getConfigSettings().isApiEnabled()) Bukkit.getPluginManager().callEvent(asyncPlayerAdvertiseEvent);
                            event.setCancelled(true);
                            return;
                        }
                        if(droppedItemMeta.hasLore()) {
                            for(String lore : droppedItemMeta.getLore()) {
                                if(OtherUtils.isArgumentDomain(TextUtils.stringToMonolith(lore))) {
                                    switch (getConfigSettings().getStorageType()) {
                                        case SQLITE: getSQLiteManager().updatePlayerStatsInfo(player, StatsType.Player.ADVERTISE_ATTEMPTS); break;
                                        case H2: {}
                                    }
                                    player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-on-item")));
                                    notifyAdmins(player, lore, null, true);
                                    for(String action : getChatSettings().getItemsDomainsProtectionActions()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", lore));
                                    PlayerAdvertiseEvent asyncPlayerAdvertiseEvent = new PlayerAdvertiseEvent(player, Chat.AdvertiseMethod.ITEM, lore);
                                    if(getConfigSettings().isApiEnabled()) Bukkit.getPluginManager().callEvent(asyncPlayerAdvertiseEvent);
                                    event.setCancelled(true);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void notifyAdmins(Player player, String message, @Nullable String word, boolean isAdvertise) {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
            if(isAdvertise) {
                if (getChatSettings().isNotifyAdminAboutAdvertise()) {
                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.item").replace("%1$f", player.getName()).replace("%2$f", message)));
                    for (Player admin : Bukkit.getOnlinePlayers()) {
                        if (!admin.hasPermission("functionalservercontrol.notification.advertise")) return;
                        if (getConfigSettings().isServerSupportsHoverEvents()) {
                            if (getConfigSettings().isButtonsOnNotifications()) {
                                if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                    admin.spigot().sendMessage(MD5TextUtils.appendTwo(
                                            MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.item").replace("%1$f", player.getName()).replace("%2$f", message)), player),
                                            MD5TextUtils.addPunishmentButtons(admin, player.getName())
                                    ));
                                    continue;
                                }
                                if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                    admin.sendMessage(
                                            AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.item").replace("%1$f", player.getName()).replace("%2$f", message)), player).append(AdventureApiUtils.addPunishmentButtons(admin, player.getName())));
                                    continue;
                                }
                            } else {
                                if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                    admin.spigot().sendMessage(MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.item").replace("%1$f", player.getName()).replace("%2$f", message)), player));
                                    continue;
                                }
                                if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                    admin.sendMessage(AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.item").replace("%1$f", player.getName()).replace("%2$f", message)), player));
                                    continue;
                                }
                            }
                        } else {
                            admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.item").replace("%1$f", player.getName()).replace("%2$f", message)));
                        }
                    }
                }
            } else {
                if(getChatSettings().isNotifyAboutBlockedWord()) {
                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.item").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)));
                    for(Player admin : Bukkit.getOnlinePlayers()) {
                        if(admin.hasPermission(" functionalservercontrol.notification.blocked-word")) {
                            if(getConfigSettings().isServerSupportsHoverEvents()) {
                                if(getConfigSettings().isButtonsOnNotifications()) {
                                    if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                        admin.spigot().sendMessage(
                                                MD5TextUtils.appendTwo(
                                                        MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.item").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player),
                                                        MD5TextUtils.addPunishmentButtons(admin, player.getName())));
                                        continue;
                                    }
                                    if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                        admin.sendMessage(AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.item").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player).append(AdventureApiUtils.addPunishmentButtons(admin, player.getName())));
                                        continue;
                                    }
                                } else {
                                    if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                        admin.spigot().sendMessage(MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.item").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player));
                                        continue;
                                    }
                                    if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                        admin.sendMessage(AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.item").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player));
                                        continue;
                                    }
                                }
                            } else {
                                admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.item").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)));
                            }
                        }
                    }
                }
            }
        });
    }

}
