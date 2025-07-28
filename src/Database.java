import java.sql.*;

public class Database {
    // connect to db
    static String url = "jdbc:sqlite:disc_golf_tracker.db";
    public static void connectToDatabase() {


        // table creation strings
        String createCoursesTable = "CREATE TABLE IF NOT EXISTS courses (" +
                "course_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT UNIQUE, " +
                "hole_count INTEGER NOT NULL" +
                ");";

        String createHolesTable = "CREATE TABLE IF NOT EXISTS holes (" +
                "hole_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "hole_number INTEGER NOT NULL, " +
                "par INTEGER NOT NULL, " +
                "course_id INTEGER, " +
                "FOREIGN KEY (course_id) REFERENCES courses(course_id)" +
                ");";

        String createRoundsTable = "CREATE TABLE IF NOT EXISTS rounds (" +
                "    round_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "    course_id INTEGER NOT NULL," +
                "    date TEXT NOT NULL," +
                "    final_score INTEGER," +
                "    FOREIGN KEY(course_id) REFERENCES courses(course_id)" +
                ");";

        String createHoleResultsTable = "CREATE TABLE IF NOT EXISTS hole_results (" +
                "    result_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "    hole_id INTEGER NOT NULL, " +
                "    round_id INTEGER NOT NULL, " +
                "    strokes INTEGER, " +
                "    FOREIGN KEY (hole_id) REFERENCES holes(hole_id), " +
                "    FOREIGN KEY (round_id) REFERENCES rounds(round_id) " +
                ");";

        // create courses table if necessary
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createCoursesTable);

        } catch (SQLException e) {
            System.out.println("Error creating courses table: " + e.getMessage());
        }

        // create holes table if necessary
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createHolesTable);

        } catch (SQLException e) {
            System.out.println("Error creating holes table: " + e.getMessage());
        }

        // create rounds table if necessary
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createRoundsTable);

        } catch (SQLException e) {
            System.out.println("Error creating rounds table: " + e.getMessage());
        }

        // create hole_results table if necessary
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createHoleResultsTable);

        } catch (SQLException e) {
            System.out.println("Error creating hole_results table: " + e.getMessage());
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }


}
