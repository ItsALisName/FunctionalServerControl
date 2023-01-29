package net.alis.functionalservercontrol.spigot.managers;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.enums.Chat;
import net.alis.functionalservercontrol.api.enums.StatsType;
import net.alis.functionalservercontrol.api.events.PlayerAdvertiseEvent;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.Component;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;

import org.bukkit.Bukkit;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class AdvertiseManager {

    /**
     * Static class
     */
    public AdvertiseManager() {}

    public static boolean isMessageContainsIp(FunctionalPlayer player, String message) {
        if(!getChatSettings().isFunctionEnabled() || !getChatSettings().isChatIpProtectionEnabled()) return false;
        if(player.hasPermission("functionalservercontrol.advertise.bypass") || player.hasPermission("functionalservercontrol.advertise.chat.bypass")) return false;
        if(OtherUtils.isArgumentIP(TextUtils.stringToMonolith(message))) {
            player.message(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-in-chat")));
            BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.ADVERTISE_ATTEMPTS);
            notifyAdminAboutAdvertiseInChat(player, message);
            PlayerAdvertiseEvent asyncPlayerAdvertiseEvent = new PlayerAdvertiseEvent(player, Chat.AdvertiseMethod.CHAT, message);
            if(getConfigSettings().isApiEnabled()) {
                Bukkit.getPluginManager().callEvent(asyncPlayerAdvertiseEvent);
            }
            TaskManager.preformSync(() -> {
                for(String action : getChatSettings().getChatIpProtectionActions()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.nickname()));
                }
            });
            return true;
        }
        return false;
    }

    public static boolean isMessageContainsDomain(FunctionalPlayer player, String message) {
        if(!getChatSettings().isFunctionEnabled() || !getChatSettings().isChatDomainsProtectionEnabled()) return false;
        if(player.hasPermission("functionalservercontrol.advertise.bypass") || player.hasPermission("functionalservercontrol.advertise.chat.bypass")) return false;
        if(OtherUtils.isArgumentDomain(TextUtils.stringToMonolith(message))) {
            player.message(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-in-chat")));
            BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.ADVERTISE_ATTEMPTS);
            notifyAdminAboutAdvertiseInChat(player, message);
            PlayerAdvertiseEvent asyncPlayerAdvertiseEvent = new PlayerAdvertiseEvent(player, Chat.AdvertiseMethod.CHAT, message);
            if(getConfigSettings().isApiEnabled()) {
                Bukkit.getPluginManager().callEvent(asyncPlayerAdvertiseEvent);
            }
            TaskManager.preformSync(() -> {
                for(String action : getChatSettings().getCommandsIpProtectionActions()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.nickname()));
                }
            });
            return true;
        }
        return false;
    }

    public static boolean isCommandContainsAdvertise(FunctionalPlayer player, String command) {
        if(!getChatSettings().isFunctionEnabled()) return false;
        if(!getChatSettings().isCommandsIpProtectionEnabled() && !getChatSettings().isCommandsDomainsProtectionEnabled()) return false;
        if(player.hasPermission("functionalservercontrol.advertise.bypass") || player.hasPermission("functionalservercontrol.advertise.commands.bypass")) return false;
        if(getChatSettings().isCommandsIpProtectionEnabled()) {
            if(OtherUtils.isArgumentIP(TextUtils.stringToMonolith(command))) {
                BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.ADVERTISE_ATTEMPTS);
                player.message(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-in-command")));
                notifyAdminsAboutAdvertiseInCommand(player, command);
                for(String action : getChatSettings().getCommandsIpProtectionActions()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.nickname()).replace("%2$f", command));
                PlayerAdvertiseEvent asyncPlayerAdvertiseEvent = new PlayerAdvertiseEvent(player, Chat.AdvertiseMethod.COMMAND, String.join(", ", command));
                if(getConfigSettings().isApiEnabled()) Bukkit.getPluginManager().callEvent(asyncPlayerAdvertiseEvent);
                return true;
            }
        }
        if(getChatSettings().isCommandsDomainsProtectionEnabled()) {
            if(OtherUtils.isArgumentDomain(TextUtils.stringToMonolith(command))) {
                BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.ADVERTISE_ATTEMPTS);
                player.message(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-in-command")));
                notifyAdminsAboutAdvertiseInCommand(player, command);
                for(String action : getChatSettings().getCommandsDomainsProtectionActions()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.nickname()).replace("%2$f", command));
                PlayerAdvertiseEvent asyncPlayerAdvertiseEvent = new PlayerAdvertiseEvent(player, Chat.AdvertiseMethod.COMMAND, command);
                if(getConfigSettings().isApiEnabled()) Bukkit.getPluginManager().callEvent(asyncPlayerAdvertiseEvent);
                return true;
            }
        }
        return false;
    }

    private static void notifyAdminAboutAdvertiseInChat(FunctionalPlayer player, String message) {
        TaskManager.preformAsync(() -> {
            if(getChatSettings().isNotifyAdminAboutAdvertise()) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.chat")
                        .replace("%1$f", player.nickname())
                        .replace("%2$f", message)));
            }
            for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                if(!admin.hasPermission("functionalservercontrol.notification.advertise")) return;
                if(getConfigSettings().isButtonsOnNotifications()) {
                    admin.expansion().message(Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.chat").replace("%1$f", player.nickname()).replace("%2$f", message)), player)
                                    .append(Component.addPunishmentButtons(admin, player.nickname())).translateDefaultColorCodes()
                    );
                } else {
                    admin.expansion().message(Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.chat").replace("%1$f", player.nickname()).replace("%2$f", message)), player).translateDefaultColorCodes());
                }
            }
        });
    }

    private static void notifyAdminsAboutAdvertiseInCommand(FunctionalPlayer player, String command) {
        TaskManager.preformAsync(() -> {
            if (getChatSettings().isNotifyAdminAboutAdvertise()) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.command").replace("%1$f", player.nickname()).replace("%2$f", command)));
                for (FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                    if (!admin.hasPermission("functionalservercontrol.notification.advertise")) return;
                    if (getConfigSettings().isButtonsOnNotifications()) {
                        admin.expansion().message(
                                Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.command").replace("%1$f", player.nickname()).replace("%2$f", command)), player)
                                        .append(Component.addPunishmentButtons(admin, player.nickname())).translateDefaultColorCodes()
                        );
                    } else {
                        admin.expansion().message(Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.command").replace("%1$f", player.nickname()).replace("%2$f", command)), player).translateDefaultColorCodes());
                    }
                }
            }
        });
    }

}
