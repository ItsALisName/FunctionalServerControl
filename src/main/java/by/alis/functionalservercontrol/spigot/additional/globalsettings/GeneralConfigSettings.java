package by.alis.functionalservercontrol.spigot.additional.globalsettings;

import by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers;
import by.alis.functionalservercontrol.api.enums.StorageType;
import by.alis.functionalservercontrol.spigot.libraries.org.apache.commons.lang3.StringUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import by.alis.functionalservercontrol.spigot.additional.tasks.*;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.managers.TaskManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;

import java.util.*;

import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.getBanContainerManager;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getChatSettings;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;
import static by.alis.functionalservercontrol.spigot.managers.mute.MuteManager.getMuteContainerManager;

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
    //API settings start
    private boolean isApiEnabled;
    //API settings end
    private boolean isProhibitYourselfInteraction = false;
    //Notification settings start
    private boolean isConsoleNotification = true;
    private boolean isPlayersNotification = true;
    //Notification settings end
    private StorageType storageType = StorageType.SQLITE;
    private boolean isAllowedUnbanWithoutReason = true;
    private String banTimeExpired = "The Ban time has expired";
    private String muteTimeExpired = "The Mute time has expired";
    private boolean sendTitleWhenMuted = true;
    private boolean sendTitleWhenUnmuted = true;
    private boolean serverSupportsHoverEvents;
    private String supportedHoverEvents = "MD5";
    private EventPriority chatListenerPriority = EventPriority.NORMAL;
    private final List<String> disabledCommandsWhenMuted = new ArrayList<>();
    private boolean sendActionbarWhileMuted = true;
    private boolean blockWorldDownloader = true;
    private final List<String> actionsOnWDL = new ArrayList<>();
    private boolean isCheatCheckFunctionEnabled = true;
    private boolean isPreventBlockPlaceDuringCheatCheck;
    private boolean isPreventBlockBreakDuringCheatCheck;
    private boolean isPreventIflictDamageDuringCheatCheck;
    private boolean isPreventTakingDamageDuringCheatCheck;
    private boolean isPreventMoveDuringCheatCheck;
    private boolean isPreventCommandsDuringCheatCheck;
    private boolean isPreventInteractionDuringCheatCheck;
    private boolean isPreventDropItemDuringCheatCheck;
    private boolean isPreventPickupItemDuringCheatCheck;
    private boolean isPreventTeleportDuringCheatCheck;
    private final List<String> ignoredCommandsDuruingCheatCheck = new ArrayList<>();
    private int defaultCheatCheckTime;
    private final List<String> actionIfQuitDuringCheatCheck = new ArrayList<>();
    private final List<String> actionIfValidCheatCheck = new ArrayList<>();
    private final List<String> actionIfFailedCheatCheck = new ArrayList<>();
    private final List<String> actionIfTimeLeftOnCheatCheck = new ArrayList<>();
    private boolean sendTitleOnCheatCheck;
    private boolean preventKickDuringCheatCheck;
    private boolean preventMuteDuringCheatCheck;
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
    private boolean asyncClientsChecking;
    private boolean blockVanillaClient;
    private final List<String> vanillaClientActions = new ArrayList<>();
    private boolean blockForgeClient;
    private final List<String> forgeClientActions = new ArrayList<>();
    private boolean blockLunarClient;
    private final List<String> lunarClientActions = new ArrayList<>();
    private boolean blockBadlionClient;
    private List<String> badlionClientActions = new ArrayList<>();
    private final List<String> blockedNickFormats = new ArrayList<>();
    private boolean buttonsOnNotifications;
    private boolean hideIpsFromCompletions;
    private boolean replaceMinecraftCommand;

    //Permissions protection start
    private boolean permissionsProtectionEnabled;
    private int permissionsProtectionDelay;
    private boolean permissionsProtectionAutoDeop;
    private final List<String> opAllowedPlayers = new ArrayList<>();
    private final List<String> opProtectionActions = new ArrayList<>();
    private final List<String> protectedGroups = new ArrayList<>();
    private final List<String> groupAllowedPlayers = new ArrayList<>();
    private final List<String> groupProtectionActions = new ArrayList<>();
    private List<String> protectedPermissions = new ArrayList<>();
    private List<String> permissionAllowedPlayers = new ArrayList<>();
    private List<String> permissionsProtectionActions = new ArrayList<>();
    //Permissions protection stop

    //Permissions protection start
    public boolean isPermissionsProtectionEnabled() {
        return permissionsProtectionEnabled;
    }
    private void setPermissionsProtectionEnabled(boolean permissionsProtectionEnabled) {
        this.permissionsProtectionEnabled = permissionsProtectionEnabled;
    }
    public int getPermissionsProtectionDelay() {
        return permissionsProtectionDelay;
    }
    private void setPermissionsProtectionDelay(int permissionsProtectionDelay) {
        this.permissionsProtectionDelay = permissionsProtectionDelay;
    }
    public boolean isPermissionsProtectionAutoDeop() {
        return permissionsProtectionAutoDeop;
    }
    private void setPermissionsProtectionAutoDeop(boolean permissionsProtectionAutoDeop) {
        this.permissionsProtectionAutoDeop = permissionsProtectionAutoDeop;
    }
    public List<String> getOpAllowedPlayers() {
        return opAllowedPlayers;
    }
    private void setOpAllowedPlayers(List<String> opAllowedPlayers) {
        this.opAllowedPlayers.clear();
        this.opAllowedPlayers.addAll(opAllowedPlayers);
    }
    public List<String> getOpProtectionActions() {
        return opProtectionActions;
    }
    public void setOpProtectionActions(List<String> opProtectionActions) {
        this.opProtectionActions.clear();
        this.opProtectionActions.addAll(opProtectionActions);
    }
    public List<String> getProtectedGroups() {
        return protectedGroups;
    }
    private void setProtectedGroups(List<String> protectedGroups) {
        this.protectedGroups.clear();
        this.protectedGroups.addAll(protectedGroups);
    }
    public List<String> getGroupAllowedPlayers() {
        return groupAllowedPlayers;
    }
    private void setGroupAllowedPlayers(List<String> groupAllowedPlayers) {
        this.groupAllowedPlayers.clear();
        this.groupAllowedPlayers.addAll(groupAllowedPlayers);
    }
    public List<String> getGroupProtectionActions() {
        return groupProtectionActions;
    }
    private void setGroupProtectionActions(List<String> groupProtectionActions) {
        this.groupProtectionActions.clear();
        this.groupProtectionActions.addAll(groupProtectionActions);
    }
    private void setProtectedPermissions(List<String> protectedPermissions) {
        this.protectedPermissions = protectedPermissions;
    }
    public List<String> getProtectedPermissions() {
        return protectedPermissions;
    }
    private void setPermissionAllowedPlayers(List<String> permissionAllowedPlayers) {
        this.permissionAllowedPlayers = permissionAllowedPlayers;
    }
    public List<String> getPermissionAllowedPlayers() {
        return permissionAllowedPlayers;
    }
    private void setPermissionsProtectionActions(List<String> permissionsProtectionActions) {
        this.permissionsProtectionActions = permissionsProtectionActions;
    }
    public List<String> getPermissionsProtectionActions() {
        return permissionsProtectionActions;
    }
    //Permissions protection stop
    public boolean isHideIpsFromCompletions() {
        return hideIpsFromCompletions;
    }
    public boolean isReplaceMinecraftCommand() {
        return replaceMinecraftCommand;
    }
    private void setHideIpsFromCompletions(boolean hideIpsFromCompletions) {
        this.hideIpsFromCompletions = hideIpsFromCompletions;
    }
    public void setReplaceMinecraftCommand(boolean replaceMinecraftCommand) {
        this.replaceMinecraftCommand = replaceMinecraftCommand;
    }
    public boolean isButtonsOnNotifications() {
        return buttonsOnNotifications;
    }
    private void setButtonsOnNotifications(boolean buttonsOnNotifications) {
        this.buttonsOnNotifications = buttonsOnNotifications;
    }
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
    public boolean isAsyncClientsChecking() {
        return asyncClientsChecking;
    }
    public void setAsyncClientsChecking(boolean asyncClientsChecking) {
        this.asyncClientsChecking = asyncClientsChecking;
    }
    public boolean isBlockVanillaClient() {
        return blockVanillaClient;
    }
    public List<String> getVanillaClientActions() {
        return vanillaClientActions;
    }
    private void setBlockVanillaClient(boolean blockVanillaClient) {
        this.blockVanillaClient = blockVanillaClient;
    }
    private void setVanillaClientActions(List<String> vanillaClientActions) {
        this.vanillaClientActions.clear();
        this.vanillaClientActions.addAll(vanillaClientActions);
    }
    public boolean isBlockForgeClient() {
        return blockForgeClient;
    }
    private void setBlockForgeClient(boolean blockForgeClient) {
        this.blockForgeClient = blockForgeClient;
    }
    public List<String> getForgeClientActions() {
        return forgeClientActions;
    }
    private void setForgeClientActions(List<String> forgeClientActions) {
        this.forgeClientActions.clear();
        this.forgeClientActions.addAll(forgeClientActions);
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
    public boolean isBlockLunarClient() {
        return blockLunarClient;
    }
    private void setBlockLunarClient(boolean blockLunarClient) {
        this.blockLunarClient = blockLunarClient;
    }
    public List<String> getLunarClientActions() {
        return lunarClientActions;
    }
    private void setLunarClientActions(List<String> lunarClientActions) {
        this.lunarClientActions.clear();
        this.lunarClientActions.addAll(lunarClientActions);
    }
    public boolean isBlockBadlionClient() {
        return blockBadlionClient;
    }
    private void setBlockBadlionClient(boolean blockBadlionClient) {
        this.blockBadlionClient = blockBadlionClient;
    }
    public List<String> getBadlionClientActions() {
        return badlionClientActions;
    }
    private void setBadlionClientActions(List<String> badlionClientActions) {
        this.badlionClientActions.clear();
        this.badlionClientActions.addAll(badlionClientActions);
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
    public boolean isPreventBlockPlaceDuringCheatCheck() {
        return isPreventBlockPlaceDuringCheatCheck;
    }
    private void setPreventBlockPlaceDuringCheatCheck(boolean preventBlockPlaceDuringCheatCheck) {
        isPreventBlockPlaceDuringCheatCheck = preventBlockPlaceDuringCheatCheck;
    }
    public boolean isPreventBlockBreakDuringCheatCheck() {
        return isPreventBlockBreakDuringCheatCheck;
    }
    private void setPreventBlockBreakDuringCheatCheck(boolean preventBlockBreakDuringCheatCheck) {
        isPreventBlockBreakDuringCheatCheck = preventBlockBreakDuringCheatCheck;
    }
    public boolean isPreventIflictDamageDuringCheatCheck() {
        return isPreventIflictDamageDuringCheatCheck;
    }
    private void setPreventIflictDamageDuringCheatCheck(boolean preventIflictDamageDuringCheatCheck) {
        isPreventIflictDamageDuringCheatCheck = preventIflictDamageDuringCheatCheck;
    }
    public boolean isPreventTakingDamageDuringCheatCheck() {
        return isPreventTakingDamageDuringCheatCheck;
    }
    private void setPreventTakingDamageDuringCheatCheck(boolean preventTakingDamageDuringCheatCheck) {
        isPreventTakingDamageDuringCheatCheck = preventTakingDamageDuringCheatCheck;
    }
    public boolean isPreventMoveDuringCheatCheck() {
        return isPreventMoveDuringCheatCheck;
    }
    private void setPreventMoveDuringCheatCheck(boolean preventMoveDuringCheatCheck) {
        isPreventMoveDuringCheatCheck = preventMoveDuringCheatCheck;
    }
    public boolean isPreventCommandsDuringCheatCheck() {
        return isPreventCommandsDuringCheatCheck;
    }
    private void setPreventCommandsDuringCheatCheck(boolean preventCommandsDuringCheatCheck) {
        isPreventCommandsDuringCheatCheck = preventCommandsDuringCheatCheck;
    }
    public boolean isPreventDropItemDuringCheatCheck() {
        return isPreventDropItemDuringCheatCheck;
    }
    private void setPreventDropItemDuringCheatCheck(boolean preventDropItemDuringCheatCheck) {
        isPreventDropItemDuringCheatCheck = preventDropItemDuringCheatCheck;
    }
    public boolean isPreventPickupItemDuringCheatCheck() {
        return isPreventPickupItemDuringCheatCheck;
    }
    private void setPreventPickupItemDuringCheatCheck(boolean preventPickupItemDuringCheatCheck) {
        isPreventPickupItemDuringCheatCheck = preventPickupItemDuringCheatCheck;
    }
    public List<String> getIgnoredCommandsDuruingCheatCheck() {
        return ignoredCommandsDuruingCheatCheck;
    }
    private void setIgnoredCommandsDuruingCheatCheck(List<String> ignoredCommandsDuruingCheatCheck) {
        try {
            this.ignoredCommandsDuruingCheatCheck.clear();
        }catch (NullPointerException ignored) {}
        this.ignoredCommandsDuruingCheatCheck.addAll(ignoredCommandsDuruingCheatCheck);
    }
    public int getDefaultCheatCheckTime() {
        return defaultCheatCheckTime;
    }
    private void setDefaultCheatCheckTime(int defaultCheatCheckTime) {
        this.defaultCheatCheckTime = defaultCheatCheckTime;
    }
    public List<String> getActionIfQuitDuringCheatCheck() {
        return actionIfQuitDuringCheatCheck;
    }
    private void setActionIfQuitDuringCheatCheck(List<String> actionIfQuitDuringCheatCheck) {
        this.actionIfQuitDuringCheatCheck.clear();
        this.actionIfQuitDuringCheatCheck.addAll(actionIfQuitDuringCheatCheck);
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
    private void setSendTitleOnCheatCheck(boolean sendTitleOnCheatCheck) {
        this.sendTitleOnCheatCheck = sendTitleOnCheatCheck;
    }
    public boolean isSendTitleOnCheatCheck() {
        return sendTitleOnCheatCheck;
    }
    public List<String> getActionIfTimeLeftOnCheatCheck() {
        return actionIfTimeLeftOnCheatCheck;
    }
    private void setActionIfTimeLeftOnCheatCheck(List<String> actionIfTimeLeftOnCheatCheck) {
        this.actionIfTimeLeftOnCheatCheck.clear();
        this.actionIfTimeLeftOnCheatCheck.addAll(actionIfTimeLeftOnCheatCheck);
    }
    private void setPreventInteractionDuringCheatCheck(boolean preventInteractionDuringCheatCheck) {
        isPreventInteractionDuringCheatCheck = preventInteractionDuringCheatCheck;
    }
    public boolean isPreventInteractionDuringCheatCheck() {
        return isPreventInteractionDuringCheatCheck;
    }
    private void setPreventKickDuringCheatCheck(boolean preventKickDuringCheatCheck) {
        this.preventKickDuringCheatCheck = preventKickDuringCheatCheck;
    }
    public boolean isPreventKickDuringCheatCheck() {
        return preventKickDuringCheatCheck;
    }
    private void setPreventMuteDuringCheatCheck(boolean preventMuteDuringCheatCheck) {
        this.preventMuteDuringCheatCheck = preventMuteDuringCheatCheck;
    }
    public boolean isPreventMuteDuringCheatCheck() {
        return preventMuteDuringCheatCheck;
    }
    public boolean isPreventTeleportDuringCheatCheck() {
        return isPreventTeleportDuringCheatCheck;
    }
    private void setPreventTeleportDuringCheatCheck(boolean preventTeleportDuringCheatCheck) {
        isPreventTeleportDuringCheatCheck = preventTeleportDuringCheatCheck;
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
        setApiEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.api"));
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
        switch (getFileAccessor().getGeneralConfig().getString("plugin-settings.storage-method")) {
            case "sqlite": {
                setStorageType(StorageType.SQLITE);
                if(!isLessInformation()) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl | Storage] Data storage method installed: %storage_method%".replace("%storage_method%", String.valueOf(getStorageType()))));
                }
                break;
            }
            case "mysql": {
                setStorageType(StorageType.MYSQL);
                if(!isLessInformation()) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl | Storage] Data storage method installed: %storage_method%".replace("%storage_method%", String.valueOf(getStorageType()))));
                }
                break;
            }
            case "h2": {
                setStorageType(StorageType.H2);
                if(!isLessInformation()) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl | Storage] Data storage method installed: %storage_method%".replace("%storage_method%", String.valueOf(getStorageType()))));
                }
                break;
            }
            default: {
                setStorageType(StorageType.SQLITE);
                Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl | Storage] Unknown data storage type is specified (%unknown_method%), using SQLite".replace("%unknown_method%", getFileAccessor().getGeneralConfig().getString("plugin-settings.storage-method"))));
                break;
            }
        }
        TaskManager.preformAsync(() -> {
            setPermissionsProtectionEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.permissions-protection.enabled"));
            if(isPermissionsProtectionEnabled()) {
                if(getFileAccessor().getGeneralConfig().getInt("plugin-settings.permissions-protection.check-delay") < 1) {
                    setPermissionsProtectionDelay(5);
                    Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in 'general.yml' file, 'permissions protection delay' cannot be less than 1 second. I use 5 seconds"));
                } else {
                    setPermissionsProtectionDelay(getFileAccessor().getGeneralConfig().getInt("plugin-settings.permissions-protection.check-delay"));
                }
                setPermissionsProtectionAutoDeop(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.permissions-protection.op-protection.auto-deop"));
                setOpAllowedPlayers(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.permissions-protection.op-protection.allowed-players").replace(" ",""), "[", "]").split(",")));
                setOpProtectionActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.permissions-protection.op-protection.actions"));
                setProtectedGroups(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.permissions-protection.group-protection.protected-groups").replace(" ",""), "[", "]").split(",")));
                setGroupAllowedPlayers(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.permissions-protection.group-protection.allowed-players").replace(" ",""), "[", "]").split(",")));
                setGroupProtectionActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.permissions-protection.group-protection.actions"));
                setProtectedPermissions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.permissions-protection.perms-protection.protected-permissions"));
                setPermissionAllowedPlayers(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.permissions-protection.perms-protection.allowed-player").replace(" ",""), "[", "]").split(",")));
                setPermissionsProtectionActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.permissions-protection.perms-protection.actions"));
            }
            setHideIpsFromCompletions(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.plugin-commands.hide-ips-from-completions"));
            setReplaceMinecraftCommand(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.plugin-commands.replace-default-minecraft-commands"));
            setButtonsOnNotifications(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.notifications.buttons-on-notifications"));
            setBlockWorldDownloader(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.world-downloader.block"));
            if(isBlockWorldDownloader()) {
                setActionsOnWDL(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.join-settings.clients-control.world-downloader.actions"));
            }
            setBanTimeExpired(getFileAccessor().getGeneralConfig().getString("plugin-settings.reason-settings.ban-time-left"));
            setMuteTimeExpired(getFileAccessor().getGeneralConfig().getString("plugin-settings.reason-settings.mute-time-left"));
            setGlobalLanguage(getFileAccessor().getGeneralConfig().getString("plugin-settings.global-language"));
            setDisabledCommandsWhenMuted(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.chat-settings.disabled-commands-when-muted"), "[", "]").split(", ")));
            setSendTitleWhenMuted(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.title-settings.send-when-muted"));
            setSendTitleWhenUnmuted(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.title-settings.send-when-unmuted"));
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
            setAsyncClientsChecking(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.async-checking"));
            setBlockVanillaClient(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.vanilla.block"));
            if(isBlockVanillaClient()) {
                setVanillaClientActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.join-settings.clients-control.vanilla.actions"));
            }
            setBlockForgeClient(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.forge.block"));
            if(isBlockForgeClient()) {
                setForgeClientActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.join-settings.clients-control.forge.actions"));
            }
            setBlockLunarClient(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.lunarclient.block"));
            if(isBlockLunarClient()) {
                setLunarClientActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.join-settings.clients-control.lunarclient.actions"));
            }
            setBlockBadlionClient(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.badlion.block"));
            if(isBlockBadlionClient()) {
                setBadlionClientActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.join-settings.clients-control.badlion.actions"));
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
            setCheatCheckFunctionEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.enabled"));
            if(isCheatCheckFunctionEnabled()) {
                setPreventBlockPlaceDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.block-place"));
                setPreventBlockBreakDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.block-break"));
                setPreventIflictDamageDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.inflict-damage"));
                setPreventTakingDamageDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.take-damage"));
                setPreventMoveDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.move"));
                setPreventInteractionDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.interact"));
                setPreventCommandsDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.use-commands"));
                setPreventDropItemDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.item-drop"));
                setPreventPickupItemDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.item-pickup"));
                setPreventKickDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevent-player-kick"));
                setPreventMuteDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevent-player-mute"));
                setIgnoredCommandsDuruingCheatCheck(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.cheat-checks-settings.whitelisted-commands"), "[", "]").split(", ")));
                setDefaultCheatCheckTime(getFileAccessor().getGeneralConfig().getInt("plugin-settings.cheat-checks-settings.default-check-time"));
                setActionIfQuitDuringCheatCheck(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.cheat-checks-settings.actions.if-player-quit"));
                setActionIfFailedCheatCheck(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.cheat-checks-settings.actions.if-check-fails"));
                setActionIfValidCheatCheck(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.cheat-checks-settings.actions.if-check-is-valid"));
                setActionIfTimeLeftOnCheatCheck(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.cheat-checks-settings.actions.if-time-left"));
                setSendTitleOnCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.send-title"));
                setPreventTeleportDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.teleport"));
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
            if(!isLessInformation()) {
                Bukkit.getConsoleSender().sendMessage(setColors(isApiEnabled() ? "&a[FunctionalServerControl | API Loading] API usage is allowed by configuration settings" : "&e[FunctionalServerControl | API Loading] API usage is prohibited by configuration settings (This is not an error)"));
            }
            //Tasks
            if(isDupeIdModeEnabled()) {
                new DupeIpTask().runTaskTimerAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), 0, getDupeIpTimerDelay() * 20L);
            }
            new MuteGlobalTask().runTaskTimerAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), 0, 20L);
            new CooldownsTask().runTaskTimerAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), 0, 20L);
            if(isPermissionsProtectionEnabled()) {
                new PermissionsControlTask().runTaskTimerAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), 0, getPermissionsProtectionDelay() * 20L);
            }
            if(getChatSettings().isFunctionEnabled()) {
                new ChatTask().runTaskTimerAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), 0, 20L);
            }
            Bukkit.getScheduler().runTaskTimerAsynchronously(FunctionalServerControl.getPlugin(FunctionalServerControl.class), new ServerInfoCollector(), 0, 20L);
            //Tasks
            OtherUtils.loadCachedPlayers();
        });
        return;
    }

    public void reloadConfig() {
        setLessInformation(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.less-information"));
        setButtonsOnNotifications(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.notifications.buttons-on-notifications"));
        setBanTimeExpired(getFileAccessor().getGeneralConfig().getString("plugin-settings.reason-settings.ban-time-left"));
        setMuteTimeExpired(getFileAccessor().getGeneralConfig().getString("plugin-settings.reason-settings.mute-time-left"));
        setHideIpsFromCompletions(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.plugin-commands.hide-ips-from-completions"));
        setReplaceMinecraftCommand(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.plugin-commands.replace-default-minecraft-commands"));
        setBlockWorldDownloader(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.world-downloader.block"));
        setAsyncClientsChecking(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.async-checking"));
        if(isBlockWorldDownloader()) {
            setActionsOnWDL(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.join-settings.clients-control.world-downloader.actions"));
        }
        setBlockVanillaClient(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.vanilla.block"));
        if(isBlockVanillaClient()) {
            setVanillaClientActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.join-settings.clients-control.vanilla.actions"));
        }
        setBlockForgeClient(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.forge.block"));
        if(isBlockForgeClient()) {
            setForgeClientActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.join-settings.clients-control.forge.actions"));
        }
        setBlockLunarClient(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.lunarclient.block"));
        if(isBlockLunarClient()) {
            setLunarClientActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.join-settings.clients-control.lunarclient.actions"));
        }
        setBlockBadlionClient(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.clients-control.badlion.block"));
        if(isBlockBadlionClient()) {
            setBadlionClientActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.join-settings.clients-control.badlion.actions"));
        }
        if(isPermissionsProtectionEnabled()) {
            if(getFileAccessor().getGeneralConfig().getInt("plugin-settings.permissions-protection.check-delay") < 1) {
                setPermissionsProtectionDelay(5);
                Bukkit.getConsoleSender().sendMessage(setColors("&c[FunctionalServerControl] Error in 'general.yml' file, 'permissions protection delay' cannot be less than 1 second. I use 5 seconds"));
            } else {
                setPermissionsProtectionDelay(getFileAccessor().getGeneralConfig().getInt("plugin-settings.permissions-protection.check-delay"));
            }
            setPermissionsProtectionAutoDeop(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.permissions-protection.op-protection.auto-deop"));
            setOpAllowedPlayers(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.permissions-protection.op-protection.allowed-players").replace(" ",""), "[", "]").split(",")));
            setOpProtectionActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.permissions-protection.op-protection.actions"));
            setProtectedGroups(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.permissions-protection.group-protection.protected-groups").replace(" ",""), "[", "]").split(",")));
            setGroupAllowedPlayers(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.permissions-protection.group-protection.allowed-players").replace(" ",""), "[", "]").split(",")));
            setGroupProtectionActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.permissions-protection.group-protection.actions"));
            setProtectedPermissions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.permissions-protection.perms-protection.protected-permissions"));
            setPermissionAllowedPlayers(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.permissions-protection.perms-protection.allowed-player").replace(" ",""), "[", "]").split(",")));
            setPermissionsProtectionActions(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.permissions-protection.perms-protection.actions"));
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
        setCheatCheckFunctionEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.enabled"));
        if(isCheatCheckFunctionEnabled()) {
            setPreventBlockPlaceDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.block-place"));
            setPreventBlockBreakDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.block-break"));
            setPreventIflictDamageDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.inflict-damage"));
            setPreventTakingDamageDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.take-damage"));
            setPreventMoveDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.move"));
            setPreventCommandsDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.use-commands"));
            setPreventInteractionDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.interact"));
            setIgnoredCommandsDuruingCheatCheck(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.cheat-checks-settings.whitelisted-commands"), "[", "]").split(", ")));
            setDefaultCheatCheckTime(getFileAccessor().getGeneralConfig().getInt("plugin-settings.cheat-checks-settings.default-check-time"));
            setActionIfQuitDuringCheatCheck(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.cheat-checks-settings.actions.if-player-quit"));
            setActionIfFailedCheatCheck(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.cheat-checks-settings.actions.if-check-fails"));
            setActionIfValidCheatCheck(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.cheat-checks-settings.actions.if-check-is-valid"));
            setActionIfTimeLeftOnCheatCheck(getFileAccessor().getGeneralConfig().getStringList("plugin-settings.cheat-checks-settings.actions.if-time-left"));
            setSendTitleOnCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.send-title"));
            setPreventKickDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevent-player-kick"));
            setPreventMuteDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevent-player-mute"));
            setPreventTeleportDuringCheatCheck(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.cheat-checks-settings.prevents.teleport"));
        }
        setNicksControlEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.nicks-control.enabled"));
        setNotifyConsoleWhenNickNameBlocked(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.nicks-control.notify-console"));
        setNickFormatControlEnabled(getFileAccessor().getGeneralConfig().getBoolean("plugin-settings.join-settings.nicks-control.nick-format-control.enabled"));
        if(isNickFormatControlEnabled()) {
            setBlockedNickFormats(Arrays.asList(StringUtils.substringBetween(getFileAccessor().getGeneralConfig().getString("plugin-settings.join-settings.nicks-control.nick-format-control.blocked-formats"), "[", "]").split(", ")));
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
