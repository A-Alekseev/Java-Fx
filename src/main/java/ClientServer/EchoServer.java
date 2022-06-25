package ClientServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServer {
    private static final int PORT = 9000;
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("Waiting client connection...");
            final Socket socket = serverSocket.accept();
            System.out.println("Client connected");
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            while (true) {
                String message = in.readUTF();
                System.out.println("Message from client: " + message);
                if (message.equals("/end")) {
                    out.writeUTF("/end");
                    break;
                }
                out.writeUTF("echo: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
