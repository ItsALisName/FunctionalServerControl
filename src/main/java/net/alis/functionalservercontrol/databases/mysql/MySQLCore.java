package net.alis.functionalservercontrol.databases.mysql;

import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import static net.alis.functionalservercontrol.spigot.managers.file.SFAccessor.getFileAccessor;

public abstract class MySQLCore {

    FunctionalServerControlSpigot plugin;
    public MySQLCore(FunctionalServerControlSpigot plugin) {
        this.plugin = plugin;
    }

    Connection mysqlConnection;
    Statement mysqlStatement;

    PreparedStatement mysqlPreparedStatement;
    ResultSet mysqlResultSet;

    String host = getFileAccessor().getGeneralConfig().getString("plugin-settings.mysql.host");
    String baseName = getFileAccessor().getGeneralConfig().getString("plugin-settings.mysql.database_name");
    String port = getFileAccessor().getGeneralConfig().getString("plugin-settings.mysql.port");
    String userName = getFileAccessor().getGeneralConfig().getString("plugin-settings.mysql.username");
    String password = getFileAccessor().getGeneralConfig().getString("plugin-settings.mysql.password");

    public abstract Connection getMysqlConnection();

    public abstract void setupTables();

}
