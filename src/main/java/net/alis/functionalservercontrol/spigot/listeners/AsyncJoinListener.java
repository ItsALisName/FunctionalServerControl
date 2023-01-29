package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.api.FunctionalApi;
import net.alis.functionalservercontrol.api.interfaces.FunctionalPlayer;
import net.alis.functionalservercontrol.api.interfaces.OfflineFunctionalPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.entity.OfflineFunctionalCraftPlayer;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FID;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.FunctionalStatistics;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.checkers.InternalBanChecker;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.checkers.InternalMuteChecker;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.data.WritableOfflinePlayerMeta;
import net.alis.functionalservercontrol.api.naf.v1_10_0.util.registerer.OfflinePlayerRegisterer;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.Component;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.api.enums.BanType;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.ban.UnbanManager;
import net.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import static net.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.*;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.*;
import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

/**
 * The class responsible for controlling the entry of players (Banned, with invalid IP addresses, with invalid nicknames)
 */
public class AsyncJoinListener implements Listener {
    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
    private final UnbanManager unbanManager = new UnbanManager();


    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String playerName = event.getName();
        FID fid = new FID(playerName);
        String address = event.getAddress().getHostAddress();
        BaseManager.getBaseManager().insertIntoAllPlayers(playerName, event.getUniqueId(), address, fid);
        BaseManager.getBaseManager().insertIntoPlayersPunishInfo(fid);
        OfflineFunctionalPlayer player = null;
        if (BaseManager.getBaseManager().updateAllPlayers(playerName, event.getUniqueId(), address, fid)) {
            if (OfflineFunctionalPlayer.get(fid) == null) {
                WritableOfflinePlayerMeta meta = new WritableOfflinePlayerMeta(
                        playerName,
                        event.getUniqueId(),
                        fid,
                        Bukkit.getOfflinePlayer(event.getUniqueId()),
                        InternalBanChecker.isPlayerBanned(fid),
                        InternalMuteChecker.isPlayerMuted(fid),
                        new FunctionalStatistics.PlayerStats(fid),
                        new FunctionalStatistics.AdminStats(fid)
                );
                OfflineFunctionalCraftPlayer craftPlayer = new OfflineFunctionalCraftPlayer(meta);
                new OfflinePlayerRegisterer(craftPlayer).register();
                player = craftPlayer;
            } else {
                player = OfflineFunctionalPlayer.get(fid);
                new OfflinePlayerRegisterer(player).unregister();
                WritableOfflinePlayerMeta meta = new WritableOfflinePlayerMeta(
                        playerName,
                        event.getUniqueId(),
                        fid,
                        Bukkit.getOfflinePlayer(event.getUniqueId()),
                        InternalBanChecker.isPlayerBanned(fid),
                        InternalMuteChecker.isPlayerMuted(fid),
                        new FunctionalStatistics.PlayerStats(fid),
                        new FunctionalStatistics.AdminStats(fid)
                );
                OfflineFunctionalCraftPlayer craftPlayer = new OfflineFunctionalCraftPlayer(meta);
                new OfflinePlayerRegisterer(craftPlayer).register();
                player = craftPlayer;
            }
        } else {
            if (OfflineFunctionalPlayer.get(fid) == null) {
                WritableOfflinePlayerMeta meta = new WritableOfflinePlayerMeta(
                        playerName,
                        event.getUniqueId(),
                        fid,
                        Bukkit.getOfflinePlayer(event.getUniqueId()),
                        InternalBanChecker.isPlayerBanned(fid),
                        InternalMuteChecker.isPlayerMuted(fid),
                        new FunctionalStatistics.PlayerStats(fid),
                        new FunctionalStatistics.AdminStats(fid)
                );
                OfflineFunctionalCraftPlayer craftPlayer = new OfflineFunctionalCraftPlayer(meta);
                new OfflinePlayerRegisterer(craftPlayer).register();
                player = craftPlayer;
            }
        }
        if(getProtectionSettings().isAccountProtectionEnabled()) {
            if(getProtectionSettings().getProtectedAccounts().containsKey(TextUtils.stringToMonolith(playerName))) {
                if(!getProtectionSettings().getProtectedAccounts().get(playerName).equalsIgnoreCase(address)) {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, setColors(String.join("\n",getFileAccessor().getLang().getStringList("kick-format"))
                            .replace("%1$f", getFileAccessor().getProtectionConfig().getString("accounts-protection.kick-message"))
                            .replace("%2$f", getGlobalVariables().getConsoleVariableName())
                    ));
                    if(getProtectionSettings().isNotifyAdminsAboutProtectedAccount()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.protected-account-join")
                                .replace("%1$f", playerName)
                                .replace("%2$f", getProtectionSettings().getProtectedAccounts().get(playerName))
                                .replace("%3$f", address)
                        ));
                        for(FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.protected-account")) {
                                admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.protected-account-join")
                                        .replace("%1$f", playerName)
                                        .replace("%2$f", getProtectionSettings().getProtectedAccounts().get(playerName))
                                        .replace("%3$f", address)
                                ));
                            }
                        }
                    }
                    return;
                }
            }
        }

        if(getConfigSettings().isIpsControlEnabled()) {
            if(getConfigSettings().getBlockedIps().contains(address)) {
                event.disallow(
                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        setColors(String.join("\n", getFileAccessor().getLang().getStringList("blocked-ip-kick-format")).replace("%1$f", address))
                );
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                if(getConfigSettings().notifyConsoleWhenIPBlocked()) {
                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.blocked-ip-notify").replace("%1$f", address)));
                }
                return;
            }
        }

        if(OtherUtils.verifyNickNameFormat(playerName)) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    setColors(String.join("\n", getFileAccessor().getLang().getStringList("blocked-nickname-kick-format")).replace("%1$f", getConfigSettings().getGlobalLanguage().equalsIgnoreCase("ru_RU") ? "Недопустимый формат" : "Invalid format"))
            );
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            if(getConfigSettings().notifyConsoleWhenNickNameBlocked()) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.blocked-nickname-notify").replace("%1$f", playerName)));
            }
            return;
        }

        if(getConfigSettings().isNicksControlEnabled()) {
            for(String name : getConfigSettings().getBlockedNickNames()) {
                if(getConfigSettings().getNicknameCheckMode().equalsIgnoreCase("equals")) {
                    if(playerName.equalsIgnoreCase(name)) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("blocked-nickname-kick-format")).replace("%1$f", name))
                        );
                        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                        if(getConfigSettings().notifyConsoleWhenNickNameBlocked()) {
                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.blocked-nickname-notify").replace("%1$f", playerName)));
                        }
                        return;
                    }
                }
                if(getConfigSettings().getNicknameCheckMode().equalsIgnoreCase("contains")) {
                    if(playerName.contains(name)) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("blocked-nickname-kick-format")).replace("%1$f", name))
                        );
                        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                        if(getConfigSettings().notifyConsoleWhenNickNameBlocked()) {
                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.blocked-nickname-notify").replace("%1$f", playerName)));
                        }
                        return;
                    }
                }
            }
        }
        if(getConfigSettings().isAllowedUseRamAsContainer()) {

            if(getBannedPlayersContainer().getFidsContainer().contains(fid)) {
                int indexOf = getBannedPlayersContainer().getFidsContainer().indexOf(fid);
                if((getBannedPlayersContainer().getBanTypesContainer().get(indexOf) == BanType.TIMED_IP
                || getBannedPlayersContainer().getBanTypesContainer().get(indexOf) == BanType.PERMANENT_IP)
                && !getBannedPlayersContainer().getIpContainer().get(indexOf).equalsIgnoreCase(address)) {
                    long currentTime = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                    BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(indexOf);
                    String reason = getBannedPlayersContainer().getReasonContainer().get(indexOf);
                    String id = getBannedPlayersContainer().getIdsContainer().get(indexOf);
                    String timeAndDate = getBannedPlayersContainer().getRealBanDateContainer().get(indexOf) + ", " + getBannedPlayersContainer().getRealBanTimeContainer().get(indexOf);
                    String initiatorName = getBannedPlayersContainer().getInitiatorNameContainer().get(indexOf);
                    String realDate = getBannedPlayersContainer().getRealBanDateContainer().get(indexOf);
                    String realTime = getBannedPlayersContainer().getRealBanTimeContainer().get(indexOf);
                    getBanContainerManager().removeFromBanContainer("-id", id);
                    getBannedPlayersContainer().addToBansContainer(
                            id,
                            address,
                            playerName,
                            initiatorName,
                            reason,
                            banType,
                            realDate,
                            realTime,
                            String.valueOf(event.getUniqueId()),
                            currentTime,
                            fid
                    );
                    BaseManager.getBaseManager().deleteFromBannedPlayers("-id", id);
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, address, playerName, initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime, fid);
                    if(banType == BanType.PERMANENT_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                        );
                        this.notifyAdmins(player, currentTime);
                        return;
                    }

                    if(banType != BanType.PERMANENT_IP){
                        if (System.currentTimeMillis() >= currentTime) {
                            this.unbanManager.preformUnban(player, "The Ban time has expired");
                            return;
                        }
                    }

                    if(banType == BanType.TIMED_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))))
                        );
                        this.notifyAdmins(player, currentTime);
                        return;
                    }
                }
            }

            if(getBannedPlayersContainer().getIpContainer().contains(address)) {
                int indexOf = getBannedPlayersContainer().getIpContainer().indexOf(address);
                if((getBannedPlayersContainer().getBanTypesContainer().get(indexOf) == BanType.PERMANENT_IP
                || getBannedPlayersContainer().getBanTypesContainer().get(indexOf) == BanType.TIMED_IP)
                && !getBannedPlayersContainer().getNameContainer().get(indexOf).equalsIgnoreCase(playerName)) {
                    long currentTime = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                    BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(indexOf);
                    String reason = getBannedPlayersContainer().getReasonContainer().get(indexOf);
                    String id = getBannedPlayersContainer().getIdsContainer().get(indexOf);
                    String timeAndDate = getBannedPlayersContainer().getRealBanDateContainer().get(indexOf) + ", " + getBannedPlayersContainer().getRealBanTimeContainer().get(indexOf);
                    String initiatorName = getBannedPlayersContainer().getInitiatorNameContainer().get(indexOf);
                    String realDate = getBannedPlayersContainer().getRealBanDateContainer().get(indexOf);
                    String realTime = getBannedPlayersContainer().getRealBanTimeContainer().get(indexOf);
                    getBanContainerManager().removeFromBanContainer("-id", id);
                    getBannedPlayersContainer().addToBansContainer(
                            id,
                            address,
                            playerName,
                            initiatorName,
                            reason,
                            banType,
                            realDate,
                            realTime,
                            String.valueOf(event.getUniqueId()),
                            currentTime,
                            fid
                    );
                    BaseManager.getBaseManager().deleteFromBannedPlayers("-id", id);
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, address, playerName, initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime, fid);
                    if(banType != BanType.PERMANENT_IP){
                        if (System.currentTimeMillis() >= currentTime) {
                            this.unbanManager.preformUnban(player, "The Ban time has expired");
                            event.allow();
                            return;
                        }
                    }

                    if(banType == BanType.PERMANENT_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                        );
                        this.notifyAdmins(player, currentTime);
                        return;
                    }
                    if(banType == BanType.TIMED_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))))
                        );
                        this.notifyAdmins(player, currentTime);
                        return;
                    }
                }
                long currentTime = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(indexOf);
                if(banType != BanType.PERMANENT_NOT_IP && banType != BanType.PERMANENT_IP){
                    if (System.currentTimeMillis() >= currentTime) {
                        this.unbanManager.preformUnban(OfflineFunctionalPlayer.get(fid), "The Ban time has expired");
                        event.allow();
                        return;
                    }
                }
                String reason = getBannedPlayersContainer().getReasonContainer().get(indexOf);
                String id = getBannedPlayersContainer().getIdsContainer().get(indexOf);
                String timeAndDate = getBannedPlayersContainer().getRealBanDateContainer().get(indexOf) + ", " + getBannedPlayersContainer().getRealBanTimeContainer().get(indexOf);
                String initiatorName = getBannedPlayersContainer().getInitiatorNameContainer().get(indexOf);
                if(banType == BanType.PERMANENT_NOT_IP) {
                    if (getBannedPlayersContainer().getUUIDContainer().contains(String.valueOf(event.getUniqueId()))) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                        );
                    }
                    this.notifyAdmins(player, currentTime);
                    return;
                }
                if(banType == BanType.PERMANENT_IP) {
                    event.disallow(
                            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                            setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                    );
                    this.notifyAdmins(player, currentTime);
                    return;
                }
                if(banType == BanType.TIMED_NOT_IP) {
                    if (getBannedPlayersContainer().getUUIDContainer().contains(String.valueOf(event.getUniqueId()))) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))))
                        );
                    }
                    this.notifyAdmins(player, currentTime);
                    return;
                }
                if(banType == BanType.TIMED_IP) {
                    event.disallow(
                            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                            setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime)))
                    ));
                    this.notifyAdmins(player, currentTime);
                    return;
                }
                return;
            }


            if(getBannedPlayersContainer().getFidsContainer().contains(fid)) {
                int indexOf = getBannedPlayersContainer().getFidsContainer().indexOf(fid);
                BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(indexOf);
                long currentTime = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                if (banType != BanType.PERMANENT_IP && banType != BanType.PERMANENT_NOT_IP && System.currentTimeMillis() >= currentTime) {
                    this.unbanManager.preformUnban(player, "The Ban time has expired");
                    event.allow();
                    return;
                }
                if(banType == BanType.PERMANENT_NOT_IP || banType == BanType.TIMED_NOT_IP) {
                    String reason = getBannedPlayersContainer().getReasonContainer().get(indexOf);
                    String id = getBannedPlayersContainer().getIdsContainer().get(indexOf);
                    String timeAndDate = getBannedPlayersContainer().getRealBanDateContainer().get(indexOf) + ", " + getBannedPlayersContainer().getRealBanTimeContainer().get(indexOf);
                    String initiatorName = getBannedPlayersContainer().getInitiatorNameContainer().get(indexOf);
                    if(banType == BanType.PERMANENT_NOT_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                        );
                        this.notifyAdmins(player, currentTime);
                        return;
                    }
                    if(banType == BanType.TIMED_NOT_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))))
                        );
                        this.notifyAdmins(player, currentTime);
                        return;
                    }
                }
            }
        } else {
            if(BaseManager.getBaseManager().getBannedFids().contains(fid)) {
                int indexOf = BaseManager.getBaseManager().getBannedFids().indexOf(fid);
                if((BaseManager.getBaseManager().getBanTypes().get(indexOf) == BanType.TIMED_IP
                        || BaseManager.getBaseManager().getBanTypes().get(indexOf) == BanType.PERMANENT_IP)
                        && !BaseManager.getBaseManager().getBannedIps().get(indexOf).equalsIgnoreCase(address)) {
                    long currentTime = BaseManager.getBaseManager().getUnbanTimes().get(indexOf);
                    BanType banType = BaseManager.getBaseManager().getBanTypes().get(indexOf);
                    if(banType != BanType.PERMANENT_IP){
                        if (System.currentTimeMillis() >= currentTime) {

                            this.unbanManager.preformUnban(player, "The Ban time has expired");
                            event.allow();
                            return;
                        }
                    }
                    String reason = BaseManager.getBaseManager().getBanReasons().get(indexOf);
                    String id = BaseManager.getBaseManager().getBannedIds().get(indexOf);
                    String timeAndDate = BaseManager.getBaseManager().getBansDates().get(indexOf) + ", " + BaseManager.getBaseManager().getBansTimes().get(indexOf);
                    String initiatorName = BaseManager.getBaseManager().getBanInitiators().get(indexOf);
                    String realDate = BaseManager.getBaseManager().getBansDates().get(indexOf);
                    String realTime = BaseManager.getBaseManager().getBansTimes().get(indexOf);
                    BaseManager.getBaseManager().deleteFromBannedPlayers("-id", id);
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, address, playerName, initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime, fid);
                    if(banType == BanType.PERMANENT_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                        );
                        this.notifyAdmins(player, currentTime);
                        return;
                    }
                    if(banType == BanType.TIMED_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))))
                        );
                        this.notifyAdmins(player, currentTime);
                        return;
                    }
                }
            }

            if(BaseManager.getBaseManager().getBannedIps().contains(address)) {
                int indexOf = BaseManager.getBaseManager().getBannedIps().indexOf(address);
                if((BaseManager.getBaseManager().getBanTypes().get(indexOf) == BanType.PERMANENT_IP
                        || BaseManager.getBaseManager().getBanTypes().get(indexOf) == BanType.TIMED_IP)
                        && !BaseManager.getBaseManager().getBannedPlayersNames().get(indexOf).equalsIgnoreCase(playerName)) {
                    long currentTime = BaseManager.getBaseManager().getUnbanTimes().get(indexOf);
                    BanType banType = BaseManager.getBaseManager().getBanTypes().get(indexOf);
                    if(banType != BanType.PERMANENT_IP){
                        if (System.currentTimeMillis() >= currentTime) {

                            this.unbanManager.preformUnban(player, "The Ban time has expired");
                            event.allow();
                            return;
                        }
                    }
                    String reason = BaseManager.getBaseManager().getBanReasons().get(indexOf);
                    String id = BaseManager.getBaseManager().getBannedIds().get(indexOf);
                    String timeAndDate = BaseManager.getBaseManager().getBansDates().get(indexOf) + ", " + BaseManager.getBaseManager().getBansTimes().get(indexOf);
                    String initiatorName = BaseManager.getBaseManager().getBanInitiators().get(indexOf);
                    String realDate = BaseManager.getBaseManager().getBansDates().get(indexOf);
                    String realTime = BaseManager.getBaseManager().getBansTimes().get(indexOf);
                    BaseManager.getBaseManager().deleteFromBannedPlayers("-id", id);
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, address, playerName, initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime, fid);
                    if(banType == BanType.PERMANENT_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                        );
                        this.notifyAdmins(player, currentTime);
                        return;
                    }
                    if(banType == BanType.TIMED_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))))
                        );
                        this.notifyAdmins(player, currentTime);
                        return;
                    }
                }
                long currentTime = BaseManager.getBaseManager().getUnbanTimes().get(indexOf);
                BanType banType = BaseManager.getBaseManager().getBanTypes().get(indexOf);
                if(banType != BanType.PERMANENT_NOT_IP && banType != BanType.PERMANENT_IP){
                    if (System.currentTimeMillis() >= currentTime) {

                        this.unbanManager.preformUnban(player, "The Ban time has expired");
                        event.allow();
                        return;
                    }
                }
                String reason = BaseManager.getBaseManager().getBanReasons().get(indexOf);
                String id = BaseManager.getBaseManager().getBannedIds().get(indexOf);
                String timeAndDate = BaseManager.getBaseManager().getBansDates().get(indexOf) + ", " + BaseManager.getBaseManager().getBansTimes().get(indexOf);
                String initiatorName = BaseManager.getBaseManager().getBanInitiators().get(indexOf);
                if(banType == BanType.PERMANENT_NOT_IP) {
                    if (getBannedPlayersContainer().getUUIDContainer().contains(String.valueOf(event.getUniqueId()))) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                        );
                    }
                    this.notifyAdmins(player, currentTime);
                    return;
                }
                if(banType == BanType.PERMANENT_IP) {
                    event.disallow(
                            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                            setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                    );
                    this.notifyAdmins(player, currentTime);
                    return;
                }
                if(banType == BanType.TIMED_NOT_IP) {
                    if (getBannedPlayersContainer().getUUIDContainer().contains(String.valueOf(event.getUniqueId()))) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))))
                        );
                    }
                    this.notifyAdmins(player, currentTime);
                    return;
                }
                if(banType == BanType.TIMED_IP) {
                    event.disallow(
                            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                            setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime)))
                            ));
                    this.notifyAdmins(player, currentTime);
                    return;
                }
                return;
            }


            if(BaseManager.getBaseManager().getBannedFids().contains(fid)) {
                int indexOf = BaseManager.getBaseManager().getBannedFids().indexOf(fid);
                BanType banType = BaseManager.getBaseManager().getBanTypes().get(indexOf);
                long currentTime = BaseManager.getBaseManager().getUnbanTimes().get(indexOf);
                if (System.currentTimeMillis() >= currentTime) {

                    this.unbanManager.preformUnban(player, "The Ban time has expired");
                    event.allow();
                    return;
                }
                if(banType == BanType.PERMANENT_NOT_IP || banType == BanType.TIMED_NOT_IP) {
                    String reason = BaseManager.getBaseManager().getBanReasons().get(indexOf);
                    String id = BaseManager.getBaseManager().getBannedIds().get(indexOf);
                    String timeAndDate = BaseManager.getBaseManager().getBansDates().get(indexOf) + ", " + BaseManager.getBaseManager().getBansTimes().get(indexOf);
                    String initiatorName = BaseManager.getBaseManager().getBanInitiators().get(indexOf);
                    if(banType == BanType.PERMANENT_NOT_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                        );
                        this.notifyAdmins(player, currentTime);
                        return;
                    }
                    if(banType == BanType.TIMED_NOT_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))))
                        );
                        this.notifyAdmins(player, currentTime);
                        return;
                    }
                }
            }
        }
    }

    private void notifyAdmins(OfflineFunctionalPlayer player, long timeLeft) {
        TaskManager.preformAsync(() -> {
            String convertedTime = getGlobalVariables().getVariableNever();
            if(timeLeft > 0) {
                convertedTime = this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(timeLeft));
            }
            if (getConfigSettings().isConsoleNotification()) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", convertedTime)));
            }
            if(getConfigSettings().isPlayersNotification()) {
                for (FunctionalPlayer admin : FunctionalApi.getOnlinePlayers()) {
                    if(getConfigSettings().isButtonsOnNotifications()) {
                        admin.expansion().message(Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", convertedTime)), player)
                                        .append(Component.addPardonButtons(admin, player.nickname())).translateDefaultColorCodes()
                        );
                    } else {
                        admin.expansion().message(Component.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.nickname()).replace("%2$f", convertedTime)), player).translateDefaultColorCodes());
                        continue;
                    }
                }
            }
        });
    }

}
