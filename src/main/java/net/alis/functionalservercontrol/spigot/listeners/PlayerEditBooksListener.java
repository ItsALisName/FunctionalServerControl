package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;

import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.api.enums.Chat;
import net.alis.functionalservercontrol.api.enums.StatsType;
import net.alis.functionalservercontrol.api.events.PlayerAdvertiseEvent;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.Component;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;

import org.jetbrains.annotations.Nullable;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getChatSettings;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class PlayerEditBooksListener implements Listener {

    @EventHandler
    public void onPlayerEditBook(PlayerEditBookEvent event) {
        if(getChatSettings().isFunctionEnabled()) {
            FunctionalPlayer player = FunctionalPlayer.get(event.getPlayer().getName());
            if(getChatSettings().isBlockedWordsEnabled() && getChatSettings().isCheckBooksForBlockedWords()) {
                if(!player.hasPermission("functionalservercontrol.books.blocked-words.bypass") && !getChatSettings().getDisabledWorldsForBlockedWords().contains(player.world().getName())) {
                    for(String pageText : event.getNewBookMeta().getPages()) {
                        for(String pageWord : pageText.split(" ")) {
                            Bukkit.getConsoleSender().sendMessage(pageWord);
                            for (String blockedWord : getChatSettings().getBlockedWords()) {
                                if(pageWord.equalsIgnoreCase(blockedWord)) {
                                    event.setCancelled(true);
                                    BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.BLOCKED_WORDS_USED);
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
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.ADVERTISE_ATTEMPTS);
                        player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-on-book")));
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
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.ADVERTISE_ATTEMPTS);
                        player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-on-book")));
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


    private void notifyAdmins(FunctionalPlayer player, String message, @Nullable String word, boolean isAdvertise) {
        TaskManager.preformAsync(() -> {
            if(isAdvertise) {
                if (getChatSettings().isNotifyAdminAboutAdvertise()) {
                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.book").replace("%1$f", player.getName()).replace("%2$f", message)));
                    for (FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                        if (!admin.hasPermission("functionalservercontrol.notification.advertise")) return;
                        admin.expansion().message(
                                Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.book").replace("%1$f", player.getName()).replace("%2$f", message)), player)
                                        .append(Component.addPunishmentButtons(admin, player.getName())).translateDefaultColorCodes()
                        );
                    }
                }
            } else {
                if(getChatSettings().isNotifyAboutBlockedWord()) {
                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.book").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)));
                    for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                        if(admin.hasPermission("functionalservercontrol.notification.blocked-word")) {
                            admin.expansion().message(
                                            Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.book").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player)
                                                    .append(Component.addPunishmentButtons(admin, player.getName())).translateDefaultColorCodes()
                            );
                        }
                    }
                }
            }
        });
    }

}
