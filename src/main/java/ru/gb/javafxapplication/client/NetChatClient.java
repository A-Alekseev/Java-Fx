package ru.gb.javafxapplication.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class NetChatClient {
    private static final String SERVER_ADDRESS = "192.168.1.61";
    public static final String END_MESSAGE = "/end";
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
            final String message = inputStream.readUTF();
            System.out.println("received: "+ message);
            if (END_MESSAGE.equalsIgnoreCase(message)){
                return false;
            }

            if (message.startsWith("/authok")){
                String[] split = message.split("\\p{Blank}+");
                String nick = split[1];
                controller.addMessage("Authorized successfully as " +nick);
                break;
            }
            else {
                controller.addMessage(message);
            }
        }
        return true;
    }

    public void closeConnection() {
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
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void readMessages() throws IOException{
        while (true){
            final String message = inputStream.readUTF();
            if (END_MESSAGE.equalsIgnoreCase(message)) {
                break;
            }
            controller.addMessage(message);
        }
    }

    public void sendMessage(String userMessage) {
        try {
            outputStream.writeUTF(userMessage);
            System.out.println("sent " + userMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
