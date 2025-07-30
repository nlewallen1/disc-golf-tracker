import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HoleDAO {

    // create new row adding hole
    public static void createHoles(Connection conn, int holeCount, int[] holes, int courseId) throws SQLException {
        // create proper amount of holes
        for (int i = 1; i <= holeCount; i++) {
            String sql = "INSERT INTO holes (hole_number, par, course_id) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, i);
                stmt.setInt(2, holes[i - 1]);
                stmt.setInt(3, courseId);
                stmt.executeUpdate();
            }
        }
    }

    public static int[] getHoleIds(int holeCount, int courseId) {
        // get hole id's for the course
        int[] holeIds = new int[holeCount];
        try (Connection conn = Database.getConnection()) {
            // select hole_id
            String sql = "SELECT holes.hole_id FROM holes INNER JOIN courses ON courses.course_id = holes.course_id WHERE courses.course_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                ResultSet rs = stmt.executeQuery();
                int i = 0;
                while (rs.next()) {
                    holeIds[i++] = rs.getInt("hole_id");

                }

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return holeIds;

    }
    public static void insertHoleResults(int holeCount, int[] holeIds, int roundId, int[] holeResults) {
        // insert into hole_results
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO hole_results (hole_id, round_id, strokes) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < holeCount; i++) {
                    stmt.setInt(1, holeIds[i]);
                    stmt.setInt(2, roundId);
                    stmt.setInt(3, holeResults[i]);
                    stmt.executeUpdate();
                }

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
