package net.alis.functionalservercontrol.spigot.listeners;

import net.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.MD5TextUtils;
import net.alis.functionalservercontrol.spigot.managers.BaseManager;
import net.alis.functionalservercontrol.spigot.managers.TaskManager;
import net.alis.functionalservercontrol.api.enums.BanType;
import net.alis.functionalservercontrol.spigot.additional.textcomponents.AdventureApiUtils;
import net.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import net.alis.functionalservercontrol.spigot.additional.misc.TextUtils;
import net.alis.functionalservercontrol.spigot.managers.ban.UnbanManager;
import net.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
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
        OfflinePlayer player = CoreAdapter.getAdapter().getOfflinePlayer(event.getUniqueId());
        String playerName = event.getName();
        String address = event.getAddress().getHostAddress();
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
                        for(Player admin : Bukkit.getOnlinePlayers()) {
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

            if(getBannedPlayersContainer().getNameContainer().contains(playerName) && getBannedPlayersContainer().getUUIDContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(playerName)).equalsIgnoreCase(String.valueOf(event.getUniqueId()))) {
                if((getBannedPlayersContainer().getBanTypesContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(playerName)) == BanType.TIMED_IP
                || getBannedPlayersContainer().getBanTypesContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(playerName)) == BanType.PERMANENT_IP)
                && !getBannedPlayersContainer().getIpContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(playerName)).equalsIgnoreCase(address)) {
                    long currentTime = getBannedPlayersContainer().getBanTimeContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(playerName));
                    BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(playerName));
                    String reason = getBannedPlayersContainer().getReasonContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(playerName));
                    String id = getBannedPlayersContainer().getIdsContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(playerName));
                    String timeAndDate = getBannedPlayersContainer().getRealBanDateContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(playerName)) + ", " + getBannedPlayersContainer().getRealBanTimeContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(playerName));
                    String initiatorName = getBannedPlayersContainer().getInitiatorNameContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(playerName));
                    String realDate = getBannedPlayersContainer().getRealBanDateContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(playerName));
                    String realTime = getBannedPlayersContainer().getRealBanTimeContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(playerName));
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
                            currentTime
                    );
                    BaseManager.getBaseManager().deleteFromBannedPlayers("-id", id);
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, address, playerName, initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime);
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

                if((getBannedPlayersContainer().getBanTypesContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(address)) == BanType.PERMANENT_IP
                || getBannedPlayersContainer().getBanTypesContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(address)) == BanType.TIMED_IP)
                && !getBannedPlayersContainer().getNameContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(address)).equalsIgnoreCase(playerName)) {
                    int indexOf = getBannedPlayersContainer().getIpContainer().indexOf(address);
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
                            currentTime
                    );
                    BaseManager.getBaseManager().deleteFromBannedPlayers("-id", id);
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, address, playerName, initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime);
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
                int indexOf = getBannedPlayersContainer().getIpContainer().indexOf(address);
                long currentTime = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(indexOf);
                if(banType != BanType.PERMANENT_NOT_IP && banType != BanType.PERMANENT_IP){
                    if (System.currentTimeMillis() >= currentTime) {
                        
                        this.unbanManager.preformUnban(player, "The Ban time has expired");
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


            if(getBannedPlayersContainer().getUUIDContainer().contains(String.valueOf(event.getUniqueId()))) {
                int indexOf = getBannedPlayersContainer().getUUIDContainer().indexOf(String.valueOf(event.getUniqueId()));
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
            if(BaseManager.getBaseManager().getBannedPlayersNames().contains(playerName) && BaseManager.getBaseManager().getBannedUUIDs().get(BaseManager.getBaseManager().getBannedPlayersNames().indexOf(playerName)).equalsIgnoreCase(String.valueOf(event.getUniqueId()))) {
                if((BaseManager.getBaseManager().getBanTypes().get(BaseManager.getBaseManager().getBannedPlayersNames().indexOf(playerName)) == BanType.TIMED_IP
                        || BaseManager.getBaseManager().getBanTypes().get(BaseManager.getBaseManager().getBannedPlayersNames().indexOf(playerName)) == BanType.PERMANENT_IP)
                        && !BaseManager.getBaseManager().getBannedIps().get(BaseManager.getBaseManager().getBannedPlayersNames().indexOf(playerName)).equalsIgnoreCase(address)) {
                    int indexOf = BaseManager.getBaseManager().getBannedPlayersNames().indexOf(playerName);
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
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, address, playerName, initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime);
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

                if((BaseManager.getBaseManager().getBanTypes().get(BaseManager.getBaseManager().getBannedIps().indexOf(address)) == BanType.PERMANENT_IP
                        || BaseManager.getBaseManager().getBanTypes().get(BaseManager.getBaseManager().getBannedIps().indexOf(address)) == BanType.TIMED_IP)
                        && !BaseManager.getBaseManager().getBannedPlayersNames().get(BaseManager.getBaseManager().getBannedIps().indexOf(address)).equalsIgnoreCase(playerName)) {
                    int indexOf = BaseManager.getBaseManager().getBannedIps().indexOf(address);
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
                    BaseManager.getBaseManager().insertIntoBannedPlayers(id, address, playerName, initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime);
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
                int indexOf = BaseManager.getBaseManager().getBannedIps().indexOf(address);
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


            if(BaseManager.getBaseManager().getBannedUUIDs().contains(String.valueOf(event.getUniqueId()))) {
                int indexOf = BaseManager.getBaseManager().getBannedUUIDs().indexOf(String.valueOf(event.getUniqueId()));
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

    private void notifyAdmins(OfflinePlayer player, long timeLeft) {
        TaskManager.preformAsync(() -> {
            String convertedTime = getGlobalVariables().getVariableNever();
            if(timeLeft > 0) {
                convertedTime = this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(timeLeft));
            }
            if (getConfigSettings().isConsoleNotification()) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", convertedTime)));
            }
            if(getConfigSettings().isPlayersNotification()) {
                for (Player admin : Bukkit.getOnlinePlayers()) {
                    if (getConfigSettings().isServerSupportsHoverEvents()) {
                        if(getConfigSettings().isButtonsOnNotifications()) {
                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                admin.spigot().sendMessage(MD5TextUtils.appendTwo(
                                        MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", convertedTime)), player),
                                        MD5TextUtils.addPardonButtons(admin, player.getName())
                                ));
                                continue;
                            }
                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                admin.sendMessage(
                                        AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", convertedTime)), player)
                                                .append(AdventureApiUtils.addPardonButtons(admin, player.getName()))
                                );
                                continue;
                            }
                        } else {
                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("MD5")) {
                                admin.spigot().sendMessage(MD5TextUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", convertedTime)), player));
                                continue;
                            }
                            if (getConfigSettings().getSupportedHoverEvents().equalsIgnoreCase("ADVENTURE")) {
                                admin.sendMessage(AdventureApiUtils.createPlayerInfoHoverText(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", convertedTime)), player));
                                continue;
                            }
                        }
                    } else {
                        admin.sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", convertedTime)));
                    }
                }
            }
        });
    }

}
