import java.sql.*;
import java.util.List;
import java.util.Scanner;

// notes
// maybe get rid of all those conn creations

public class Main {
    public static void main(String[] args) {

        // connect to db
        String url = "jdbc:sqlite:disc_golf_tracker.db";
        Database.connectToDatabase();

        Scanner input = new Scanner(System.in);

        System.out.println("1. Add course");
        System.out.println("2. Add round");

        int choice = input.nextInt();
        input.nextLine();

        // menu
        switch (choice) {
            // call createCourseData
            case 1:
                Course.createCourse(input, url);
                break;

            // add a round
            case 2:
                // ask user for input and get course names
                List<String> courseNames = Course.addRound(input, url);

                // get course id
                int courseId = 0;
                try {
                    courseId = Course.getCourseID(input, courseNames, url);
                } catch (SQLException e) {
                    System.out.println("Failed to get course_id" + e.getMessage());
                }
                System.out.println(courseId);

                // get user input
                NewRound newRound = new NewRound(courseId);
                newRound.createNewRound();
        }
    }


}