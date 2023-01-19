package net.alis.functionalservercontrol.spigot.managers;

import net.alis.functionalservercontrol.api.enums.Chat;
import net.alis.functionalservercontrol.api.enums.StatsType;
import net.alis.functionalservercontrol.api.events.PlayerAdvertiseEvent;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.MD5TextUtils;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.AdventureApiUtils;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class AdvertiseManager {

    /**
     * Static class
     */
    public AdvertiseManager() {}

    public static boolean isMessageContainsIp(Player player, String message) {
        if(!getChatSettings().isFunctionEnabled() || !getChatSettings().isChatIpProtectionEnabled()) return false;
        if(player.hasPermission("functionalservercontrol.advertise.bypass") || player.hasPermission("functionalservercontrol.advertise.chat.bypass")) return false;
        if(OtherUtils.isArgumentIP(TextUtils.stringToMonolith(message))) {
            player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-in-chat")));
            BaseManager.getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.ADVERTISE_ATTEMPTS);
            notifyAdminAboutAdvertiseInChat(player, message);
            PlayerAdvertiseEvent asyncPlayerAdvertiseEvent = new PlayerAdvertiseEvent(player, Chat.AdvertiseMethod.CHAT, message);
            if(getConfigSettings().isApiEnabled()) {
                Bukkit.getPluginManager().callEvent(asyncPlayerAdvertiseEvent);
            }
            TaskManager.preformSync(() -> {
                for(String action : getChatSettings().getChatIpProtectionActions()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                }
            });
            return true;
        }
        return false;
    }

    public static boolean isMessageContainsDomain(Player player, String message) {
        if(!getChatSettings().isFunctionEnabled() || !getChatSettings().isChatDomainsProtectionEnabled()) return false;
        if(player.hasPermission("functionalservercontrol.advertise.bypass") || player.hasPermission("functionalservercontrol.advertise.chat.bypass")) return false;
        if(OtherUtils.isArgumentDomain(TextUtils.stringToMonolith(message))) {
            player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-in-chat")));
            BaseManager.getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.ADVERTISE_ATTEMPTS);
            notifyAdminAboutAdvertiseInChat(player, message);
            PlayerAdvertiseEvent asyncPlayerAdvertiseEvent = new PlayerAdvertiseEvent(player, Chat.AdvertiseMethod.CHAT, message);
            if(getConfigSettings().isApiEnabled()) {
                Bukkit.getPluginManager().callEvent(asyncPlayerAdvertiseEvent);
            }
            TaskManager.preformSync(() -> {
                for(String action : getChatSettings().getCommandsIpProtectionActions()) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()));
                }
            });
            return true;
        }
        return false;
    }

    public static boolean isCommandContainsAdvertise(Player player, String command) {
        if(!getChatSettings().isFunctionEnabled()) return false;
        if(!getChatSettings().isCommandsIpProtectionEnabled() && !getChatSettings().isCommandsDomainsProtectionEnabled()) return false;
        if(player.hasPermission("functionalservercontrol.advertise.bypass") || player.hasPermission("functionalservercontrol.advertise.commands.bypass")) return false;
        if(getChatSettings().isCommandsIpProtectionEnabled()) {
            if(OtherUtils.isArgumentIP(TextUtils.stringToMonolith(command))) {
                BaseManager.getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.ADVERTISE_ATTEMPTS);
                player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-in-command")));
                notifyAdminsAboutAdvertiseInCommand(player, command);
                for(String action : getChatSettings().getCommandsIpProtectionActions()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", command));
                PlayerAdvertiseEvent asyncPlayerAdvertiseEvent = new PlayerAdvertiseEvent(player, Chat.AdvertiseMethod.COMMAND, String.join(", ", command));
                if(getConfigSettings().isApiEnabled()) Bukkit.getPluginManager().callEvent(asyncPlayerAdvertiseEvent);
                return true;
            }
        }
        if(getChatSettings().isCommandsDomainsProtectionEnabled()) {
            if(OtherUtils.isArgumentDomain(TextUtils.stringToMonolith(command))) {
                BaseManager.getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.ADVERTISE_ATTEMPTS);
                player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.advertise-in-command")));
                notifyAdminsAboutAdvertiseInCommand(player, command);
                for(String action : getChatSettings().getCommandsDomainsProtectionActions()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), action.replace("%1$f", player.getName()).replace("%2$f", command));
                PlayerAdvertiseEvent asyncPlayerAdvertiseEvent = new PlayerAdvertiseEvent(player, Chat.AdvertiseMethod.COMMAND, command);
                if(getConfigSettings().isApiEnabled()) Bukkit.getPluginManager().callEvent(asyncPlayerAdvertiseEvent);
                return true;
            }
        }
        return false;
    }

    private static void notifyAdminAboutAdvertiseInChat(Player player, String message) {
        TaskManager.preformAsync(() -> {
            if(getChatSettings().isNotifyAdminAboutAdvertise()) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.chat")
                        .replace("%1$f", player.getName())
                        .replace("%2$f", message)));
            }
            for(Player admin : Bukkit.getOnlinePlayers()) {
                if(!admin.hasPermission("functionalservercontrol.notification.advertise")) return;
                if(getConfigSettings().isServerSupportsHoverEvents()) {
                    if(getConfigSettings().isButtonsOnNotifications()) {
                        if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                            admin.spigot().sendMessage(MD5TextUtils.appendTwo(
                                    MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.chat").replace("%1$f", player.getName()).replace("%2$f", message)), player),
                                    MD5TextUtils.appendTwo(
                                            MD5TextUtils.createClickableSuggestCommandText(setColors(" " + getGlobalVariables().getButtonBan() + " "), "/ban " + player.getName()),
                                            MD5TextUtils.createClickableSuggestCommandText(setColors(getGlobalVariables().getButtonMute()), "/mute " + player.getName())
                                    )
                            ));
                            continue;
                        }
                        if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                            admin.sendMessage(AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.chat").replace("%1$f", player.getName()).replace("%2$f", message)), player)
                                    .append(AdventureApiUtils.createClickableSuggestCommandText(setColors(" " + getGlobalVariables().getButtonBan() + " "), "/ban " + player.getName()))
                                    .append(AdventureApiUtils.createClickableSuggestCommandText(setColors(getGlobalVariables().getButtonMute()), "/mute " + player.getName())));
                            continue;
                        }
                    } else {
                        if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                            admin.spigot().sendMessage(MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.chat").replace("%1$f", player.getName()).replace("%2$f", message)), player));
                            continue;
                        }
                        if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                            admin.sendMessage(AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.chat").replace("%1$f", player.getName()).replace("%2$f", message)), player));
                            continue;
                        }
                    }
                } else {
                    admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.chat")
                            .replace("%1$f", player.getName())
                            .replace("%2$f", message)));
                }
            }
        });
    }

    private static void notifyAdminsAboutAdvertiseInCommand(Player player, String command) {
        TaskManager.preformAsync(() -> {
            if (getChatSettings().isNotifyAdminAboutAdvertise()) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.command").replace("%1$f", player.getName()).replace("%2$f", command)));
                for (Player admin : Bukkit.getOnlinePlayers()) {
                    if (!admin.hasPermission("functionalservercontrol.notification.advertise")) return;
                    if (getConfigSettings().isServerSupportsHoverEvents()) {
                        if (getConfigSettings().isButtonsOnNotifications()) {
                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                admin.spigot().sendMessage(MD5TextUtils.appendTwo(
                                        MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.command").replace("%1$f", player.getName()).replace("%2$f", command)), player),
                                        MD5TextUtils.addPunishmentButtons(admin, player.getName())
                                ));
                                continue;
                            }
                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                admin.sendMessage(
                                        AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.command").replace("%1$f", player.getName()).replace("%2$f", command)), player).append(AdventureApiUtils.addPunishmentButtons(admin, player.getName())));
                                continue;
                            }
                        } else {
                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                admin.spigot().sendMessage(MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.command").replace("%1$f", player.getName()).replace("%2$f", command)), player));
                                continue;
                            }
                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                admin.sendMessage(AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.command").replace("%1$f", player.getName()).replace("%2$f", command)), player));
                                continue;
                            }
                        }
                    } else {
                        admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.advertise.command").replace("%1$f", player.getName()).replace("%2$f", command)));
                    }
                }
            }
        });
    }

}
