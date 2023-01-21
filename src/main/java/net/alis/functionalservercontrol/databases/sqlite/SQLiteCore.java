package net.alis.functionalservercontrol.databases.sqlite;

import net.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class SQLiteCore {


    FunctionalServerControlSpigot plugin;
    public SQLiteCore(FunctionalServerControlSpigot plugin) {
        this.plugin = plugin;
    }
    Connection sqlConnection;
    Statement sqlStatement;
    ResultSet sqlResultSet;

    protected abstract Connection getSQLiteConnection();

    protected abstract void setupTables();

}
