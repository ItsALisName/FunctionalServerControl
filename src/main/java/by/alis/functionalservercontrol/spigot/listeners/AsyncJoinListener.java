package by.alis.functionalservercontrol.spigot.listeners;

import by.alis.functionalservercontrol.api.enums.BanType;
import by.alis.functionalservercontrol.spigot.additional.coreadapters.CoreAdapter;
import by.alis.functionalservercontrol.spigot.additional.misc.AdventureApiUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.MD5TextUtils;
import by.alis.functionalservercontrol.spigot.additional.misc.OtherUtils;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import by.alis.functionalservercontrol.spigot.managers.ban.UnbanManager;
import by.alis.functionalservercontrol.spigot.managers.time.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import static by.alis.functionalservercontrol.spigot.additional.containers.StaticContainers.*;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.BaseManager.getBaseManager;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

/**
 * The class responsible for controlling the entry of players (Banned, with invalid IP addresses, with invalid nicknames)
 */
public class AsyncJoinListener implements Listener {
    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
    private final UnbanManager unbanManager = new UnbanManager();


    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        OfflinePlayer player = CoreAdapter.getAdapter().getOfflinePlayer(event.getUniqueId());
        if(getConfigSettings().isIpsControlEnabled()) {
            if(getConfigSettings().getBlockedIps().contains(event.getAddress().getHostAddress())) {
                event.disallow(
                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        setColors(String.join("\n", getFileAccessor().getLang().getStringList("blocked-ip-kick-format")).replace("%1$f", event.getAddress().getHostAddress()))
                );
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                if(getConfigSettings().notifyConsoleWhenIPBlocked()) {
                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.blocked-ip-notify").replace("%1$f", event.getAddress().getHostAddress())));
                }
                return;
            }
        }

        if(OtherUtils.verifyNickNameFormat(event.getName())) {
            event.disallow(
                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    setColors(String.join("\n", getFileAccessor().getLang().getStringList("blocked-nickname-kick-format")).replace("%1$f", getConfigSettings().getGlobalLanguage().equalsIgnoreCase("ru_RU") ? "Недопустимый формат" : "Invalid format"))
            );
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            if(getConfigSettings().notifyConsoleWhenNickNameBlocked()) {
                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.blocked-nickname-notify").replace("%1$f", event.getName())));
            }
            return;
        }

        if(getConfigSettings().isNicksControlEnabled()) {
            for(String name : getConfigSettings().getBlockedNickNames()) {
                if(getConfigSettings().getNicknameCheckMode().equalsIgnoreCase("equals")) {
                    if(event.getName().equalsIgnoreCase(name)) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("blocked-nickname-kick-format")).replace("%1$f", name))
                        );
                        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                        if(getConfigSettings().notifyConsoleWhenNickNameBlocked()) {
                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.blocked-nickname-notify").replace("%1$f", event.getName())));
                        }
                        return;
                    }
                }
                if(getConfigSettings().getNicknameCheckMode().equalsIgnoreCase("contains")) {
                    if(event.getName().contains(name)) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("blocked-nickname-kick-format")).replace("%1$f", name))
                        );
                        event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                        if(getConfigSettings().notifyConsoleWhenNickNameBlocked()) {
                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.blocked-nickname-notify").replace("%1$f", event.getName())));
                        }
                        return;
                    }
                }
            }
        }

        if(getConfigSettings().isAllowedUseRamAsContainer()) {

            if(getBannedPlayersContainer().getNameContainer().contains(event.getName()) && getBannedPlayersContainer().getUUIDContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName())).equalsIgnoreCase(String.valueOf(event.getUniqueId()))) {
                if((getBannedPlayersContainer().getBanTypesContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName())) == BanType.TIMED_IP
                || getBannedPlayersContainer().getBanTypesContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName())) == BanType.PERMANENT_IP)
                && !getBannedPlayersContainer().getIpContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName())).equalsIgnoreCase(event.getAddress().getHostAddress())) {
                    long currentTime = getBannedPlayersContainer().getBanTimeContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName()));
                    BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName()));
                    String reason = getBannedPlayersContainer().getReasonContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName()));
                    String id = getBannedPlayersContainer().getIdsContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName()));
                    String timeAndDate = getBannedPlayersContainer().getRealBanDateContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName())) + ", " + getBannedPlayersContainer().getRealBanTimeContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName()));
                    String initiatorName = getBannedPlayersContainer().getInitiatorNameContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName()));
                    String realDate = getBannedPlayersContainer().getRealBanDateContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName()));
                    String realTime = getBannedPlayersContainer().getRealBanTimeContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName()));
                    getBanContainerManager().removeFromBanContainer("-id", id);
                    getBannedPlayersContainer().addToBansContainer(
                            id,
                            event.getAddress().getHostAddress(),
                            event.getName(),
                            initiatorName,
                            reason,
                            banType,
                            realDate,
                            realTime,
                            String.valueOf(event.getUniqueId()),
                            currentTime
                    );
                    getBaseManager().deleteFromBannedPlayers("-id", id);
                    getBaseManager().insertIntoBannedPlayers(id, event.getAddress().getHostAddress(), event.getName(), initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime);
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

            if(getBannedPlayersContainer().getIpContainer().contains(event.getAddress().getHostAddress())) {

                if((getBannedPlayersContainer().getBanTypesContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress())) == BanType.PERMANENT_IP
                || getBannedPlayersContainer().getBanTypesContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress())) == BanType.TIMED_IP)
                && !getBannedPlayersContainer().getNameContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress())).equalsIgnoreCase(event.getName())) {
                    int indexOf = getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress());
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
                            event.getAddress().getHostAddress(),
                            event.getName(),
                            initiatorName,
                            reason,
                            banType,
                            realDate,
                            realTime,
                            String.valueOf(event.getUniqueId()),
                            currentTime
                    );
                    getBaseManager().deleteFromBannedPlayers("-id", id);
                    getBaseManager().insertIntoBannedPlayers(id, event.getAddress().getHostAddress(), event.getName(), initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime);
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
                int indexOf = getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress());
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
            if(getBaseManager().getBannedPlayersNames().contains(event.getName()) && getBaseManager().getBannedUUIDs().get(getBaseManager().getBannedPlayersNames().indexOf(event.getName())).equalsIgnoreCase(String.valueOf(event.getUniqueId()))) {
                if((getBaseManager().getBanTypes().get(getBaseManager().getBannedPlayersNames().indexOf(event.getName())) == BanType.TIMED_IP
                        || getBaseManager().getBanTypes().get(getBaseManager().getBannedPlayersNames().indexOf(event.getName())) == BanType.PERMANENT_IP)
                        && !getBaseManager().getBannedIps().get(getBaseManager().getBannedPlayersNames().indexOf(event.getName())).equalsIgnoreCase(event.getAddress().getHostAddress())) {
                    int indexOf = getBaseManager().getBannedPlayersNames().indexOf(event.getName());
                    long currentTime = getBaseManager().getUnbanTimes().get(indexOf);
                    BanType banType = getBaseManager().getBanTypes().get(indexOf);
                    if(banType != BanType.PERMANENT_IP){
                        if (System.currentTimeMillis() >= currentTime) {

                            this.unbanManager.preformUnban(player, "The Ban time has expired");
                            event.allow();
                            return;
                        }
                    }
                    String reason = getBaseManager().getBanReasons().get(indexOf);
                    String id = getBaseManager().getBannedIds().get(indexOf);
                    String timeAndDate = getBaseManager().getBansDates().get(indexOf) + ", " + getBaseManager().getBansTimes().get(indexOf);
                    String initiatorName = getBaseManager().getBanInitiators().get(indexOf);
                    String realDate = getBaseManager().getBansDates().get(indexOf);
                    String realTime = getBaseManager().getBansTimes().get(indexOf);
                    getBaseManager().deleteFromBannedPlayers("-id", id);
                    getBaseManager().insertIntoBannedPlayers(id, event.getAddress().getHostAddress(), event.getName(), initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime);
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

            if(getBaseManager().getBannedIps().contains(event.getAddress().getHostAddress())) {

                if((getBaseManager().getBanTypes().get(getBaseManager().getBannedIps().indexOf(event.getAddress().getHostAddress())) == BanType.PERMANENT_IP
                        || getBaseManager().getBanTypes().get(getBaseManager().getBannedIps().indexOf(event.getAddress().getHostAddress())) == BanType.TIMED_IP)
                        && !getBaseManager().getBannedPlayersNames().get(getBaseManager().getBannedIps().indexOf(event.getAddress().getHostAddress())).equalsIgnoreCase(event.getName())) {
                    int indexOf = getBaseManager().getBannedIps().indexOf(event.getAddress().getHostAddress());
                    long currentTime = getBaseManager().getUnbanTimes().get(indexOf);
                    BanType banType = getBaseManager().getBanTypes().get(indexOf);
                    if(banType != BanType.PERMANENT_IP){
                        if (System.currentTimeMillis() >= currentTime) {

                            this.unbanManager.preformUnban(player, "The Ban time has expired");
                            event.allow();
                            return;
                        }
                    }
                    String reason = getBaseManager().getBanReasons().get(indexOf);
                    String id = getBaseManager().getBannedIds().get(indexOf);
                    String timeAndDate = getBaseManager().getBansDates().get(indexOf) + ", " + getBaseManager().getBansTimes().get(indexOf);
                    String initiatorName = getBaseManager().getBanInitiators().get(indexOf);
                    String realDate = getBaseManager().getBansDates().get(indexOf);
                    String realTime = getBaseManager().getBansTimes().get(indexOf);
                    getBaseManager().deleteFromBannedPlayers("-id", id);
                    getBaseManager().insertIntoBannedPlayers(id, event.getAddress().getHostAddress(), event.getName(), initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime);
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
                int indexOf = getBaseManager().getBannedIps().indexOf(event.getAddress().getHostAddress());
                long currentTime = getBaseManager().getUnbanTimes().get(indexOf);
                BanType banType = getBaseManager().getBanTypes().get(indexOf);
                if(banType != BanType.PERMANENT_NOT_IP && banType != BanType.PERMANENT_IP){
                    if (System.currentTimeMillis() >= currentTime) {

                        this.unbanManager.preformUnban(player, "The Ban time has expired");
                        event.allow();
                        return;
                    }
                }
                String reason = getBaseManager().getBanReasons().get(indexOf);
                String id = getBaseManager().getBannedIds().get(indexOf);
                String timeAndDate = getBaseManager().getBansDates().get(indexOf) + ", " + getBaseManager().getBansTimes().get(indexOf);
                String initiatorName = getBaseManager().getBanInitiators().get(indexOf);
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


            if(getBaseManager().getBannedUUIDs().contains(String.valueOf(event.getUniqueId()))) {
                int indexOf = getBaseManager().getBannedUUIDs().indexOf(String.valueOf(event.getUniqueId()));
                BanType banType = getBaseManager().getBanTypes().get(indexOf);
                long currentTime = getBaseManager().getUnbanTimes().get(indexOf);
                if (System.currentTimeMillis() >= currentTime) {

                    this.unbanManager.preformUnban(player, "The Ban time has expired");
                    event.allow();
                    return;
                }
                if(banType == BanType.PERMANENT_NOT_IP || banType == BanType.TIMED_NOT_IP) {
                    String reason = getBaseManager().getBanReasons().get(indexOf);
                    String id = getBaseManager().getBannedIds().get(indexOf);
                    String timeAndDate = getBaseManager().getBansDates().get(indexOf) + ", " + getBaseManager().getBansTimes().get(indexOf);
                    String initiatorName = getBaseManager().getBanInitiators().get(indexOf);
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
        Bukkit.getScheduler().runTaskAsynchronously(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class), () -> {
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
