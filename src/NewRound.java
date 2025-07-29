
import java.util.InputMismatchException;
import java.util.Scanner;

// FIXME total throws either need to be added to each time user is asked or calculated
// FIXME display results to user
// add a new round of stats
public class NewRound {
    private int roundId;
    private int finalScore;
    private final int courseId;
    private int[] holeIds;
    private int[] holeResults;
    private int holeCount;


    // constructor
    public NewRound(int courseId) {
        roundId = 0;
        finalScore = 0;
        this.courseId = courseId;
    }


    public void askResults() {
        Scanner input = new Scanner(System.in);
        // loop through each hole, ask for results
        holeResults = new int[holeCount];
        for (int i = 0; i < holeCount; i++) {
            while (true) {
                System.out.println("Enter hole " + (i + 1) + " result");
                // get result
                try {
                    int result = input.nextInt();
                    holeResults[i] = result;
                    break; // good input
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input, please enter an integer.");
                    input.nextLine();
                }
            }
        }
    }


    // create new round method
    public void createNewRound() {
        roundId = RoundDAO.askRound(courseId);
        holeCount = CourseDAO.getHoleAmount(courseId);
        askResults();
        holeIds = HoleDAO.getHoleIds(holeCount, courseId);
        HoleDAO.insertHoleResults(holeCount, holeIds, roundId, holeResults);
        finalScore = RoundDAO.getFinalScore(roundId, courseId);
        RoundDAO.updateFinalScore(finalScore, roundId);
    }

    // display new round
    public void displayResults() {
        System.out.println("Round successfully added!");
        System.out.println("Final results:");
        System.out.println("Score: " + finalScore);
        System.out.println("Round review: ");
        // future more general method in Database class

    }
}
