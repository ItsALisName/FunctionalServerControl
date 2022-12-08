package by.alis.functionalbans.spigot.Listeners;

import by.alis.functionalbans.spigot.Additional.Enums.BanType;
import by.alis.functionalbans.spigot.Managers.BansManagers.BanManager;
import by.alis.functionalbans.spigot.Managers.FilesManagers.FileAccessor;
import by.alis.functionalbans.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import static by.alis.functionalbans.databases.StaticBases.getSQLiteManager;
import static by.alis.functionalbans.spigot.Additional.Containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class AsyncJoinListener implements Listener {

    private final FileAccessor accessor = new FileAccessor();
    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();
    private final BanManager banManager = new BanManager();


    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {

            if(getBannedPlayersContainer().getNameContainer().contains(event.getName()) && getBannedPlayersContainer().getUUIDContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName())).equalsIgnoreCase(String.valueOf(event.getUniqueId()))) {
                if((getBannedPlayersContainer().getBanTypesContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName())) == BanType.TIMED_IP
                || getBannedPlayersContainer().getBanTypesContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName())) == BanType.PERMANENT_IP)
                && !getBannedPlayersContainer().getIpContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName())).equalsIgnoreCase(event.getAddress().getHostAddress())) {
                    long currentTime = getBannedPlayersContainer().getBanTimeContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName()));
                    BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName()));
                    if(banType != BanType.PERMANENT_IP){
                        if (System.currentTimeMillis() >= currentTime) {
                            //ЛОГИКА РАЗБЛОКИРОВКИ АККАУНТА MULTI-IPS
                            return;
                        }
                    }
                    String reason = getBannedPlayersContainer().getReasonContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName()));
                    String id = getBannedPlayersContainer().getIdsContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName()));
                    String timeAndDate = getBannedPlayersContainer().getRealBanDateContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName())) + ", " + getBannedPlayersContainer().getRealBanTimeContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName()));
                    String initiatorName = getBannedPlayersContainer().getInitiatorNameContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName()));
                    String realDate = getBannedPlayersContainer().getRealBanDateContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName()));
                    String realTime = getBannedPlayersContainer().getRealBanTimeContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName()));
                    this.banManager.getBanContainerManager().removeFromBanContainer("-id", id);
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
                        case "sqlite": {
                            getSQLiteManager().deleteFromBannedPlayers("-id", id);
                            getSQLiteManager().insertIntoBannedPlayers(id, event.getAddress().getHostAddress(), event.getName(), initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime);
                            break;
                        }
                        case "mysql": {
                            break;
                        }
                        case "h2": {
                            break;
                        }
                        default: {
                            getSQLiteManager().deleteFromBannedPlayers("-id", id);
                            getSQLiteManager().insertIntoBannedPlayers(id, event.getAddress().getHostAddress(), event.getName(), initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime);
                            break;
                        }
                    }
                    if(banType == BanType.PERMANENT_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", this.accessor.getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                        );
                        return;
                    }
                    if(banType == BanType.TIMED_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(currentTime))))
                        );
                        return;
                    }
                }
            }

            if(getBannedPlayersContainer().getIpContainer().contains(event.getAddress().getHostAddress())) {

                if((getBannedPlayersContainer().getBanTypesContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress())) == BanType.PERMANENT_IP
                || getBannedPlayersContainer().getBanTypesContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress())) == BanType.TIMED_IP)
                && !getBannedPlayersContainer().getNameContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress())).equalsIgnoreCase(event.getName())) {
                    long currentTime = getBannedPlayersContainer().getBanTimeContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress()));
                    BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress()));
                    if(banType != BanType.PERMANENT_IP){
                        if (System.currentTimeMillis() >= currentTime) {
                            //ЛОГИКА РАЗБЛОКИРОВКИ АККАУНТА MULTI-IPS
                            return;
                        }
                    }
                    String reason = getBannedPlayersContainer().getReasonContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress()));
                    String id = getBannedPlayersContainer().getIdsContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress()));
                    String timeAndDate = getBannedPlayersContainer().getRealBanDateContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress())) + ", " + getBannedPlayersContainer().getRealBanTimeContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress()));
                    String initiatorName = getBannedPlayersContainer().getInitiatorNameContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress()));
                    String realDate = getBannedPlayersContainer().getRealBanDateContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress()));
                    String realTime = getBannedPlayersContainer().getRealBanTimeContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress()));
                    this.banManager.getBanContainerManager().removeFromBanContainer("-id", id);
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
                        case "sqlite": {
                            getSQLiteManager().deleteFromBannedPlayers("-id", id);
                            getSQLiteManager().insertIntoBannedPlayers(id, event.getAddress().getHostAddress(), event.getName(), initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime);
                            break;
                        }
                         case "mysql": {
                             break;
                         }
                        case "h2": {
                            break;
                        }
                        default: {
                            getSQLiteManager().deleteFromBannedPlayers("-id", id);
                            getSQLiteManager().insertIntoBannedPlayers(id, event.getAddress().getHostAddress(), event.getName(), initiatorName, reason, banType, realDate, realTime, event.getUniqueId(), currentTime);
                            break;
                        }

                    }
                    if(banType == BanType.PERMANENT_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", this.accessor.getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                        );
                        return;
                    }
                    if(banType == BanType.TIMED_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(currentTime))))
                        );
                        return;
                    }
                }

                long currentTime = getBannedPlayersContainer().getBanTimeContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress()));
                BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress()));
                if(banType != BanType.PERMANENT_NOT_IP && banType != BanType.PERMANENT_IP){
                    if (System.currentTimeMillis() >= currentTime) {
                        //ЛОГИКА РАЗБЛОКИРОВКИ АККАУНТА
                        return;
                    }
                }
                String reason = getBannedPlayersContainer().getReasonContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress()));
                String id = getBannedPlayersContainer().getIdsContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress()));
                String timeAndDate = getBannedPlayersContainer().getRealBanDateContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress())) + ", " + getBannedPlayersContainer().getRealBanTimeContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(event.getName()));
                String initiatorName = getBannedPlayersContainer().getInitiatorNameContainer().get(getBannedPlayersContainer().getIpContainer().indexOf(event.getAddress().getHostAddress()));
                if(banType == BanType.PERMANENT_NOT_IP) {
                    if (getBannedPlayersContainer().getUUIDContainer().contains(String.valueOf(event.getUniqueId()))) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", this.accessor.getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                        );
                    }
                    return;
                }
                if(banType == BanType.PERMANENT_IP) {
                    event.disallow(
                            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                            setColors(String.join("\n", this.accessor.getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                    );
                    return;
                }
                if(banType == BanType.TIMED_NOT_IP) {
                    if (getBannedPlayersContainer().getUUIDContainer().contains(String.valueOf(event.getUniqueId()))) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(currentTime))))
                        );
                    }
                    return;
                }
                if(banType == BanType.TIMED_IP) {
                    event.disallow(
                            AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                            setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(currentTime)))
                    ));
                    return;
                }
                return;
            }


            if(getBannedPlayersContainer().getUUIDContainer().contains(String.valueOf(event.getUniqueId()))) {
                int indexOf = getBannedPlayersContainer().getUUIDContainer().indexOf(String.valueOf(event.getUniqueId()));
                BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(indexOf);
                long currentTime = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                if(System.currentTimeMillis() >= currentTime) {
                    //ЛОГИКА РАЗБЛОКИРОВКИ АККАУНТА
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
                                setColors(String.join("\n", this.accessor.getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                        );
                        return;
                    }
                    if(banType == BanType.TIMED_NOT_IP) {
                        event.disallow(
                                AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(currentTime))))
                        );
                        return;
                    }
                }
            }
        } else {
            switch (getConfigSettings().getStorageType()) {
                case "sqlite": {
                    if(getSQLiteManager().getBannedPlayersNames().contains(event.getName()) && getSQLiteManager().getBannedUUIDs().get(getSQLiteManager().getBannedPlayersNames().indexOf(event.getName())).equalsIgnoreCase(String.valueOf(event.getUniqueId()))) {
                        int indexOf = getSQLiteManager().getBannedPlayersNames().indexOf(event.getName());
                        if((getSQLiteManager().getBanTypes().get(indexOf) == BanType.TIMED_IP
                                || getSQLiteManager().getBanTypes().get(indexOf) == BanType.PERMANENT_IP)
                                && !getSQLiteManager().getBannedIps().get(indexOf).equalsIgnoreCase(event.getAddress().getHostAddress())) {
                            long currentTime = getSQLiteManager().getUnbanTimes().get(indexOf);
                            BanType banType = getSQLiteManager().getBanTypes().get(indexOf);
                            if(banType != BanType.PERMANENT_IP){
                                if (System.currentTimeMillis() >= currentTime) {
                                    //ЛОГИКА РАЗБЛОКИРОВКИ АККАУНТА MULTI-IPS
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
                                        setColors(String.join("\n", this.accessor.getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                                );
                                return;
                            }
                            if(banType == BanType.TIMED_IP) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(currentTime))))
                                );
                                return;
                            }
                        }
                    }


                    if(getSQLiteManager().getBannedIps().contains(event.getAddress().getHostAddress())) {
                        int indexOf = getSQLiteManager().getBannedIps().indexOf(event.getAddress().getHostAddress());
                        if((getSQLiteManager().getBanTypes().get(indexOf) == BanType.PERMANENT_IP
                                || getSQLiteManager().getBanTypes().get(indexOf) == BanType.TIMED_IP)
                                && !getSQLiteManager().getBannedPlayersNames().get(indexOf).equalsIgnoreCase(event.getName())) {
                            long currentTime = getSQLiteManager().getUnbanTimes().get(indexOf);
                            BanType banType = getSQLiteManager().getBanTypes().get(indexOf);
                            if(banType != BanType.PERMANENT_IP){
                                if (System.currentTimeMillis() >= currentTime) {
                                    //ЛОГИКА РАЗБЛОКИРОВКИ АККАУНТА MULTI-IPS
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
                                        setColors(String.join("\n", this.accessor.getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                                );
                                return;
                            }
                            if(banType == BanType.TIMED_IP) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(currentTime))))
                                );
                                return;
                            }
                        }

                        long currentTime = getSQLiteManager().getUnbanTimes().get(indexOf);
                        BanType banType = getSQLiteManager().getBanTypes().get(indexOf);
                        if(banType != BanType.PERMANENT_NOT_IP && banType != BanType.PERMANENT_IP){
                            if (System.currentTimeMillis() >= currentTime) {
                                //ЛОГИКА РАЗБЛОКИРОВКИ АККАУНТА
                                return;
                            }
                        }
                        String reason = getSQLiteManager().getBanReasons().get(indexOf);
                        String id = getSQLiteManager().getBannedIds().get(indexOf);
                        String timeAndDate = getSQLiteManager().getBansDates().get(indexOf) + ", " + getSQLiteManager().getBansTimes().get(indexOf);
                        String initiatorName = getSQLiteManager().getBanInitiators().get(indexOf);
                        if(banType == BanType.PERMANENT_NOT_IP) {
                            if (getSQLiteManager().getBannedUUIDs().contains(String.valueOf(event.getUniqueId()))) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", this.accessor.getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                                );
                            }
                            return;
                        }
                        if(banType == BanType.PERMANENT_IP) {
                            event.disallow(
                                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                    setColors(String.join("\n", this.accessor.getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                            );
                            return;
                        }
                        if(banType == BanType.TIMED_NOT_IP) {
                            if (getBannedPlayersContainer().getUUIDContainer().contains(String.valueOf(event.getUniqueId()))) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(currentTime))))
                                );
                            }
                            return;
                        }
                        if(banType == BanType.TIMED_IP) {
                            event.disallow(
                                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                    setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(currentTime)))
                                    ));
                            return;
                        }
                        return;
                    }

                    if(getSQLiteManager().getBannedUUIDs().contains(String.valueOf(event.getUniqueId()))) {
                        int indexOf = getSQLiteManager().getBannedUUIDs().indexOf(String.valueOf(event.getUniqueId()));
                        BanType banType = getSQLiteManager().getBanTypes().get(indexOf);
                        long currentTime = getSQLiteManager().getUnbanTimes().get(indexOf);
                        if(System.currentTimeMillis() >= currentTime) {
                            //ЛОГИКА РАЗБЛОКИРОВКИ АККАУНТА
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
                                        setColors(String.join("\n", this.accessor.getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                                );
                                return;
                            }
                            if(banType == BanType.TIMED_NOT_IP) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(currentTime))))
                                );
                                return;
                            }
                        }
                    }

                }


                case "mysql": {
                    break;
                }


                case "h2": {
                    break;
                }


                default: {
                    if(getSQLiteManager().getBannedPlayersNames().contains(event.getName()) && getSQLiteManager().getBannedUUIDs().get(getSQLiteManager().getBannedPlayersNames().indexOf(event.getName())).equalsIgnoreCase(String.valueOf(event.getUniqueId()))) {
                        int indexOf = getSQLiteManager().getBannedPlayersNames().indexOf(event.getName());
                        if((getSQLiteManager().getBanTypes().get(indexOf) == BanType.TIMED_IP
                                || getSQLiteManager().getBanTypes().get(indexOf) == BanType.PERMANENT_IP)
                                && !getSQLiteManager().getBannedIps().get(indexOf).equalsIgnoreCase(event.getAddress().getHostAddress())) {
                            long currentTime = getSQLiteManager().getUnbanTimes().get(indexOf);
                            BanType banType = getSQLiteManager().getBanTypes().get(indexOf);
                            if(banType != BanType.PERMANENT_IP){
                                if (System.currentTimeMillis() >= currentTime) {
                                    //ЛОГИКА РАЗБЛОКИРОВКИ АККАУНТА MULTI-IPS
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
                                        setColors(String.join("\n", this.accessor.getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                                );
                                return;
                            }
                            if(banType == BanType.TIMED_IP) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(currentTime))))
                                );
                                return;
                            }
                        }
                    }


                    if(getSQLiteManager().getBannedIps().contains(event.getAddress().getHostAddress())) {
                        int indexOf = getSQLiteManager().getBannedIps().indexOf(event.getAddress().getHostAddress());
                        if((getSQLiteManager().getBanTypes().get(indexOf) == BanType.PERMANENT_IP
                                || getSQLiteManager().getBanTypes().get(indexOf) == BanType.TIMED_IP)
                                && !getSQLiteManager().getBannedPlayersNames().get(indexOf).equalsIgnoreCase(event.getName())) {
                            long currentTime = getSQLiteManager().getUnbanTimes().get(indexOf);
                            BanType banType = getSQLiteManager().getBanTypes().get(indexOf);
                            if(banType != BanType.PERMANENT_IP){
                                if (System.currentTimeMillis() >= currentTime) {
                                    //ЛОГИКА РАЗБЛОКИРОВКИ АККАУНТА MULTI-IPS
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
                                        setColors(String.join("\n", this.accessor.getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                                );
                                return;
                            }
                            if(banType == BanType.TIMED_IP) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(currentTime))))
                                );
                                return;
                            }
                        }

                        long currentTime = getSQLiteManager().getUnbanTimes().get(indexOf);
                        BanType banType = getSQLiteManager().getBanTypes().get(indexOf);
                        if(banType != BanType.PERMANENT_NOT_IP && banType != BanType.PERMANENT_IP){
                            if (System.currentTimeMillis() >= currentTime) {
                                //ЛОГИКА РАЗБЛОКИРОВКИ АККАУНТА
                                return;
                            }
                        }
                        String reason = getSQLiteManager().getBanReasons().get(indexOf);
                        String id = getSQLiteManager().getBannedIds().get(indexOf);
                        String timeAndDate = getSQLiteManager().getBansDates().get(indexOf) + ", " + getSQLiteManager().getBansTimes().get(indexOf);
                        String initiatorName = getSQLiteManager().getBanInitiators().get(indexOf);
                        if(banType == BanType.PERMANENT_NOT_IP) {
                            if (getSQLiteManager().getBannedUUIDs().contains(String.valueOf(event.getUniqueId()))) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", this.accessor.getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                                );
                            }
                            return;
                        }
                        if(banType == BanType.PERMANENT_IP) {
                            event.disallow(
                                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                    setColors(String.join("\n", this.accessor.getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                            );
                            return;
                        }
                        if(banType == BanType.TIMED_NOT_IP) {
                            if (getBannedPlayersContainer().getUUIDContainer().contains(String.valueOf(event.getUniqueId()))) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(currentTime))))
                                );
                            }
                            return;
                        }
                        if(banType == BanType.TIMED_IP) {
                            event.disallow(
                                    AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                    setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(currentTime)))
                                    ));
                            return;
                        }
                        return;
                    }

                    if(getSQLiteManager().getBannedUUIDs().contains(String.valueOf(event.getUniqueId()))) {
                        int indexOf = getSQLiteManager().getBannedUUIDs().indexOf(String.valueOf(event.getUniqueId()));
                        BanType banType = getSQLiteManager().getBanTypes().get(indexOf);
                        long currentTime = getSQLiteManager().getUnbanTimes().get(indexOf);
                        if(System.currentTimeMillis() >= currentTime) {
                            //ЛОГИКА РАЗБЛОКИРОВКИ АККАУНТА
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
                                        setColors(String.join("\n", this.accessor.getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", getGlobalVariables().getVariableNever()))
                                );
                                return;
                            }
                            if(banType == BanType.TIMED_NOT_IP) {
                                event.disallow(
                                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                                        setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", timeAndDate).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(currentTime))))
                                );
                                return;
                            }
                        }
                    }
                }
            }
        }
    }
}
