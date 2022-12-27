package by.alis.functionalservercontrol.databases.SQLite;

import by.alis.functionalservercontrol.spigot.FunctionalServerControlSpigot;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class SQLCore {


    FunctionalServerControlSpigot plugin;
    public SQLCore(FunctionalServerControlSpigot plugin) {
        this.plugin = plugin;
    }
    Connection sqlConnection;
    Statement sqlStatement;
    ResultSet sqlResultSet;

    protected abstract Connection getSQLConnection();

    protected abstract void setupTables();

}
