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
    public DataInputStream dis;
    public DataOutputStream dos;
    static Semaphore semaphore = new Semaphore(4);
    String threadName;
    private static int liczba;

    public EchoThread(Socket clientSocket, String threadName) {

        this.threadName = threadName;
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

        if (DatabaseHandler.checkUser) {

          //  while (true) {

                MyRunnable t1 = new MyRunnable("Nazwa ELo" + liczba ++ );

                Thread thread = new Thread(t1);
                thread.start();

         //   }
        }
    }

    public class MyRunnable implements Runnable {

        String name;

        public MyRunnable(String name) {
            this.name = name;
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

        public void run() {
            try {

                semaphore.acquire();
                System.out.println(name + " : got the permit!");
                System.out.println("available Semaphore permits : "
                        + semaphore.availablePermits());

                try {

                    saveFile(pathToFiles + login + "\\");

                } finally {

                    // calling release() after a successful acquire()
                    System.out.println(name + " : releasing lock...");
                    semaphore.release();
                    System.out.println(name + " : available Semaphore permits now: "
                            + semaphore.availablePermits());

                }

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
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