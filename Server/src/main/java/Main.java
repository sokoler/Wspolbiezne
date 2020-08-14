import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.util.concurrent.*;

public class Main {

    static final int PORT = 6666;
    static Connection connection;
    private static Semaphore sem;
    private static int liczba;
  //  Semaphore sem;

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            DatabaseConnection databaseConnection = new DatabaseConnection();
            connection = databaseConnection.getConnection();
            DatabaseHandler databaseHandler = new DatabaseHandler();
            databaseHandler.createUserTable(connection);

            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            new EchoThread(socket,sem,"Wątek Klienta "+ liczba++ ).start();
        }
    }
}