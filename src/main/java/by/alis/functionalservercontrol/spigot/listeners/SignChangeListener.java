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
import org.bukkit.event.block.SignChangeEvent;
import org.jetbrains.annotations.Nullable;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.*;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class SignChangeListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        String[] lines = event.getLines();
        Player player = event.getPlayer();
        if(getChatSettings().isFunctionEnabled()) {
            if(getChatSettings().isBlockedWordsEnabled()) {
                if (getChatSettings().isCheckSignsForBlockedWords() && !player.hasPermission("functionalservercontrol.signs.blocked-words.bypass") && !getChatSettings().getDisabledWorldsForBlockedWords().contains(player.getWorld().getName())) {
                    for (String blockedWord : getChatSettings().getBlockedWords()) {
                        for (String line : lines) {
                            if (line.contains(blockedWord)) {
                                event.setCancelled(true);
                                switch (getConfigSettings().getStorageType()) {
                                    case SQLITE:
                                        getSQLiteManager().updatePlayerStatsInfo(player, StatsType.Player.BLOCKED_WORDS_USED);
                                        break;
                                    case H2: {
                                    }
                                }
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: getSQLiteManager().updatePlayerStatsInfo(player, StatsType.Player.ADVERTISE_ATTEMPTS); break;
                            case H2: {}
                        }
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
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: getSQLiteManager().updatePlayerStatsInfo(player, StatsType.Player.ADVERTISE_ATTEMPTS); break;
                            case H2: {}
                        }
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

    private void notifyAdmins(Player player, String message, @Nullable String word, boolean isAdvertise) {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
            if(isAdvertise) {
                if (getChatSettings().isNotifyAdminAboutAdvertise()) {
                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.sign").replace("%1$f", player.getName()).replace("%2$f", message)));
                    for (Player admin : Bukkit.getOnlinePlayers()) {
                        if (!admin.hasPermission("functionalservercontrol.notification.advertise")) return;
                        if (getConfigSettings().isServerSupportsHoverEvents()) {
                            if (getConfigSettings().isButtonsOnNotifications()) {
                                if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                    admin.spigot().sendMessage(MD5TextUtils.appendTwo(
                                            MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.sign").replace("%1$f", player.getName()).replace("%2$f", message)), player),
                                            MD5TextUtils.addPunishmentButtons(admin, player.getName())
                                    ));
                                    continue;
                                }
                                if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                    admin.sendMessage(
                                            AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.sign").replace("%1$f", player.getName()).replace("%2$f", message)), player).append(AdventureApiUtils.addPunishmentButtons(admin, player.getName())));
                                    continue;
                                }
                            } else {
                                if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                    admin.spigot().sendMessage(MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.sign").replace("%1$f", player.getName()).replace("%2$f", message)), player));
                                    continue;
                                }
                                if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                    admin.sendMessage(AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.sign").replace("%1$f", player.getName()).replace("%2$f", message)), player));
                                    continue;
                                }
                            }
                        } else {
                            admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.sign").replace("%1$f", player.getName()).replace("%2$f", message)));
                        }
                    }
                }
            } else {
                if(getChatSettings().isNotifyAboutBlockedWord()) {
                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.sign").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)
                    ));
                    for(Player admin : Bukkit.getOnlinePlayers()) {
                        if(admin.hasPermission(" functionalservercontrol.notification.blocked-word")) {
                            if(getConfigSettings().isServerSupportsHoverEvents()) {
                                if(getConfigSettings().isButtonsOnNotifications()) {
                                    if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                        admin.spigot().sendMessage(
                                                MD5TextUtils.appendTwo(
                                                        MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.sign").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player),
                                                        MD5TextUtils.addPunishmentButtons(admin, player.getName())));
                                        continue;
                                    }
                                    if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                        admin.sendMessage(AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.sign").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player).append(AdventureApiUtils.addPunishmentButtons(admin, player.getName())));
                                        continue;
                                    }
                                } else {
                                    if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                        admin.spigot().sendMessage(MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.sign").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player));
                                        continue;
                                    }
                                    if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                        admin.sendMessage(AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.sign").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player));
                                        continue;
                                    }
                                }
                            } else {
                                admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.sign").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)));
                            }
                        }
                    }
                }
            }
        });
    }

}
