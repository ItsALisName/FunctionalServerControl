package by.alis.functionalservercontrol.databases.sql;

import by.alis.functionalservercontrol.api.enums.BanType;
import by.alis.functionalservercontrol.api.enums.MuteType;
import by.alis.functionalservercontrol.api.enums.StatsType;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import static by.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class MySQLManager extends MySQLCore {

    public MySQLManager(FunctionalServerControl plugin) {
        super(plugin);
    }

    @Override
    public Connection getMysqlConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            mysqlConnection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + baseName + "?useUnicode=true&characterEncoding=utf8&autoReconnect=true", userName, password);
            return mysqlConnection;
        } catch (SQLException | ClassNotFoundException e) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4BEFORE REPORTING THIS TO ALis's, MAKE SURE THAT EVERYTHING IS SET UP ON YOUR SIDE!"));
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to connect to the database!"));
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] -> Unknown error, try reinstalling the plugin."));
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] No further work possible!"));
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] Disabling the plugin..."));
            e.printStackTrace();
            Bukkit.getConsoleSender().sendMessage(setColors("&4BEFORE REPORTING THIS TO ALis's, MAKE SURE THAT EVERYTHING IS SET UP ON YOUR SIDE!"));
            this.plugin.getPluginLoader().disablePlugin(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class));
            return null;
        }
    }

    @Override
    public void setupTables() {
        mysqlConnection = getMysqlConnection();
        String queryTableOne = "CREATE TABLE IF NOT EXISTS bannedPlayers (id varchar(16), ip varchar(24) , name varchar(72), initiatorName varchar(72), reason varchar(255), banType varchar(32), banDate varchar(32), banTime varchar(32), uuid varchar(64), unbanTime varchar(48));";
        String queryTableTwo = "CREATE TABLE IF NOT EXISTS nullBannedPlayers (id varchar(16), ip varchar(24), name varchar(72) , initiatorName varchar(255), reason varchar(255), banType varchar(32), banDate varchar(32), banTime varchar(32), uuid varchar(64), unbanTime varchar(48));";
        String queryTableThree = "CREATE TABLE IF NOT EXISTS allPlayers (name varchar(72), uuid varchar(64), ip varchar(24));";
        String queryTableFour = "CREATE TABLE IF NOT EXISTS mutedPlayers (id varchar(16), ip varchar(24) , name varchar(72), initiatorName varchar(72), reason varchar(255), muteType varchar(32), muteDate varchar(32), muteTime varchar(32), uuid varchar(64), unmuteTime varchar(48));";
        String queryTableFive = "CREATE TABLE IF NOT EXISTS playersStats (uuid varchar(64), totalBans varchar(10), totalMutes varchar(10), totalKicks varchar(10), didBans varchar(10), didMutes varchar(10), didKicks varchar(10), didUnbans varchar(10), didUnmutes varchar(10), blockedCommandsUsed varchar(10), blockedWordsUsed varchar(10), advertiseAttempts varchar(10));";
        String queryTableSix = "CREATE TABLE IF NOT EXISTS nullMutedPlayers (id varchar(16), ip varchar(24), name varchar(72) , initiatorName varchar(72), reason varchar(255), muteType varchar(32), muteDate varchar(32), muteTime varchar(32), uuid varchar(64), unmuteTime varchar(48));";
        String queryTableSeven = "CREATE TABLE IF NOT EXISTS History (history varchar(324));";
        try {
            mysqlStatement = mysqlConnection.createStatement();
            mysqlStatement.executeUpdate(queryTableOne);
            mysqlStatement.executeUpdate(queryTableTwo);
            mysqlStatement.executeUpdate(queryTableThree);
            mysqlStatement.executeUpdate(queryTableFour);
            mysqlStatement.executeUpdate(queryTableFive);
            mysqlStatement.executeUpdate(queryTableSix);
            mysqlStatement.executeUpdate(queryTableSeven);
            mysqlStatement.close();
        } catch (SQLException a) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
            throw new RuntimeException(a);
        } finally {
            try { if (mysqlConnection != null) { mysqlConnection.close();} } catch (SQLException ignored) { }
            try { if (mysqlStatement != null) { mysqlStatement.close();} } catch (SQLException ignored) { }
            try { if (mysqlResultSet != null) { mysqlResultSet.close();} } catch (SQLException ignored) { }
        }
    }

    public void insertIntoPlayersPunishInfo(UUID uuid) {
        mysqlConnection = getMysqlConnection();
        String getUUID = "SELECT uuid FROM playersStats;";
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(getUUID);
            while (mysqlResultSet.next()) {
                String rUuid = mysqlResultSet.getString("uuid");
                if(rUuid == null || rUuid.equalsIgnoreCase(String.valueOf(uuid))) return;
            }
            String insertInfo = "INSERT INTO playersStats (uuid, totalBans, totalMutes, totalKicks, didBans, didMutes, didKicks, didUnbans, didUnmutes, blockedCommandsUsed, blockedWordsUsed, advertiseAttempts) VALUES ('" + String.valueOf(uuid) + "', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');";
            mysqlConnection.createStatement().executeUpdate(insertInfo);
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public String getPlayerStatsInfo(OfflinePlayer player, StatsType.Player statsType) {
        mysqlConnection = getMysqlConnection();
        try {
            String a = "";
            switch (statsType) {
                case STATS_BANS: a = "SELECT totalBans FROM playersStats WHERE uuid = '" + player.getUniqueId() + "';"; break;
                case STATS_KICKS: a = "SELECT totalKicks FROM playersStats WHERE uuid = '" + player.getUniqueId() + "';"; break;
                case STATS_MUTES: a = "SELECT totalMutes FROM playersStats WHERE uuid = '" + player.getUniqueId() + "';"; break;
                case BLOCKED_WORDS_USED: a = "SELECT blockedWordsUsed FROM playersStats WHERE uuid = '" + player.getUniqueId() + "';"; break;
                case BLOCKED_COMMANDS_USED: a = "SELECT blockedCommandsUsed FROM playersStats WHERE uuid = '" + player.getUniqueId() + "';"; break;
                case ADVERTISE_ATTEMPTS: a = "SELECT advertiseAttempts FROM playersStats WHERE uuid = '" + player.getUniqueId() + "';"; break;
            }
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(a);
            String info = "null";
            while (mysqlResultSet.next()) {
                switch (statsType) {
                    case STATS_MUTES: info = mysqlResultSet.getString("totalMutes"); break;
                    case STATS_KICKS: info = mysqlResultSet.getString("totalKicks"); break;
                    case STATS_BANS: info = mysqlResultSet.getString("totalBans"); break;
                    case BLOCKED_WORDS_USED: info = mysqlResultSet.getString("blockedWordsUsed"); break;
                    case BLOCKED_COMMANDS_USED: info = mysqlResultSet.getString("blockedCommandsUsed"); break;
                    case ADVERTISE_ATTEMPTS: info = mysqlResultSet.getString("advertiseAttempts"); break;
                }
                if(info != null) {
                    return info;
                }
            }
            mysqlConnection.close();
            return info.equalsIgnoreCase("null") ? null : info;
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public String getAdminStatsInfo(OfflinePlayer player, StatsType.Administrator statsType) {
        mysqlConnection = getMysqlConnection();
        try {
            String a = "";
            switch (statsType) {
                case STATS_BANS: a = "SELECT didBans FROM playersStats WHERE uuid = '" + player.getUniqueId() + "';"; break;
                case STATS_KICKS: a = "SELECT didKicks FROM playersStats WHERE uuid = '" + player.getUniqueId() + "';"; break;
                case STATS_MUTES: a = "SELECT didMutes FROM playersStats WHERE uuid = '" + player.getUniqueId() + "';"; break;
                case STATS_UNBANS: a = "SELECT didUnbans FROM playersStats WHERE uuid = '" + player.getUniqueId() + "';"; break;
                case STATS_UNMUTES: a = "SELECT didUnmutes FROM playersStats WHERE uuid = '" + player.getUniqueId() + "';"; break;
            }
            if(mysqlConnection == null) mysqlConnection = getMysqlConnection();
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(a);
            String info = "null";
            while (mysqlResultSet.next()) {
                switch (statsType) {
                    case STATS_MUTES: info = mysqlResultSet.getString("didMutes"); break;
                    case STATS_KICKS: info = mysqlResultSet.getString("didKicks"); break;
                    case STATS_BANS: info = mysqlResultSet.getString("didBans"); break;
                    case STATS_UNBANS: info = mysqlResultSet.getString("didUnbans"); break;
                    case STATS_UNMUTES: info = mysqlResultSet.getString("didUnmutes"); break;
                }
                if(info != null) {
                    return info;
                }
            }
            mysqlConnection.close();
            return info.equalsIgnoreCase("null") ? null : info;
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public void updatePlayerStatsInfo(OfflinePlayer player, StatsType.Player statsType) {
        if (getPlayerStatsInfo(player, statsType) != null) {
            int total = Integer.parseInt(getPlayerStatsInfo(player, statsType));
            mysqlConnection = getMysqlConnection();
            try {
                String a = "null";
                switch (statsType) {
                    case STATS_BANS: a = "UPDATE playersStats SET totalBans='" + (total + 1) + "' WHERE uuid='" + player.getUniqueId() + "';"; break;
                    case STATS_KICKS: a = "UPDATE playersStats SET totalKicks='" + (total + 1) + "' WHERE uuid='" + player.getUniqueId() + "';"; break;
                    case STATS_MUTES: a = "UPDATE playersStats SET totalMutes='" + (total + 1) + "' WHERE uuid='" + player.getUniqueId() + "';"; break;
                    case BLOCKED_COMMANDS_USED: a = "UPDATE playersStats SET blockedCommandsUsed='" + (total + 1) + "' WHERE uuid='" + player.getUniqueId() + "';"; break;
                    case BLOCKED_WORDS_USED: a = "UPDATE playersStats SET blockedWordsUsed='" + (total + 1) + "' WHERE uuid='" + player.getUniqueId() + "';"; break;
                    case ADVERTISE_ATTEMPTS: a = "UPDATE playersStats SET advertiseAttempts='" + (total + 1) + "' WHERE uuid='" + player.getUniqueId() + "';"; break;
                }
                mysqlConnection.createStatement().executeUpdate(a);
                mysqlConnection.close();
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
                throw new RuntimeException(ex);
            } finally {
                try {
                    if(mysqlConnection != null) {
                        mysqlConnection.close();
                    }
                } catch (SQLException ex) {}
                try {
                    if(mysqlStatement != null) {
                        mysqlStatement.close();
                    }
                } catch (SQLException ex) {}
                try {
                    if(mysqlResultSet != null) {
                        mysqlResultSet.close();
                    }
                } catch (SQLException ex) {}
            }
        }
    }

    public void updateAdminStatsInfo(OfflinePlayer player, StatsType.Administrator statsType) {
        if (getAdminStatsInfo(player, statsType) != null) {
            int total = Integer.parseInt(getAdminStatsInfo(player, statsType));
            mysqlConnection = getMysqlConnection();
            try {
                String a = "null";
                switch (statsType) {
                    case STATS_BANS: a = "UPDATE playersStats SET didBans='" + (total + 1) + "' WHERE uuid='" + player.getUniqueId() + "';"; break;
                    case STATS_KICKS: a = "UPDATE playersStats SET didKicks='" + (total + 1) + "' WHERE uuid='" + player.getUniqueId() + "';"; break;
                    case STATS_MUTES: a = "UPDATE playersStats SET didMutes='" + (total + 1) + "' WHERE uuid='" + player.getUniqueId() + "';"; break;
                    case STATS_UNBANS: a = "UPDATE playersStats SET didUnbans='" + (total + 1) + "' WHERE uuid='" + player.getUniqueId() + "';"; break;
                    case STATS_UNMUTES: a = "UPDATE playersStats SET didUnmutes='" + (total + 1) + "' WHERE uuid='" + player.getUniqueId() + "';"; break;
                }
                mysqlConnection.createStatement().executeUpdate(a);
                mysqlConnection.close();
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
                throw new RuntimeException(ex);
            } finally {
                try {
                    if(mysqlConnection != null) {
                        mysqlConnection.close();
                    }
                } catch (SQLException ex) {}
                try {
                    if(mysqlStatement != null) {
                        mysqlStatement.close();
                    }
                } catch (SQLException ex) {}
                try {
                    if(mysqlResultSet != null) {
                        mysqlResultSet.close();
                    }
                } catch (SQLException ex) {}
            }
        }
    }

    public void insertIntoHistory(String message) {
        mysqlConnection = getMysqlConnection();
        try {
            String a = "INSERT INTO History (history) VALUES ('" + message + "');";
            mysqlConnection.createStatement().executeUpdate(a);
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public List<String> getRecordsFromHistory(CommandSender sender, int count, @Nullable String attribute) {
        mysqlConnection = getMysqlConnection();
        List<String> a = new ArrayList<>();
        if(attribute == null) {
            try {
                String b = "SELECT history FROM History;";
                mysqlResultSet = mysqlConnection.createStatement().executeQuery(b);
                String record = "null";
                int start = 0;
                while (mysqlResultSet.next()) {
                    record = mysqlResultSet.getString("history");
                    if (record != null) {
                        start = start + 1;
                        a.add(start + ". " + record);
                        if(start >= count) return a;
                    }
                }
                if(start < count) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.fewer-records").replace("%1$f", String.valueOf(start))));
                }
                if(mysqlConnection != null) mysqlConnection.close();
                if(mysqlResultSet != null) mysqlResultSet.close();
            }catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
                throw new RuntimeException(ex);
            }
        } else {
            try {
                String b = "SELECT history FROM History;";
                mysqlResultSet = mysqlConnection.createStatement().executeQuery(b);
                String record = "null";
                int start = 0;
                while (mysqlResultSet.next()) {
                    record = mysqlResultSet.getString("history");
                    if (record != null && record.contains(attribute)) {
                        a.add(record);
                        start = start + 1;
                    }
                }
                if(start < count) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.fewer-records").replace("%1$f", String.valueOf(start))));
                }
                if(mysqlConnection != null) mysqlConnection.close();
                if(mysqlResultSet != null) mysqlResultSet.close();
            }catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
                throw new RuntimeException(ex);
            }
        }
        return a;
    }

    public void clearHistory() {
        try {
            String a = "DELETE FROM History;";
            getMysqlConnection().createStatement().executeUpdate(a);
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }


    public void deleteFromMutedPlayers(String expression, String param) {
        mysqlConnection = getMysqlConnection();
        try {
            if(expression.equalsIgnoreCase("-n")) {
                String a = "DELETE FROM mutedPlayers WHERE name = '" + param + "';";
                mysqlConnection.createStatement().executeUpdate(a);
            } else if(expression.equalsIgnoreCase("-ip")) {
                String a = "DELETE FROM mutedPlayers WHERE ip = '" + param + "';";
                mysqlConnection.createStatement().executeUpdate(a);
            } else if(expression.equalsIgnoreCase("-id")) {
                String a = "DELETE FROM mutedPlayers WHERE id = '" + param + "';";
                mysqlConnection.createStatement().executeUpdate(a);
            } else if(expression.equalsIgnoreCase("-u")) {
                String a = "DELETE FROM mutedPlayers WHERE uuid = '" + param + "';";
                mysqlConnection.createStatement().executeUpdate(a);
            }
            mysqlConnection.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
        } finally {
            try {
                if (mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public void deleteFromNullMutedPlayers(String expression, String param) {
        mysqlConnection = getMysqlConnection();
        try {
            if(expression.equalsIgnoreCase("-n")) {
                String a = "DELETE FROM nullMutedPlayers WHERE name = '" + param + "';";
                mysqlConnection.createStatement().executeUpdate(a);
            } else if(expression.equalsIgnoreCase("-id")) {
                String a = "DELETE FROM nullMutedPlayers WHERE id = '" + param + "';";
                mysqlConnection.createStatement().executeUpdate(a);
            }
            mysqlConnection.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
        } finally {
            try {
                if (mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {
            }
        }
    }


    @Nullable
    public String getUuidByName(String playerName) {
        mysqlConnection = getMysqlConnection();
        try {
            String a = "SELECT uuid FROM allPlayers WHERE name = '" + playerName + "';";
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(a);
            String uuid = "null";
            while (mysqlResultSet.next()) {
                uuid = mysqlResultSet.getString("uuid");
                if(uuid != null) {
                    return uuid;
                }
            }
            return uuid.equalsIgnoreCase("null") ? null : uuid;
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if (mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }


    public UUID getUUIDByIp(String ip) {
        mysqlConnection = getMysqlConnection();
        try {
            String a = "SELECT uuid FROM allPlayers WHERE ip = '" + ip + "';";
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(a);
            String uuid = "null";
            while (mysqlResultSet.next()) {
                uuid = mysqlResultSet.getString("uuid");
                if(uuid != null) {
                    return UUID.fromString(uuid);
                }
            }
            return uuid.equalsIgnoreCase("null") ? null : UUID.fromString(uuid);
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if (mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }



    public String getIpByUUID(UUID uuid) {
        mysqlConnection = getMysqlConnection();
        String select = "SELECT ip FROM allPlayers WHERE uuid = '" + uuid + "';";
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(select);
            String ip = "UNKNOWN_ERROR";
            while (mysqlResultSet.next()) {
                ip = mysqlResultSet.getString("ip");
            }
            return ip;
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }


    public void insertIntoAllPlayers(String name, UUID uuid, String ip) {
        mysqlConnection = getMysqlConnection();
        String getUUID = "SELECT uuid FROM allPlayers;";
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(getUUID);
            while (mysqlResultSet.next()) {
                String rUuid = mysqlResultSet.getString("uuid");
                if(rUuid == null || rUuid.equalsIgnoreCase(String.valueOf(uuid))) return;
            }
            String insertName = "INSERT INTO allPlayers (name, uuid, ip) VALUES ('" + name + "', '" + uuid + "', '" + ip + "');";
            mysqlConnection.createStatement().executeUpdate(insertName);
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public void deleteFromBannedPlayers(String expression, String param) {
        mysqlConnection = getMysqlConnection();
        try {
            if(expression.equalsIgnoreCase("-n")) {
                String a = "DELETE FROM bannedPlayers WHERE name = '" + param + "';";
                mysqlConnection.createStatement().executeUpdate(a);
            } else if(expression.equalsIgnoreCase("-ip")) {
                String a = "DELETE FROM bannedPlayers WHERE ip = '" + param + "';";
                mysqlConnection.createStatement().executeUpdate(a);
            } else if(expression.equalsIgnoreCase("-id")) {
                String a = "DELETE FROM bannedPlayers WHERE id = '" + param + "';";
                mysqlConnection.createStatement().executeUpdate(a);
            } else if(expression.equalsIgnoreCase("-u")) {
                String a = "DELETE FROM bannedPlayers WHERE uuid = '" + param + "';";
                mysqlConnection.createStatement().executeUpdate(a);
            }
            mysqlConnection.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if (mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public void deleteFromNullBannedPlayers(String expression, String param) {
        mysqlConnection = getMysqlConnection();
        try {
            if(expression.equalsIgnoreCase("-n")) {
                String a = "DELETE FROM nullBannedPlayers WHERE name = '" + param + "';";
                mysqlConnection.createStatement().executeUpdate(a);
            } else if(expression.equalsIgnoreCase("-id")) {
                String a = "DELETE FROM nullBannedPlayers WHERE id = '" + param + "';";
                mysqlConnection.createStatement().executeUpdate(a);
            }
            mysqlConnection.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if (mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public void insertIntoBannedPlayers(String id, String ip, String name, String initiatorName, String reason, BanType banType, String banDate, String banTime, UUID uuid, long unbanTime) {
        mysqlConnection = getMysqlConnection();
        try {
            String insert = "INSERT INTO bannedPlayers (id, ip, name, initiatorName, reason, banType, banDate, banTime, uuid, unbanTime) VALUES ('" + id + "', '" + ip + "', '" + name + "', '" + initiatorName + "', '" + reason + "', '" + banType + "', '" + banDate +"', '" + banTime +"', '" + uuid +"', '" + unbanTime + "');";
            mysqlConnection.createStatement().executeUpdate(insert);
            mysqlResultSet.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public void insertIntoMutedPlayers(String id, String ip, String name, String initiatorName, String reason, MuteType muteType, String banDate, String banTime, UUID uuid, long unmuteTime) {
        mysqlConnection = getMysqlConnection();
        try {
            String insert = "INSERT INTO mutedPlayers (id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, uuid, unmuteTime) VALUES ('" + id + "', '" + ip + "', '" + name + "', '" + initiatorName + "', '" + reason + "', '" + muteType + "', '" + banDate +"', '" + banTime +"', '" + uuid +"', '" + unmuteTime + "');";
            mysqlConnection.createStatement().executeUpdate(insert);
            mysqlResultSet.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public void insertIntoNullBannedPlayers(String id, String name, String initiatorName, String reason, BanType banType, String banDate, String banTime, UUID uuid,  long unbanTime) {
        mysqlConnection = getMysqlConnection();
        try {
            String insert = "INSERT INTO nullBannedPlayers (id, ip, name, initiatorName, reason, banType, banDate, banTime, uuid, unbanTime) VALUES ('" + id + "', 'NULL_PLAYER', '" + name + "', '" + initiatorName + "', '" + reason + "', '" + banType + "', '" + banDate +"', '" + banTime +"', '" + uuid + "', '" + unbanTime + "');";
            mysqlConnection.createStatement().executeUpdate(insert);
            mysqlResultSet.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public void insertIntoNullMutedPlayers(String id, String name, String initiatorName, String reason, MuteType muteType, String muteDate, String muteTime, UUID uuid, long unmuteTime) {
        mysqlConnection = getMysqlConnection();
        try {
            String insert = "INSERT INTO nullMutedPlayers (id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, uuid, unmuteTime) VALUES ('" + id + "', 'NULL_PLAYER', '" + name + "', '" + initiatorName + "', '" + reason + "', '" + muteType + "', '" + muteDate +"', '" + muteTime +"', '" + uuid + "', '" + unmuteTime + "');";
            mysqlConnection.createStatement().executeUpdate(insert);
            mysqlResultSet.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public void insertIntoNullBannedPlayersIP(String id, String ip, String initiatorName, String reason, BanType banType, String banDate, String banTime, long unbanTime) {
        mysqlConnection = getMysqlConnection();
        try {
            String insert = "INSERT INTO nullBannedPlayers (id, ip, name, initiatorName, reason, banType, banDate, banTime, uuid, unbanTime) VALUES ('" + id + "', '" + ip + "', 'NULL_PLAYER', '" + initiatorName + "', '" + reason + "', '" + banType + "', '" + banDate +"', '" + banTime +"', 'NULL_PLAYER', '" + unbanTime + "');";
            mysqlConnection.createStatement().executeUpdate(insert);
            mysqlResultSet.close();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
            throw new RuntimeException(e);
        } finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public void insertIntoNullMutedPlayersIP(String id, String ip, String initiatorName, String reason, MuteType muteType, String muteDate, String muteTime, long unmuteTime) {
        mysqlConnection = getMysqlConnection();
        try {
            String insert = "INSERT INTO nullMutedPlayers (id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, uuid, unmuteTime) VALUES ('" + id + "', '" + ip + "', 'NULL_PLAYER', '" + initiatorName + "', '" + reason + "', '" + muteType + "', '" + muteDate +"', '" + muteTime +"', 'NULL_PLAYER', '" + unmuteTime + "');";
            mysqlConnection.createStatement().executeUpdate(insert);
            mysqlResultSet.close();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
            throw new RuntimeException(e);
        } finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public List<Long> unbanTimesFromNullBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT unbanTime FROM nullBannedPlayers;";
        List<Long> unbanTimes = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String unbanTime = "UNBAN_TIME_ERROR";
            while (mysqlResultSet.next()) {
                unbanTime = mysqlResultSet.getString("unbanTime");
                if(unbanTime == null) {
                    unbanTime = "0";
                    unbanTimes.add(Long.valueOf(unbanTime));
                } else {
                    unbanTimes.add(Long.valueOf(unbanTime));
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return unbanTimes;
    }

    public List<String> ipsFromNullBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT ip FROM nullBannedPlayers;";
        List<String> ips = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String ip = "NULL_PLAYER";
            while (mysqlResultSet.next()) {
                ip = mysqlResultSet.getString("ip");
                if(ip == null) {
                    ip = "NULL_PLAYER";
                    ips.add(ip);
                } else {
                    ips.add(ip);
                }
            }
            return ips;
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public List<String> uuidFromNullBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT uuid FROM nullBannedPlayers;";
        List<String> uuids = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String uuid = "NULL_PLAYER";
            while (mysqlResultSet.next()) {
                uuid = mysqlResultSet.getString("uuid");
                if(uuid == null) {
                    uuid = "NULL_PLAYER";
                    uuids.add(uuid);
                } else {
                    uuids.add(uuid);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return uuids;
    }

    public List<String> banDatesFromNullBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT banDate FROM nullBannedPlayers;";
        List<String> banDates = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String banDate = "BAN_DATE_ERROR";
            while (mysqlResultSet.next()) {
                banDate = mysqlResultSet.getString("banDate");
                if(banDate == null) {
                    banDate = "BAN_DATE_ERROR";
                    banDates.add(banDate);
                } else {
                    banDates.add(banDate);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return banDates;
    }

    public List<String> banTimesFromNullBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT banTime FROM nullBannedPlayers;";
        List<String> banTimes = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String banTime = "BAN_TIME_ERROR";
            while (mysqlResultSet.next()) {
                banTime = mysqlResultSet.getString("banTime");
                if(banTime == null) {
                    banTime = "BAN_TIME_ERROR";
                    banTimes.add(banTime);
                } else {
                    banTimes.add(banTime);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return banTimes;
    }

    public List<String> idsFromNullBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT id FROM nullBannedPlayers;";
        List<String> ids = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String id = "ID_ERROR";
            while (mysqlResultSet.next()) {
                id = mysqlResultSet.getString("id");
                if(id == null) {
                    id = "ID_ERROR";
                    ids.add(id);
                } else {
                    ids.add(id);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return ids;
    }

    public List<String> namesFromNullBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT name FROM nullBannedPlayers;";
        List<String> names = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String name = "NAME_ERROR";
            while (mysqlResultSet.next()) {
                name = mysqlResultSet.getString("name");
                if(name == null) {
                    name = "NAME_ERROR";
                    names.add(name);
                } else {
                    names.add(name);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return names;
    }

    public List<String> initiatorsFromNullBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT initiatorName FROM nullBannedPlayers;";
        List<String> iNames = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String iName = "INITIATOR_NAME_ERROR";
            while (mysqlResultSet.next()) {
                iName = mysqlResultSet.getString("initiatorName");
                if(iName == null) {
                    iName = "INITIATOR_NAME_ERROR";
                    iNames.add(iName);
                } else {
                    iNames.add(iName);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return iNames;
    }

    public List<String> reasonsFromNullBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT reason FROM nullBannedPlayers;";
        List<String> reasons = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String reason = "REASON_ERROR";
            while (mysqlResultSet.next()) {
                reason = mysqlResultSet.getString("reason");
                if(reason == null) {
                    reason = "REASON_ERROR";
                    reasons.add(reason);
                } else {
                    reasons.add(reason);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return reasons;
    }

    public List<BanType> banTypesFromNullBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT banType FROM nullBannedPlayers;";
        List<BanType> banTypes = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String banType = "BAN_TYPE_ERROR";
            while (mysqlResultSet.next()) {
                banType = mysqlResultSet.getString("banType");
                if(banType == null) {
                    banType = "BAN_TYPE_ERROR";
                    banTypes.add(BanType.BAN_TYPE_ERROR);
                } else {
                    banTypes.add(BanType.valueOf(banType));
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return banTypes;
    }

    // nullBannedPlayersTable

    // bannedPlayer table
    public List<String> idsFromBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT id FROM bannedPlayers;";
        List<String> ids = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String id = "ID_ERROR";
            while (mysqlResultSet.next()) {
                id = mysqlResultSet.getString("id");
                if(id == null) {
                    id = "ID_ERROR";
                    ids.add(id);
                } else {
                    ids.add(id);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return ids;
    }

    public List<String> ipsFromBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT ip FROM bannedPlayers;";
        List<String> ids = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String id = "ID_ERROR";
            while (mysqlResultSet.next()) {
                id = mysqlResultSet.getString("ip");
                if(id == null) {
                    id = "IP_ERROR";
                    ids.add(id);
                } else {
                    ids.add(id);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return ids;
    }

    public List<String> namesFromBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT name FROM bannedPlayers;";
        List<String> names = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String name = "NAME_ERROR";
            while (mysqlResultSet.next()) {
                name = mysqlResultSet.getString("name");
                if(name == null) {
                    name = "NAME_ERROR";
                    names.add(name);
                } else {
                    names.add(name);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return names;
    }

    public List<String> initiatorsFromBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT initiatorName FROM bannedPlayers;";
        List<String> initiators = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String initiatorName = "ID_ERROR";
            while (mysqlResultSet.next()) {
                initiatorName = mysqlResultSet.getString("initiatorName");
                if(initiatorName == null) {
                    initiatorName = "INITIATOR_NAME_ERROR";
                    initiators.add(initiatorName);
                } else {
                    initiators.add(initiatorName);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return initiators;
    }

    public List<String> reasonsFromBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT reason FROM bannedPlayers;";
        List<String> reasons = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String reason = "ID_ERROR";
            while (mysqlResultSet.next()) {
                reason = mysqlResultSet.getString("reason");
                if(reason == null) {
                    reason = "REASON_ERROR";
                    reasons.add(reason);
                } else {
                    reasons.add(reason);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return reasons;
    }

    public List<BanType> banTypesFromBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT banType FROM bannedPlayers;";
        List<BanType> banTypes = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String banType = "BAN_TYPE_ERROR";
            while (mysqlResultSet.next()) {
                banType = mysqlResultSet.getString("banType");
                if(banType == null) {
                    banType = "BAN_TYPE_ERROR";
                    banTypes.add(BanType.BAN_TYPE_ERROR);
                } else {
                    banTypes.add(BanType.valueOf(banType));
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return banTypes;
    }

    public List<String> banDatesFromBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT banDate FROM bannedPlayers;";
        List<String> banDates = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String banDate = "BAN_DATE_ERROR";
            while (mysqlResultSet.next()) {
                banDate = mysqlResultSet.getString("banDate");
                if(banDate == null) {
                    banDate = "BAN_DATE_ERROR";
                    banDates.add(banDate);
                } else {
                    banDates.add(banDate);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return banDates;
    }

    public List<String> banTimesFromBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        String taskOne = "SELECT banTime FROM bannedPlayers;";
        List<String> banTimes = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String banTime = "BAN_TIME_ERROR";
            while (mysqlResultSet.next()) {
                banTime = mysqlResultSet.getString("banTime");
                if(banTime == null) {
                    banTime = "BAN_TIME_ERROR";
                    banTimes.add(banTime);
                } else {
                    banTimes.add(banTime);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return banTimes;
    }

    public List<String> uuidsFromBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<String> uuids = new ArrayList<>();
        String taskOne = "SELECT uuid FROM bannedPlayers;";
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String uuid = "UUID_ERROR";
            while (mysqlResultSet.next()) {
                uuid = mysqlResultSet.getString("uuid");
                if(uuid == null) {
                    uuid = "UUID_ERROR";
                    uuids.add(uuid);
                } else {
                    uuids.add(uuid);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return uuids;
    }

    public List<Long> unbanTimesFromBannedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<Long> unbanTimes = new ArrayList<>();
        String taskOne = "SELECT unbanTime FROM bannedPlayers;";
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String unbanTime = "-1";
            while (mysqlResultSet.next()) {
                unbanTime = mysqlResultSet.getString("unbanTime");
                if(unbanTime == null) {
                    unbanTime = "-1";
                    unbanTimes.add(Long.valueOf(unbanTime));
                } else {
                    unbanTimes.add(Long.valueOf(unbanTime));
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return unbanTimes;
    }
    //bannedPlayer table

    public void updateAllPlayers(Player player) {
        if (!getIpByUUID(player.getUniqueId()).equalsIgnoreCase(player.getAddress().getAddress().getHostAddress())) {
            mysqlConnection = getMysqlConnection();
            try {
                String a = "DELETE FROM allPlayers WHERE uuid = '" + String.valueOf(player.getUniqueId()) + "';";
                mysqlConnection.createStatement().executeUpdate(a);
                mysqlConnection.close();
                insertIntoAllPlayers(player.getName(), player.getUniqueId(), player.getAddress().getAddress().getHostAddress());
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
                throw new RuntimeException(ex);
            } finally {
                try {
                    if (mysqlConnection != null) {
                        mysqlConnection.close();
                    }
                } catch (SQLException ex) {
                }
                try {
                    if (mysqlStatement != null) {
                        mysqlStatement.close();
                    }
                } catch (SQLException ex) {
                }
                try {
                    if (mysqlResultSet != null) {
                        mysqlResultSet.close();
                    }
                } catch (SQLException ex) {
                }
            }
        }
    }

    public List<String> getNamesFromAllPlayers() {
        mysqlConnection = getMysqlConnection();
        List<String> names = new ArrayList<>();
        String taskOne = "SELECT name FROM allPlayers;";
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String name = "NULL";
            while (mysqlResultSet.next()) {
                name = mysqlResultSet.getString("name");
                if(name == null) {
                    name = "NULL";
                    names.add(name);
                } else {
                    names.add(name);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return names;
    }

    public List<String> getUUIDsFromAllPlayers() {
        mysqlConnection = getMysqlConnection();
        List<String> uuids = new ArrayList<>();
        String taskOne = "SELECT uuid FROM allPlayers;";
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String uuid = "NULL";
            while (mysqlResultSet.next()) {
                uuid = mysqlResultSet.getString("uuid");
                if(uuid == null) {
                    uuid = "NULL";
                    uuids.add(uuid);
                } else {
                    uuids.add(uuid);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return uuids;
    }

    public List<String> getIpsFromAllPlayers() {
        mysqlConnection = getMysqlConnection();
        List<String> ips = new ArrayList<>();
        String taskOne = "SELECT ip FROM allPlayers;";
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery(taskOne);
            String ip = "NULL";
            while (mysqlResultSet.next()) {
                ip = mysqlResultSet.getString("ip");
                if(ip == null) {
                    ip = "NULL";
                    ips.add(ip);
                } else {
                    ips.add(ip);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return ips;
    }

    public void clearBans() {
        mysqlConnection = getMysqlConnection();
        try {
            String a = "DELETE FROM bannedPlayers;";
            String b = "DELETE FROM nullBannedPlayers;";
            mysqlConnection.createStatement().executeUpdate(a);
            mysqlConnection.createStatement().executeUpdate(b);
            mysqlConnection.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public void clearMutes() {
        mysqlConnection = getMysqlConnection();
        try {
            String a = "DELETE FROM mutedPlayers;";
            String b = "DELETE FROM nullMutedPlayers;";
            mysqlConnection.createStatement().executeUpdate(a);
            mysqlConnection.createStatement().executeUpdate(b);
            mysqlConnection.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    //Combining nullBannedPlayers and bannedPlayers list

    public List<String> getBannedIds() {
        List<String> ids = new ArrayList<>();
        ids.addAll(idsFromBannedPlayersTable());
        ids.addAll(idsFromNullBannedPlayersTable());
        return ids;
    }

    public List<String> getBannedIps() {
        List<String> ips = new ArrayList<>();
        ips.addAll(ipsFromBannedPlayersTable());
        ips.addAll(ipsFromNullBannedPlayersTable());
        return ips;
    }

    public List<String> getBannedPlayersNames() {
        List<String> banedNames = new ArrayList<>();
        banedNames.addAll(namesFromBannedPlayersTable());
        banedNames.addAll(namesFromNullBannedPlayersTable());
        return banedNames;
    }

    public List<String> getBanInitiators() {
        List<String> banInitiators = new ArrayList<>();
        banInitiators.addAll(initiatorsFromBannedPlayersTable());
        banInitiators.addAll(initiatorsFromNullBannedPlayersTable());
        return banInitiators;
    }

    public List<String> getBanReasons() {
        List<String> reasons = new ArrayList<>();
        reasons.addAll(reasonsFromBannedPlayersTable());
        reasons.addAll(reasonsFromNullBannedPlayersTable());
        return reasons;
    }

    public List<BanType> getBanTypes() {
        List<BanType> banTypes = new ArrayList<>();
        banTypes.addAll(banTypesFromBannedPlayersTable());
        banTypes.addAll(banTypesFromNullBannedPlayersTable());
        return banTypes;
    }

    public List<String> getBansDates() {
        List<String> banDates = new ArrayList<>();
        banDates.addAll(banDatesFromBannedPlayersTable());
        banDates.addAll(banDatesFromNullBannedPlayersTable());
        return banDates;
    }

    public List<String> getBansTimes() {
        List<String> banTimes = new ArrayList<>();
        banTimes.addAll(banTimesFromBannedPlayersTable());
        banTimes.addAll(banTimesFromNullBannedPlayersTable());
        return banTimes;
    }

    public List<String> getBannedUUIDs() {
        List<String> uuids = new ArrayList<>();
        uuids.addAll(uuidsFromBannedPlayersTable());
        uuids.addAll(uuidFromNullBannedPlayersTable());
        return uuids;
    }

    public List<Long> getUnbanTimes() {
        List<Long> unbanTimes = new ArrayList<>();
        unbanTimes.addAll(unbanTimesFromBannedPlayersTable());
        unbanTimes.addAll(unbanTimesFromNullBannedPlayersTable());
        return unbanTimes;
    }

    //Combining nullBannedPlayers and bannedPlayers list


    public List<Long> unmuteTimesFromNullMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<Long> a = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT unmuteTime FROM nullMutedPlayers;");
            String b = "UNMUTE_TIME_ERROR";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("unmuteTime");
                if(b == null) {
                    b = "0";
                    a.add(Long.valueOf(b));
                } else {
                    a.add(Long.valueOf(b));
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> ipsFromNullMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<String> b = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT ip FROM nullMutedPlayers;");
            String c = "NULL_PLAYER";
            while (mysqlResultSet.next()) {
                c = mysqlResultSet.getString("ip");
                if(c == null) {
                    c = "NULL_PLAYER";
                    b.add(c);
                } else {
                    b.add(c);
                }
            }
            return b;
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public List<String> uuidsFromNullMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<String> b = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT uuid FROM nullMutedPlayers;");
            String c = "NULL_PLAYER";
            while (mysqlResultSet.next()) {
                c = mysqlResultSet.getString("uuid");
                if(c == null) {
                    c = "NULL_PLAYER";
                    b.add(c);
                } else {
                    b.add(c);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return b;
    }

    public List<String> muteDatesFromNullMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<String> a = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT muteDate FROM nullMutedPlayers;");
            String b = "MUTE_DATE_ERROR";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("muteDate");
                if(b == null) {
                    b = "MUTE_DATE_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> muteTimesFromNullMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<String> a = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT muteTime FROM nullMutedPlayers;");
            String b = "MUTE_TIME_ERROR";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("muteTime");
                if(b == null) {
                    b = "MUTE_TIME_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> idsFromNullMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<String> a = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT id FROM nullMutedPlayers;");
            String b = "ID_ERROR";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("id");
                if(b == null) {
                    b = "ID_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> namesFromNullMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<String> a = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT name FROM nullMutedPlayers;");
            String b = "NAME_ERROR";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("name");
                if(b == null) {
                    b = "NAME_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> initiatorsFromNullMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<String> a = new ArrayList<>();
        try {
            mysqlResultSet = getMysqlConnection().createStatement().executeQuery("SELECT initiatorName FROM nullMutedPlayers;");
            String b = "INITIATOR_NAME_ERROR";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("initiatorName");
                if(b == null) {
                    b = "INITIATOR_NAME_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> reasonsFromNullMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<String> a = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT reason FROM nullMutedPlayers;");
            String b = "REASON_ERROR";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("reason");
                if(b == null) {
                    b = "REASON_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }
        return a;
    }

    public List<MuteType> muteTypesFromNullMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<MuteType> a = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT muteType FROM nullMutedPlayers;");
            String b = "BAN_TYPE_ERROR";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("muteType");
                if(b == null) {
                    b = "BAN_TYPE_ERROR";
                    a.add(MuteType.BAN_TYPE_ERROR);
                } else {
                    a.add(MuteType.valueOf(b));
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> idsFromMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<String> a = new ArrayList<>();
        try {
            mysqlResultSet = getMysqlConnection().createStatement().executeQuery("SELECT id FROM mutedPlayers;");
            String b = "ID_ERROR";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("id");
                if(b == null) {
                    b = "ID_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> ipsFromMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<String> a = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT ip FROM mutedPlayers;");
            String b = "ID_ERROR";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("ip");
                if(b == null) {
                    b = "IP_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> namesFromMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<String> a = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT name FROM mutedPlayers;");
            String b = "NAME_ERROR";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("name");
                if(b == null) {
                    b = "NAME_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!");
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> initiatorsFromMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<String> a = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT initiatorName FROM mutedPlayers;");
            String b = "ID_ERROR";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("initiatorName");
                if(b == null) {
                    b = "INITIATOR_NAME_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> reasonsFromMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<String> a = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT reason FROM mutedPlayers;");
            String b = "ID_ERROR";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("reason");
                if(b == null) {
                    b = "REASON_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<MuteType> muteTypesFromMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<MuteType> a = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT muteType FROM mutedPlayers;");
            String b = "MUTE_TYPE_ERROR";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("muteType");
                if(b == null) {
                    b = "MUTE_TYPE_ERROR";
                    a.add(MuteType.BAN_TYPE_ERROR);
                } else {
                    a.add(MuteType.valueOf(b));
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> muteDatesFromMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<String> a = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT muteDate FROM mutedPlayers;");
            String b = "MUTE_DATE_ERROR";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("muteDate");
                if(b == null) {
                    b = "MUTE_DATE_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> muteTimesFromMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<String> a = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT muteTime FROM mutedPlayers;");
            String b = "MUTE_TIME_ERROR";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("muteTime");
                if(b == null) {
                    b = "MUTE_TIME_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> uuidsFromMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<String> a = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT uuid FROM mutedPlayers;");
            String b = "UUID_ERROR";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("uuid");
                if(b == null) {
                    b = "UUID_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<Long> unmuteTimesFromMutedPlayersTable() {
        mysqlConnection = getMysqlConnection();
        List<Long> a = new ArrayList<>();
        try {
            mysqlResultSet = mysqlConnection.createStatement().executeQuery("SELECT unmuteTime FROM mutedPlayers;");
            String b = "-1";
            while (mysqlResultSet.next()) {
                b = mysqlResultSet.getString("unmuteTime");
                if(b == null) {
                    b = "-1";
                    a.add(Long.valueOf(b));
                } else {
                    a.add(Long.valueOf(b));
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | MySQL] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(mysqlConnection != null) {
                    mysqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlStatement != null) {
                    mysqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(mysqlResultSet != null) {
                    mysqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> getMutedIds() {
        List<String> a = new ArrayList<>();
        a.addAll(idsFromMutedPlayersTable());
        a.addAll(idsFromNullMutedPlayersTable());
        return a;
    }

    public List<String> getMutedIps() {
        List<String> a = new ArrayList<>();
        a.addAll(ipsFromMutedPlayersTable());
        a.addAll(ipsFromNullMutedPlayersTable());
        return a;
    }

    public List<String> getMutedPlayersNames() {
        List<String> a = new ArrayList<>();
        a.addAll(namesFromMutedPlayersTable());
        a.addAll(namesFromNullMutedPlayersTable());
        return a;
    }

    public List<String> getMuteInitiators() {
        List<String> a = new ArrayList<>();
        a.addAll(initiatorsFromMutedPlayersTable());
        a.addAll(initiatorsFromNullMutedPlayersTable());
        return a;
    }

    public List<String> getMuteReasons() {
        List<String> a = new ArrayList<>();
        a.addAll(reasonsFromMutedPlayersTable());
        a.addAll(reasonsFromNullBannedPlayersTable());
        return a;
    }

    public List<MuteType> getMuteTypes() {
        List<MuteType> a = new ArrayList<>();
        a.addAll(muteTypesFromMutedPlayersTable());
        a.addAll(muteTypesFromNullMutedPlayersTable());
        return a;
    }

    public List<String> getMuteDates() {
        List<String> a = new ArrayList<>();
        a.addAll(muteDatesFromMutedPlayersTable());
        a.addAll(muteDatesFromNullMutedPlayersTable());
        return a;
    }

    public List<String> getMuteTimes() {
        List<String> a = new ArrayList<>();
        a.addAll(muteTimesFromMutedPlayersTable());
        a.addAll(muteTimesFromNullMutedPlayersTable());
        return a;
    }

    public List<String> getMutedUUIDs() {
        List<String> a = new ArrayList<>();
        a.addAll(uuidsFromMutedPlayersTable());
        a.addAll(uuidsFromNullMutedPlayersTable());
        return a;
    }

    public List<Long> getUnmuteTimes() {
        List<Long> a = new ArrayList<>();
        a.addAll(unmuteTimesFromMutedPlayersTable());
        a.addAll(unmuteTimesFromNullMutedPlayersTable());
        return a;
    }
    
}
