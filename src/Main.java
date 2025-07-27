import java.sql.Statement;
import java.util.Scanner;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// notes
// maybe get rid of all those conn creations

public class Main {
    public static void main(String[] args) {

        // connect to db
        String url = "jdbc:sqlite:disc_golf_tracker.db";

        // table creation strings
        String createCoursesTable = "CREATE TABLE IF NOT EXISTS courses (" +
                "    course_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    name TEXT UNIQUE," +
                "    hole_count INTEGER NOT NULL" +
                ");";
        String createHolesTable = "CREATE TABLE IF NOT EXISTS holes (" +
                "    hole_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    hole_number INTEGER NOT NULL," +
                "    par INTEGER NOT NULL," +
                "    course_id INTEGER," +
                "    FOREIGN KEY (course_id) REFERENCES courses(course_id)" +
                ");";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                System.out.println("Connected to SQLite database.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // create courses table if necessary
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.execute(createCoursesTable);

        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }

        // create holes table if necessary
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {

            stmt.execute(createHolesTable);

        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }


        Scanner input = new Scanner(System.in);

        System.out.println("1. Add course");
        System.out.println("2. Add round");

        int choice = input.nextInt();
        input.nextLine();

        // menu
        switch (choice) {
            case 1:
                // create new course class
                Course newcourse = new Course();

                System.out.println("Enter course name");
                newcourse.setName(input.nextLine());

                System.out.println("Enter number of holes on course");
                newcourse.setHoleCount(input.nextInt());

                // ask for par for each hole
                newcourse.setHoles();

                // create course
                try (Connection conn = DriverManager.getConnection(url)) {
                    newcourse.createCourse(conn);
                    newcourse.createHoles(conn);
                    System.out.println("New course added!");
                } catch (SQLException e) {
                    System.out.println("Error adding course: " + e.getMessage());
                }


                // TEMP DISPLAY COURSE
                System.out.println(newcourse.getName() + " (Par " + newcourse.getTotalPar() + ")");
                newcourse.getHoleResults();
                break;
            case 2:
                System.out.println("What course did you play?");
                // FIXME let user choose from list of added courses
                // NewRound newround = NewRound()
        }
    }
}