package ru.gb.javafxapplication.server;

import java.sql.SQLException;

public class ServerLauncher {
    public static void main(String[] args) {
        try {
            new NetChatServer().start();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
