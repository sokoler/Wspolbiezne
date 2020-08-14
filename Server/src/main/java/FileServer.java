import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;

public class FileServer extends Thread {

    private Thread t;
    private String threadName;

    static int id = 1;

    private ServerSocket ss;
    private Socket clientSock;
    private String login;
    private static String pathToFiles = "D:\\Programy\\Wspolbieżne\\Server\\UsersFiles\\";
    private DataInputStream dis;
    private DataOutputStream dos;

    public FileServer(int port, String name) {
        try {
            threadName = name;
            ss = new ServerSocket(port);
            clientSock = ss.accept();
            readLogin(clientSock);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start () {
        System.out.println("Starting " +  threadName );
        if (t == null) {
            t = new Thread (this, threadName);
            t.start ();
        }
    }

    public void run() {
        while (true) {

            System.out.println("Running " +  threadName );

            try {
                saveFile(clientSock,pathToFiles + login +"\\");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveFile(Socket clientSock, String path) throws IOException {
     //   dis = new DataInputStream(clientSock.getInputStream());
        byte[] buffer = new byte[4096]; //4096 16384

        String fileName = dis.readUTF();
        int fileSize = (int) dis.readLong();

        int read = 0;
        int totalRead = 0;

        FileOutputStream fos = new FileOutputStream(path + fileName);
        System.out.println("Nazwa pliku " + fileName);

        while ((read = dis.read(buffer, 0, Math.min(buffer.length, fileSize))) > 0) {
            totalRead += read;
            fileSize -= read;
            System.out.println("Przeczytano " + totalRead + " bajtów.");
            fos.write(buffer, 0, read);
        }

      //  fos.close();
      //  dis.close();
    }

    public void readLogin(Socket clientSock) throws IOException {
        dis = new DataInputStream(clientSock.getInputStream());
        dos = new DataOutputStream(clientSock.getOutputStream());

        DatabaseConnection databaseConnection = new DatabaseConnection();
        Connection connection = databaseConnection.getConnection();
        DatabaseHandler databaseHandler = new DatabaseHandler();

        databaseHandler.createUserTable(connection);
        String serverAnswer = " ";

        login = dis.readUTF();
        String password = dis.readUTF();

        System.out.println(login + " " + password);

        try {
            serverAnswer = databaseHandler.login(connection, login, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dos.writeUTF(serverAnswer);
        dos.writeBoolean(DatabaseHandler.checkUser);
    }

    public static void main(String[] args) throws SQLException {

            FileServer fs = new FileServer(6666, "Thread Name - " + id++);
            fs.start();

    }


}