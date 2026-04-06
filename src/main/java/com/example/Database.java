import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Database {

    private static final String URL = "jdbc:sqlite:hotel.db";

    public static Connection connect() throws Exception {
        return DriverManager.getConnection(URL);
    }

    public static void init() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS rooms (" +
                    "roomNumber INTEGER PRIMARY KEY, " +
                    "type TEXT, " +
                    "price REAL, " +
                    "isBooked INTEGER, " +
                    "customerName TEXT, " +
                    "phone TEXT, " +
                    "checkIn TEXT, " +
                    "checkOut TEXT, " +
                    "totalAmount REAL)";

            stmt.execute(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}