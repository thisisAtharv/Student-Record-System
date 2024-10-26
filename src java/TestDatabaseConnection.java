import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:student_details.db";

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(DB_URL)) {
            if (connection != null) {
                System.out.println("Connection to the database has been established.");
            } else {
                System.out.println("Failed to establish a connection.");
            }
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }
}
