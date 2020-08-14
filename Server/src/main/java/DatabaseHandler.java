import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

public class DatabaseHandler {

    static boolean checkUser = false;

    public void createUserTable(Connection connection){

        Statement statement;

        try {
         // String query = "CREATE TABLE IF NOT EXISTS appUser(id SERIAL PRIMARY KEY, login varchar(255), password varchar(255))";
         // String insert = "INSERT INTO appUser(login, password) VALUES ('admin', '1234')";
            statement = connection.createStatement();
         // statement.executeUpdate(query);
         // statement.executeUpdate(insert);
            System.out.println("Table created !");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createDirectory(String name) throws IOException {

        String fileName = "D:\\Programy\\Wspolbieżne\\Server\\UsersFiles\\" + name;

        Path path = Paths.get(fileName);

        if (!Files.exists(path)) {

            Files.createDirectory(path);
            System.out.println("Directory created");
        } else {
            System.out.println("Directory already exists");
        }
    }

    public String login(Connection connection, String name, String password) throws SQLException, IOException {

        PreparedStatement ps;
        ResultSet rs;

        String query = "SELECT * FROM appUser WHERE login =? AND password =?";

        ps = connection.prepareStatement(query);

        ps.setString(1, name);
        ps.setString(2, password);

        rs = ps.executeQuery();

        if(rs.next()) {
            checkUser = true;
            System.out.println("Welcome back " + name);
            createDirectory(name);
            return "Welcome back "+ name;

        }else {
            checkUser = false;
            System.out.println("Incorrect login or password");
            return "Incorrect login or password";
        }


    }

}
