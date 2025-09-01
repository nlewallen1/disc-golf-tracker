import java.sql.*;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // connect to db
        Database.connectToDatabase();

        Scanner input = new Scanner(System.in);
        boolean run = true;
        // main loop
        while (run) {
            int choice;
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
                    newcourse.createCourse(input);
                    break;
                }
                // add a round
                case 2: {
                    // check if any courses exist
                    boolean coursesExist = CourseDAO.checkForEntries();
                    // break if courses have not been added
                    if (!coursesExist) {
                        System.out.println("Please create a course first.");
                        break;
                    }
                    // ask user for input and get course names
                    System.out.println("What course did you play?");

                    List<String> courseNames = Course.listCourses();
                    System.out.println("0. Exit");


                    // get course id
                    int courseId = 0;
                    try {
                        courseId = Course.askCourseID(input, courseNames);
                    } catch (SQLException e) {
                        System.out.println("Failed to get course_id" + e.getMessage());
                    }

                    // if courseId is still 0, exit
                    if (courseId == 0) {
                        break;
                    }

                    // get user input
                    NewRound newRound = new NewRound(courseId);
                    newRound.createNewRound();
                    newRound.displayResults();
                    break;
                }
                // view round
                case 3: {
                    // check if any rounds exist
                    boolean roundsExist = RoundDAO.checkForEntries();
                    // break if courses have not been added
                    if (!roundsExist) {
                        System.out.println("Please add a round first.");
                        break;
                    }

                    System.out.println("Which round would you like to view?");
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

                    // wait for user to go back to main menu
                    System.out.println("\nPress 0 to return.");
                    int eChoice;
                    while (true) {
                        try {
                            eChoice = input.nextInt();
                            // break loop if yes
                            if (eChoice == 0) {
                                break;
                            } else {
                                System.out.println("Please enter a proper choice.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println(e);
                            input.next();
                        }
                    }

                    break;
                }
                // edit results
                case 4: {
                    // check if any rounds exist
                    boolean roundsExist = RoundDAO.checkForEntries();
                    // break if courses have not been added
                    if (!roundsExist) {
                        System.out.println("Please add a round first.");
                        break;
                    }

                    Hole_ResultsDAO.editHoleResults();
                    break;
                }
                // delete course
                case 5: {
                    // check if any courses exist
                    boolean coursesExist = CourseDAO.checkForEntries();
                    // break if courses have not been added
                    if (!coursesExist) {
                        System.out.println("Please create a course first.");
                        break;
                    }

                    System.out.println("All rounds and stats for the chosen course will be deleted. " +
                            "Are you sure you want to continue?");
                    System.out.println("1. Yes");
                    System.out.println("2. No");

                    int eChoice;
                    boolean cont = true;

                    while (true) {
                        try {
                            eChoice = input.nextInt();
                            // break loop if yes
                            if (eChoice == 1) {
                                break;
                            }
                            // return if no
                            else if (eChoice == 2) {
                                cont = false;
                                break;
                            } else {
                                System.out.println("Please enter a proper choice.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println(e);
                            input.next();
                        }
                    }

                    // exit case if user chose to not continue
                    if (!cont) {
                        break;
                    }

                    System.out.println("Which course would you like to delete?");

                    // get course list
                    List<String> courseNames = Course.listCourses();

                    // get course id
                    int courseId = 0;

                    try {
                        courseId = Course.askCourseID(input, courseNames);
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
                    // check if any rounds exist
                    boolean roundsExist = RoundDAO.checkForEntries();
                    // break if courses have not been added
                    if (!roundsExist) {
                        System.out.println("Please add a round first.");
                        break;
                    }

                    System.out.println("Round deletion will remove stats related to the chosen round. " +
                            "Are you sure you want to continue?");
                    System.out.println("1. Yes");
                    System.out.println("2. No");

                    int eChoice;
                    boolean cont = true;

                    while (true) {
                        try {
                            eChoice = input.nextInt();
                            // break loop if yes
                            if (eChoice == 1) {
                                break;
                            }
                            // return if no
                            else if (eChoice == 2) {
                                cont = false;
                                break;
                            } else {
                                System.out.println("Please enter a proper choice.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println(e);
                            input.next();
                        }
                    }

                    // exit case if user chose to not continue
                    if (!cont) {
                        break;
                    }

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

                    // wait for user to go back to main menu
                    System.out.println("\nPress 0 to return.");
                    int eChoice;
                    while (true) {
                        try {
                            eChoice = input.nextInt();
                            // break loop if yes
                            if (eChoice == 0) {
                                break;
                            } else {
                                System.out.println("Please enter a proper choice.");
                            }
                        } catch (InputMismatchException e) {
                            System.out.println(e);
                            input.next();
                        }
                    }

                    break;
                }
                // course specific stats
                case 8: {
                    // check if any courses exist
                    boolean coursesExist = CourseDAO.checkForEntries();
                    // break if courses have not been added
                    if (!coursesExist) {
                        System.out.println("Please create a course first.");
                        break;
                    }

                    // ask user for input and get course names
                    System.out.println("What course would you like stats for?");
                    List<String> courseNames = Course.listCourses();

                    // get course id
                    int courseId = 0;
                    try {
                        courseId = Course.askCourseID(input, courseNames);
                    } catch (SQLException e) {
                        System.out.println("Failed to get course_id" + e.getMessage());
                    }

                    // once courseId is retrieved, create new Stats object. functions similarly, just requires id
                    Stats courseStats = new Stats();
                    courseStats.calcStatsCourse(courseId);
                    courseStats.displayStats();

                    System.out.println("\nWould you like to view individual hole stats?");
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
                    } else {
                        // wait for user to go back to main menu
                        System.out.println("\nPress 0 to return.");
                        int eChoice;
                        while (true) {
                            try {
                                eChoice = input.nextInt();
                                // break loop if yes
                                if (eChoice == 0) {
                                    break;
                                } else {
                                    System.out.println("Please enter a proper choice.");
                                }
                            } catch (InputMismatchException e) {
                                System.out.println(e);
                                input.next();
                            }
                        }
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