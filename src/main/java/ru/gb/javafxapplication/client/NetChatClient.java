package ru.gb.javafxapplication.client;

import ru.gb.javafxapplication.common.Command;
import ru.gb.javafxapplication.common.Message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetChatClient {
    private static final String SERVER_ADDRESS = "192.168.1.61";
    private static final int PORT = 9000;

    private final Controller controller;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;


    public NetChatClient(Controller controller) {
        this.controller = controller;
    }

    public void openConnection() throws IOException {
        socket = new Socket(SERVER_ADDRESS, PORT);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
        new Thread(()->{
            try {
                if (waitAuth()){
                    controller.setAuth(true);
                    readMessages();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                closeConnection();
            }
        }).start();
    }

    private boolean waitAuth() throws IOException {
        while (true){
            final String text = inputStream.readUTF();
            System.out.println("received: "+ text);

            Message message;
            try {
                message = Message.fromString(text);
            }
            catch (RuntimeException exc){
                exc.printStackTrace();
                controller.addNotification("Error parsing message from server: " + exc.getMessage());
                continue;
            }

            if (message.isCommandEquals(Command.END)){
                return false;
            }
            else if (message.isCommandEquals(Command.STOP)) {
                controller.addNotification("Authorization timeout");
                sendMessage(Command.END);
                return false;
            }
            else if (message.isCommandEquals(Command.AUTHOK)){
                String nick = message.getParameter(0);
                controller.addMessage("Authorized successfully as " + nick);
                break;
            }
            else if (message.isCommandEquals(Command.ERROR)) {
                controller.addMessage(message.getParameter(0));
            }
            else {
                controller.addNotification("Unexpected command from server: " + message);
            }
        }
        return true;
    }

    public void closeConnection() {
        if (outputStream != null) {
            try {
                outputStream.close();
                outputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (inputStream != null){
            try {
                inputStream.close();
                inputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (socket != null){
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void readMessages() throws IOException {
        while (true) {
            final String text = inputStream.readUTF();
            Message message;

            try {
                message = Message.fromString(text);
            } catch (RuntimeException exc) {
                exc.printStackTrace();
                controller.addNotification(exc.getMessage());
                continue;
            }

            if (message.isCommandEquals(Command.END)) {
                break;
            }

            String messageText = null;
            if (message.isCommandEquals(Command.MESSAGE)) {
                messageText = message.getParameter(0);
            }
            else if (message.isCommandEquals(Command.CLIENTS)){
                controller.updateClients(message.getParameters());
            }else {
                messageText = "Unexpected command " + text;
            }

            if (messageText != null) {
                controller.addMessage(messageText);
            }
        }
    }

    public void sendMessage(Command command, String... parameters ) {
        try {
            String text = new Message(command, parameters).toString();

            outputStream.writeUTF(text);
            System.out.println("sent " + text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void authenicate(String login, String pass) {
        if (socket == null || socket.isClosed()){
            try {
                openConnection();
            }
            catch (IOException exc)
            {
                exc.printStackTrace();
                controller.addNotification(exc.getMessage());
                return;
            }
        }
        sendMessage(Command.AUTH, login, pass);
    }
}
