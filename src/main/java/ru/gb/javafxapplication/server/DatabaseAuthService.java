package ru.gb.javafxapplication.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class DatabaseAuthService implements AuthService {

    private Connection connection;
    private Connection getConnection() throws SQLException {
        if (connection == null){
            connection = CreateConnection();
        }
        return connection;
    }
    protected abstract Connection CreateConnection() throws SQLException;

    @Override
    public void close() throws IOException {
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection = null;
        }
    }

    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        try(Statement statement = getConnection().createStatement()) {
            String query = "SELECT login, password, nick FROM users WHERE login = '" + login + "'";
            try(ResultSet resultSet = statement.executeQuery(query)){
                if (resultSet.next()){
                    User user = new User(
                            resultSet.getString("nick"),
                            resultSet.getString("login"),
                            resultSet.getString("password"));
                    if (user.isPasswordCorrect(password)){
                        return user.getNick();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public  boolean changeNick(String login, String newNick) {
        try(Statement statement = getConnection().createStatement()) {
            String query = "UPDATE users SET nick = '" + newNick + "'WHERE login = '" + login + "'";
            return statement.executeUpdate(query) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
