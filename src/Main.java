import java.sql.*;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // connect to db
        String url = "jdbc:sqlite:disc_golf_tracker.db";
        Database.connectToDatabase();

        Scanner input = new Scanner(System.in);
        boolean run = true;
        // main loop
        while (run) {
            int choice = 0;
            while (true) {
                try {
                    System.out.println("1. Add course");
                    System.out.println("2. Add round");
                    System.out.println("3. View round");
                    System.out.println("4. Edit round");
                    System.out.println("5. Delete course");
                    System.out.println("6. Delete round");
                    System.out.println("7. Overall stats");
                    System.out.println("8. Course specific stats");
                    System.out.println("9. Exit");

                    choice = input.nextInt();
                    input.nextLine();

                    // break if valid
                    if (choice > 0 && choice < 10) {
                        break;
                    } else {
                        System.out.println("Please enter a proper choice.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a proper choice.");
                    input.next();
                }
            }
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

                    // delete course, course related
                    Hole_ResultsDAO.deleteResultsCourse(courseId);
                    HoleDAO.deleteHoles(courseId);
                    CourseDAO.deleteCourse(courseId);
                    RoundDAO.deleteAllRounds(courseId);
                    break;
                }
                // delete round
                case 6: {
                    System.out.println("Which round would you like to delete?");
                    List<String> dates = RoundDAO.showDates();

                    // get round id
                    int roundId = 0;
                    try {
                        roundId = RoundDAO.askRoundID(input, dates);
                    } catch (SQLException e) {
                        System.out.println("Failed to get round_id" + e.getMessage());
                    }

                    // delete round, round related
                    Hole_ResultsDAO.deleteResults(roundId);
                    RoundDAO.deleteRound(roundId);
                    break;
                }
                // get overall stats
                case 7: {
                    // stats object
                    Stats stats = new Stats();
                    stats.calcStats();

                    stats.displayStats();
                    break;
                }
                // course specific stats
                case 8: {
                    // ask user for input and get course names
                    System.out.println("What course would you like stats for?");
                    List<String> courseNames = Course.listCourses();

                    // get course id
                    int courseId = 0;
                    try {
                        courseId = Course.askCourseID(input, courseNames, url);
                    } catch (SQLException e) {
                        System.out.println("Failed to get course_id" + e.getMessage());
                    }

                    // once courseId is retrieved, create new Stats object. functions similarly, just requires id
                    Stats courseStats = new Stats();
                    courseStats.calcStatsCourse(courseId);
                    courseStats.displayStats();

                    System.out.println("Would you like to view individual hole stats?");
                    System.out.println("1. Yes\n2. No");
                    // ask if user wants to see hole specific stats
                    while (true) {
                        try {
                            choice = input.nextInt();
                            if (choice < 1 || choice > 2) {
                                System.out.println("Please enter a proper choice.");
                            } else {
                                break;
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("Please enter a proper choice");
                            input.next();
                        }
                    }

                    // user wants hole specific stats
                    if (choice == 1) {
                        // new Stats object
                        Stats holeStats = new Stats();
                        // get holeId
                        int holeId = holeStats.askWhichHole(courseId, CourseDAO.getHoleAmount(courseId), input);
                        // get results list for that hole
                        List<Integer> holeList = Hole_ResultsDAO.getResultsForHole(holeId);
                        holeStats.calcAverageScoreHole(holeId, holeList);
                        holeStats.displayStats();
                    }
                    break;

                }
                case 9: {
                    run = false;
                }
            }
        }
    }


}