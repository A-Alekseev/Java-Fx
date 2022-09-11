package ru.gb.javafxapplication.server;

import java.io.Closeable;

public interface AuthService extends Closeable {
    String getNickByLoginAndPassword(String login, String password);

    boolean changeNick(String login, String newNick);
}
