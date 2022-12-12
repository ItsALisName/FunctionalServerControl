package by.alis.functionalbans.spigot.Managers.BansManagers;

import by.alis.functionalbans.API.Spigot.Events.AsyncBanPreprocessEvent;
import by.alis.functionalbans.API.Spigot.Events.AsyncUnbanPreprocessEvent;
import by.alis.functionalbans.spigot.Additional.Enums.BanType;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.Languages.LangEnglish;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.Languages.LangRussian;
import by.alis.functionalbans.spigot.Additional.WorldDate.WorldTimeAndDateClass;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.IdsManager;
import by.alis.functionalbans.spigot.Managers.FilesManagers.FileAccessor;
import by.alis.functionalbans.spigot.Managers.TimeManagers.TimeManager;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static by.alis.functionalbans.spigot.Additional.Containers.StaticContainers.getBannedPlayersContainer;
import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.*;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;
import static by.alis.functionalbans.databases.StaticBases.getSQLiteManager;

public class BanManager {

    FileAccessor accessor = new FileAccessor();
    IdsManager idsManager = new IdsManager();
    TimeManager timeManager = new TimeManager();

    BanContainerManager banContainerManager = new BanContainerManager();

    /**
     * Checks if null player is banned
     * @param nullPlayerName - player name who never player on the server
     * @return true if nickname banned
     */
    public boolean isPlayerBanned(String nullPlayerName) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getNameContainer().contains(nullPlayerName);
        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    return getSQLiteManager().getBannedPlayersNames().contains(nullPlayerName);
                }
                case MYSQL: {
                    return false;
                }
                case H2: {
                    return false;
                }
                default: {
                    return getSQLiteManager().getBannedPlayersNames().contains(nullPlayerName);
                }
            }
        }
    }

    /**
     * Checks if a player is banned
     * @param player - player to be tested
     * @return true if player banned
     */
    public boolean isPlayerBanned(OfflinePlayer player) {
        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getNameContainer().contains(player.getName()) && getBannedPlayersContainer().getUUIDContainer().contains(String.valueOf(player.getUniqueId()));
        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    return getSQLiteManager().getBannedUUIDs().contains(String.valueOf(player.getUniqueId())) && getSQLiteManager().getBannedPlayersNames().contains(player.getName());
                }
                case MYSQL: {
                    return false;
                }
                case H2: {
                    return false;
                }
                default: {
                    return getSQLiteManager().getBannedUUIDs().contains(String.valueOf(player.getUniqueId())) && getSQLiteManager().getBannedPlayersNames().contains(player.getName());
                }
            }
        }
    }

    /**
     * Checks if ip is banned
     * @param ipAddress - ip to be tested
     * @return true if IP banned
     */
    public boolean isIpBanned(String ipAddress) {


        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getIpContainer().contains(ipAddress);
        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    return getSQLiteManager().getBannedIps().contains(ipAddress);
                }
                case MYSQL: {
                    break;
                }
                case H2: {
                    break;
                }
                default: {
                    return getSQLiteManager().getBannedIps().contains(ipAddress);
                }
            }
        }
        return false;
    }

    /**
     * Checks if the IP of the specified player is banned
     * @param player - player whose ip will be verified
     * @return true if player ip is banned
     */
    public boolean isIpBanned(OfflinePlayer player) {

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            return getBannedPlayersContainer().getIpContainer().contains(getSQLiteManager().selectIpByUUID(player.getUniqueId()));
        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    return getSQLiteManager().getBannedIps().contains(getSQLiteManager().selectIpByUUID(player.getUniqueId())) && getSQLiteManager().getBannedUUIDs().contains(String.valueOf(player.getUniqueId()));
                }
                case MYSQL: {
                    return false;
                }
                case H2: {
                    return false;
                }
                default: {
                    return getSQLiteManager().getBannedIps().contains(getSQLiteManager().selectIpByUUID(player.getUniqueId())) && getSQLiteManager().getBannedUUIDs().contains(String.valueOf(player.getUniqueId()));
                }
            }
        }
    }

    /**
     * Blocks a player who has ever played on the server (i.e. not a null player)
     * @param player - player
     * @param type - Ban type
     * @param reason - Ban reason
     * @param initiator - Who blocked
     * @param time - Ban time
     * @param announceBan - If true, then the blocking will be notified in the chat
     */
    public void preformBan(OfflinePlayer player, BanType type, String reason, CommandSender initiator, long time, boolean announceBan) {

        String realTime = WorldTimeAndDateClass.getTime();
        String realDate = WorldTimeAndDateClass.getDate();
        if(type == BanType.PERMANENT_IP || type == BanType.PERMANENT_NOT_IP) time = -1;
        if(reason == null || reason.equalsIgnoreCase("")) {
            reason = getGlobalVariables().getDefaultReason();
        }
        String convertedTime;
        if(time < 0) {
            convertedTime = getGlobalVariables().getVarUnknownTime();
        } else {
            convertedTime = this.timeManager.convertFromMillis(this.timeManager.getBanTime(time));
        }
        String id = idsManager.getId();
        AsyncBanPreprocessEvent banPlayerEvent = new AsyncBanPreprocessEvent(id, player, initiator, type, time, reason, realTime, realDate, getConfigSettings().isApiEnabled(), convertedTime);
        if(getConfigSettings().isApiEnabled()) {
            if(!getConfigSettings().isApiProtectedByPassword()) {
                Bukkit.getPluginManager().callEvent(banPlayerEvent);
            } else {
                if(banPlayerEvent.getApiPassword() != null && banPlayerEvent.getApiPassword().equalsIgnoreCase(this.accessor.getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(banPlayerEvent);
                }
            }
        }
        if(banPlayerEvent.isCancelled()) return;
        reason = banPlayerEvent.getReason().replace("'", "\"");
        String initiatorName = null;
        if(initiator instanceof Player) {
            initiatorName = ((Player)initiator).getPlayerListName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }


        if(type == BanType.PERMANENT_NOT_IP) {
            if (isPlayerBanned(player)) {
                if (initiator.hasPermission("functionalbans.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ingored) {}
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId()));} catch (NullPointerException ingored) {}
                                getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                                break;
                            }
                            case MYSQL: {
                                break;
                            }
                            case H2: {
                                break;
                            }
                            default: {
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ingored) {}
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId()));} catch (NullPointerException ingored) {}
                                getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                                break;
                            }
                        }
                        try { getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId())); } catch (NullPointerException ingored) {}
                        try { getBanContainerManager().removeFromBanContainer("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        getBanContainerManager().addToBanContainer(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
                        if(player.isOnline()) {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));                            }
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.last-ban-removed").replace("%1$f", player.getName())));
                        if(announceBan) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.ban.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ingored) {}
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId()));} catch (NullPointerException ingored) {}
                                getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                                break;
                            }
                            case MYSQL: {
                                break;
                            }
                            case H2: {
                                break;
                            }
                            default: {
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ingored) {}
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId()));} catch (NullPointerException ingored) {}
                                getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                                break;
                            }
                        }
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.last-ban-removed").replace("%1$f", player.getName())));
                        if(player.isOnline()) {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", reason).replace("%3$f", initiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                        }
                        if(announceBan) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.ban.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                    }
                } else {
                    initiator.sendMessage(setColors(this.accessor.getLang().getString("other.already-banned")));
                    return;
                }
            } else {

                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                            break;
                        }
                        case MYSQL: {
                            break;
                        }
                        case H2: {
                            break;
                        }
                        default: {
                            getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                            break;
                        }
                    }
                    getBanContainerManager().addToBanContainer(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
                    if(announceBan) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.ban.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        String finalReason = reason;
                        String finalInitiatorName = initiatorName;
                        Bukkit.getScheduler().runTask(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                        });
                    }
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                            break;
                        }
                        case MYSQL: {
                            break;
                        }
                        case H2: {
                            break;
                        }
                        default: {
                            getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                            break;
                        }
                    }
                    getBanContainerManager().addToBanContainer(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
                    if(announceBan) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.ban.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        String finalReason = reason;
                        String finalInitiatorName = initiatorName;
                        Bukkit.getScheduler().runTask(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                        });
                    }
                }
            }
        }

        if(type == BanType.PERMANENT_IP) {
            if(isIpBanned(player)) {
                if(initiator.hasPermission("functionalbans.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                                break;
                            }
                            case MYSQL: {
                                break;
                            }
                            case H2: {
                                break;
                            }
                            default: {
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                                break;
                            }
                        }
                        try { getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId())); } catch (NullPointerException ingored) {}
                        try { getBanContainerManager().removeFromBanContainer("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        getBanContainerManager().addToBanContainer(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.last-ban-removed").replace("%1$f", player.getName())));
                        if(announceBan) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.ban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason)));
                        }
                        if(player.isOnline()) {
                            String finalReason = reason;
                            String finalInitiatorName = initiatorName;
                            Bukkit.getScheduler().runTask(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                                player.getPlayer().kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                            });
                        }
                    } else {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                                break;
                            }
                            case MYSQL: {
                                break;
                            }
                            case H2: {
                                break;
                            }
                            default: {
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                                break;
                            }
                        }
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.last-ban-removed").replace("%1$f", player.getName())));
                        if(announceBan) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.ban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", reason)));
                        }
                        if(player.isOnline()) {
                            String finalReason = reason;
                            String finalInitiatorName = initiatorName;
                            Bukkit.getScheduler().runTask(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                                player.getPlayer().kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                            });
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(setColors(this.accessor.getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                            break;
                        }
                        case MYSQL: {
                            break;
                        }
                        case H2: {
                            break;
                        }
                        default: {
                            getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                            break;
                        }
                    }
                    getBanContainerManager().addToBanContainer(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), -1L);
                    if(announceBan) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        String finalReason = reason;
                        String finalInitiatorName = initiatorName;
                        Bukkit.getScheduler().runTask(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                        });
                    }
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                            break;
                        }
                        case MYSQL: {
                            break;
                        }
                        case H2: {
                            break;
                        }
                        default: {
                            getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), -1);
                            break;
                        }
                    }
                    if(announceBan) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    if(player.isOnline()) {
                        String finalReason = reason;
                        String finalInitiatorName = initiatorName;
                        Bukkit.getScheduler().runTask(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", getGlobalVariables().getVariableNever())));
                        });
                    }
                    return;
                }
            }
        }

        if(type == BanType.TIMED_NOT_IP) {
            if (isPlayerBanned(player)) {
                if (initiator.hasPermission("functionalbans.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                                break;
                            }
                            case MYSQL: {
                                break;
                            }
                            case H2: {
                                break;
                            }
                            default: {
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                                break;
                            }
                        }
                        try { getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        try { getBanContainerManager().removeFromBanContainer("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        getBanContainerManager().addToBanContainer(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), banPlayerEvent.getBanTime());
                        if(player.isOnline()) {
                            String finalInitiatorName = initiatorName;
                            String finalReason = reason;
                            Bukkit.getScheduler().runTask(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                                player.getPlayer().kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime())))));
                            });
                        }
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.last-ban-removed").replace("%1$f", player.getName())));
                        if(announceBan) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.tempban.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))).replace("%1$f", initiatorName).replace("%4$f", reason)));
                        }
                        return;
                    } else {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                                break;
                            }
                            case MYSQL: {
                                break;
                            }
                            case H2: {
                                break;
                            }
                            default: {
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                                break;
                            }
                        }
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.last-ban-removed").replace("%1$f", player.getName())));
                        if(player.isOnline()) {
                            String finalReason = reason;
                            String finalInitiatorName = initiatorName;
                            Bukkit.getScheduler().runTask(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                                player.getPlayer().kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime())))));
                            });
                        }
                        if(announceBan) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.tempban.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))).replace("%1$f", initiatorName).replace("%4$f", reason)));
                        }
                    }
                    return;
                } else {
                    initiator.sendMessage(setColors(this.accessor.getLang().getString("other.already-banned")));
                    return;
                }
            } else {

                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                            break;
                        }
                        case MYSQL: {
                            break;
                        }
                        case H2: {
                            break;
                        }
                        default: {
                            getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                            break;
                        }
                    }
                    getBanContainerManager().addToBanContainer(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), banPlayerEvent.getBanTime());
                    if(announceBan) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.tempban.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))).replace("%1$f", initiatorName).replace("%4$f", reason)));
                    }
                    if(player.isOnline()) {
                        String finalReason = reason;
                        String finalInitiatorName = initiatorName;
                        Bukkit.getScheduler().runTask(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime())))));
                        });
                    }
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                            break;
                        }
                        case MYSQL: {
                            break;
                        }
                        case H2: {
                            break;
                        }
                        default: {
                            getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                            break;
                        }
                    }
                    if(player.isOnline()) {
                        String finalReason = reason;
                        String finalInitiatorName = initiatorName;
                        Bukkit.getScheduler().runTask(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-message-format")).replace("%1$f", String.valueOf(id)).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime())))));
                        });
                    }
                    if(announceBan) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.tempban.broadcast-message").replace("%2$f", player.getName()).replace("%3$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))).replace("%1$f", initiatorName).replace("%4$f", reason)));
                    }
                    return;
                }
            }
        }

        if(type == BanType.TIMED_IP) {
            if(isIpBanned(player)) {
                if(initiator.hasPermission("functionalbans.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                                break;
                            }
                            case MYSQL: {
                                break;
                            }
                            case H2: {
                                break;
                            }
                            default: {
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                                break;
                            }
                        }
                        try { getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        try { getBanContainerManager().removeFromBanContainer("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId())); } catch (NullPointerException ignored) {}
                        getBanContainerManager().addToBanContainer(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), banPlayerEvent.getBanTime());
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.last-ban-removed").replace("%1$f", player.getName())));
                        if(announceBan) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                        if(player.isOnline()) {
                            String finalReason = reason;
                            String finalInitiatorName = initiatorName;
                            Bukkit.getScheduler().runTask(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                                player.getPlayer().kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName)).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))));
                            });
                        }
                    } else {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId()));} catch (NullPointerException ingored) {}
                                getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                                break;
                            }
                            case MYSQL: {
                                break;
                            }
                            case H2: {
                                break;
                            }
                            default: {
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));} catch (NullPointerException ignored) {}
                                try {
                                    getSQLiteManager().deleteFromBannedPlayers("-ip", getSQLiteManager().selectIpByUUID(player.getUniqueId()));} catch (NullPointerException ingored) {}
                                getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                                break;
                            }
                        }
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.last-ban-removed").replace("%1$f", player.getName())));
                        if(announceBan) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                        if(player.isOnline()) {
                            String finalReason = reason;
                            String finalInitiatorName = initiatorName;
                            Bukkit.getScheduler().runTask(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                                player.getPlayer().kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("ban-ip-message-format")).replace("%1$f", id).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName)).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))));
                            });
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(setColors(this.accessor.getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                            break;
                        }
                        case MYSQL: {
                            break;
                        }
                        case H2: {
                            break;
                        }
                        default: {
                            getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                            break;
                        }
                    }
                    getBanContainerManager().addToBanContainer(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, String.valueOf(player.getUniqueId()), banPlayerEvent.getBanTime());
                    if(announceBan) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                    if(player.isOnline()) {
                        String finalInitiatorName = initiatorName;
                        String finalReason = reason;
                        Bukkit.getScheduler().runTask(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName)).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))));
                        });
                    }
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                            break;
                        }
                        case MYSQL: {
                            break;
                        }
                        case H2: {
                            break;
                        }
                        default: {
                            getSQLiteManager().insertIntoBannedPlayers(id, getSQLiteManager().selectIpByUUID(player.getUniqueId()), player.getName(), initiatorName, reason, type, realDate, realTime, player.getUniqueId(), banPlayerEvent.getBanTime());
                            break;
                        }
                    }
                    if(announceBan) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                    if(player.isOnline()) {
                        String finalInitiatorName = initiatorName;
                        String finalReason = reason;
                        Bukkit.getScheduler().runTask(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class), () -> {
                            player.getPlayer().kickPlayer(setColors(String.join("\n", this.accessor.getLang().getStringList("temporary-ban-ip-message-format")).replace("%1$f", id).replace("%2$f", finalReason).replace("%3$f", finalInitiatorName)).replace("%4$f", realDate + ", " + realTime).replace("%5$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))));
                        });
                    }
                }
            }
        }

    }

    public void preformBan(String player, BanType type, String reason, CommandSender initiator, long time, boolean announceBan) {

        String realTime = WorldTimeAndDateClass.getTime();
        String realDate = WorldTimeAndDateClass.getDate();
        String convertedTime;
        if(time < 0) {
            convertedTime = getGlobalVariables().getVariableNever();
        } else {
            convertedTime = this.timeManager.convertFromMillis(time - System.currentTimeMillis());
        }
        String id = idsManager.getId();
        if(reason == null || reason.equalsIgnoreCase("")) {
            reason = getGlobalVariables().getDefaultReason();
        }
        AsyncBanPreprocessEvent banPlayerEvent = new AsyncBanPreprocessEvent(id, player, initiator, type, time, reason, realTime, realDate, getConfigSettings().isApiEnabled(), convertedTime);
        reason = banPlayerEvent.getReason().replace("'", "\"");
        time = banPlayerEvent.getBanTime();
        if(type == BanType.PERMANENT_IP || type == BanType.PERMANENT_NOT_IP) time = -1;
        if(getConfigSettings().isApiEnabled()) {
            if(!getConfigSettings().isApiProtectedByPassword()) {
                Bukkit.getPluginManager().callEvent(banPlayerEvent);
            } else {
                if(banPlayerEvent.getApiPassword() != null && banPlayerEvent.getApiPassword().equalsIgnoreCase(this.accessor.getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(banPlayerEvent);
                }
            }
        }
        if(banPlayerEvent.isCancelled()) return;

        String initiatorName = "ERROR";
        if(initiator instanceof Player) {
            initiatorName = ((Player)initiator).getPlayerListName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }

        if(type == BanType.PERMANENT_NOT_IP) {
            if(isPlayerBanned(player)) {
                if(initiator.hasPermission("functionalbans.use.re-ban")) {
                    if(getConfigSettings().isAllowedUseRamAsContainer()) {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                break;
                            }
                            case MYSQL: {
                                break;
                            }
                            case H2: {
                                break;
                            }
                            default: {
                                getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                break;
                            }
                        }
                        getBanContainerManager().removeFromBanContainer("-n", player);
                        getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if(announceBan) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.ban.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                break;
                            }
                            case MYSQL: {
                                break;
                            }
                            case H2: {
                                break;
                            }
                            default: {
                                getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                break;
                            }
                        }
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if(announceBan) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.ban.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(setColors(this.accessor.getLang().getString("other.already-banned") ));
                    return;
                }
            } else {
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            break;
                        }
                        case MYSQL: {
                            break;
                        }
                        case H2: {
                            break;
                        }
                        default: {
                            getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            break;
                        }
                    }
                    getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                    initiator.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if(announceBan) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.ban.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            break;
                        }
                        case MYSQL: {
                            break;
                        }
                        case H2: {
                            break;
                        }
                        default: {
                            getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            break;
                        }
                    }
                    initiator.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if(announceBan) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.ban.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if(type == BanType.PERMANENT_IP) {
            if(isPlayerBanned(player)) {
                if(initiator.hasPermission("functionalbans.use.re-ban")) {
                    if(getConfigSettings().isAllowedUseRamAsContainer()) {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                break;
                            }
                            case MYSQL: {
                                break;
                            }
                            case H2: {
                                break;
                            }
                            default: {
                                getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                break;
                            }
                        }
                        getBanContainerManager().removeFromBanContainer("-n", player);
                        getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", banPlayerEvent.getBanTime());
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if(announceBan) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    } else {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                break;
                            }
                            case MYSQL: {
                                break;
                            }
                            case H2: {
                                break;
                            }
                            default: {
                                getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                break;
                            }
                        }
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if(announceBan) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                        }
                        return;
                    }
                } else {
                    initiator.sendMessage(setColors(this.accessor.getLang().getString("other.already-banned") ));
                    return;
                }
            } else {
                if(getConfigSettings().isAllowedUseRamAsContainer()) {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            break;
                        }
                        case MYSQL: {
                            break;
                        }
                        case H2: {
                            break;
                        }
                        default: {
                            getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            break;
                        }
                    }
                    getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", banPlayerEvent.getBanTime());
                    initiator.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if(announceBan) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            break;
                        }
                        case MYSQL: {
                            break;
                        }
                        case H2: {
                            break;
                        }
                        default: {
                            getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            break;
                        }
                    }
                    initiator.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if(announceBan) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.ban-ip.broadcast-message").replace("%2$f", player).replace("%3$f", reason).replace("%1$f", initiatorName)));
                    }
                    return;
                }
            }
        }

        if(type == BanType.TIMED_IP) {
            if(isPlayerBanned(player)) {
                if(initiator.hasPermission("functionalbans.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromBannedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                break;
                            }
                            case MYSQL: {
                                break;
                            }
                            case H2: {
                                break;
                            }
                            default: {
                                getSQLiteManager().deleteFromBannedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                break;
                            }
                        }
                        getBanContainerManager().removeFromBanContainer("-n", player);
                        getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if(announceBan) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromBannedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            }
                            case MYSQL: {
                                break;
                            }
                            case H2: {
                                break;
                            }
                            default: {
                                getSQLiteManager().deleteFromBannedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            }
                        }
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if(announceBan) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(setColors(this.accessor.getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            break;
                        }
                        case MYSQL: {
                            break;
                        }
                        case H2: {
                            break;
                        }
                        default: {
                            getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            break;
                        }
                    }
                    getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                    initiator.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if(announceBan) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            break;
                        }
                        case MYSQL: {
                            break;
                        }
                        case H2: {
                            break;
                        }
                        default: {
                            getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            break;
                        }
                    }
                    initiator.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if(announceBan) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.tempban-ip.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                }
            }
        }

        if(type == BanType.TIMED_NOT_IP) {
            if(isPlayerBanned(player)) {
                if(initiator.hasPermission("functionalbans.use.re-ban")) {
                    if (getConfigSettings().isAllowedUseRamAsContainer()) {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromBannedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                break;
                            }
                            case MYSQL: {
                                break;
                            }
                            case H2: {
                                break;
                            }
                            default: {
                                getSQLiteManager().deleteFromBannedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                break;
                            }
                        }
                        getBanContainerManager().removeFromBanContainer("-n", player);
                        getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if(announceBan) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.tempban.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    } else {
                        switch (getConfigSettings().getStorageType()) {
                            case SQLITE: {
                                getSQLiteManager().deleteFromBannedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                break;
                            }
                            case MYSQL: {
                                break;
                            }
                            case H2: {
                                break;
                            }
                            default: {
                                getSQLiteManager().deleteFromBannedPlayers("-n", player);
                                getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                                break;
                            }
                        }
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-player").replace("%1$f", player)));
                        initiator.sendMessage(setColors(this.accessor.getLang().getString("other.last-ban-removed").replace("%1$f", player)));
                        if(announceBan) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.tempban.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                        }
                    }
                } else {
                    initiator.sendMessage(setColors(this.accessor.getLang().getString("other.already-banned")));
                    return;
                }
            } else {
                if (getConfigSettings().isAllowedUseRamAsContainer()) {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            break;
                        }
                        case MYSQL: {
                            break;
                        }
                        case H2: {
                            break;
                        }
                        default: {
                            getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            break;
                        }
                    }
                    getBanContainerManager().addToBanContainer(id, "NULL_PLAYER", player, initiatorName, reason, type, realDate, realTime, "NULL_PLAYER", time);
                    initiator.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if(announceBan) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.tempban.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                } else {
                    switch (getConfigSettings().getStorageType()) {
                        case SQLITE: {
                            getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            break;
                        }
                        case MYSQL: {
                            break;
                        }
                        case H2: {
                            break;
                        }
                        default: {
                            getSQLiteManager().insertIntoNullBannedPlayers(id, player, initiatorName, reason, type, realDate, realTime, time);
                            break;
                        }
                    }
                    initiator.sendMessage(setColors(this.accessor.getLang().getString("other.unknown-player").replace("%1$f", player)));
                    if(announceBan) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.tempban.broadcast-message").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", this.timeManager.convertFromMillis(this.timeManager.getBanTime(banPlayerEvent.getBanTime()))).replace("%4$f", reason)));
                    }
                }
            }
        }

    }

    public void preformUnban(OfflinePlayer player, CommandSender unbanInitiator, String unbanReason, boolean announceUnban) {
        String initiatorName = null;
        if(unbanInitiator instanceof Player) {
            initiatorName = ((Player) unbanInitiator).getPlayerListName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }
        if(!isPlayerBanned(player)) {
            unbanInitiator.sendMessage(setColors(this.accessor.getLang().getString("commands.unban.player-not-banned")).replace("%1$f", player.getName()));
            return;
        }

        AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(player, unbanInitiator, unbanReason, getConfigSettings().isApiEnabled());

        if(getConfigSettings().isApiEnabled()) {
            if(getConfigSettings().isApiProtectedByPassword()) {
                if(asyncUnbanPreprocessEvent.getApiPassword() != null && asyncUnbanPreprocessEvent.getApiPassword().equalsIgnoreCase(this.accessor.getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
                }
            } else {
                Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
            }
        }

        if(asyncUnbanPreprocessEvent.isCancelled()) return;

        unbanReason = asyncUnbanPreprocessEvent.getReason();

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            try {
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                        break;
                    }
                    case MYSQL: {
                        break;
                    }
                    case H2: {
                        break;
                    }
                    default: {
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                        break;
                    }
                }
                this.getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId()));
                if(unbanReason == "" || unbanReason == null) {
                    if(announceUnban) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName())));
                    }
                } else {
                    if(announceUnban) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", unbanReason)));
                    }
                }
                return;
            } catch (NullPointerException ignored) {
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                        break;
                    }
                    case "en_US": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                        break;
                    }
                    default: {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                        break;
                    }
                }
            }

        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    try {
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                    } catch (NullPointerException ignored) {
                        switch (getConfigSettings().getConsoleLanguageMode()) {
                            case "ru_RU": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                                break;
                            }
                            case "en_US": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                            default: {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                        }
                    }
                    if(unbanReason == "" || unbanReason == null) {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName())));
                        }
                    } else {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", unbanReason)));
                        }
                    }
                    break;
                }
                case MYSQL: {
                    break;
                }
                case H2: {
                    break;
                }
                default: {
                    try {
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                    } catch (NullPointerException ignored) {
                        switch (getConfigSettings().getConsoleLanguageMode()) {
                            case "ru_RU": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                                break;
                            }
                            case "en_US": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                            default: {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                        }
                    }
                    if(unbanReason == "" || unbanReason == null) {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName())));
                        }
                    } else {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player.getName()).replace("%3$f", unbanReason)));
                        }
                    }
                    break;
                }
            }
        }

    }

    public void preformUnban(String player, CommandSender unbanInitiator, String unbanReason, boolean announceUnban) {
        String initiatorName = null;
        if(!isPlayerBanned(player)) {
            unbanInitiator.sendMessage(setColors(this.accessor.getLang().getString("commands.unban.player-not-banned")).replace("%1$f", player));
            return;
        }
        if(unbanInitiator instanceof Player) {
            initiatorName = ((Player) unbanInitiator).getPlayerListName();
        } else {
            initiatorName = getGlobalVariables().getConsoleVariableName();
        }

        AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(player, unbanInitiator, unbanReason, getConfigSettings().isApiEnabled());

        if(getConfigSettings().isApiEnabled()) {
            if(getConfigSettings().isApiProtectedByPassword()) {
                if(asyncUnbanPreprocessEvent.getApiPassword() != null && asyncUnbanPreprocessEvent.getApiPassword().equalsIgnoreCase(this.accessor.getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
                }
            } else {
                Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
            }
        }

        unbanReason = asyncUnbanPreprocessEvent.getReason();

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            try {
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                        break;
                    }
                    case MYSQL: {
                        break;
                    }
                    case H2: {
                        break;
                    }
                    default: {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                        break;
                    }
                }
                this.getBanContainerManager().removeFromBanContainer("-n", player);
                if(unbanReason == "" || unbanReason == null) {
                    if(announceUnban) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
                    }
                } else {
                    if(announceUnban) {
                        Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unbanReason)));
                    }
                }
                return;
            } catch (NullPointerException ignored) {
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                        break;
                    }
                    case "en_US": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                        break;
                    }
                    default: {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                        break;
                    }
                }
            }

        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    try {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                    } catch (NullPointerException ignored) {
                        switch (getConfigSettings().getConsoleLanguageMode()) {
                            case "ru_RU": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                                break;
                            }
                            case "en_US": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                            default: {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                        }
                    }
                    if(unbanReason == "" || unbanReason == null) {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
                        }
                    } else {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unbanReason)));
                        }
                    }
                    break;
                }
                case MYSQL: {
                    break;
                }
                case H2: {
                    break;
                }
                default: {
                    try {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                    } catch (NullPointerException ignored) {
                        switch (getConfigSettings().getConsoleLanguageMode()) {
                            case "ru_RU": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                                break;
                            }
                            case "en_US": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                            default: {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                        }
                    }
                    if(unbanReason == "" || unbanReason == null) {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.without-reason").replace("%1$f", initiatorName).replace("%2$f", player)));
                        }
                    } else {
                        if(announceUnban) {
                            Bukkit.broadcastMessage(setColors(this.accessor.getLang().getString("commands.unban.broadcast-message.with-reason").replace("%1$f", initiatorName).replace("%2$f", player).replace("%3$f", unbanReason)));
                        }
                    }
                    break;
                }
            }
        }
    }

    public void preformUnban(OfflinePlayer player, String unbanReason) { //NEW
        AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(player, unbanReason, getConfigSettings().isApiEnabled());

        if(getConfigSettings().isApiEnabled()) {
            if(getConfigSettings().isApiProtectedByPassword()) {
                if(asyncUnbanPreprocessEvent.getApiPassword() != null && asyncUnbanPreprocessEvent.getApiPassword().equalsIgnoreCase(this.accessor.getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
                }
            } else {
                Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
            }
        }

        if(asyncUnbanPreprocessEvent.isCancelled()) return;

        unbanReason = "The Ban time has expired";

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            try {
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                        break;
                    }
                    case MYSQL: {
                        break;
                    }
                    case H2: {
                        break;
                    }
                    default: {
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                        break;
                    }
                }
                this.getBanContainerManager().removeFromBanContainer("-u", String.valueOf(player.getUniqueId()));
                return;
            } catch (NullPointerException ignored) {
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                        break;
                    }
                    case "en_US": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                        break;
                    }
                    default: {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                        break;
                    }
                }
            }

        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    try {
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                    } catch (NullPointerException ignored) {
                        switch (getConfigSettings().getConsoleLanguageMode()) {
                            case "ru_RU": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                                break;
                            }
                            case "en_US": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                            default: {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                        }
                    }
                    break;
                }
                case MYSQL: {
                    break;
                }
                case H2: {
                    break;
                }
                default: {
                    try {
                        getSQLiteManager().deleteFromBannedPlayers("-u", String.valueOf(player.getUniqueId()));
                    } catch (NullPointerException ignored) {
                        switch (getConfigSettings().getConsoleLanguageMode()) {
                            case "ru_RU": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                                break;
                            }
                            case "en_US": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                            default: {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    public void preformUnban(String player, String unbanReason) { //Another

        AsyncUnbanPreprocessEvent asyncUnbanPreprocessEvent = new AsyncUnbanPreprocessEvent(player, unbanReason, getConfigSettings().isApiEnabled());

        if(getConfigSettings().isApiEnabled()) {
            if(getConfigSettings().isApiProtectedByPassword()) {
                if(asyncUnbanPreprocessEvent.getApiPassword() != null && asyncUnbanPreprocessEvent.getApiPassword().equalsIgnoreCase(this.accessor.getGeneralConfig().getString("plugin-settings.api.spigot.password.password"))) {
                    Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
                }
            } else {
                Bukkit.getPluginManager().callEvent(asyncUnbanPreprocessEvent);
            }
        }

        unbanReason = "The Ban time has expired";

        if(getConfigSettings().isAllowedUseRamAsContainer()) {
            try {
                switch (getConfigSettings().getStorageType()) {
                    case SQLITE: {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                        break;
                    }
                    case MYSQL: {
                        break;
                    }
                    case H2: {
                        break;
                    }
                    default: {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                        break;
                    }
                }
                this.getBanContainerManager().removeFromBanContainer("-n", player);
                return;
            } catch (NullPointerException ignored) {
                switch (getConfigSettings().getConsoleLanguageMode()) {
                    case "ru_RU": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                        break;
                    }
                    case "en_US": {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                        break;
                    }
                    default: {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                        break;
                    }
                }
            }

        } else {
            switch (getConfigSettings().getStorageType()) {
                case SQLITE: {
                    try {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                    } catch (NullPointerException ignored) {
                        switch (getConfigSettings().getConsoleLanguageMode()) {
                            case "ru_RU": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                                break;
                            }
                            case "en_US": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                            default: {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                        }
                    }
                    break;
                }
                case MYSQL: {
                    break;
                }
                case H2: {
                    break;
                }
                default: {
                    try {
                        getSQLiteManager().deleteFromNullBannedPlayers("-n", player);
                    } catch (NullPointerException ignored) {
                        switch (getConfigSettings().getConsoleLanguageMode()) {
                            case "ru_RU": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.UNBAN_FAILED));
                                break;
                            }
                            case "en_US": {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                            default: {
                                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.UNBAN_FAILED));
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
    }

    public BanContainerManager getBanContainerManager() {
        return banContainerManager;
    }
}
