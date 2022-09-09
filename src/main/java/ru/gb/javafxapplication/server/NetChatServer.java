package ru.gb.javafxapplication.server;

import ru.gb.javafxapplication.common.Command;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NetChatServer {
    private static final int PORT = 9000;

  private final List<ClientHandler> clients;

    public NetChatServer() {
        clients = new ArrayList<ClientHandler>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT);
             AuthService authService = new InMemoryAuthService()){
            while (true){
                System.out.println("Waiting connection");
                Socket socket = serverSocket.accept();
                new ClientHandler(socket, this, authService);
                System.out.println("Connection accepted");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean isNickBusy(String nick) {
        return clients.stream().filter(ch->ch.getNick().equals(nick)).count() > 0;
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientsList();
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientsList();
    }

    private void broadcastClientsList() {
        String[] nicks = clients.stream()
                .map(ClientHandler::getNick)
                .toArray(String[]::new);

        for (ClientHandler client : clients) {
            client.sendMessage(Command.CLIENTS, nicks);
        }
    }


    public void sendTextToClients(String message, String nickFrom, String nickTo) {
        String prefix = (nickFrom != null ? nickFrom : "server") + "->" + (nickTo != null ? nickTo : "all") + ": ";
        for (ClientHandler client:clients) {
            if (nickTo == null || client.getNick().equals(nickTo) || client.getNick().equals(nickFrom)){
                client.sendMessage(Command.MESSAGE, prefix + message);
            }
        }
    }
}
