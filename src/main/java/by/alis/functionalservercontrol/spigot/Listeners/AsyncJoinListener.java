package by.alis.functionalservercontrol.spigot.Listeners;

import by.alis.functionalservercontrol.API.Enums.BanType;
import by.alis.functionalservercontrol.spigot.Additional.Other.OtherUtils;
import by.alis.functionalservercontrol.spigot.Managers.Bans.UnbanManager;
import by.alis.functionalservercontrol.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import static by.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getBanContainerManager;
import static by.alis.functionalservercontrol.spigot.Additional.Containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalservercontrol.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalservercontrol.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

/**
 * The class responsible for controlling the entry of players (Banned, with invalid IP addresses, with invalid nicknames)
 */
public class AsyncJoinListener implements Listener {
    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
    private final UnbanManager unbanManager = new UnbanManager();


    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {

        if(getConfigSettings().isIpsControlEnabled()) {
            if(getConfigSettings().getBlockedIps().contains(event.getAddress().getHostAddress())) {
                event.disallow(
                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        setColors(String.join("\n", getFileAccessor().getLang().getStringList("blocked-ip-kick-format")).replace("%1$f", event.getAddress().getHostAddress()))
                );
                event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                if(getConfigSettings().notifyConsoleWhenNickNameBlocked()) {
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().deleteFromBannedPlayers("-id", id);
                            getSQLiteManager().insertIntoBannedPlayers(id, event.getAddress().getHostAddress(), event.getName(), initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime);
                            break;
                        }
                        case MYSQL: {
                            break;
                        }
                        case H2: {
                            break;
                        }
                    }
                    if(banType == BanType.PERMANENT_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                        );
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", event.getAddress().getHostAddress())));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(Player admin : Bukkit.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", event.getAddress().getHostAddress())));
                                }
                            }
                        }
                        return;
                    }

                    if(banType != BanType.PERMANENT_IP){
                        if (System.currentTimeMillis() >= currentTime) {
                            OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUniqueId());
                            this.unbanManager.preformUnban(player, "The Ban time has expired");
                            return;
                        }
                    }

                    if(banType == BanType.TIMED_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))))
                        );
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))).replace("%2$f", event.getAddress().getHostAddress())));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(Player admin : Bukkit.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))).replace("%2$f", event.getAddress().getHostAddress())));
                                }
                            }
                        }
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
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().deleteFromBannedPlayers("-id", id);
                            getSQLiteManager().insertIntoBannedPlayers(id, event.getAddress().getHostAddress(), event.getName(), initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime);
                            break;
                        }
                         case MYSQL: {
                             break;
                         }
                        case H2: {
                            break;
                        }

                    }

                    if(banType != BanType.PERMANENT_IP){
                        if (System.currentTimeMillis() >= currentTime) {
                            OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUniqueId());
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
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", event.getAddress().getHostAddress())));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(Player admin : Bukkit.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", event.getAddress().getHostAddress())));
                                }
                            }
                        }
                        return;
                    }
                    if(banType == BanType.TIMED_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))))
                        );
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))).replace("%2$f", event.getAddress().getHostAddress())));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(Player admin : Bukkit.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))).replace("%2$f", event.getAddress().getHostAddress())));
                                }
                            }
                        }
                        return;
                    }
                }
                int indexOf = getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress());
                long currentTime = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(indexOf);
                if(banType != BanType.PERMANENT_NOT_IP && banType != BanType.PERMANENT_IP){
                    if (System.currentTimeMillis() >= currentTime) {
                        OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUniqueId());
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
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", event.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", event.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                            }
                        }
                    }
                    return;
                }
                if(banType == BanType.PERMANENT_IP) {
                    event.disallow(
                            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                            setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                    );
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", event.getAddress().getHostAddress())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", event.getAddress().getHostAddress())));
                            }
                        }
                    }
                    return;
                }
                if(banType == BanType.TIMED_NOT_IP) {
                    if (getBannedPlayersContainer().getUUIDContainer().contains(String.valueOf(event.getUniqueId()))) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))))
                        );
                    }
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", event.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime)))));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", event.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime)))));
                            }
                        }
                    }
                    return;
                }
                if(banType == BanType.TIMED_IP) {
                    event.disallow(
                            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                            setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime)))
                    ));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))).replace("%2$f", event.getAddress().getHostAddress())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))).replace("%2$f", event.getAddress().getHostAddress())));
                            }
                        }
                    }
                    return;
                }
                return;
            }


            if(getBannedPlayersContainer().getUUIDContainer().contains(String.valueOf(event.getUniqueId()))) {
                int indexOf = getBannedPlayersContainer().getUUIDContainer().indexOf(String.valueOf(event.getUniqueId()));
                BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(indexOf);
                long currentTime = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                if (System.currentTimeMillis() >= currentTime) {
                    OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUniqueId());
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
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", event.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(Player admin : Bukkit.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", event.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                                }
                            }
                        }
                        return;
                    }
                    if(banType == BanType.TIMED_NOT_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))))
                        );
                        if (getConfigSettings().isConsoleNotification()) {
                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", event.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime)))));
                        }
                        if(getConfigSettings().isPlayersNotification()) {
                            for(Player admin : Bukkit.getOnlinePlayers()) {
                                if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", event.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime)))));
                                }
                            }
                        }
                        return;
                    }
                }
            }
        }




        //=============================================================================
        // TODO: 10.12.2022
        else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    if(getSQLiteManager().getBannedPlayersNames().contains(event.getName()) && getSQLiteManager().getBannedUUIDs().get(getSQLiteManager().getBannedPlayersNames().indexOf(event.getName())).equalsIgnoreCase(String.valueOf(event.getUniqueId()))) {
                        if((getSQLiteManager().getBanTypes().get(getSQLiteManager().getBannedPlayersNames().indexOf(event.getName())) == BanType.TIMED_IP
                                || getSQLiteManager().getBanTypes().get(getSQLiteManager().getBannedPlayersNames().indexOf(event.getName())) == BanType.PERMANENT_IP)
                                && !getSQLiteManager().getBannedIps().get(getSQLiteManager().getBannedPlayersNames().indexOf(event.getName())).equalsIgnoreCase(event.getAddress().getHostAddress())) {
                            int indexOf = getSQLiteManager().getBannedPlayersNames().indexOf(event.getName());
                            long currentTime = getSQLiteManager().getUnbanTimes().get(indexOf);
                            BanType banType = getSQLiteManager().getBanTypes().get(indexOf);
                            if(banType != BanType.PERMANENT_IP){
                                if (System.currentTimeMillis() >= currentTime) {
                                    OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUniqueId());
                                    this.unbanManager.preformUnban(player, "The Ban time has expired");
                                    event.allow();
                                    return;
                                }
                            }
                            String reason = getSQLiteManager().getBanReasons().get(indexOf);
                            String id = getSQLiteManager().getBannedIds().get(indexOf);
                            String timeAndDate = getSQLiteManager().getBansDates().get(indexOf) + ", " + getSQLiteManager().getBansTimes().get(indexOf);
                            String initiatorName = getSQLiteManager().getBanInitiators().get(indexOf);
                            String realDate = getSQLiteManager().getBansDates().get(indexOf);
                            String realTime = getSQLiteManager().getBansTimes().get(indexOf);
                            getSQLiteManager().deleteFromBannedPlayers("-id", id);
                            getSQLiteManager().insertIntoBannedPlayers(id, event.getAddress().getHostAddress(), event.getName(), initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime);
                            if(banType == BanType.PERMANENT_IP) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                                );
                                if (getConfigSettings().isConsoleNotification()) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", event.getAddress().getHostAddress())));
                                }
                                if(getConfigSettings().isPlayersNotification()) {
                                    for(Player admin : Bukkit.getOnlinePlayers()) {
                                        if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", event.getAddress().getHostAddress())));
                                        }
                                    }
                                }
                                return;
                            }
                            if(banType == BanType.TIMED_IP) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))))
                                );
                                if (getConfigSettings().isConsoleNotification()) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))).replace("%2$f", event.getAddress().getHostAddress())));
                                }
                                if(getConfigSettings().isPlayersNotification()) {
                                    for(Player admin : Bukkit.getOnlinePlayers()) {
                                        if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))).replace("%2$f", event.getAddress().getHostAddress())));
                                        }
                                    }
                                }
                                return;
                            }
                        }
                    }

                    if(getSQLiteManager().getBannedIps().contains(event.getAddress().getHostAddress())) {

                        if((getSQLiteManager().getBanTypes().get(getSQLiteManager().getBannedIps().indexOf(event.getAddress().getHostAddress())) == BanType.PERMANENT_IP
                                || getSQLiteManager().getBanTypes().get(getSQLiteManager().getBannedIps().indexOf(event.getAddress().getHostAddress())) == BanType.TIMED_IP)
                                && !getSQLiteManager().getBannedPlayersNames().get(getSQLiteManager().getBannedIps().indexOf(event.getAddress().getHostAddress())).equalsIgnoreCase(event.getName())) {
                            int indexOf = getSQLiteManager().getBannedIps().indexOf(event.getAddress().getHostAddress());
                            long currentTime = getSQLiteManager().getUnbanTimes().get(indexOf);
                            BanType banType = getSQLiteManager().getBanTypes().get(indexOf);
                            if(banType != BanType.PERMANENT_IP){
                                if (System.currentTimeMillis() >= currentTime) {
                                    OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUniqueId());
                                    this.unbanManager.preformUnban(player, "The Ban time has expired");
                                    event.allow();
                                    return;
                                }
                            }
                            String reason = getSQLiteManager().getBanReasons().get(indexOf);
                            String id = getSQLiteManager().getBannedIds().get(indexOf);
                            String timeAndDate = getSQLiteManager().getBansDates().get(indexOf) + ", " + getSQLiteManager().getBansTimes().get(indexOf);
                            String initiatorName = getSQLiteManager().getBanInitiators().get(indexOf);
                            String realDate = getSQLiteManager().getBansDates().get(indexOf);
                            String realTime = getSQLiteManager().getBansTimes().get(indexOf);
                            getSQLiteManager().deleteFromBannedPlayers("-id", id);
                            getSQLiteManager().insertIntoBannedPlayers(id, event.getAddress().getHostAddress(), event.getName(), initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime);
                            if(banType == BanType.PERMANENT_IP) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                                );
                                if (getConfigSettings().isConsoleNotification()) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", event.getAddress().getHostAddress())));
                                }
                                if(getConfigSettings().isPlayersNotification()) {
                                    for(Player admin : Bukkit.getOnlinePlayers()) {
                                        if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", event.getAddress().getHostAddress())));
                                        }
                                    }
                                }
                                return;
                            }
                            if(banType == BanType.TIMED_IP) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))))
                                );
                                if (getConfigSettings().isConsoleNotification()) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))).replace("%2$f", event.getAddress().getHostAddress())));
                                }
                                if(getConfigSettings().isPlayersNotification()) {
                                    for(Player admin : Bukkit.getOnlinePlayers()) {
                                        if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))).replace("%2$f", event.getAddress().getHostAddress())));
                                        }
                                    }
                                }
                                return;
                            }
                        }
                        int indexOf = getSQLiteManager().getBannedIps().indexOf(event.getAddress().getHostAddress());
                        long currentTime = getSQLiteManager().getUnbanTimes().get(indexOf);
                        BanType banType = getSQLiteManager().getBanTypes().get(indexOf);
                        if(banType != BanType.PERMANENT_NOT_IP && banType != BanType.PERMANENT_IP){
                            if (System.currentTimeMillis() >= currentTime) {
                                OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUniqueId());
                                this.unbanManager.preformUnban(player, "The Ban time has expired");
                                event.allow();
                                return;
                            }
                        }
                        String reason = getSQLiteManager().getBanReasons().get(indexOf);
                        String id = getSQLiteManager().getBannedIds().get(indexOf);
                        String timeAndDate = getSQLiteManager().getBansDates().get(indexOf) + ", " + getSQLiteManager().getBansTimes().get(indexOf);
                        String initiatorName = getSQLiteManager().getBanInitiators().get(indexOf);
                        if(banType == BanType.PERMANENT_NOT_IP) {
                            if (getBannedPlayersContainer().getUUIDContainer().contains(String.valueOf(event.getUniqueId()))) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                                );
                            }
                            if (getConfigSettings().isConsoleNotification()) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", event.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                            }
                            if(getConfigSettings().isPlayersNotification()) {
                                for(Player admin : Bukkit.getOnlinePlayers()) {
                                    if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", event.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                                    }
                                }
                            }
                            return;
                        }
                        if(banType == BanType.PERMANENT_IP) {
                            event.disallow(
                                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                    setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                            );
                            if (getConfigSettings().isConsoleNotification()) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", event.getAddress().getHostAddress())));
                            }
                            if(getConfigSettings().isPlayersNotification()) {
                                for(Player admin : Bukkit.getOnlinePlayers()) {
                                    if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", event.getAddress().getHostAddress())));
                                    }
                                }
                            }
                            return;
                        }
                        if(banType == BanType.TIMED_NOT_IP) {
                            if (getBannedPlayersContainer().getUUIDContainer().contains(String.valueOf(event.getUniqueId()))) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))))
                                );
                            }
                            if (getConfigSettings().isConsoleNotification()) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", event.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime)))));
                            }
                            if(getConfigSettings().isPlayersNotification()) {
                                for(Player admin : Bukkit.getOnlinePlayers()) {
                                    if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", event.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime)))));
                                    }
                                }
                            }
                            return;
                        }
                        if(banType == BanType.TIMED_IP) {
                            event.disallow(
                                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                    setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime)))
                                    ));
                            if (getConfigSettings().isConsoleNotification()) {
                                Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))).replace("%2$f", event.getAddress().getHostAddress())));
                            }
                            if(getConfigSettings().isPlayersNotification()) {
                                for(Player admin : Bukkit.getOnlinePlayers()) {
                                    if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                        Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban-ip").replace("%1$f", event.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))).replace("%2$f", event.getAddress().getHostAddress())));
                                    }
                                }
                            }
                            return;
                        }
                        return;
                    }


                    if(getSQLiteManager().getBannedUUIDs().contains(String.valueOf(event.getUniqueId()))) {
                        int indexOf = getSQLiteManager().getBannedUUIDs().indexOf(String.valueOf(event.getUniqueId()));
                        BanType banType = getSQLiteManager().getBanTypes().get(indexOf);
                        long currentTime = getSQLiteManager().getUnbanTimes().get(indexOf);
                        if (System.currentTimeMillis() >= currentTime) {
                            OfflinePlayer player = Bukkit.getOfflinePlayer(event.getUniqueId());
                            this.unbanManager.preformUnban(player, "The Ban time has expired");
                            event.allow();
                            return;
                        }
                        if(banType == BanType.PERMANENT_NOT_IP || banType == BanType.TIMED_NOT_IP) {
                            String reason = getSQLiteManager().getBanReasons().get(indexOf);
                            String id = getSQLiteManager().getBannedIds().get(indexOf);
                            String timeAndDate = getSQLiteManager().getBansDates().get(indexOf) + ", " + getSQLiteManager().getBansTimes().get(indexOf);
                            String initiatorName = getSQLiteManager().getBanInitiators().get(indexOf);
                            if(banType == BanType.PERMANENT_NOT_IP) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", getFileAccessor().getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                                );
                                if (getConfigSettings().isConsoleNotification()) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", event.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                                }
                                if(getConfigSettings().isPlayersNotification()) {
                                    for(Player admin : Bukkit.getOnlinePlayers()) {
                                        if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", event.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                                        }
                                    }
                                }
                                return;
                            }
                            if(banType == BanType.TIMED_NOT_IP) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", getFileAccessor().getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime))))
                                );
                                if (getConfigSettings().isConsoleNotification()) {
                                    Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", event.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime)))));
                                }
                                if(getConfigSettings().isPlayersNotification()) {
                                    for(Player admin : Bukkit.getOnlinePlayers()) {
                                        if(admin.hasPermission("functionalservercontrol.notification.ban")) {
                                            Bukkit.getConsoleSender().sendMessage(setColors(getFileAccessor().getLang().getString("other.notifications.ban").replace("%1$f", event.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getPunishTime(currentTime)))));
                                        }
                                    }
                                }
                                return;
                            }
                        }
                    }
                }

                case MYSQL: {
                    break;
                }


                case H2: {
                    break;
                }
            }
        }
    }
}
