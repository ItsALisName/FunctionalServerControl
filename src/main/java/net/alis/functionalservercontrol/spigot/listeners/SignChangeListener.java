package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.enums.Chat;
import net.alis.functionalservercontrol.api.enums.StatsType;
import net.alis.functionalservercontrol.api.events.PlayerAdvertiseEvent;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.Component;

import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.Nullable;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class SignChangeListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String[] lines = event.getLines();
        FunctionalPlayer player = FunctionalPlayer.get(event.getPlayer().getName());
        if(getChatSettings().isFunctionEnabled()) {
            if(getChatSettings().isBlockedWordsEnabled()) {
                if (getChatSettings().isCheckSignsForBlockedWords() && !player.hasPermission("functionalservercontrol.signs.blocked-words.bypass") && !getChatSettings().getDisabledWorldsForBlockedWords().contains(player.world().getName())) {
                    for (String blockedWord : getChatSettings().getBlockedWords()) {
                        for (String line : lines) {
                            if (line.contains(blockedWord)) {
                                event.setCancelled(true);
                                BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.BLOCKED_WORDS_USED);
                                player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.blocked-word-on-sign").replace("%1$f", blockedWord)));
                                notifyAdmins(player, "\n" + String.join("\n", lines), blockedWord, false);
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
            if(getChatSettings().isSignsIpProtectionEnabled() && !player.hasPermission("functionalservercontrol.advertise.signs.bypass") && !player.hasPermission("functionalservercontrol.advertise.bypass")) {
                for(String line : lines) {
                    if(OtherUtils.isArgumentIP(TextUtils.stringToMonolith(line))) {
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.ADVERTISE_ATTEMPTS);
                        player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-on-sign")));
                        notifyAdmins(player, "\n" + String.join("\n", lines), null, true);
                        for(String action : getChatSettings().getSignsIpProtectionActions()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", line));
                        PlayerAdvertiseEvent asyncPlayerAdvertiseEvent = new PlayerAdvertiseEvent(player, Chat.AdvertiseMethod.SIGN, String.join(", ", lines));
                        if(getConfigSettings().isApiEnabled()) Bukkit.getPluginManager().callEvent(asyncPlayerAdvertiseEvent);
                        event.setCancelled(true);
                        return;
                    }
                }
            }
            if(getChatSettings().isSignsDomainsProtectionEnabled() && !player.hasPermission("functionalservercontrol.advertise.signs.bypass") && !player.hasPermission("functionalservercontrol.advertise.bypass")) {
                for(String line : lines) {
                    if(OtherUtils.isArgumentDomain(TextUtils.stringToMonolith(line))) {
                        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.ADVERTISE_ATTEMPTS);
                        player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-on-sign")));
                        notifyAdmins(player, "\n" + String.join("\n", lines), null, true);
                        for(String action : getChatSettings().getSignsDomainsProtectionActions()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", line));
                        PlayerAdvertiseEvent asyncPlayerAdvertiseEvent = new PlayerAdvertiseEvent(player, Chat.AdvertiseMethod.SIGN, String.join(", ", lines));
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
                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.sign").replace("%1$f", player.getName()).replace("%2$f", message)));
                    for (FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                        if (!admin.hasPermission("functionalservercontrol.notification.advertise")) return;
                        if (getConfigSettings().isButtonsOnNotifications()) {
                            admin.expansion().message(
                                    Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.sign").replace("%1$f", player.getName()).replace("%2$f", message)), player)
                                            .append(Component.addPunishmentButtons(admin, player.nickname())).translateDefaultColorCodes()
                            );
                        } else {
                            admin.expansion().message(Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.sign").replace("%1$f", player.getName()).replace("%2$f", message)), player).translateDefaultColorCodes());
                            continue;
                        }
                    }
                }
            } else {
                if(getChatSettings().isNotifyAboutBlockedWord()) {
                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.sign").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)
                    ));
                    for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                        if(admin.hasPermission("functionalservercontrol.notification.blocked-word")) {
                            if(getConfigSettings().isButtonsOnNotifications()) {
                                admin.expansion().message(
                                                Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.sign").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player)
                                                        .append(Component.addPunishmentButtons(admin, player.nickname())).translateDefaultColorCodes()
                                );
                                continue;
                            } else {
                                admin.expansion().message(Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.sign").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player).translateDefaultColorCodes());
                                continue;
                            }
                        }
                    }
                }
            }
        });
    }

}
