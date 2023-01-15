package by.alis.functionalservercontrol.spigot.listeners;

import by.alis.functionalservercontrol.api.enums.Chat;
import by.alis.functionalservercontrol.api.enums.StatsType;
import by.alis.functionalservercontrol.api.events.PlayerAdvertiseEvent;
import by.alis.functionalservercontrol.spigot.additional.misc.AdventureApiUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.MD5TextUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.TextUtils;

import by.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;

import org.jetbrains.annotations.Nullable;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getChatSettings;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class PlayerEditBooksListener implements Listener {

    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        if(getChatSettings().isFunctionEnabled()) {
            Player player = event.getPlayer();
            if(getChatSettings().isBlockedWordsEnabled() && getChatSettings().isCheckBooksForBlockedWords()) {
                if(!player.hasPermission("functionalservercontrol.books.blocked-words.bypass") && !getChatSettings().getDisabledWorldsForBlockedWords().contains(player.getWorld().getName())) {
                    for(String pageText : event.getNewBookMeta().getPages()) {
                        for(String pageWord : pageText.split(" ")) {
                            Bukkit.getConsoleSender().sendMessage(pageWord);
                            for (String blockedWord : getChatSettings().getBlockedWords()) {
                                if(pageWord.equalsIgnoreCase(blockedWord)) {
                                    event.setCancelled(true);
                                    getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.BLOCKED_WORDS_USED);
                                    player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.blocked-word-in-book").replace("%1$f", blockedWord)));
                                    notifyAdmins(player, pageText, blockedWord, false);
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
            if(getChatSettings().isBookIpProtectionEnabled() && !player.hasPermission("functionalservercontrol.advertise.books.bypass")) {
                for(String pageText : event.getNewBookMeta().getPages()) {
                    if(OtherUtils.isArgumentIP(TextUtils.stringToMonolith(pageText))) {
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.ADVERTISE_ATTEMPTS);
                        player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-in-book")));
                        notifyAdmins(player, pageText, null, true);
                        for(String action : getChatSettings().getBookIpProtectionActions()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", pageText));
                        PlayerAdvertiseEvent asyncPlayerAdvertiseEvent = new PlayerAdvertiseEvent(player, Chat.AdvertiseMethod.BOOK, pageText);
                        if(getConfigSettings().isApiEnabled()) Bukkit.getPluginManager().callEvent(asyncPlayerAdvertiseEvent);
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            if(getChatSettings().isBookDomainsProtectionEnabled() && !player.hasPermission("functionalservercontrol.advertise.books.bypass")) {
                for(String pageText : event.getNewBookMeta().getPages()) {
                    if(OtherUtils.isArgumentDomain(TextUtils.stringToMonolith(pageText))) {
                        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.ADVERTISE_ATTEMPTS);
                        player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-in-book")));
                        notifyAdmins(player, pageText, null, true);
                        for(String action : getChatSettings().getBookDomainsProtectionActions()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", pageText));
                        PlayerAdvertiseEvent asyncPlayerAdvertiseEvent = new PlayerAdvertiseEvent(player, Chat.AdvertiseMethod.BOOK, pageText);
                        if(getConfigSettings().isApiEnabled()) Bukkit.getPluginManager().callEvent(asyncPlayerAdvertiseEvent);
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }


    private void notifyAdmins(Player player, String message, @Nullable String word, boolean isAdvertise) {
        TaskManager.preformAsync(() -> {
            if(isAdvertise) {
                if (getChatSettings().isNotifyAdminAboutAdvertise()) {
                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.book").replace("%1$f", player.getName()).replace("%2$f", message)));
                    for (Player admin : Bukkit.getOnlinePlayers()) {
                        if (!admin.hasPermission("functionalservercontrol.notification.advertise")) return;
                        if (getConfigSettings().isServerSupportsHoverEvents()) {
                            if (getConfigSettings().isButtonsOnNotifications()) {
                                if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                    admin.spigot().sendMessage(MD5TextUtils.appendTwo(
                                            MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.book").replace("%1$f", player.getName()).replace("%2$f", message)), player),
                                            MD5TextUtils.addPunishmentButtons(admin, player.getName())
                                    ));
                                    continue;
                                }
                                if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                    admin.sendMessage(
                                            AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.book").replace("%1$f", player.getName()).replace("%2$f", message)), player).append(AdventureApiUtils.addPunishmentButtons(admin, player.getName())));
                                    continue;
                                }
                            } else {
                                if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                    admin.spigot().sendMessage(MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.book").replace("%1$f", player.getName()).replace("%2$f", message)), player));
                                    continue;
                                }
                                if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                    admin.sendMessage(AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.book").replace("%1$f", player.getName()).replace("%2$f", message)), player));
                                    continue;
                                }
                            }
                        } else {
                            admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.book").replace("%1$f", player.getName()).replace("%2$f", message)));
                        }
                    }
                }
            } else {
                if(getChatSettings().isNotifyAboutBlockedWord()) {
                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.book").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)));
                    for(Player admin : Bukkit.getOnlinePlayers()) {
                        if(admin.hasPermission(" functionalservercontrol.notification.blocked-word")) {
                            if(getConfigSettings().isServerSupportsHoverEvents()) {
                                if(getConfigSettings().isButtonsOnNotifications()) {
                                    if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                        admin.spigot().sendMessage(
                                                MD5TextUtils.appendTwo(
                                                        MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.book").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player),
                                                        MD5TextUtils.addPunishmentButtons(admin, player.getName())));
                                        continue;
                                    }
                                    if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                        admin.sendMessage(AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.book").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player).append(AdventureApiUtils.addPunishmentButtons(admin, player.getName())));
                                        continue;
                                    }
                                } else {
                                    if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                        admin.spigot().sendMessage(MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.book").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player));
                                        continue;
                                    }
                                    if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                        admin.sendMessage(AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.book").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player));
                                        continue;
                                    }
                                }
                            } else {
                                admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.book").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)));
                            }
                        }
                    }
                }
            }
        });
    }

}
