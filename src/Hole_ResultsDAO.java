import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Hole_ResultsDAO {
    public static void deleteResults(int roundId) {
        try (Connection conn = Database.getConnection()) {
            // delete results from round
            String sql = "DELETE FROM hole_results WHERE round_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, roundId);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void deleteResultsCourse(int courseId) {
        try (Connection conn = Database.getConnection()) {
            // delete results from the course
            String sql = "DELETE FROM hole_results WHERE round_id IN (SELECT round_id FROM rounds WHERE course_id = ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
