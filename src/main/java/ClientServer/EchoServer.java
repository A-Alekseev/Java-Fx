package ClientServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class EchoServer {
    private static final int PORT = 9000;
    private static final String MESSAGE_END = "/end";

    public static void main(String[] args) {
        System.out.println("Waiting client connection...");

        try(ServerSocket serverSocket = new ServerSocket(PORT);
            Socket socket = serverSocket.accept();
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);
        )
        {
            System.out.println("Client connected");
            Thread consoleInputThread = new Thread(()->{
                while (true) {
                    String messageToClient = scanner.nextLine();
                    try {
                        if (socket.isClosed())
                            break;
                        out.writeUTF(messageToClient);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            consoleInputThread.start();

            while (true) {
                String message = in.readUTF();
                System.out.println("Message from client: " + message);
                if (MESSAGE_END.equalsIgnoreCase(message)) {
                    out.writeUTF(MESSAGE_END);
                    break;
                }
                out.writeUTF("echo: " + message);
            }

            //TODO how to stop consoleInputThread ?
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
