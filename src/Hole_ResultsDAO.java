import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;


public class Hole_ResultsDAO {

    public static void editHoleResults() {

        Scanner input = new Scanner(System.in);

        System.out.println("Which round would you like to edit?");

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

        // ask to edit round date
        System.out.println("Would you like to edit the round date?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        int choice;
        // get user decision
        while (true) {
            try {
                choice = input.nextInt();
                if (choice < 1 || choice > 2) {
                    System.out.println("Please enter a proper choice.");
                } else {
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a proper choice.");
                input.next();
            }
        }
        // edit date if yes
        if (choice == 1) {
            RoundDAO.editDate(roundId);
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


        // continue asking to edit
        while (true) {
            System.out.println("Which hole would you like to edit? (Enter hole number or 0 to exit.)");

            int holeChoice;
            while (true) {
                try {
                    holeChoice = input.nextInt();
                    // allow exit
                    if (holeChoice == 0) {
                        return;
                    }
                    // break from loop if hole count is correct
                    else if (holeChoice >= 1 || holeChoice <= holeCount) {
                        break;
                    } else {
                        System.out.println("Please enter an existing hole.");
                    }

                    // break if proper int
                    break;
                } catch (InputMismatchException e) {
                    System.out.println(e);
                    input.next();
                }
            }

            System.out.println("Enter new result.");
            int newResult;
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

            // update final score
            RoundDAO.updateFinalScore(RoundDAO.setFinalScore(roundId, courseId), roundId);
            System.out.println("Edit again?");
            System.out.println("1) Yes");
            System.out.println("2) No");
            while (true) {
                try {
                    choice = input.nextInt();
                    if (choice < 1 || choice > 2) {
                        System.out.println("Please select yes or no.");
                    } else {
                        // 1 or 2 selected, break
                        break;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Please enter 1 or 2");
                    input.next();
                }
            }

            // exit method if edits are done
            if (choice == 2) {
                break;
            }
        }
    }

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
