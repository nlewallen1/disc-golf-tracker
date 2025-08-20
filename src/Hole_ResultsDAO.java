import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    // return list of strokes for all results of one hole
    public static List<Integer> getResultsForHole(int hole_id) {
        // array list to store strokes for each result
        List<Integer> results = new ArrayList<>();

        try (Connection conn = Database.getConnection()) {
            // get all strokes for this hole
            String sql = "SELECT strokes FROM hole_results WHERE hole_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, hole_id);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    results.add(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching courses: " + e.getMessage());
        }
        return results;
    }
}
