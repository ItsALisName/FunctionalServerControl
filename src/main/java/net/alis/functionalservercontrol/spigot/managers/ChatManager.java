package net.alis.functionalservercontrol.spigot.managers;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.Component;
import net.alis.functionalservercontrol.api.enums.StatsType;
import net.alis.functionalservercontrol.spigot.additional.misc.TemporaryCache;
import net.alis.functionalservercontrol.spigot.dependencies.Expansions;
import org.bukkit.Bukkit;

import java.util.Map;

import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class ChatManager {

    public String replaceMessageIfNeed(FunctionalPlayer player, String message) {
        if(player.hasPermission("functionalservercontrol.chat.messages-replaces.bypass")) return message;
        for(Map.Entry<String, String> entry : getChatSettings().getMessageReplacer().entrySet()) {
            if(message.contains(entry.getKey())) return message.replace(entry.getKey(), entry.getValue());
        }
        return message;
    }

    public boolean playerHasChatDelay(FunctionalPlayer player) {
        if(TemporaryCache.getChatDelays().containsKey(player.getUniqueId())) {
            player.message(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.chat-delay").replace("%1$f", String.valueOf(TemporaryCache.getChatDelays().get(player.getUniqueId())))));
            return true;
        }
        return false;
    }

    public void setupChatDelay(FunctionalPlayer player) {
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

    public boolean isRepeatingMessage(FunctionalPlayer player, String message) {
        if(TemporaryCache.getRepeatingMessages().containsKey(player.getFunctionalId()) && TemporaryCache.getRepeatingMessages().get(player.getUniqueId()).equalsIgnoreCase(message)) {
            player.message(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.repeating-message")));
            return true;
        }
        return false;
    }

    public void setRepeatingMessage(FunctionalPlayer player, String message) {
        if(player.hasPermission("functionalservercontrol.chat.repeating-message.bypass")) return;
        TaskManager.preformAsync(() -> {
            if(getChatSettings().isBlockRepeatingMessages()) {
                if (TemporaryCache.getRepeatingMessages().containsKey(player.getFunctionalId())) {
                    TemporaryCache.getRepeatingMessages().replace(player.getFunctionalId(), message);
                } else {
                    TemporaryCache.addRepeatingMessage(player, message);
                }
            }
        });
    }

    public boolean isMessageContainsBlockedWord(FunctionalPlayer player, String message) {
        if(!getChatSettings().isBlockedWordsEnabled()) return false;
        if(player.hasPermission("functionalservercontrol.chat.blocked-words.bypass") || !getChatSettings().isBlockedWordsEnabled()) return false;
        if(getChatSettings().getDisabledWorldsForBlockedWords().contains(player.world().getName())) return false;
        for(String word : getChatSettings().getBlockedWords()) {
            for (String messageArg : message.split(" ")) {
                if (messageArg.equalsIgnoreCase(word)) {
                    player.message(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.blocked-word-in-chat").replace("%1$f", word)));
                    if (getChatSettings().isPunishEnabledForBlockedWords()) {
                        for (String punishCmd : getChatSettings().getPunishForBlockedWords()) {
                            TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishCmd.replace("%1$f", player.nickname()).replace("%2$f", word)));
                        }
                    }
                    this.notifyAdminsAboutBlockedWord(player, word, message);
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isCommandContainsBlockedWord(FunctionalPlayer player, String command) {
        if(!getChatSettings().isBlockedWordsEnabled()) return false;
        if(player.hasPermission("functionalservercontrol.commands.blocked-words.bypass")) return false;
        if(getChatSettings().getDisabledWorldsForBlockedWords().contains(player.world().getName())) return false;
        for(String word : getChatSettings().getBlockedWords()) {
            for(String commandArg : command.split(" ")) {
                if (commandArg.equalsIgnoreCase(word)) {
                    player.message(setColors(getFileAccessor().getLang().getString("other.chat-settings-messages.blocked-word-in-command").replace("%1$f", word)));
                    if (getChatSettings().isPunishEnabledForBlockedWords()) {
                        for (String punishCmd : getChatSettings().getPunishForBlockedWords()) {
                            TaskManager.preformSync(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), punishCmd.replace("%1$f", player.nickname()).replace("%2$f", word)));
                        }
                    }
                    this.notifyAdminsAboutBlockedWordInCommand(player, word, command);
                    return true;
                }
            }
        }
        return false;
    }

    private void notifyAdminsAboutBlockedWord(FunctionalPlayer player, String word, String message) {
        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.BLOCKED_WORDS_USED);
        TaskManager.preformAsync(() -> {
            if(getChatSettings().isNotifyAboutBlockedWord()) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.chat").replace("%1$f", player.nickname()).replace("%2$f", word).replace("%3$f", message)));
                for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                    if(admin.hasPermission("functionalservercontrol.notification.blocked-word")) {
                        if(getConfigSettings().isButtonsOnNotifications()) {
                            admin.expansion().message(
                                            Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.chat").replace("%1$f", player.nickname()).replace("%2$f", word).replace("%3$f", message)), player)
                                                    .append(Component.addPunishmentButtons(admin, player.nickname())).translateDefaultColorCodes()
                            );
                            continue;
                        } else {
                            admin.expansion().message(
                                    Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.chat").replace("%1$f", player.nickname()).replace("%2$f", word).replace("%3$f", message)), player).translateDefaultColorCodes());
                            continue;
                        }
                    }
                }
            }
        });
    }

    public void notifyAdminsOnTryChat(FunctionalPlayer player, String message, String timeLeft) {
        TaskManager.preformAsync(() -> {
            if (getConfigSettings().isConsoleNotification()) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.mute").replace("%1$f", player.nickname()).replace("%2$f", message).replace("%3$f", timeLeft)));
            }
            if (getConfigSettings().isPlayersNotification()) {
                for (FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                    if (admin.hasPermission("functionalservercontrol.notification.mute")) {
                        if (getConfigSettings().isButtonsOnNotifications()) {
                            admin.expansion().message(
                                    Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.mute").replace("%1$f", player.nickname()).replace("%2$f", message).replace("%3$f", timeLeft)), player)
                                            .append(Component.addPardonButtons(admin, player.nickname())).translateDefaultColorCodes()
                            );
                        } else {
                            admin.expansion().message(Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.mute").replace("%1$f", player.nickname()).replace("%2$f", message).replace("%3$f", timeLeft)), player).translateDefaultColorCodes());
                            continue;
                        }
                    }
                }
            }
        });
    }

    private void notifyAdminsAboutBlockedWordInCommand(FunctionalPlayer player, String word, String command) {
        BaseManager.getBaseManager().updatePlayerStatsInfo(player.getFunctionalId(), StatsType.Player.BLOCKED_WORDS_USED);
        TaskManager.preformAsync(() -> {
            if(getChatSettings().isNotifyAboutBlockedWord()) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.command").replace("%1$f", player.nickname()).replace("%2$f", word).replace("%3$f", command)));
                for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                    if(admin.hasPermission("functionalservercontrol.notification.blocked-word")) {
                        if(getConfigSettings().isButtonsOnNotifications()) {
                            admin.expansion().message(
                                            Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.command").replace("%1$f", player.nickname()).replace("%2$f", word).replace("%3$f", command)), player)
                                                    .append(Component.addPunishmentButtons(admin, player.nickname())).translateDefaultColorCodes()
                            );
                            continue;
                        } else {
                            admin.expansion().message(Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.blocked-word.command").replace("%1$f", player.nickname()).replace("%2$f", word).replace("%3$f", command)), player).translateDefaultColorCodes());
                            continue;
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
