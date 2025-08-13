import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Stats {
    // get number of throws
    public static int getTotalThrows() {
        try (Connection conn = Database.getConnection()) {
            // select sum of strokes
            String sql = "SELECT SUM(strokes) FROM hole_results";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs  = stmt.executeQuery();
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // failed
        return -1;
    }
}
