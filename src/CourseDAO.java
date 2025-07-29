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
}
