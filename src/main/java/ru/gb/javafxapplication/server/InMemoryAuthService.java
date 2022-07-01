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

    private static class User{
         private String nick;
         private String login;
         private String password;

        public User(String nick, String login, String password) {
            this.nick = nick;
            this.login = login;
            this.password = password;
        }

        public String getNick() {
            return nick;
        }

        public String getLogin() {
            return login;
        }

        public boolean isPasswordCorrect(String password) {
            return password.equals(this.password);
        }
    }

    private List<User> users;
    @Override
    public String getNickByLoginAndPassword(String login, String password) {
        for (User u:users) {
            if(login.equals(u.getLogin()) && u.isPasswordCorrect(password)){
                return u.nick;
            }
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        System.out.println("Authentication service is closed");
    }
}
