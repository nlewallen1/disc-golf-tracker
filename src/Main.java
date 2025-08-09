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

        //FIXME input validation when menu is complete
        System.out.println("1. Add course");
        System.out.println("2. Add round");
        System.out.println("3. View round");
        System.out.println("4. Edit round");
        System.out.println("5. Delete course");

        int choice = input.nextInt();
        input.nextLine();

        // menu
        switch (choice) {
            // call createCourseData
            case 1: {
                Course newcourse = new Course();
                newcourse.createCourse(input, url);
                break;
            }
            // add a round
            case 2: {
                // ask user for input and get course names
                System.out.println("What course did you play?");
                List<String> courseNames = Course.listCourses();

                // get course id
                int courseId = 0;
                try {
                    courseId = Course.askCourseID(input, courseNames, url);
                } catch (SQLException e) {
                    System.out.println("Failed to get course_id" + e.getMessage());
                }

                // get user input
                NewRound newRound = new NewRound(courseId);
                newRound.createNewRound();
                newRound.displayResults();
                break;
            }
            // view round
            case 3: {
                // ask user for input and get dates
                List<String> dates = RoundDAO.showDates();

                // get round id
                int roundId = 0;
                try {
                    roundId = RoundDAO.askRoundID(input, dates);
                } catch (SQLException e) {
                    System.out.println("Failed to get round_id" + e.getMessage());
                }
                RoundDAO.displayResults(roundId);
                break;
            }
            // edit results
            case 4: {
                HoleDAO.editHoleResults();
                break;
            }
            // delete course
            case 5: {
                // get course list
                List<String> courseNames = Course.listCourses();
                System.out.println("Which course would you like to delete? " +
                        "(All rounds and stats at this course will be deleted.)");
                // get course id
                int courseId = 0;
                try {
                    courseId = Course.askCourseID(input, courseNames, url);
                } catch (SQLException e) {
                    System.out.println("Failed to get course_id" + e.getMessage());
                }

                HoleDAO.deleteHoles(courseId);
                CourseDAO.deleteCourse(courseId);
                RoundDAO.deleteAllRounds(courseId);
            }
        }
    }


}