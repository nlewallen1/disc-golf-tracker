import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Stats {
    private int roundsCount;
    private int totalThrows;
    private double averageScore;
    private int aces;
    private int albatrosses;
    private int eagles;
    private int birdies;
    private int pars;
    private int bogeys;
    private int doubleBogeys;
    private int tripleBogeys;
    private int overTripleBogeys;


    // overall stats calc
    public void calcStats() {
        roundsCount = RoundDAO.getNumberOfRounds();
        calcTotalThrows();
        calcResults();
        calcAverageScore();
    }

    // course specific stats calc
    public void calcStatsCourse(int courseId) {
        roundsCount = RoundDAO.getNumberOfRoundsCourse(courseId);
        calcTotalThrowsCourse(courseId);
        calcResultsCourse(courseId);
        calcAverageScoreCourse(courseId);
    }

    // show all stats to user
    public void displayStats() {
        // get rounds played
        // use overall method if course boolean is true, course specific if not
        if (roundsCount != 0) {
            System.out.println("Rounds played: " + roundsCount);
        }
        // if average is negative, - will display
        if (averageScore < 0) {
            // ensure correct average formatting
            if (averageScore % 1 == 0) {
                System.out.println("Average score: " + (int) averageScore);

            } else {
                System.out.println("Average score: " + Math.round((averageScore * 100)) / 100.0);

            }
        }
        // display + if positive
        else {
            // ensure correct average formatting
            if (averageScore % 1 == 0) {
                System.out.println("Average score: +" + (int) averageScore);

            } else {
                System.out.println("Average score: +" + Math.round((averageScore * 100)) / 100.0);

            }
        }
        System.out.println("Total throws: " + totalThrows);
        System.out.println("Aces: " + aces);
        System.out.println("Albatrosses: " + albatrosses);
        System.out.println("Eagles: " + eagles);
        System.out.println("Birdies: " + birdies);
        System.out.println("Pars: " + pars);
        System.out.println("Bogeys: " + bogeys);
        System.out.println("Double Bogeys: " + doubleBogeys);
        System.out.println("Triple Bogeys: " + tripleBogeys);
        System.out.println("Over Triple Bogeys: " + overTripleBogeys);
    }

    // get number of throws
    public void calcTotalThrows() {
        try (Connection conn = Database.getConnection()) {
            // select sum of strokes
            String sql = "SELECT SUM(strokes) FROM hole_results";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                totalThrows = rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    // get strokes and par each time, ++ to relevant statistical category
    public void calcResults() {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT hole_results.strokes, holes.par FROM hole_results INNER JOIN holes ON holes.hole_id = hole_results.hole_id";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                // while there are still results
                while (rs.next()) {
                    int strokes = rs.getInt(1);
                    int par = rs.getInt(2);
                    // par - strokes examples
                    // 3 - 3 = 0, par
                    // 3 - 2 = 1, birdie
                    // 4 - 2 = 2, eagle
                    // 3 - 4 = -1, bogey
                    // 3 - 5 = -2, double bogey
                    // 3 - 6 = -3, triple bogey
                    // 3 - 7 = -4, > 3+ bogey
                    int category = par - strokes;
                    // ace condition
                    if (strokes == 1) {
                        aces++;
                    } else {
                        // 3+ bogey
                        if (category <= -4) {
                            overTripleBogeys++;
                        } else {
                            // defined categories
                            switch (category) {
                                case -3 -> tripleBogeys++;
                                case -2 -> doubleBogeys++;
                                case -1 -> bogeys++;
                                case 0 -> pars++;
                                case 1 -> birdies++;
                                case 2 -> eagles++;
                                case 3 -> albatrosses++;

                            }
                        }

                    }
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void calcAverageScore() {
        try (Connection conn = Database.getConnection()) {
            // get average for all rounds
            String sql = "SELECT AVG(final_score) FROM rounds";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                averageScore = rs.getDouble(1);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching courses: " + e.getMessage());
        }
    }

    // get number of throws for a course
    public void calcTotalThrowsCourse(int courseId) {
        try (Connection conn = Database.getConnection()) {
            // select sum of strokes
            String sql = "SELECT SUM(hole_results.strokes) FROM hole_results INNER JOIN rounds ON rounds.round_id = hole_results.round_id WHERE course_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                ResultSet rs = stmt.executeQuery();
                totalThrows = rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    // get strokes and par each time, ++ to relevant statistical category, course specific
    public void calcResultsCourse(int courseId) {
        try (Connection conn = Database.getConnection()) {

            String sql = "SELECT hole_results.strokes, holes.par FROM hole_results INNER JOIN holes ON holes.hole_id = hole_results.hole_id WHERE holes.course_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                ResultSet rs = stmt.executeQuery();
                // while there are still results
                while (rs.next()) {
                    int strokes = rs.getInt(1);
                    int par = rs.getInt(2);
                    // par - strokes examples
                    // 3 - 3 = 0, par
                    // 3 - 2 = 1, birdie
                    // 4 - 2 = 2, eagle
                    // 3 - 4 = -1, bogey
                    // 3 - 5 = -2, double bogey
                    // 3 - 6 = -3, triple bogey
                    // 3 - 7 = -4, > 3+ bogey
                    int category = par - strokes;
                    // ace condition
                    if (strokes == 1) {
                        aces++;
                    } else {
                        // 3+ bogey
                        if (category <= -4) {
                            overTripleBogeys++;
                        } else {
                            // defined categories
                            switch (category) {
                                case -3 -> tripleBogeys++;
                                case -2 -> doubleBogeys++;
                                case -1 -> bogeys++;
                                case 0 -> pars++;
                                case 1 -> birdies++;
                                case 2 -> eagles++;
                                case 3 -> albatrosses++;

                            }
                        }

                    }
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void calcAverageScoreCourse(int courseId) {
        try (Connection conn = Database.getConnection()) {
            // get average score for course
            String sql = "SELECT AVG(final_score) FROM rounds WHERE course_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                ResultSet rs = stmt.executeQuery();
                averageScore = rs.getDouble(1);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching courses: " + e.getMessage());
        }
    }

    public int askWhichHole(int courseId, int hole_amount, Scanner input) {
        System.out.println("Which hole would you like stats for? Enter the hole number.");

        for (int i = 1; i <= hole_amount; i++) {
            System.out.println("Hole " + i);
        }
        int choice;
        // get hole choice
        while (true) {
            try {
                choice = input.nextInt();
                if (choice < 1 || choice > hole_amount) {
                    System.out.println("Please enter a proper choice.");
                } else {
                    break;
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a proper choice");
                input.next();
            }
        }
        // select correct hole by using hole number on this course
        try (Connection conn = Database.getConnection()) {
            // get hole_id from course_id and hole number
            String sql = "SELECT hole_id FROM holes WHERE course_id = ? AND hole_number = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, courseId);
                stmt.setInt(2, choice);
                ResultSet rs = stmt.executeQuery();
                // return hole_id
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching courses: " + e.getMessage());
        }
        // failed
        return -1;
    }

    public void calcAverageScoreHole(int holeId, List<Integer> holeList) {
        double average = 0;
        int par = HoleDAO.getPar(holeId);

        for (int i = 0; i < holeList.size(); i++) {
            // subtract par from strokes to get result for each hole
            int result = (par - holeList.get(i));
            // add to proper statistical category
            if (holeList.get(i) == 1) {
                aces++;
            } else {
                // 3+ bogey
                if (result <= -4) {
                    overTripleBogeys++;
                } else {
                    // defined categories
                    switch (result) {
                        case -3 -> tripleBogeys++;
                        case -2 -> doubleBogeys++;
                        case -1 -> bogeys++;
                        case 0 -> pars++;
                        case 1 -> birdies++;
                        case 2 -> eagles++;
                        case 3 -> albatrosses++;

                    }
                }

            }
            // subtract all results together to divide by length of list
            average -= result;
        }
        averageScore = average/holeList.size();
    }
}
