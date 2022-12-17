package by.alis.functionalbans.spigot.Additional.GlobalSettings;

import by.alis.functionalbans.spigot.Additional.Containers.StaticContainers;
import by.alis.functionalbans.API.Enums.StorageType;
import by.alis.functionalbans.spigot.Additional.Other.OtherUtils;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.CooldownsManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.util.*;

import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalbans.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class GeneralConfigSettings {


    private boolean isOldServerVersion = false;
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
    private String banTimeExpired = "The Ban time has expired";
    private boolean isAutoPurgerEnabled = false;
    private int autoPurgerDelay = 600;

    private boolean isLoggerEnabled = false;
    private String logFormat = "[FunctionalBans <-> %1$f] %2$f";
    private List<String> messagesToLog = new ArrayList<>();

    private boolean isNicksControlEnabled = false;
    private String nicknameCheckMode = "contains";
    private List<String> blockedNickNames = new ArrayList<>();
    private boolean notifyConsoleWhenNickNameBlocked = false;


    private boolean isIpsControlEnabled = false;
    private List<String> blockedIps = new ArrayList<>();
    private boolean notifyConsoleWhenIPBlocked = false;

    private boolean dupeIdModeEnabled = false;
    private int maxIpsPerSession = 1;
    private String dupeIpCheckMode = "timer";
    private String dupeIpAction = null;
    private int dupeIpTimerDelay = 30;

    public boolean isDupeIdModeEnabled() {
        return this.dupeIdModeEnabled;
    }
    private void setDupeIdModeEnabled(boolean dupeIdModeEnabled) {
        this.dupeIdModeEnabled = dupeIdModeEnabled;
    }
    public int getMaxIpsPerSession() {
        return this.maxIpsPerSession;
    }
    private void setMaxIpsPerSession(int maxIpsPerSession) {
        this.maxIpsPerSession = maxIpsPerSession;
    }
    public String getDupeIpCheckMode() {
        return this.dupeIpCheckMode;
    }
    private void setDupeIpCheckMode(String dupeIpCheckMode) {
        this.dupeIpCheckMode = dupeIpCheckMode;
    }
    public String getDupeIpAction() {
        return this.dupeIpAction;
    }
    private void setDupeIpAction(String dupeIpAction) {
        this.dupeIpAction = dupeIpAction;
    }
    public int getDupeIpTimerDelay() {
        return this.dupeIpTimerDelay;
    }
    private void setDupeIpTimerDelay(int dupeIpTimerDelay) {
        this.dupeIpTimerDelay = dupeIpTimerDelay;
    }

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

    public boolean isUnsafeActionsConfirmation() {
        return this.unsafeActionsConfirmation;
    }
    public boolean showDescription() {
        return this.showDescription;
    }
    private void setShowDescription(boolean showDescription) {
        this.showDescription = showDescription;
    }
    private void setUnsafeActionsConfirmation(boolean unsafeActionsConfirmation) {
        this.unsafeActionsConfirmation = unsafeActionsConfirmation;
    }
    private void setApiEnabled(boolean apiEnabled) {
        this.isApiEnabled = apiEnabled;
    }
    public boolean isApiEnabled() {
        return this.isApiEnabled;
    }
    public boolean isPurgeConfirmation() {
        return this.purgeConfirmation;
    }
    private void setPurgeConfirmation(boolean purgeConfirmation) {
        this.purgeConfirmation = purgeConfirmation;
    }
    public boolean isApiProtectedByPassword() {
        return this.isApiProtectedByPassword;
    }
    private void setApiProtectedByPassword(boolean apiProtectedByPassword) {
        isApiProtectedByPassword = apiProtectedByPassword;
    }
    public boolean isBanAllowedWithoutReason() {
        return isBanAllowedWithoutReason;
    }
    public void setBanAllowedWithoutReason(boolean banAllowedWithoutReason) {
        isBanAllowedWithoutReason = banAllowedWithoutReason;
    }
    public boolean isKickAllowedWithoutReason() {
        return isKickAllowedWithoutReason;
    }
    public void setKickAllowedWithoutReason(boolean kickAllowedWithoutReason) {
        isKickAllowedWithoutReason = kickAllowedWithoutReason;
    }
    public Set<String> getPossibleGroups() {
        return timeRestrictionGroups;
    }
    public void setPossibleGroups(Set<String> timeRestrictionGroups) {
        this.timeRestrictionGroups.clear();
        this.timeRestrictionGroups = timeRestrictionGroups;
    }
    public boolean isMuteAllowedWithoutReason() {
        return isMuteAllowedWithoutReason;
    }
    public void setMuteAllowedWithoutReason(boolean muteAllowedWithoutReason) {
        isMuteAllowedWithoutReason = muteAllowedWithoutReason;
    }
    public boolean isAllowedUseRamAsContainer() {
        return isAllowedUseRamAsContainer;
    }
    private void setAllowedUseRamAsContainer(boolean allowedUseRamAsContainer) {
        isAllowedUseRamAsContainer = allowedUseRamAsContainer;
    }
    public boolean showExamples() {
        return showExamples;
    }
    public void setShowExamples(boolean showExamples) {
        this.showExamples = showExamples;
    }
    public StorageType getStorageType() {
        return storageType;
    }
    private void setStorageType(StorageType storageType) {
        this.storageType = storageType;
    }
    public boolean isAnnounceWhenLogHided() {
        return isAnnounceWhenLogHided;
    }
    private void setAnnounceWhenLogHided(boolean status) {
        isAnnounceWhenLogHided = status;
    }
    public boolean hideMainCommand() {
        return hideMainCommand;
    }
    private void setHideMainCommand(boolean hideMainCommand) {
        this.hideMainCommand = hideMainCommand;
    }
    public boolean isLessInformation() {
        return isLessInformation;
    }
    private void setLessInformation(boolean lessInformation) {
        isLessInformation = lessInformation;
    }
    public boolean isProhibitYourselfInteraction() {
        return isProhibitYourselfInteraction;
    }
    private void setProhibitYourselfInteraction(boolean prohibitYourselfInteraction) { isProhibitYourselfInteraction = prohibitYourselfInteraction; }
    public boolean isPlayersNotification() {
        return isPlayersNotification;
    }
    public boolean isConsoleNotification() {
        return isConsoleNotification;
    }
    private void setPlayersNotification(boolean playersNotification) {
        isPlayersNotification = playersNotification;
    }
    private void setConsoleNotification(boolean consoleNotification) {
        isConsoleNotification = consoleNotification;
    }
    private void setCooldownsEnabled(boolean cooldownsEnabled) {
        isCooldownsEnabled = cooldownsEnabled;
    }
    public boolean isCooldownsEnabled() {
        return isCooldownsEnabled;
    }
    public boolean isSaveCooldowns() {
        return isSaveCooldowns;
    }
    private void setSaveCooldowns(boolean saveCooldowns) {
        isSaveCooldowns = saveCooldowns;
    }
    public String getGlobalLanguage() {
        return this.globalLanguage;
    }
    public void setGlobalLanguage(String globalLanguage) {
        this.globalLanguage = globalLanguage;
    }
    public boolean isAllowedUnbanWithoutReason() {
        return this.isAllowedUnbanWithoutReason;
    }
    private void setAllowedUnbanWithoutReason(boolean allowedUnbanWithoutReason) {
        this.isAllowedUnbanWithoutReason = allowedUnbanWithoutReason;
    }
    public String getBanTimeExpired() {
        return this.banTimeExpired;
    }
    public void setBanTimeExpired(String banTimeExpired) {
        this.banTimeExpired = banTimeExpired;
    }

    public boolean isLoggerEnabled() {
        return this.isLoggerEnabled;
    }
    private void setLoggerEnabled(boolean loggerEnabled) {
        this.isLoggerEnabled = loggerEnabled;
    }
    public String getLogFormat() {
        return this.logFormat;
    }
    private void setLogFormat(String logFormat) {
        this.logFormat = logFormat;
    }
    public List<String> getMessagesToLog() {
        return messagesToLog;
    }
    private void setMessagesToLog(List<String> messagesToLog) {
        this.messagesToLog.clear();
        this.messagesToLog = messagesToLog;
    }

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

    public boolean isOldServerVersion() {
        return this.isOldServerVersion;
    }
    private void setOldServerVersion(boolean oldServerVersion) {
        this.isOldServerVersion = oldServerVersion;
    }
    public boolean isAutoPurgerEnabled() {
        return this.isAutoPurgerEnabled;
    }
    private void setAutoPurgerEnabled(boolean autoPurgerEnabled) {
        this.isAutoPurgerEnabled = autoPurgerEnabled;
    }
    public int getAutoPurgerDelay() {
        return this.autoPurgerDelay;
    }
    private void setAutoPurgerDelay(int autoPurgerDelay) {
        this.autoPurgerDelay = autoPurgerDelay;
    }

    public void loadConfigSettings() {
        setLessInformation(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.less-information"));
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
            switch (getFileAccessor().getGeneralConfig().getString("plugin-settings.storage-method")) {
                case "sqlite": {
                    setStorageType(StorageType.SQLITE);
                    if(!isLessInformation()) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalBans | Plugin Loading] Data storage method installed: %storage_method%".replace("%storage_method%", String.valueOf(getStorageType()))));
                    }
                    break;
                }
                case "mysql": {
                    setStorageType(StorageType.MYSQL);
                    if(!isLessInformation()) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalBans | Plugin Loading] Data storage method installed: %storage_method%".replace("%storage_method%", String.valueOf(getStorageType()))));
                    }
                    break;
                }
                case "h2": {
                    setStorageType(StorageType.H2);
                    if(!isLessInformation()) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalBans | Plugin Loading] Data storage method installed: %storage_method%".replace("%storage_method%", String.valueOf(getStorageType()))));
                    }
                    break;
                }
                default: {
                    setStorageType(StorageType.SQLITE);
                    Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalBans | Plugin Loading] Unknown data storage type is specified (%unknown_method%), using SQLite".replace("%unknown_method%", getFileAccessor().getGeneralConfig().getString("plugin-settings.storage-method"))));
                    break;
                }
            }

            setBanTimeExpired(getFileAccessor().getGeneralConfig().getString("plugin-settings.reason-settings.ban-time-left"));

            setGlobalLanguage(getFileAccessor().getGeneralConfig().getString("plugin-settings.global-language"));

            setCooldownsEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cooldowns.enabled"));
            setSaveCooldowns(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cooldowns.save-cooldowns"));
            CooldownsManager.setupCooldowns();

            setNicksControlEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.nicks-control.enabled"));
            setNotifyConsoleWhenNickNameBlocked(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.nicks-control.notify-console"));

            setIpsControlEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.ips-control.enabled"));
            if(isIpsControlEnabled()) {
                setNotifyConsoleWhenIPBlocked(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.ips-control.notify-console"));
                setBlockedIps(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.join-settings.ips-control.blocked-ips"), "[", "]").split(", ")));
            }

            setLoggerEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.logger.enabled"));
            if(isLoggerEnabled()) {
                setLogFormat(getFileAccessor().getGeneralConfig().getString("plugin-settings.logger.log-format"));
                setMessagesToLog(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.logger.messages-to-log"), "[", "]").split(", ")));
            }

            if(isNicksControlEnabled()) {
                switch (getFileAccessor().getGeneralConfig().getString("plugin-settings.join-settings.nicks-control.check-mode")) {
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
                        Bukkit.getConsoleSender().sendMessage(setColors("&c[Functional Bans | Error] The type of verification of nicknames is incorrect in general.yml, use the standard type (contains)"));
                        break;
                    }
                }

                setBlockedNickNames(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.join-settings.nicks-control.blocked-nicks"), "[", "]").split(", ")));

            }

            setDupeIdModeEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.ips-control.dupe-ip.enabled"));
            if(isDupeIdModeEnabled()) {
                setMaxIpsPerSession(getFileAccessor().getGeneralConfig().getInt("plugin-settings.join-settings.ips-control.dupe-ip.max-similar-ips-per-session"));
                if(getMaxIpsPerSession() < 1) {
                    setMaxIpsPerSession(1);
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalBans | Error] The value of 'max-similar-ips-per-session' cannot be less than 1, I use 1"));
                }
                setDupeIpCheckMode(getFileAccessor().getGeneralConfig().getString("plugin-settings.join-settings.ips-control.dupe-ip.check-mode"));
                if(!getDupeIpCheckMode().equalsIgnoreCase("join") && !getDupeIpCheckMode().equalsIgnoreCase("timer")) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalBans | Error] Unknown check method 'dupe-ip' %method%, using 'timer'".replace("%method%", getDupeIpCheckMode())));
                    setDupeIpCheckMode("timer");
                }
                setDupeIpTimerDelay(getFileAccessor().getGeneralConfig().getInt("plugin-settings.join-settings.ips-control.dupe-ip.timer-delay"));
                if(getDupeIpTimerDelay() < 30) {
                    setDupeIpTimerDelay(30);
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalBans | Error] The value of 'dupe-ip.timer-delay' cannot be less than 30, I use 30"));
                }
                setDupeIpAction(getFileAccessor().getGeneralConfig().getString("plugin-settings.join-settings.ips-control.dupe-ip.action"));
            }

            setOldServerVersion(OtherUtils.isOldServerVersion());
            setProhibitYourselfInteraction(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.prohibit-interaction-to-yourself"));
            setBanAllowedWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.bans-without-reason.allowed"));
            setKickAllowedWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.kick-without-reason.allowed"));
            setMuteAllowedWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.mute-without-reason.allowed"));
            setAllowedUnbanWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.unban-with-out-reason.allowed"));
            setUnsafeActionsConfirmation(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.unsafe-actions-confirmation"));
            setPurgeConfirmation(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.purge-confirmation"));
            setShowExamples(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.show-examples"));
            setShowDescription(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.show-description"));
            setHideMainCommand(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.hide-main-command"));
            setConsoleNotification(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.notifications.console"));
            setPlayersNotification(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.notifications.players"));
            setPossibleGroups(getFileAccessor().getGeneralConfig().getConfigurationSection("plugin-settings.time-settings.per-groups").getKeys(false));

            setAutoPurgerEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.auto-purger.enabled"));
            if(isAutoPurgerEnabled()) {
                setAutoPurgerDelay(getFileAccessor().getGeneralConfig().getInt("plugin-settings.auto-purger.delay"));
            }

            setAllowedUseRamAsContainer(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.allow-use-ram"));
            if(isAllowedUseRamAsContainer()) {
                if(!isLessInformation()) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalBans | Plugin loading] Use RAM for data storage (Allowed by the configuration file)"));
                }
            } else {
                if (!isLessInformation()) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalBans | Plugin loading] RAM usage is prohibited, use direct access to the database!"));
                }
            }

            setAnnounceWhenLogHided(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.console-logger.announce-console-when-message-hidden"));

            setApiEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.api.spigot.enabled"));
            if(!isLessInformation()) {
                Bukkit.getConsoleSender().sendMessage(setColors(isApiEnabled() ? "&a[FunctionalBans | API Loading] API usage is allowed by configuration settings" : "&e[FunctionalBans | API Loading] API usage is prohibited by configuration settings (This is not an error)"));
            }

            setApiProtectedByPassword(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.api.spigot.password.enabled"));
            if(isApiEnabled()) {
                if (!isLessInformation()) {
                    Bukkit.getConsoleSender().sendMessage(setColors(isApiProtectedByPassword() ? "&a[FunctionalBans | API Loading] Password protection is set for API" : "&c[FunctionalBans | API Loading] Password protection is not installed for the API (This is not an error)"));
                }
            }
        });
        return;
    }

    public void reloadConfig() {
        setLessInformation(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.less-information"));
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {


            switch (getFileAccessor().getGeneralConfig().getString("plugin-settings.storage-method")) {
                case "sqlite": {
                    setStorageType(StorageType.SQLITE);
                    if (!isLessInformation()) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalBans | Plugin Loading] Data storage method installed: %storage_method%".replace("%storage_method%", String.valueOf(getStorageType()))));
                    }
                    break;
                }
                case "mysql": {
                    setStorageType(StorageType.MYSQL);
                    if (!isLessInformation()) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalBans | Plugin Loading] Data storage method installed: %storage_method%".replace("%storage_method%", String.valueOf(getStorageType()))));
                    }
                    break;
                }
                case "h2": {
                    setStorageType(StorageType.H2);
                    if (!isLessInformation()) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalBans | Plugin Loading] Data storage method installed: %storage_method%".replace("%storage_method%", String.valueOf(getStorageType()))));
                    }
                    break;
                }
                default: {
                    setStorageType(StorageType.SQLITE);
                    Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalBans | Plugin Loading] Unknown data storage type is specified (%unknown_method%), using SQLite".replace("%unknown_method%", getFileAccessor().getGeneralConfig().getString("plugin-settings.storage-method"))));
                    break;
                }
            }

            setBanTimeExpired(getFileAccessor().getGeneralConfig().getString("plugin-settings.reason-settings.ban-time-left"));

            setGlobalLanguage(getFileAccessor().getGeneralConfig().getString("plugin-settings.global-language"));
            setConsoleNotification(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.notifications.console"));
            setPlayersNotification(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.notifications.players"));
            setProhibitYourselfInteraction(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.prohibit-interaction-to-yourself"));
            StaticContainers.getReplacedMessagesContainer().reloadReplacedMessages();
            StaticContainers.getHidedMessagesContainer().reloadHidedMessages();

            setCooldownsEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cooldowns.enabled"));
            setSaveCooldowns(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cooldowns.save-cooldowns"));
            CooldownsManager.setupCooldowns();

            setNicksControlEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.nicks-control.enabled"));
            setNotifyConsoleWhenNickNameBlocked(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.nicks-control.notify-console"));

            setAutoPurgerEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.auto-purger.enabled"));
            if(isAutoPurgerEnabled()) {
                setAutoPurgerDelay(getFileAccessor().getGeneralConfig().getInt("plugin-settings.auto-purger.delay"));
            }

            setIpsControlEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.ips-control.enabled"));
            if(isIpsControlEnabled()) {
                setNotifyConsoleWhenIPBlocked(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.ips-control.notify-console"));
                setBlockedIps(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.join-settings.ips-control.blocked-ips"), "[", "]").split(", ")));
            }

            setDupeIdModeEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.ips-control.dupe-ip.enabled"));
            if(isDupeIdModeEnabled()) {
                setMaxIpsPerSession(getFileAccessor().getGeneralConfig().getInt("plugin-settings.join-settings.ips-control.dupe-ip.max-similar-ips-per-session"));
                if(getMaxIpsPerSession() < 1) {
                    setMaxIpsPerSession(1);
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalBans | Error] The value of 'max-similar-ips-per-session' cannot be less than 1, I use 1"));
                }
                setDupeIpCheckMode(getFileAccessor().getGeneralConfig().getString("plugin-settings.join-settings.ips-control.dupe-ip.check-mode"));
                if(!getDupeIpCheckMode().equalsIgnoreCase("join") && !getDupeIpCheckMode().equalsIgnoreCase("timer")) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalBans | Error] Unknown check method 'dupe-ip' %method%, using 'timer'".replace("%method%", getDupeIpCheckMode())));
                    setDupeIpCheckMode("timer");
                }
                setDupeIpTimerDelay(getFileAccessor().getGeneralConfig().getInt("plugin-settings.join-settings.ips-control.dupe-ip.timer-delay"));
                if(getDupeIpTimerDelay() < 1) {
                    setDupeIpTimerDelay(1);
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalBans | Error] The value of 'dupe-ip.timer-delay' cannot be less than 1, I use 1"));
                }
                setDupeIpAction(getFileAccessor().getGeneralConfig().getString("plugin-settings.join-settings.ips-control.dupe-ip.action"));
            }

            if(isNicksControlEnabled()) {
                switch (getFileAccessor().getGeneralConfig().getString("plugin-settings.join-settings.nicks-control.check-mode")) {
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
                        Bukkit.getConsoleSender().sendMessage(setColors("&c[Functional Bans | Error] The type of verification of nicknames is incorrect in general.yml, use the standard type (contains)"));
                        break;
                    }
                }
                setBlockedNickNames(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.join-settings.nicks-control.blocked-nicks"), "[", "]").split(", ")));

            }

            setBanAllowedWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.bans-without-reason.allowed"));
            setKickAllowedWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.kick-without-reason.allowed"));
            setMuteAllowedWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.mute-without-reason.allowed"));
            setAllowedUnbanWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.unban-with-out-reason.allowed"));
            setUnsafeActionsConfirmation(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.unsafe-actions-confirmation"));
            setPurgeConfirmation(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.purge-confirmation"));
            setShowExamples(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.show-examples"));
            setShowDescription(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.show-description"));
            setHideMainCommand(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.hide-main-command"));
            getPossibleGroups().clear();
            setPossibleGroups(getFileAccessor().getGeneralConfig().getConfigurationSection("plugin-settings.time-settings.per-groups").getKeys(false));
            setAnnounceWhenLogHided(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.console-logger.announce-console-when-message-hidden"));

        });
    }

}
