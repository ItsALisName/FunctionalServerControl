package net.alis.functionalservercontrol.databases.sqlite;

import net.alis.functionalservercontrol.spigot.FunctionalServerControl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class SQLiteCore {


    FunctionalServerControl plugin;
    public SQLiteCore(FunctionalServerControl plugin) {
        this.plugin = plugin;
    }
    Connection sqlConnection;
    Statement sqlStatement;
    ResultSet sqlResultSet;

    protected abstract Connection getSQLiteConnection();

    protected abstract void setupTables();

}
