package by.alis.functionalservercontrol.databases.sqlite;

import by.alis.functionalservercontrol.spigot.FunctionalServerControl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class SQLCore {


    FunctionalServerControl plugin;
    public SQLCore(FunctionalServerControl plugin) {
        this.plugin = plugin;
    }
    Connection sqlConnection;
    Statement sqlStatement;
    ResultSet sqlResultSet;

    protected abstract Connection getSQLConnection();

    protected abstract void setupTables();

}
