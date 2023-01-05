package by.alis.functionalservercontrol.spigot.Additional.GlobalSettings;

import by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers;
import by.alis.functionalservercontrol.API.Enums.StorageType;
import by.alis.functionalservercontrol.spigot.Additional.CoreAdapters.Adapter;
import by.alis.functionalservercontrol.spigot.Additional.SomeUtils.OtherUtils;
import by.alis.functionalservercontrol.spigot.Additional.TimerTasks.DupeIpTask;
import by.alis.functionalservercontrol.spigot.Additional.TimerTasks.MuteGlobalTask;
import by.alis.functionalservercontrol.spigot.Additional.TimerTasks.PurgerTask;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.Managers.CooldownsManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;

import java.util.*;

import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getBanContainerManager;
import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;
import static by.alis.functionalservercontrol.spigot.Managers.Mute.MuteManager.getMuteContainerManager;

public class GeneralConfigSettings {


    private String serverCoreName;
    private boolean isOldServerVersion = false;
    private String globalLanguage = "ru_RU";
    private boolean isAnnounceWhenLogHided = true;
    private boolean isAllowedUseRamAsContainer = false;
    private boolean unsafeActionsConfirmation = true;
    private boolean isBanAllowedWithoutReason = true;
    private boolean isKickAllowedWithoutReason = true;
    private boolean isCheatsCheckAllowedWithoutReason = true;
    private boolean isUnmuteAllowedWithoutReason = true;
    private boolean showDescription = true;
    private boolean purgeConfirmation = true;
    private boolean isMuteAllowedWithoutReason = true;
    private boolean isLessInformation = false;
    private boolean hideMainCommand = false;
    private boolean showExamples = true;
    private boolean announceConsoleAboutBrand = false;
    private Set<String> timeRestrictionGroups = new HashSet<>();

    //History settings start
    private boolean isHistoryRecordingEnabled;
    private boolean isHistoryForceSave;
    private int maxHistorySize;
    //History settings end
    //API settings start
    private boolean isApiEnabled = true;
    private boolean isApiProtectedByPassword = false;
    //API settings end
    private boolean isProhibitYourselfInteraction = false;
    //Notification settings start
    private boolean isConsoleNotification = true;
    private boolean isPlayersNotification = true;
    //Notification settings end
    //Cooldowns settings start
    private boolean isCooldownsEnabled = false;
    private boolean isSaveCooldowns = false;
    //Cooldowns settings end
    private StorageType storageType = StorageType.SQLITE;
    private boolean isAllowedUnbanWithoutReason = true;
    private String banTimeExpired = "The Ban time has expired";
    private String muteTimeExpired = "The Mute time has expired";
    private boolean isAutoPurgerEnabled = false;
    private int autoPurgerDelay = 600;
    private boolean sendTitleWhenMuted = true;
    private boolean sendTitleWhenUnmuted = true;
    private boolean serverSupportsHoverEvents = false;
    private String supportedHoverEvents = "MD5";
    private EventPriority chatListenerPriority = EventPriority.NORMAL;
    private List<String> disabledCommandsWhenMuted = new ArrayList<>();
    private boolean sendActionbarWhileMuted = true;
    private boolean blockWorldDownloader = true;
    private List<String> actionsOnWDL = new ArrayList<>();
    private boolean isCheatCheckFunctionEnabled = true;
    private boolean isPreventBlockPlaceDuringCheck;
    private boolean isPreventBlockBreakDuringCheck;
    private boolean isPreventIflictDamageDuringCheck;
    private boolean isPreventTakingDamageDuringCheck;
    private boolean isPreventMoveDuringCheck;
    private boolean isPreventCommandsDuringCheck;
    private boolean isPreventInteractionDuringCheck;
    private boolean isPreventDropItemDuringCheck;
    private boolean isPreventPickupItemDuringCheck;
    private List<String> ignoredCommandsDuruingCheck = new ArrayList<>();
    private int defaultCheatCheckTime;
    private final List<String> actionIfQuitDuringCheck = new ArrayList<>();
    private final List<String> actionIfValidCheatCheck = new ArrayList<>();
    private final List<String> actionIfFailedCheatCheck = new ArrayList<>();
    private final List<String> actionIfTimeLeft = new ArrayList<>();
    private boolean sendTitleOnCheck;
    private boolean preventKickDuringCheck;
    private boolean preventMuteDuringCheck;
    private boolean isLoggerEnabled = false;
    private String logFormat = "[FunctionalBans <-> %1$f] %2$f";
    private final List<String> messagesToLog = new ArrayList<>();
    private boolean isNicksControlEnabled = false;
    private String nicknameCheckMode = "contains";
    private final List<String> blockedNickNames = new ArrayList<>();
    private boolean notifyConsoleWhenNickNameBlocked = false;
    private boolean isIpsControlEnabled = false;
    private final List<String> blockedIps = new ArrayList<>();
    private boolean notifyConsoleWhenIPBlocked = false;
    private boolean dupeIdModeEnabled = false;
    private int maxIpsPerSession = 1;
    private String dupeIpCheckMode = "timer";
    private String dupeIpAction = null;
    private int dupeIpTimerDelay = 30;
    private boolean nickFormatControlEnabled = false;
    private boolean blockVanilla = false;
    private final List<String> vanillaActions = new ArrayList<>();
    private boolean blockForge = false;
    private final List<String> forgeActions = new ArrayList<>();
    private final List<String> blockedNickFormats = new ArrayList<>();

    private void setServerCoreName(String serverCoreName) {
        this.serverCoreName = serverCoreName;
    }
    public String getServerCoreName() {
        return serverCoreName;
    }
    public boolean isAnnounceConsoleAboutBrand() {
        return announceConsoleAboutBrand;
    }
    private void setAnnounceConsoleAboutBrand(boolean announceConsoleAboutBrand) {
        this.announceConsoleAboutBrand = announceConsoleAboutBrand;
    }
    public boolean isBlockVanilla() {
        return blockVanilla;
    }
    public List<String> getVanillaActions() {
        return vanillaActions;
    }
    private void setBlockVanilla(boolean blockVanilla) {
        this.blockVanilla = blockVanilla;
    }
    private void setVanillaActions(List<String> vanillaActions) {
        this.vanillaActions.clear();
        this.vanillaActions.addAll(vanillaActions);
    }
    public boolean isBlockForge() {
        return blockForge;
    }
    private void setBlockForge(boolean blockForge) {
        this.blockForge = blockForge;
    }
    public List<String> getForgeActions() {
        return forgeActions;
    }
    private void setForgeActions(List<String> forgeActions) {
        this.forgeActions.clear();
        this.forgeActions.addAll(forgeActions);
    }

    public boolean isBlockWorldDownloader() {
        return blockWorldDownloader;
    }
    private void setBlockWorldDownloader(boolean blockWorldDownloader) {
        this.blockWorldDownloader = blockWorldDownloader;
    }
    public List<String> getActionsOnWDL() {
        return actionsOnWDL;
    }
    private void setActionsOnWDL(List<String> actionsOnWDL) {
        this.actionsOnWDL.clear();
        this.actionsOnWDL.addAll(actionsOnWDL);
    }

    public boolean isHistoryRecordingEnabled() {
        return isHistoryRecordingEnabled;
    }
    public boolean isHistoryForceSave() {
        return isHistoryForceSave;
    }
    public int getMaxHistorySize() {
        return maxHistorySize;
    }
    private void setHistoryRecordingEnabled(boolean historyRecordingEnabled) {
        isHistoryRecordingEnabled = historyRecordingEnabled;
    }
    private void setHistoryForceSave(boolean historyForceSave) {
        isHistoryForceSave = historyForceSave;
    }
    private void setMaxHistorySize(int maxHistorySize) {
        this.maxHistorySize = maxHistorySize;
    }

    public boolean isServerSupportsHoverEvents() {
        return serverSupportsHoverEvents;
    }
    private void setServerSupportsHoverEvents(boolean serverSupportsHoverEvents) {
        this.serverSupportsHoverEvents = serverSupportsHoverEvents;
    }

    public List<String> getDisabledCommandsWhenMuted() {
        return disabledCommandsWhenMuted;
    }
    private void setDisabledCommandsWhenMuted(List<String> disabledCommandsWhenMuted) {
        this.disabledCommandsWhenMuted.clear();
        this.disabledCommandsWhenMuted.addAll(disabledCommandsWhenMuted);
    }

    public String getMuteTimeExpired() {
        return muteTimeExpired;
    }
    private void setMuteTimeExpired(String muteTimeExpired) {
        this.muteTimeExpired = muteTimeExpired;
    }

    public boolean isSendTitleWhenMuted() {
        return sendTitleWhenMuted;
    }
    public boolean isSendTitleWhenUnmuted() {
        return sendTitleWhenUnmuted;
    }
    private void setSendTitleWhenMuted(boolean sendTitleWhenMuted) {
        this.sendTitleWhenMuted = sendTitleWhenMuted;
    }
    private void setSendTitleWhenUnmuted(boolean sendTitleWhenUnmuted) {
        this.sendTitleWhenUnmuted = sendTitleWhenUnmuted;
    }

    public boolean isCheatCheckFunctionEnabled() {
        return isCheatCheckFunctionEnabled;
    }
    private void setCheatCheckFunctionEnabled(boolean cheatCheckFunctionEnabled) {
        isCheatCheckFunctionEnabled = cheatCheckFunctionEnabled;
    }
    public boolean isPreventBlockPlaceDuringCheck() {
        return isPreventBlockPlaceDuringCheck;
    }
    private void setPreventBlockPlaceDuringCheck(boolean preventBlockPlaceDuringCheck) {
        isPreventBlockPlaceDuringCheck = preventBlockPlaceDuringCheck;
    }
    public boolean isPreventBlockBreakDuringCheck() {
        return isPreventBlockBreakDuringCheck;
    }
    private void setPreventBlockBreakDuringCheck(boolean preventBlockBreakDuringCheck) {
        isPreventBlockBreakDuringCheck = preventBlockBreakDuringCheck;
    }
    public boolean isPreventIflictDamageDuringCheck() {
        return isPreventIflictDamageDuringCheck;
    }
    private void setPreventIflictDamageDuringCheck(boolean preventIflictDamageDuringCheck) {
        isPreventIflictDamageDuringCheck = preventIflictDamageDuringCheck;
    }
    public boolean isPreventTakingDamageDuringCheck() {
        return isPreventTakingDamageDuringCheck;
    }
    private void setPreventTakingDamageDuringCheck(boolean preventTakingDamageDuringCheck) {
        isPreventTakingDamageDuringCheck = preventTakingDamageDuringCheck;
    }
    public boolean isPreventMoveDuringCheck() {
        return isPreventMoveDuringCheck;
    }
    private void setPreventMoveDuringCheck(boolean preventMoveDuringCheck) {
        isPreventMoveDuringCheck = preventMoveDuringCheck;
    }
    public boolean isPreventCommandsDuringCheck() {
        return isPreventCommandsDuringCheck;
    }
    private void setPreventCommandsDuringCheck(boolean preventCommandsDuringCheck) {
        isPreventCommandsDuringCheck = preventCommandsDuringCheck;
    }
    public boolean isPreventDropItemDuringCheck() {
        return isPreventDropItemDuringCheck;
    }
    private void setPreventDropItemDuringCheck(boolean preventDropItemDuringCheck) {
        isPreventDropItemDuringCheck = preventDropItemDuringCheck;
    }
    public boolean isPreventPickupItemDuringCheck() {
        return isPreventPickupItemDuringCheck;
    }
    private void setPreventPickupItemDuringCheck(boolean preventPickupItemDuringCheck) {
        isPreventPickupItemDuringCheck = preventPickupItemDuringCheck;
    }
    public List<String> getIgnoredCommandsDuruingCheck() {
        return ignoredCommandsDuruingCheck;
    }
    private void setIgnoredCommandsDuruingCheck(List<String> ignoredCommandsDuruingCheck) {
        try {
            this.ignoredCommandsDuruingCheck.clear();
        }catch (NullPointerException ignored) {}
        this.ignoredCommandsDuruingCheck.addAll(ignoredCommandsDuruingCheck);
    }
    public int getDefaultCheatCheckTime() {
        return defaultCheatCheckTime;
    }
    private void setDefaultCheatCheckTime(int defaultCheatCheckTime) {
        this.defaultCheatCheckTime = defaultCheatCheckTime;
    }
    public List<String> getActionIfQuitDuringCheck() {
        return actionIfQuitDuringCheck;
    }
    private void setActionIfQuitDuringCheck(List<String> actionIfQuitDuringCheck) {
        this.actionIfQuitDuringCheck.clear();
        this.actionIfQuitDuringCheck.addAll(actionIfQuitDuringCheck);
    }
    public List<String> getActionIfValidCheatCheck() {
        return actionIfValidCheatCheck;
    }
    private void setActionIfValidCheatCheck(List<String> actionIfValidCheatCheck) {
        this.actionIfValidCheatCheck.clear();
        this.actionIfValidCheatCheck.addAll(actionIfValidCheatCheck);
    }
    public List<String> getActionIfFailedCheatCheck() {
        return actionIfFailedCheatCheck;
    }
    private void setActionIfFailedCheatCheck(List<String> actionIfFailedCheatCheck) {
        this.actionIfFailedCheatCheck.clear();
        this.actionIfFailedCheatCheck.addAll(actionIfFailedCheatCheck);
    }
    private void setSendTitleOnCheck(boolean sendTitleOnCheck) {
        this.sendTitleOnCheck = sendTitleOnCheck;
    }
    public boolean isSendTitleOnCheck() {
        return sendTitleOnCheck;
    }
    public List<String> getActionIfTimeLeft() {
        return actionIfTimeLeft;
    }
    private void setActionIfTimeLeft(List<String> actionIfTimeLeft) {
        this.actionIfTimeLeft.clear();
        this.actionIfTimeLeft.addAll(actionIfTimeLeft);
    }
    private void setPreventInteractionDuringCheck(boolean preventInteractionDuringCheck) {
        isPreventInteractionDuringCheck = preventInteractionDuringCheck;
    }
    public boolean isPreventInteractionDuringCheck() {
        return isPreventInteractionDuringCheck;
    }
    private void setPreventKickDuringCheck(boolean preventKickDuringCheck) {
        this.preventKickDuringCheck = preventKickDuringCheck;
    }
    public boolean isPreventKickDuringCheck() {
        return preventKickDuringCheck;
    }
    private void setPreventMuteDuringCheck(boolean preventMuteDuringCheck) {
        this.preventMuteDuringCheck = preventMuteDuringCheck;
    }
    public boolean isPreventMuteDuringCheck() {
        return preventMuteDuringCheck;
    }
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

    public EventPriority getChatListenerPriority() {
        return chatListenerPriority;
    }
    private void setChatListenerPriority(EventPriority chatListenerPriority) {
        this.chatListenerPriority = chatListenerPriority;
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
    public boolean isCheatsCheckAllowedWithoutReason() {
        return this.isCheatsCheckAllowedWithoutReason;
    }
    private void setCheatsCheckAllowedWithoutReason(boolean cheatsCheckAllowedWithoutReason) {
        this.isCheatsCheckAllowedWithoutReason = cheatsCheckAllowedWithoutReason;
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
    public boolean isUnmuteAllowedWithoutReason() {
        return isUnmuteAllowedWithoutReason;
    }
    private void setUnmuteAllowedWithoutReason(boolean unmuteAllowedWithoutReason) {
        isUnmuteAllowedWithoutReason = unmuteAllowedWithoutReason;
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
    private void setBanAllowedWithoutReason(boolean banAllowedWithoutReason) {
        isBanAllowedWithoutReason = banAllowedWithoutReason;
    }
    public boolean isKickAllowedWithoutReason() {
        return isKickAllowedWithoutReason;
    }
    private void setKickAllowedWithoutReason(boolean kickAllowedWithoutReason) {
        isKickAllowedWithoutReason = kickAllowedWithoutReason;
    }
    public Set<String> getPossibleGroups() {
        return timeRestrictionGroups;
    }
    private void setPossibleGroups(Set<String> timeRestrictionGroups) {
        this.timeRestrictionGroups.clear();
        this.timeRestrictionGroups = timeRestrictionGroups;
    }
    public boolean isMuteAllowedWithoutReason() {
        return isMuteAllowedWithoutReason;
    }
    private void setMuteAllowedWithoutReason(boolean muteAllowedWithoutReason) {
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
    private void setShowExamples(boolean showExamples) {
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
    private void setGlobalLanguage(String globalLanguage) {
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
    private void setBanTimeExpired(String banTimeExpired) {
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
        this.messagesToLog.addAll(messagesToLog);
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
    private void setBlockedNickNames(List<String> blockedNickName) {
        this.blockedNickNames.clear();
        this.blockedNickNames.addAll(blockedNickName);
    }
    private void setNotifyConsoleWhenNickNameBlocked(boolean notifyConsoleWhenNickNameBlocked) {
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
    private void setNickFormatControlEnabled(boolean nickFormatControlEnabled) {
        this.nickFormatControlEnabled = nickFormatControlEnabled;
    }
    public boolean isNickFormatControlEnabled() {
        return nickFormatControlEnabled;
    }
    private void setBlockedNickFormats(List<String> blockedNickFormats) {
        this.blockedNickFormats.clear();
        this.blockedNickFormats.addAll(blockedNickFormats);
    }
    public List<String> getBlockedNickFormats() {
        return blockedNickFormats;
    }
    public boolean isSendActionbarWhileMuted() {
        return sendActionbarWhileMuted;
    }
    private void setSendActionbarWhileMuted(boolean sendActionbarWhileMuted) {
        this.sendActionbarWhileMuted = sendActionbarWhileMuted;
    }
    private void setSupportedHoverEvents(String supportedHoverEvents) {
        this.supportedHoverEvents = supportedHoverEvents;
    }
    public String getSupportedHoverEvents() {
        return supportedHoverEvents;
    }

    public void loadConfigSettings() {
        setServerCoreName(OtherUtils.getServerCoreName(Bukkit.getServer()));
        setLessInformation(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.less-information"));
        switch (getFileAccessor().getGeneralConfig().getString("plugin-settings.chat-settings.chat-listener-priority")) {
            case "LOWEST": {
                setChatListenerPriority(EventPriority.LOWEST);
                if(!isLessInformation()) Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl] Chat listener priority set to 'LOWEST'"));
                break;
            }
            case "LOW": {
                setChatListenerPriority(EventPriority.LOW);
                if(!isLessInformation()) Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl] Chat listener priority set to 'LOW'"));
                break;
            }
            case "NORMAL": {
                setChatListenerPriority(EventPriority.NORMAL);
                if(!isLessInformation()) Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl] Chat listener priority set to 'NORMAL'"));
                break;
            }
            case "HIGH": {
                setChatListenerPriority(EventPriority.HIGH);
                if(!isLessInformation()) Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl] Chat listener priority set to 'HIGH'"));
                break;
            }
            case "HIGHEST": {
                setChatListenerPriority(EventPriority.HIGHEST);
                if(!isLessInformation()) Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl] Chat listener priority set to 'HIGHEST'"));
                break;
            }
            default: {
                setChatListenerPriority(EventPriority.NORMAL);
                if(!isLessInformation())  Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Unknown priority value(%priority%) for chat listener! I use 'NORMAL'".replace("%priority%", getFileAccessor().getGeneralConfig().getString("plugin-settings.chat-settings.chat-listener-priority"))));
                break;
            }
        }
        if(OtherUtils.isServerSupportMDHoverText()) {
            setServerSupportsHoverEvents(true);
            setSupportedHoverEvents("MD5");
        }
        if(OtherUtils.isServerSupportAdventureApi()){
            setServerSupportsHoverEvents(true);
            setSupportedHoverEvents("ADVENTURE");
        }
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
            switch (getFileAccessor().getGeneralConfig().getString("plugin-settings.storage-method")) {
                case "sqlite": {
                    setStorageType(StorageType.SQLITE);
                    if(!isLessInformation()) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl | Plugin Loading] Data storage method installed: %storage_method%".replace("%storage_method%", String.valueOf(getStorageType()))));
                    }
                    break;
                }
                case "mysql": {
                    setStorageType(StorageType.MYSQL);
                    if(!isLessInformation()) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl | Plugin Loading] Data storage method installed: %storage_method%".replace("%storage_method%", String.valueOf(getStorageType()))));
                    }
                    break;
                }
                case "h2": {
                    setStorageType(StorageType.H2);
                    if(!isLessInformation()) {
                        Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl | Plugin Loading] Data storage method installed: %storage_method%".replace("%storage_method%", String.valueOf(getStorageType()))));
                    }
                    break;
                }
                default: {
                    setStorageType(StorageType.SQLITE);
                    Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl | Plugin Loading] Unknown data storage type is specified (%unknown_method%), using SQLite".replace("%unknown_method%", getFileAccessor().getGeneralConfig().getString("plugin-settings.storage-method"))));
                    break;
                }
            }
            setBlockWorldDownloader(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.world-downloader.block"));
            if(isBlockWorldDownloader()) {
                setActionsOnWDL(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.join-settings.clients-control.world-downloader.actions"));
            }
            setBanTimeExpired(getFileAccessor().getGeneralConfig().getString("plugin-settings.reason-settings.ban-time-left"));
            setMuteTimeExpired(getFileAccessor().getGeneralConfig().getString("plugin-settings.reason-settings.mute-time-left"));
            setHistoryRecordingEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.history-settings.enabled"));
            if(isHistoryRecordingEnabled()) {
                setHistoryForceSave(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.history-settings.force-save"));
                setMaxHistorySize(getFileAccessor().getGeneralConfig().getInt("plugin-settings.history-settings.maximum-records"));
            }
            setGlobalLanguage(getFileAccessor().getGeneralConfig().getString("plugin-settings.global-language"));
            setDisabledCommandsWhenMuted(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.chat-settings.disabled-commands-when-muted"), "[", "]").split(", ")));
            setSendTitleWhenMuted(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.title-settings.send-when-muted"));
            setSendTitleWhenUnmuted(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.title-settings.send-when-unmuted"));
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
            setBlockVanilla(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.vanilla.block"));
            if(isBlockVanilla()) {
                setVanillaActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.join-settings.clients-control.vanilla.actions"));
            }
            setBlockForge(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.forge.block"));
            if(isBlockForge()) {
                setForgeActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.join-settings.clients-control.forge.actions"));
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
            setSendActionbarWhileMuted(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.action-bar-settings.send-while-muted"));
            setDupeIdModeEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.ips-control.dupe-ip.enabled"));
            if(isDupeIdModeEnabled()) {
                setMaxIpsPerSession(getFileAccessor().getGeneralConfig().getInt("plugin-settings.join-settings.ips-control.dupe-ip.max-similar-ips-per-session"));
                if(getMaxIpsPerSession() < 1) {
                    setMaxIpsPerSession(1);
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] The value of 'max-similar-ips-per-session' cannot be less than 1, I use 1"));
                }
                setDupeIpCheckMode(getFileAccessor().getGeneralConfig().getString("plugin-settings.join-settings.ips-control.dupe-ip.check-mode"));
                if(!getDupeIpCheckMode().equalsIgnoreCase("join") && !getDupeIpCheckMode().equalsIgnoreCase("timer")) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Unknown check method 'dupe-ip' %method%, using 'timer'".replace("%method%", getDupeIpCheckMode())));
                    setDupeIpCheckMode("timer");
                }
                setDupeIpTimerDelay(getFileAccessor().getGeneralConfig().getInt("plugin-settings.join-settings.ips-control.dupe-ip.timer-delay"));
                if(getDupeIpTimerDelay() < 30) {
                    setDupeIpTimerDelay(30);
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] The value of 'dupe-ip.timer-delay' cannot be less than 30, I use 30"));
                }
                setDupeIpAction(getFileAccessor().getGeneralConfig().getString("plugin-settings.join-settings.ips-control.dupe-ip.action"));
            }
            setOldServerVersion(OtherUtils.isOldServerVersion());
            setProhibitYourselfInteraction(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.prohibit-interaction-to-yourself"));
            setBanAllowedWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.bans-without-reason.allowed"));
            setKickAllowedWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.kick-without-reason.allowed"));
            setMuteAllowedWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.mute-without-reason.allowed"));
            setCheatsCheckAllowedWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.cheatcheck-without-reason.allowed"));
            setUnsafeActionsConfirmation(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.unsafe-actions-confirmation"));
            setAllowedUnbanWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.unban-without-reason.allowed"));
            setUnmuteAllowedWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.unmute-without-reason.allowed"));
            setPurgeConfirmation(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.purge-confirmation"));
            setShowExamples(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.show-examples"));
            setShowDescription(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.show-description"));
            setHideMainCommand(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.hide-main-command"));
            setConsoleNotification(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.notifications.console"));
            setPlayersNotification(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.notifications.players"));
            setPossibleGroups(getFileAccessor().getGeneralConfig().getConfigurationSection("plugin-settings.time-settings.per-groups").getKeys(false));
            setAnnounceConsoleAboutBrand(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.announce-console-about-brand"));
            setNickFormatControlEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.nicks-control.nick-format-control.enabled"));
            if(isNickFormatControlEnabled()) {
                setBlockedNickFormats(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.join-settings.nicks-control.nick-format-control.blocked-formats"), "[", "]").split(", ")));
            }
            setAutoPurgerEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.auto-purger.enabled"));
            if(isAutoPurgerEnabled()) {
                setAutoPurgerDelay(getFileAccessor().getGeneralConfig().getInt("plugin-settings.auto-purger.delay"));
            }
            setCheatCheckFunctionEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.enabled"));
            if(isCheatCheckFunctionEnabled()) {
                setPreventBlockPlaceDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.block-place"));
                setPreventBlockBreakDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.block-break"));
                setPreventIflictDamageDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.inflict-damage"));
                setPreventTakingDamageDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.take-damage"));
                setPreventMoveDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.move"));
                setPreventInteractionDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.interact"));
                setPreventCommandsDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.use-commands"));
                setPreventDropItemDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.item-drop"));
                setPreventPickupItemDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.item-pickup"));
                setPreventKickDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevent-player-kick"));
                setPreventMuteDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevent-player-mute"));
                setIgnoredCommandsDuruingCheck(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.cheat-checks-settings.whitelisted-commands"), "[", "]").split(", ")));
                setDefaultCheatCheckTime(getFileAccessor().getGeneralConfig().getInt("plugin-settings.cheat-checks-settings.default-check-time"));
                setActionIfQuitDuringCheck(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.cheat-checks-settings.actions.if-player-quit"));
                setActionIfFailedCheatCheck(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.cheat-checks-settings.actions.if-check-fails"));
                setActionIfValidCheatCheck(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.cheat-checks-settings.actions.if-check-is-valid"));
                setActionIfTimeLeft(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.cheat-checks-settings.actions.if-time-left"));
                setSendTitleOnCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.send-title"));
            }
            setAllowedUseRamAsContainer(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.allow-use-ram"));
            //Bases
            if(isAllowedUseRamAsContainer()) {
                if(!isLessInformation()) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl | Plugin loading] Use RAM for data storage (Allowed by the configuration file)"));
                }
                getMuteContainerManager().loadMutesIntoRAM();
                getBanContainerManager().loadBansIntoRAM();
            } else {
                if (!isLessInformation()) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl | Plugin loading] RAM usage is prohibited, use direct access to the database!"));
                }
            }
            //Bases
            setAnnounceWhenLogHided(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.console-logger.announce-console-when-message-hidden"));
            setApiEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.api.spigot.enabled"));
            if(!isLessInformation()) {
                Bukkit.getConsoleSender().sendMessage(setColors(isApiEnabled() ? "&a[FunctionalServerControl | API Loading] API usage is allowed by configuration settings" : "&e[FunctionalServerControl | API Loading] API usage is prohibited by configuration settings (This is not an error)"));
            }
            setApiProtectedByPassword(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.api.spigot.password.enabled"));
            if(isApiEnabled()) {
                if (!isLessInformation()) {
                    Bukkit.getConsoleSender().sendMessage(setColors(isApiProtectedByPassword() ? "&a[FunctionalServerControl | API Loading] Password protection is set for API" : "&c[FunctionalServerControl | API Loading] Password protection is not installed for the API (This is not an error)"));
                }
            }
            //Tasks
            if (isAutoPurgerEnabled()) {
                if (getAutoPurgerDelay() > 5) {
                    new PurgerTask().runTaskTimerAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), 0, getAutoPurgerDelay() * 20L);
                }
            }
            if(isDupeIdModeEnabled()) {
                new DupeIpTask().runTaskTimerAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), 0, getDupeIpTimerDelay() * 20L);        //Timers
            }
            new MuteGlobalTask().runTaskTimerAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), 0, 20L);
            //Tasks
            OtherUtils.loadCachedPlayers();
        });
        return;
    }

    public void reloadConfig() {
        setLessInformation(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.less-information"));
        switch (getFileAccessor().getGeneralConfig().getString("plugin-settings.storage-method")) {
            case "sqlite": {
                setStorageType(StorageType.SQLITE);
                if (!isLessInformation()) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl | Plugin Loading] Data storage method installed: %storage_method%".replace("%storage_method%", String.valueOf(getStorageType()))));
                }
                break;
            }
            case "mysql": {
                setStorageType(StorageType.MYSQL);
                if (!isLessInformation()) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl | Plugin Loading] Data storage method installed: %storage_method%".replace("%storage_method%", String.valueOf(getStorageType()))));
                }
                break;
            }
            case "h2": {
                setStorageType(StorageType.H2);
                if (!isLessInformation()) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl | Plugin Loading] Data storage method installed: %storage_method%".replace("%storage_method%", String.valueOf(getStorageType()))));
                }
                break;
            }
            default: {
                setStorageType(StorageType.SQLITE);
                Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl | Plugin Loading] Unknown data storage type is specified (%unknown_method%), using SQLite".replace("%unknown_method%", getFileAccessor().getGeneralConfig().getString("plugin-settings.storage-method"))));
                break;
            }
        }
        setBanTimeExpired(getFileAccessor().getGeneralConfig().getString("plugin-settings.reason-settings.ban-time-left"));
        setMuteTimeExpired(getFileAccessor().getGeneralConfig().getString("plugin-settings.reason-settings.mute-time-left"));
        setHistoryRecordingEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.history-settings.enabled"));
        if(isHistoryRecordingEnabled()) {
            setHistoryForceSave(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.history-settings.force-save"));
            setMaxHistorySize(getFileAccessor().getGeneralConfig().getInt("plugin-settings.history-settings.maximum-records"));
        }
        setBlockWorldDownloader(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.world-downloader.block"));
        if(isBlockWorldDownloader()) {
            setActionsOnWDL(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.join-settings.clients-control.world-downloader.actions"));
        }
        setBlockVanilla(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.vanilla.block"));
        if(isBlockVanilla()) {
            setVanillaActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.join-settings.clients-control.vanilla.actions"));
        }
        setBlockForge(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.forge.block"));
        if(isBlockForge()) {
            setForgeActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.join-settings.clients-control.forge.actions"));
        }
        setDisabledCommandsWhenMuted(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.chat-settings.disabled-commands-when-muted"), "[", "]").split(", ")));
        setSendTitleWhenMuted(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.title-settings.send-when-muted"));
        setSendTitleWhenUnmuted(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.title-settings.send-when-unmuted"));
        setGlobalLanguage(getFileAccessor().getGeneralConfig().getString("plugin-settings.global-language"));
        setConsoleNotification(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.notifications.console"));
        setPlayersNotification(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.notifications.players"));
        setProhibitYourselfInteraction(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.prohibit-interaction-to-yourself"));
        StaticContainers.getReplacedMessagesContainer().reloadReplacedMessages();
        StaticContainers.getHidedMessagesContainer().reloadHidedMessages();
        setCooldownsEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cooldowns.enabled"));
        setSaveCooldowns(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cooldowns.save-cooldowns"));
        CooldownsManager.setupCooldowns();
        setCheatCheckFunctionEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.enabled"));
        if(isCheatCheckFunctionEnabled()) {
            setPreventBlockPlaceDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.block-place"));
            setPreventBlockBreakDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.block-break"));
            setPreventIflictDamageDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.inflict-damage"));
            setPreventTakingDamageDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.take-damage"));
            setPreventMoveDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.move"));
            setPreventCommandsDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.use-commands"));
            setPreventInteractionDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.interact"));
            setIgnoredCommandsDuruingCheck(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.cheat-checks-settings.whitelisted-commands"), "[", "]").split(", ")));
            setDefaultCheatCheckTime(getFileAccessor().getGeneralConfig().getInt("plugin-settings.cheat-checks-settings.default-check-time"));
            setActionIfQuitDuringCheck(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.cheat-checks-settings.actions.if-player-quit"));
            setActionIfFailedCheatCheck(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.cheat-checks-settings.actions.if-check-fails"));
            setActionIfValidCheatCheck(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.cheat-checks-settings.actions.if-check-is-valid"));
            setActionIfTimeLeft(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.cheat-checks-settings.actions.if-time-left"));
            setSendTitleOnCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.send-title"));
            setPreventKickDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevent-player-kick"));
            setPreventMuteDuringCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevent-player-mute"));
        }
        setNicksControlEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.nicks-control.enabled"));
        setNotifyConsoleWhenNickNameBlocked(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.nicks-control.notify-console"));
        setNickFormatControlEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.nicks-control.nick-format-control.enabled"));
        if(isNickFormatControlEnabled()) {
            setBlockedNickFormats(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.join-settings.nicks-control.nick-format-control.blocked-formats"), "[", "]").split(", ")));
        }
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
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] The value of 'max-similar-ips-per-session' cannot be less than 1, I use 1"));
            }
            setDupeIpCheckMode(getFileAccessor().getGeneralConfig().getString("plugin-settings.join-settings.ips-control.dupe-ip.check-mode"));
            if(!getDupeIpCheckMode().equalsIgnoreCase("join") && !getDupeIpCheckMode().equalsIgnoreCase("timer")) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Unknown check method 'dupe-ip' %method%, using 'timer'".replace("%method%", getDupeIpCheckMode())));
                setDupeIpCheckMode("timer");
            }
            setDupeIpTimerDelay(getFileAccessor().getGeneralConfig().getInt("plugin-settings.join-settings.ips-control.dupe-ip.timer-delay"));
            if(getDupeIpTimerDelay() < 1) {
                setDupeIpTimerDelay(1);
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] The value of 'dupe-ip.timer-delay' cannot be less than 1, I use 1"));
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
        setAnnounceConsoleAboutBrand(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.announce-console-about-brand"));
        setSendActionbarWhileMuted(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.action-bar-settings.send-while-muted"));
        setBanAllowedWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.bans-without-reason.allowed"));
        setKickAllowedWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.kick-without-reason.allowed"));
        setMuteAllowedWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.mute-without-reason.allowed"));
        setAllowedUnbanWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.unban-without-reason.allowed"));
        setUnmuteAllowedWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.unmute-without-reason.allowed"));
        setCheatsCheckAllowedWithoutReason(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.reason-settings.cheatcheck-without-reason.allowed"));
        setUnsafeActionsConfirmation(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.unsafe-actions-confirmation"));
        setPurgeConfirmation(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.purge-confirmation"));
        setShowExamples(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.show-examples"));
        setShowDescription(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.show-description"));
        setHideMainCommand(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.hide-main-command"));
        getPossibleGroups().clear();
        setPossibleGroups(getFileAccessor().getGeneralConfig().getConfigurationSection("plugin-settings.time-settings.per-groups").getKeys(false));
        setAnnounceWhenLogHided(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.console-logger.announce-console-when-message-hidden"));
    }
}
