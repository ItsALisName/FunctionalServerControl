package by.alis.functionalservercontrol.spigot.managers;

import by.alis.functionalservercontrol.api.enums.StatsType;
import by.alis.functionalservercontrol.spigot.additional.misc.AdventureApiUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.MD5TextUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import by.alis.functionalservercontrol.spigot.expansions.Expansions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class ChatManager {

    public String replaceMessageIfNeed(Player player, String message) {
        if(player.hasPermission("functionalservercontrol.chat.messages-replaces.bypass")) return message;
        for(Map.Entry<String, String> entry : getChatSettings().getMessageReplacer().entrySet()) {
            if(message.contains(entry.getKey())) return message.replace(entry.getKey(), entry.getValue());
        }
        return message;
    }

    public boolean playerHasChatDelay(Player player) {
        if(TemporaryCache.getChatDelays().containsKey(player.getUniqueId())) {
            player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.chat-delay").replace("%1$f", String.valueOf(TemporaryCache.getChatDelays().get(player.getUniqueId())))));
            return true;
        }
        return false;
    }

    public void setupChatDelay(Player player) {
        if(player.hasPermission("functionalservercontrol.chat.delay.bypass")) return;
        TaskManager.preformAsync(() -> {
            if(getChatSettings().isUseGroups()) {
                if(Expansions.getVaultManager().isVaultSetuped()) {
                    String playerGroup = Expansions.getVaultManager().getPlayerGroup(player);
                    if(getChatSettings().getChatDelays().containsKey(playerGroup)) {
                        TemporaryCache.addChatDelay(player, getChatSettings().getChatDelays().get(playerGroup));
                        return;
                    }
                }
                if(Expansions.getLuckPermsManager().isLuckPermsSetuped()) {
                    String playerGroup = Expansions.getLuckPermsManager().getPlayerGroup(player);
                    if(getChatSettings().getChatDelays().containsKey(playerGroup)) {
                        TemporaryCache.addChatDelay(player, getChatSettings().getChatDelays().get(playerGroup));
                        return;
                    }
                }
                TemporaryCache.addChatDelay(player, getChatSettings().getChatDelays().get("global_delay"));
                return;
            } else {
                TemporaryCache.addChatDelay(player, getChatSettings().getChatDelays().get("global_delay"));
                return;
            }
        });
    }

    public boolean isRepeatingMessage(Player player, String message) {
        if(TemporaryCache.getRepeatingMessages().containsKey(player.getUniqueId()) && TemporaryCache.getRepeatingMessages().get(player.getUniqueId()).equalsIgnoreCase(message)) {
            player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.repeating-message")));
            return true;
        }
        return false;
    }

    public void setRepeatingMessage(Player player, String message) {
        if(player.hasPermission("functionalservercontrol.chat.repeating-message.bypass")) return;
        TaskManager.preformAsync(() -> {
            if(getChatSettings().isBlockRepeatingMessages()) {
                if (TemporaryCache.getRepeatingMessages().containsKey(player.getUniqueId())) {
                    TemporaryCache.getRepeatingMessages().replace(player.getUniqueId(), message);
                } else {
                    TemporaryCache.addRepeatingMessage(player, message);
                }
            }
        });
    }

    public boolean isMessageContainsBlockedWord(Player player, String message) {
        if(!getChatSettings().isBlockedWordsEnabled()) return false;
        if(player.hasPermission("functionalservercontrol.chat.blocked-words.bypass") || !getChatSettings().isBlockedWordsEnabled()) return false;
        if(getChatSettings().getDisabledWorldsForBlockedWords().contains(player.getWorld().getName())) return false;
        for(String word : getChatSettings().getBlockedWords()) {
            for (String messageArg : message.split(" ")) {
                if (messageArg.equalsIgnoreCase(word)) {
                    player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.blocked-word-in-chat").replace("%1$f", word)));
                    if (getChatSettings().isPunishEnabledForBlockedWords()) {
                        for (String punishCmd : getChatSettings().getPunishForBlockedWords()) {
                            TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishCmd.replace("%1$f", player.getName()).replace("%2$f", word)));
                        }
                    }
                    this.notifyAdminsAboutBlockedWord(player, word, message);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCommandContainsBlockedWord(Player player, String command) {
        if(!getChatSettings().isBlockedWordsEnabled()) return false;
        if(player.hasPermission("functionalservercontrol.commands.blocked-words.bypass")) return false;
        if(getChatSettings().getDisabledWorldsForBlockedWords().contains(player.getWorld().getName())) return false;
        for(String word : getChatSettings().getBlockedWords()) {
            for(String commandArg : command.split(" ")) {
                if (commandArg.equalsIgnoreCase(word)) {
                    player.sendMessage(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.blocked-word-in-command").replace("%1$f", word)));
                    if (getChatSettings().isPunishEnabledForBlockedWords()) {
                        for (String punishCmd : getChatSettings().getPunishForBlockedWords()) {
                            TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishCmd.replace("%1$f", player.getName()).replace("%2$f", word)));
                        }
                    }
                    this.notifyAdminsAboutBlockedWordInCommand(player, word, command);
                    return true;
                }
            }
        }
        return false;
    }

    private void notifyAdminsAboutBlockedWord(Player player, String word, String message) {
        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.BLOCKED_WORDS_USED);
        TaskManager.preformAsync(() -> {
            if(getChatSettings().isNotifyAboutBlockedWord()) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.chat").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)));
                for(Player admin : Bukkit.getOnlinePlayers()) {
                    if(admin.hasPermission(" functionalservercontrol.notification.blocked-word")) {
                        if(getConfigSettings().isServerSupportsHoverEvents()) {
                            if(getConfigSettings().isButtonsOnNotifications()) {
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                    admin.spigot().sendMessage(
                                            MD5TextUtils.appendTwo(
                                                    MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.chat").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player),
                                                    MD5TextUtils.addPunishmentButtons(admin, player.getName())));
                                    continue;
                                }
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                    admin.sendMessage(AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.chat").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player).append(AdventureApiUtils.addPunishmentButtons(admin, player.getName())));
                                    continue;
                                }
                            } else {
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                    admin.spigot().sendMessage(
                                            MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.chat").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player));
                                    continue;
                                }
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                    admin.sendMessage(AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.chat").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)), player));
                                    continue;
                                }
                            }
                        } else {
                            admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.chat").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", message)));
                        }
                    }
                }
            }
        });
    }

    public void notifyAdminsOnTryChat(Player player, String message, String timeLeft) {
        TaskManager.preformAsync(() -> {
            if (getConfigSettings().isConsoleNotification()) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.mute").replace("%1$f", player.getName()).replace("%2$f", message).replace("%3$f", timeLeft)));
            }
            if (getConfigSettings().isPlayersNotification()) {
                for (Player admin : Bukkit.getOnlinePlayers()) {
                    if (admin.hasPermission("functionalservercontrol.notification.mute")) {
                        if(getConfigSettings().isServerSupportsHoverEvents()) {
                            if (getConfigSettings().isButtonsOnNotifications()) {
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                    admin.spigot().sendMessage(MD5TextUtils.appendTwo(
                                            MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.mute").replace("%1$f", player.getName()).replace("%2$f", message).replace("%3$f", timeLeft)), player),
                                            MD5TextUtils.addPardonButtons(admin, player.getName()))
                                    );
                                    continue;
                                }
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                    admin.sendMessage(
                                            AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.mute").replace("%1$f", player.getName()).replace("%2$f", message).replace("%3$f", timeLeft)), player)
                                                    .append(AdventureApiUtils.addPardonButtons(admin, player.getName())));
                                    continue;
                                }
                            } else {
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                    admin.spigot().sendMessage(MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.mute").replace("%1$f", player.getName()).replace("%2$f", message).replace("%3$f", timeLeft)), player));
                                    continue;
                                }
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                    admin.sendMessage(AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.mute").replace("%1$f", player.getName()).replace("%2$f", message).replace("%3$f", timeLeft)), player));
                                    continue;
                                }
                            }
                        }
                        admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.mute").replace("%1$f", player.getName()).replace("%2$f", message).replace("%3$f", timeLeft)));
                    }
                }
            }
        });
    }

    private void notifyAdminsAboutBlockedWordInCommand(Player player, String word, String command) {
        getBaseManager().updatePlayerStatsInfo(player, StatsType.Player.BLOCKED_WORDS_USED);
        TaskManager.preformAsync(() -> {
            if(getChatSettings().isNotifyAboutBlockedWord()) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.command").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", command)));
                for(Player admin : Bukkit.getOnlinePlayers()) {
                    if(admin.hasPermission(" functionalservercontrol.notification.blocked-word")) {
                        if(getConfigSettings().isServerSupportsHoverEvents()) {
                            if(getConfigSettings().isButtonsOnNotifications()) {
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                    admin.spigot().sendMessage(
                                            MD5TextUtils.appendTwo(
                                                    MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.command").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", command)), player),
                                                    MD5TextUtils.addPunishmentButtons(admin, player.getName())));
                                    continue;
                                }
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                    admin.sendMessage(
                                            AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.command").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", command)), player)
                                                    .append(AdventureApiUtils.addPunishmentButtons(admin, player.getName())));
                                    continue;
                                }
                            } else {
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                    admin.spigot().sendMessage(MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.command").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", command)), player));
                                    continue;
                                }
                                if(getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                    admin.sendMessage(AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.command").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", command)), player));
                                    continue;
                                }
                            }
                        } else {
                            admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.command").replace("%1$f", player.getName()).replace("%2$f", word).replace("%3$f", command)));
                        }
                    }
                }
            }
        });
    }

    private static final ChatManager chatManager = new ChatManager();
    public static ChatManager getChatManager() {
        return chatManager;
    }
}
