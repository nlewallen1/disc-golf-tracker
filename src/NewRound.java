import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

// FIXME total throws either need to be added to each time user is asked or calculated
// FIXME seperate some parts (like get total par) into seperate methods
// FIXME display results to user
// add a new round of stats
public class NewRound {

    public static void askResults(int courseId) {
        Scanner input = new Scanner(System.in);
        String date;
        int roundId = 0;
        int totalStrokes = 0;
        int totalPar = 0;

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

        int[] holeResults;
        // determine amount of holes on course
        int holeCount = 0;
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT hole_count FROM courses WHERE course_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                ResultSet rs = stmt.executeQuery();
                holeCount = rs.getInt("hole_count");


            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // loop through each hole, ask for results
        holeResults = new int[holeCount];
        for (int i = 0; i < holeCount; i++) {
            while (true) {
                System.out.println("Enter hole " + (i + 1) + " result");
                // get result
                try {
                    int result = input.nextInt();
                    holeResults[i] = result;
                    break; // good input
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input, please enter an integer.");
                    input.nextLine();
                }
            }
        }

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


        try (Connection conn = Database.getConnection()) {
            // insert into hole_results
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

        // set final score to the sum of hole_results strokes minus sum of all pars with the proper course_id
        // get total strokes
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

        int finalScore = totalStrokes - totalPar;

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


//    public void displayResults() {
//        //fixme add + for positive
//        System.out.println("Final results:" + (totalThrows - course.getTotalPar()));
//        System.out.println("Course: " + course.getName());
//        System.out.println("Hole results:");
//        for (int i = 0; i < course.getHoleCount(); i++) {
//            System.out.println("Hole " + (i+1) +" (Par " + course.getHolePar(i) + "): " + holeResults[i]);
//        }
//    }