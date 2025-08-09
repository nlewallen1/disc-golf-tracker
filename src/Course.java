import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
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
    // FIXME maybe move
    public void createCourseData(Connection conn) throws SQLException {
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

    // show each hole, par for it
    public void getHoleResults() {
        for (int i = 0; i < holeCount; i++) {
            System.out.println("Hole " + (i + 1) + " - Par " + holes[i]);
        }
    }

    public int getHolePar(int num) {
        return holes[num];
    }


    public static int askCourseID(Scanner input, List<String> courseNames, String url) throws SQLException {

        String name;
        // get user input
        while (true) {
            try {
                int choice = input.nextInt();
                // get the name of listed course
                name = courseNames.get(choice - 1);
                break;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter an integer");
            }
        }


        // return course id
        String sql = "SELECT course_id FROM courses WHERE name = ?";

        try (Connection conn = Database.getConnection()) {

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();
                return rs.getInt("course_id");
            }

        }

    }

    // create course
    public void createCourse(Scanner input, String url) {

        System.out.println("Enter course name");
        setName(input.nextLine());

        System.out.println("Enter number of holes on course");
        setHoleCount(input.nextInt());

        // ask for par for each hole
         setHoles();

        // create course
        try (Connection conn = Database.getConnection()) {
            createCourseData(conn);
            HoleDAO.createHoles(conn, holeCount, holes, courseId);
            System.out.println("New course added!");
        } catch (SQLException e) {
            System.out.println("Error adding course: " + e.getMessage());
        }


        // TEMP DISPLAY COURSE
        System.out.println(name + " (Par " + totalPar + ")");
        getHoleResults();
    }

    // begin adding round
    // displays all courses to the user, then returns names with list to use find in database
    public static List<String> listCourses() {
        // array list needed to keep track of names on the list
        List<String> courseNames = new ArrayList<>();

        try (Connection conn = Database.getConnection()) {
            // select all courses
            String sql = "SELECT name FROM courses";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();

                // keep of how many courses being displayed
                int i = 1;
                // loop through result set, list all courses
                while (rs.next()) {
                    String name = rs.getString("name");
                    System.out.println(i++ + ") " + name);
                    courseNames.add(name);
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // return list
        return courseNames;
    }




}
