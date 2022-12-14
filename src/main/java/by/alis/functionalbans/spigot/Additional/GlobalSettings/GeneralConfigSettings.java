package by.alis.functionalbans.spigot.Additional.GlobalSettings;

import by.alis.functionalbans.spigot.Additional.Containers.StaticContainers;
import by.alis.functionalbans.API.Enums.StorageType;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.ConsoleLanguages.LangEnglish;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.ConsoleLanguages.LangRussian;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.CooldownsManager;
import by.alis.functionalbans.spigot.Managers.Files.FileAccessor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.util.*;

import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class GeneralConfigSettings {



    private String consoleLanguageMode = "en_US";
    private String globalLanguage = "ru_RU";
    private boolean isAnnounceWhenLogHided = true;
    private boolean isAllowedUseRamAsContainer = false;
    private boolean unsafeActionsConfirmation = true;
    private boolean isBanAllowedWithoutReason = true;
    private boolean isKickAllowedWithoutReason = true;
    private boolean showDescription = true;
    private boolean purgeConfirmation = true;
    private boolean isMuteAllowedWithoutReason = true;
    private boolean isLessInformation = false;
    private boolean hideMainCommand = false;
    private boolean showExamples = true;
    private Set<String> timeRestrictionGroups = new HashSet<>();
    private boolean isApiEnabled = true;
    private boolean isApiProtectedByPassword = false;
    private boolean isProhibitYourselfInteraction = false;
    private boolean isConsoleNotification = true;
    private boolean isPlayersNotification = true;
    private boolean isCooldownsEnabled = false;
    private boolean isSaveCooldowns = false;
    private StorageType storageType = StorageType.SQLITE;
    private boolean isAllowedUnbanWithoutReason = true;
    private String defaultUnbanReason = "The Ban time has expired";



    private boolean isNicksControlEnabled = false;
    private String nicknameCheckMode = "contains";
    private List<String> blockedNickNames = new ArrayList<>();
    private boolean notifyConsoleWhenNickNameBlocked = false;


    private boolean isIpsControlEnabled = false;
    private List<String> blockedIps = new ArrayList<>();
    private boolean notifyConsoleWhenIPBlocked = false;


    public boolean isIpsControlEnabled() {
        return isIpsControlEnabled;
    }
    private void setIpsControlEnabled(boolean ipsControlEnabled) {
        isIpsControlEnabled = ipsControlEnabled;
    }
    public List<String> getBlockedIps() {
        return this.blockedIps;
    }
    private void setBlockedIps(List<String> blockedIps) {
        this.blockedIps.clear();
        this.blockedIps.addAll(blockedIps);
    }
    private void setNotifyConsoleWhenIPBlocked(boolean notifyConsoleWhenIPBlocked) {
        this.notifyConsoleWhenIPBlocked = notifyConsoleWhenIPBlocked;
    }
    public boolean notifyConsoleWhenIPBlocked() {
        return this.notifyConsoleWhenIPBlocked;
    }

    public boolean isUnsafeActionsConfirmation() { return this.unsafeActionsConfirmation; }
    public boolean showDescription() { return this.showDescription; }
    private void setShowDescription(boolean showDescription) { this.showDescription = showDescription; }
    private void setUnsafeActionsConfirmation(boolean unsafeActionsConfirmation) { unsafeActionsConfirmation = unsafeActionsConfirmation; }
    private void setApiEnabled(boolean apiEnabled) { this.isApiEnabled = apiEnabled; }
    public boolean isApiEnabled() { return this.isApiEnabled; }
    public boolean isPurgeConfirmation() { return this.purgeConfirmation; }
    private void setPurgeConfirmation(boolean purgeConfirmation) { this.purgeConfirmation = purgeConfirmation; }
    public boolean isApiProtectedByPassword() { return this.isApiProtectedByPassword; }
    private void setApiProtectedByPassword(boolean apiProtectedByPassword) { isApiProtectedByPassword = apiProtectedByPassword; }
    public boolean isBanAllowedWithoutReason() { return isBanAllowedWithoutReason; }
    public void setBanAllowedWithoutReason(boolean banAllowedWithoutReason) { isBanAllowedWithoutReason = banAllowedWithoutReason; }
    public boolean isKickAllowedWithoutReason() { return isKickAllowedWithoutReason; }
    public void setKickAllowedWithoutReason(boolean kickAllowedWithoutReason) { isKickAllowedWithoutReason = kickAllowedWithoutReason; }
    public Set<String> getPossibleGroups() { return timeRestrictionGroups; }
    public void setPossibleGroups(Set<String> timeRestrictionGroups) {
        timeRestrictionGroups.clear();
        timeRestrictionGroups = timeRestrictionGroups;
    }
    public boolean isMuteAllowedWithoutReason() { return isMuteAllowedWithoutReason; }
    public void setMuteAllowedWithoutReason(boolean muteAllowedWithoutReason) { isMuteAllowedWithoutReason = muteAllowedWithoutReason; }
    public boolean isAllowedUseRamAsContainer() { return isAllowedUseRamAsContainer; }
    private void setAllowedUseRamAsContainer(boolean allowedUseRamAsContainer) { isAllowedUseRamAsContainer = allowedUseRamAsContainer; }
    public boolean showExamples() { return showExamples; }
    public void setShowExamples(boolean showExamples) { showExamples = showExamples; }
    public StorageType getStorageType() { return storageType; }
    private void setStorageType(StorageType storageType) { storageType = storageType; }
    public String getConsoleLanguageMode() { return consoleLanguageMode; }
    private void setConsoleLanguageMode(String mode) { consoleLanguageMode = mode; }
    public boolean isAnnounceWhenLogHided() { return isAnnounceWhenLogHided; }
    private void setAnnounceWhenLogHided(boolean status) { isAnnounceWhenLogHided = status; }
    public boolean hideMainCommand() { return hideMainCommand; }
    private void setHideMainCommand(boolean hideMainCommand) { hideMainCommand = hideMainCommand; }
    public boolean isLessInformation() { return isLessInformation; }
    private void setLessInformation(boolean lessInformation) { isLessInformation = lessInformation; }
    public boolean isProhibitYourselfInteraction() { return isProhibitYourselfInteraction; }
    private void setProhibitYourselfInteraction(boolean prohibitYourselfInteraction) { isProhibitYourselfInteraction = prohibitYourselfInteraction; }
    public boolean isPlayersNotification() { return isPlayersNotification; }
    public boolean isConsoleNotification() { return isConsoleNotification; }
    private void setPlayersNotification(boolean playersNotification) { isPlayersNotification = playersNotification; }
    private void setConsoleNotification(boolean consoleNotification) { isConsoleNotification = consoleNotification; }
    private void setCooldownsEnabled(boolean cooldownsEnabled) { isCooldownsEnabled = cooldownsEnabled; }
    public boolean isCooldownsEnabled() { return isCooldownsEnabled; }
    public boolean isSaveCooldowns() { return isSaveCooldowns; }
    private void setSaveCooldowns(boolean saveCooldowns) { isSaveCooldowns = saveCooldowns; }
    public String getGlobalLanguage() { return globalLanguage; }
    public void setGlobalLanguage(String globalLanguage) { globalLanguage = globalLanguage; }
    public boolean isAllowedUnbanWithoutReason() { return isAllowedUnbanWithoutReason; }
    private void setAllowedUnbanWithoutReason(boolean allowedUnbanWithoutReason) { isAllowedUnbanWithoutReason = allowedUnbanWithoutReason; }
    public String getDefaultUnbanReason() { return defaultUnbanReason; }


    public boolean isNicksControlEnabled() {
        return this.isNicksControlEnabled;
    }
    private void setNicknameCheckMode(String nicknameCheckMode) {
        this.nicknameCheckMode = nicknameCheckMode;
    }
    public boolean notifyConsoleWhenNickNameBlocked() {
        return notifyConsoleWhenNickNameBlocked;
    }
    public String getNicknameCheckMode() {
        return nicknameCheckMode;
    }
    public void setBlockedNickNames(List<String> blockedNickName) {
        this.blockedNickNames.clear();
        this.blockedNickNames.addAll(blockedNickName);
    }
    public void setNotifyConsoleWhenNickNameBlocked(boolean notifyConsoleWhenNickNameBlocked) {
        this.notifyConsoleWhenNickNameBlocked = notifyConsoleWhenNickNameBlocked;
    }
    private void setNicksControlEnabled(boolean nicksControlEnabled) {
        isNicksControlEnabled = nicksControlEnabled;
    }
    public List<String> getBlockedNickNames() {
        return this.blockedNickNames;
    }

    public void loadConfigSettings() {
        FileAccessor fileAccessor = new FileAccessor();
        setConsoleLanguageMode(fileAccessor.getGeneralConfig().getString("plugin-settings.console-language"));
        if(!isLessInformation()) {
            if (fileAccessor.getGeneralConfig().getString("plugin-settings.console-language").equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalBans] Установлен язык для консоли: ru_RU (Русский) ✔"));
            }
            if (fileAccessor.getGeneralConfig().getString("plugin-settings.console-language").equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalBans] The language for the console is set: en_US (English) ✔"));
            }
        }
        if(!fileAccessor.getGeneralConfig().getString("plugin-settings.console-language").equalsIgnoreCase("en_US") && !fileAccessor.getGeneralConfig().getString("plugin-settings.console-language").equalsIgnoreCase("ru_RU")) {
            setConsoleLanguageMode("en_US");
            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_UNKNOWN_LANGUAGE));
        }


        switch (Objects.requireNonNull(fileAccessor.getGeneralConfig().getString("plugin-settings.storage-method"))) {
            case "sqlite": {
                setStorageType(StorageType.SQLITE);
                if(!isLessInformation()) {
                    switch (getConsoleLanguageMode()) {
                        case "ru_RU":
                            Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                            break;
                        case "en_US":
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                            break;
                        default:
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                            break;
                    }
                }
                break;
            }
            case "mysql": {
                setStorageType(StorageType.MYSQL);
                if(!isLessInformation()) {
                    switch (getConsoleLanguageMode()) {
                        case "ru_RU":
                            Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                            break;
                        case "en_US":
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                            break;
                        default:
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                            break;
                    }
                }
                break;
            }
            case "h2": {
                setStorageType(StorageType.H2);
                if(!isLessInformation()) {
                    switch (getConsoleLanguageMode()) {
                        case "ru_RU":
                            Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                            break;
                        case "en_US":
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                            break;
                        default:
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                            break;
                    }
                }
                break;
            }
            default: {
                setStorageType(StorageType.SQLITE);
                switch (getConsoleLanguageMode()) {
                    case "ru_RU":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONFIG_STORAGE_METHOD_UNKNOWN.replace("%unknown_method%", fileAccessor.getGeneralConfig().getString("plugin-settings.storage-method"))));
                        break;
                    case "en_US":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_STORAGE_METHOD_UNKNOWN.replace("%unknown_method%", fileAccessor.getGeneralConfig().getString("plugin-settings.storage-method"))));
                        break;
                    default:
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_STORAGE_METHOD_UNKNOWN.replace("%unknown_method%", fileAccessor.getGeneralConfig().getString("plugin-settings.storage-method"))));
                        break;
                }
                break;
            }
        }

        setGlobalLanguage(fileAccessor.getGeneralConfig().getString("plugin-settings.global-language"));

        StaticContainers.getHidedMessagesContainer().loadHidedMessages();
        StaticContainers.getReplacedMessagesContainer().loadReplacedMessages();

        setCooldownsEnabled(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.cooldowns.enabled"));
        setSaveCooldowns(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.cooldowns.save-cooldowns"));
        CooldownsManager.setupCooldowns();

        setNicksControlEnabled(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.join-settings.nicks-control.enabled"));
        setNotifyConsoleWhenNickNameBlocked(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.join-settings.nicks-control.notify-console"));

        setIpsControlEnabled(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.join-settings.ips-control.enabled"));
        if(isIpsControlEnabled()) {
            setNotifyConsoleWhenIPBlocked(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.join-settings.ips-control.notify-console"));
            setBlockedIps(Arrays.asList(StringUtils.substringBetween(fileAccessor.getGeneralConfig().getString("plugin-settings.join-settings.ips-control.blocked-ips"), "[", "]").split(", ")));
        }

        if(isNicksControlEnabled()) {
            switch (fileAccessor.getGeneralConfig().getString("plugin-settings.join-settings.nicks-control.check-mode")) {
                case "equals": {
                    setNicknameCheckMode("equals");
                    break;
                }
                case "contains": {
                    setNicknameCheckMode("contains");
                    break;
                }
                default: {
                    setNicknameCheckMode("contains");
                    switch (getConsoleLanguageMode()) {
                        case "ru_RU": {
                            Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONFIG_NICKNAME_UNKNOWN_CHECK_MODE));
                            break;
                        }
                        case "en_US": {
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_NICKNAME_UNKNOWN_CHECK_MODE));
                            break;
                        }
                        default: {
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_NICKNAME_UNKNOWN_CHECK_MODE));
                            break;
                        }
                    }
                    break;
                }
            }

            setBlockedNickNames(Arrays.asList(StringUtils.substringBetween(fileAccessor.getGeneralConfig().getString("plugin-settings.join-settings.nicks-control.blocked-nicks"), "[", "]").split(", ")));

        }

        setLessInformation(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.less-information"));
        setProhibitYourselfInteraction(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.prohibit-interaction-to-yourself"));
        setBanAllowedWithoutReason(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.reason-settings.bans-without-reason.allowed"));
        setKickAllowedWithoutReason(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.reason-settings.kick-without-reason.allowed"));
        setMuteAllowedWithoutReason(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.reason-settings.mute-without-reason.allowed"));
        setAllowedUnbanWithoutReason(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.reason-settings.unban-with-out-reason.allowed"));
        setUnsafeActionsConfirmation(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.unsafe-actions-confirmation"));
        setPurgeConfirmation(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.purge-confirmation"));
        setShowExamples(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.show-examples"));
        setShowDescription(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.show-description"));
        setHideMainCommand(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.hide-main-command"));
        setConsoleNotification(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.notifications.console"));
        setPlayersNotification(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.notifications.players"));
        timeRestrictionGroups.clear();
        setPossibleGroups(fileAccessor.getGeneralConfig().getConfigurationSection("plugin-settings.time-settings.per-groups").getKeys(false));

        setAllowedUseRamAsContainer(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.allow-use-ram"));
        if(isAllowedUseRamAsContainer()) {
            if(isLessInformation()) {
                switch (getConsoleLanguageMode()) {
                    case "ru_RU":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONFIG_USE_RAM_ALLOWED));
                        break;
                    case "en_US":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_USE_RAM_ALLOWED));
                        break;
                    default:
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_USE_RAM_ALLOWED));
                        break;
                }
            }
        } else {
            if (isLessInformation()) {
                switch (getConsoleLanguageMode()) {
                    case "ru_RU":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONFIG_USE_RAM_DISALLOWED));
                        break;
                    case "en_US":
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_USE_RAM_DISALLOWED));
                        break;
                    default:
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_USE_RAM_DISALLOWED));
                        break;
                }
            }
        }

        setAnnounceWhenLogHided(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.console-logger.announce-console-when-message-hidden"));

        setApiEnabled(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.api.spigot.enabled"));
        if(isLessInformation()) {
            switch (getConsoleLanguageMode()) {
                case "ru_RU": {
                    Bukkit.getConsoleSender().sendMessage(setColors(isApiEnabled() ? LangRussian.CONFIG_API_ENABLED : LangRussian.CONFIG_API_DISABLED));
                    break;
                }
                case "en_US": {
                    Bukkit.getConsoleSender().sendMessage(setColors(isApiEnabled() ? LangEnglish.CONFIG_API_ENABLED : LangEnglish.CONFIG_API_DISABLED));
                    break;
                }
                default: {
                    Bukkit.getConsoleSender().sendMessage(setColors(isApiEnabled() ? LangEnglish.CONFIG_API_ENABLED : LangEnglish.CONFIG_API_DISABLED));
                    break;
                }
            }
        }

        setApiProtectedByPassword(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.api.spigot.password.enabled"));
        if(isApiEnabled()) {
            if (isLessInformation()) {
                switch (getConsoleLanguageMode()) {
                    case "ru_RU": {
                        Bukkit.getConsoleSender().sendMessage(setColors(isApiProtectedByPassword() ? LangRussian.CONFIG_API_NOT_PROTECTED : LangRussian.CONFIG_API_PROTECTED));
                        break;
                    }
                    case "en_US": {
                        Bukkit.getConsoleSender().sendMessage(setColors(isApiProtectedByPassword() ? LangEnglish.CONFIG_API_NOT_PROTECTED : LangEnglish.CONFIG_API_PROTECTED));
                        break;
                    }
                    default: {
                        Bukkit.getConsoleSender().sendMessage(setColors(isApiProtectedByPassword() ? LangEnglish.CONFIG_API_NOT_PROTECTED : LangEnglish.CONFIG_API_PROTECTED));
                        break;
                    }
                }
            }
        }
        return;
    }

    public void reloadConfig() {
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
            FileAccessor fileAccessor = new FileAccessor();
            consoleLanguageMode = fileAccessor.getGeneralConfig().getString("plugin-settings.console-language");
            if (fileAccessor.getGeneralConfig().getString("plugin-settings.console-language").equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalBans] Установлен язык для консоли: ru_RU (Русский) ✔"));
            }
            if (fileAccessor.getGeneralConfig().getString("plugin-settings.console-language").equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalBans] The language for the console is set: en_US (English) ✔"));
            }
            if (!fileAccessor.getGeneralConfig().getString("plugin-settings.console-language").equalsIgnoreCase("en_US") && !fileAccessor.getGeneralConfig().getString("plugin-settings.console-language").equalsIgnoreCase("ru_RU")) {
                consoleLanguageMode = "en_US";
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_UNKNOWN_LANGUAGE));
            }
            switch (fileAccessor.getGeneralConfig().getString("plugin-settings.storage-method")) {
                case "sqlite": {
                    storageType = StorageType.SQLITE;
                    if (!isLessInformation()) {
                        switch (getConsoleLanguageMode()) {
                            case "ru_RU":
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                                break;
                            case "en_US":
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                                break;
                            default:
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                                break;
                        }
                    }
                    break;
                }
                case "mysql": {
                    storageType = StorageType.MYSQL;
                    if (!isLessInformation()) {
                        switch (getConsoleLanguageMode()) {
                            case "ru_RU":
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                                break;
                            case "en_US":
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                                break;
                            default:
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                                break;
                        }
                    }
                    break;
                }
                case "h2": {
                    storageType = StorageType.H2;
                    if (!isLessInformation()) {
                        switch (getConsoleLanguageMode()) {
                            case "ru_RU":
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                                break;
                            case "en_US":
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                                break;
                            default:
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_STORAGE_METHOD_LOADED.replace("%storage_method%", String.valueOf(getStorageType()))));
                                break;
                        }
                    }
                    break;
                }
                default: {
                    storageType = StorageType.SQLITE;
                    switch (getConsoleLanguageMode()) {
                        case "ru_RU":
                            Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONFIG_STORAGE_METHOD_UNKNOWN.replace("%unknown_method%", fileAccessor.getGeneralConfig().getString("plugin-settings.storage-method"))));
                        case "en_US":
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_STORAGE_METHOD_UNKNOWN.replace("%unknown_method%", fileAccessor.getGeneralConfig().getString("plugin-settings.storage-method"))));
                            break;
                        default:
                            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_STORAGE_METHOD_UNKNOWN.replace("%unknown_method%", fileAccessor.getGeneralConfig().getString("plugin-settings.storage-method"))));
                            break;
                    }
                    break;
                }
            }
            setGlobalLanguage(fileAccessor.getGeneralConfig().getString("plugin-settings.global-language"));
            setLessInformation(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.less-information"));
            setConsoleNotification(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.notifications.console"));
            setPlayersNotification(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.notifications.players"));
            setProhibitYourselfInteraction(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.prohibit-interaction-to-yourself"));
            StaticContainers.getReplacedMessagesContainer().reloadReplacedMessages();
            StaticContainers.getHidedMessagesContainer().reloadHidedMessages();

            setCooldownsEnabled(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.cooldowns.enabled"));
            setSaveCooldowns(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.cooldowns.save-cooldowns"));
            CooldownsManager.setupCooldowns();

            setNicksControlEnabled(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.join-settings.nicks-control.enabled"));
            setNotifyConsoleWhenNickNameBlocked(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.join-settings.nicks-control.notify-console"));

            setIpsControlEnabled(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.join-settings.ips-control.enabled"));
            if(isIpsControlEnabled()) {
                setNotifyConsoleWhenIPBlocked(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.join-settings.ips-control.notify-console"));
                setBlockedIps(Arrays.asList(StringUtils.substringBetween(fileAccessor.getGeneralConfig().getString("plugin-settings.join-settings.ips-control.blocked-ips"), "[", "]").split(", ")));
            }

            if(isNicksControlEnabled()) {
                switch (fileAccessor.getGeneralConfig().getString("plugin-settings.join-settings.nicks-control.check-mode")) {
                    case "equals": {
                        setNicknameCheckMode("equals");
                        break;
                    }
                    case "contains": {
                        setNicknameCheckMode("contains");
                        break;
                    }
                    default: {
                        setNicknameCheckMode("contains");
                        switch (getConsoleLanguageMode()) {
                            case "ru_RU": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.CONFIG_NICKNAME_UNKNOWN_CHECK_MODE));
                                break;
                            }
                            case "en_US": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_NICKNAME_UNKNOWN_CHECK_MODE));
                                break;
                            }
                            default: {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.CONFIG_NICKNAME_UNKNOWN_CHECK_MODE));
                                break;
                            }
                        }
                        break;
                    }
                }
                setBlockedNickNames(Arrays.asList(StringUtils.substringBetween(fileAccessor.getGeneralConfig().getString("plugin-settings.join-settings.nicks-control.blocked-nicks"), "[", "]").split(", ")));

            }

            setBanAllowedWithoutReason(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.reason-settings.bans-without-reason.allowed"));
            setKickAllowedWithoutReason(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.reason-settings.kick-without-reason.allowed"));
            setMuteAllowedWithoutReason(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.reason-settings.mute-without-reason.allowed"));
            setAllowedUnbanWithoutReason(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.reason-settings.unban-with-out-reason.allowed"));
            setUnsafeActionsConfirmation(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.unsafe-actions-confirmation"));
            setPurgeConfirmation(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.purge-confirmation"));
            setShowExamples(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.show-examples"));
            setShowDescription(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.show-description"));
            setHideMainCommand(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.hide-main-command"));
            getPossibleGroups().clear();
            setPossibleGroups(fileAccessor.getGeneralConfig().getConfigurationSection("plugin-settings.time-settings.per-groups").getKeys(false));
            setAnnounceWhenLogHided(fileAccessor.getGeneralConfig().getBoolean("plugin-settings.console-logger.announce-console-when-message-hidden"));

        });
    }

}
