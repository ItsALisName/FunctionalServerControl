package net.alis.functionalservercontrol.spigot.managers;

import net.alis.functionalservercontrol.api.enums.BanType;
import net.alis.functionalservercontrol.api.enums.MuteType;
import net.alis.functionalservercontrol.api.enums.StatsType;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

import static net.alis.functionalservercontrol.databases.DataBases.getMySQLManager;
import static net.alis.functionalservercontrol.databases.DataBases.getSQLiteManager;
import static net.alis.functionalservercontrol.spigot.additional.globalsettings.SettingsAccessor.getConfigSettings;

public class BaseManager {

    public void insertIntoPlayersPunishInfo(UUID uuid) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().insertIntoPlayersPunishInfo(uuid); break;
            }
            case MYSQL: {
                getMySQLManager().insertIntoPlayersPunishInfo(uuid); break;
            }
            case H2: {}
        }
    }

    public String getPlayerStatsInfo(OfflinePlayer player, StatsType.Player statsType) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getPlayerStatsInfo(player, statsType);
            }
            case MYSQL: {
                return getMySQLManager().getPlayerStatsInfo(player, statsType);
            }
            case H2: {}
        }
        return null;
    }

    public String getAdminStatsInfo(OfflinePlayer player, StatsType.Administrator statsType) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getAdminStatsInfo(player, statsType);
            }
            case MYSQL: {
                return getMySQLManager().getAdminStatsInfo(player, statsType);
            }
            case H2: {}
        }
        return null;
    }

    public void updatePlayerStatsInfo(OfflinePlayer player, StatsType.Player statsType) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().updatePlayerStatsInfo(player, statsType); break;
            }
            case MYSQL: {
                getMySQLManager().updatePlayerStatsInfo(player, statsType); break;
            }
            case H2: {}
        }
    }

    public void updateAdminStatsInfo(OfflinePlayer player, StatsType.Administrator statsType) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().updateAdminStatsInfo(player, statsType); break;
            }
            case MYSQL: {
                getMySQLManager().updateAdminStatsInfo(player, statsType); break;
            }
            case H2: {}
        }
    }

    public void insertIntoHistory(String message) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().insertIntoHistory(message); break;
            }
            case MYSQL: {
                getMySQLManager().insertIntoHistory(message); break;
            }
            case H2: {}
        }
    }

    public List<String> getRecordsFromHistory(CommandSender sender, int count, @Nullable String attribute) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getRecordsFromHistory(sender, count, attribute);
            }
            case MYSQL: {
                return getMySQLManager().getRecordsFromHistory(sender, count, attribute);
            }
            case H2: {}
        }
        return null;
    }

    public void deleteFromMutedPlayers(String expression, String param) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().deleteFromMutedPlayers(expression, param); break;
            }
            case MYSQL: {
                getMySQLManager().deleteFromMutedPlayers(expression, param); break;
            }
            case H2: {}
        }
    }

    public void deleteFromNullMutedPlayers(String expression, String param) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().deleteFromNullMutedPlayers(expression, param); break;
            }
            case MYSQL: {
                getMySQLManager().deleteFromNullMutedPlayers(expression, param); break;
            }
            case H2: {}
        }
    }

    @Nullable
    public String getUuidByName(String playerName) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getUuidByName(playerName);
            }
            case MYSQL: {
                return getMySQLManager().getUuidByName(playerName);
            }
            case H2: {}
        }
        return null;
    }

    public UUID getUUIDByIp(String ip) {
        switch (getConfigSettings().getStorageType()){
            case SQLITE: {
                return getSQLiteManager().getUUIDByIp(ip);
            }
            case MYSQL: {
                return getMySQLManager().getUUIDByIp(ip);
            }
            case H2: {}
        }
        return null;
    }

    public void insertIntoAllPlayers(String name, UUID uuid, String ip) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().insertIntoAllPlayers(name, uuid, ip); break;
            }
            case MYSQL: {
                getMySQLManager().insertIntoAllPlayers(name, uuid, ip); break;
            }
            case H2: {}
        }
    }

    public void deleteFromBannedPlayers(String expression, String param) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().deleteFromBannedPlayers(expression, param); break;
            }
            case MYSQL: {
                getMySQLManager().deleteFromBannedPlayers(expression, param); break;
            }
            case H2: {}
        }
    }

    public void deleteFromNullBannedPlayers(String expression, String param) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().deleteFromNullBannedPlayers(expression, param); break;
            }
            case MYSQL: {
                getMySQLManager().deleteFromNullBannedPlayers(expression, param); break;
            }
            case H2: {}
        }
    }

    public void clearHistory() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: getSQLiteManager().clearHistory(); break;
            case MYSQL: getMySQLManager().clearHistory(); break;
            case H2: {}
        }
    }

    public void insertIntoBannedPlayers(String id, String ip, String name, String initiatorName, String reason, BanType banType, String banDate, String banTime, UUID uuid, long unbanTime) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().insertIntoBannedPlayers(id, ip, name, initiatorName, reason, banType, banDate, banTime, uuid, unbanTime); break;
            }
            case MYSQL: {
                getMySQLManager().insertIntoBannedPlayers(id, ip, name, initiatorName, reason, banType, banDate, banTime, uuid, unbanTime); break;
            }
            case H2: {}
        }
    }

    public void insertIntoMutedPlayers(String id, String ip, String name, String initiatorName, String reason, MuteType muteType, String banDate, String banTime, UUID uuid, long unmuteTime) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().insertIntoMutedPlayers(id, ip, name, initiatorName, reason, muteType, banDate, banTime, uuid, unmuteTime); break;
            }
            case MYSQL: {
                getMySQLManager().insertIntoMutedPlayers(id, ip, name, initiatorName, reason, muteType, banDate, banTime, uuid, unmuteTime); break;
            }
            case H2: {}
        }
    }

    public void insertIntoNullMutedPlayers(String id, String name, String initiatorName, String reason, MuteType muteType, String muteDate, String muteTime, UUID uuid, long unmuteTime) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().insertIntoNullMutedPlayers(id, name, initiatorName, reason, muteType, muteDate, muteTime, uuid, unmuteTime); break;
            }
            case MYSQL: {
                getMySQLManager().insertIntoNullMutedPlayers(id, name, initiatorName, reason, muteType, muteDate, muteTime, uuid, unmuteTime); break;
            }
            case H2: {}
        }
    }

    public void insertIntoNullBannedPlayersIP(String id, String ip, String initiatorName, String reason, BanType banType, String banDate, String banTime, long unbanTime) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName, reason, banType, banDate, banTime, unbanTime); break;
            }
            case MYSQL: {
                getMySQLManager().insertIntoNullBannedPlayersIP(id, ip, initiatorName, reason, banType, banDate, banTime, unbanTime); break;
            }
            case H2: {}
        }
    }

    public void insertIntoNullBannedPlayers(String id, String name, String initiatorName, String reason, BanType banType, String banDate, String banTime, UUID uuid,  long unbanTime) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().insertIntoNullBannedPlayers(id, name, initiatorName, reason, banType, banDate, banTime, uuid, unbanTime); break;
            }
            case MYSQL: {
                getMySQLManager().insertIntoNullBannedPlayers(id, name, initiatorName, reason, banType, banDate, banTime, uuid, unbanTime); break;
            }
            case H2: {}
        }
    }

    public void insertIntoNullMutedPlayersIP(String id, String ip, String initiatorName, String reason, MuteType muteType, String muteDate, String muteTime, long unmuteTime) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName, reason, muteType, muteDate, muteTime, unmuteTime); break;
            }
            case MYSQL: {
                getMySQLManager().insertIntoNullMutedPlayersIP(id, ip, initiatorName, reason, muteType, muteDate, muteTime, unmuteTime); break;
            }
            case H2: {}
        }
    }

    private List<Long> unbanTimesFromNullBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().unbanTimesFromNullBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().unbanTimesFromNullBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> ipsFromNullBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().ipsFromNullBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().ipsFromNullBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> uuidFromNullBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().uuidFromNullBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().uuidFromNullBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> banDatesFromNullBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().banDatesFromNullBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().banDatesFromNullBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> banTimesFromNullBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().banTimesFromNullBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().banTimesFromNullBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> idsFromNullBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().idsFromNullBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().idsFromNullBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> namesFromNullBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().namesFromNullBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().namesFromNullBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> initiatorsFromNullBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().initiatorsFromNullBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().initiatorsFromNullBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> reasonsFromNullBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().reasonsFromNullBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().reasonsFromNullBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<BanType> banTypesFromNullBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().banTypesFromNullBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().banTypesFromNullBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> idsFromBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().idsFromBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().idsFromBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> ipsFromBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().ipsFromBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().ipsFromBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> namesFromBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().namesFromBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().namesFromBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> initiatorsFromBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().initiatorsFromBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().initiatorsFromBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> reasonsFromBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().reasonsFromBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().reasonsFromBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<BanType> banTypesFromBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().banTypesFromBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().banTypesFromBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> banDatesFromBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().banDatesFromBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().banDatesFromBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> banTimesFromBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().banTimesFromBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().banTimesFromBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> uuidsFromBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().uuidsFromBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().uuidsFromBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<Long> unbanTimesFromBannedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().unbanTimesFromBannedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().unbanTimesFromBannedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public void updateAllPlayers(Player player) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().updateAllPlayers(player); break;
            }
            case MYSQL: {
                getMySQLManager().updateAllPlayers(player); break;
            }
            case H2: {}
        }
    }

    public List<String> getNamesFromAllPlayers() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getNamesFromAllPlayers();
            }
            case MYSQL: {
                return getMySQLManager().getIpsFromAllPlayers();
            }
            case H2: {}
        }
        return null;
    }

    public String getIpByUUID(UUID uuid) {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getIpByUUID(uuid);
            }
            case MYSQL: {
                return getMySQLManager().getIpByUUID(uuid);
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getUUIDsFromAllPlayers() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getUUIDsFromAllPlayers();
            }
            case MYSQL: {
                return getMySQLManager().getUUIDsFromAllPlayers();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getIpsFromAllPlayers() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getIpsFromAllPlayers();
            }
            case MYSQL: {
                return getMySQLManager().getIpsFromAllPlayers();
            }
            case H2: {}
        }
        return null;
    }

    public void clearBans() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().clearBans(); break;
            }
            case MYSQL: {
                getMySQLManager().clearBans(); break;
            }
            case H2: {}
        }
    }

    public void clearMutes() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                getSQLiteManager().clearMutes(); break;
            }
            case MYSQL: {
                getMySQLManager().clearMutes(); break;
            }
            case H2: {}
        }
    }

    public List<Long> unmuteTimesFromNullMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().unmuteTimesFromNullMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().unmuteTimesFromNullMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> ipsFromNullMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().ipsFromNullMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().ipsFromNullMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> uuidsFromNullMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().uuidsFromNullMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().uuidsFromNullMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> muteDatesFromNullMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().muteDatesFromNullMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().muteDatesFromNullMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> muteTimesFromNullMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().muteTimesFromNullMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().muteTimesFromNullMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> idsFromNullMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().idsFromNullMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().idsFromNullMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> namesFromNullMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().namesFromNullMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().namesFromNullMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> initiatorsFromNullMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().initiatorsFromNullMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().initiatorsFromNullMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> reasonsFromNullMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().reasonsFromNullMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().reasonsFromNullMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<MuteType> muteTypesFromNullMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().muteTypesFromNullMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().muteTypesFromNullMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> idsFromMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().idsFromMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().idsFromMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> ipsFromMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().ipsFromMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().ipsFromMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> namesFromMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().namesFromMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().namesFromMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> initiatorsFromMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().initiatorsFromMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().initiatorsFromMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> reasonsFromMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().reasonsFromMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().reasonsFromMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<MuteType> muteTypesFromMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().muteTypesFromMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().muteTypesFromMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> muteDatesFromMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().muteDatesFromMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().muteDatesFromMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> muteTimesFromMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().muteTimesFromMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().muteTimesFromMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> uuidsFromMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().uuidsFromMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().uuidsFromMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<Long> unmuteTimesFromMutedPlayersTable() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().unmuteTimesFromMutedPlayersTable();
            }
            case MYSQL: {
                return getMySQLManager().unmuteTimesFromMutedPlayersTable();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getBannedIds() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getBannedIds();
            }
            case MYSQL: {
                return getMySQLManager().getBannedIds();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getBannedIps() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getBannedIps();
            }
            case MYSQL: {
                return getMySQLManager().getBannedIps();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getBannedPlayersNames() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getBannedPlayersNames();
            }
            case MYSQL: {
                return getMySQLManager().getBannedPlayersNames();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getBanInitiators() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getBanInitiators();
            }
            case MYSQL: {
                return getMySQLManager().getBanInitiators();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getBanReasons() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getBanReasons();
            }
            case MYSQL: {
                return getMySQLManager().getBanReasons();
            }
            case H2: {}
        }
        return null;
    }

    public List<BanType> getBanTypes() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getBanTypes();
            }
            case MYSQL: {
                return getMySQLManager().getBanTypes();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getBansDates() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getBansDates();
            }
            case MYSQL: {
                return getMySQLManager().getBansDates();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getBansTimes() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getBansTimes();
            }
            case MYSQL: {
                return getMySQLManager().getBansTimes();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getBannedUUIDs() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getBannedUUIDs();
            }
            case MYSQL: {
                return getMySQLManager().getBannedUUIDs();
            }
            case H2: {}
        }
        return null;
    }

    public List<Long> getUnbanTimes() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getUnbanTimes();
            }
            case MYSQL: {
                return getMySQLManager().getUnbanTimes();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getMutedIds() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getMutedIds();
            }
            case MYSQL: {
                return getMySQLManager().getMutedIds();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getMutedIps() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getMutedIps();
            }
            case MYSQL: {
                return getMySQLManager().getMutedIps();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getMutedPlayersNames() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getMutedPlayersNames();
            }
            case MYSQL: {
                return getMySQLManager().getMutedPlayersNames();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getMuteInitiators() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getMuteInitiators();
            }
            case MYSQL: {
                return getMySQLManager().getMuteInitiators();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getMuteReasons() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getMuteReasons();
            }
            case MYSQL: {
                return getMySQLManager().getMuteReasons();
            }
            case H2: {}
        }
        return null;
    }

    public List<MuteType> getMuteTypes() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getMuteTypes();
            }
            case MYSQL: {
                return getMySQLManager().getMuteTypes();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getMuteDates() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getMuteDates();
            }
            case MYSQL: {
                return getMySQLManager().getMuteDates();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getMuteTimes() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getMuteTimes();
            }
            case MYSQL: {
                return getMySQLManager().getMuteTimes();
            }
            case H2: {}
        }
        return null;
    }

    public List<String> getMutedUUIDs() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getMutedUUIDs();
            }
            case MYSQL: {
                return getMySQLManager().getMutedUUIDs();
            }
            case H2: {}
        }
        return null;
    }

    public List<Long> getUnmuteTimes() {
        switch (getConfigSettings().getStorageType()) {
            case SQLITE: {
                return getSQLiteManager().getUnmuteTimes();
            }
            case MYSQL: {
                return getMySQLManager().getUnmuteTimes();
            }
            case H2: {}
        }
        return null;
    }

    private static BaseManager baseManager = new BaseManager();
    public static BaseManager getBaseManager() {
        return baseManager;
    }
}
