import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseDAO {
    public static int getHoleAmount(int courseId) {

        // determine amount of holes on course
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT hole_count FROM courses WHERE course_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                ResultSet rs = stmt.executeQuery();
                return rs.getInt("hole_count");


            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // failed
        return 0;
    }

    public static String getName(int courseId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT name FROM courses WHERE course_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                ResultSet rs = stmt.executeQuery();
                return rs.getString(1);


            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return "failed";
    }

    // get course_id from round_id
    public static int getCourseId(int roundId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT courses.course_id FROM courses INNER JOIN rounds ON rounds.course_id = courses.course_id WHERE round_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, roundId);
                ResultSet rs = stmt.executeQuery();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // failed
        return -1;
    }
}
