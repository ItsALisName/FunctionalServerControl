package by.alis.functionalbans.spigot.Listeners;

import by.alis.functionalbans.spigot.Additional.Enums.BanType;
import by.alis.functionalbans.spigot.Managers.BansManagers.BanManager;
import by.alis.functionalbans.spigot.Managers.FilesManagers.FileAccessor;
import by.alis.functionalbans.spigot.Managers.TimeManagers.TimeSettingsAccessor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import static by.alis.functionalbans.databases.StaticBases.getSQLiteManager;
import static by.alis.functionalbans.spigot.Additional.Containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getGlobalVariables;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class NullPlayerJoinListener implements Listener {

    private final BanManager banManager = new BanManager();
    private final FileAccessor accessor = new FileAccessor();
    private final TimeSettingsAccessor timeSettingsAccessor = new TimeSettingsAccessor();

    @EventHandler
    public void onNullPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            if(getBannedPlayersContainer().getNameContainer().contains(player.getName()) && getBannedPlayersContainer().getUUIDContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(player.getName())).equalsIgnoreCase("NULL_PLAYER")) {
                int indexOf = getBannedPlayersContainer().getNameContainer().indexOf(player.getName());
                String initiatorName = getBannedPlayersContainer().getInitiatorNameContainer().get(indexOf);
                long time = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                String reason = getBannedPlayersContainer().getReasonContainer().get(indexOf);
                String realDate = getBannedPlayersContainer().getRealBanDateContainer().get(indexOf);
                String realTime = getBannedPlayersContainer().getRealBanTimeContainer().get(indexOf);
                String id = getBannedPlayersContainer().getIdsContainer().get(indexOf);
                BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(indexOf);
                this.banManager.getBanContainerManager().removeFromBanContainer("-n", player.getName());
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player.getName());
                        getSQLiteManager().insertIntoBannedPlayers(id, player.getAddress().getAddress().getHostAddress(), player.getName(), initiatorName, reason, banType, realDate, realTime, player.getUniqueId(), time);
                        break;
                    }
                    case MYSQL: {
                        break;
                    }
                    case H2: {
                        break;
                    }
                    default: {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player.getName());
                        getSQLiteManager().insertIntoBannedPlayers(id, player.getAddress().getAddress().getHostAddress(), player.getName(), initiatorName, reason, banType, realDate, realTime, player.getUniqueId(), time);
                        break;
                    }
                }
                getBannedPlayersContainer().addToBansContainer(
                        id,
                        player.getAddress().getAddress().getHostAddress(),
                        player.getName(),
                        initiatorName,
                        reason,
                        banType,
                        realDate,
                        realTime,
                        String.valueOf(player.getUniqueId()),
                        time
                );
                if(banType == BanType.PERMANENT_NOT_IP) {
                    player.kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalbans.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                            }
                        }
                    }
                }
                if(banType == BanType.PERMANENT_IP) {
                    player.kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalbans.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                            }
                        }
                    }
                }
                if(banType == BanType.TIMED_NOT_IP) {
                    player.kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time)))));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time)))));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalbans.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time)))));
                            }
                        }
                    }
                }
                if(banType == BanType.TIMED_IP) {
                    player.kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time)))));
                    if (getConfigSettings().isConsoleNotification()) {
                        Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                    }
                    if(getConfigSettings().isPlayersNotification()) {
                        for(Player admin : Bukkit.getOnlinePlayers()) {
                            if(admin.hasPermission("functionalbans.notification.ban")) {
                                Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                            }
                        }
                    }
                }
                return;
            }
        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    if(getBannedPlayersContainer().getNameContainer().contains(player.getName()) && getBannedPlayersContainer().getUUIDContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(player.getName())).equalsIgnoreCase("NULL_PLAYER")) {
                        int indexOf = getBannedPlayersContainer().getNameContainer().indexOf(player.getName());
                        String initiatorName = getBannedPlayersContainer().getInitiatorNameContainer().get(indexOf);
                        long time = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                        String reason = getBannedPlayersContainer().getReasonContainer().get(indexOf);
                        String realDate = getBannedPlayersContainer().getRealBanDateContainer().get(indexOf);
                        String realTime = getBannedPlayersContainer().getRealBanTimeContainer().get(indexOf);
                        String id = getBannedPlayersContainer().getIdsContainer().get(indexOf);
                        BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(indexOf);
                        this.banManager.getBanContainerManager().removeFromBanContainer("-n", player.getName());
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player.getName());
                        getSQLiteManager().insertIntoBannedPlayers(id, player.getAddress().getAddress().getHostAddress(), player.getName(), initiatorName, reason, banType, realDate, realTime, player.getUniqueId(), time);
                        getBannedPlayersContainer().addToBansContainer(
                                id,
                                player.getAddress().getAddress().getHostAddress(),
                                player.getName(),
                                initiatorName,
                                reason,
                                banType,
                                realDate,
                                realTime,
                                String.valueOf(player.getUniqueId()),
                                time
                        );
                        if(banType == BanType.PERMANENT_NOT_IP) {
                            player.kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                            if (getConfigSettings().isConsoleNotification()) {
                                Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                            }
                            if(getConfigSettings().isPlayersNotification()) {
                                for(Player admin : Bukkit.getOnlinePlayers()) {
                                    if(admin.hasPermission("functionalbans.notification.ban")) {
                                        Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                                    }
                                }
                            }
                        }
                        if(banType == BanType.PERMANENT_IP) {
                            player.kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                            if (getConfigSettings().isConsoleNotification()) {
                                Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                            }
                            if(getConfigSettings().isPlayersNotification()) {
                                for(Player admin : Bukkit.getOnlinePlayers()) {
                                    if(admin.hasPermission("functionalbans.notification.ban")) {
                                        Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                                    }
                                }
                            }
                        }
                        if(banType == BanType.TIMED_NOT_IP) {
                            player.kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time)))));
                            if (getConfigSettings().isConsoleNotification()) {
                                Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time)))));
                            }
                            if(getConfigSettings().isPlayersNotification()) {
                                for(Player admin : Bukkit.getOnlinePlayers()) {
                                    if(admin.hasPermission("functionalbans.notification.ban")) {
                                        Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time)))));
                                    }
                                }
                            }
                        }
                        if(banType == BanType.TIMED_IP) {
                            player.kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time)))));
                            if (getConfigSettings().isConsoleNotification()) {
                                Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                            }
                            if(getConfigSettings().isPlayersNotification()) {
                                for(Player admin : Bukkit.getOnlinePlayers()) {
                                    if(admin.hasPermission("functionalbans.notification.ban")) {
                                        Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                                    }
                                }
                            }
                        }
                        return;
                    }
                }

                case MYSQL: {
                    break;
                }

                case H2: {
                    break;
                }

                default: {
                    if(getBannedPlayersContainer().getNameContainer().contains(player.getName()) && getBannedPlayersContainer().getUUIDContainer().get(getBannedPlayersContainer().getNameContainer().indexOf(player.getName())).equalsIgnoreCase("NULL_PLAYER")) {
                        int indexOf = getBannedPlayersContainer().getNameContainer().indexOf(player.getName());
                        String initiatorName = getBannedPlayersContainer().getInitiatorNameContainer().get(indexOf);
                        long time = getBannedPlayersContainer().getBanTimeContainer().get(indexOf);
                        String reason = getBannedPlayersContainer().getReasonContainer().get(indexOf);
                        String realDate = getBannedPlayersContainer().getRealBanDateContainer().get(indexOf);
                        String realTime = getBannedPlayersContainer().getRealBanTimeContainer().get(indexOf);
                        String id = getBannedPlayersContainer().getIdsContainer().get(indexOf);
                        BanType banType = getBannedPlayersContainer().getBanTypesContainer().get(indexOf);
                        this.banManager.getBanContainerManager().removeFromBanContainer("-n", player.getName());
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player.getName());
                        getSQLiteManager().insertIntoBannedPlayers(id, player.getAddress().getAddress().getHostAddress(), player.getName(), initiatorName, reason, banType, realDate, realTime, player.getUniqueId(), time);
                        getBannedPlayersContainer().addToBansContainer(
                                id,
                                player.getAddress().getAddress().getHostAddress(),
                                player.getName(),
                                initiatorName,
                                reason,
                                banType,
                                realDate,
                                realTime,
                                String.valueOf(player.getUniqueId()),
                                time
                        );
                        if(banType == BanType.PERMANENT_NOT_IP) {
                            player.kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                            if (getConfigSettings().isConsoleNotification()) {
                                Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                            }
                            if(getConfigSettings().isPlayersNotification()) {
                                for(Player admin : Bukkit.getOnlinePlayers()) {
                                    if(admin.hasPermission("functionalbans.notification.ban")) {
                                        Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", getGlobalVariables().getVariableNever())));
                                    }
                                }
                            }
                        }
                        if(banType == BanType.PERMANENT_IP) {
                            player.kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                            if (getConfigSettings().isConsoleNotification()) {
                                Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                            }
                            if(getConfigSettings().isPlayersNotification()) {
                                for(Player admin : Bukkit.getOnlinePlayers()) {
                                    if(admin.hasPermission("functionalbans.notification.ban")) {
                                        Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", getGlobalVariables().getVariableNever()).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                                    }
                                }
                            }
                        }
                        if(banType == BanType.TIMED_NOT_IP) {
                            player.kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time)))));
                            if (getConfigSettings().isConsoleNotification()) {
                                Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time)))));
                            }
                            if(getConfigSettings().isPlayersNotification()) {
                                for(Player admin : Bukkit.getOnlinePlayers()) {
                                    if(admin.hasPermission("functionalbans.notification.ban")) {
                                        Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban").replace("%1$f", player.getName()).replace("%2$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time)))));
                                    }
                                }
                            }
                        }
                        if(banType == BanType.TIMED_IP) {
                            player.kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time)))));
                            if (getConfigSettings().isConsoleNotification()) {
                                Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                            }
                            if(getConfigSettings().isPlayersNotification()) {
                                for(Player admin : Bukkit.getOnlinePlayers()) {
                                    if(admin.hasPermission("functionalbans.notification.ban")) {
                                        Bukkit.getConsoleSender().sendMessage(setColors(this.accessor.getLang().getString("other.notifications.ban-ip").replace("%1$f", player.getName()).replace("%3$f", this.timeSettingsAccessor.getTimeManager().convertFromMillis(this.timeSettingsAccessor.getTimeManager().getBanTime(time))).replace("%2$f", player.getAddress().getAddress().getHostAddress())));
                                    }
                                }
                            }
                        }
                        return;
                    }
                }

            }
        }
    }

}
