import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {

    public Connection getConnection(){

        Connection con = null;
        try {
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/dropbox","postgres" , "admin");

            if(con != null) {
                System.out.println("Connection OK");
            } else {
                System.out.println("Connection failed");
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return con;
    }

}