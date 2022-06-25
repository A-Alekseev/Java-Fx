package ru.gb.javafxapplication.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private static final String END_MESSAGE = "/end";

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Socket socket;
    private NetChatServer server;
    private String nick;
    private AuthService authService;

    public ClientHandler(Socket socket, NetChatServer server, AuthService authService) throws IOException {
        this.socket = socket;
        this.server = server;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
        this.authService = authService;
        new Thread(()->{
            try {
                if (authenticate()) {
                    readMessages();
                }
            }
            finally {
                closeConnection();
            }
            }).start();
    }

    private boolean authenticate() {
        while (true){
            try {
                String message = inputStream.readUTF();
                System.out.println("receiived: "+ message);
                if(END_MESSAGE.equalsIgnoreCase(message))
                    return false;

                final String[] splits = message.split("\\p{Blank}+");
                if (splits.length == 3 && splits[0].equals("/auth")){
                    String login = splits[1];
                    String pass = splits[2];
                    String nick = authService.getNickByLoginAndPassword(login, pass);
                    if(nick != null){
                        if (server.isNickBusy(nick)){
                            sendMessage("User had already authorized");
                            continue;
                        }
                        sendMessage("/authok " + nick);
                        this.nick = nick;
                        server.sendToClients(nick + " entered chat", null, null );
                        server.subscribe(this);
                        break;
                    }
                    else {
                        sendMessage("Incorrect login and password");
                    }

                }
                else {
                    sendMessage("Not authenticated");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void closeConnection() {
        sendMessage(END_MESSAGE);
        
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (inputStream != null){
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null){
            try {
                server.unsubscribe(this);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages() {
        while (true){
            try {
                final String message = inputStream.readUTF();
                if(END_MESSAGE.equalsIgnoreCase(message)){
                    break;
                }
                else if (message.startsWith("/w"))
                {
                    String[] split = message.split("\\p{Blank}+");
                    if (split.length < 3){
                        sendMessage("format: /w <nick> <message text>");
                    }
                    else {
                        String nick = split[1];
                        if (server.isNickBusy(nick)){
                            String pMessage = extractPersonalMessageText(message, nick);
                            server.sendToClients(pMessage, this.nick, nick);
                        }
                        else
                        {
                            sendMessage("nick " + nick + " not connected");
                        }
                    }
                }
                else
                {
                    server.sendToClients(message, this.nick, null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getNick() {
        return nick;
    }

    static String extractPersonalMessageText(String message, String nick){
        String textNoW = message.substring(2, message.length()).trim();
        String textNoNick =textNoW.substring(nick.length(), textNoW.length()).trim();
        return textNoNick;
    }
}
