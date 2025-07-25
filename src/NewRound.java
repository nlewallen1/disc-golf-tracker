import java.util.InputMismatchException;
import java.util.Scanner;

// add a new round of stats
public class NewRound {
    Scanner input = new Scanner(System.in);
    private Course course;
    private int totalThrows;
    int[] holeResults;

    public NewRound (Course course) {
        this.course = course;
    }

    // get results
    public void askResults() {
        int holeCount = course.getHoleCount();
        // array to hold results for each hole
        holeResults = new int[holeCount];
        for (int i = 0; i < holeCount; i++) {
            while (true) {
                System.out.println("Enter hole " + (i + 1) + " result");
                // get result
                try {
                    int result = input.nextInt();
                    holeResults[i] = result;
                    totalThrows += result;
                    break; // good input
                }
                catch (InputMismatchException e) {
                    System.out.println("Invalid input, please enter an integer.");
                    input.nextLine();
                }
            }
        }
    }

    public void displayResults() {
        //fixme add + for positive
        System.out.println("Final results:" + (totalThrows - course.getTotalPar()));
        System.out.println("Course: " + course.getName());
        System.out.println("Hole results:");
        for (int i = 0; i < course.getHoleCount(); i++) {
            System.out.println("Hole " + (i+1) +" (Par " + course.getHolePar(i) + "): " + holeResults[i]);
        }
    }
}
