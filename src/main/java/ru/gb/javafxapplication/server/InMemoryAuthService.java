package ru.gb.javafxapplication.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InMemoryAuthService implements AuthService {
    public InMemoryAuthService() {
        this.users =  new ArrayList<User>();
        for (int i = 1; i < 5 ; i++) {
            users.add(new User("nick"+i, "login"+i, "pass"+i));
        }
    }

    private List<User> users;
    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        for (User u:users) {
            if(login.equals(u.getLogin()) && u.isPasswordCorrect(password)){
                return u.getNick();
            }
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        System.out.println("Authentication service is closed");
    }
}

