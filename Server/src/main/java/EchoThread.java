import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.*;

public class EchoThread extends Thread {

    protected Socket socket;

    private String login;
    private static String pathToFiles = "D:\\Programy\\Wspolbieżne\\Server\\UsersFiles\\";
    private DataInputStream dis;
    private DataOutputStream dos;
    Semaphore sem;
    String threadName;

    public EchoThread(Socket clientSocket, Semaphore sem, String threadName) {

        this.threadName = threadName;
        this.sem = sem;
        this.socket = clientSocket;
    }

    public void run() {

        InputStream input;
        BufferedReader bufferedReader;
        OutputStream output;
        try {
            input = socket.getInputStream();
            output = socket.getOutputStream();
         //   bufferedReader = new BufferedReader(new InputStreamReader(input));
            dis = new DataInputStream(input);
            dos = new DataOutputStream(output);

            System.out.println(Thread.currentThread());

            readLogin();

        } catch (IOException e) {
            return;
        }
        String line;

        if(DatabaseHandler.checkUser) {

            while (true) {
                try {

                    // Will get the permit to access shared resource
                    System.out.println(threadName + " waiting for a permit.");

                    // acquiring the lock
                    sem.acquire();

                    System.out.println(threadName + " gets a permit.");

                    saveFile(pathToFiles + login + "\\");

//                line = bufferedReader.readLine();
//                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
//                    socket.close();
//                    return;
//                } else {
//                    dos.writeBytes(line + "\n\r");
//                    dos.flush();
//                }

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    // Release the permit.
                    System.out.println(threadName + " releases the permit.");
                    sem.release();
                    return;
                }
            }
        }
    }

    private void saveFile(String path) throws IOException {

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

    }

    public void readLogin() throws IOException {

//        DatabaseConnection databaseConnection = new DatabaseConnection();
//        Connection connection = databaseConnection.getConnection();
        DatabaseHandler databaseHandler = new DatabaseHandler();

        String serverAnswer = " ";

        login = dis.readUTF();
        String password = dis.readUTF();

        System.out.println(login + " " + password);

        try {
            serverAnswer = databaseHandler.login(Main.connection, login, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dos.writeUTF(serverAnswer);
        dos.writeBoolean(DatabaseHandler.checkUser);

    }

}