import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

// Hold information about the course
public class Course {
    Scanner input = new Scanner(System.in);
    private int holeCount;
    private String name;
    private int[] holes;
    private int totalPar;
private int courseId;

    // set holeCount
    public int getHoleCount() {
        return holeCount;
    }

    // get holeCount
    public void setHoleCount(int holeCount) {
        this.holeCount = holeCount;
    }

    // set name
    public void setName(String name) {
        this.name = name;
    }

    // get name
    public String getName() {
        return name;
    }

    // get totalPar
    public int getTotalPar() {
        return totalPar;
    }

    // set par for each hole
    public void setHoles() {
        // initialize array with the amount of holes on the course
        holes = new int[holeCount];

        // get par for each hole
        for (int i = 0; i < holeCount; i++) {
            while (true) {
                System.out.println("Enter par for hole " + (i + 1) + ":");
                try {
                    int par = input.nextInt();
                    holes[i] = par;
                    totalPar += par;
                    break; // good input
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input, please enter an integer");
                    input.nextLine();
                }
            }
        }
    }

    // create new row if adding a course
    public void createCourse(Connection conn) throws SQLException {
        String sql = "INSERT INTO courses (name, hole_count) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, this.name);
            stmt.setInt(2, this.holeCount);
            stmt.executeUpdate();

            // save course_id
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    this.courseId = rs.getInt(1);
                } else {
                    throw new SQLException("Creating course failed, no ID obtained.");
                }
            }
        }
    }

    // create new row adding hole
    public void createHoles(Connection conn) throws SQLException {
        // create proper amount of holes
        for (int i = 1; i <= holeCount; i++) {
            String sql = "INSERT INTO holes (hole_number, par, course_id) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, i);
                stmt.setInt(2, holes[i-1]);
                stmt.setInt(3, courseId);
                stmt.executeUpdate();
            }
        }
    }

    // show each hole, par for it
    public void getHoleResults() {
        for (int i = 0; i < holeCount; i++) {
            System.out.println("Hole " + (i + 1) + " - Par " + holes[i]);
        }
    }

    public int getHolePar(int num) {
        return holes[num];
    }
}
