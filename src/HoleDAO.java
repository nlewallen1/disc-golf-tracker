import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

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

    public static void editHoleResults() {
        Scanner input = new Scanner(System.in);

        // need to display rounds for editing
        // ask user for input and get dates
        List<String> dates = RoundDAO.showDates();

        // get round id
        int roundId = 0;
        try {
            roundId = RoundDAO.askRoundID(input, dates);
        } catch (SQLException e) {
            System.out.println("Failed to get round_id" + e.getMessage());
        }
        RoundDAO.displayResults(roundId);

        // get hole amount for input validation
        int holeCount = 0;
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT courses.hole_count FROM courses INNER JOIN rounds ON rounds.course_id = courses.course_id WHERE round_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, roundId);
                ResultSet rs = stmt.executeQuery();
                holeCount = rs.getInt(1);

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Which hole would you like to edit?");

        int holeChoice = 0;
        while (true) {
            try {
                while (true) {
                    holeChoice = input.nextInt();
                    // break from loop if hole count is correct
                    if (holeChoice >= 1 || holeChoice <= holeCount) {
                        break;
                    } else {
                        System.out.println("Please enter an existing hole.");
                    }
                }
                // break if proper int
                break;
            } catch (InputMismatchException e) {
                System.out.println(e);
            }
        }

        System.out.println("Enter new result.");
        int newResult = 0;
        while (true) {
            try {
                while (true) {
                    newResult = input.nextInt();
                    // break from loop if score is valid
                    if (newResult > 0) {
                        break;
                    } else {
                        System.out.println("Please enter a valid score.");
                    }
                }
                // break if proper int
                break;
            } catch (InputMismatchException e) {
                System.out.println(e);
            }
        }

        // get courseId
        int courseId = CourseDAO.getCourseId(roundId);
        try (Connection conn = Database.getConnection()) {
            // update strokes where hole number and round_id match
            String sql = "UPDATE hole_results SET strokes = ? WHERE hole_results.hole_id = (SELECT hole_id FROM holes WHERE hole_number = ? AND course_id = ?) AND hole_results.round_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, newResult);
                stmt.setInt(2, holeChoice);
                stmt.setInt(3, courseId);
                stmt.setInt(4, roundId);

                stmt.executeUpdate();
                System.out.println("Hole updated!");

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // FIXME final score must reflect change
    }


}
