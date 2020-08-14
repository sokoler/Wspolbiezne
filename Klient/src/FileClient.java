import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.util.Scanner;

public class FileClient{

    private Socket socket;
    private static String login;
    private static String pathToFiles = "D:\\Programy\\Wspolbieżne\\Klient\\UsersFiles\\";
    private DataOutputStream dos;
    private DataInputStream dis;

    public FileClient(String host, int port) {
        try {
            socket = new Socket(host, port);
            login();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void login() throws IOException, InterruptedException {
        dis = new DataInputStream(socket.getInputStream());
        dos = new DataOutputStream(socket.getOutputStream());

        Scanner scan = new Scanner(System.in);
        System.out.println("Podaj login");
        login = scan.nextLine();
        System.out.println("Podaj hasło");
        String password = scan.nextLine();

        dos.writeUTF(login);
        dos.writeUTF(password);

        String answer = dis.readUTF();
        System.out.println(answer);

        boolean checkUser = dis.readBoolean();
//        dos.close();
//        dis.close();

        if(checkUser) {

            Path path = Paths.get(pathToFiles + login);

            createDirectory(path);
            watchDirectory(path);
        }

//        String line = "";
//        while(!line.equals("disconnect")){
//            line = scan.nextLine();
//        }
//
//        dos.close();
    }

    public void watchDirectory(Path path) throws IOException, InterruptedException {
        WatchService watchService
                = FileSystems.getDefault().newWatchService();

        path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE
                //     StandardWatchEventKinds.ENTRY_DELETE,
               // StandardWatchEventKinds.ENTRY_MODIFY
                );

        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                System.out.println(
                        "Event kind:" + event.kind()
                                + ". File affected: " + event.context());
                Runnable runnable = () -> {
                    System.out.println("Inside : " + Thread.currentThread().getName());
                    File file = new File(pathToFiles + login + "\\" + event.context());
                    try {
                        Thread.sleep(50);
                        sendFile(file);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }

                };

//                Thread t = new Thread() {
//                    @Override
//                    public void run() {
//                        System.out.println("Inside : " + Thread.currentThread().getName());
//                        File file = new File(pathToFiles + login + "\\" + event.context());
//                        try {
//                            sendFile(file);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                };
//                t.start();  // call back run()

                System.out.println("Creating Thread...");
                Thread thread = new Thread(runnable);

                System.out.println("Starting Thread...");
                thread.start();
            }
            key.reset();
        }
    }

    public static void createDirectory(Path path) throws IOException {

        if (!Files.exists(path)) {

            Files.createDirectory(path);
            System.out.println("Directory created");
        } else {

            System.out.println("Directory already exists");
        }
    }

    public void sendFile(File file) throws IOException {

        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[4096];  //4096 16384

        // writing name
        dos.writeUTF(file.getName());
        // writing length
        dos.writeLong(file.length());

        System.out.println(file.getName() +" "+ file.length());

        int count;

        while ((count = fis.read(buffer)) > 0) {
            dos.write(buffer,0,count);
        }

       // fis.close();
      //  dos.close();
    }

}
