package by.alis.functionalbans.databases.SQLite;

import by.alis.functionalbans.spigot.FunctionalBansSpigot;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class SQLCore {


    FunctionalBansSpigot plugin;
    public SQLCore(FunctionalBansSpigot plugin) {
        this.plugin = plugin;
    }
    Connection sqlConnection;
    Statement sqlStatement;
    ResultSet sqlResultSet;

    protected abstract Connection getSQLConnection();

    protected abstract void setupTables();

}
