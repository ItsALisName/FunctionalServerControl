package net.alis.functionalservercontrol.databases.sqlite;

import net.alis.functionalservercontrol.api.enums.StatsType;
import net.alis.functionalservercontrol.spigot.FunctionalServerControl;
import net.alis.functionalservercontrol.api.enums.BanType;
import net.alis.functionalservercontrol.api.enums.MuteType;
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

import static net.alis.functionalservercontrol.spigot.additional.misc.TextUtils.setColors;
import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public class SQLiteManager extends SQLiteCore {

    public SQLiteManager(FunctionalServerControl plugin) {
        super(plugin);
    }

    @Override
    protected Connection getSQLiteConnection() {
        if(!getFileAccessor().getSQLiteFile().exists()) {
            FunctionalServerControl.getPlugin(FunctionalServerControl.class).saveResource("sqlite.db", false);
        }
        if (getFileAccessor().getSQLiteFile().exists()) {
            try {
                Class.forName("org.sqlite.JDBC");
                sqlConnection = DriverManager.getConnection("jdbc:sqlite:" + getFileAccessor().getSQLiteFile().getPath());
                return sqlConnection;
            } catch (ClassNotFoundException | SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to connect to the database!"));
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] -> Unknown error, try reinstalling the plugin."));
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] No further work possible!"));
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] Disabling the plugin..."));
                this.plugin.getPluginLoader().disablePlugin(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class));
                return null;
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to connect to the database!"));
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] -> File &4&osqlite.db &4has not been found!"));
            Bukkit.getConsoleSender().sendMessage("");
            Bukkit.getConsoleSender().sendMessage(setColors("&e[FunctionalServerControl] Attempt to create a file..."));
            try {
                getFileAccessor().getSQLiteFile().createNewFile();
            } catch (IOException ex) {
            }
            if (getFileAccessor().getSQLiteFile().exists()) {
                Bukkit.getConsoleSender().sendMessage(setColors("&a[FunctionalServerControl] Database file created successfully, reconnecting..."));
                try {
                    Class.forName("org.sqlite.JDBC");
                    sqlConnection = DriverManager.getConnection("jdbc:sqlite:" + getFileAccessor().getSQLiteFile().getPath());
                    Bukkit.getConsoleSender().sendMessage(setColors("&2[FunctionalServerControl] Connection to the database was successful."));
                    String queryTableOne = "CREATE TABLE IF NOT EXISTS bannedPlayers (id varchar(16), ip varchar(24) , name varchar(72), initiatorName varchar(72), reason varchar(255), banType varchar(32), banDate varchar(32), banTime varchar(32), uuid varchar(64), unbanTime varchar(48));";
                    String queryTableTwo = "CREATE TABLE IF NOT EXISTS nullBannedPlayers (id varchar(16), ip varchar(24), name varchar(72) , initiatorName varchar(255), reason varchar(255), banType varchar(32), banDate varchar(32), banTime varchar(32), uuid varchar(64), unbanTime varchar(48));";
                    String queryTableThree = "CREATE TABLE IF NOT EXISTS allPlayers (name varchar(72), uuid varchar(64), ip varchar(24));";
                    String queryTableFour = "CREATE TABLE IF NOT EXISTS mutedPlayers (id varchar(16), ip varchar(24) , name varchar(72), initiatorName varchar(72), reason varchar(255), muteType varchar(32), muteDate varchar(32), muteTime varchar(32), uuid varchar(64), unmuteTime varchar(48));";
                    String queryTableFive = "CREATE TABLE IF NOT EXISTS playersStats (uuid varchar(64), totalBans varchar(10), totalMutes varchar(10), totalKicks varchar(10), didBans varchar(10), didMutes varchar(10), didKicks varchar(10), didUnbans varchar(10), didUnmutes varchar(10), blockedCommandsUsed varchar(10), blockedWordsUsed varchar(10), advertiseAttempts varchar(10));";
                    String queryTableSix = "CREATE TABLE IF NOT EXISTS nullMutedPlayers (id varchar(16), ip varchar(24), name varchar(72) , initiatorName varchar(72), reason varchar(255), muteType varchar(32), muteDate varchar(32), muteTime varchar(32), uuid varchar(64), unmuteTime varchar(48));";
                    String queryTableSeven = "CREATE TABLE IF NOT EXISTS History (history varchar(324));";
                    sqlStatement = sqlConnection.createStatement();
                    sqlStatement.executeUpdate(queryTableOne);
                    sqlStatement.executeUpdate(queryTableTwo);
                    sqlStatement.executeUpdate(queryTableThree);
                    sqlStatement.executeUpdate(queryTableFour);
                    sqlStatement.executeUpdate(queryTableFive);
                    sqlStatement.executeUpdate(queryTableSix);
                    sqlStatement.executeUpdate(queryTableSeven);
                    sqlStatement.close();
                    return sqlConnection;
                } catch (ClassNotFoundException | SQLException ex) {
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to connect to the database!"));
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] -> Unknown error, try reinstalling the plugin."));
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] No further work possible!"));
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] Disabling the plugin..."));
                    this.plugin.getPluginLoader().disablePlugin(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class));
                    return null;
                }
            } else {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] No further work possible!"));
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] Disabling the plugin..."));
                this.plugin.getPluginLoader().disablePlugin(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class));
                return null;
            }
        }
    }

    @Override
    public void setupTables() {
        sqlConnection = getSQLiteConnection();
        String queryTableOne = "CREATE TABLE IF NOT EXISTS bannedPlayers (id varchar(16), ip varchar(24) , name varchar(72), initiatorName varchar(72), reason varchar(255), banType varchar(32), banDate varchar(32), banTime varchar(32), uuid varchar(64), unbanTime varchar(48));";
        String queryTableTwo = "CREATE TABLE IF NOT EXISTS nullBannedPlayers (id varchar(16), ip varchar(24), name varchar(72) , initiatorName varchar(255), reason varchar(255), banType varchar(32), banDate varchar(32), banTime varchar(32), uuid varchar(64), unbanTime varchar(48));";
        String queryTableThree = "CREATE TABLE IF NOT EXISTS allPlayers (name varchar(72), uuid varchar(64), ip varchar(24));";
        String queryTableFour = "CREATE TABLE IF NOT EXISTS mutedPlayers (id varchar(16), ip varchar(24) , name varchar(72), initiatorName varchar(72), reason varchar(255), muteType varchar(32), muteDate varchar(32), muteTime varchar(32), uuid varchar(64), unmuteTime varchar(48));";
        String queryTableFive = "CREATE TABLE IF NOT EXISTS playersStats (uuid varchar(64), totalBans varchar(10), totalMutes varchar(10), totalKicks varchar(10), didBans varchar(10), didMutes varchar(10), didKicks varchar(10), didUnbans varchar(10), didUnmutes varchar(10), blockedCommandsUsed varchar(10), blockedWordsUsed varchar(10), advertiseAttempts varchar(10));";
        String queryTableSix = "CREATE TABLE IF NOT EXISTS nullMutedPlayers (id varchar(16), ip varchar(24), name varchar(72) , initiatorName varchar(72), reason varchar(255), muteType varchar(32), muteDate varchar(32), muteTime varchar(32), uuid varchar(64), unmuteTime varchar(48));";
        String queryTableSeven = "CREATE TABLE IF NOT EXISTS History (history varchar(324));";
        try {
            sqlStatement = sqlConnection.createStatement();
            sqlStatement.executeUpdate(queryTableOne);
            sqlStatement.executeUpdate(queryTableTwo);
            sqlStatement.executeUpdate(queryTableThree);
            sqlStatement.executeUpdate(queryTableFour);
            sqlStatement.executeUpdate(queryTableFive);
            sqlStatement.executeUpdate(queryTableSix);
            sqlStatement.executeUpdate(queryTableSeven);
            sqlStatement.close();
        } catch (SQLException a) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
            throw new RuntimeException(a);
        } finally {
            try {if (sqlConnection != null) {sqlConnection.close();}} catch (SQLException ignored) {}
            try {if (sqlStatement != null) {sqlStatement.close();}} catch (SQLException ignored) {}
            try {if (sqlResultSet != null) {sqlResultSet.close();}} catch (SQLException ignored) {}
        }
    }

    public void insertIntoPlayersPunishInfo(UUID uuid) {
        sqlConnection = getSQLiteConnection();
        String getUUID = "SELECT uuid FROM playersStats;";
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(getUUID);
            while (sqlResultSet.next()) {
                String rUuid = sqlResultSet.getString("uuid");
                if(rUuid == null || rUuid.equalsIgnoreCase(String.valueOf(uuid))) return;
            }
            String insertInfo = "INSERT INTO playersStats (uuid, totalBans, totalMutes, totalKicks, didBans, didMutes, didKicks, didUnbans, didUnmutes, blockedCommandsUsed, blockedWordsUsed, advertiseAttempts) VALUES ('" + String.valueOf(uuid) + "', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');";
            sqlConnection.createStatement().executeUpdate(insertInfo);
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public String getPlayerStatsInfo(OfflinePlayer player, StatsType.Player statsType) {
        sqlConnection = getSQLiteConnection();
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
            sqlResultSet = sqlConnection.createStatement().executeQuery(a);
            String info = "null";
            while (sqlResultSet.next()) {
                switch (statsType) {
                    case STATS_MUTES: info = sqlResultSet.getString("totalMutes"); break;
                    case STATS_KICKS: info = sqlResultSet.getString("totalKicks"); break;
                    case STATS_BANS: info = sqlResultSet.getString("totalBans"); break;
                    case BLOCKED_WORDS_USED: info = sqlResultSet.getString("blockedWordsUsed"); break;
                    case BLOCKED_COMMANDS_USED: info = sqlResultSet.getString("blockedCommandsUsed"); break;
                    case ADVERTISE_ATTEMPTS: info = sqlResultSet.getString("advertiseAttempts"); break;
                }
                if(info != null) {
                    return info;
                }
            }
            sqlConnection.close();
            return info.equalsIgnoreCase("null") ? null : info;
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public String getAdminStatsInfo(OfflinePlayer player, StatsType.Administrator statsType) {
        sqlConnection = getSQLiteConnection();
        try {
            String a = "";
            switch (statsType) {
                case STATS_BANS: a = "SELECT didBans FROM playersStats WHERE uuid = '" + player.getUniqueId() + "';"; break;
                case STATS_KICKS: a = "SELECT didKicks FROM playersStats WHERE uuid = '" + player.getUniqueId() + "';"; break;
                case STATS_MUTES: a = "SELECT didMutes FROM playersStats WHERE uuid = '" + player.getUniqueId() + "';"; break;
                case STATS_UNBANS: a = "SELECT didUnbans FROM playersStats WHERE uuid = '" + player.getUniqueId() + "';"; break;
                case STATS_UNMUTES: a = "SELECT didUnmutes FROM playersStats WHERE uuid = '" + player.getUniqueId() + "';"; break;
            }
            if(sqlConnection == null) sqlConnection = getSQLiteConnection();
            sqlResultSet = sqlConnection.createStatement().executeQuery(a);
            String info = "null";
            while (sqlResultSet.next()) {
                switch (statsType) {
                    case STATS_MUTES: info = sqlResultSet.getString("didMutes"); break;
                    case STATS_KICKS: info = sqlResultSet.getString("didKicks"); break;
                    case STATS_BANS: info = sqlResultSet.getString("didBans"); break;
                    case STATS_UNBANS: info = sqlResultSet.getString("didUnbans"); break;
                    case STATS_UNMUTES: info = sqlResultSet.getString("didUnmutes"); break;
                }
                if(info != null) {
                    return info;
                }
            }
            sqlConnection.close();
            return info.equalsIgnoreCase("null") ? null : info;
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public void updatePlayerStatsInfo(OfflinePlayer player, StatsType.Player statsType) {
        if (getPlayerStatsInfo(player, statsType) != null) {
            int total = Integer.parseInt(getPlayerStatsInfo(player, statsType));
            sqlConnection = getSQLiteConnection();
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
                sqlConnection.createStatement().executeUpdate(a);
                sqlConnection.close();
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
                throw new RuntimeException(ex);
            } finally {
                try {
                    if(sqlConnection != null) {
                        sqlConnection.close();
                    }
                } catch (SQLException ex) {}
                try {
                    if(sqlStatement != null) {
                        sqlStatement.close();
                    }
                } catch (SQLException ex) {}
                try {
                    if(sqlResultSet != null) {
                        sqlResultSet.close();
                    }
                } catch (SQLException ex) {}
            }
        }
    }

    public void updateAdminStatsInfo(OfflinePlayer player, StatsType.Administrator statsType) {
        if (getAdminStatsInfo(player, statsType) != null) {
            int total = Integer.parseInt(getAdminStatsInfo(player, statsType));
            sqlConnection = getSQLiteConnection();
            try {
                String a = "null";
                switch (statsType) {
                    case STATS_BANS: a = "UPDATE playersStats SET didBans='" + (total + 1) + "' WHERE uuid='" + player.getUniqueId() + "';"; break;
                    case STATS_KICKS: a = "UPDATE playersStats SET didKicks='" + (total + 1) + "' WHERE uuid='" + player.getUniqueId() + "';"; break;
                    case STATS_MUTES: a = "UPDATE playersStats SET didMutes='" + (total + 1) + "' WHERE uuid='" + player.getUniqueId() + "';"; break;
                    case STATS_UNBANS: a = "UPDATE playersStats SET didUnbans='" + (total + 1) + "' WHERE uuid='" + player.getUniqueId() + "';"; break;
                    case STATS_UNMUTES: a = "UPDATE playersStats SET didUnmutes='" + (total + 1) + "' WHERE uuid='" + player.getUniqueId() + "';"; break;
                }
                sqlConnection.createStatement().executeUpdate(a);
                sqlConnection.close();
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
                throw new RuntimeException(ex);
            } finally {
                try {
                    if(sqlConnection != null) {
                        sqlConnection.close();
                    }
                } catch (SQLException ex) {}
                try {
                    if(sqlStatement != null) {
                        sqlStatement.close();
                    }
                } catch (SQLException ex) {}
                try {
                    if(sqlResultSet != null) {
                        sqlResultSet.close();
                    }
                } catch (SQLException ex) {}
            }
        }
    }

    public void insertIntoHistory(String message) {
        sqlConnection = getSQLiteConnection();
        try {
            String a = "INSERT INTO History (history) VALUES ('" + message + "');";
            sqlConnection.createStatement().executeUpdate(a);
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public List<String> getRecordsFromHistory(CommandSender sender, int count, @Nullable String attribute) {
        sqlConnection = getSQLiteConnection();
        List<String> a = new ArrayList<>();
        if(attribute == null) {
            try {
                String b = "SELECT history FROM History;";
                sqlResultSet = sqlConnection.createStatement().executeQuery(b);
                String record = "null";
                int start = 0;
                while (sqlResultSet.next()) {
                    record = sqlResultSet.getString("history");
                    if (record != null) {
                        start = start + 1;
                        a.add(start + ". " + record);
                        if(start >= count) return a;
                    }
                }
                if(start < count) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.fewer-records").replace("%1$f", String.valueOf(start))));
                }
                if(sqlConnection != null) sqlConnection.close();
                if(sqlResultSet != null) sqlResultSet.close();
            }catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
                throw new RuntimeException(ex);
            }
        } else {
            try {
                String b = "SELECT history FROM History;";
                sqlResultSet = sqlConnection.createStatement().executeQuery(b);
                String record = "null";
                int start = 0;
                while (sqlResultSet.next()) {
                    record = sqlResultSet.getString("history");
                    if (record != null && record.contains(attribute)) {
                        a.add(record);
                        start = start + 1;
                    }
                }
                if(start < count) {
                    sender.sendMessage(setColors(getFileAccessor().getLang().getString("commands.history.fewer-records").replace("%1$f", String.valueOf(start))));
                }
                if(sqlConnection != null) sqlConnection.close();
                if(sqlResultSet != null) sqlResultSet.close();
            }catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
                throw new RuntimeException(ex);
            }
        }
        return a;
    }

    public void clearHistory() {
        try {
            String a = "DELETE FROM History;";
            getSQLiteConnection().createStatement().executeUpdate(a);
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }


    public void deleteFromMutedPlayers(String expression, String param) {
        sqlConnection = getSQLiteConnection();
        try {
            if(expression.equalsIgnoreCase("-n")) {
                String a = "DELETE FROM mutedPlayers WHERE name = '" + param + "';";
                sqlConnection.createStatement().executeUpdate(a);
            } else if(expression.equalsIgnoreCase("-ip")) {
                String a = "DELETE FROM mutedPlayers WHERE ip = '" + param + "';";
                sqlConnection.createStatement().executeUpdate(a);
            } else if(expression.equalsIgnoreCase("-id")) {
                String a = "DELETE FROM mutedPlayers WHERE id = '" + param + "';";
                sqlConnection.createStatement().executeUpdate(a);
            } else if(expression.equalsIgnoreCase("-u")) {
                String a = "DELETE FROM mutedPlayers WHERE uuid = '" + param + "';";
                sqlConnection.createStatement().executeUpdate(a);
            }
            sqlConnection.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
        } finally {
            try {
                if (sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public void deleteFromNullMutedPlayers(String expression, String param) {
        sqlConnection = getSQLiteConnection();
        try {
            if(expression.equalsIgnoreCase("-n")) {
                String a = "DELETE FROM nullMutedPlayers WHERE name = '" + param + "';";
                sqlConnection.createStatement().executeUpdate(a);
            } else if(expression.equalsIgnoreCase("-id")) {
                String a = "DELETE FROM nullMutedPlayers WHERE id = '" + param + "';";
                sqlConnection.createStatement().executeUpdate(a);
            }
            sqlConnection.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
        } finally {
            try {
                if (sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {
            }
        }
    }


    @Nullable
    public String getUuidByName(String playerName) {
        sqlConnection = getSQLiteConnection();
        try {
            String a = "SELECT uuid FROM allPlayers WHERE name = '" + playerName + "';";
            sqlResultSet = sqlConnection.createStatement().executeQuery(a);
            String uuid = "null";
            while (sqlResultSet.next()) {
                uuid = sqlResultSet.getString("uuid");
                if(uuid != null) {
                    return uuid;
                }
            }
            return uuid.equalsIgnoreCase("null") ? null : uuid;
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if (sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }


    public UUID getUUIDByIp(String ip) {
        sqlConnection = getSQLiteConnection();
        try {
            String a = "SELECT uuid FROM allPlayers WHERE ip = '" + ip + "';";
            sqlResultSet = sqlConnection.createStatement().executeQuery(a);
            String uuid = "null";
            while (sqlResultSet.next()) {
                uuid = sqlResultSet.getString("uuid");
                if(uuid != null) {
                    return UUID.fromString(uuid);
                }
            }
            return uuid.equalsIgnoreCase("null") ? null : UUID.fromString(uuid);
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if (sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }



    public String getIpByUUID(UUID uuid) {
        sqlConnection = getSQLiteConnection();
        String select = "SELECT ip FROM allPlayers WHERE uuid = '" + uuid + "';";
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(select);
            String ip = "UNKNOWN_ERROR";
            while (sqlResultSet.next()) {
                ip = sqlResultSet.getString("ip");
            }
            return ip;
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }


    public void insertIntoAllPlayers(String name, UUID uuid, String ip) {
        sqlConnection = getSQLiteConnection();
        String getUUID = "SELECT uuid FROM allPlayers;";
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(getUUID);
            while (sqlResultSet.next()) {
                String rUuid = sqlResultSet.getString("uuid");
                if(rUuid == null || rUuid.equalsIgnoreCase(String.valueOf(uuid))) return;
            }
            String insertName = "INSERT INTO allPlayers (name, uuid, ip) VALUES ('" + name + "', '" + uuid + "', '" + ip + "');";
            sqlConnection.createStatement().executeUpdate(insertName);
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public void deleteFromBannedPlayers(String expression, String param) {
        sqlConnection = getSQLiteConnection();
        try {
            if(expression.equalsIgnoreCase("-n")) {
                String a = "DELETE FROM bannedPlayers WHERE name = '" + param + "';";
                sqlConnection.createStatement().executeUpdate(a);
            } else if(expression.equalsIgnoreCase("-ip")) {
                String a = "DELETE FROM bannedPlayers WHERE ip = '" + param + "';";
                sqlConnection.createStatement().executeUpdate(a);
            } else if(expression.equalsIgnoreCase("-id")) {
                String a = "DELETE FROM bannedPlayers WHERE id = '" + param + "';";
                sqlConnection.createStatement().executeUpdate(a);
            } else if(expression.equalsIgnoreCase("-u")) {
                String a = "DELETE FROM bannedPlayers WHERE uuid = '" + param + "';";
                sqlConnection.createStatement().executeUpdate(a);
            }
            sqlConnection.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if (sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public void deleteFromNullBannedPlayers(String expression, String param) {
        sqlConnection = getSQLiteConnection();
        try {
            if(expression.equalsIgnoreCase("-n")) {
                String a = "DELETE FROM nullBannedPlayers WHERE name = '" + param + "';";
                sqlConnection.createStatement().executeUpdate(a);
            } else if(expression.equalsIgnoreCase("-id")) {
                String a = "DELETE FROM nullBannedPlayers WHERE id = '" + param + "';";
                sqlConnection.createStatement().executeUpdate(a);
            }
            sqlConnection.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if (sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {
            }
            try {
                if (sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {
            }
        }
    }

    public void insertIntoBannedPlayers(String id, String ip, String name, String initiatorName, String reason, BanType banType, String banDate, String banTime, UUID uuid, long unbanTime) {
            sqlConnection = getSQLiteConnection();
        try {
            String insert = "INSERT INTO bannedPlayers (id, ip, name, initiatorName, reason, banType, banDate, banTime, uuid, unbanTime) VALUES ('" + id + "', '" + ip + "', '" + name + "', '" + initiatorName + "', '" + reason + "', '" + banType + "', '" + banDate +"', '" + banTime +"', '" + uuid +"', '" + unbanTime + "');";
            sqlConnection.createStatement().executeUpdate(insert);
            sqlResultSet.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public void insertIntoMutedPlayers(String id, String ip, String name, String initiatorName, String reason, MuteType muteType, String banDate, String banTime, UUID uuid, long unmuteTime) {
        sqlConnection = getSQLiteConnection();
        try {
            String insert = "INSERT INTO mutedPlayers (id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, uuid, unmuteTime) VALUES ('" + id + "', '" + ip + "', '" + name + "', '" + initiatorName + "', '" + reason + "', '" + muteType + "', '" + banDate +"', '" + banTime +"', '" + uuid +"', '" + unmuteTime + "');";
            sqlConnection.createStatement().executeUpdate(insert);
            sqlResultSet.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public void insertIntoNullBannedPlayers(String id, String name, String initiatorName, String reason, BanType banType, String banDate, String banTime, UUID uuid,  long unbanTime) {
        sqlConnection = getSQLiteConnection();
        try {
            String insert = "INSERT INTO nullBannedPlayers (id, ip, name, initiatorName, reason, banType, banDate, banTime, uuid, unbanTime) VALUES ('" + id + "', 'NULL_PLAYER', '" + name + "', '" + initiatorName + "', '" + reason + "', '" + banType + "', '" + banDate +"', '" + banTime +"', '" + uuid + "', '" + unbanTime + "');";
            sqlConnection.createStatement().executeUpdate(insert);
            sqlResultSet.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public void insertIntoNullMutedPlayers(String id, String name, String initiatorName, String reason, MuteType muteType, String muteDate, String muteTime, UUID uuid, long unmuteTime) {
        sqlConnection = getSQLiteConnection();
        try {
            String insert = "INSERT INTO nullMutedPlayers (id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, uuid, unmuteTime) VALUES ('" + id + "', 'NULL_PLAYER', '" + name + "', '" + initiatorName + "', '" + reason + "', '" + muteType + "', '" + muteDate +"', '" + muteTime +"', '" + uuid + "', '" + unmuteTime + "');";
            sqlConnection.createStatement().executeUpdate(insert);
            sqlResultSet.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public void insertIntoNullBannedPlayersIP(String id, String ip, String initiatorName, String reason, BanType banType, String banDate, String banTime, long unbanTime) {
        sqlConnection = getSQLiteConnection();
        try {
            String insert = "INSERT INTO nullBannedPlayers (id, ip, name, initiatorName, reason, banType, banDate, banTime, uuid, unbanTime) VALUES ('" + id + "', '" + ip + "', 'NULL_PLAYER', '" + initiatorName + "', '" + reason + "', '" + banType + "', '" + banDate +"', '" + banTime +"', 'NULL_PLAYER', '" + unbanTime + "');";
            sqlConnection.createStatement().executeUpdate(insert);
            sqlResultSet.close();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
            throw new RuntimeException(e);
        } finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public void insertIntoNullMutedPlayersIP(String id, String ip, String initiatorName, String reason, MuteType muteType, String muteDate, String muteTime, long unmuteTime) {
        sqlConnection = getSQLiteConnection();
        try {
            String insert = "INSERT INTO nullMutedPlayers (id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, uuid, unmuteTime) VALUES ('" + id + "', '" + ip + "', 'NULL_PLAYER', '" + initiatorName + "', '" + reason + "', '" + muteType + "', '" + muteDate +"', '" + muteTime +"', 'NULL_PLAYER', '" + unmuteTime + "');";
            sqlConnection.createStatement().executeUpdate(insert);
            sqlResultSet.close();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
            throw new RuntimeException(e);
        } finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public List<Long> unbanTimesFromNullBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT unbanTime FROM nullBannedPlayers;";
        List<Long> unbanTimes = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String unbanTime = "UNBAN_TIME_ERROR";
            while (sqlResultSet.next()) {
                unbanTime = sqlResultSet.getString("unbanTime");
                if(unbanTime == null) {
                    unbanTime = "0";
                    unbanTimes.add(Long.valueOf(unbanTime));
                } else {
                    unbanTimes.add(Long.valueOf(unbanTime));
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return unbanTimes;
    }

    public List<String> ipsFromNullBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT ip FROM nullBannedPlayers;";
        List<String> ips = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String ip = "NULL_PLAYER";
            while (sqlResultSet.next()) {
                ip = sqlResultSet.getString("ip");
                if(ip == null) {
                    ip = "NULL_PLAYER";
                    ips.add(ip);
                } else {
                    ips.add(ip);
                }
            }
            return ips;
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        } finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public List<String> uuidFromNullBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT uuid FROM nullBannedPlayers;";
        List<String> uuids = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String uuid = "NULL_PLAYER";
            while (sqlResultSet.next()) {
                uuid = sqlResultSet.getString("uuid");
                if(uuid == null) {
                    uuid = "NULL_PLAYER";
                    uuids.add(uuid);
                } else {
                    uuids.add(uuid);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return uuids;
    }

    public List<String> banDatesFromNullBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT banDate FROM nullBannedPlayers;";
        List<String> banDates = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String banDate = "BAN_DATE_ERROR";
            while (sqlResultSet.next()) {
                banDate = sqlResultSet.getString("banDate");
                if(banDate == null) {
                    banDate = "BAN_DATE_ERROR";
                    banDates.add(banDate);
                } else {
                    banDates.add(banDate);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return banDates;
    }

    public List<String> banTimesFromNullBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT banTime FROM nullBannedPlayers;";
        List<String> banTimes = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String banTime = "BAN_TIME_ERROR";
            while (sqlResultSet.next()) {
                banTime = sqlResultSet.getString("banTime");
                if(banTime == null) {
                    banTime = "BAN_TIME_ERROR";
                    banTimes.add(banTime);
                } else {
                    banTimes.add(banTime);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return banTimes;
    }

    public List<String> idsFromNullBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT id FROM nullBannedPlayers;";
        List<String> ids = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String id = "ID_ERROR";
            while (sqlResultSet.next()) {
                id = sqlResultSet.getString("id");
                if(id == null) {
                    id = "ID_ERROR";
                    ids.add(id);
                } else {
                    ids.add(id);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return ids;
    }

    public List<String> namesFromNullBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT name FROM nullBannedPlayers;";
        List<String> names = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String name = "NAME_ERROR";
            while (sqlResultSet.next()) {
                name = sqlResultSet.getString("name");
                if(name == null) {
                    name = "NAME_ERROR";
                    names.add(name);
                } else {
                    names.add(name);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return names;
    }

    public List<String> initiatorsFromNullBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT initiatorName FROM nullBannedPlayers;";
        List<String> iNames = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String iName = "INITIATOR_NAME_ERROR";
            while (sqlResultSet.next()) {
                iName = sqlResultSet.getString("initiatorName");
                if(iName == null) {
                    iName = "INITIATOR_NAME_ERROR";
                    iNames.add(iName);
                } else {
                    iNames.add(iName);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return iNames;
    }

    public List<String> reasonsFromNullBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT reason FROM nullBannedPlayers;";
        List<String> reasons = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String reason = "REASON_ERROR";
            while (sqlResultSet.next()) {
                reason = sqlResultSet.getString("reason");
                if(reason == null) {
                    reason = "REASON_ERROR";
                    reasons.add(reason);
                } else {
                    reasons.add(reason);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return reasons;
    }

    public List<BanType> banTypesFromNullBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT banType FROM nullBannedPlayers;";
        List<BanType> banTypes = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String banType = "BAN_TYPE_ERROR";
            while (sqlResultSet.next()) {
                banType = sqlResultSet.getString("banType");
                if(banType == null) {
                    banType = "BAN_TYPE_ERROR";
                    banTypes.add(BanType.BAN_TYPE_ERROR);
                } else {
                    banTypes.add(BanType.valueOf(banType));
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return banTypes;
    }

    // nullBannedPlayersTable

    // bannedPlayer table
    public List<String> idsFromBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT id FROM bannedPlayers;";
        List<String> ids = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String id = "ID_ERROR";
            while (sqlResultSet.next()) {
                id = sqlResultSet.getString("id");
                if(id == null) {
                    id = "ID_ERROR";
                    ids.add(id);
                } else {
                    ids.add(id);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return ids;
    }

    public List<String> ipsFromBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT ip FROM bannedPlayers;";
        List<String> ids = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String id = "ID_ERROR";
            while (sqlResultSet.next()) {
                id = sqlResultSet.getString("ip");
                if(id == null) {
                    id = "IP_ERROR";
                    ids.add(id);
                } else {
                    ids.add(id);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return ids;
    }

    public List<String> namesFromBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT name FROM bannedPlayers;";
        List<String> names = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String name = "NAME_ERROR";
            while (sqlResultSet.next()) {
                name = sqlResultSet.getString("name");
                if(name == null) {
                    name = "NAME_ERROR";
                    names.add(name);
                } else {
                    names.add(name);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return names;
    }

    public List<String> initiatorsFromBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT initiatorName FROM bannedPlayers;";
        List<String> initiators = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String initiatorName = "ID_ERROR";
            while (sqlResultSet.next()) {
                initiatorName = sqlResultSet.getString("initiatorName");
                if(initiatorName == null) {
                    initiatorName = "INITIATOR_NAME_ERROR";
                    initiators.add(initiatorName);
                } else {
                    initiators.add(initiatorName);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return initiators;
    }

    public List<String> reasonsFromBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT reason FROM bannedPlayers;";
        List<String> reasons = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String reason = "ID_ERROR";
            while (sqlResultSet.next()) {
                reason = sqlResultSet.getString("reason");
                if(reason == null) {
                    reason = "REASON_ERROR";
                    reasons.add(reason);
                } else {
                    reasons.add(reason);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return reasons;
    }

    public List<BanType> banTypesFromBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT banType FROM bannedPlayers;";
        List<BanType> banTypes = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String banType = "BAN_TYPE_ERROR";
            while (sqlResultSet.next()) {
                banType = sqlResultSet.getString("banType");
                if(banType == null) {
                    banType = "BAN_TYPE_ERROR";
                    banTypes.add(BanType.BAN_TYPE_ERROR);
                } else {
                    banTypes.add(BanType.valueOf(banType));
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return banTypes;
    }

    public List<String> banDatesFromBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT banDate FROM bannedPlayers;";
        List<String> banDates = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String banDate = "BAN_DATE_ERROR";
            while (sqlResultSet.next()) {
                banDate = sqlResultSet.getString("banDate");
                if(banDate == null) {
                    banDate = "BAN_DATE_ERROR";
                    banDates.add(banDate);
                } else {
                    banDates.add(banDate);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return banDates;
    }

    public List<String> banTimesFromBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        String taskOne = "SELECT banTime FROM bannedPlayers;";
        List<String> banTimes = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String banTime = "BAN_TIME_ERROR";
            while (sqlResultSet.next()) {
                banTime = sqlResultSet.getString("banTime");
                if(banTime == null) {
                    banTime = "BAN_TIME_ERROR";
                    banTimes.add(banTime);
                } else {
                    banTimes.add(banTime);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return banTimes;
    }

    public List<String> uuidsFromBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<String> uuids = new ArrayList<>();
        String taskOne = "SELECT uuid FROM bannedPlayers;";
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String uuid = "UUID_ERROR";
            while (sqlResultSet.next()) {
                uuid = sqlResultSet.getString("uuid");
                if(uuid == null) {
                    uuid = "UUID_ERROR";
                    uuids.add(uuid);
                } else {
                    uuids.add(uuid);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return uuids;
    }

    public List<Long> unbanTimesFromBannedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<Long> unbanTimes = new ArrayList<>();
        String taskOne = "SELECT unbanTime FROM bannedPlayers;";
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String unbanTime = "-1";
            while (sqlResultSet.next()) {
                unbanTime = sqlResultSet.getString("unbanTime");
                if(unbanTime == null) {
                    unbanTime = "-1";
                    unbanTimes.add(Long.valueOf(unbanTime));
                } else {
                    unbanTimes.add(Long.valueOf(unbanTime));
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return unbanTimes;
    }
    //bannedPlayer table

    public void updateAllPlayers(Player player) {
        if (!getIpByUUID(player.getUniqueId()).equalsIgnoreCase(player.getAddress().getAddress().getHostAddress()) || !getUuidByName(player.getName()).equalsIgnoreCase(String.valueOf(player.getUniqueId()))) {
            sqlConnection = getSQLiteConnection();
            try {
                String a = "DELETE FROM allPlayers WHERE uuid = '" + String.valueOf(player.getUniqueId()) + "';";
                String b = "DELETE FROM allPlayer WHERE name = '" + player.getName() + "';";
                sqlConnection.createStatement().executeUpdate(a);
                sqlConnection.createStatement().executeUpdate(b);
                sqlConnection.close();
                insertIntoAllPlayers(player.getName(), player.getUniqueId(), player.getAddress().getAddress().getHostAddress());
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
                throw new RuntimeException(ex);
            } finally {
                try {
                    if (sqlConnection != null) {
                        sqlConnection.close();
                    }
                } catch (SQLException ex) {
                }
                try {
                    if (sqlStatement != null) {
                        sqlStatement.close();
                    }
                } catch (SQLException ex) {
                }
                try {
                    if (sqlResultSet != null) {
                        sqlResultSet.close();
                    }
                } catch (SQLException ex) {
                }
            }
        }
    }

    public List<String> getNamesFromAllPlayers() {
        sqlConnection = getSQLiteConnection();
        List<String> names = new ArrayList<>();
        String taskOne = "SELECT name FROM allPlayers;";
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String name = "NULL";
            while (sqlResultSet.next()) {
                name = sqlResultSet.getString("name");
                if(name == null) {
                    name = "NULL";
                    names.add(name);
                } else {
                    names.add(name);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return names;
    }

    public List<String> getUUIDsFromAllPlayers() {
        sqlConnection = getSQLiteConnection();
        List<String> uuids = new ArrayList<>();
        String taskOne = "SELECT uuid FROM allPlayers;";
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String uuid = "NULL";
            while (sqlResultSet.next()) {
                uuid = sqlResultSet.getString("uuid");
                if(uuid == null) {
                    uuid = "NULL";
                    uuids.add(uuid);
                } else {
                    uuids.add(uuid);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return uuids;
    }

    public List<String> getIpsFromAllPlayers() {
        sqlConnection = getSQLiteConnection();
        List<String> ips = new ArrayList<>();
        String taskOne = "SELECT ip FROM allPlayers;";
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String ip = "NULL";
            while (sqlResultSet.next()) {
                ip = sqlResultSet.getString("ip");
                if(ip == null) {
                    ip = "NULL";
                    ips.add(ip);
                } else {
                    ips.add(ip);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return ips;
    }

    public void clearBans() {
        sqlConnection = getSQLiteConnection();
        try {
            String a = "DELETE FROM bannedPlayers;";
            String b = "DELETE FROM nullBannedPlayers;";
            sqlConnection.createStatement().executeUpdate(a);
            sqlConnection.createStatement().executeUpdate(b);
            sqlConnection.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public void clearMutes() {
        sqlConnection = getSQLiteConnection();
        try {
            String a = "DELETE FROM mutedPlayers;";
            String b = "DELETE FROM nullMutedPlayers;";
            sqlConnection.createStatement().executeUpdate(a);
            sqlConnection.createStatement().executeUpdate(b);
            sqlConnection.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to edit the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
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
        sqlConnection = getSQLiteConnection();
        List<Long> a = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT unmuteTime FROM nullMutedPlayers;");
            String b = "UNMUTE_TIME_ERROR";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("unmuteTime");
                if(b == null) {
                    b = "0";
                    a.add(Long.valueOf(b));
                } else {
                    a.add(Long.valueOf(b));
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> ipsFromNullMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<String> b = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT ip FROM nullMutedPlayers;");
            String c = "NULL_PLAYER";
            while (sqlResultSet.next()) {
                c = sqlResultSet.getString("ip");
                if(c == null) {
                    c = "NULL_PLAYER";
                    b.add(c);
                } else {
                    b.add(c);
                }
            }
            return b;
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
    }

    public List<String> uuidsFromNullMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<String> b = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT uuid FROM nullMutedPlayers;");
            String c = "NULL_PLAYER";
            while (sqlResultSet.next()) {
                c = sqlResultSet.getString("uuid");
                if(c == null) {
                    c = "NULL_PLAYER";
                    b.add(c);
                } else {
                    b.add(c);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return b;
    }

    public List<String> muteDatesFromNullMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<String> a = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT muteDate FROM nullMutedPlayers;");
            String b = "MUTE_DATE_ERROR";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("muteDate");
                if(b == null) {
                    b = "MUTE_DATE_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> muteTimesFromNullMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<String> a = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT muteTime FROM nullMutedPlayers;");
            String b = "MUTE_TIME_ERROR";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("muteTime");
                if(b == null) {
                    b = "MUTE_TIME_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> idsFromNullMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<String> a = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT id FROM nullMutedPlayers;");
            String b = "ID_ERROR";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("id");
                if(b == null) {
                    b = "ID_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> namesFromNullMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<String> a = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT name FROM nullMutedPlayers;");
            String b = "NAME_ERROR";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("name");
                if(b == null) {
                    b = "NAME_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> initiatorsFromNullMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<String> a = new ArrayList<>();
        try {
            sqlResultSet = getSQLiteConnection().createStatement().executeQuery("SELECT initiatorName FROM nullMutedPlayers;");
            String b = "INITIATOR_NAME_ERROR";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("initiatorName");
                if(b == null) {
                    b = "INITIATOR_NAME_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> reasonsFromNullMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<String> a = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT reason FROM nullMutedPlayers;");
            String b = "REASON_ERROR";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("reason");
                if(b == null) {
                    b = "REASON_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }
        return a;
    }

    public List<MuteType> muteTypesFromNullMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<MuteType> a = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT muteType FROM nullMutedPlayers;");
            String b = "BAN_TYPE_ERROR";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("muteType");
                if(b == null) {
                    b = "BAN_TYPE_ERROR";
                    a.add(MuteType.BAN_TYPE_ERROR);
                } else {
                    a.add(MuteType.valueOf(b));
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> idsFromMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<String> a = new ArrayList<>();
        try {
            sqlResultSet = getSQLiteConnection().createStatement().executeQuery("SELECT id FROM mutedPlayers;");
            String b = "ID_ERROR";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("id");
                if(b == null) {
                    b = "ID_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> ipsFromMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<String> a = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT ip FROM mutedPlayers;");
            String b = "ID_ERROR";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("ip");
                if(b == null) {
                    b = "IP_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> namesFromMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<String> a = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT name FROM mutedPlayers;");
            String b = "NAME_ERROR";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("name");
                if(b == null) {
                    b = "NAME_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            throw new RuntimeException("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!");
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> initiatorsFromMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<String> a = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT initiatorName FROM mutedPlayers;");
            String b = "ID_ERROR";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("initiatorName");
                if(b == null) {
                    b = "INITIATOR_NAME_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> reasonsFromMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<String> a = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT reason FROM mutedPlayers;");
            String b = "ID_ERROR";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("reason");
                if(b == null) {
                    b = "REASON_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<MuteType> muteTypesFromMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<MuteType> a = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT muteType FROM mutedPlayers;");
            String b = "MUTE_TYPE_ERROR";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("muteType");
                if(b == null) {
                    b = "MUTE_TYPE_ERROR";
                    a.add(MuteType.BAN_TYPE_ERROR);
                } else {
                    a.add(MuteType.valueOf(b));
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> muteDatesFromMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<String> a = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT muteDate FROM mutedPlayers;");
            String b = "MUTE_DATE_ERROR";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("muteDate");
                if(b == null) {
                    b = "MUTE_DATE_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> muteTimesFromMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<String> a = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT muteTime FROM mutedPlayers;");
            String b = "MUTE_TIME_ERROR";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("muteTime");
                if(b == null) {
                    b = "MUTE_TIME_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<String> uuidsFromMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<String> a = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT uuid FROM mutedPlayers;");
            String b = "UUID_ERROR";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("uuid");
                if(b == null) {
                    b = "UUID_ERROR";
                    a.add(b);
                } else {
                    a.add(b);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ex) {}
        }
        return a;
    }

    public List<Long> unmuteTimesFromMutedPlayersTable() {
        sqlConnection = getSQLiteConnection();
        List<Long> a = new ArrayList<>();
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery("SELECT unmuteTime FROM mutedPlayers;");
            String b = "-1";
            while (sqlResultSet.next()) {
                b = sqlResultSet.getString("unmuteTime");
                if(b == null) {
                    b = "-1";
                    a.add(Long.valueOf(b));
                } else {
                    a.add(Long.valueOf(b));
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | SQLite] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ex) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
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
