package by.alis.functionalservercontrol.databases.SQLite;

import by.alis.functionalservercontrol.API.Enums.BanType;
import by.alis.functionalservercontrol.API.Enums.MuteType;
import by.alis.functionalservercontrol.spigot.FunctionalServerControl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

import static by.alis.functionalservercontrol.spigot.Additional.SomeUtils.TextUtils.setColors;
import static by.alis.functionalservercontrol.spigot.Managers.Files.SFAccessor.getFileAccessor;

public class SQLManager extends SQLCore {

    public SQLManager(FunctionalServerControl plugin) {
        super(plugin);
    }

    @Override
    protected Connection getSQLConnection() {
        if (getFileAccessor().getSQLiteFile().exists()) {
            try {
                Class.forName("org.sqlite.JDBC");
                sqlConnection = DriverManager.getConnection("jdbc:sqlite:" + getFileAccessor().getSQLiteFile().getPath());
                return sqlConnection;
            } catch (ClassNotFoundException | SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to connect to the database!"));
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] -> Unknown error, try reinstalling the plugin."));
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] No further work possible!"));
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Disabling the plugin..."));
                this.plugin.getPluginLoader().disablePlugin(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class));
                return null;
            }

        } else {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to connect to the database!"));
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] -> File &4&odatabase.db &4has not been found!"));
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
                    String queryTableOne = "CREATE TABLE IF NOT EXISTS bannedPlayers (id varchar(255), ip varchar(255) , name varchar(255), initiatorName varchar(255), reason varchar(255), banType varchar(255), banDate varchar(255), banTime varchar(255), uuid varchar(255), unbanTime varchar(255));";
                    String queryTableTwo = "CREATE TABLE IF NOT EXISTS nullBannedPlayers (id varchar(255), ip varchar(255), name varchar(255) , initiatorName varchar(255), reason varchar(255), banType varchar(255), banDate varchar(255), banTime varchar(255), uuid varchar(255), unbanTime varchar(255));";
                    String queryTableThree = "CREATE TABLE IF NOT EXISTS allPlayers (name varchar(255), uuid varchar(255), ip varchar(255));";
                    String queryTableFour = "CREATE TABLE IF NOT EXISTS mutedPlayers (id varchar(255), ip varchar(255) , name varchar(255), initiatorName varchar(255), reason varchar(255), muteType varchar(255), muteDate varchar(255), muteTime varchar(255), uuid varchar(255), unmuteTime varchar(255));";
                    String queryTableFive = "CREATE TABLE IF NOT EXISTS Cooldowns (PlayerAndCommand varchar(255), Time varchar(255));";
                    String queryTableSix = "CREATE TABLE IF NOT EXISTS nullMutedPlayers (id varchar(255), ip varchar(255), name varchar(255) , initiatorName varchar(255), reason varchar(255), muteType varchar(255), muteDate varchar(255), muteTime varchar(255), uuid varchar(255), unmuteTime varchar(255));";
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
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to connect to the database!"));
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] -> Unknown error, try reinstalling the plugin."));
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] No further work possible!"));
                    Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Disabling the plugin..."));
                    this.plugin.getPluginLoader().disablePlugin(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class));
                    return null;
                }
            } else {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] No further work possible!"));
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] Disabling the plugin..."));
                this.plugin.getPluginLoader().disablePlugin(FunctionalServerControl.getProvidingPlugin(FunctionalServerControl.class));
                return null;
            }
        }
    }

    @Override
    public void setupTables() {
        sqlConnection = getSQLConnection();
        String queryTableOne = "CREATE TABLE IF NOT EXISTS bannedPlayers (id varchar(255), ip varchar(255) , name varchar(255), initiatorName varchar(255), reason varchar(255), banType varchar(255), banDate varchar(255), banTime varchar(255), uuid varchar(255), unbanTime varchar(255));";
        String queryTableTwo = "CREATE TABLE IF NOT EXISTS nullBannedPlayers (id varchar(255), ip varchar(255), name varchar(255) , initiatorName varchar(255), reason varchar(255), banType varchar(255), banDate varchar(255), banTime varchar(255), uuid varchar(255), unbanTime varchar(255));";
        String queryTableThree = "CREATE TABLE IF NOT EXISTS allPlayers (name varchar(255), uuid varchar(255), ip varchar(255));";
        String queryTableFour = "CREATE TABLE IF NOT EXISTS mutedPlayers (id varchar(255), ip varchar(255) , name varchar(255), initiatorName varchar(255), reason varchar(255), muteType varchar(255), muteDate varchar(255), muteTime varchar(255), uuid varchar(255), unmuteTime varchar(255));";
        String queryTableFive = "CREATE TABLE IF NOT EXISTS Cooldowns (PlayerAndCommand varchar(255), Time varchar(255));";
        String queryTableSix = "CREATE TABLE IF NOT EXISTS nullMutedPlayers (id varchar(255), ip varchar(255), name varchar(255) , initiatorName varchar(255), reason varchar(255), muteType varchar(255), muteDate varchar(255), muteTime varchar(255), uuid varchar(255), unmuteTime varchar(255));";
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
            throw new RuntimeException(a);
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

    public void insertIntoHistory(String message) {
        sqlConnection = getSQLConnection();
        String getUUID = "SELECT uuid FROM allPlayers;";
        try {
            String a = "INSERT INTO History (history) VALUES ('" + message + "');";
            sqlConnection.createStatement().executeUpdate(a);
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
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


    public void clearHistory() {
        sqlConnection = getSQLConnection();
        try {
            String a = "DELETE FROM History;";
            sqlConnection.createStatement().executeUpdate(a);
            sqlConnection.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
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
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
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
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
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


    public String getUuidByName(String playerName) {
        sqlConnection = getSQLConnection();
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
            return uuid;
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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
        sqlConnection = getSQLConnection();
        String select = "SELECT ip FROM allPlayers WHERE uuid = '" + uuid + "';";
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(select);
            String ip = "UNKNOWN_ERROR";
            while (sqlResultSet.next()) {
                ip = sqlResultSet.getString("ip");
            }
            return ip;
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
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
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
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
            sqlConnection = getSQLConnection();
        try {
            String insert = "INSERT INTO bannedPlayers (id, ip, name, initiatorName, reason, banType, banDate, banTime, uuid, unbanTime) VALUES ('" + id + "', '" + ip + "', '" + name + "', '" + initiatorName + "', '" + reason + "', '" + banType + "', '" + banDate +"', '" + banTime +"', '" + uuid +"', '" + unbanTime + "');";
            sqlConnection.createStatement().executeUpdate(insert);
            sqlResultSet.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
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
        sqlConnection = getSQLConnection();
        try {
            String insert = "INSERT INTO mutedPlayers (id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, uuid, unmuteTime) VALUES ('" + id + "', '" + ip + "', '" + name + "', '" + initiatorName + "', '" + reason + "', '" + muteType + "', '" + banDate +"', '" + banTime +"', '" + uuid +"', '" + unmuteTime + "');";
            sqlConnection.createStatement().executeUpdate(insert);
            sqlResultSet.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
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

    public void insertIntoNullBannedPlayers(String id, String name, String initiatorName, String reason, BanType banType, String banDate, String banTime, long unbanTime) {
        sqlConnection = getSQLConnection();
        try {
            String insert = "INSERT INTO nullBannedPlayers (id, ip, name, initiatorName, reason, banType, banDate, banTime, uuid, unbanTime) VALUES ('" + id + "', 'NULL_PLAYER', '" + name + "', '" + initiatorName + "', '" + reason + "', '" + banType + "', '" + banDate +"', '" + banTime +"', 'NULL_PLAYER', '" + unbanTime + "');";
            sqlConnection.createStatement().executeUpdate(insert);
            sqlResultSet.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
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

    public void insertIntoNullMutedPlayers(String id, String name, String initiatorName, String reason, MuteType muteType, String muteDate, String muteTime, long unmuteTime) {
        sqlConnection = getSQLConnection();
        try {
            String insert = "INSERT INTO nullMutedPlayers (id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, uuid, unmuteTime) VALUES ('" + id + "', 'NULL_PLAYER', '" + name + "', '" + initiatorName + "', '" + reason + "', '" + muteType + "', '" + muteDate +"', '" + muteTime +"', 'NULL_PLAYER', '" + unmuteTime + "');";
            sqlConnection.createStatement().executeUpdate(insert);
            sqlResultSet.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
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
        sqlConnection = getSQLConnection();
        try {
            String insert = "INSERT INTO nullBannedPlayers (id, ip, name, initiatorName, reason, banType, banDate, banTime, uuid, unbanTime) VALUES ('" + id + "', '" + ip + "', 'NULL_PLAYER', '" + initiatorName + "', '" + reason + "', '" + banType + "', '" + banDate +"', '" + banTime +"', 'NULL_PLAYER', '" + unbanTime + "');";
            sqlConnection.createStatement().executeUpdate(insert);
            sqlResultSet.close();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
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
        sqlConnection = getSQLConnection();
        try {
            String insert = "INSERT INTO nullMutedPlayers (id, ip, name, initiatorName, reason, muteType, muteDate, muteTime, uuid, unmuteTime) VALUES ('" + id + "', '" + ip + "', 'NULL_PLAYER', '" + initiatorName + "', '" + reason + "', '" + muteType + "', '" + muteDate +"', '" + muteTime +"', 'NULL_PLAYER', '" + unmuteTime + "');";
            sqlConnection.createStatement().executeUpdate(insert);
            sqlResultSet.close();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
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

    private List<Long> unbanTimesFromNullBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> ipsFromNullBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> uuidFromNullBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> banDatesFromNullBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> banTimesFromNullBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> idsFromNullBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> namesFromNullBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> initiatorsFromNullBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> reasonsFromNullBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<BanType> banTypesFromNullBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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
    private List<String> idsFromBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> ipsFromBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> namesFromBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> initiatorsFromBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> reasonsFromBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<BanType> banTypesFromBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> banDatesFromBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> banTimesFromBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> uuidsFromBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<Long> unbanTimesFromBannedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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
        if (!getIpByUUID(player.getUniqueId()).equalsIgnoreCase(player.getAddress().getAddress().getHostAddress())) {
            sqlConnection = getSQLConnection();
            try {
                String a = "DELETE FROM allPlayers WHERE uuid = '" + String.valueOf(player.getUniqueId()) + "';";
                sqlConnection.createStatement().executeUpdate(a);
                sqlConnection.close();
                insertIntoAllPlayers(player.getName(), player.getUniqueId(), player.getAddress().getAddress().getHostAddress());
            } catch (SQLException ex) {
                Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
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
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    public List<String> getPlayersAndCommandsFromCooldowns() {
        sqlConnection = getSQLConnection();
        List<String> playersAndCommands = new ArrayList<>();
        String taskOne = "SELECT * FROM Cooldowns;";
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String pac = "NULL";
            while (sqlResultSet.next()) {
                if(sqlResultSet.getString("PlayerAndCommand") == null) {
                    pac = "NULL";
                    playersAndCommands.add(pac);
                } else {
                    pac = sqlResultSet.getString("PlayerAndCommand");
                    playersAndCommands.add(pac);
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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
        return playersAndCommands;
    }

    public void clearCooldowns() {
        sqlConnection = getSQLConnection();
        try {
            String a = "DELETE FROM Cooldowns;";
            sqlConnection.createStatement().executeUpdate(a);
            sqlConnection.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
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

    public List<Long> getTimesFromCooldowns() {
        sqlConnection = getSQLConnection();
        List<Long> times = new ArrayList<>();
        String taskOne = "SELECT Time FROM Cooldowns;";
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(taskOne);
            String time = "0";
            while (sqlResultSet.next()) {
                time = sqlResultSet.getString("Time");
                if(time == null) {
                    time = "0";
                    times.add(Long.parseLong(time));
                } else {
                    times.add(Long.parseLong(time));
                }
            }
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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
        return times;
    }

    public void saveCooldowns(TreeMap<String, Long> cooldowns) {
        sqlConnection = getSQLConnection();
        String getUUID = "SELECT uuid FROM allPlayers;";
        try {
            for(Map.Entry<String, Long> e : cooldowns.entrySet()) {
                String playerAndCommand = e.getKey();
                long time = e.getValue();
                String insert = "INSERT INTO Cooldowns (PlayerAndCommand, Time) VALUES ('" + playerAndCommand + "', '" + String.valueOf(time) + "');";
                sqlConnection.createStatement().executeUpdate(insert);
            }
            if(sqlResultSet != null){
                sqlResultSet.close();
            }
            return;
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
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

    public void clearBans() {
        sqlConnection = getSQLConnection();
        try {
            String a = "DELETE FROM bannedPlayers;";
            String b = "DELETE FROM nullBannedPlayers;";
            sqlConnection.createStatement().executeUpdate(a);
            sqlConnection.createStatement().executeUpdate(b);
            sqlConnection.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
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
        sqlConnection = getSQLConnection();
        try {
            String a = "DELETE FROM mutedPlayers;";
            String b = "DELETE FROM nullMutedPlayers;";
            sqlConnection.createStatement().executeUpdate(a);
            sqlConnection.createStatement().executeUpdate(b);
            sqlConnection.close();
        } catch (SQLException ex) {
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to edit the database!"));
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


    private List<Long> unmuteTimesFromNullMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> ipsFromNullMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> uuidsFromNullMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> muteDatesFromNullMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> muteTimesFromNullMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> idsFromNullMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> namesFromNullMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> initiatorsFromNullMutedPlayersTable() {
        sqlConnection = getSQLConnection();
        List<String> a = new ArrayList<>();
        try {
            sqlResultSet = getSQLConnection().createStatement().executeQuery("SELECT initiatorName FROM nullMutedPlayers;");
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> reasonsFromNullMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
            throw new RuntimeException(ex);
        }
        return a;
    }

    private List<MuteType> muteTypesFromNullMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> idsFromMutedPlayersTable() {
        sqlConnection = getSQLConnection();
        List<String> a = new ArrayList<>();
        try {
            sqlResultSet = getSQLConnection().createStatement().executeQuery("SELECT id FROM mutedPlayers;");
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> ipsFromMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> namesFromMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            throw new RuntimeException("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!");
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

    private List<String> initiatorsFromMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> reasonsFromMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<MuteType> muteTypesFromMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> muteDatesFromMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> muteTimesFromMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<String> uuidsFromMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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

    private List<Long> unmuteTimesFromMutedPlayersTable() {
        sqlConnection = getSQLConnection();
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
            Bukkit.getConsoleSender().sendMessage(setColors("&4[FunctionalServerControl | Error] An error occurred while trying to read the database!"));
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
