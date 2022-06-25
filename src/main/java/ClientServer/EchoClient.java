package ClientServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class EchoClient {

    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final String MESSAGE_END = "/end";
    private static final int SERVER_PORT = 9000;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    public static void main(String[] args) {
        new EchoClient().start();
    }

    private void start() {
        try {
            openConnection();
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String messageToServer = scanner.nextLine();
                sendMessage(messageToServer);

                //exit if user entered "/end"
                if (MESSAGE_END.equalsIgnoreCase(messageToServer) )
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openConnection () throws IOException {
        socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        new Thread(() -> {
            try {
                while (true) {
                    final String messageFromServer = in.readUTF();
                    System.out.println("Message from server: " + messageFromServer);
                    if (MESSAGE_END.equalsIgnoreCase(messageFromServer)){
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }).start();
    }

    private void closeConnection() {
        if(in != null){
            try {
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(out != null)
        {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(socket!= null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage(String s) {
        try {
            out.writeUTF(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}