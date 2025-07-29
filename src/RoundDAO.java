import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class RoundDAO {

    // get round info and save round id
    public static int askRound(int courseId) {
        Scanner input = new Scanner(System.in);
        String date;
        int roundId = 0;
        while (true) {
            try {
                System.out.println("Enter date played. (YYYY-MM-DD)");
                date = input.nextLine();
                break;
            } catch (InputMismatchException e) {
                System.out.println("An error occurred while reading the date: " + e.getMessage());
            }
        }
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO rounds (course_id, date) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                stmt.setString(2, date);
                stmt.executeUpdate();

                // save round_id
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        roundId = rs.getInt(1);
                    } else {
                        throw new SQLException("No round ID found");
                    }
                }

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return roundId;


    }
    public static int getFinalScore(int roundId, int courseId) {
        // set final score to the sum of hole_results strokes minus sum of all pars with the proper course_id
        // get total strokes
        int totalStrokes = 0;
        int totalPar = 0;
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT SUM(strokes) FROM hole_results WHERE round_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, roundId);
                ResultSet rs = stmt.executeQuery();
                totalStrokes = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // get total par
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT SUM(par) FROM holes WHERE course_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                ResultSet rs = stmt.executeQuery();
                totalPar = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return totalStrokes - totalPar;
    }

    public static void updateFinalScore(int finalScore, int roundId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE rounds SET final_score = ? WHERE round_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, finalScore);
                stmt.setInt(2, roundId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
