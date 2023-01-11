package by.alis.functionalservercontrol.spigot.additional.globalsettings;

import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class ChatSettings {

    private boolean functionEnabled;
    private boolean useGroups;
    private boolean messagesReplacerEnabled;
    private boolean tickDelaysIfOffline;
    private boolean blockRepeatingMessages;
    private final HashMap<String, String> messageReplacer = new HashMap<>();
    private final HashMap<String, Integer> chatDelays = new HashMap<>();
    private boolean blockedWordsEnabled;
    private boolean punishEnabledForBlockedWords;
    private final List<String> blockedWords = new ArrayList<>();
    private final List<String> punishForBlockedWords = new ArrayList<>();
    private final List<String> disabledWorldsForBlockedWords = new ArrayList<>();
    private boolean notifyAboutBlockedWord;
    private boolean checkBooksForBlockedWords;
    private boolean checkSignsForBlockedWords;
    private boolean checkItemsForBlockedWords;
    private boolean checkCommandsForBlockedWords;

    private boolean notifyAdminAboutAdvertise;
    private boolean chatIpProtectionEnabled;
    private final List<String> chatIpProtectionActions = new ArrayList<>();
    private boolean chatDomainsProtectionEnabled;
    private final List<String> chatDomainsProtectionActions = new ArrayList<>();
    private boolean signsIpProtectionEnabled;
    private final List<String> signsIpProtectionActions = new ArrayList<>();
    private boolean signsDomainsProtectionEnabled;
    private final List<String> signsDomainsProtectionActions = new ArrayList<>();
    private boolean bookIpProtectionEnabled;
    private final List<String> bookIpProtectionActions = new ArrayList<>();
    private boolean bookDomainsProtectionEnabled;
    private final List<String> bookDomainsProtectionActions = new ArrayList<>();
    private boolean itemsIpProtectionEnabled;
    private final List<String> itemsIpProtectionActions = new ArrayList<>();
    private boolean itemsDomainsProtectionEnabled;
    private final List<String> itemsDomainsProtectionActions = new ArrayList<>();
    private boolean commandsIpProtectionEnabled;
    private final List<String> commandsIpProtectionActions = new ArrayList<>();
    private boolean commandsDomainsProtectionEnabled;
    private final List<String> commandsDomainsProtectionActions = new ArrayList<>();


    public boolean isNotifyAdminAboutAdvertise() {
        return notifyAdminAboutAdvertise;
    }
    public boolean isCommandsIpProtectionEnabled() {
        return commandsIpProtectionEnabled;
    }
    private void setCommandsIpProtectionEnabled(boolean commandsIpProtectionEnabled) {
        this.commandsIpProtectionEnabled = commandsIpProtectionEnabled;
    }
    public boolean isCommandsDomainsProtectionEnabled() {
        return commandsDomainsProtectionEnabled;
    }
    private void setCommandsDomainsProtectionEnabled(boolean commandsDomainsProtectionEnabled) {
        this.commandsDomainsProtectionEnabled = commandsDomainsProtectionEnabled;
    }
    public List<String> getCommandsDomainsProtectionActions() {
        return commandsDomainsProtectionActions;
    }
    private void setCommandsDomainsProtectionActions(List<String> commandsDomainsProtectionActions) {
        this.commandsDomainsProtectionActions.clear();
        this.commandsDomainsProtectionActions.addAll(commandsDomainsProtectionActions);
    }
    public List<String> getCommandsIpProtectionActions() {
        return commandsIpProtectionActions;
    }
    private void setCommandsIpProtectionActions(List<String> commandsIpProtectionActions) {
        this.commandsIpProtectionActions.clear();
        this.commandsIpProtectionActions.addAll(commandsIpProtectionActions);
    }
    public boolean isItemsIpProtectionEnabled() {
        return itemsIpProtectionEnabled;
    }
    private void setItemsIpProtectionEnabled(boolean itemsIpProtectionEnabled) {
        this.itemsIpProtectionEnabled = itemsIpProtectionEnabled;
    }
    public boolean isItemsDomainsProtectionEnabled() {
        return itemsDomainsProtectionEnabled;
    }
    private void setItemsDomainsProtectionEnabled(boolean itemsDomainsProtectionEnabled) {
        this.itemsDomainsProtectionEnabled = itemsDomainsProtectionEnabled;
    }
    public List<String> getItemsIpProtectionActions() {
        return itemsIpProtectionActions;
    }
    private void setItemsIpProtectionActions(List<String> itemsIpProtectionActions) {
        this.itemsIpProtectionActions.clear();
        this.itemsIpProtectionActions.addAll(itemsIpProtectionActions);
    }
    public List<String> getItemsDomainsProtectionActions() {
        return itemsDomainsProtectionActions;
    }
    private void setItemsDomainsProtectionActions(List<String> itemsDomainsProtectionActions) {
        this.itemsDomainsProtectionActions.clear();
        this.itemsDomainsProtectionActions.addAll(itemsDomainsProtectionActions);
    }
    public boolean isBookIpProtectionEnabled() {
        return bookIpProtectionEnabled;
    }
    private void setBookIpProtectionEnabled(boolean bookIpProtectionEnabled) {
        this.bookIpProtectionEnabled = bookIpProtectionEnabled;
    }
    public boolean isBookDomainsProtectionEnabled() {
        return bookDomainsProtectionEnabled;
    }
    private void setBookDomainsProtectionEnabled(boolean bookDomainsProtectionEnabled) {
        this.bookDomainsProtectionEnabled = bookDomainsProtectionEnabled;
    }
    public List<String> getBookIpProtectionActions() {
        return bookIpProtectionActions;
    }
    private void setBookIpProtectionActions(List<String> bookIpProtectionActions) {
        this.bookIpProtectionActions.clear();
        this.bookIpProtectionActions.addAll(bookIpProtectionActions);
    }
    public List<String> getBookDomainsProtectionActions() {
        return bookDomainsProtectionActions;
    }
    private void setBookDomainsProtectionActions(List<String> bookDomainsProtectionActions) {
        this.bookDomainsProtectionActions.clear();
        this.bookDomainsProtectionActions.addAll(bookDomainsProtectionActions);
    }
    public boolean isSignsDomainsProtectionEnabled() {
        return signsDomainsProtectionEnabled;
    }
    private void setSignsDomainsProtectionEnabled(boolean signsDomainsProtectionEnabled) {
        this.signsDomainsProtectionEnabled = signsDomainsProtectionEnabled;
    }
    public List<String> getSignsDomainsProtectionActions() {
        return signsDomainsProtectionActions;
    }
    private void setSignsDomainsProtectionActions(List<String> signsDomainsProtectionActions) {
        this.signsDomainsProtectionActions.clear();
        this.signsDomainsProtectionActions.addAll(signsDomainsProtectionActions);
    }
    public boolean isSignsIpProtectionEnabled() {
        return signsIpProtectionEnabled;
    }
    private void setSignsIpProtectionEnabled(boolean signsIpProtectionEnabled) {
        this.signsIpProtectionEnabled = signsIpProtectionEnabled;
    }
    public List<String> getSignsIpProtectionActions() {
        return signsIpProtectionActions;
    }
    public void setSignsIpProtectionActions(List<String> signsIpProtectionActions) {
        this.signsIpProtectionActions.clear();
        this.signsIpProtectionActions.addAll(signsIpProtectionActions);
    }
    private void setNotifyAdminAboutAdvertise(boolean notifyAdminAboutAdvertise) {
        this.notifyAdminAboutAdvertise = notifyAdminAboutAdvertise;
    }
    public boolean isChatIpProtectionEnabled() {
        return chatIpProtectionEnabled;
    }
    public void setChatIpProtectionEnabled(boolean chatIpProtectionEnabled) {
        this.chatIpProtectionEnabled = chatIpProtectionEnabled;
    }
    public List<String> getChatIpProtectionActions() {
        return chatIpProtectionActions;
    }
    public void setChatIpProtectionActions(List<String> chatIpProtectionActions) {
        this.chatIpProtectionActions.clear();
        this.chatIpProtectionActions.addAll(chatIpProtectionActions);
    }
    public boolean isChatDomainsProtectionEnabled() {
        return chatDomainsProtectionEnabled;
    }
    private void setChatDomainsProtectionEnabled(boolean chatDomainsProtectionEnabled) {
        this.chatDomainsProtectionEnabled = chatDomainsProtectionEnabled;
    }
    private void setChatDomainsProtectionActions(List<String> chatDomainsProtectionActions) {
        this.chatDomainsProtectionActions.clear();
        this.chatDomainsProtectionActions.addAll(chatDomainsProtectionActions);
    }
    public List<String> getChatDomainsProtectionActions() {
        return chatDomainsProtectionActions;
    }


    public boolean isNotifyAboutBlockedWord() {
        return notifyAboutBlockedWord;
    }
    public boolean isCheckBooksForBlockedWords() {
        return checkBooksForBlockedWords;
    }
    private void setCheckBooksForBlockedWords(boolean checkBooksForBlockedWords) {
        this.checkBooksForBlockedWords = checkBooksForBlockedWords;
    }
    public boolean isCheckSignsForBlockedWords() {
        return checkSignsForBlockedWords;
    }
    private void setCheckSignsForBlockedWords(boolean checkSignsForBlockedWords) {
        this.checkSignsForBlockedWords = checkSignsForBlockedWords;
    }
    public boolean isCheckItemsForBlockedWords() {
        return checkItemsForBlockedWords;
    }
    private void setCheckItemsForBlockedWords(boolean checkItemsForBlockedWords) {
        this.checkItemsForBlockedWords = checkItemsForBlockedWords;
    }
    public boolean isCheckCommandsForBlockedWords() {
        return checkCommandsForBlockedWords;
    }
    public void setCheckCommandsForBlockedWords(boolean checkCommandsForBlockedWords) {
        this.checkCommandsForBlockedWords = checkCommandsForBlockedWords;
    }
    private void setNotifyAboutBlockedWord(boolean notifyAboutBlockedWord) {
        this.notifyAboutBlockedWord = notifyAboutBlockedWord;
    }
    private void setDisabledWorldsForBlockedWords(List<String> disabledWorldsForBlockedWords) {
        this.disabledWorldsForBlockedWords.clear();
        this.disabledWorldsForBlockedWords.addAll(disabledWorldsForBlockedWords);
    }
    public List<String> getDisabledWorldsForBlockedWords() {
        return disabledWorldsForBlockedWords;
    }
    private void setPunishForBlockedWords(List<String> punishForBlockedWords) {
        this.punishForBlockedWords.clear();
        this.punishForBlockedWords.addAll(punishForBlockedWords);
    }
    public List<String> getPunishForBlockedWords() {
        return punishForBlockedWords;
    }
    public List<String> getBlockedWords() {
        return blockedWords;
    }
    private void setBlockedWords(List<String> blockedWords) {
        this.blockedWords.clear();
        this.blockedWords.addAll(blockedWords);
    }
    public boolean isBlockedWordsEnabled() {
        return blockedWordsEnabled;
    }
    public boolean isPunishEnabledForBlockedWords() {
        return punishEnabledForBlockedWords;
    }
    private void setBlockedWordsEnabled(boolean blockedWordsEnabled) {
        this.blockedWordsEnabled = blockedWordsEnabled;
    }
    private void setPunishEnabledForBlockedWords(boolean punishEnabledForBlockedWords) {
        this.punishEnabledForBlockedWords = punishEnabledForBlockedWords;
    }
    public boolean isBlockRepeatingMessages() {
        return blockRepeatingMessages;
    }
    private void setBlockRepeatingMessages(boolean blockRepeatingMessages) {
        this.blockRepeatingMessages = blockRepeatingMessages;
    }
    private void setTickDelaysIfOffline(boolean tickDelaysIfOffline) {
        this.tickDelaysIfOffline = tickDelaysIfOffline;
    }
    public boolean isTickDelaysIfOffline() {
        return tickDelaysIfOffline;
    }
    public boolean isUseGroups() {
        return useGroups;
    }
    private void setUseGroups(boolean useGroups) {
        this.useGroups = useGroups;
    }
    private void setFunctionEnabled(boolean functionEnabled) {
        this.functionEnabled = functionEnabled;
    }
    public boolean isFunctionEnabled() {
        return functionEnabled;
    }
    public boolean isMessagesReplacerEnabled() {
        return messagesReplacerEnabled;
    }
    private void setMessagesReplacerEnabled(boolean messagesReplacerEnabled) {
        this.messagesReplacerEnabled = messagesReplacerEnabled;
    }
    public HashMap<String, String> getMessageReplacer() {
        return messageReplacer;
    }
    private void setMessageReplacer(List<String> messagesList) {
        this.messageReplacer.clear();
        for(String r : messagesList) {
            if(!r.contains(" -> ")) {
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'chat-settings.yml' file! Invalid format in 'messages-replacer', the number of the sheet with the incorrect format %number%".replace("%number%", String.valueOf(messagesList.indexOf(r) + 1))));
                continue;
            }
            String[] rArgs = r.split(" -> ");
            this.messageReplacer.put(rArgs[0], rArgs[1]);
        }
    }
    public HashMap<String, Integer> getChatDelays() {
        return chatDelays;
    }

    public void addChatDelay(String group, int delay) {
        this.chatDelays.put(group, delay);
    }

    public void loadChatSettings() {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
            setFunctionEnabled(getFileAccessor().getChatConfig().getBoolean("settings.enabled"));
            if(isFunctionEnabled()) {
                //Advertise protection settings
                setNotifyAdminAboutAdvertise(getFileAccessor().getChatConfig().getBoolean("advertise-protection.notify-admins"));
                setChatIpProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.chat.ip-protection.enabled"));
                if(isChatIpProtectionEnabled())
                    setChatIpProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.chat.ip-protection.actions"));
                setChatDomainsProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.chat.domains-protection.enabled"));
                if(isChatDomainsProtectionEnabled())
                    setChatDomainsProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.chat.domains-protection.actions"));
                setSignsIpProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.signs.ip-protection.enabled"));
                if(isSignsIpProtectionEnabled())
                    setSignsIpProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.signs.ip-protection.actions"));
                setSignsDomainsProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.signs.domains-protection.enabled"));
                if(isSignsDomainsProtectionEnabled())
                    setSignsDomainsProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.signs.domains-protection.actions"));
                setBookIpProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.books.ip-protection.enabled"));
                if(isBookIpProtectionEnabled())
                    setBookIpProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.books.ip-protection.actions"));
                setBookDomainsProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.books.domains-protection.enabled"));
                if(isBookDomainsProtectionEnabled())
                    setBookDomainsProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.books.domains-protection.actions"));
                setItemsIpProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.items.ip-protection.enabled"));
                if(isItemsIpProtectionEnabled())
                    setItemsIpProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.items.ip-protection.actions"));
                setItemsDomainsProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.items.domains-protection.enabled"));
                if(isItemsDomainsProtectionEnabled())
                    setItemsDomainsProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.items.domains-protection.actions"));
                setCommandsIpProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.commands.ip-protection.enabled"));
                if(isCommandsIpProtectionEnabled())
                    setCommandsIpProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.commands.ip-protection.actions"));
                setCommandsDomainsProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.commands.domains-protection.enabled"));
                if(isCommandsDomainsProtectionEnabled())
                    setCommandsDomainsProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.commands.domains-protection.actions"));
                //Advertise protection settings
                setCheckCommandsForBlockedWords(getFileAccessor().getChatConfig().getBoolean("chat.blocked-words.check-commands"));
                setCheckBooksForBlockedWords(getFileAccessor().getChatConfig().getBoolean("chat.blocked-words.check-books"));
                setCheckItemsForBlockedWords(getFileAccessor().getChatConfig().getBoolean("chat.blocked-words.check-items"));
                setCheckSignsForBlockedWords(getFileAccessor().getChatConfig().getBoolean("chat.blocked-words.check-signs"));
                setTickDelaysIfOffline(getFileAccessor().getChatConfig().getBoolean("chat.chat-delay.tick-if-offline"));
                setUseGroups(getFileAccessor().getChatConfig().getBoolean("settings.use-groups"));
                setMessagesReplacerEnabled(getFileAccessor().getChatConfig().getBoolean("chat.messages-replacer.enabled"));
                setBlockRepeatingMessages(getFileAccessor().getChatConfig().getBoolean("chat.block-repeating-messages"));
                if(isMessagesReplacerEnabled()) {
                    setMessageReplacer(getFileAccessor().getChatConfig().getStringList("chat.messages-replacer.replaces"));
                }
                this.chatDelays.clear();
                try {
                    int a = 0;
                    a = Integer.parseInt(getFileAccessor().getChatConfig().getString("chat.chat-delay.standard"));
                    if(a > 0) addChatDelay("global_delay", getFileAccessor().getChatConfig().getInt("chat.chat-delay.standard"));
                } catch (NumberFormatException ignored) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'chat-settings.yml' file. The 'chat -> chat-delay' parameter can only be an integer"));
                }
                if(isUseGroups()) {
                    for(String groupName : getFileAccessor().getChatConfig().getConfigurationSection("chat.chat-delay.per-group").getKeys(false)) {
                        try {
                            int a = 0;
                            a = Integer.parseInt(getFileAccessor().getChatConfig().getString("chat.chat-delay.per-group." + groupName));
                            if(a > 0) addChatDelay(groupName, getFileAccessor().getChatConfig().getInt("chat.chat-delay.per-group." + groupName));
                        } catch (NumberFormatException ignored) {
                            Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'chat-settings.yml' file. The 'chat -> chat-delay -> per-group -> " + groupName + "' parameter can only be an integer"));
                        }
                    }
                }
                setBlockedWordsEnabled(getFileAccessor().getChatConfig().getBoolean("chat.blocked-words.enabled"));
                if(isBlockedWordsEnabled()) {
                    setBlockedWords(getFileAccessor().getChatConfig().getStringList("chat.blocked-words.words"));
                    setPunishEnabledForBlockedWords(getFileAccessor().getChatConfig().getBoolean("chat.blocked-words.punishments.enabled"));
                    setDisabledWorldsForBlockedWords(getFileAccessor().getChatConfig().getStringList("chat.blocked-words.disabled-worlds"));
                    setNotifyAboutBlockedWord(getFileAccessor().getChatConfig().getBoolean("chat.blocked-words.notify-admins"));
                    if(isPunishEnabledForBlockedWords()) {
                        setPunishForBlockedWords(getFileAccessor().getChatConfig().getStringList("chat.blocked-words.punishments.commands"));
                    }
                }
            }

        });
    }

    public void reloadChatSettings() {
        if(isFunctionEnabled()) {
            //Advertise protection settings
            setNotifyAdminAboutAdvertise(getFileAccessor().getChatConfig().getBoolean("advertise-protection.notify-admins"));
            setChatIpProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.chat.ip-protection.enabled"));
            if(isChatIpProtectionEnabled())
                setChatIpProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.chat.ip-protection.actions"));
            setChatDomainsProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.chat.domains-protection.enabled"));
            if(isChatDomainsProtectionEnabled())
                setChatDomainsProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.chat.domains-protection.actions"));
            setSignsIpProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.signs.ip-protection.enabled"));
            if(isSignsIpProtectionEnabled())
                setSignsIpProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.signs.ip-protection.actions"));
            setSignsDomainsProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.signs.domains-protection.enabled"));
            if(isSignsDomainsProtectionEnabled())
                setSignsDomainsProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.signs.domains-protection.actions"));
            setBookIpProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.books.ip-protection.enabled"));
            if(isBookIpProtectionEnabled())
                setBookIpProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.books.ip-protection.actions"));
            setBookDomainsProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.books.domains-protection.enabled"));
            if(isBookDomainsProtectionEnabled())
                setBookDomainsProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.books.domains-protection.actions"));
            setItemsIpProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.items.ip-protection.enabled"));
            if(isItemsIpProtectionEnabled())
                setItemsIpProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.items.ip-protection.actions"));
            setItemsDomainsProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.items.domains-protection.enabled"));
            if(isItemsDomainsProtectionEnabled())
                setItemsDomainsProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.items.domains-protection.actions"));
            setCommandsIpProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.commands.ip-protection.enabled"));
            if(isCommandsIpProtectionEnabled())
                setCommandsIpProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.commands.ip-protection.actions"));
            setCommandsDomainsProtectionEnabled(getFileAccessor().getChatConfig().getBoolean("advertise-protection.commands.domains-protection.enabled"));
            if(isCommandsDomainsProtectionEnabled())
                setCommandsDomainsProtectionActions(getFileAccessor().getChatConfig().getStringList("advertise-protection.commands.domains-protection.actions"));
            //Advertise protection settings
            setCheckCommandsForBlockedWords(getFileAccessor().getChatConfig().getBoolean("chat.blocked-words.check-commands"));
            setCheckBooksForBlockedWords(getFileAccessor().getChatConfig().getBoolean("chat.blocked-words.check-books"));
            setCheckItemsForBlockedWords(getFileAccessor().getChatConfig().getBoolean("chat.blocked-words.check-items"));
            setCheckSignsForBlockedWords(getFileAccessor().getChatConfig().getBoolean("chat.blocked-words.check-signs"));
            setTickDelaysIfOffline(getFileAccessor().getChatConfig().getBoolean("chat.chat-delay.tick-if-offline"));
            setUseGroups(getFileAccessor().getChatConfig().getBoolean("settings.use-groups"));
            setMessagesReplacerEnabled(getFileAccessor().getChatConfig().getBoolean("chat.messages-replacer.enabled"));
            setBlockRepeatingMessages(getFileAccessor().getChatConfig().getBoolean("chat.block-repeating-messages"));
            if(isMessagesReplacerEnabled()) {
                setMessageReplacer(getFileAccessor().getChatConfig().getStringList("chat.messages-replacer.replaces"));
            }
            this.chatDelays.clear();
            try {
                int a = 0;
                a = Integer.parseInt(getFileAccessor().getChatConfig().getString("chat.chat-delay.standard"));
                if(a > 0) addChatDelay("global_delay", getFileAccessor().getChatConfig().getInt("chat.chat-delay.standard"));
            } catch (NumberFormatException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'chat-settings.yml' file. The 'chat -> chat-delay' parameter can only be an integer"));
            }
            if(isUseGroups()) {
                for(String groupName : getFileAccessor().getChatConfig().getConfigurationSection("chat.chat-delay.per-group").getKeys(false)) {
                    try {
                        int a = 0;
                        a = Integer.parseInt(getFileAccessor().getChatConfig().getString("chat.chat-delay.per-group." + groupName));
                        if(a > 0) addChatDelay(groupName, getFileAccessor().getChatConfig().getInt("chat.chat-delay.per-group." + groupName));
                    } catch (NumberFormatException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in the 'chat-settings.yml' file. The 'chat -> chat-delay -> per-group -> " + groupName + "' parameter can only be an integer"));
                    }
                }
            }
            setBlockedWordsEnabled(getFileAccessor().getChatConfig().getBoolean("chat.blocked-words.enabled"));
            if(isBlockedWordsEnabled()) {
                setBlockedWords(getFileAccessor().getChatConfig().getStringList("chat.blocked-words.words"));
                setPunishEnabledForBlockedWords(getFileAccessor().getChatConfig().getBoolean("chat.blocked-words.punishments.enabled"));
                setDisabledWorldsForBlockedWords(getFileAccessor().getChatConfig().getStringList("chat.blocked-words.disabled-worlds"));
                setNotifyAboutBlockedWord(getFileAccessor().getChatConfig().getBoolean("chat.blocked-words.notify-admins"));
                if(isPunishEnabledForBlockedWords()) {
                    setPunishForBlockedWords(getFileAccessor().getChatConfig().getStringList("chat.blocked-words.punishments.commands"));
                }
            }
        }
    }

}
