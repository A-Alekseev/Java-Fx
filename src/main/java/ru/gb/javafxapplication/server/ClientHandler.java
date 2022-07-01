package ru.gb.javafxapplication.server;

import ru.gb.javafxapplication.common.Command;
import ru.gb.javafxapplication.common.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    final int AUTH_TIMEOUT = 12000;

    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private Socket socket;
    private NetChatServer server;
    private String nick;
    private AuthService authService;
    private Thread authenticationTimeoutThread;

    public ClientHandler(Socket socket, NetChatServer server, AuthService authService) throws IOException {
        this.socket = socket;
        this.server = server;
        this.authService = authService;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
        authenticationTimeoutThread = new Thread(() -> {
            try {
                Thread.sleep(AUTH_TIMEOUT);
                sendMessage(Command.STOP); // Если в другом потоке не будет вызван метод interrupt, то мы попадем сюда
            } catch (InterruptedException e) {
                // В другом потоке была успешная авторизация
                System.out.println("Authenticated successfully");
            }
        });
        authenticationTimeoutThread.start();

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
                String text = inputStream.readUTF();
                System.out.println("receiived: "+ text);
                Message message = Message.fromString(text);

                if(message.isCommandEquals(Command.END))
                    return false;

                if (message.isCommandEquals(Command.AUTH))
                {
                    String login = message.getParameter(0);
                    String pass = message.getParameter(1);
                    String nick = authService.getNickByLoginAndPassword(login, pass);
                    if(nick != null){
                        if (server.isNickBusy(nick)){
                            sendMessage(Command.ERROR, "User had already authorized");
                            continue;
                        }
                        authenticationTimeoutThread.interrupt();
                        sendMessage(Command.AUTHOK, nick);
                        this.nick = nick;
                        server.sendTextToClients(nick + " entered chat", null, null );
                        server.subscribe(this);
                        break;
                    }
                    else {
                        sendMessage(Command.ERROR, "Incorrect login and password");
                    }
                }
                else {
                    sendMessage(Command.ERROR, "You are not authenticated");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private void closeConnection() {
        sendMessage(Command.END);
        
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


    public void sendMessage(Command command, String... parameters) {
        try {
            outputStream.writeUTF(new Message(command, parameters).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readMessages() {
        while (true){
            try {
                final String text = inputStream.readUTF();
                Message message;
                try{
                    message = Message.fromString(text);
                }
                catch (RuntimeException exc){
                    System.out.println(exc.getStackTrace());
                    sendMessage(Command.ERROR, exc.getMessage());
                    continue;
                }

                if(message.isCommandEquals(Command.END)){
                    break;
                }
                else if (message.isCommandEquals(Command.PRIVATE_MESSAGE))
                {
                    String nick = message.getParameter(0);
                    String messageText = message.getParameter(1);
                    if (server.isNickBusy(nick)){
                        server.sendTextToClients(messageText, this.nick, nick);
                    }
                    else
                    {
                        sendMessage(Command.ERROR,"nick " + nick + " not connected");
                    }
                }
                else if (message.isCommandEquals(Command.MESSAGE))
                {
                    String messageText = message.getParameter(0);
                    server.sendTextToClients(messageText, this.nick, null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getNick() {
        return nick;
    }

}

