import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class RoundDAO {

    // check if rounds have been added
    // returns true if rounds exist, false if not
    public static boolean checkForEntries() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT COUNT(round_id) FROM rounds";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {

                ResultSet rs = stmt.executeQuery();
                if (rs.getInt(1) == 0) {
                    // rounds don't exist yet
                    return false;
                }

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // rounds exist
        return true;
    }

    // date checker for validation
    public static boolean dateChecker(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate.parse(date, formatter);
            // valid
            return true;
        } catch (DateTimeParseException e) {
            // invalid
            return false;
        }
    }

    // get round info and save round id
    public static int askRound(int courseId) {
        Scanner input = new Scanner(System.in);
        String date;
        int roundId = 0;
        System.out.println("Enter date played. (YYYY-MM-DD)");
        while (true) {
            try {
                date = input.nextLine();
                boolean dateCheck = dateChecker(date);
                // break if date is good
                if (dateCheck) {
                    break;
                }
                System.out.println("Please enter a valid date.");
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
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

    // obtain final score by getting strokes, par
    public static int setFinalScore(int roundId, int courseId) {
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

    // change a round's final score
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

    // get list of rounds dates
    public static List<String> showDates() {
        // array list needed to keep track of dates on the list
        List<String> dates = new ArrayList<>();

        try (Connection conn = Database.getConnection()) {
            // select all dates
            String sql = "SELECT rounds.date, courses.name FROM rounds INNER JOIN courses ON courses.course_id = rounds.course_id";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();

                // keep of how many dates being displayed
                int i = 1;
                // loop through result set, list all dates
                while (rs.next()) {
                    String date = rs.getString("date");
                    String course = rs.getString("name");
                    System.out.println(i++ + ". " + date + " (" + course + ")");
                    dates.add(date);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching dates: " + e.getMessage());
        }

        // return list
        return dates;
    }

    // obtain round id
    public static int askRoundID(Scanner input, List<String> dates) throws SQLException {

        String date;
        // get user input
        while (true) {
            try {
                int choice = input.nextInt();
                if (choice < 1 || choice > dates.size()) {
                    System.out.println("Please enter a proper choice.");
                } else {
                    // get the round
                    date = dates.get(choice - 1);
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer.");
                input.next();
            }
        }


        // return course id
        String sql = "SELECT round_id FROM rounds WHERE date = ?";

        try (Connection conn = Database.getConnection()) {

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, date);
                ResultSet rs = stmt.executeQuery();
                return rs.getInt("round_id");
            }

        }

    }

    // display course, hole number, hole par, strokes
    // need to use roundId to get the right results, then simply display in ascending order, but also need to match to
    // hole_id in holes to get the par
    public static void displayResults(int roundId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT course_id FROM rounds WHERE round_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, roundId);
                ResultSet rs = stmt.executeQuery();
                int courseId = rs.getInt(1);
                System.out.println("Course: " + CourseDAO.getName(courseId));
            }

            // select final score, results for each hole
            sql = "SELECT holes.hole_number, holes.par, hole_results.strokes FROM hole_results " +
                    "INNER JOIN holes ON holes.hole_id = hole_results.hole_id WHERE hole_results.round_id = ?" +
                    "ORDER BY hole_results.hole_id ASC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, roundId);
                ResultSet rs = stmt.executeQuery();

                // loop through result set, list all dates
                while (rs.next()) {
                    int holeNumber = rs.getInt("hole_number");
                    int par = rs.getInt("par");
                    int strokes = rs.getInt("strokes");

                    // single space
                    if (holeNumber < 10) {
                        // displays as under/at par
                        if (strokes - par <= 0) {
                            System.out.printf("Hole %d: Par %d, Strokes %d (%d)%n", holeNumber, par, strokes, strokes - par);
                        }
                        // displays as over par
                        else {
                            System.out.printf("Hole %d: Par %d, Strokes %d (+%d)%n", holeNumber, par, strokes, strokes - par);
                        }
                    }
                    // double space
                    else {
                        // displays as under/at par
                        if (strokes - par <= 0) {
                            System.out.printf("Hole %2d: Par %d, Strokes %d (%d)%n", holeNumber, par, strokes, strokes - par);
                        }
                        // displays as over par
                        else {
                            System.out.printf("Hole %2d: Par %d, Strokes %d (+%d)%n", holeNumber, par, strokes, strokes - par);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching dates: " + e.getMessage());
        }

        // display final score
        int finalScore = RoundDAO.getFinalScore(roundId);
        // if + final score
        if (finalScore > 0) {
            System.out.println("Final score: +" + finalScore);
        }
        // - or 0 final score
        else {
            System.out.println("Final score: " + finalScore);
        }
    }

    // return final score from a round
    public static int getFinalScore(int roundId) {
        try (Connection conn = Database.getConnection()) {
            // select final score, results for each hole
            String sql = "SELECT final_score FROM rounds WHERE round_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, roundId);
                ResultSet rs = stmt.executeQuery();
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching dates: " + e.getMessage());
        }
        // failed
        return 0;
    }

    // delete a round from round id
    public static void deleteRound(int roundId) {
        try (Connection conn = Database.getConnection()) {
            // delete round from roundId
            String sql = "DELETE FROM rounds WHERE round_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, roundId);
                stmt.executeUpdate();
                System.out.println("Round deleted!");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // delete all rounds on a course
    public static void deleteAllRounds(int courseId) {
        try (Connection conn = Database.getConnection()) {
            // delete all rounds matching the courseId
            String sql = "DELETE FROM rounds WHERE course_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                stmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // get rounds played
    public static int getNumberOfRounds() {
        try (Connection conn = Database.getConnection()) {
            // select number of rounds
            String sql = "SELECT COUNT(round_id) FROM rounds";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // failed
        return -1;
    }

    // get rounds played on one course
    public static int getNumberOfRoundsCourse(int courseId) {
        try (Connection conn = Database.getConnection()) {
            // select number of rounds
            String sql = "SELECT COUNT(round_id) FROM rounds WHERE course_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                ResultSet rs = stmt.executeQuery();
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // failed
        return -1;
    }

    // edit a round's date
    public static void editDate(int round_id) {
        Scanner input = new Scanner(System.in);

        // date prompt
        String date;
        System.out.println("Enter new date played. (YYYY-MM-DD)");
        while (true) {
            try {
                date = input.nextLine();
                boolean dateCheck = dateChecker(date);
                // break if date is good
                if (dateCheck) {
                    break;
                }
                System.out.println("Please enter a valid date.");
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
            }
        }

        try (Connection conn = Database.getConnection()) {
            // edit date
            String sql = "UPDATE rounds SET date = ? WHERE round_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, date);
                stmt.setInt(2, round_id);
                stmt.executeUpdate();
                System.out.println("Date updated!");
                System.out.println("Round results:");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
