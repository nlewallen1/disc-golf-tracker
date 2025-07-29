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
    private String date;
    private int roundId;
    private int totalStrokes;
    private int totalPar;
    private int finalScore;
    private final int courseId;
    private int[] holeIds;
    private int[] holeResults;
    private int holeCount;
    Scanner input = new Scanner(System.in);


    // constructor
    public NewRound(int courseId) {
        date = "";
        roundId = 0;
        totalStrokes = 0;
        totalPar = 0;
        finalScore = 0;
        this.courseId = courseId;
    }

    public void askRound() {

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


    }

    public void getHoleAmount() {

        // determine amount of holes on course
        holeCount = 0;
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
    }

    public void askResults() {
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
    }

    public void getHoleIds() {
        // get hole id's for the course
        holeIds = new int[holeCount];
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

    }

    public void getHoleResults() {
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

    public void setFinalScore() {
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
        finalScore = totalStrokes - totalPar;
    }

    public void updateFinalScore() {
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

    // create new round method
    public void createNewRound() {
        askRound();
        getHoleAmount();
        askResults();
        getHoleIds();
        getHoleResults();
        setFinalScore();
        updateFinalScore();
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