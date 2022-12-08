package by.alis.functionalbans.databases.SQLite;

import by.alis.functionalbans.spigot.Additional.Enums.BanType;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.Languages.LangEnglish;
import by.alis.functionalbans.spigot.Additional.GlobalSettings.Languages.LangRussian;
import by.alis.functionalbans.spigot.FunctionalBansSpigot;
import by.alis.functionalbans.spigot.Managers.FilesManagers.FileAccessor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static by.alis.functionalbans.spigot.Additional.GlobalSettings.StaticSettingsAccessor.getConfigSettings;
import static by.alis.functionalbans.spigot.Additional.Other.TextUtils.setColors;

public class SQLManager extends SQLCore {

    private final FileAccessor fileAccessor = new FileAccessor();

    public SQLManager(FunctionalBansSpigot plugin) {
        super(plugin);
    }

    @Override
    protected Connection getSQLConnection() {
        if (this.fileAccessor.getSQLFile().exists()) {
            if (getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                try {
                    Class.forName("org.sqlite.JDBC");
                    sqlConnection = DriverManager.getConnection("jdbc:sqlite:" + this.fileAccessor.getSQLFile());
                    return sqlConnection;
                } catch (ClassNotFoundException | SQLException ignored) {
                    Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_CONNECTION_ERROR));
                    Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_UNKNOWN_ERROR));
                    Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_PRE_OFF_PLUGIN));
                    Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_OFF_PLUGIN));
                    this.plugin.getPluginLoader().disablePlugin(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class));
                    return null;
                }
            }

            if (getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                try {
                    Class.forName("org.sqlite.JDBC");
                    sqlConnection = DriverManager.getConnection("jdbc:sqlite:" + this.fileAccessor.getSQLFile());
                    return sqlConnection;
                } catch (ClassNotFoundException | SQLException ignored) {
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_CONNECTION_ERROR));
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_UNKNOWN_ERROR));
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_PRE_OFF_PLUGIN));
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_OFF_PLUGIN));
                    this.plugin.getPluginLoader().disablePlugin(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class));
                    return null;
                }
            }

            try {
                Class.forName("org.sqlite.JDBC");
                sqlConnection = DriverManager.getConnection("jdbc:sqlite:" + this.fileAccessor.getSQLFile());
                return sqlConnection;
            } catch (ClassNotFoundException | SQLException ignored) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_CONNECTION_ERROR));
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_UNKNOWN_ERROR));
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_PRE_OFF_PLUGIN));
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_OFF_PLUGIN));
                this.plugin.getPluginLoader().disablePlugin(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class));
                return null;
            }

        } else {
            if (getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_CONNECTION_ERROR));
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_BASE_FILE_NOT_FOUND));
                Bukkit.getConsoleSender().sendMessage("");
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_ATTEMPT_FILE_RECREATE));
                try {
                    this.fileAccessor.getSQLFile().createNewFile();
                } catch (IOException ignored) {
                }
                if (this.fileAccessor.getSQLFile().exists()) {
                    Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_FILE_RECREATED));
                    try {
                        Class.forName("org.sqlite.JDBC");
                        sqlConnection = DriverManager.getConnection("jdbc:sqlite:" + this.fileAccessor.getSQLFile());
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_RECONNECTION_SUCCESS));
                        String queryTableOne = "CREATE TABLE IF NOT EXISTS bannedPlayers (id varchar(255) PRIMARY KEY, ip varchar(255) , name varchar(255), initiatorName varchar(255), reason varchar(255), banType varchar(255), banDate varchar(255), banTime varchar(255), uuid varchar(255), unbanTime varchar(255));";
                        String queryTableTwo = "CREATE TABLE IF NOT EXISTS nullBannedPlayers (id varchar(255) PRIMARY KEY, ip varchar(255), name varchar(255) , initiatorName varchar(255), reason varchar(255), banType varchar(255), banDate varchar(255), banTime varchar(255), uuid varchar(255), unbanTime varchar(255));";
                        String queryTableThree = "CREATE TABLE IF NOT EXISTS allPlayers (name varchar(255) PRIMARY KEY, uuid varchar(255), ip varchar(255));";
                        String queryTableFour = "CREATE TABLE IF NOT EXISTS mutedPlayers (id varchar(255) PRIMARY KEY, ip varchar(255) , name varchar(255), initiatorName varchar(255), reason varchar(255), muteType varchar(255), muteDate varchar(255), muteTime varchar(255), uuid varchar(255), unmuteTime varchar(255));";
                        sqlStatement = sqlConnection.createStatement();
                        sqlStatement.executeUpdate(queryTableOne);
                        sqlStatement.executeUpdate(queryTableTwo);
                        sqlStatement.executeUpdate(queryTableThree);
                        sqlStatement.executeUpdate(queryTableFour);
                        sqlStatement.close();
                        return sqlConnection;
                    } catch (ClassNotFoundException | SQLException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_CONNECTION_ERROR));
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_UNKNOWN_ERROR));
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_PRE_OFF_PLUGIN));
                        Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_OFF_PLUGIN));
                        this.plugin.getPluginLoader().disablePlugin(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class));
                        return null;
                    }
                } else {
                    Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_PRE_OFF_PLUGIN_1));
                    Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_OFF_PLUGIN));
                    this.plugin.getPluginLoader().disablePlugin(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class));
                    return null;
                }
            }

            if (getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_CONNECTION_ERROR));
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_BASE_FILE_NOT_FOUND));
                Bukkit.getConsoleSender().sendMessage("");
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_ATTEMPT_FILE_RECREATE));
                try {
                    this.fileAccessor.getSQLFile().createNewFile();
                } catch (IOException ignored) {
                }
                if (this.fileAccessor.getSQLFile().exists()) {
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_FILE_RECREATED));
                    try {
                        Class.forName("org.sqlite.JDBC");
                        sqlConnection = DriverManager.getConnection("jdbc:sqlite:" + this.fileAccessor.getSQLFile());
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_RECONNECTION_SUCCESS));
                        String queryTableOne = "CREATE TABLE IF NOT EXISTS bannedPlayers (id varchar(255) PRIMARY KEY, ip varchar(255) , name varchar(255), initiatorName varchar(255), reason varchar(255), banType varchar(255), banDate varchar(255), banTime varchar(255), uuid varchar(255), unbanTime varchar(255));";
                        String queryTableTwo = "CREATE TABLE IF NOT EXISTS nullBannedPlayers (id varchar(255) PRIMARY KEY, ip varchar(255), name varchar(255) , initiatorName varchar(255), reason varchar(255), banType varchar(255), banDate varchar(255), banTime varchar(255), uuid varchar(255), unbanTime varchar(255));";
                        String queryTableThree = "CREATE TABLE IF NOT EXISTS allPlayers (name varchar(255) PRIMARY KEY, uuid varchar(255), ip varchar(255));";
                        String queryTableFour = "CREATE TABLE IF NOT EXISTS mutedPlayers (id varchar(255) PRIMARY KEY, ip varchar(255) , name varchar(255), initiatorName varchar(255), reason varchar(255), muteType varchar(255), muteDate varchar(255), muteTime varchar(255), uuid varchar(255), unmuteTime varchar(255));";
                        sqlStatement = sqlConnection.createStatement();
                        sqlStatement.executeUpdate(queryTableOne);
                        sqlStatement.executeUpdate(queryTableTwo);
                        sqlStatement.executeUpdate(queryTableThree);
                        sqlStatement.executeUpdate(queryTableFour);
                        sqlStatement.close();
                        return sqlConnection;
                    } catch (ClassNotFoundException | SQLException ignored) {
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_CONNECTION_ERROR));
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_UNKNOWN_ERROR));
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_PRE_OFF_PLUGIN));
                        Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_OFF_PLUGIN));
                        this.plugin.getPluginLoader().disablePlugin(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class));
                        return null;
                    }
                } else {
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_PRE_OFF_PLUGIN_1));
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_OFF_PLUGIN));
                    this.plugin.getPluginLoader().disablePlugin(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class));
                    return null;
                }
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_CONNECTION_ERROR));
            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_BASE_FILE_NOT_FOUND));
            Bukkit.getConsoleSender().sendMessage("");
            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_ATTEMPT_FILE_RECREATE));
            try {
                this.fileAccessor.getSQLFile().createNewFile();
            } catch (IOException ignored) {
            }
            if (this.fileAccessor.getSQLFile().exists()) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_FILE_RECREATED));
                try {
                    Class.forName("org.sqlite.JDBC");
                    sqlConnection = DriverManager.getConnection("jdbc:sqlite:" + this.fileAccessor.getSQLFile());
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_RECONNECTION_SUCCESS));
                    String queryTableOne = "CREATE TABLE IF NOT EXISTS bannedPlayers (id varchar(255) PRIMARY KEY, ip varchar(255) , name varchar(255), initiatorName varchar(255), reason varchar(255), banType varchar(255), banDate varchar(255), banTime varchar(255), uuid varchar(255), unbanTime varchar(255));";
                    String queryTableTwo = "CREATE TABLE IF NOT EXISTS nullBannedPlayers (id varchar(255) PRIMARY KEY, ip varchar(255), name varchar(255) , initiatorName varchar(255), reason varchar(255), banType varchar(255), banDate varchar(255), banTime varchar(255), uuid varchar(255), unbanTime varchar(255));";
                    String queryTableThree = "CREATE TABLE IF NOT EXISTS allPlayers (name varchar(255) PRIMARY KEY, uuid varchar(255), ip varchar(255));";
                    String queryTableFour = "CREATE TABLE IF NOT EXISTS mutedPlayers (id varchar(255) PRIMARY KEY, ip varchar(255) , name varchar(255), initiatorName varchar(255), reason varchar(255), muteType varchar(255), muteDate varchar(255), muteTime varchar(255), uuid varchar(255), unmuteTime varchar(255));";
                    sqlStatement = sqlConnection.createStatement();
                    sqlStatement.executeUpdate(queryTableOne);
                    sqlStatement.executeUpdate(queryTableTwo);
                    sqlStatement.executeUpdate(queryTableThree);
                    sqlStatement.executeUpdate(queryTableFour);
                    sqlStatement.close();
                    return sqlConnection;
                } catch (ClassNotFoundException | SQLException ignored) {
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_CONNECTION_ERROR));
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_UNKNOWN_ERROR));
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_PRE_OFF_PLUGIN));
                    Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_OFF_PLUGIN));
                    this.plugin.getPluginLoader().disablePlugin(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class));
                    return null;
                }
            } else {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_PRE_OFF_PLUGIN_1));
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_OFF_PLUGIN));
                this.plugin.getPluginLoader().disablePlugin(FunctionalBansSpigot.getProvidingPlugin(FunctionalBansSpigot.class));
                return null;
            }

        }
    }

    @Override
    public void setupTables() {
        sqlConnection = getSQLConnection();
        String queryTableOne = "CREATE TABLE IF NOT EXISTS bannedPlayers (id varchar(255) PRIMARY KEY, ip varchar(255) , name varchar(255), initiatorName varchar(255), reason varchar(255), banType varchar(255), banDate varchar(255), banTime varchar(255), uuid varchar(255), unbanTime varchar(255));";
        String queryTableTwo = "CREATE TABLE IF NOT EXISTS nullBannedPlayers (id varchar(255) PRIMARY KEY, ip varchar(255), name varchar(255) , initiatorName varchar(255), reason varchar(255), banType varchar(255), banDate varchar(255), banTime varchar(255), uuid varchar(255), unbanTime varchar(255));";
        String queryTableThree = "CREATE TABLE IF NOT EXISTS allPlayers (name varchar(255) PRIMARY KEY, uuid varchar(255), ip varchar(255));";
        String queryTableFour = "CREATE TABLE IF NOT EXISTS mutedPlayers (id varchar(255) PRIMARY KEY, ip varchar(255) , name varchar(255), initiatorName varchar(255), reason varchar(255), muteType varchar(255), muteDate varchar(255), muteTime varchar(255), uuid varchar(255), unmuteTime varchar(255));";
        try {
            sqlStatement = sqlConnection.createStatement();
            sqlStatement.executeUpdate(queryTableOne);
            sqlStatement.executeUpdate(queryTableTwo);
            sqlStatement.executeUpdate(queryTableThree);
            sqlStatement.executeUpdate(queryTableFour);
            sqlStatement.close();
        } catch (SQLException a) {
            if (getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_EDIT_ERROR));
                return;
            }

            if (getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_EDIT_ERROR));
                return;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_EDIT_ERROR));

        } finally {
            try {
                if (sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ignored) {
            }
            try {
                if (sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ignored) {
            }
            try {
                if (sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ignored) {
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
        } catch (SQLException ignored) {
            if (getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if (getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
        } finally {
            try {
                if (sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ignored) {
            }
            try {
                if (sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ignored) {
            }
            try {
                if (sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ignored) {}
        }
    }



    public String selectIpByUUID(UUID uuid) {
        sqlConnection = getSQLConnection();
        String select = "SELECT ip FROM allPlayers WHERE uuid = '" + uuid + "';";
        try {
            sqlResultSet = sqlConnection.createStatement().executeQuery(select);
            String ip = "UNKNOWN_ERROR";
            while (sqlResultSet.next()) {
                ip = sqlResultSet.getString("ip");
            }
            return ip;
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
        } finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ignored) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ignored) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ignored) {}
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
            sqlResultSet.close();
            return;
        } catch (SQLException ex) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_EDIT_ERROR));
                return;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_EDIT_ERROR));
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_EDIT_ERROR));

        } finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ignored) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ignored) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ignored) {}
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_EDIT_ERROR));
                return;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_EDIT_ERROR));
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_EDIT_ERROR));
        } finally {
            try {
                if (sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ignored) {
            }
            try {
                if (sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ignored) {
            }
            try {
                if (sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ignored) {
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_EDIT_ERROR));
                return;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_EDIT_ERROR));
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_EDIT_ERROR));
        } finally {
            try {
                if (sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ignored) {
            }
            try {
                if (sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ignored) {
            }
            try {
                if (sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

    public void insertIntoBannedPlayers(String id, String ip, String name, String initiatorName, String reason, BanType banType, String banDate, String banTime, UUID uuid, long unbanTime) {
            sqlConnection = getSQLConnection();
        try {
            String insert = "INSERT INTO bannedPlayers (id, ip, name, initiatorName, reason, banType, banDate, banTime, uuid, unbanTime) VALUES ('" + id + "', '" + ip + "', '" + name + "', '" + initiatorName + "', '" + reason + "', '" + banType + "', '" + banDate +"', '" + banTime +"', '" + uuid +"', '" + unbanTime + "');";
            sqlConnection.createStatement().executeUpdate(insert);
            sqlResultSet.close();
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_EDIT_ERROR));
                return;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_EDIT_ERROR));
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_EDIT_ERROR));
        } finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ignored) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ignored) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ignored) {}
        }
    }

    public void insertIntoNullBannedPlayers(String id, String name, String initiatorName, String reason, BanType banType, String banDate, String banTime, long unbanTime) {
        sqlConnection = getSQLConnection();
        try {
            String insert = "INSERT INTO nullBannedPlayers (id, ip, name, initiatorName, reason, banType, banDate, banTime, uuid, unbanTime) VALUES ('" + id + "', 'NULL_PLAYER', '" + name + "', '" + initiatorName + "', '" + reason + "', '" + banType + "', '" + banDate +"', '" + banTime +"', 'NULL_PLAYER', '" + unbanTime + "');";
            sqlConnection.createStatement().executeUpdate(insert);
            sqlResultSet.close();
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_EDIT_ERROR));
                return;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_EDIT_ERROR));
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_EDIT_ERROR));
        } finally {
            try {
                if(sqlConnection != null) {
                    sqlConnection.close();
                }
            } catch (SQLException ignored) {}
            try {
                if(sqlStatement != null) {
                    sqlStatement.close();
                }
            } catch (SQLException ignored) {}
            try {
                if(sqlResultSet != null) {
                    sqlResultSet.close();
                }
            } catch (SQLException ignored) {}
        }
    }

    // nullBannedPlayersTable

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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
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
        } catch (SQLException ignored) {
            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("ru_RU")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangRussian.SQL_READ_ERROR));
                return null;
            }

            if(getConfigSettings().getConsoleLanguageMode().equalsIgnoreCase("en_US")) {
                Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
                return null;
            }

            Bukkit.getConsoleSender().sendMessage(setColors(LangEnglish.SQL_READ_ERROR));
            return null;
        }
        return unbanTimes;
    }
    //bannedPlayer table

    //Combining nullBannedPlayers and bannedPlayers list

    public List<String> getBanedIds() {
        List<String> ids = new ArrayList<>();
        ids.addAll(idsFromBannedPlayersTable());
        ids.addAll(idsFromNullBannedPlayersTable());
        return ids;
    }

    public List<String> getBanedIps() {
        List<String> ips = new ArrayList<>();
        ips.addAll(ipsFromBannedPlayersTable());
        ips.addAll(ipsFromNullBannedPlayersTable());
        return ips;
    }

    public List<String> getBanedNames() {
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

    public List<String> getBanedReasons() {
        List<String> reasons = new ArrayList<>();
        reasons.addAll(reasonsFromBannedPlayersTable());
        reasons.addAll(reasonsFromNullBannedPlayersTable());
        return reasons;
    }

    public List<BanType> getBanedTypes() {
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

    public List<String> getBanedUUIDs() {
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



}
