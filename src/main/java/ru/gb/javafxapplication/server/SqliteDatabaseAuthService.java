package ru.gb.javafxapplication.server;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class SqliteDatabaseAuthService extends DatabaseAuthService {

    @Override
    protected Connection CreateConnection() throws SQLException {
        String connectionString = "jdbc:sqlite:src/Database/java.chat.db";
        return DriverManager.getConnection(connectionString);
    }
}
